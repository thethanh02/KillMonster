package com.killmonster.objects;

import java.util.Map;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Disposable;
import com.killmonster.character.Player;
import com.killmonster.util.Constants;
import com.killmonster.util.box2d.BodyBuilder;

public class GameObject extends Sprite implements Disposable {

	public enum State { IDLE, HIT, DESTROYED };
	
	protected Map<State, Animation<TextureRegion>> animation;
	
	protected State currentState;
	protected State previousState;
	
	protected World currentWorld;
	protected BodyBuilder bodyBuilder;
	protected Body b2body;
	protected Fixture bodyFixture;
	
	protected float stateTimer;
	protected boolean isInvincible;
	protected boolean isDestroyed;
	protected boolean setToDestroy;
	protected Player inRangeTarget;
	
	protected String name;
	protected int health;
	
	protected float bodyHeight;
	protected float bodyWidth;
	protected float offsetX;
	protected float offsetY;
	
	TextureRegion textureRegion;

	public GameObject(Texture texture, World currentWorld, float x, float y) {
		super(texture);
		this.currentWorld = currentWorld;
		setPosition(x, y);
		
		bodyBuilder = new BodyBuilder(currentWorld);
	}
	
	public void update(float delta) {
		if (!isDestroyed) {
			if (setToDestroy) {
				setRegion(getFrame(delta));
				if (animation.get(State.DESTROYED).isAnimationFinished(stateTimer)) {
					currentWorld.destroyBody(b2body);
					isDestroyed = true;
				}
			} else {
				setRegion(getFrame(delta));
			}

			float textureX = b2body.getPosition().x - offsetX;
			float textureY = b2body.getPosition().y - offsetY;
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
	
	protected void defineBody(BodyDef.BodyType type, short bodyCategoryBits, short bodyMaskBits) {
		b2body = bodyBuilder.type(type)
				.position(getX(), getY(), Constants.PPM)
				.buildBody();
		
		createBodyFixture(bodyCategoryBits, bodyMaskBits);
	}
	
	protected void createBodyFixture(short categoryBits, short maskBits) {
		bodyFixture = bodyBuilder
				.newRectangleFixture(b2body.getPosition(), bodyWidth / 2, bodyHeight / 2, Constants.PPM)
				.categoryBits(categoryBits)
				.maskBits(maskBits)
				.setUserData(this)
				.buildFixture();
	}

	// Maybe inflict damage or maybe regenerate health point
	// Mainly affect Player
	protected void alterHealth(Character c) {
		
	}

	@Override
	public void dispose() {
		
	}
}
