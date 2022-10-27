package com.killmonster.screens;

import com.killmonster.character.Character;
import com.killmonster.character.Player;
import com.killmonster.event.GameEventManager;
import com.killmonster.event.MainGameScreenResizeEvent;
import com.killmonster.event.MapChangedEvent;
import com.killmonster.*;
import com.killmonster.map.*;
import com.killmonster.system.Box2DDebugRendererSystem;
import com.killmonster.system.TiledMapRendererSystem;
import com.killmonster.ui.*;
import com.killmonster.util.*;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.Array;

public class MainGameScreen extends AbstractScreen implements GameWorldManager {
	
	private PooledEngine engine;
	private GameEventManager gameEventManager;
	
	private final AssetManager assets;
	private final TmxMapLoader mapLoader;
	
	private final DamageIndicator damageIndicator;
	private final MessageArea messageArea;
	private final HUD hud;
	private final Image shade;
	
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
		
		// Initialize the world, and register the world contact listener.
		world = new World(new Vector2(0, Constants.GRAVITY), true);
		world.setContactListener(new WorldContactListener());
		
		engine = new PooledEngine();
		engine.addSystem(new TiledMapRendererSystem((OrthographicCamera) getCamera()));
		engine.addSystem(new Box2DDebugRendererSystem(world, getCamera()));

		gameEventManager = GameEventManager.getINSTANCE();
		
		// Initialize shade to provide fade in/out effects later.
		// The shade is drawn atop everything, with only its transparency being adjusted.
		shade = new Image(new TextureRegion(Utils.getTexture()));
		shade.setSize(getViewport().getScreenWidth(), getViewport().getScreenHeight());
		shade.setColor(0, 0, 0, 0);
		addActor(shade);
		
		// Initialize the OrthogonalTiledMapRenderer to render our map.
		mapLoader = new TmxMapLoader();
		
		// Load the map and spawn player.
		gameMapFile = "res/level" + currentLevel + ".tmx";
		setGameMap(gameMapFile);
		player = currentMap.spawnPlayer();
		
		// Initialize HUD.
		damageIndicator = new DamageIndicator(gsm, getCamera(), 1.5f);
		messageArea = new MessageArea(gsm, 6, 3f);
		hud = new HUD(gsm, player);
		
		pauseOverlay = new PauseOverlay(gsm);
		levelCompletedOverlay = new LevelCompletedOverlay(gsm);
		shapeRenderer = new ShapeRenderer();
	}


	public void handleInput(float delta) {
		if (player.isSetToKill()) {
			return;
		}
        
		if(isAllEnemiesKilled()) {
			Constants.COMPLETED = true;
			levelCompletedOverlay.handleInput();
			return;
		}
		
		if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_1)) {
			Constants.PAUSE = !Constants.PAUSE;
		}   
		if (Constants.PAUSE) {
			pauseOverlay.handleInput();
			return;
		}
		if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_0)) {
			Constants.DEBUG = !Constants.DEBUG;
		}
		
		
		if (Gdx.input.isKeyPressed(Input.Keys.CONTROL_LEFT)) {
			player.swingWeapon();
		}
		
		if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
			player.jump();
		} else if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
			player.moveRight();
		} else if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
			player.moveLeft();
		}
	}
    
	public boolean isAllEnemiesKilled() {
		for (Character character : enemies)
			if (!character.isKilled())
				return false;
		return true;
	}

	public void update(float delta) {
		if (isNextLevel) {
			if (currentLevel < 2) currentLevel++;
			gameMapFile = "res/level" + currentLevel + ".tmx";
			setGameMap(gameMapFile);
			world.destroyBody(player.getB2Body());
			
			player = currentMap.spawnPlayer();
			isNextLevel = false;
		}
		handleInput(delta);
		if (!Constants.COMPLETED && !Constants.PAUSE) {
			world.step(1/60f, 6, 2);
			
			enemies.forEach((Character c) -> c.update(delta));
			player.update(delta);
			hud.update(delta);
			messageArea.update(delta);
			damageIndicator.update(delta);
			
			if (CameraShake.getShakeTimeLeft() > 0){
				CameraShake.update(Gdx.graphics.getDeltaTime());
				getCamera().translate(CameraShake.getPos());
			} else {
				CameraUtils.lerpToTarget(getCamera(), player.getB2Body().getPosition());
			}
			
			// Make sure to bound the camera within the TiledMap.
			CameraUtils.boundCamera(getCamera(), getCurrentMap());
			
			// Update all actors in this stage.
			this.act(delta);
		}
	}

	@Override
	public void render(float delta) {
		update(delta);
		gsm.clearScreen();
		
		// Render game map.
		engine.update(delta);
		
		// Render characters.
		getBatch().setProjectionMatrix(getCamera().combined);
		getBatch().begin();
		enemies.forEach((Character c) -> c.draw(getBatch()));
		player.draw(getBatch());
		getBatch().end();
		
		getBatch().setProjectionMatrix(damageIndicator.getCamera().combined);
		damageIndicator.draw();
		
		getBatch().setProjectionMatrix(messageArea.getCamera().combined);
		messageArea.draw();
		
		// Set our batch to now draw what the Hud camera sees.
		getBatch().setProjectionMatrix(hud.getCamera().combined);
		hud.draw();
		
		if (Constants.COMPLETED) 
			levelCompletedOverlay.draw();
		
		if (Constants.PAUSE) 
			pauseOverlay.draw();;
		
		
		// Draw all actors on this stage.
		this.draw();
	}

	@Override
	public void resize(int width, int height) {
		super.resize(width, height);
		
		damageIndicator.getViewport().update(width, height);
		
		int viewportX = getViewport().getScreenX();
		int viewportY = getViewport().getScreenY();
		int viewportWidth = getViewport().getScreenWidth();
		int viewportHeight = getViewport().getScreenHeight();
		gameEventManager.fireEvent(new MainGameScreenResizeEvent(viewportX, viewportY, viewportWidth, viewportHeight));
	}

	@Override
	public void dispose() {
		hud.dispose();
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
				if (!bodies.get(i).equals(player.getB2Body())) {
					world.destroyBody(bodies.get(i));
				}
			}
		}

		// Load the new map from gameMapFile.
		currentMap = new GameMap(this, gameMapFile);
		
		// Sets the OrthogonalTiledMapRenderer to show our new map.
		gameEventManager.fireEvent(new MapChangedEvent(currentMap));
		
		// Update shade size to make fade out/in work correctly.
		shade.setSize(getCurrentMap().getMapWidth(), getCurrentMap().getMapHeight());
		
		// TODO: Don't respawn enemies whenever a map loads.
		enemies = currentMap.spawnNPCs();
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
	public MessageArea getMessageArea() {
		return messageArea;
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