package com.killmonster.character;

import com.killmonster.util.Constants;
import com.killmonster.util.CategoryBits;
import com.killmonster.util.box2d.BodyBuilder;

import java.util.Map;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.Queue;

public abstract class Character extends Sprite implements Disposable {

	public enum State { IDLE, RUNNING, JUMPING, FALLING, HIT, ATTACKING, KILLED };
	
	protected Map<State, Animation<TextureRegion>> animation;
	
	protected State currentState;
	protected State previousState;
	
	protected World currentWorld;
	protected BodyBuilder bodyBuilder;
	protected Body body;
	protected Fixture bodyFixture;
	protected Fixture meleeWeaponFixture;
	protected Fixture feetFixture;
	protected String typeMeleeShape;
	
	protected float stateTimer;
	protected boolean isAlerted;
	protected boolean facingRight;
	protected boolean isJumping;
	protected boolean isOnPlatform;
	protected boolean isAttacking;
	protected boolean isInvincible;
	protected boolean isKilled;
	protected boolean setToKill;
	protected boolean isHitted;
	
	protected String name;
	protected int level;
	protected int exp;
	protected int health;
	protected int stamina;
	protected int magicka;
	
	protected float bodyHeight;
	protected float bodyWidth;
	protected float offsetX;
	protected float offsetY;
	
	protected float movementSpeed;
	protected float jumpHeight;
	protected float attackForce;
	protected int attackRange;
	protected int attackDamage;
	
	protected BehavioralModel behavioralModel;
	protected Character lockedOnTarget;
	protected Character inRangeTarget;
	
	protected Queue<Actor> damageIndicators; // not removing expired ones yet.
	
	TextureRegion textureRegion;
	
	public Character(Texture texture, World currentWorld, float x, float y) {
		super(texture);
		this.currentWorld = currentWorld;
		setPosition(x, y);
		
		bodyBuilder = new BodyBuilder(currentWorld);
		behavioralModel = new BehavioralModel(this);
		
		currentState = State.IDLE;
		previousState = State.IDLE;
		facingRight = true;
		
		damageIndicators = new Queue<>();
	}
    
	public void update(float delta) {
		if (!isKilled) {
			// If the character's health has reached zero but hasn't die yet,
			// it means that the killedAnimation is not fully played.
			// So here we'll play it until it's finished.
			if (setToKill) {
				setRegion(getFrame(delta));
				// Set killed to true to prevent further rendering updates.
				if (animation.get(State.KILLED).isAnimationFinished(stateTimer)) {
					currentWorld.destroyBody(body);
					isKilled = true;
				}
			} else if (isHitted) {
				setRegion(getFrame(delta));
				
				// Set isHitted back to false, implying hit has complete.
				if (animation.get(State.HIT).isAnimationFinished(stateTimer)) {
					isHitted = false;
					stateTimer = 0;
				}
			} else {
				setRegion(getFrame(delta));
				
				// Set isAttacking back to false, implying attack has complete.
				if (animation.get(State.ATTACKING).isAnimationFinished(stateTimer)) {
					isAttacking = false;
					stateTimer = 0;
				}
			}

			float textureX = body.getPosition().x - offsetX;
			float textureY = body.getPosition().y - offsetY;
			setPosition(textureX, textureY);
		}
	}
    
	private TextureRegion getFrame(float delta) {
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
        
//		fix later
		if (typeMeleeShape.equals("CircleShape")) {
			if (!facingRight && !textureRegion.isFlipX()) {
				textureRegion.flip(true, false);
				CircleShape shape = (CircleShape) meleeWeaponFixture.getShape();
				shape.setPosition(new Vector2(-attackRange / Constants.PPM, 0));
			} else if (facingRight && textureRegion.isFlipX()) {
				textureRegion.flip(true, false);
				CircleShape shape = (CircleShape) meleeWeaponFixture.getShape();
				shape.setPosition(new Vector2(attackRange / Constants.PPM, 0));
			} 
		} else {
		}
        
		stateTimer = (currentState != previousState) ? 0 : stateTimer + delta;
		return textureRegion;
	}
	
