package com.killmonster.entity.character;

import com.killmonster.util.Constants;
import com.killmonster.entity.Entity;
import com.killmonster.util.CategoryBits;
import java.util.Map;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;

public abstract class Character extends Entity {

	public enum State { IDLE, RUNNING, JUMPING, FALLING, HIT, ATTACKING, KILLED, ATTACK2 };
	
	protected Map<State, Animation<TextureRegion>> animation;
	
	protected State currentState;
	protected State previousState;

	protected Fixture meleeWeaponFixture;
	protected Fixture feetFixture;
	protected String typeMeleeShape;
	
	protected Sound deathSound;
	protected Sound attackSound;
	protected Sound jumpSound;
	protected float volume;
	protected boolean isMute;
	
	protected boolean isAlerted;
	protected boolean isJumping;
	protected boolean isAttacking;
	protected boolean isAttacking2;
	
	protected float movementSpeed;
	protected float jumpHeight;
	protected float attackForce;
	protected int attackRange;
	protected int attackDamage;
	protected int attackPosX;
	
	protected float startHitTime;
	protected float endHitTime;
	protected boolean isInflictDmg;
	protected int stamina;
	
	protected BehavioralModel behavioralModel;
	
	public Character(Texture texture, World currentWorld, float x, float y) {
		super(texture, currentWorld, x, y);
		behavioralModel = new BehavioralModel(this);
		
		currentState = State.IDLE;
		previousState = State.IDLE;
		facingRight = true;
		volume = .5f;
	}
    
	@Override
	public void update(float delta) {
		if (!isDestroyed) {
			// If the character's health has reached zero but hasn't die yet,
			// it means that the killedAnimation is not fully played.
			// So here we'll play it until it's finished.
			setRegion(getFrame(delta));
			if (setToDestroy) {
				// Set killed to true to prevent further rendering updates.
				if (animation.get(State.KILLED).isAnimationFinished(stateTimer)) {
					currentWorld.destroyBody(body);
					isDestroyed = true;
				}
			} else if (isHitted) {
				// Set isHitted back to false, implying hit has complete.
				if (animation.get(State.HIT).isAnimationFinished(stateTimer)) {
					isHitted = false;
					stateTimer = 0;
				}
			} else {
				// Set isAttacking back to false, implying attack has complete.
				if (animation.get(State.ATTACKING).isAnimationFinished(stateTimer)) {
					isAttacking = false;
					stateTimer = 0;
				} else if (!isInflictDmg && stateTimer >= startHitTime && stateTimer <= endHitTime) {
					for (Entity entity : inRangeAttack)
						if (hasInRangeAttack() && !entity.isInvincible() && !entity.isSetToKill()) 
							inflictDamage(entity, attackDamage);
					isInflictDmg = true;
				}
			} 

			float textureX = body.getPosition().x - offsetX;
			float textureY = body.getPosition().y - offsetY;
			setPosition(textureX, textureY);
		}
	}
    
	protected TextureRegion getFrame(float delta) {
		previousState = currentState;
		currentState = getState();
		
		switch (currentState) {
			case RUNNING:
				textureRegion = animation.get(State.RUNNING).getKeyFrame(stateTimer, true);
				break;
			case JUMPING:
				textureRegion = animation.get(State.JUMPING).getKeyFrame(stateTimer, false);
				break;
			case FALLING:
				textureRegion = animation.get(State.FALLING).getKeyFrame(stateTimer, true);
				break;
			case ATTACK2:
				textureRegion = animation.get(State.ATTACK2).getKeyFrame(stateTimer, false);
				break;
			case ATTACKING:
				textureRegion = animation.get(State.ATTACKING).getKeyFrame(stateTimer, false);
				break;
			case HIT:
				textureRegion = animation.get(State.HIT).getKeyFrame(stateTimer, false);
				break;
			case KILLED:
				textureRegion = animation.get(State.KILLED).getKeyFrame(stateTimer, false);
				break;
			case IDLE:
			default:
				textureRegion = animation.get(State.IDLE).getKeyFrame(stateTimer, true);
				break;
		}
        
		updateWeaponFixture();
        
		stateTimer = (currentState != previousState) ? 0 : stateTimer + delta;
		return textureRegion;
	}
	
	// default is fixture most of enemy
	protected void updateWeaponFixture() {
		if (!facingRight && textureRegion.isFlipX()) {
			textureRegion.flip(true, false);
			CircleShape shape = (CircleShape) meleeWeaponFixture.getShape();
			shape.setPosition(new Vector2(-attackPosX / Constants.PPM, 0));
		} else if (facingRight && !textureRegion.isFlipX()) {
			textureRegion.flip(true, false);
			CircleShape shape = (CircleShape) meleeWeaponFixture.getShape();
			shape.setPosition(new Vector2(attackPosX / Constants.PPM, 0));
		} 
	}
	
