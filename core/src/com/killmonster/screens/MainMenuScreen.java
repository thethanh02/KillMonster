package com.killmonster.screens;

import com.killmonster.KillMonster;
import com.killmonster.util.Constants;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener.ChangeEvent;
import com.badlogic.gdx.utils.Align;

public class MainMenuScreen extends AbstractScreen {
	
	private static final String SKIN_FILE = "res/button_atlas.json";
    private static final String BACKGROUND_TEXTURE_FILE = "interface/mainmenu_bg.png";
    
    private Button playButton, optionsButton, quitButton;
    private Texture backgroundTexture;
    
    public MainMenuScreen(KillMonster gsm) {
        super(gsm);
        
        backgroundTexture = gsm.getAssets().get(BACKGROUND_TEXTURE_FILE);
        Skin skin = gsm.getAssets().get(SKIN_FILE);
        
        playButton = new Button(skin, "play");
        optionsButton = new Button(skin, "options");
        quitButton = new Button(skin, "quit");
        
        Table table = new Table();
        table.setFillParent(true);
        
        handleInput();
        table.add(playButton).row();
        table.add(optionsButton).padTop(5f).row();
        table.add(quitButton).padTop(5f).row();
        
        addActor(table);
    }
    
    
    @Override
    public void render(float delta) {
    	
        gsm.clearScreen();
        gsm.getBatch().begin();
        gsm.getBatch().draw(backgroundTexture, 0, 0, Constants.V_WIDTH, Constants.V_HEIGHT);
        gsm.getBatch().end();	
        
        draw();
    }
    
    public void handleInput() {
    	
        playButton.addListener(new ClickListener() {
        	
        	@Override
        	public void clicked(InputEvent event, float x, float y) {
        		gsm.showScreen(Screens.GAME);
        	}
        	
        	@Override
        	public boolean mouseMoved(InputEvent event, float x, float y) {
        		playButton.setChecked(true);
        		return super.mouseMoved(event, x, y);
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
        backgroundTexture.dispose();
    }

}