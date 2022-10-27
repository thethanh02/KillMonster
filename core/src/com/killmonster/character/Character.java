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

	public enum State { IDLE, RUNNING, JUMPING, FALLING, ATTACKING, KILLED };
	
	protected Map<State, Animation<TextureRegion>> animation;
	
	protected Character.State currentState;
	protected Character.State previousState;
	
	protected World currentWorld;
	protected BodyBuilder bodyBuilder;
	protected Body b2body;
	protected Fixture bodyFixture;
	protected Fixture meleeWeaponFixture;
	protected Fixture feetFixture;
	
	protected float stateTimer;
	protected boolean isAlerted;
	protected boolean facingRight;
	protected boolean isJumping;
	protected boolean isOnPlatform;
	protected boolean isAttacking;
	protected boolean isInvincible;
	protected boolean isKilled;
	protected boolean setToKill;
	
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
	protected String typeFixtureShape;
	
	protected float movementSpeed;
	protected float jumpHeight;
	protected float attackForce;
	protected float attackTime;
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
				setRegion(getFrame(delta, typeFixtureShape));
				// Set killed to true to prevent further rendering updates.
				if (animation.get(State.KILLED).isAnimationFinished(stateTimer)) {
					currentWorld.destroyBody(b2body);
					isKilled = true;
				}
			} else {
				setRegion(getFrame(delta, typeFixtureShape));
				
				// Set isAttacking back to false, implying attack has complete.
				if (animation.get(State.ATTACKING).isAnimationFinished(stateTimer)) {
					isAttacking = false;
					stateTimer = 0;
				}
			}

			float textureX = b2body.getPosition().x - offsetX;
			float textureY = b2body.getPosition().y - offsetY;
			setPosition(textureX, textureY);
		}
	}
    
	private TextureRegion getFrame(float delta, String type) {
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
			case KILLED:
				textureRegion = animation.get(State.KILLED).getKeyFrame(stateTimer, false);
				break;
			case IDLE:
			default:
				textureRegion = animation.get(State.IDLE).getKeyFrame(stateTimer, true);;
				break;
		}
        
		if (type.equals("CircleShape")) {
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
		} else if (isAttacking) {
			return State.ATTACKING;
		} else if (isJumping && b2body.getLinearVelocity().y < -.01f) {
			return State.FALLING;
		} else if (b2body.getLinearVelocity().y > .01f) {
			return State.JUMPING;
		} else if (b2body.getLinearVelocity().x > .01f || b2body.getLinearVelocity().x < -.01f) {
			return State.RUNNING;
		} else {
			return State.IDLE;
		}
	}

	protected void defineBody(BodyDef.BodyType type, short bodyCategoryBits, short bodyMaskBits, short feetMaskBits, short meleeWeaponMaskBits) {
		b2body = bodyBuilder.type(type)
				.position(getX(), getY(), Constants.PPM)
				.buildBody();

		createBodyFixture(bodyCategoryBits, bodyMaskBits);
		createFeetFixture(feetMaskBits);
		createMeleeWeaponFixture(meleeWeaponMaskBits);
	}

	protected void createBodyFixture(short categoryBits, short maskBits) {
		bodyFixture = bodyBuilder
				.newRectangleFixture(b2body.getPosition(), bodyWidth / 2, bodyHeight / 2, Constants.PPM)
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

		if (b2body.getLinearVelocity().x >= -movementSpeed * 2) {
			b2body.applyLinearImpulse(new Vector2(-movementSpeed, 0), b2body.getWorldCenter(), true);
		}
	}
    
	public void moveRight() {
		facingRight = true;

		if (b2body.getLinearVelocity().x <= movementSpeed * 2) {
			b2body.applyLinearImpulse(new Vector2(movementSpeed, 0), b2body.getWorldCenter(), true);
		}
	}
    
	public void jump() {
		if (!isJumping) {
			isJumping = true;

			getB2Body().applyLinearImpulse(new Vector2(0, jumpHeight), b2body.getWorldCenter(), true);
		}
	}

	public void jumpDown() {
		if (isOnPlatform) {
			isOnPlatform = false;
			b2body.setTransform(b2body.getPosition().x, b2body.getPosition().y - 8f / Constants.PPM, 0);
		}
	}

	public void swingWeapon() {
		if (!isAttacking()) {
			setIsAttacking(true);

			if (hasInRangeTarget() && !inRangeTarget.isInvincible() && !inRangeTarget.isSetToKill()) {
				setLockedOnTarget(inRangeTarget);
				inRangeTarget.setLockedOnTarget(this);

				inflictDamage(inRangeTarget, attackDamage);
			}

			return;
		}
	}

	public void inflictDamage(Character c, int damage) {
		c.receiveDamage(damage);
		c.knockedBack((facingRight) ? attackForce : -attackForce);
	}

	public void receiveDamage(int damage) {
		if (!isInvincible) {
			health -= damage;

			if (health <= 0) {
				setCategoryBits(bodyFixture, CategoryBits.DESTROYED);
				setToKill = true;
			} else {
			}
		}
	}

	public void knockedBack(float force) {
		b2body.applyLinearImpulse(new Vector2(force, 1f), b2body.getWorldCenter(), true);
	}

	public static void setCategoryBits(Fixture f, short bits) {
		Filter filter = new Filter();
		filter.categoryBits = bits;
		f.setFilterData(filter);
	}


	// Review the code below.
	public Body getB2Body() {
		return b2body;
	}

	public Fixture getBodyFixture() {
		return bodyFixture;
	}

	public String getName() {
		return name;
	}
    
	public boolean isAttacking() {
		return isAttacking;
	}

	public boolean isInvincible() {
		return isInvincible;
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
    
	public boolean isJumping() {
		return isJumping;
	}
    
	public void setIsJumping(boolean isJumping) {
		this.isJumping = isJumping;
	}

	public boolean isOnPlatform() {
		return isOnPlatform;
	}

	public void setIsOnPlatform(boolean isOnPlatform) {
		this.isOnPlatform = isOnPlatform;
	}
    
	public void setIsAttacking(boolean isAttacking) {
		this.isAttacking = isAttacking;
	}


	public boolean hasLockedOnTarget() {
		return lockedOnTarget != null;
	}
    
	public boolean hasInRangeTarget() {
		return inRangeTarget != null;
	}
    
	public Character getLockedOnTarget() {
		return lockedOnTarget;
	}
	
	public Character getInRangeTarget() {
		return inRangeTarget;
	}
	
	public void setLockedOnTarget(Character enemy) {
		lockedOnTarget = enemy;
	}
	
	public void setInRangeTarget(Character enemy) {
		inRangeTarget = enemy;
	}
	
	
	public boolean facingRight() {
		return facingRight;
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
    
}