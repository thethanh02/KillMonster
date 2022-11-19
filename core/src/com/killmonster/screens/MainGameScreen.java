package com.killmonster.screens;

import com.killmonster.entity.character.Character;
import com.killmonster.entity.character.Player;
import com.killmonster.entity.objects.*;
import com.killmonster.entity.shooter.*;
import com.killmonster.*;
import com.killmonster.map.*;
import com.killmonster.ui.*;
import com.killmonster.util.*;

import java.util.Random;

import com.badlogic.gdx.*;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.Array;

public class MainGameScreen extends AbstractScreen implements GameWorldManager {

	private final AssetManager assets;
	private final OrthogonalTiledMapRenderer renderer;
	private final Box2DDebugRenderer b2dr;
	private final TmxMapLoader mapLoader;
	
	private final DamageIndicator damageIndicator;
	private final MessageArea messageArea;
	private final HUD hud;
	private final Image shade;
	
	private World world;
	private GameMap currentMap;
	
	private Player player;
	private Array<Character> enemies;
	private Array<Box> boxes;
	private Array<Potion> potions;
	private Array<Shooter> cannons;
	private Array<Bullet> bullets;
	private Array<GameObject> objs;
	
	private PauseOverlay pauseOverlay;
	private ShapeRenderer shapeRenderer;
	
	public static boolean isNextLevel;
	private static int currentLevel = 0;
	private String gameMapFile;
	
	public MainGameScreen(GameStateManager gsm) {
		super(gsm);
		assets = gsm.getAssets();
		// Since we will be rendering TiledMaps, we should scale the viewport with PPM.
		getViewport().setWorldSize(Constants.V_WIDTH / Constants.PPM, Constants.V_HEIGHT / Constants.PPM);
		
		// Initialize the world, and register the world contact listener.
		world = new World(new Vector2(0, Constants.GRAVITY), true);
		world.setContactListener(new WorldContactListener());
		
		// Initialize shade to provide fade in/out effects later.
		// The shade is drawn atop everything, with only its transparency being adjusted.
		shade = new Image(new TextureRegion(Utils.getTexture()));
		shade.setSize(getViewport().getScreenWidth(), getViewport().getScreenHeight());
		shade.setColor(0, 0, 0, 0);
		addActor(shade);
		
		// Initialize the OrthogonalTiledMapRenderer to render our map.
		renderer = new OrthogonalTiledMapRenderer(null, 1 / Constants.PPM);
		b2dr = new Box2DDebugRenderer();
		mapLoader = new TmxMapLoader();
		
		// Load the map and spawn player.
		gameMapFile = "res/level" + currentLevel + ".tmx";
		setGameMap(gameMapFile);
		player = currentMap.spawnPlayer();
		potions = new Array<>();
		bullets = new Array<>();
		
		// Initialize HUD.
		damageIndicator = new DamageIndicator(gsm, getCamera(), 1.5f);
		messageArea = new MessageArea(gsm, 6, 3f);
		hud = new HUD(gsm, player);
		
		pauseOverlay = new PauseOverlay(this);
		shapeRenderer = new ShapeRenderer();
	}


	public void handleInput(float delta) {
		if (player.isSetToKill()) {
			currentMap.stopBackgroundMusic();
			gsm.showScreen(Screens.GAME_OVER);
			return;
		}
        Gdx.input.setInputProcessor(this);
		if(isAllEnemiesKilled()) {
			gsm.showScreen(Screens.GAME_COMPLETED);
			return;
		}
		
		if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
			Constants.PAUSE = !Constants.PAUSE;
		}   
		if (Constants.PAUSE) {
			Gdx.input.setInputProcessor(pauseOverlay);
			pauseOverlay.handleInput();
			return;
		}
		
