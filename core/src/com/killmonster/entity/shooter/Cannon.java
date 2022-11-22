package com.killmonster.entity.shooter;

import java.util.HashMap;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.killmonster.GameWorldManager;
import com.killmonster.util.CategoryBits;
import com.killmonster.util.Constants;
import com.killmonster.util.Utils;

public class Cannon extends Shooter {

	private static final String TEXTURE_FILE = "objects/shooter/cannon_atlas.png";

	public Cannon(GameWorldManager gameWorldManager, float x, float y, boolean facingRight) {
		super(gameWorldManager.getAssets().get(TEXTURE_FILE), gameWorldManager.getWorld(), x, y);
		
		name = "Cannon";
		bodyWidth = 40f;
		bodyHeight = 26f;
		offsetX = .2f;
		offsetY = .15f;
		this.facingRight = facingRight;
		
		health = 1;
		isInvincible = true;
		cooldownTime = .4f;

		// Create animations by extracting frames from the spritesheet.
		animation = new HashMap<>();
		animation.put(State.IDLE, 	 	Utils.createAnimation(getTexture(), 1f / Constants.PPM, 0, 0, 0, 40, 26));
		animation.put(State.ATTACKING, 	Utils.createAnimation(getTexture(), 20f / Constants.PPM, 0, 6, 0, 40, 26));
		animation.put(State.HIT,	 	Utils.createAnimation(getTexture(), 1f / Constants.PPM, 0, 0, 0, 40, 26));
		animation.put(State.DESTROYED, 	Utils.createAnimation(getTexture(), 1f / Constants.PPM, 0, 0, 0, 40, 26));

		// Create body and fixtures.
		short bodyCategoryBits = CategoryBits.CANNON;
		short bodyMaskBits = CategoryBits.GROUND | CategoryBits.PLATFORM;
		super.defineBody(BodyType.DynamicBody, bodyCategoryBits, bodyMaskBits);

		setBounds(0, 0, 40 / Constants.PPM, 26 / Constants.PPM);
		setRegion(animation.get(State.ATTACKING).getKeyFrame(stateTimer, true));

	}
	
}