	private State getState() {
		if (setToKill) {
			return State.KILLED;
		} else if (isHitted) {
			return State.HIT;
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
		body = bodyBuilder.type(type)
				.position(getX(), getY(), Constants.PPM)
				.buildBody();

		createBodyFixture(bodyCategoryBits, bodyMaskBits);
		createFeetFixture(feetMaskBits);
		createMeleeWeaponFixture(meleeWeaponMaskBits);
	}

	protected void createBodyFixture(short categoryBits, short maskBits) {
		bodyFixture = bodyBuilder
				.newRectangleFixture(body.getPosition(), bodyWidth / 2, bodyHeight / 2, Constants.PPM)
				.categoryBits(categoryBits)
				.maskBits(maskBits)
				.setUserData(this)
				.buildFixture();
	}

	protected void createFeetFixture(short maskBits) {
		Vector2[] feetPolyVertices = new Vector2[4];
		feetPolyVertices[0] =  new Vector2(-bodyWidth / 2 + 1, -bodyHeight / 2);
		feetPolyVertices[1] =  new Vector2(bodyWidth / 2 - 1, -bodyHeight / 2);
		feetPolyVertices[2] =  new Vector2(-bodyWidth / 2 + 1, -bodyHeight / 2 - 2);
		feetPolyVertices[3] =  new Vector2(bodyWidth / 2 - 1, -bodyHeight / 2 - 2);

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
		}
	}

	public void jumpDown() {
		if (isOnPlatform) {
			isOnPlatform = false;
			body.setTransform(body.getPosition().x, body.getPosition().y - 8f / Constants.PPM, 0);
		}
	}

	public void swingWeapon() {
		if (!isAttacking) {
			isAttacking = true;

			if (hasInRangeTarget() && !inRangeTarget.isInvincible && !inRangeTarget.setToKill) {
				this.lockedOnTarget = inRangeTarget;
				inRangeTarget.lockedOnTarget = this;

				inflictDamage(inRangeTarget, attackDamage);
			}

			return;
		}
	}

	public void inflictDamage(Character c, int damage) {
		c.receiveDamage(damage);
		if (facingRight) {
			c.knockedBack(attackForce);
//			c.facingRight = true;
//			if (!facingRight && !textureRegion.isFlipX()) {
//				textureRegion.flip(true, false);
//			} else if (facingRight && textureRegion.isFlipX()) {
//				textureRegion.flip(true, false);
//			} 
		} else {
			c.knockedBack(-attackForce);
//			c.facingRight = false;
//			if (!c.facingRight && !c.textureRegion.isFlipX()) {
//				c.textureRegion.flip(true, false);
//			} else if (c.facingRight && c.textureRegion.isFlipX()) {
//				c.textureRegion.flip(true, false);
//			}
		}
	}

	public void receiveDamage(int damage) {
		if (!isInvincible) {
			health -= damage;

			if (health <= 0) {
				setCategoryBits(bodyFixture, CategoryBits.DESTROYED);
				setToKill = true;
			} else {
				isHitted = true;
			}
		}
	}

	public void knockedBack(float force) {
		body.applyLinearImpulse(new Vector2(force, 1f), body.getWorldCenter(), true);
	}

	public static void setCategoryBits(Fixture f, short bits) {
		Filter filter = new Filter();
		filter.categoryBits = bits;
		f.setFilterData(filter);
	}

	public Body getBody() {
		return body;
	}
	
	public int getHealth() {
		return health;
	}
	
	public boolean isSetToKill() {
		return setToKill;
	}
    
	public boolean isKilled() {
		return isKilled;
	}
	
	public boolean isHitted() {
		return isHitted;
	}
    
	public void setIsJumping(boolean isJumping) {
		this.isJumping = isJumping;
	}

	public void setIsOnPlatform(boolean isOnPlatform) {
		this.isOnPlatform = isOnPlatform;
	}
    
	public boolean hasLockedOnTarget() {
		return lockedOnTarget != null;
	}
    
	public boolean hasInRangeTarget() {
		return inRangeTarget != null;
	}
    
	public void setInRangeTarget(Character enemy) {
		inRangeTarget = enemy;
	}
	
	public BehavioralModel getBehavioralModel() {
		return behavioralModel;
	}
	
	public Queue<Actor> getDamageIndicators() {
		return damageIndicators;
	}
	
	public void flipXTextureRegion() {
		textureRegion.flip(true, false);
	}
	
	@Override
	public void dispose() {
	}
    
}