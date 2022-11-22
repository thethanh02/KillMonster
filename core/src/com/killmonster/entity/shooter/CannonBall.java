package com.killmonster.entity.shooter;

import java.util.HashMap;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.physics.box2d.World;
import com.killmonster.util.Constants;
import com.killmonster.util.Utils;

public class CannonBall extends Bullet {

	private static final String TEXTURE_FILE = "objects/shooter/cannon_ball.png";

	public CannonBall(AssetManager assets, World world, float x, float y, boolean moveRight) {
		super(assets.get(TEXTURE_FILE), world, x, y);
		
		name = "Cannon Ball";
		bodyWidth = 15f;
		bodyHeight = 15f;
		offsetX = .0775f;
		offsetY = .0775f;
		facingRight = moveRight;
		
		health = 1;
		attackDamage = 15;
		
		// Create animations by extracting frames from the spritesheet.
		animation = new HashMap<>();
		animation.put(State.IDLE, 	 	Utils.createAnimation(getTexture(), 1f / Constants.PPM, 0, 0, 0, 15, 15));
		animation.put(State.DESTROYED, 	Utils.createAnimation(getTexture(), 1f / Constants.PPM, 0, 0, 0, 15, 15));
		
		createBodyandFixtureBullet();
		setBounds(0, 0, bodyWidth / Constants.PPM, bodyWidth / Constants.PPM);
		setRegion(animation.get(State.IDLE).getKeyFrame(stateTimer, true));
	}
	
}
