package com.killmonster.ui;

import com.killmonster.GameStateManager;
import com.killmonster.KillMonster;
import com.killmonster.screens.Screens;
import com.killmonster.util.Constants;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;import com.badlogic.gdx.graphics.Color;
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
	
	private static final String SKIN_FILE = "res/button_atlas.json";
    private static final String TEXTURE_FILE = "res/pause_menu.png";
    
    private Button playButton, quitButton;
    Image transparentImage, background;
    
    private Table tableTransparent;
    private Table tablePause;
    private Table tableButton;
    
    public PauseOverlay(GameStateManager gsm) {
    	this.gsm = gsm;
        Gdx.input.setInputProcessor(this);
        
        Texture texture = gsm.getAssets().get(TEXTURE_FILE);
        Skin skin = gsm.getAssets().get(SKIN_FILE);
        
        // Define transparent image
        transparentImage = new Image(new TextureRegion(texture, 95, 0, 100, 100));
        transparentImage.setScale(Constants.V_WIDTH, Constants.V_HEIGHT);
        transparentImage.setColor(0, 0, 0, .6f);;
        
        tableTransparent = new Table();
        tableTransparent.setFillParent(true);
        tableTransparent.bottom().left();
        tableTransparent.add(transparentImage);
        
        // Define pause menu
        background = new Image(new TextureRegion(texture));
        
        tablePause = new Table();
        tablePause.setFillParent(true);
        tablePause.add(background);
        
        // Define button
        playButton = new Button(skin, "play");
        quitButton = new Button(skin, "quit");
        
        tableButton = new Table();
        tableButton.setFillParent(true);
        tableButton.padTop(250f);
        
        handleInput();
        tableButton.add(playButton).row();
        tableButton.add(quitButton).padTop(12f).row();
        
        
		addActor(tableTransparent);
		addActor(tablePause);
        addActor(tableButton);
    }
    
    public void handleInput() {
    	
    	transparentImage.addListener(new ClickListener() {
    		@Override
    		public boolean mouseMoved(InputEvent event, float x, float y) {
    			resetButtonChecked(false, false, false);
    			return super.mouseMoved(event, x, y);
    		}
    	});
    	
    	background.addListener(new ClickListener() {
    		@Override
    		public boolean mouseMoved(InputEvent event, float x, float y) {
    			resetButtonChecked(false, false, false);
    			return super.mouseMoved(event, x, y);
    		}
    	});
    	
        playButton.addListener(new ClickListener() {
        	@Override
        	public void clicked(InputEvent event, float x, float y) {
        		Constants.PAUSE = false;
        	}
        	
        	@Override
        	public boolean mouseMoved(InputEvent event, float x, float y) {
        		resetButtonChecked(true, false, false);
        		return super.mouseMoved(event, x, y);
        	}
        });
        
        quitButton.addListener(new ClickListener() {
        	@Override
        	public void clicked(InputEvent event, float x, float y) {
        		gsm.showScreen(Screens.GAME);
        		Gdx.app.exit();
        	}
        	
        	@Override
        	public boolean mouseMoved(InputEvent event, float x, float y) {
        		resetButtonChecked(false, false, true);
        		return super.mouseMoved(event, x, y);
        	}
        });
        
    }
    
    void resetButtonChecked(boolean play, boolean options, boolean quit) {
    	playButton.setChecked(play);
		quitButton.setChecked(quit);
    }
    
    @Override
    public void dispose() {
        super.dispose();
    }
}