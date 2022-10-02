package com.killmonster.screens;

import com.killmonster.KillMonster;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

public class MainMenuScreen extends AbstractScreen {
	
	private static final String SKIN_FILE = "res/button_atlas.json";
    private static final String BACKGROUND_TEXTURE_FILE = "interface/mainmenu_bg.png";
    
    private Button playButton, optionsButton, quitButton;
    private Image backgroundImage;
    
    public MainMenuScreen(KillMonster gsm) {
        super(gsm);
        
        Texture backgroundTexture = gsm.getAssets().get(BACKGROUND_TEXTURE_FILE);
        Skin skin = gsm.getAssets().get(SKIN_FILE);
        
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
        
        handleInput();
        tableButton.add(playButton).row();
        tableButton.add(optionsButton).padTop(10f).row();
        tableButton.add(quitButton).padTop(10f).row();
        
        addActor(tableButton);
    }
    
    
    @Override
    public void render(float delta) {
    	
        gsm.clearScreen();
        gsm.getBatch().begin();
        gsm.getBatch().end();	
        
        draw();
    }
    
    public void handleInput() {
    	
    	backgroundImage.addListener(new ClickListener() {
    		@Override
    		public boolean mouseMoved(InputEvent event, float x, float y) {
    			resetButtonChecked(false, false, false);
    			return super.mouseMoved(event, x, y);
    		}
    	});
    	
        playButton.addListener(new ClickListener() {
        	@Override
        	public void clicked(InputEvent event, float x, float y) {
        		gsm.showScreen(Screens.GAME);
        	}
        	
        	@Override
        	public boolean mouseMoved(InputEvent event, float x, float y) {
        		resetButtonChecked(true, false, false);
        		return super.mouseMoved(event, x, y);
        	}
        });
        
        optionsButton.addListener(new ClickListener() {
        	@Override
        	public void clicked(InputEvent event, float x, float y) {
        	}
        	
        	@Override
        	public boolean mouseMoved(InputEvent event, float x, float y) {
        		resetButtonChecked(false, true, false);
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
		optionsButton.setChecked(options);
		quitButton.setChecked(quit);
    }
    
    @Override
    public void dispose() {
        super.dispose();
    }

}