package com.killmonster.entity.objects.diamond;

import java.util.HashMap;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.killmonster.entity.character.Player;
import com.killmonster.entity.objects.GameObject;
import com.killmonster.util.*;

public abstract class Treasure extends GameObject {
	
	protected int scorePoint;

	public Treasure(Texture texture, World currentWorld, float x, float y) {
		super(texture, currentWorld, x, y);
		
		// Create animations by extracting frames from the spritesheet.
		animation = new HashMap<>();
		animation.put(State.IDLE, 		Utils.createAnimation(getTexture(), 14f / Constants.PPM, 0, 3, 0, 64, 64));
		animation.put(State.HIT, 		Utils.createAnimation(getTexture(), 0f / Constants.PPM, 7, 7, 0, 64, 64));
		animation.put(State.DESTROYED, 	Utils.createAnimation(getTexture(), 0f / Constants.PPM, 7, 7, 0, 64, 64));
		
	}
	
	// Create body and fixtures.
	protected void createBodyandFixturePotion() {
		short bodyCategoryBits = CategoryBits.DIAMOND;
		short bodyMaskBits = CategoryBits.GROUND | CategoryBits.PLAYER;
		
		super.defineBody(BodyType.StaticBody);
		super.createBodyFixture(bodyCategoryBits, bodyMaskBits);
//		body.setGravityScale(0f);
		bodyFixture.setSensor(true);

		setBounds(0, 0, 64 / Constants.PPM, 64 / Constants.PPM);
		setRegion(animation.get(State.IDLE).getKeyFrame(stateTimer, true));
	}

	public void increaseScorePoint(Player c) {
		c.receiveScore(scorePoint);
	}

}
