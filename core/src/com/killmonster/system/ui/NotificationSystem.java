package com.killmonster.system.ui;

import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.killmonster.ui.NotificationArea;

public class NotificationSystem extends EntitySystem {
	
	private Batch batch;
	private final NotificationArea notificationArea;
	
	public NotificationSystem(Batch batch, NotificationArea notificationArea) {
		super();
		this.batch = batch;
		this.notificationArea = notificationArea;
	}
	
	@Override
	public void update(float deltaTime) {
		batch.setProjectionMatrix(notificationArea.getCamera().combined);
		notificationArea.update(deltaTime);
		notificationArea.draw();
	}
	
}
