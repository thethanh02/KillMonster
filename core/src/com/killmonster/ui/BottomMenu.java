package com.killmonster.ui;


import com.killmonster.entity.character.Player;
import com.killmonster.GameStateManager;
import com.killmonster.util.Constants;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.FitViewport;

public class BottomMenu extends Stage {

	private GameStateManager gameStateManager;
	private Player player;
	
	private Texture bottomMenuTexture;
	private TextureRegion background;
	private TextureRegion button;
	private TextureRegion inventory;
	private TextureRegion slotTexture;
	private TextureRegion slotHoveredTexture;
	
	private Image inventoryButton;
	
	private Table table;
	private Table buttonTable;
	
	private Window inventoryWindow;
	
	public BottomMenu(GameStateManager gameStateManager, Player player) {
		super(new FitViewport(Constants.V_WIDTH, Constants.V_HEIGHT), gameStateManager.getBatch());
		this.gameStateManager = gameStateManager;
		this.player = player;
		
		// Initializes player hud Texture and TextureRegions.
		bottomMenuTexture = gameStateManager.getAssets().get("Interface/HUD/hud.png");
		background = new TextureRegion(bottomMenuTexture, 0, 36, 120, 23);
		button = new TextureRegion(bottomMenuTexture, 0, 59, 24, 15);
		inventory = new TextureRegion(bottomMenuTexture, 0, 83, 148, 182);
		slotTexture = new TextureRegion(bottomMenuTexture, 0, 265, 20, 20);
		slotHoveredTexture = new TextureRegion(bottomMenuTexture, 20, 265, 20, 20);
		
		
		table = new Table();
		table.center().bottom();
		table.setFillParent(true);
		
		table.add(new Image(background));
		
		
		buttonTable = new Table();
		buttonTable.center().bottom().padBottom(2f);
		buttonTable.setFillParent(true);
		
		inventoryButton = new Image(button);
		
		inventoryButton.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				inventoryWindow.setVisible((inventoryWindow.isVisible()) ? false : true);
			}
		});

		buttonTable.add(inventoryButton).pad(2f);
		buttonTable.add(new Image(button)).pad(2f);
		buttonTable.add(new Image(button)).pad(2f);
		buttonTable.add(new Image(button)).pad(2f);


		Window.WindowStyle windowStyle = new Window.WindowStyle(new BitmapFont(), Color.BLACK, new TextureRegionDrawable(inventory));
		inventoryWindow = new Window("", windowStyle);
		inventoryWindow.padTop(21f);
		inventoryWindow.top().left().padLeft(10f);
		inventoryWindow.setVisible(false);
		inventoryWindow.setPosition(Constants.V_WIDTH / 2 - inventoryWindow.getWidth() / 2, Constants.V_HEIGHT / 2 - inventoryWindow.getHeight() / 2);

		for (int i = 0; i < 4; i++) {
			for (int j = 0; j < 5; j++) {
				Image currentSlot = new Image(slotTexture);
				inventoryWindow.add(currentSlot).padRight(2.5f);

				currentSlot.addListener(new ClickListener() {
					@Override
					public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
						System.out.println("hover");
						currentSlot.setDrawable(new TextureRegionDrawable(slotHoveredTexture));
					}

					@Override
					public void exit(InputEvent event, float x, float y, int pointer, Actor fromActor) {
						currentSlot.setDrawable(new TextureRegionDrawable(slotTexture));
					}

					@Override
					public void clicked(InputEvent event, float x, float y) {
						System.out.println("wut");
					}
				});
			}
			inventoryWindow.row().padTop(2.5f);
		}

		addActor(table);
		addActor(buttonTable);
		addActor(inventoryWindow);
	}

	public void update(float delta) {
		Gdx.input.setInputProcessor(this);
		act();
	}
}