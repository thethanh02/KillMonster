package com.killmonster.system.ui;

import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.killmonster.ui.HUD;

public class HUDSystem extends EntitySystem {
	
	private final Batch batch;
	private final HUD hud;
	
	public HUDSystem(Batch batch, HUD hud) {
		super();
		this.batch = batch;
		this.hud = hud;
	}
	
	@Override
	public void update(float deltaTime) {
		batch.setProjectionMatrix(hud.getCamera().combined);
		hud.update(deltaTime);
		hud.draw();
	}
}
