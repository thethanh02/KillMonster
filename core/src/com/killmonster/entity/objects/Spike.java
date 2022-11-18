package com.killmonster.entity.objects;

import java.util.HashMap;

import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.killmonster.GameWorldManager;
import com.killmonster.util.CategoryBits;
import com.killmonster.util.Constants;
import com.killmonster.util.Utils;

public class Spike extends GameObject {
	
	private static final String TEXTURE_FILE = "objects/trap_atlas.png";
	
	public Spike(GameWorldManager gameWorldManager, float x, float y) {
		super(gameWorldManager.getAssets().get(TEXTURE_FILE), gameWorldManager.getWorld(), x, y);
		
		name = "Spike";
		bodyWidth = 32f;
		bodyHeight = 16f;
		offsetX = .16f;
		offsetY = .08f;
		
		health = 1;
		isInvincible = true;
		
		// Create animations by extracting frames from the spritesheet.
		animation = new HashMap<>();
		animation.put(State.IDLE, 	 	Utils.createAnimation(getTexture(), 1f / Constants.PPM, 0, 0, 0, 0, 32, 32));
		animation.put(State.HIT, 		Utils.createAnimation(getTexture(), 0f / Constants.PPM, 1, 1, 0, 0, 32, 32));
		animation.put(State.DESTROYED, 	Utils.createAnimation(getTexture(), 0f / Constants.PPM, 1, 1, 0, 0, 32, 32));
		
		// Create body and fixtures.
		short bodyCategoryBits = CategoryBits.DEATHPLACE;
		short bodyMaskBits = CategoryBits.GROUND | CategoryBits.PLAYER | CategoryBits.ENEMY;
		
		super.defineBody(BodyType.StaticBody);
		super.createBodyFixture(bodyCategoryBits, bodyMaskBits);
		
		setBounds(0, 0, 32 / Constants.PPM, 32 / Constants.PPM);
		setRegion(animation.get(State.IDLE).getKeyFrame(stateTimer, true));

	}
	
}
