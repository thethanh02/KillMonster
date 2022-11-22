package com.killmonster.ui;

import com.killmonster.entity.character.Player;
import com.killmonster.screens.MainGameScreen;
import com.killmonster.GameStateManager;
import com.killmonster.util.Constants;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.viewport.FitViewport;

public class HUD extends Stage {
	
	private static final String SKIN_FILE = "interface/skin/font_skin.json";
	private static int healthLength = 150; // pixel
	private static int powerLength = 104; // pixel
    
	private Player player;
	
	private Texture hudTexture;
	private TextureRegion barsBackground;
	private TextureRegion healthBar;
	private TextureRegion staminaBar;
	   
	private Image healthBarImage;
	private Image staminaBarImage;
	private Label scoreLabel;  
	
	private Table hudTable;
	private Table barTable;
	private Table scoreTable;
	    
	public HUD(GameStateManager gsm, Player player) {
		super(new FitViewport(Constants.V_WIDTH, Constants.V_HEIGHT), gsm.getBatch());
		this.player = player;
		
		// Initializes player hud Texture and TextureRegions.
		Skin skin = gsm.getAssets().get(SKIN_FILE);
		hudTexture = gsm.getAssets().get("res/health_power_bar.png");
		barsBackground = new TextureRegion(hudTexture);
		healthBar = new TextureRegion(hudTexture, 10, 8, 1, 4);
		staminaBar = new TextureRegion(hudTexture, 32, 36, 1, 2);
		
		healthBarImage = new Image(healthBar);
		staminaBarImage = new Image(staminaBar);
		healthBarImage.setScaleX(healthLength);
		staminaBarImage.setScaleX(powerLength);
		
		scoreLabel = new Label("Score: 0", skin);
		scoreLabel.setFontScale(1.2f);
		    
		hudTable = new Table();
		hudTable.top().left();
		hudTable.setFillParent(true);
		hudTable.add(new Image(barsBackground)).padTop(20f).padLeft(20f);
		    
		    
		barTable = new Table();
		barTable.top().left();
		barTable.setFillParent(true);
		barTable.padTop(34f).padLeft(45f);
		barTable.add(healthBarImage);
		barTable.row().padTop(16f).padLeft(19f);
		barTable.add(staminaBarImage);
		   
		scoreTable = new Table();
		scoreTable.center().top();
		scoreTable.setFillParent(true);
		scoreTable.add(scoreLabel).expandX();
		
		addActor(hudTable);
		addActor(barTable);
		addActor(scoreTable);
	}
    
    
	public void update(float delta) {
		scoreLabel.setText("Score: "+MainGameScreen.currentScore);
		healthBarImage.setScaleX(healthLength * player.getHealth() / 100f); // 100 is only temporary (player's full heatlh is 100)
	}

}