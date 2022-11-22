package com.killmonster.entity.shooter;

import java.util.HashMap;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.physics.box2d.World;
import com.killmonster.util.*;

public class Pearl extends Bullet {

	private static final String TEXTURE_FILE = "objects/shooter/seashell.png";

	public Pearl(AssetManager assets, World world, float x, float y, boolean moveRight) {
		super(assets.get(TEXTURE_FILE), world, x, y);
		
		name = "Pearl";
		bodyWidth = 7f;
		bodyHeight = 7f;
		offsetX = .485f;
		offsetY = .42f;
		facingRight = moveRight;
		
		health = 1;
		attackDamage = 10;
		
		// Create animations by extracting frames from the spritesheet.
		animation = new HashMap<>();
		animation.put(State.IDLE, 	 	Utils.createAnimation(getTexture(), 1f / Constants.PPM, 24, 24, 0, 96, 96));
		animation.put(State.DESTROYED, 	Utils.createAnimation(getTexture(), 1f / Constants.PPM, 25, 26, 0, 96, 96));
		
		createBodyandFixtureBullet();
		setBounds(0, 0, 96 / Constants.PPM, 96 / Constants.PPM);
		setRegion(animation.get(State.IDLE).getKeyFrame(stateTimer, true));
	}
	
}