	protected State getState() {
		if (setToDestroy) {
			return State.KILLED;
		} else if (isHitted) {
			return State.HIT;
		} else if (isAttacking2) {
			return State.ATTACK2;
		} else if (isAttacking) {
			return State.ATTACKING;
		} else if (isJumping && body.getLinearVelocity().y < -.01f) {
			return State.FALLING;
		} else if (body.getLinearVelocity().y > .01f) {
			return State.JUMPING;
		} else if (body.getLinearVelocity().x > .01f || body.getLinearVelocity().x < -.01f) {
			return State.RUNNING;
		} else {
			return State.IDLE;
		}
	}

	protected void defineBody(BodyDef.BodyType type, short bodyCategoryBits, short bodyMaskBits, short feetMaskBits, short meleeWeaponMaskBits) {
		super.defineBody(type);
		super.createBodyFixture(bodyCategoryBits, bodyMaskBits);
		createFeetFixture(feetMaskBits);
		createMeleeWeaponFixture(meleeWeaponMaskBits);
	}

	protected void createFeetFixture(short maskBits) {
		Vector2[] feetPolyVertices = new Vector2[4];
		// Origin is origin of body
		feetPolyVertices[0] = new Vector2(-bodyWidth / 2 + 1, -bodyHeight / 2);
		feetPolyVertices[1] = new Vector2(bodyWidth / 2 - 1, -bodyHeight / 2);
		feetPolyVertices[2] = new Vector2(-bodyWidth / 2 + 1, -bodyHeight / 2 - 2);
		feetPolyVertices[3] = new Vector2(bodyWidth / 2 - 1, -bodyHeight / 2 - 2);

		feetFixture = bodyBuilder
				.newPolygonFixture(feetPolyVertices, Constants.PPM)
				.categoryBits(CategoryBits.FEET)
				.maskBits(maskBits)
				.isSensor(true)
				.setUserData(this)
				.buildFixture();
	}

	protected void createMeleeWeaponFixture(short maskBits) {
		Vector2 meleeAttackFixturePosition = new Vector2(attackRange, 0);

		meleeWeaponFixture = bodyBuilder
				.newCircleFixture(meleeAttackFixturePosition, attackRange, Constants.PPM)
				.categoryBits(CategoryBits.MELEE_WEAPON)
				.maskBits(maskBits)
				.isSensor(true)
				.setUserData(this)
				.buildFixture();
	}

	public void moveLeft() {
		facingRight = false;

		if (body.getLinearVelocity().x >= -movementSpeed * 2) {
			body.applyLinearImpulse(new Vector2(-movementSpeed, 0), body.getWorldCenter(), true);
		}
	}
    
	public void moveRight() {
		facingRight = true;

		if (body.getLinearVelocity().x <= movementSpeed * 2) {
			body.applyLinearImpulse(new Vector2(movementSpeed, 0), body.getWorldCenter(), true);
		}
	}
    
	public void jump() {
		if (!isJumping) {
			isJumping = true;

			getBody().applyLinearImpulse(new Vector2(0, jumpHeight), body.getWorldCenter(), true);
			if (jumpSound != null && !isMute) jumpSound.play(volume);
		}
	}
	
	public void swingWeapon() {
		if (!isAttacking) {
			isAttacking = true;
			isInflictDmg = false;
			for (Entity entity : inRangeAttack) {
				if (hasInRangeAttack() && !entity.isInvincible() && !entity.isSetToKill()) {
					this.lockedOnTarget.addAll(inRangeAttack);
					entity.setLockedOnTarget(this);
				}
			}
			if (attackSound != null && !isMute) attackSound.play(volume);
		}
	}

	public void inflictDamage(Entity c, int damage) {
    	c.receiveDamage(damage);
		if (facingRight) {
			c.knockedBack(attackForce);
		} else {
			c.knockedBack(-attackForce);
		}
	}
	
	@Override
	public void receiveDamage(int damage) {
		if (!isInvincible) {
			health -= damage;

			if (health <= 0) {
				setToDestroy = true;
				if (deathSound != null && !isMute) deathSound.play(volume);
			} else {
				isHitted = true;
			}
		}
	}
	
	@Override
	public void SetToDestroy() {
		super.SetToDestroy();
		if (deathSound != null && !isMute) deathSound.play(volume);
	}
    
	public void setIsJumping(boolean isJumping) {
		this.isJumping = isJumping;
	}
	
	public BehavioralModel getBehavioralModel() {
		return behavioralModel;
	}
	
	public void setVolume(float volume) {
		this.volume = volume;
	}
	
	public void setToMute(boolean isMute) {
		this.isMute = isMute;
	}
	
	public void dispose() {
	}
	
}