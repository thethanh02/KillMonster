package com.killmonster.entity.shooter;

import java.util.HashMap;
import com.killmonster.GameWorldManager;
import com.killmonster.util.Constants;
import com.killmonster.util.Utils;

public class CannonBall extends Bullet {

	private static final String TEXTURE_FILE = "objects/cannon_ball.png";

	public CannonBall(GameWorldManager gameWorldManager, float x, float y) {
		super(gameWorldManager.getAssets().get(TEXTURE_FILE), gameWorldManager.getWorld(), x, y);
		
		name = "Cannon Ball";
		bodyWidth = 15f;
		bodyHeight = 15f;
		offsetX = .0775f;
		offsetY = .0775f;
		
		health = 1;
		
		// Create animations by extracting frames from the spritesheet.
		animation = new HashMap<>();
		animation.put(State.IDLE, 	 	Utils.createAnimation(getTexture(), 1f / Constants.PPM, 0, 0, 0, 0, 15, 15));
		animation.put(State.DESTROYED, 	Utils.createAnimation(getTexture(), 1f / Constants.PPM, 0, 0, 0, 0, 15, 15));
		
		createBodyandFixtureBullet();

	}
	
}
