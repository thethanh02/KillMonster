package com.killmonster.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.killmonster.GameStateManager;

public class LevelCompletedScreen extends AbstractScreen {

	private static final String URM_SKIN_FILE = "res/urm_button.json";
	private static final String TEXTURE_FILE = "res/completed_sprite.png";
	
	private static final String BACKGROUND_IMAGE = "interface/field.png";
	private static final String BACKGROUND_MUSIC_FILE = "sound/lvlcompleted.wav";
	
	private Music backgroundMusic;
	private Button resumeButton, homeButton;
	private Image background, completedSprite;
	
	public LevelCompletedScreen(GameStateManager gsm) {
		super(gsm);
		
		Texture texture1 = gsm.getAssets().get(BACKGROUND_IMAGE);
		Texture texture2 = gsm.getAssets().get(TEXTURE_FILE);
		Skin urmSkin = gsm.getAssets().get(URM_SKIN_FILE);
		backgroundMusic = gsm.getAssets().get(BACKGROUND_MUSIC_FILE);
		
		background = new Image(texture1);
//		background.setScale(Constants.V_WIDTH, Constants.V_HEIGHT);
		Table tableBackground = new Table();
		tableBackground.setFillParent(true);
		tableBackground.bottom().left();
		tableBackground.add(background);
		
		// Define completed menu
		completedSprite = new Image(texture2);
		Table tableCompleted = new Table();
		tableCompleted.setFillParent(true);
		tableCompleted.add(completedSprite);
		
		// Define button
		resumeButton = new Button(urmSkin, "resume");
		homeButton = new Button(urmSkin, "home");
		
		Table tableUrmButton = new Table();
		tableUrmButton.setFillParent(true);
		tableUrmButton.padTop(100f);
		
		tableUrmButton.add(homeButton);
		tableUrmButton.add(resumeButton).padLeft(40f);
		
		addActor(tableBackground);
		addActor(tableCompleted);
		addActor(tableUrmButton);
		
		backgroundMusic.setLooping(false);
		backgroundMusic.play();
	}

	public void handleInput(float dt) {
		Gdx.input.setInputProcessor(this);
		resumeButton.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				backgroundMusic.stop();
				MainGameScreen.isNextLevel = true;
				gsm.showScreen(Screens.GAME);
				dispose();
			}
		});
		homeButton.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				Gdx.app.exit();
			}
		});
	}
    
	@Override
	public void render(float delta) {
		handleInput(delta);
		gsm.clearScreen();
		draw();
	}

	@Override
	public void resize(int width, int height) {
		getViewport().update(width, height);
	}
    
	@Override
	public void dispose() {
		super.dispose();
	}

}
