package com.killmonster.entity.objects;

import java.util.HashMap;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.killmonster.entity.character.Player;
import com.killmonster.util.CategoryBits;
import com.killmonster.util.Constants;
import com.killmonster.util.Utils;
import com.killmonster.util.box2d.BodyBuilder;

public abstract class Potion extends GameObject {

	protected int healthRegen;

	public Potion(Texture texture, World currentWorld, float x, float y) {
		super(texture, currentWorld, x, y);
		
		bodyBuilder = new BodyBuilder(currentWorld);
		
		// Create animations by extracting frames from the spritesheet.
		animation = new HashMap<>();
		animation.put(State.IDLE, 		Utils.createAnimation(getTexture(), 14f / Constants.PPM, 0, 6, 0, 0, 12, 16));
		animation.put(State.HIT, 		Utils.createAnimation(getTexture(), 0f / Constants.PPM, 7, 7, 0, 0, 12, 16));
		animation.put(State.DESTROYED, 	Utils.createAnimation(getTexture(), 0f / Constants.PPM, 7, 7, 0, 0, 12, 16));
		
	}
	
	@Override
	public void update(float delta) {
		super.update(delta);
	}
	
	// Create body and fixtures.
	protected void createBodyandFixturePotion() {
		short bodyCategoryBits = CategoryBits.POTION;
		short bodyMaskBits = CategoryBits.GROUND | CategoryBits.PLAYER;
		
		super.defineBody(BodyType.StaticBody);
		super.createBodyFixture(bodyCategoryBits, bodyMaskBits);
//		body.setGravityScale(0f);
		bodyFixture.setSensor(true);

		setBounds(0, 0, 12 / Constants.PPM, 16 / Constants.PPM);
		setRegion(animation.get(State.IDLE).getKeyFrame(stateTimer, true));
	}

	public void healing(Player c) {
		c.healed(healthRegen);
	}

}
