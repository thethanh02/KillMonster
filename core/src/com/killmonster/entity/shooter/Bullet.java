package com.killmonster.entity.shooter;

import java.util.Map;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.killmonster.entity.Entity;
import com.killmonster.entity.character.Player;
import com.killmonster.util.CategoryBits;
import com.killmonster.util.Constants;

public abstract class Bullet extends Entity {
	
	public enum State { IDLE, DESTROYED };
	
	protected Map<State, Animation<TextureRegion>> animation;

	protected State currentState;
	protected State previousState;
	
	protected int attackDamage;
	protected float attackForce;
	protected float movementSpeed;
	
	public Bullet(Texture texture, World currentWorld, float x, float y) {
		super(texture, currentWorld, x, y);
		health = 1;
		attackForce = 1.1f;
		movementSpeed = 0.6f;
	}
		
	@Override
	public void update(float delta) {
		if (!isDestroyed) {
			if (setToDestroy) {
				setRegion(getFrame(delta));
				if (animation.get(State.DESTROYED).isAnimationFinished(stateTimer)) {
					currentWorld.destroyBody(body);
					isDestroyed = true;
				}
			} else {
				if (body.getLinearVelocity().x >= -movementSpeed * 2) {
					body.applyLinearImpulse(new Vector2(-movementSpeed, 0), body.getWorldCenter(), true);
				}
				setRegion(getFrame(delta));
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
			case DESTROYED:
				textureRegion = animation.get(State.DESTROYED).getKeyFrame(stateTimer, false);
				break;
			case IDLE:
			default:
				textureRegion = animation.get(State.IDLE).getKeyFrame(stateTimer, true);
				break;
		}
		stateTimer = (currentState != previousState) ? 0 : stateTimer + delta;
		return textureRegion;
	}
	
	private State getState() {
		if (setToDestroy) {
			return State.DESTROYED;
		} else {
			return State.IDLE;
		}
	}
	
	@Override
	protected void createBodyFixture(short categoryBits, short maskBits) {
		bodyFixture = bodyBuilder
				.newCircleFixture(new Vector2(0, 0), bodyWidth / 2, Constants.PPM)
				.categoryBits(categoryBits)
				.maskBits(maskBits)
				.setUserData(this)
				.buildFixture();
	}
	
	protected void defineBody(BodyDef.BodyType type, short bodyCategoryBits, short bodyMaskBits) {
		super.defineBody(type);
		super.createBodyFixture(bodyCategoryBits, bodyMaskBits);
	}
	
	protected void createBodyandFixtureBullet() {
		short bodyCategoryBits = CategoryBits.BULLET;
		short bodyMaskBits = CategoryBits.PLAYER | CategoryBits.WALL;
		defineBody(BodyType.DynamicBody, bodyCategoryBits, bodyMaskBits);
		
		body.setGravityScale(0);
		setBounds(0, 0, bodyWidth / Constants.PPM, bodyWidth / Constants.PPM);
		setRegion(animation.get(State.IDLE).getKeyFrame(stateTimer, true));
	}
	
	public void inflictDamage(Player c) {
		c.receiveDamage(attackDamage);
		if (facingRight) {
			c.knockedBack(attackForce);
		} else {
			c.knockedBack(-attackForce);
		}
	}
}
