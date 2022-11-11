package com.killmonster.objects;

import java.util.HashMap;

import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.killmonster.GameWorldManager;
import com.killmonster.character.Player;
import com.killmonster.util.CategoryBits;
import com.killmonster.util.Constants;
import com.killmonster.util.Utils;

public class Box extends GameObject {

	private static final String TEXTURE_FILE = "objects/objects_sprites.png";
	
	private GameWorldManager gameWorldManager;
	
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
		animation.put(State.DESTROYED, 	Utils.createAnimation(getTexture(), 24f / Constants.PPM, 0, 7, 0, 0, 40, 30));
		
		// Create body and fixtures.
		short bodyCategoryBits = CategoryBits.PLAYER;
		short bodyMaskBits = CategoryBits.GROUND | CategoryBits.WALL | CategoryBits.PLAYER;
		
		super.defineBody(BodyType.StaticBody, bodyCategoryBits, bodyMaskBits);
		
		setBounds(0, 0, 40 / Constants.PPM, 30 / Constants.PPM);
		setRegion(animation.get(State.IDLE).getKeyFrame(stateTimer, true));
	}
	
}
