package com.killmonster.ui;

import com.killmonster.GameStateManager;
import com.killmonster.util.Constants;
import com.killmonster.character.Player;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.viewport.FitViewport;

public class InventoryWindow extends Stage {
    
	private static int barLength = 50; // pixel
	
	private GameStateManager gameStateManager;
	private Player player;
	
	private Texture hudTexture;
	private TextureRegion barsBackground;
	private TextureRegion healthBar;
	private TextureRegion staminaBar;
	private TextureRegion magickaBar;
	
	private Image healthBarImage;
	private Image staminaBarImage;
	private Image magickaBarImage;
	
	private Table hudTable;
	private Table barTable;
	
	public InventoryWindow(GameStateManager gameStateManager, Player player) {
		super(new FitViewport(Constants.V_WIDTH, Constants.V_HEIGHT), gameStateManager.getBatch());
		this.gameStateManager = gameStateManager;
		this.player = player;
		
		// Initializes player hud Texture and TextureRegions.
		hudTexture = gameStateManager.getAssets().get("Interface/HUD/hud.png");
		barsBackground = new TextureRegion(hudTexture, 0, 4, 100, 32);
		healthBar = new TextureRegion(hudTexture, 0, 0, 1, 4);
		staminaBar = new TextureRegion(hudTexture, 1, 0, 1, 4);
		magickaBar = new TextureRegion(hudTexture, 2, 0, 1, 4);
		
		
		hudTable = new Table();
		hudTable.top().left();
		hudTable.setFillParent(true);
		
		hudTable.add(new Image(barsBackground)).padTop(20f).padLeft(20f);
		
		
		barTable = new Table();
		barTable.top().left();
		barTable.setFillParent(true);
		barTable.padTop(24f).padLeft(58f);
		
		healthBarImage = new Image(healthBar);
		staminaBarImage = new Image(staminaBar);
		magickaBarImage = new Image(magickaBar);
		
		healthBarImage.setScaleX(barLength);
		staminaBarImage.setScaleX(barLength);
		magickaBarImage.setScaleX(barLength);
		
		barTable.add(healthBarImage);
		barTable.row().padTop(6f);
		barTable.add(staminaBarImage);
		barTable.row().padTop(6f);
		barTable.add(magickaBarImage);
		
		addActor(hudTable);
		addActor(barTable);
	}
    
	public void update(float dt) {
		healthBarImage.setScaleX(barLength * player.getHealth() / 100f); // 100 is only temporary (player's full heatlh is 100)
	}

}