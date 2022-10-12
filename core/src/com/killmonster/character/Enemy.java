package com.killmonster.character;

import com.killmonster.util.Constants;
import com.killmonster.util.Utils;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.physics.box2d.World;

public abstract class Enemy extends Character {

    public Enemy(Texture texture, World world, float x, float y) {
        super(texture, world, x, y);
    }
    
    @Override
    public void update(float delta) {
        super.update(delta);
        if (isSetToKill()) return;

        if (isAlerted && hasLockedOnTarget()) {
            // Is the target within melee attack range?
            if (hasInRangeTarget()) {
                // If yes, swing its weapon.
                swingWeapon();

                // If the target's heath reaches zero, unset lockedOnTarget and it will stop attacking.
                if (lockedOnTarget.isSetToKill()) {
                    lockedOnTarget = null;
                }
            } else {
                // If the target isn't within melee attack range, move toward it until it can be attacked.
                if (Utils.getDistance(b2body.getPosition().x, lockedOnTarget.b2body.getPosition().x) >= attackRange * 2 / Constants.PPM) {
                    getBehavioralModel().moveTowardTarget(lockedOnTarget);

                    // Jump if it gets stucked while moving toward the lockedOnTarget.
                    getBehavioralModel().jumpIfStucked(delta, .1f);
                }
            }
        } else {
            getBehavioralModel().moveRandomly(delta, 0, 5, 0, 5);
        }
    }

    @Override
    public void inflictDamage(Character c, int damage) {
        super.inflictDamage(c, damage);
        setInRangeTarget(null);
    }
    
    @Override
    public void receiveDamage(int damage) {
        super.receiveDamage(damage);
        isAlerted = true;
    }
    
}