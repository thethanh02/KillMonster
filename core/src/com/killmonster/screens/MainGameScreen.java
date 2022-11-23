package com.killmonster.screens;

import com.killmonster.entity.character.Enemy;
import com.killmonster.entity.character.Player;
import com.killmonster.entity.character.Shark;
import com.killmonster.entity.objects.*;
import com.killmonster.entity.objects.chest.*;
import com.killmonster.entity.objects.container.*;
import com.killmonster.entity.objects.diamond.*;
import com.killmonster.entity.objects.potion.*;
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
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.RunnableAction;
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
	private Array<Enemy> enemies;
	private Array<Container> boxes;
	private Array<Potion> potions;
	private Array<Shooter> cannons;
	private Array<Bullet> bullets;
	private Array<GameObject> objs;
	private Array<Treasure> dia;
	private Array<Treasure> coin;
	private Array<Chest> chest;
	private Array<Key> key;
	
	private PauseOverlay pauseOverlay;
	private ShapeRenderer shapeRenderer;
	
	public static boolean isNextLevel;
	private static int currentLevel = 4;
	private String gameMapFile;
	private int prvScorePlayer = 0;
	public static int currentScore = 0;
	
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
		gameMapFile = "map/level" + currentLevel + ".tmx";
		setGameMap(gameMapFile);
		player = currentMap.spawnPlayer();
		potions = new Array<>();
		bullets = new Array<>();
		shade.addAction(Actions.sequence(Actions.alpha(.3f), new RunnableAction() {
			@Override
			public void run() {
			}
		}, Actions.fadeOut(.3f)));
		
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
			currentMap.stopBackgroundMusic();
			shade.addAction(Actions.sequence(Actions.fadeIn(.3f), new RunnableAction() {
				@Override
				public void run() {
					gsm.showScreen(Screens.LEVEL_COMPLETED);
				}
				
			}, Actions.fadeOut(.5f)));
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
		if (Gdx.input.isKeyPressed(Input.Keys.K)) {
			player.specialAttack();
		}
		if (!player.isAttacking2()) {
			if (Gdx.input.isKeyPressed(Input.Keys.J)) {
				player.swingWeapon();
			}
			if (Gdx.input.isKeyJustPressed(Input.Keys.E)) {
				player.openChest();
			}
			if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
				player.jump();
			} else if (Gdx.input.isKeyPressed(Input.Keys.RIGHT) || Gdx.input.isKeyPressed(Input.Keys.D)) {
				player.moveRight();
			} else if (Gdx.input.isKeyPressed(Input.Keys.LEFT) || Gdx.input.isKeyPressed(Input.Keys.A)) {
				player.moveLeft();
			}
		}
	}
    
	public boolean isAllEnemiesKilled() {
		for (Enemy x : enemies)
			if (!x.isKilled())
				return false;
		for (Chest c : chest)
			if (!c.isKilled())
				return false;
		for (Treasure diamond : dia)
			if (!diamond.isKilled())
				return false;
		return true;
	}

	public void update(float delta) {
		if (isNextLevel) {
			prvScorePlayer = player.getScore();
			
			if (currentLevel < 7) {
				currentLevel++;
				gameMapFile = "map/level" + currentLevel + ".tmx";
				isNextLevel = false;
				shade.addAction(Actions.sequence(Actions.alpha(.3f), new RunnableAction() {
					@Override
					public void run() {
						setGameMap(gameMapFile);
					}
				}, Actions.fadeOut(.3f)));
			} else
				gsm.showScreen(Screens.GAME_COMPLETED);
		}
		
		handleInput(delta);
		if (!Constants.PAUSE) {
			world.step(1/60f, 6, 2);
			
			currentScore = player.getScore();
			// entities update
			for (Container x : boxes) {
				x.update(delta);
				if (x.isKilled()) {
					Random generator = new Random();
					int rnd = generator.nextInt(3);
					// if random == 0 -> Box is blank
					if (rnd == 1) 
						potions.add(new BluePotion(assets, world, x.getBody().getPosition().x * Constants.PPM, x.getBody().getPosition().y * Constants.PPM + 10));
					else if (rnd == 2)
						potions.add(new RedPotion(assets, world, x.getBody().getPosition().x * Constants.PPM, x.getBody().getPosition().y * Constants.PPM + 10));
				
					boxes.removeValue(x, true);
				}
			}
			objs.forEach((GameObject x) -> x.update(delta));
			for (Shooter x : cannons) {
				x.update(delta);
				if (x.isKilled()) cannons.removeValue(x, true);
				else if (x.cooldownSpawnBullet()) {
					bullets.add(x.spawnBullet());
				}
			}
			for (Chest x : chest) {
				x.update(delta);
				if (x.isKilled()) {
					dia.add(new RedDiamond(assets , world, x.getBody().getPosition().x * Constants.PPM, x.getBody().getPosition().y * Constants.PPM + 10));
					chest.removeValue(x, true);
				}
			}
			for (Key x : key) {
				x.update(delta);
				if (x.isKilled()) key.removeValue(x, true);
			}
			for (Treasure x : dia) {
				x.update(delta);
				if (x.isKilled()) dia.removeValue(x, true);
			}
			for (Treasure x : coin) {
				x.update(delta);
				if (x.isKilled()) coin.removeValue(x, true);
			}	
			for (Bullet x : bullets) {
				x.update(delta);
				if (x.isKilled()) bullets.removeValue(x, true);
			}
			for (Potion x : potions) {
				x.update(delta);
				if (x.isKilled()) potions.removeValue(x, true);
			}			
			for (Enemy x : enemies) {
				x.update(delta);
				if (x.isKilled()) enemies.removeValue(x, true);
				else if(!player.isKilled())  {
					float distanceX = player.getBody().getPosition().x - x.getBody().getPosition().x;
					float distanceY = player.getBody().getPosition().y - x.getBody().getPosition().y;
					if (distanceY >= -.15f && distanceY <= .15f) {
						if (x.isFacingRight()) {
							if (distanceX <= x.getSwingWeaponRange() && distanceX >= 0) 
								x.setInRangeTarget(true);
							else
								x.setInRangeTarget(false);
						} else {
							if (distanceX >= -x.getSwingWeaponRange() && distanceX <= 0) 
								x.setInRangeTarget(true);
							else 
								x.setInRangeTarget(false);
						}
					}
					if (x instanceof Shark && distanceY >= -.3f && distanceY <= .3f) {
						if (x.isFacingRight()) {
							if (distanceX <= 1.5f && distanceX >= 0) 
								x.setLockedOnTarget(player);
							else
								x.removeLockedOnTarget(player);
						} else {
							if (distanceX >= -1.5f && distanceX <= 0) 
								x.setLockedOnTarget(player);
							else 
								x.removeLockedOnTarget(player);
						}
					}
						
				}
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
		boxes.forEach((Container x) -> x.draw(getBatch()));
		potions.forEach((Potion x) -> x.draw(getBatch()));
		chest.forEach((Chest x) -> x.draw(getBatch()));
		key.forEach((Key x) -> x.draw(getBatch()));
		dia.forEach((Treasure x) -> x.draw(getBatch()));
		coin.forEach((Treasure x) -> x.draw(getBatch()));
		enemies.forEach((Enemy x) -> x.draw(getBatch()));		
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
		boxes.forEach((Container x) -> x.dispose());
		potions.forEach((Potion x) -> x.dispose());
		chest.forEach((Chest x) -> x.dispose());
		key.forEach((Key x) -> x.dispose());
		dia.forEach((Treasure x) -> x.dispose());
		coin.forEach((Treasure x) -> x.dispose());
		enemies.forEach((Enemy x) -> x.dispose());
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
			player.setScore(prvScorePlayer);
		}
		// TODO: Don't respawn enemies whenever a map loads.
		enemies = currentMap.spawnNPCs();
		boxes = currentMap.spawnBoxes();
		cannons = currentMap.spawnCannons();
		objs = currentMap.spawnGameObjects();
		dia = currentMap.spawnDiamonds();
		coin = currentMap.spawnCoin();
		key = currentMap.spawnKey();
		chest = currentMap.spawnChest();
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