		if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_0)) {
			Constants.DEBUG = !Constants.DEBUG;
		}
		
		if (player.isHitted()) return;
		if (Gdx.input.isKeyPressed(Input.Keys.CONTROL_LEFT)) {
			player.swingWeapon();
		}
		
		if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
			player.jump();
		} else if (Gdx.input.isKeyPressed(Input.Keys.RIGHT) || Gdx.input.isKeyPressed(Input.Keys.D)) {
			player.moveRight();
		} else if (Gdx.input.isKeyPressed(Input.Keys.LEFT) || Gdx.input.isKeyPressed(Input.Keys.A)) {
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
		
//		if (isNextLevel) {
//			if (currentLevel < 2) currentLevel++;
//			gameMapFile = "res/level" + currentLevel + ".tmx";
//			isNextLevel = false;
//			shade.addAction(Actions.sequence(Actions.fadeIn(.3f), new RunnableAction() {
//				@Override
//				public void run() {
//					setGameMap(gameMapFile);
//				}
//				
//			}, Actions.fadeOut(.8f)));
//		}
		
		if (isNextLevel) {
			if (currentLevel < 3) currentLevel++;
			gameMapFile = "res/level" + currentLevel + ".tmx";
			setGameMap(gameMapFile);
			isNextLevel = false;
		}
		handleInput(delta);
		if (!Constants.PAUSE) {
			world.step(1/60f, 6, 2);
			
			// entities update
			for (Box x : boxes) {
				x.update(delta);
				if (x.isKilled()) {
					Random generator = new Random();
					int rnd = generator.nextInt(3);
					// if random == 0 -> Box is blank
					if (rnd == 1) {
						Potion tmPotion = new BluePotion(assets, world, x.getBody().getPosition().x * Constants.PPM, x.getBody().getPosition().y * Constants.PPM + 10); 
						potions.add(tmPotion);
					}
					else if (rnd == 2)
						potions.add(new RedPotion(assets, world, x.getBody().getPosition().x * Constants.PPM, x.getBody().getPosition().y * Constants.PPM + 10));
				
					boxes.removeValue(x, true);
				}
			}
			objs.forEach((GameObject x) -> x.update(delta));
			for (Shooter x : cannons) {
				x.update(delta);
				if (x.cooldownSpawnBullet()) {
					CannonBall cannonBall = new CannonBall(assets, world, x.getBody().getPosition().x * Constants.PPM - 8.5f, x.getBody().getPosition().y * Constants.PPM);
					bullets.add(cannonBall);
					
				}
			}
			for (Bullet x : bullets) {
				x.update(delta);
				if (x.isKilled()) bullets.removeValue(x, true);
			}
			for (Potion x : potions) {
				x.update(delta);
				if (x.isKilled()) potions.removeValue(x, true);
			}			
			for (Character x : enemies) {
				x.update(delta);
				if (x.isKilled()) enemies.removeValue(x, true);
			}
			player.update(delta);
			// ui update
			hud.update(delta);
			messageArea.update(delta);
			damageIndicator.update(delta);
			
			if (CameraShake.getShakeTimeLeft() > 0){
				CameraShake.update(Gdx.graphics.getDeltaTime());
				getCamera().translate(CameraShake.getPos());
			} else {
				CameraUtils.lerpToTarget(getCamera(), player.getBody().getPosition());
			}
			
			// Make sure to bound the camera within the TiledMap.
			CameraUtils.boundCamera(getCamera(), getCurrentMap());
			
			// Tell our renderer to draw only what our camera can see.
			renderer.setView((OrthographicCamera) getCamera());
			
			// Update all actors in this stage.
			this.act(delta);
		}
	}

	@Override
	public void render(float delta) {
		update(delta);
		gsm.clearScreen();
		
		// Render game map.
		renderer.render();
		if (Constants.DEBUG) b2dr.render(world, getCamera().combined);
		
		// Render characters.
		getBatch().setProjectionMatrix(getCamera().combined);
		getBatch().begin();
		
		// entities render
		cannons.forEach((Shooter x) -> x.draw(getBatch()));
		bullets.forEach((Bullet x) -> x.draw(getBatch()));
		boxes.forEach((Box x) -> x.draw(getBatch()));
		potions.forEach((Potion x) -> x.draw(getBatch()));
		enemies.forEach((Character x) -> x.draw(getBatch()));		
		player.draw(getBatch());
		objs.forEach((GameObject x) -> x.draw(getBatch()));

		getBatch().end();
		
		// ui render
		getBatch().setProjectionMatrix(damageIndicator.getCamera().combined);
		damageIndicator.draw();
		
		getBatch().setProjectionMatrix(messageArea.getCamera().combined);
		messageArea.draw();
		
		// Set our batch to now draw what the Hud camera sees.
		getBatch().setProjectionMatrix(hud.getCamera().combined);
		hud.draw();
		
		if (Constants.PAUSE) 
			pauseOverlay.draw();;
		
		
		// Draw all actors on this stage.
		this.draw();
	}

	@Override
	public void resize(int width, int height) {
		super.resize(width, height);
		damageIndicator.getViewport().update(width, height);
	}

	@Override
	public void dispose() {
		renderer.dispose();
		b2dr.dispose();
		hud.dispose();
		currentMap.dispose();
		world.dispose();
		
		objs.forEach((GameObject x) -> x.dispose());
		cannons.forEach((Shooter x) -> x.dispose());
		bullets.forEach((Bullet x) -> x.dispose());
		boxes.forEach((Box x) -> x.dispose());
		potions.forEach((Potion x) -> x.dispose());
		enemies.forEach((Character x) -> x.dispose());
		player.dispose();
		
		pauseOverlay.dispose();
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
			currentMap.stopBackgroundMusic();
			currentMap.dispose();
			
			// Destroy all bodies except player's body.
			Array<Body> bodies = new Array<>();
			world.getBodies(bodies);

			for (int i = 0; i < bodies.size; i++) {
				if (bodies.get(i) != player.getBody()) {
					world.destroyBody(bodies.get(i));
				}
			}
		}

		// Load the new map from gameMapFile.
		currentMap = new GameMap(this, gameMapFile);
		if (pauseOverlay != null) { 
			if (!pauseOverlay.setToMuteMusic())
				currentMap.playBackgroundMusic(pauseOverlay.getVolume());
		} else
			currentMap.playBackgroundMusic(.5f);
		
		// Sets the OrthogonalTiledMapRenderer to show our new map.
		renderer.setMap(currentMap.getTiledMap());
		
		// Update shade size to make fade out/in work correctly.
		shade.setSize(getCurrentMap().getMapWidth(), getCurrentMap().getMapHeight());
		
		if (player != null) {
			player.reposition(currentMap.getPosPlayer());
		}
		// TODO: Don't respawn enemies whenever a map loads.
		enemies = currentMap.spawnNPCs();
		boxes = currentMap.spawnBoxes();
		cannons = currentMap.spawnCannons();
		objs = currentMap.spawnGameObjects();
	}
	
	public void addBullet(Bullet b) {
		bullets.add(b);;
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