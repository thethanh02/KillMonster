package com.killmonster.screens;

import com.killmonster.KillMonster;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

public class MainMenuScreen extends AbstractScreen {
	
	private static final String SKIN_FILE = "res/button_atlas.json";
	private static final String BACKGROUND_TEXTURE_FILE = "interface/mainmenu.jpg";
	private static final String BACKGROUND_MUSIC_FILE = "sound/menu.wav";
	
	private Music backgroundMusic;
	private Button playButton, optionsButton, quitButton;
	private Image backgroundImage;
	
	public MainMenuScreen(KillMonster gsm) {
		super(gsm);
		
		Texture backgroundTexture = gsm.getAssets().get(BACKGROUND_TEXTURE_FILE);
		Skin skin = gsm.getAssets().get(SKIN_FILE);
		backgroundMusic = gsm.getAssets().get(BACKGROUND_MUSIC_FILE);
		
		// Define background and button
		backgroundImage = new Image(backgroundTexture);
		playButton = new Button(skin, "play");
		optionsButton = new Button(skin, "options");
		quitButton = new Button(skin, "quit");
		
		Table tableBackground = new Table();
		tableBackground.setFillParent(true);
		tableBackground.add(backgroundImage);
		
		addActor(tableBackground);
		
		Table tableButton = new Table();
		tableButton.setFillParent(true);
		tableButton.add(playButton).row();
		tableButton.add(optionsButton).padTop(10f).row();
		tableButton.add(quitButton).padTop(10f).row();
		
		addActor(tableButton);
		
		backgroundMusic.setLooping(true);
		backgroundMusic.play();
		handleInput();
	}
    
	@Override
	public void render(float delta) {
		gsm.clearScreen();
		gsm.getBatch().begin();
		gsm.getBatch().end();	
		
		draw();
	}
    
	public void handleInput() {
		playButton.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				backgroundMusic.stop();
				gsm.showScreen(Screens.GAME);
			}
		});
        
		quitButton.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				gsm.showScreen(Screens.GAME);
				Gdx.app.exit();
			}
		});
        
    }
    
	@Override
	public void dispose() {
		super.dispose();
	}
}