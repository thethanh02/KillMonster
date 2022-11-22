package com.killmonster.entity.shooter;

import java.util.Map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.World;
import com.killmonster.entity.Entity;

public abstract class Shooter extends Entity {

	public enum State { IDLE, ATTACKING, HIT, DESTROYED };
	
	protected Map<State, Animation<TextureRegion>> animation;
	
	protected State currentState;
	protected State previousState;
	
	protected boolean isAttacking;
	protected float cooldownTime;
	protected float attackTimeLoop;
	
	TextureRegion textureRegion;

	public Shooter(Texture texture, World currentWorld, float x, float y) {
		super(texture, currentWorld, x, y);
	}
	
	@Override
	public void update(float delta) {
		if (!isDestroyed) {
			cooldownTime += Gdx.graphics.getDeltaTime();
			
			// If the character's health has reached zero but hasn't die yet,
			// it means that the killedAnimation is not fully played.
			// So here we'll play it until it's finished.
			if (setToDestroy) {
				setRegion(getFrame(delta));
				// Set killed to true to prevent further rendering updates.
				if (animation.get(State.DESTROYED).isAnimationFinished(stateTimer)) {
					currentWorld.destroyBody(body);
					isDestroyed = true;
					
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
			case ATTACKING:
				textureRegion = animation.get(State.ATTACKING).getKeyFrame(stateTimer, false);
				break;
			case HIT:
				textureRegion = animation.get(State.HIT).getKeyFrame(stateTimer, false);
				break;
			case DESTROYED:
				textureRegion = animation.get(State.DESTROYED).getKeyFrame(stateTimer, false);
				break;
			case IDLE:
			default:
				textureRegion = animation.get(State.IDLE).getKeyFrame(stateTimer, true);
				break;
		}
		// Default texture of shooter is facing left, so facingRight meaning facingLeft
		if (facingRight && !textureRegion.isFlipX()) {
			textureRegion.flip(true, false);
		} else if (!facingRight && textureRegion.isFlipX()) {
			textureRegion.flip(true, false);
		}
		
		stateTimer = (currentState != previousState) ? 0 : stateTimer + delta;
		return textureRegion;
	}
	
	private State getState() {
		if (setToDestroy) {
			return State.DESTROYED;
		} else if (isHitted) {
			return State.HIT;
		} else if (isAttacking) {
			return State.ATTACKING;
		} else {
			return State.ATTACKING;
		}
	}
	
	protected void defineBody(BodyDef.BodyType type, short bodyCategoryBits, short bodyMaskBits) {
		super.defineBody(type);
		super.createBodyFixture(bodyCategoryBits, bodyMaskBits);
	}

	public boolean cooldownSpawnBullet() {
		if (cooldownTime > attackTimeLoop) {
			cooldownTime = 0;
			return true;
		}
		return false;
	}
	
	public Bullet spawnBullet() {
		return null;
	}
	
}
