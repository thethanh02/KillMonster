package com.killmonster.character;

import com.killmonster.component.CharacterState;
import com.killmonster.util.*;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.World;

public class Crabby extends Enemy {
    
	private static final String TEXTURE_FILE = "character/crabby/Crabby.png";
	
	public Crabby(AssetManager assets, World world, float x, float y) {
		super(assets.get(TEXTURE_FILE), world, x, y);
		
		stats.name = "Crabby";
		stats.bodyWidth = 10;
		stats.bodyHeight = 34;
		stats.offsetX = .357f;
		stats.offsetY = .158f;
		
		stats.health = 100;
		stats.movementSpeed = .25f;
		stats.jumpHeight = 4.5f;
		stats.attackForce = 1.2f;
		stats.attackTime = 1.2f;
		stats.attackRange = 14;
		stats.attackDamage = 25;
		
		stats.typeFixtureShape = "PolygonShape";
		
		// Knight stand animation.
		animations.put(CharacterState.IDLE, 	Utils.createAnimation(sprite.sprite.getTexture(), 12f / Constants.PPM, 0, 8, 0, 0 * 32, 72, 32));
		animations.put(CharacterState.RUNNING, 	Utils.createAnimation(sprite.sprite.getTexture(), 12f / Constants.PPM, 0, 5, 0, 1 * 32, 72, 32));
		animations.put(CharacterState.JUMPING, 	Utils.createAnimation(sprite.sprite.getTexture(), 12f / Constants.PPM, 5, 7, 0, 0 * 32, 72, 32));
		animations.put(CharacterState.FALLING, 	Utils.createAnimation(sprite.sprite.getTexture(), 12f / Constants.PPM, 7, 7, 0, 0 * 32, 72, 32));
		animations.put(CharacterState.ATTACKING, Utils.createAnimation(sprite.sprite.getTexture(), 20f / Constants.PPM, 0, 6, 0, 2 * 32, 72, 32));
		animations.put(CharacterState.KILLED, 	Utils.createAnimation(sprite.sprite.getTexture(), 24f / Constants.PPM, 0, 4, 0, 4 * 32, 72, 32));
		
		defineBody();
		
		sprite.sprite.setBounds(0, 0, 72 / Constants.PPM, 32 / Constants.PPM);
		
		state.facingRight = false;
	}
    
	public void defineBody() {
		short bodyCategoryBits = CategoryBits.ENEMY;
		short bodyMaskBits = CategoryBits.GROUND | CategoryBits.PLATFORM | CategoryBits.WALL | CategoryBits.PLAYER | CategoryBits.MELEE_WEAPON | CategoryBits.CLIFF_MARKER;
		short feetMaskBits = CategoryBits.GROUND | CategoryBits.PLATFORM;
		short weaponMaskBits = CategoryBits.PLAYER | CategoryBits.OBJECT;
		
		body.body = body.bodyBuilder
				.type(BodyDef.BodyType.DynamicBody)
				.position(sprite.sprite.getX(), sprite.sprite.getY(), Constants.PPM)
				.buildBody();

		createBodyFixture(bodyCategoryBits, bodyMaskBits);
		createFeetFixture(feetMaskBits);
		createMeleeWeaponFixture(weaponMaskBits);
	}
    
	public void createBodyFixture(short categoryBits, short maskBits) {
		body.bodyFixture = body.bodyBuilder
				.newRectangleFixture(body.body.getPosition(), 12.3f, 11.5f, Constants.PPM)
				.categoryBits(categoryBits)
				.maskBits(maskBits)
				.setUserData(this)
				.buildFixture();
	}
    
	public void createFeetFixture(short maskBits) {
		Vector2[] feetPolyVertices = new Vector2[4];
		feetPolyVertices[0] =  new Vector2(-stats.bodyWidth / 2 + 1, -stats.bodyHeight / 2 + 3);
		feetPolyVertices[1] =  new Vector2(stats.bodyWidth / 2 - 1, -stats.bodyHeight / 2 + 3);
		feetPolyVertices[2] =  new Vector2(-stats.bodyWidth / 2 + 1, -stats.bodyHeight / 2 + 2);
		feetPolyVertices[3] =  new Vector2(stats.bodyWidth / 2 - 1, -stats.bodyHeight / 2 + 2);
		
		body.feetFixture = body.bodyBuilder
				.newPolygonFixture(feetPolyVertices, Constants.PPM)
				.categoryBits(CategoryBits.FEET)
				.maskBits(maskBits)
				.isSensor(true)
				.setUserData(this)
				.buildFixture();
	}

	public void createMeleeWeaponFixture(short maskBits) {
		// Vector2 meleeAttackFixturePosition = new Vector2(attackRange, 0);

		body.meleeWeaponFixture = body.bodyBuilder
				.newRectangleFixture(body.body.getPosition(), 32f, 4f, Constants.PPM)
				.categoryBits(CategoryBits.MELEE_WEAPON)
				.maskBits(maskBits)
				.isSensor(true)
				.setUserData(this)
				.buildFixture();
	}
    
}