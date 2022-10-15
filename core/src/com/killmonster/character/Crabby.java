package com.killmonster.character;

import com.killmonster.util.*;

import java.util.HashMap;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.World;

public class Crabby extends Enemy {
    
	private static final String TEXTURE_FILE = "character/crabby/Crabby.png";
	
	public Crabby(AssetManager assets, World world, float x, float y) {
		super(assets.get(TEXTURE_FILE), world, x, y);
		
		name = "Crabby";
		bodyWidth = 10;
		bodyHeight = 34;
		offsetX = .357f;
		offsetY = .158f;
		
		health = 100;
		movementSpeed = .25f;
		jumpHeight = 4.5f;
		attackForce = 1.2f;
		attackTime = 1.2f;
		attackRange = 14;
		attackDamage = 25;
		
		typeFixtureShape = "PolygonShape";
		
		// Knight stand animation.
		animation = new HashMap<>();
		animation.put(State.IDLE, 		Utils.createAnimation(getTexture(), 12f / Constants.PPM, 0, 8, 0, 0 * 32, 72, 32));
		animation.put(State.RUNNING, 	Utils.createAnimation(getTexture(), 12f / Constants.PPM, 0, 5, 0, 1 * 32, 72, 32));
		animation.put(State.JUMPING, 	Utils.createAnimation(getTexture(), 12f / Constants.PPM, 5, 7, 0, 0 * 32, 72, 32));
		animation.put(State.FALLING, 	Utils.createAnimation(getTexture(), 12f / Constants.PPM, 7, 7, 0, 0 * 32, 72, 32));
		animation.put(State.ATTACKING,  Utils.createAnimation(getTexture(), 20f / Constants.PPM, 0, 6, 0, 2 * 32, 72, 32));
		animation.put(State.KILLED, 	Utils.createAnimation(getTexture(), 24f / Constants.PPM, 0, 4, 0, 4 * 32, 72, 32));
		
		defineBody();
		
		setBounds(0, 0, 72 / Constants.PPM, 32 / Constants.PPM);
		setRegion(animation.get(State.IDLE).getKeyFrame(stateTimer, true));
		
		facingRight = false;
	}
    
	public void defineBody() {
		short bodyCategoryBits = CategoryBits.ENEMY;
		short bodyMaskBits = CategoryBits.GROUND | CategoryBits.PLATFORM | CategoryBits.WALL | CategoryBits.PLAYER | CategoryBits.MELEE_WEAPON | CategoryBits.CLIFF_MARKER;
		short feetMaskBits = CategoryBits.GROUND | CategoryBits.PLATFORM;
		short weaponMaskBits = CategoryBits.PLAYER | CategoryBits.OBJECT;
		
		b2body = bodyBuilder
				.type(BodyDef.BodyType.DynamicBody)
				.position(getX(), getY(), Constants.PPM)
				.buildBody();

		createBodyFixture(bodyCategoryBits, bodyMaskBits);
		createFeetFixture(feetMaskBits);
		createMeleeWeaponFixture(weaponMaskBits);
	}
    
	public void createBodyFixture(short categoryBits, short maskBits) {
		bodyFixture = bodyBuilder
				.newRectangleFixture(b2body.getPosition(), 12.3f, 11.5f, Constants.PPM)
				.categoryBits(categoryBits)
				.maskBits(maskBits)
				.setUserData(this)
				.buildFixture();
	}
    
	public void createFeetFixture(short maskBits) {
		Vector2[] feetPolyVertices = new Vector2[4];
		feetPolyVertices[0] =  new Vector2(-bodyWidth / 2 + 1, -bodyHeight / 2 + 3);
		feetPolyVertices[1] =  new Vector2(bodyWidth / 2 - 1, -bodyHeight / 2 + 3);
		feetPolyVertices[2] =  new Vector2(-bodyWidth / 2 + 1, -bodyHeight / 2 + 2);
		feetPolyVertices[3] =  new Vector2(bodyWidth / 2 - 1, -bodyHeight / 2 + 2);
		
		feetFixture = bodyBuilder
				.newPolygonFixture(feetPolyVertices, Constants.PPM)
				.categoryBits(CategoryBits.FEET)
				.maskBits(maskBits)
				.isSensor(true)
				.setUserData(this)
				.buildFixture();
	}

	protected void createMeleeWeaponFixture(short maskBits) {
		// Vector2 meleeAttackFixturePosition = new Vector2(attackRange, 0);

		meleeWeaponFixture = bodyBuilder
				.newRectangleFixture(b2body.getPosition(), 32f, 4f, Constants.PPM)
				.categoryBits(CategoryBits.MELEE_WEAPON)
				.maskBits(maskBits)
				.isSensor(true)
				.setUserData(this)
				.buildFixture();
	}
    
}