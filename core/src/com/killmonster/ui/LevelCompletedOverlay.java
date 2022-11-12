package com.killmonster.ui;

import com.killmonster.GameStateManager;
import com.killmonster.screens.MainGameScreen;
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

public class LevelCompletedOverlay extends Stage{
	
	private static final String URM_SKIN_FILE = "res/urm_button.json";
	private static final String TEXTURE_FILE = "res/completed_sprite.png";
	
	private Button resumeButton, homeButton;
	private Image transparentImage, background;
	
	public LevelCompletedOverlay(GameStateManager gsm) {
		
		Texture texture = gsm.getAssets().get(TEXTURE_FILE);
		Skin urmSkin = gsm.getAssets().get(URM_SKIN_FILE);
		
		
		// Define transparent image
		transparentImage = new Image(new TextureRegion(texture, 95, 0, 100, 100));
		transparentImage.setScale(Constants.V_WIDTH, Constants.V_HEIGHT);
		transparentImage.setColor(0, 0, 0, .6f);;
		
		Table tableTransparent = new Table();
		tableTransparent.setFillParent(true);
		tableTransparent.bottom().left();
		tableTransparent.add(transparentImage);
		
		// Define completed menu
		background = new Image(new TextureRegion(texture));
		Table tableBackground = new Table();
		tableBackground.setFillParent(true);
		tableBackground.add(background);
		
		// Define button
		resumeButton = new Button(urmSkin, "resume");
		homeButton = new Button(urmSkin, "home");
		
		
		handleInput();
		
		Table tableUrmButton = new Table();
		tableUrmButton.setFillParent(true);
		tableUrmButton.padTop(100f);
		
		tableUrmButton.add(homeButton);
		tableUrmButton.add(resumeButton).padLeft(40f);
		
		
		addActor(tableTransparent);
		addActor(tableBackground);
		addActor(tableUrmButton);
	}
    
	public void handleInput() {
		Gdx.input.setInputProcessor(this);
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
				Constants.COMPLETED = false;
				MainGameScreen.isNextLevel = true;
			}
			
			@Override
			public boolean mouseMoved(InputEvent event, float x, float y) {
				resetButtonChecked("resumeButton");
				return super.mouseMoved(event, x, y);
			}
		});
        
		homeButton.addListener(new ClickListener() {
			@Override
			public boolean mouseMoved(InputEvent event, float x, float y) {
				resetButtonChecked("homeButton");
				return super.mouseMoved(event, x, y);
			}
		});  
    }
    
	private void resetButtonChecked(String check) {
		homeButton.setChecked(false);
		resumeButton.setChecked(false);
		if (check.equals("homeButton")) homeButton.setChecked(true);
		else if (check.equals("resumeButton")) resumeButton.setChecked(true);
	}
    
	@Override
	public void dispose() {
		super.dispose();
	}
}
