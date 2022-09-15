package com.killmonster;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;

public class KillMonster extends Game {
	public static KillMonster INSTANCE;
	private int widthScreen, heightScreen;
	private OrthographicCamera orthographicCamera;
	
	public KillMonster() {
		INSTANCE = this;
	}
	
	@Override
	public void create () {
		this.widthScreen = Gdx.graphics.getWidth();
		this.heightScreen = Gdx.graphics.getHeight();
		this.orthographicCamera = new OrthographicCamera();
		this.orthographicCamera.setToOrtho(false, widthScreen / 1.5f, heightScreen / 1.5f);
		setScreen(new GameScreen(orthographicCamera));
	}

	@Override
	public void render () {
		super.render();
	}
	
	@Override
	public void resize(int width, int height) {
		super.resize(width, height);
	}
	
	@Override
	public void pause() {
		super.pause();
	}
	
	@Override
	public void resume() {
		super.resume();
	}
	
	@Override
	public void dispose () {
	}
	
}
