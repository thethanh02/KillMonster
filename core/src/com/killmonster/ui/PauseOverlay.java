package com.killmonster.ui;

import com.killmonster.GameStateManager;
import com.killmonster.util.Constants;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

public class PauseOverlay extends Stage {
	
	private GameStateManager gsm;
	
	private static final String SOUND_SKIN_FILE = "res/sound_button.json";
	private static final String URM_SKIN_FILE = "res/urm_button.json";
	private static final String VOLUME_SKIN_FILE = "res/volume_button.json";
	private static final String TEXTURE_FILE = "res/pause_menu.png";
    
    private Button resumeButton, backButton, homeButton;
    private Button volumeButton, volumeBar;
    private Button musicButton, sfxButton;
    Image transparentImage, background;
    
    public PauseOverlay(GameStateManager gsm) {
    	this.gsm = gsm;
        Gdx.input.setInputProcessor(this);
        
        Texture texture = gsm.getAssets().get(TEXTURE_FILE);
        Skin soundSkin = gsm.getAssets().get(SOUND_SKIN_FILE);
        Skin urmSkin = gsm.getAssets().get(URM_SKIN_FILE);
        Skin volumeSkin = gsm.getAssets().get(VOLUME_SKIN_FILE);
        
        // Define transparent image
        transparentImage = new Image(new TextureRegion(texture, 95, 0, 100, 100));
        transparentImage.setScale(Constants.V_WIDTH, Constants.V_HEIGHT);
        transparentImage.setColor(0, 0, 0, .6f);;
        
        Table tableTransparent = new Table();
        tableTransparent.setFillParent(true);
        tableTransparent.bottom().left();
        tableTransparent.add(transparentImage);
        
        // Define pause menu
        background = new Image(new TextureRegion(texture));
        
        Table tablePause = new Table();
        tablePause.setFillParent(true);
        tablePause.add(background);
        
        // Define button
        volumeBar = new Button(volumeSkin, "bar");
        volumeButton = new Button(volumeSkin, "button");
        
        resumeButton = new Button(urmSkin, "resume");
        backButton = new Button(urmSkin, "back");
        homeButton = new Button(urmSkin, "home");
        
        musicButton = new Button(soundSkin, "music");
        sfxButton = new Button(soundSkin, "sfx");
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
        tableVolumeBar.add(volumeBar);
        
        Table tableVolumeButton = new Table();
        tableVolumeButton.setFillParent(true);
        tableVolumeButton.padTop(160f);
        tableVolumeButton.add(volumeButton);
        
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
    	
    	transparentImage.addListener(new ClickListener() {
    		@Override
    		public boolean mouseMoved(InputEvent event, float x, float y) {
    			resetButtonChecked("");
    			return super.mouseMoved(event, x, y);
    		}
    	});
    	
    	background.addListener(new ClickListener() {
    		@Override
    		public boolean mouseMoved(InputEvent event, float x, float y) {
    			resetButtonChecked("");
    			return super.mouseMoved(event, x, y);
    		}
    	});
    	
        resumeButton.addListener(new ClickListener() {
        	@Override
        	public void clicked(InputEvent event, float x, float y) {
        		Constants.PAUSE = false;
        	}
        	
        	@Override
        	public boolean mouseMoved(InputEvent event, float x, float y) {
        		resetButtonChecked("resumeButton");
        		return super.mouseMoved(event, x, y);
        	}
        });
        
        backButton.addListener(new ClickListener() {
        	@Override
        	public boolean mouseMoved(InputEvent event, float x, float y) {
        		resetButtonChecked("backButton");
        		return super.mouseMoved(event, x, y);
        	}
        });
        
        musicButton.addListener(new ClickListener() {
        	@Override
        	public boolean mouseMoved(InputEvent event, float x, float y) {
        		resetButtonChecked("musicButton");
        		return super.mouseMoved(event, x, y);
        	}
        });
        
       	sfxButton.addListener(new ClickListener() {
        	@Override
        	public boolean mouseMoved(InputEvent event, float x, float y) {
        		resetButtonChecked("sfxButton");
        		return super.mouseMoved(event, x, y);
        	}
        });
        
        backButton.addListener(new ClickListener() {
        	@Override
        	public boolean mouseMoved(InputEvent event, float x, float y) {
        		resetButtonChecked("backButton");
        		return super.mouseMoved(event, x, y);
        	}
        });
        
        volumeButton.addListener(new ClickListener() {
        	@Override
        	public void touchDragged(InputEvent event, float x, float y, int pointer) {
        		if (volumeButton.getX() < 524) {
        			volumeButton.setX(524);
        		} else if (volumeButton.getX() > 696) {
        			volumeButton.setX(696);
        		}
        		else if (event.getStageX() >= 524 && event.getStageX() <= 696)
        			volumeButton.setX(event.getStageX());
        	}
        	
        	@Override
        	public boolean mouseMoved(InputEvent event, float x, float y) {
        		resetButtonChecked("volumeButton");
        		return super.mouseMoved(event, x, y);
        	}
        });
        
    }
    void resetButtonChecked(String check) {
    	homeButton.setChecked(false);
    	backButton.setChecked(false);
			resumeButton.setChecked(false);
		volumeButton.setChecked(false);
		musicButton.setChecked(false);
		sfxButton.setChecked(false);
		if (check.equals("homeButton")) homeButton.setChecked(true);
		else if (check.equals("backButton")) backButton.setChecked(true);
		else if (check.equals("resumeButton")) resumeButton.setChecked(true);
		else if (check.equals("volumeButton")) volumeButton.setChecked(true);
		else if (check.equals("musicButton")) musicButton.setChecked(true);
		else if (check.equals("sfxButton")) sfxButton.setChecked(true);
    }
    
    @Override
    public void dispose() {
        super.dispose();
    }
}