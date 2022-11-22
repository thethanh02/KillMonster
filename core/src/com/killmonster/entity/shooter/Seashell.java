package com.killmonster.entity.shooter;

import java.util.HashMap;

import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.killmonster.GameWorldManager;
import com.killmonster.util.*;

public class Seashell extends Shooter {

	private static final String TEXTURE_FILE = "objects/shooter/seashell.png";
	private GameWorldManager gwm;
	
	public Seashell(GameWorldManager gameWorldManager, float x, float y, boolean facingRight) {
		super(gameWorldManager.getAssets().get(TEXTURE_FILE), gameWorldManager.getWorld(), x, y);
		gwm = gameWorldManager;
		
		name = "Seashell";
		bodyWidth = 32f;
		bodyHeight = 21f;
		offsetX = .47f;
		offsetY = .44f;
		this.facingRight = facingRight;
		
		health = 75;
		cooldownTime = .4f;
		attackTimeLoop = 1.2f;

		// Create animations by extracting frames from the spritesheet.
		animation = new HashMap<>();
		animation.put(State.IDLE, 	 	Utils.createAnimation(getTexture(), 1f / Constants.PPM, 0, 0, 0, 96, 96));
		animation.put(State.ATTACKING, 	Utils.createAnimation(getTexture(), 20f / Constants.PPM, 1, 6, 0, 96, 96));
		animation.put(State.HIT,	 	Utils.createAnimation(getTexture(), 16f / Constants.PPM, 18, 21, 0, 96, 96));
		animation.put(State.DESTROYED, 	Utils.createAnimation(getTexture(), 16f / Constants.PPM, 22, 23, 0, 96, 96));

		// Create body and fixtures.
		short bodyCategoryBits = CategoryBits.SHOOTER;
		short bodyMaskBits = CategoryBits.GROUND | CategoryBits.MELEE_WEAPON;
		super.defineBody(BodyType.DynamicBody, bodyCategoryBits, bodyMaskBits);

		setBounds(0, 0, 96 / Constants.PPM, 96 / Constants.PPM);
		setRegion(animation.get(State.ATTACKING).getKeyFrame(stateTimer, true));

	}
	
	@Override
	public Bullet spawnBullet() {
		if (!facingRight)
			return new Pearl(gwm.getAssets(), gwm.getWorld(), body.getPosition().x * Constants.PPM - 13f, body.getPosition().y * Constants.PPM, false);
		else
			return new Pearl(gwm.getAssets(), gwm.getWorld(), body.getPosition().x * Constants.PPM + 13f, body.getPosition().y * Constants.PPM, true);
	}
}