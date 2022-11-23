package com.killmonster.screens;

import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.killmonster.GameStateManager;

public class TutorialScreen extends AbstractScreen {
    
	private static final String SKIN_BUTTON = "res/button_atlas.json";
	private static final String SKIN_FILE = "interface/skin/font_skin.json";
	private static final String BACKGROUND_MUSIC_FILE = "sound/menu.wav";
	private static final String BACKGROUND_IMAGE = "interface/field.png";
	
	private Music backgroundMusic;
	private Skin skin;
	private Button playButton;
	
	public TutorialScreen(GameStateManager gsm) {
		super(gsm);
		
		skin = gsm.getAssets().get(SKIN_FILE);
		backgroundMusic = gsm.getAssets().get(BACKGROUND_MUSIC_FILE);
		Skin skin1 = gsm.getAssets().get(SKIN_BUTTON);
		
		playButton = new Button(skin1, "play");
		Texture texture1 = gsm.getAssets().get(BACKGROUND_IMAGE);
		Texture aTexture = gsm.getAssets().get("interface/key/keya.png");
		Texture dTexture = gsm.getAssets().get("interface/key/keyd.png");
		Texture eTexture = gsm.getAssets().get("interface/key/keye.png");
		Texture jTexture = gsm.getAssets().get("interface/key/keyj.png");
		Texture kTexture = gsm.getAssets().get("interface/key/keyk.png");
		Texture leftTexture = gsm.getAssets().get("interface/key/keyleft.png");
		Texture rightTexture = gsm.getAssets().get("interface/key/keyright.png");
		Texture spaceTexture = gsm.getAssets().get("interface/key/keyspace.png");
		Image background = new Image(texture1);
		Image aImage = new Image(aTexture);
		Image dImage = new Image(dTexture);
		Image eImage = new Image(eTexture);
		Image jImage = new Image(jTexture);
		Image kImage = new Image(kTexture);
		Image leftImage = new Image(leftTexture);
		Image rightImage = new Image(rightTexture);
		Image spaceImage = new Image(spaceTexture);
		Label tutorialLabel = new Label("Tutorial", skin);
		Label missionLabel = new Label("Mission: kill all enemies and pick up diamond to complete level", skin);
		Label leftLabel = new Label("Move left", skin);
		Label rightLabel = new Label("Move right", skin);
		Label eLabel = new Label("Open chest", skin);
		Label jkLabel = new Label("Attack", skin);
		Label spaceLabel = new Label("Jump", skin);
		tutorialLabel.setFontScale(2f);
		missionLabel.setFontScale(1.7f);
		leftLabel.setFontScale(1.5f);
		rightLabel.setFontScale(1.5f);
		eLabel.setFontScale(1.5f);
		jkLabel.setFontScale(1.5f);
		spaceLabel.setFontScale(1.5f);
		
		Table tableBackground = new Table();
		tableBackground.setFillParent(true);
		tableBackground.bottom().left();
		tableBackground.add(background);
		
		Table table = new Table();
		table.setFillParent(true);
		table.top().center();
		
		table.add(aImage).padTop(-65); table.add(leftImage).padTop(-65).padLeft(10); table.add(leftLabel).padTop(-65).padLeft(10).row();
		table.add(dImage); table.add(rightImage).padLeft(10); table.add(rightLabel).padLeft(10).row();
		table.add(eImage).padTop(10); table.add(new Label("", skin)).padTop(10).padLeft(10); table.add(eLabel).padTop(10).padLeft(10).row();
		table.add(jImage).padTop(10); table.add(kImage).padTop(10).padLeft(10); table.add(jkLabel).padTop(10).padLeft(10).row();
		table.add(spaceImage).padTop(10);  table.add(new Label("", skin)).padTop(10).padLeft(10); table.add(spaceLabel).padTop(10).padLeft(10).row();
		
		Table table1 = new Table();
		table1.setFillParent(true);
		table1.top().center();
		table1.add(tutorialLabel).row();
		table1.add(missionLabel).padTop(310).row();
		table1.add(playButton).padBottom(20).padTop(10);
		
		addActor(tableBackground);
		addActor(table);
		addActor(table1);
		
		backgroundMusic.setLooping(false);
		backgroundMusic.play();
	}

	public void handleInput(float dt) {
		playButton.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				backgroundMusic.stop();
				gsm.showScreen(Screens.GAME);
				dispose();
			}
		});
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
