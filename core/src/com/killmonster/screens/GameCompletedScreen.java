package com.killmonster.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.killmonster.GameStateManager;

public class GameCompletedScreen extends AbstractScreen {
	
	private static final String SKIN_FILE = "interface/skin/font_skin.json";
	private static final String BACKGROUND_MUSIC_FILE = "sound/lvlcompleted.wav";
	
	private Music backgroundMusic;
	private Skin skin;
	
	protected GameCompletedScreen(GameStateManager gsm) {
		super(gsm);
		
		skin = gsm.getAssets().get(SKIN_FILE);
		backgroundMusic = gsm.getAssets().get(BACKGROUND_MUSIC_FILE);
		
		Table table = new Table();
		table.center();
		table.setFillParent(true);
		
		Label gameCompletedLabel = new Label("GAME COMPLETED", skin);
		gameCompletedLabel.setFontScale(1.7f);
		Label congrateLabel = new Label("CONGRATULATION!", skin);
		congrateLabel.setFontScale(1.7f);
		Label scoreLabel = new Label("Score: "+MainGameScreen.currentScore, skin);
		Label retryLabel = new Label("Click to return Menu", skin);
		
		table.add(gameCompletedLabel).expandX();
		table.row();
		table.add(congrateLabel).expandX().padTop(10f);
		table.row();
		table.add(scoreLabel).expandX().padTop(10f);
		table.row();
		table.add(retryLabel).expandX().padTop(10f);
		
		addActor(table);
		
		backgroundMusic.setLooping(false);
		backgroundMusic.play();
	}
	
	public void handleInput(float dt) {
		if (Gdx.input.justTouched()) {
			backgroundMusic.stop();
			gsm.showScreen(Screens.MAIN_MENU);
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

}
