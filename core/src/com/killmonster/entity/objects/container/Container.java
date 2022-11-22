package com.killmonster.entity.objects.container;

import java.util.HashMap;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.killmonster.entity.objects.GameObject;
import com.killmonster.util.*;
import com.killmonster.util.box2d.BodyBuilder;

public abstract class Container extends GameObject {

	public Container(Texture texture, World currentWorld, float x, float y) {
		super(texture, currentWorld, x, y);
		bodyBuilder = new BodyBuilder(currentWorld);
		
		// Create animations by extracting frames from the spritesheet.
		animation = new HashMap<>();
		animation.put(State.IDLE, 	 	Utils.createAnimation(getTexture(), 14f / Constants.PPM, 0, 0, 0, 40, 30));
		animation.put(State.HIT, 		Utils.createAnimation(getTexture(), 0f / Constants.PPM, 8, 8, 0, 40, 30));
		animation.put(State.DESTROYED, 	Utils.createAnimation(getTexture(), 15f / Constants.PPM, 0, 7, 0, 40, 30));
	}
	
	protected void defineBody() {
		BodyType type = BodyType.DynamicBody;
		short bodyCategoryBits = CategoryBits.CONTAINER;
		short bodyMaskBits = CategoryBits.GROUND | CategoryBits.MELEE_WEAPON;
		super.defineBody(type);
		super.createBodyFixture(bodyCategoryBits, bodyMaskBits);
	}

}
