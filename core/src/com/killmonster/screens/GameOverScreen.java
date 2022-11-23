package com.killmonster.screens;

import com.killmonster.GameStateManager;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;

public class GameOverScreen extends AbstractScreen {
    
	private static final String SKIN_FILE = "interface/skin/font_skin.json";
	private static final String BACKGROUND_MUSIC_FILE = "sound/gameover.wav";
	
	private Music backgroundMusic;
	private Skin skin;
	
	public GameOverScreen(GameStateManager gsm) {
		super(gsm);
		
		skin = gsm.getAssets().get(SKIN_FILE);
		backgroundMusic = gsm.getAssets().get(BACKGROUND_MUSIC_FILE);
		
		Table table = new Table();
		table.center();
		table.setFillParent(true);
		
		Label gameOverLabel = new Label("GAME OVER", skin);
		gameOverLabel.setFontScale(1.7f);;
		Label scoreLabel = new Label("Score: "+MainGameScreen.currentScore, skin);
		scoreLabel.setFontScale(1.2f);;
		Label retryLabel = new Label("Click to retry", skin);
		Label escapeLabel = new Label("Press Escape to exit", skin);
		
		table.add(gameOverLabel).expandX();
		table.row();
		table.add(scoreLabel).expandX().padTop(10f);
		table.row();
		table.add(retryLabel).expandX().padTop(10f);
		table.row();
		table.add(escapeLabel).expandX().padTop(10f);
		
		addActor(table);
		
		backgroundMusic.setLooping(false);
		backgroundMusic.play();
	}

	public void handleInput(float dt) {
		if (Gdx.input.justTouched()) {
			backgroundMusic.stop();
			gsm.showScreen(Screens.GAME);
			dispose();
		} else if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE))
			Gdx.app.exit();
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
