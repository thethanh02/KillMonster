package com.killmonster.system;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.World;
import com.killmonster.component.*;
import com.killmonster.util.Constants;

public class CharacterRendererSystem extends IteratingSystem {
	
	private World world;
	private Batch batch;
	private Camera camera;
	
	private CharacterStatsComponent stats;
	private BodyComponent body;
	private SpriteComponent sprite;
	private AnimationComponent animation;
	private StateComponent state;
	
	public CharacterRendererSystem(Batch batch, Camera camera, World world) {
		super(Family.all(SpriteComponent.class).get());
		
		this.batch = batch;
		this.camera = camera;
		this.world = world;
	}
	
	@Override
	public void update(float deltaTime) {
		batch.setProjectionMatrix(camera.combined);
		batch.begin();
		super.update(deltaTime);
		batch.end();
	}

	@Override
	protected void processEntity(Entity entity, float deltaTime) {
		stats = Mappers.CHARACTER_STATS.get(entity);
		body = Mappers.BODY.get(entity);
		sprite = Mappers.SPRITE.get(entity);
		animation = Mappers.ANIMATION.get(entity);
		state = Mappers.STATE.get(entity);
		
		if (!state.isKilled) {
			if (state.setToKill) {
				sprite.sprite.setRegion(getFrame(deltaTime));
				if (animation.get(CharacterState.KILLED).isAnimationFinished(state.time)) {
					world.destroyBody(body.body);
					state.isKilled = true;
				}
			} else {
				sprite.sprite.setRegion(getFrame(deltaTime));
				if (state.isAttacking && state.time >= stats.attackTime) {
					state.isAttacking = false;
					state.time = 0;
				}
			}
			
			float textureX = body.body.getPosition().x - stats.offsetX;
			float textureY = body.body.getPosition().y - stats.offsetY;
			sprite.sprite.setPosition(textureX, textureY);
		}
		
		sprite.sprite.setRegion(getFrame(deltaTime));
		sprite.sprite.draw(batch);
	}

	private TextureRegion getFrame(float deltaTime) {
		state.setCurrentState(getState());
		TextureRegion textureRegion;
		switch (state.getCurrentState()) {
			case RUNNING:
				textureRegion = animation.get(CharacterState.RUNNING).getKeyFrame(state.time, true);
				break;
			case JUMPING:
				textureRegion = animation.get(CharacterState.JUMPING).getKeyFrame(state.time, false);
				break;
			case FALLING:
				textureRegion = animation.get(CharacterState.FALLING).getKeyFrame(state.time, true);
				break;
			case ATTACKING:
				textureRegion = animation.get(CharacterState.ATTACKING).getKeyFrame(state.time, false);
				break;
			case KILLED:
				textureRegion = animation.get(CharacterState.KILLED).getKeyFrame(state.time, false);
				break;
			case IDLE:
			default:
				textureRegion = animation.get(CharacterState.IDLE).getKeyFrame(state.time, true);;
				break;
		}
		
		if (stats.typeFixtureShape.equals("CircleShape")) {
			if (!state.facingRight && !textureRegion.isFlipX()) {
				textureRegion.flip(true, false);
				CircleShape shape = (CircleShape) body.meleeWeaponFixture.getShape();
				shape.setPosition(new Vector2(-stats.attackRange / Constants.PPM, 0));
			} else if (state.facingRight && textureRegion.isFlipX()) {
				textureRegion.flip(true, false);
				CircleShape shape = (CircleShape) body.meleeWeaponFixture.getShape();
				shape.setPosition(new Vector2(stats.attackRange / Constants.PPM, 0));
			} 
		} else {
		}
        
		state.time = (state.getCurrentState() != state.getPreviousState()) ? 0 : state.time + deltaTime;
		return textureRegion;
	}

	private CharacterState getState() {
		if (state.setToKill) {
			return CharacterState.KILLED;
		} else if (state.isAttacking) {
			return CharacterState.ATTACKING;
		} else if (state.isJumping && body.body.getLinearVelocity().y < -.01f) {
			return CharacterState.FALLING;
		} else if (body.body.getLinearVelocity().y > .01f) {
			return CharacterState.JUMPING;
		} else if (body.body.getLinearVelocity().x > .01f || body.body.getLinearVelocity().x < -.01f) {
			return CharacterState.RUNNING;
		} else {
			return CharacterState.IDLE;
		}
	}

	
}
