package com.killmonster.entity.character;

import com.killmonster.entity.Entity;
import com.killmonster.util.Constants;
import com.killmonster.util.Utils;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.physics.box2d.World;

public abstract class Enemy extends Character {
	
	private boolean hasInRangeTarget;
	
	public Enemy(Texture texture, World world, float x, float y) {
		super(texture, world, x, y);
	}
    
	@Override
	public void update(float delta) {
		super.update(delta);
		if (setToDestroy || isHitted) return;
	}
	
	protected void AIBehavior1(float delta) {
		if (isAlerted && hasLockedOnTarget()) {
			// Is the target within melee attack range?
			if (hasInRangeAttack()) {
				// If yes, swing its weapon.
				swingWeapon();

				// If the target's heath reaches zero, unset lockedOnTarget and it will stop attacking.
				for (Entity entity : lockedOnTarget) {
					if (entity.isSetToKill()) {
						lockedOnTarget.removeValue(entity, false);
					}
				}
			} else {
				// If the target isn't within melee attack range, move toward it until it can be attacked.
				for (Entity entity : lockedOnTarget) {
					if (Utils.getDistance(body.getPosition().x, entity.getBody().getPosition().x) >= attackRange * 2 / Constants.PPM) {
						behavioralModel.moveTowardTarget((Character) entity);
	
						// Jump if it gets stucked while moving toward the lockedOnTarget.
						behavioralModel.jumpIfStucked(delta, .1f);
					}
				}
			}
		} else {
			behavioralModel.moveRandomly(delta, 0, 5, 0, 5);
		}
	}
	
	protected void AIBehavior2(float delta) {
		if (hasInRangeTarget) {
			swingWeapon2();
		} else if (hasLockedOnTarget()) {
			for (Entity entity : lockedOnTarget) {
				if (Utils.getDistance(body.getPosition().x, entity.getBody().getPosition().x) >= attackRange * 2 / Constants.PPM) {
					behavioralModel.moveTowardTarget((Character) entity);
				}
			}
		} else {
			behavioralModel.moveRandomly(delta, 0, 5, 0, 5);
		}
	}
	
	public void swingWeapon2() {
		if (!isAttacking) {
			isAttacking = true;
			isInflictDmg = false;
		} else if (!isInflictDmg && stateTimer >= startHitTime && stateTimer <= endHitTime) {
			for (Entity entity : inRangeAttack)
				if (hasInRangeAttack() && !entity.isInvincible() && !entity.isSetToKill()) 
					inflictDamage(entity, attackDamage);
			isInflictDmg = true;
		}
	}
	
	public void setInRangeTarget(boolean check) {
		hasInRangeTarget = check;
	}

	@Override
	public void inflictDamage(Entity c, int damage) {
		super.inflictDamage(c, damage);
		removeInRangeTarget(c);
	}
    
	@Override
	public void receiveDamage(int damage) {
		super.receiveDamage(damage);
		isAlerted = true;
	}

}