package com.killmonster.screens;

import com.killmonster.character.Player;
import com.killmonster.character.Character;
import com.killmonster.event.*;
import com.killmonster.*;
import com.killmonster.map.*;
import com.killmonster.system.*;
import com.killmonster.system.ui.*;
import com.killmonster.ui.*;
import com.killmonster.util.*;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;

public class MainGameScreen extends AbstractScreen implements GameWorldManager {
	
	private final PooledEngine engine;
	private final GameEventManager gameEventManager;
	
	private final DamageIndicator damageIndicator;
	private final NotificationArea notificationArea;
	private final HUD hud;
	
	private final AssetManager assets;
	private final TmxMapLoader mapLoader;
	
	private World world;
	private GameMap currentMap;
	
	private Player player;
	private Array<Character> enemies;
	
	private PauseOverlay pauseOverlay;
	private LevelCompletedOverlay levelCompletedOverlay;
	private ShapeRenderer shapeRenderer;
	
	public static boolean isNextLevel;
	public static int currentLevel = 0;
	private String gameMapFile;
	
	public MainGameScreen(GameStateManager gsm) {
		super(gsm);
		assets = gsm.getAssets();
		
		// Since we will be rendering TiledMaps, we should scale the viewport with PPM.
		getViewport().setWorldSize(Constants.V_WIDTH / Constants.PPM, Constants.V_HEIGHT / Constants.PPM);
		
		// Initialize the GameEventManager
		gameEventManager = GameEventManager.getINSTANCE();
		
		// Initialize the world, and register the world contact listener.
		world = new World(new Vector2(0, Constants.GRAVITY), true);
		world.setContactListener(new WorldContactListener());

		// Initialize HUD.
		hud = new HUD(gsm);
		damageIndicator = new DamageIndicator(getBatch(), gsm.getFont().getDefaultFont(), getCamera(), 1.5f);
		notificationArea = new NotificationArea(gsm, 6, 4f);
		
		// Initialize PooledEngine and Systems
		engine = new PooledEngine();
		engine.addSystem(new TiledMapRendererSystem((OrthographicCamera) getCamera()));
		engine.addSystem(new CharacterRendererSystem(getBatch(), getCamera(), world));
		engine.addSystem(new CharacterAISystem());
		engine.addSystem(new PlayerControlSystem(engine));
		engine.addSystem(new Box2DDebugRendererSystem(world, getCamera()));
		engine.addSystem(new DamageIndicatorSystem(getBatch(), damageIndicator));
		engine.addSystem(new NotificationSystem(getBatch(), notificationArea));
		engine.addSystem(new HUDSystem(getBatch(), hud));
		engine.addSystem(new ScreenFadeSystem(this));
		
		// Load the map and spawn player.
		mapLoader = new TmxMapLoader();
		gameMapFile = "res/level" + currentLevel + ".tmx";
		setGameMap(gameMapFile);
		player = currentMap.spawnPlayer();
		engine.addEntity(player);
		
		// Initialize OverlayScreen
		pauseOverlay = new PauseOverlay(gsm);
		levelCompletedOverlay = new LevelCompletedOverlay(gsm);
		shapeRenderer = new ShapeRenderer();
		hud.setPlayer(player);
		
	}


	public void handleInput(float delta) {
		if(isAllEnemiesKilled()) {
			Constants.COMPLETED = true;
			levelCompletedOverlay.handleInput();
			return;
		}
		if (Constants.PAUSE) {
			pauseOverlay.handleInput();
			return;
		}
	}
    
	public boolean isAllEnemiesKilled() {
		for (Character character : enemies)
			if (!character.getStateIsKilled())
				return false;
		return true;
	}

	public void update(float delta) {
		if (isNextLevel) {
			if (currentLevel < 2) currentLevel++;
			gameMapFile = "res/level" + currentLevel + ".tmx";
			setGameMap(gameMapFile);
			
			// set ScreenFadeSystem again when next map
			engine.addSystem(new ScreenFadeSystem(this));
			
			world.destroyBody(player.getBody());
			player = currentMap.spawnPlayer();
			
			isNextLevel = false;
		}
		
		handleInput(delta);
		if (!Constants.COMPLETED && !Constants.PAUSE) {
			world.step(1/60f, 6, 2);
			
			if (CameraShake.getShakeTimeLeft() > 0){
				CameraShake.update(Gdx.graphics.getDeltaTime());
				getCamera().translate(CameraShake.getPos());
			} else {
				CameraUtils.lerpToTarget(getCamera(), player.getBody().getPosition());
			}
			
			// Make sure to bound the camera within the TiledMap.
			CameraUtils.boundCamera(getCamera(), getCurrentMap());
		}
	}

	@Override
	public void render(float delta) {
		update(delta);
		gsm.clearScreen();
		engine.update(delta);
		if (Constants.COMPLETED) 
			levelCompletedOverlay.draw();
		
		if (Constants.PAUSE) 
			pauseOverlay.draw();
	}

	@Override
	public void resize(int width, int height) {
		super.resize(width, height);
		
		int viewportX = getViewport().getScreenX();
		int viewportY = getViewport().getScreenY();
		int viewportWidth = getViewport().getScreenWidth();
		int viewportHeight = getViewport().getScreenHeight();
		gameEventManager.fireEvent(new MainGameScreenResizeEvent(viewportX, viewportY, viewportWidth, viewportHeight));
	}

	@Override
	public void dispose() {
		currentMap.dispose();
		world.dispose();
		player.dispose();
		enemies.forEach((Character c) -> c.dispose());
		
		pauseOverlay.dispose();
		levelCompletedOverlay.dispose();
		shapeRenderer.dispose();
	}


	/**
	 * Sets the speicified GameMap as the current one.
	 * @param gameMapFile path to the .tmx tiled map.
	 */
	@Override
	public void setGameMap(String gameMapFile) {
		// Dispose previous map data if there is any.
		if (currentMap != null) {
			// Stop the background music, lights and dispose previous GameMap.
			currentMap.dispose();
			
			// Destroy all bodies except player's body.
			Array<Body> bodies = new Array<>();
			world.getBodies(bodies);
			
			for(int i = 0; i < bodies.size; i++) {
				if (!bodies.get(i).equals(player.getBody())) {
					world.destroyBody(bodies.get(i));
				}
			}
		}

		// Load the new map from gameMapFile.
		currentMap = new GameMap(this, gameMapFile);
		
		// Sets the OrthogonalTiledMapRenderer to show our new map.
		gameEventManager.fireEvent(new MapChangedEvent(currentMap));
		
		// TODO: Don't respawn enemies whenever a map loads.
		enemies = currentMap.spawnNPCs();
		enemies.forEach(e -> engine.addEntity(e));
	}

	@Override
	public World getWorld() {
		return world;
	}
	
	@Override
	public AssetManager getAssets() {
		return assets;
	}
	
	@Override
	public TmxMapLoader getMapLoader() {
		return mapLoader;
	}
	
	@Override
	public NotificationArea getNotificationArea() {
		return notificationArea;
	}
	
	@Override
	public DamageIndicator getDamageIndicator() {
		return damageIndicator;
	}
	
	public GameMap getCurrentMap() {
		return currentMap;
	}
	
	@Override
	public Player getPlayer() {
		return player;
	}

}