package com.killmonster.system.ui;

import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.killmonster.event.GameEventManager;
import com.killmonster.event.GameEventType;
import com.killmonster.event.MainGameScreenResizeEvent;
import com.killmonster.ui.DamageIndicator;

public class DamageIndicatorSystem extends EntitySystem {
	
	private final Batch batch;
	private final DamageIndicator damageIndicator;
	
	public DamageIndicatorSystem(Batch batch, DamageIndicator damageIndicator) {
		super();
		this.batch = batch;
		this.damageIndicator = damageIndicator;
		
		GameEventManager.getINSTANCE().addEventListener(GameEventType.MAINGAME_SCREEN_RESIZED, (MainGameScreenResizeEvent e) -> {
			damageIndicator.getViewport().update(e.getViewportWidth(), e.getViewportHeight());
		});
	}
	
	@Override
	public void update(float deltaTime) {
		batch.setProjectionMatrix(damageIndicator.getCamera().combined);
		damageIndicator.update(deltaTime);
		damageIndicator.draw();
	}
	
}
