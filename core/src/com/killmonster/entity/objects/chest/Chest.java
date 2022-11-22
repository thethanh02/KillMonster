package com.killmonster.entity.objects.chest;

import java.util.HashMap;

import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.killmonster.GameWorldManager;
import com.killmonster.entity.objects.GameObject;
import com.killmonster.util.*;

public class Chest extends GameObject {

	private final static String TEXTURE_FILE = "objects/chest/chest.png";
	
	public Chest(GameWorldManager gameWorldManager, float x, float y) {
		super(gameWorldManager.getAssets().get(TEXTURE_FILE), gameWorldManager.getWorld(), x, y);
		
		name = "Key";
		
		bodyWidth = 8f;
		bodyHeight = 15f;
		offsetX = .48f;
		offsetY = .48f;
		
		// Create animations by extracting frames from the spritesheet.
		animation = new HashMap<>();
		animation.put(State.IDLE, 		Utils.createAnimation(getTexture(), 14f / Constants.PPM, 0, 0, 0, 96, 96));
		animation.put(State.HIT, 		Utils.createAnimation(getTexture(), 0f / Constants.PPM, 10, 10, 0, 96, 96));
		animation.put(State.DESTROYED, 	Utils.createAnimation(getTexture(), 14f / Constants.PPM, 1, 8, 0, 96, 96));
		
		short bodyCategoryBits = CategoryBits.DIAMOND;
		short bodyMaskBits = CategoryBits.GROUND | CategoryBits.PLAYER;
		
		super.defineBody(BodyType.StaticBody);
		super.createBodyFixture(bodyCategoryBits, bodyMaskBits);
		bodyFixture.setSensor(true);

		setBounds(0, 0, 96 / Constants.PPM, 96 / Constants.PPM);
		setRegion(animation.get(State.IDLE).getKeyFrame(stateTimer, true));
	}

}
