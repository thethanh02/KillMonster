package com.killmonster.entity.objects;

import java.util.Map;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.World;
import com.killmonster.entity.Entity;

public abstract class GameObject extends Entity {

	public enum State { IDLE, HIT, DESTROYED };
	
	protected Map<State, Animation<TextureRegion>> animation;
	
	protected State currentState;
	protected State previousState;
	
	TextureRegion textureRegion;

	public GameObject(Texture texture, World currentWorld, float x, float y) {
		super(texture, currentWorld, x, y);
		facingRight = true;
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
		if (!facingRight && !textureRegion.isFlipX()) {
			textureRegion.flip(true, false);
		} else if (facingRight && textureRegion.isFlipX()) {
			textureRegion.flip(true, false);
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
	
}
