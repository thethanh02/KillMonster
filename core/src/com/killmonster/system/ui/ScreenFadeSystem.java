package com.killmonster.system.ui;

import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.killmonster.event.GameEventManager;
import com.killmonster.event.GameEventType;
import com.killmonster.event.MapChangedEvent;
import com.killmonster.util.Utils;

public class ScreenFadeSystem extends EntitySystem {

	public static final float FADEIN_DURATION = .3f;
	public static final float FADEOUT_DURATION = .8f;
	
	private Stage mainGameStage;
	private final Image shade;
	
	public ScreenFadeSystem(Stage mainGameStage) {
		this.mainGameStage = mainGameStage;
		shade = new Image(new TextureRegion(Utils.getTexture()));
		shade.setSize(mainGameStage.getViewport().getScreenWidth(), mainGameStage.getViewport().getScreenHeight());;
		mainGameStage.addActor(shade);
	
		GameEventManager.getINSTANCE().addEventListener(GameEventType.MAP_COMPLETED, (MapChangedEvent e) -> {
			shade.setSize(e.getGameMap().getMapWidth(), e.getGameMap().getMapHeight());
		});
		shade.addAction(Actions.fadeOut(FADEOUT_DURATION));
	}
	
}
