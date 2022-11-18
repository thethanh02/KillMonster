package com.killmonster.ui;

import com.killmonster.screens.MainGameScreen;
import com.killmonster.util.Constants;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

public class PauseOverlay extends Stage {
	
	private static final String SOUND_SKIN_FILE = "res/sound_button.json";
	private static final String URM_SKIN_FILE = "res/urm_button.json";
	private static final String VOLUME_SKIN_FILE = "res/volume_button_slider.json";
	private static final String TEXTURE_FILE = "res/pause_menu.png";
    
	private Button resumeButton, backButton, homeButton;
	private Button musicButton, sfxButton;
	private Slider volumeMusicSlider;
	private Image transparentImage, background;
	
	private MainGameScreen gsm;
    
	public PauseOverlay(MainGameScreen gsm) {
		this.gsm = gsm;

		Texture texture = gsm.getAssets().get(TEXTURE_FILE);
		Skin soundSkin = gsm.getAssets().get(SOUND_SKIN_FILE);
		Skin urmSkin = gsm.getAssets().get(URM_SKIN_FILE);
		Skin volumeSkin = gsm.getAssets().get(VOLUME_SKIN_FILE);
		
		// Define transparent image
		transparentImage = new Image(new TextureRegion(texture, 95, 0, 100, 100));
		transparentImage.setScale(Constants.V_WIDTH, Constants.V_HEIGHT);
		transparentImage.setColor(0, 0, 0, .6f);
		
		Table tableTransparent = new Table();
		tableTransparent.setFillParent(true);
		tableTransparent.bottom().left();
		tableTransparent.add(transparentImage);
		
		// Define pause menu
		background = new Image(texture);
		
		Table tablePause = new Table();
		tablePause.setFillParent(true);
		tablePause.add(background);
		
		// Define button
		volumeMusicSlider = new Slider(0, 1, .01f, false, volumeSkin);
		volumeMusicSlider.setValue(.5f);
		
		resumeButton = new Button(urmSkin, "resume");
		backButton = new Button(urmSkin, "back");
		homeButton = new Button(urmSkin, "home");
		
		musicButton = new Button(soundSkin, "default");
		sfxButton = new Button(soundSkin, "default");
		handleInput();
		
		Table tableUrmButton = new Table();
		tableUrmButton.setFillParent(true);
		tableUrmButton.padTop(270f);
		
		tableUrmButton.add(homeButton);
		tableUrmButton.add(backButton).padLeft(12f);
		tableUrmButton.add(resumeButton).padLeft(12f);
		
		Table tableVolumeBar = new Table();
		tableVolumeBar.setFillParent(true);
		tableVolumeBar.padTop(160f);
		tableVolumeBar.add(volumeMusicSlider);
		
		Table tableVolumeButton = new Table();
		tableVolumeButton.setFillParent(true);
		tableVolumeButton.padTop(160f);
		
		Table tableSoundButton = new Table();
		tableSoundButton.setFillParent(true);
		tableSoundButton.padTop(-65f).padLeft(110f);
		
		tableSoundButton.add(musicButton).row();
		tableSoundButton.add(sfxButton).padTop(6f);
		
		addActor(tableTransparent);
		addActor(tablePause);
		addActor(tableUrmButton);
		addActor(tableVolumeBar);
		addActor(tableVolumeButton);
		addActor(tableSoundButton);
	}
    
	public void handleInput() {
//		Gdx.input.setInputProcessor(this);
		if (!musicButton.isChecked()) 
			gsm.getCurrentMap().playBackgroundMusic(getVolume());
		else 
			gsm.getCurrentMap().pauseBackgroundMusic();
		
		gsm.getPlayer().setToMute(sfxButton.isChecked());
		if (!sfxButton.isChecked())
			gsm.getPlayer().setVolume(volumeMusicSlider.getValue());
			
		resumeButton.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				Constants.PAUSE = false;
			}
		});
	}
	
	public boolean setToMuteMusic() {
		return musicButton.isChecked();
	}
	
    @Override
    public void dispose() {
        super.dispose();
    }
    
    public float getVolume() {
		return volumeMusicSlider.getValue();
	}
}