package com.killmonster.entity.character;

import com.killmonster.entity.Entity;
import com.killmonster.util.Constants;
import com.killmonster.util.Utils;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.physics.box2d.World;

public abstract class Enemy extends Character {
	
	public Enemy(Texture texture, World world, float x, float y) {
		super(texture, world, x, y);
	}
    
	@Override
	public void update(float delta) {
		super.update(delta);
		if (setToDestroy || isHitted) return;
		
		if (isAlerted && hasLockedOnTarget()) {
			// Is the target within melee attack range?
			if (hasInRangeTarget()) {
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