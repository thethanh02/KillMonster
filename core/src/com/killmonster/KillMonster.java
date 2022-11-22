package com.killmonster;

import com.killmonster.screens.AbstractScreen;
import com.killmonster.screens.Screens;
import com.killmonster.util.Font;
import com.badlogic.gdx.*;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.*;
import com.badlogic.gdx.graphics.*;
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
		assets.load("res/volume_button_slider.json", Skin.class);
		assets.load("interface/skin/font_skin.json", Skin.class);
		
		assets.load("map/playing_bg_img.png", Texture.class);
		assets.load("res/health_power_bar.png", Texture.class);
		assets.load("res/pause_menu.png", Texture.class);
		assets.load("res/completed_sprite.png", Texture.class);
		assets.load("interface/mainmenu.jpg", Texture.class);
		assets.load("interface/field.png", Texture.class);
		
		assets.load("character/player/CaptainClownNose.png", Texture.class);
		assets.load("character/crabby/Crabby.png", Texture.class);
		assets.load("character/shark/Shark.png", Texture.class);
		assets.load("character/pinkstar/PinkStar.png", Texture.class);
		
		assets.load("objects/diamond/green_diamond.png", Texture.class);
		assets.load("objects/diamond/blue_diamond.png", Texture.class);
		assets.load("objects/potion/blue_potion.png", Texture.class);
		assets.load("objects/potion/red_potion.png", Texture.class);
		assets.load("objects/containers/box.png", Texture.class);
		assets.load("objects/containers/barrel.png", Texture.class);
		assets.load("objects/trap_atlas.png", Texture.class);
		assets.load("objects/shooter/cannon_atlas.png", Texture.class);
		assets.load("objects/shooter/cannon_ball.png", Texture.class);
		assets.load("objects/water_atlas_animation.png", Texture.class);
		assets.load("objects/tree/tree_one_atlas.png", Texture.class);
		assets.load("objects/tree/tree_two_atlas.png", Texture.class);
		
		assets.load("sound/menu.wav", Music.class);
		assets.load("sound/lvlcompleted.wav", Music.class);
		assets.load("sound/gameover.wav", Music.class);
		assets.load("sound/level1.wav", Music.class);
		assets.load("sound/level2.wav", Music.class);
		assets.load("sound/die.wav", Sound.class);
		assets.load("sound/attack1.wav", Sound.class);
		assets.load("sound/jump.wav", Sound.class);
		
		assets.finishLoading();
		
		showScreen(Screens.MAIN_MENU);
	}
    
    
	/**
	 * Shows the specified Screen.
	 * @param s screens to show.
	 */
	@Override
	public void showScreen(Screens s) {
		// Get current screens to dispose it
//		Screen currentScreen = getScreen();
 
		// Show new screens
		AbstractScreen newScreen = s.newScreen(this);
		setScreen(newScreen);
 
//		Will be Error White Console if MainGame dispose
		// Dispose previous screens
//		if (currentScreen != null) {
//			currentScreen.dispose();
//		}
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

	/**
	 * Gets the default font.
	 * @return default font.
	 */
	@Override
	public Font getFont() {
		return font;
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
    
}