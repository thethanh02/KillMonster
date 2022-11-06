package com.killmonster.character;

import com.killmonster.util.Constants;
import com.killmonster.component.*;
import com.killmonster.util.CategoryBits;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.Queue;

public abstract class Character extends Entity implements Disposable {

	protected SpriteComponent sprite;
	protected AnimationComponent animations;
	protected BodyComponent body;
	protected StateComponent state;
	protected CharacterStatsComponent stats;
	protected CombatTargetComponent targets;
	
	protected BehavioralModel behavioralModel;
	protected Queue<Actor> damageIndicators; // not removing expired ones yet.
	
	public Character(Texture texture, World currentWorld, float x, float y) {
		stats = new CharacterStatsComponent();
		animations = new AnimationComponent();
		body = new BodyComponent(currentWorld);
		sprite = new SpriteComponent(texture, x, y);
		state = new StateComponent(CharacterState.IDLE); 
		targets = new CombatTargetComponent();
		
		add(stats);
		add(animations);
		add(body);
		add(sprite);
		add(state);
		add(targets);
		
		behavioralModel = new BehavioralModel(this);
		damageIndicators = new Queue<>();
	}

	protected void defineBody(BodyDef.BodyType type, short bodyCategoryBits, short bodyMaskBits, short feetMaskBits, short meleeWeaponMaskBits) {
		body.body = body.bodyBuilder.type(type)
				.position(sprite.sprite.getX(), sprite.sprite.getY(), Constants.PPM)
				.buildBody();

		createBodyFixture(bodyCategoryBits, bodyMaskBits);
		createFeetFixture(feetMaskBits);
		createMeleeWeaponFixture(meleeWeaponMaskBits);
	}

	protected void createBodyFixture(short categoryBits, short maskBits) {
		body.bodyFixture = body.bodyBuilder
				.newRectangleFixture(body.body.getPosition(), stats.bodyWidth / 2, stats.bodyHeight / 2, Constants.PPM)
				.categoryBits(categoryBits)
				.maskBits(maskBits)
				.setUserData(this)
				.buildFixture();
	}

	protected void createFeetFixture(short maskBits) {
		Vector2[] feetPolyVertices = new Vector2[4];
		feetPolyVertices[0] =  new Vector2(-stats.bodyWidth / 2 + 1, -stats.bodyHeight / 2);
		feetPolyVertices[1] =  new Vector2(stats.bodyWidth / 2 - 1, -stats.bodyHeight / 2);
		feetPolyVertices[2] =  new Vector2(-stats.bodyWidth / 2 + 1, -stats.bodyHeight / 2 - 2);
		feetPolyVertices[3] =  new Vector2(stats.bodyWidth / 2 - 1, -stats.bodyHeight / 2 - 2);

		body.feetFixture = body.bodyBuilder
				.newPolygonFixture(feetPolyVertices, Constants.PPM)
				.categoryBits(CategoryBits.FEET)
				.maskBits(maskBits)
				.isSensor(true)
				.setUserData(this)
				.buildFixture();
	}

	protected void createMeleeWeaponFixture(short maskBits) {
		Vector2 meleeAttackFixturePosition = new Vector2(stats.attackRange, 0);

		body.meleeWeaponFixture = body.bodyBuilder
				.newCircleFixture(meleeAttackFixturePosition, stats.attackRange, Constants.PPM)
				.categoryBits(CategoryBits.MELEE_WEAPON)
				.maskBits(maskBits)
				.isSensor(true)
				.setUserData(this)
				.buildFixture();
	}


	public void moveLeft() {
		state.facingRight = false;

		if (body.body.getLinearVelocity().x >= -stats.movementSpeed * 2) {
			body.body.applyLinearImpulse(new Vector2(-stats.movementSpeed, 0), body.body.getWorldCenter(), true);
		}
	}
    
	public void moveRight() {
		state.facingRight = true;

		if (body.body.getLinearVelocity().x <= stats.movementSpeed * 2) {
			body.body.applyLinearImpulse(new Vector2(stats.movementSpeed, 0), body.body.getWorldCenter(), true);
		}
	}
    
	public void jump() {
		if (!state.isJumping) {
			state.isJumping = true;

			getBody().applyLinearImpulse(new Vector2(0, stats.jumpHeight), body.body.getWorldCenter(), true);
		}
	}

	public void jumpDown() {
		if (state.isOnPlatform) {
			state.isOnPlatform = false;
			body.body.setTransform(body.body.getPosition().x, body.body.getPosition().y - 8f / Constants.PPM, 0);
		}
	}

	public void swingWeapon() {
		if (!state.isAttacking) {
			state.isAttacking = true;

			if (targets.hasInRangeTarget() && !targets.inRangeTarget.state.isInvincible() && !targets.inRangeTarget.state.isSetToKill()) {
				setLockedOnTarget(targets.inRangeTarget);
				targets.inRangeTarget.setLockedOnTarget(this);

				inflictDamage(targets.inRangeTarget, stats.attackDamage);
			}

			return;
		}
	}

	public void inflictDamage(Character c, int damage) {
		c.receiveDamage(damage);
		c.knockedBack((state.facingRight) ? stats.attackForce : -stats.attackForce);
	}

	public void receiveDamage(int damage) {
		if (!state.isInvincible) {
			stats.health -= damage;

			if (stats.health <= 0) {
				setCategoryBits(body.bodyFixture, CategoryBits.DESTROYED);
				state.setToKill = true;
			} else {
			}
		}
	}

	public void knockedBack(float force) {
		body.body.applyLinearImpulse(new Vector2(force, 1f), body.body.getWorldCenter(), true);
	}

	public static void setCategoryBits(Fixture f, short bits) {
		Filter filter = new Filter();
		filter.categoryBits = bits;
		f.setFilterData(filter);
	}


	// Review the code below.
	public Body getBody() {
		return body.body;
	}

	public String getName() {
		return stats.name;
	}
    
	public int getHealth() {
		return stats.health;
	}
    
	public void setIsJumping(boolean isJumping) {
		state.isJumping = isJumping;
	}

	public void setIsOnPlatform(boolean isOnPlatform) {
		state.isOnPlatform = isOnPlatform;
	}
    
	public void setLockedOnTarget(Character enemy) {
		targets.lockedOnTarget = enemy;
	}
	
	public void setInRangeTarget(Character enemy) {
		targets.inRangeTarget = enemy;
	}
	
	public BehavioralModel getBehavioralModel() {
		return behavioralModel;
	}
	
	public Queue<Actor> getDamageIndicators() {
		return damageIndicators;
	}
	
	@Override
	public void dispose() {
	}
	
	@Override
	public String toString() {
		return stats.name;
	}

	public boolean getStateIsKilled() {
		return state.isKilled;
	}
    
}