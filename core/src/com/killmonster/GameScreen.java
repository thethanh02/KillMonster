package com.killmonster;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.ui.List;

import helper.TileMapHelper;
import objects.players.*;

import static helper.Constants.*;

import java.util.*;

public class GameScreen extends ScreenAdapter {
	
	private OrthographicCamera camera;
	private SpriteBatch batch;
	private World world;
	private Box2DDebugRenderer box2dDebugRenderer;
	
	private OrthogonalTiledMapRenderer orthogonalTiledMapRenderer;
	private TileMapHelper tileMapHelper;
	
	// game objects
	private Player player;
	private ArrayList<Entity> entityList;

	public GameScreen(OrthographicCamera camera) {
		entityList = new ArrayList<>();
		
		this.camera = camera;
		this.batch = new SpriteBatch();
		this.world = new World(new Vector2(0, -25f), false); // if x = 0, y = -9.81f, we will have GRAVITY
		this.box2dDebugRenderer = new Box2DDebugRenderer();
		
		this.tileMapHelper = new TileMapHelper(this);
		this.orthogonalTiledMapRenderer = tileMapHelper.setupMap();
	}
	
	@Override
	public void show() {
		// TODO Auto-generated method stub

	}

	@Override
	public void render(float delta) {
		this.update();
		cameraUpdate();
		
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		// Y Sorting renderer sprite
//		Collections.sort(entityList, Comparator.comparing(Entity::getPositionY).reversed());
		
		
		batch.begin();
		
		for (Entity entity : entityList)
			entity.update();
		orthogonalTiledMapRenderer.render();
		for (Entity entity : entityList)
			entity.render(batch);
		
		batch.end();
		
//		camera.combined.scl(3);
		box2dDebugRenderer.render(world, camera.combined.scl(3));
	}

	private void update() {
		world.step(1/60f, 6, 2);
		
		batch.setProjectionMatrix(camera.combined);
		orthogonalTiledMapRenderer.setView(camera);
		
		if (Gdx.input.isKeyPressed(Input.Keys.ESCAPE)) {
			Gdx.app.exit();
		}
		
	}
	
	private void cameraUpdate() {
		Vector3 position = camera.position;
		position.x = Math.round(player.getBody().getPosition().x + PPM * 10) / 10f + 100;
		position.y = Math.round(player.getBody().getPosition().y + PPM * 10) / 10f + 200;
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
	
	public void setBox(Box box) {
		entityList.add(box);
	}

}
