package com.killmonster;

import com.killmonster.screens.AbstractScreen;
import com.killmonster.screens.Screens;
import com.killmonster.util.Font;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

public class KillMonster extends Game implements GameStateManager {
    
	private SpriteBatch batch;
	private AssetManager assets;
	private Font font;
	
	@Override
	public void create () {
		this.batch = new SpriteBatch();
		this.assets = new AssetManager();
		this.font = new Font(this);
		
		assets.load("res/button_atlas.json", Skin.class);
		assets.load("res/sound_button.json", Skin.class);
		assets.load("res/urm_button.json", Skin.class);
		assets.load("res/volume_button.json", Skin.class);
		assets.load("interface/skin/medievania_skin.json", Skin.class);
		assets.load("res/health_power_bar.png", Texture.class);
		assets.load("res/pause_menu.png", Texture.class);
		assets.load("res/completed_sprite.png", Texture.class);
		assets.load("interface/mainmenu_bg.png", Texture.class);
		assets.load("interface/hud/hud.png", Texture.class);
		assets.load("character/player/Player.png", Texture.class);
		assets.load("character/crabby/Crabby.png", Texture.class);
		assets.finishLoading();
		
		showScreen(Screens.GAME);
	}
    
    
	/**
	 * Shows the specified Screen.
	 * @param s screens to show.
	 */
	@Override
	public void showScreen(Screens s) {
		// Get current screens to dispose it
		Screen currentScreen = getScreen();
 
		// Show new screens
		AbstractScreen newScreen = s.newScreen(this);
		setScreen(newScreen);
 
		// Dispose previous screens
		if (currentScreen != null) {
			currentScreen.dispose();
		}
	}
    
	/**
	 * Clears the screens with pure black.
	 */
	@Override
	public void clearScreen() {
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
	}
    
    
	/**
	 * Gets the SpriteBatch.
	 * @return sprite batch.
	 */
	@Override
	public SpriteBatch getBatch() {
		return batch;
	}
    
	/**
	 * Gets the AssetManager.
	 * @return asset managers.
	 */
	@Override
	public AssetManager getAssets() {
		return assets;
	}

	@Override
	public void render () {
		super.render();
		assets.update();
	}
    
	@Override
	public void dispose () {
		assets.dispose();
		batch.dispose();
	}


	@Override
	public Font getFont() {
		return font;
	}
    
}