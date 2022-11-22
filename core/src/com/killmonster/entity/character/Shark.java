package com.killmonster.entity.character;

import java.util.HashMap;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.killmonster.entity.Entity;
import com.killmonster.util.*;

public class Shark extends Enemy {

	private static final String TEXTURE_FILE = "character/shark/Shark.png";
	
	public Shark(AssetManager assets, World world, float x, float y) {
		super(assets.get(TEXTURE_FILE), world, x, y);
		
		name = "Shark";
		bodyWidth = 21f;
		bodyHeight = 22f;
		offsetX = .16f;
		offsetY = .15f;
		
		health = 100;
		movementSpeed = .25f;
		jumpHeight = 4.5f;
		attackForce = 1.5f;
		attackRange = 10;
		attackDamage = 15;
		
		startHitTime = .4f;
		endHitTime = .6f;
		
		typeMeleeShape = "Enemy";
		
		// Knight stand animation.
		animation = new HashMap<>();
		animation.put(State.IDLE, 		Utils.createAnimation(getTexture(), 12f / Constants.PPM, 0, 7, 0 * 30, 34, 30));
		animation.put(State.RUNNING, 	Utils.createAnimation(getTexture(), 12f / Constants.PPM, 0, 5, 1 * 30, 34, 30));
		animation.put(State.JUMPING, 	Utils.createAnimation(getTexture(), 12f / Constants.PPM, 2, 3, 0 * 30, 34, 30));
		animation.put(State.FALLING, 	Utils.createAnimation(getTexture(), 12f / Constants.PPM, 4, 4, 0 * 30, 34, 30));
		animation.put(State.ATTACKING,  Utils.createAnimation(getTexture(), 10f / Constants.PPM, 0, 7, 2 * 30, 34, 30));
		animation.put(State.HIT, 		Utils.createAnimation(getTexture(), 12f / Constants.PPM, 0, 3, 3 * 30, 34, 30));
		animation.put(State.KILLED, 	Utils.createAnimation(getTexture(), 24f / Constants.PPM, 0, 4, 4 * 30, 34, 30));
		
		short bodyCategoryBits = CategoryBits.ENEMY;
		short bodyMaskBits = CategoryBits.GROUND | CategoryBits.PLATFORM | CategoryBits.WALL | CategoryBits.PLAYER | CategoryBits.MELEE_WEAPON | CategoryBits.CLIFF_MARKER | CategoryBits.DEATHPLACE;
		short feetMaskBits = CategoryBits.GROUND | CategoryBits.PLATFORM;
		short weaponMaskBits = CategoryBits.PLAYER;
		super.defineBody(BodyDef.BodyType.DynamicBody, bodyCategoryBits, bodyMaskBits, feetMaskBits, weaponMaskBits);
		
		facingRight = false;
		setBounds(0, 0, 34 / Constants.PPM, 30 / Constants.PPM);
		textureRegion = animation.get(State.IDLE).getKeyFrame(stateTimer, true);
		textureRegion.flip(true, false);
		setRegion(textureRegion);
	}
	
	@Override
	protected void createMeleeWeaponFixture(short maskBits) {
		Vector2 meleeAttackFixturePosition = new Vector2(0f, 0);

		meleeWeaponFixture = bodyBuilder
				.newCircleFixture(meleeAttackFixturePosition, attackRange, Constants.PPM)
				.categoryBits(CategoryBits.MELEE_WEAPON)
				.maskBits(maskBits)
				.isSensor(true)
				.setUserData(this)
				.buildFixture();
	}
	
	@Override
	public void swingWeapon() {
		if (!isAttacking) {
			isAttacking = true;
			isInflictDmg = false;
			if (facingRight)
				body.applyLinearImpulse(new Vector2(2f, 0f), body.getWorldCenter(), false);
			else 
				body.applyLinearImpulse(new Vector2(-2f, 0f), body.getWorldCenter(), false);
			for (Entity entity : inRangeTarget) {
				if (hasInRangeTarget() && !entity.isInvincible() && !entity.isSetToKill()) {
					this.lockedOnTarget.addAll(inRangeTarget);
					entity.setLockedOnTarget(this);
				}
			}
		} else if (!isInflictDmg && stateTimer >= startHitTime && stateTimer <= endHitTime) {
			for (Entity entity : inRangeTarget)
				if (hasInRangeTarget() && !entity.isInvincible() && !entity.isSetToKill()) 
					inflictDamage(entity, attackDamage);
				
			isInflictDmg = true;
		}
	}
}
