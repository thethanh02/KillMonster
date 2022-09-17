package com.killmonster;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;

import helper.ListenerClass;
import helper.TileMapHelper;
import objects.players.*;

import static helper.Constants.*;

import java.util.*;

public class GameScreen extends ScreenAdapter {
	
	private OrthographicCamera gameCamera;
	private SpriteBatch batch;
	private World world;
	private Box2DDebugRenderer box2dDebugRenderer;
	
	private OrthogonalTiledMapRenderer orthogonalTiledMapRenderer;
	private TileMapHelper tileMapHelper;
	
	// game objects
	private Player player;
	private ArrayList<Entity> entityList;
	
	private int mapWidth, mapHeight;

	public GameScreen(OrthographicCamera camera) {
		entityList = new ArrayList<>();
		
		this.gameCamera = camera;
		this.batch = new SpriteBatch();
		this.world = new World(new Vector2(0, -25f), true); // if x = 0, y = -9.81f, we will have GRAVITY
		this.box2dDebugRenderer = new Box2DDebugRenderer();
		this.world.setContactListener(new ListenerClass(this));
		
		this.tileMapHelper = new TileMapHelper(this);
		this.orthogonalTiledMapRenderer = tileMapHelper.setupMap();
		
		mapWidth = tileMapHelper.getMapWidth();
		mapHeight = tileMapHelper.getMapHeight();
	}
	
	@Override
	public void show() {
		// TODO Auto-generated method stub

	}

	@Override
	public void render(float delta) {
		this.update();
		
		Gdx.gl.glClearColor(0, 0, 0, 1);
		
		batch.begin();
		
		for (Entity entity : entityList)
			entity.update();
		
		orthogonalTiledMapRenderer.render();
		for (Entity entity : entityList)
			entity.render(batch);
		
		batch.end();
		
//		camera.combined.scl(PPM);
		box2dDebugRenderer.render(world, gameCamera.combined.scl(PPM));
	}

	private void update() {
		world.step(1/60f, 6, 2);
		cameraUpdate();
		
		batch.setProjectionMatrix(gameCamera.combined);
		orthogonalTiledMapRenderer.setView(gameCamera);
		
		if (Gdx.input.isKeyPressed(Input.Keys.ESCAPE)) {
			Gdx.app.exit();
		}
		
	}
	
	private void cameraUpdate() {
		lerpToTarget(gameCamera, player.getBody().getPosition());

        float startX = gameCamera.viewportWidth / 2;
        float startY = gameCamera.viewportHeight / 2;
        float width =  (mapWidth * 32) - gameCamera.viewportWidth / 2;
        float height = (mapHeight * 32) - gameCamera.viewportHeight / 2;
        boundCamera(gameCamera, startX, startY, width, height);
        gameCamera.update();
	}
	  
	// "Lerp" is short word of Linear interpolation
	public void lerpToTarget(OrthographicCamera camera, Vector2 target) {
        Vector3 position = camera.position;
        position.x = Math.round(player.getBody().getPosition().x * PPM * 10) / 10f + 100;
        position.y = Math.round(player.getBody().getPosition().y * PPM * 10) / 10f + 100;

        camera.position.set(position);
        camera.update();
    }
	
    public void boundCamera(OrthographicCamera camera, float startX, float startY, float endX, float endY) {
        Vector3 position = camera.position;

        if (position.x < startX) {
            position.x = startX;
        }
        if (position.y < startY) {
            position.y = startY;
        }

        if (position.x > endX) {
            position.x = endX;
        }
        if (position.y > endY) {
            position.y = endY;
        }

        camera.position.set(position);
        camera.update();
    }

	@Override
	public void resize(int width, int height) {
		// TODO Auto-generated method stub

	}

	@Override
	public void pause() {
		// TODO Auto-generated method stub

	}

	@Override
	public void resume() {
		// TODO Auto-generated method stub

	}

	@Override
	public void hide() {
		// TODO Auto-generated method stub

	}

	@Override
	public void dispose() {
		batch.dispose();
	}
	
	public World getWorld() {
		return world;
	}
	
	public void setPlayer(Player player) {
		this.player = player;
		entityList.add(player);
	}
	
	public void setEnemy(Enemy enemy) {
		entityList.add(enemy);
	}
}
