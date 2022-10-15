package com.killmonster.screens;

import com.killmonster.GameStateManager;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;

public class GameOverScreen extends AbstractScreen {
    
	private static final String SKIN_FILE = "Interface/Skin/medievania_skin.json";
	
	private Skin skin;
	
	public GameOverScreen(GameStateManager gsm) {
		super(gsm);
		
		skin = gsm.getAssets().get(SKIN_FILE);
		
		Table table = new Table();
		table.center();
		table.setFillParent(true);
		
		Label gameOverLabel = new Label("GAME OVER", skin);
		Label retryLabel = new Label("Click to retry", skin);
		
		table.add(gameOverLabel).expandX();
		table.row();
		table.add(retryLabel).expandX().padTop(10f);
		
		addActor(table);
	}

	public void handleInput(float dt) {
		if (Gdx.input.justTouched()) {
			gsm.showScreen(Screens.GAME);
			dispose();
		}
	}
    
	@Override
	public void render(float delta) {
		handleInput(delta);
		gsm.clearScreen();
		draw();
	}

	@Override
	public void resize(int width, int height) {
		getViewport().update(width, height);
	}
    
	@Override
	public void dispose() {
	super.dispose();
	skin.dispose();
	}

}
