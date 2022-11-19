package com.killmonster.entity.objects;

import java.util.HashMap;

import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.killmonster.GameWorldManager;
import com.killmonster.util.CategoryBits;
import com.killmonster.util.Constants;
import com.killmonster.util.Utils;

public class Box extends GameObject {

	private static final String TEXTURE_FILE = "objects/objects_sprites.png";
	
	public Box(GameWorldManager gameWorldManager, float x, float y) {
		super(gameWorldManager.getAssets().get(TEXTURE_FILE), gameWorldManager.getWorld(), x, y);
		
		name = "Box";
		bodyWidth = 25f;
		bodyHeight = 18f;
		offsetX = .195f;
		offsetY = .09f;
		
		health = 1;
		
		// Create animations by extracting frames from the spritesheet.
		animation = new HashMap<>();
		animation.put(State.IDLE, 	 	Utils.createAnimation(getTexture(), 14f / Constants.PPM, 0, 0, 0, 0, 40, 30));
		animation.put(State.HIT, 		Utils.createAnimation(getTexture(), 0f / Constants.PPM, 8, 8, 0, 0, 40, 30));
		animation.put(State.DESTROYED, 	Utils.createAnimation(getTexture(), 15f / Constants.PPM, 0, 7, 0, 0, 40, 30));
		
		// Create body and fixtures.
		defineBody();
		
//		bodyFixture.setSensor(true);
		
		setBounds(0, 0, 40 / Constants.PPM, 30 / Constants.PPM);
		setRegion(animation.get(State.IDLE).getKeyFrame(stateTimer, true));
	}
	
	private void defineBody() {
		BodyType type = BodyType.DynamicBody;
		short bodyCategoryBits = CategoryBits.BOX;
		short bodyMaskBits = CategoryBits.GROUND | CategoryBits.MELEE_WEAPON;
		super.defineBody(type);
		super.createBodyFixture(bodyCategoryBits, bodyMaskBits);
	}
}