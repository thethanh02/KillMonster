package com.killmonster.entity.objects;

import java.util.HashMap;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.killmonster.entity.character.Player;
import com.killmonster.util.CategoryBits;
import com.killmonster.util.Constants;
import com.killmonster.util.Utils;
import com.killmonster.util.box2d.BodyBuilder;

public class Potion extends GameObject {
	
	private final static String TEXTURE_FILE = "objects/blue_potion.png";
	private int healthRegen;
	
	public Potion(AssetManager assets, World world, float x, float y) {
		super(assets.get(TEXTURE_FILE), world, x, y);

		name = "Blue Potion";
		bodyWidth = 8f;
		bodyHeight = 14f;
		offsetX = .065f;
		offsetY = .067f;
		
		healthRegen = 10;
		
		bodyBuilder = new BodyBuilder(currentWorld);
		
		// Create animations by extracting frames from the spritesheet.
		animation = new HashMap<>();
		animation.put(State.IDLE, 		Utils.createAnimation(getTexture(), 14f / Constants.PPM, 0, 6, 0, 0, 12, 16));
		animation.put(State.DESTROYED, 	Utils.createAnimation(getTexture(), 14f / Constants.PPM, 7, 7, 0, 0, 12, 16));
		
		// Create body and fixtures.
		short bodyCategoryBits = CategoryBits.POTION;
		short bodyMaskBits = CategoryBits.GROUND | CategoryBits.PLAYER;
		
		super.defineBody(BodyType.StaticBody, bodyCategoryBits, bodyMaskBits);
		
		bodyFixture.setSensor(true);

		setBounds(0, 0, 12 / Constants.PPM, 16 / Constants.PPM);
		setRegion(animation.get(State.IDLE).getKeyFrame(stateTimer, true));
	}
	
	public void healing(Player c) {
		c.healed(healthRegen);
	}
	
	public void isPickedUp() {
		this.setToDestroy = true;
	}
	
	@Override
	public void dispose() {
		
	}
}
