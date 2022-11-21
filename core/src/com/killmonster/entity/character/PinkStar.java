package com.killmonster.entity.character;

import java.util.HashMap;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.World;
import com.killmonster.util.CategoryBits;
import com.killmonster.util.Constants;
import com.killmonster.util.Utils;

public class PinkStar extends Enemy {

	private static final String TEXTURE_FILE = "character/pinkstar/PinkStar.png";

	public PinkStar(AssetManager assets, World world, float x, float y) {
		super(assets.get(TEXTURE_FILE), world, x, y);
		
		name = "Pink Star";
		bodyWidth = 27f;
		bodyHeight = 25f;
		offsetX = .135f+.015f;
		offsetY = .125f+.025f;
		
		health = 100;
		movementSpeed = .25f;
		jumpHeight = 4.5f;
		attackForce = 1.5f;
		attackRange = 14;
		attackDamage = 2;
		facingRight = false;
		
		startHitTime = 0f;
		endHitTime = .7f;
		
		typeMeleeShape = "Enemy";
		
		// Knight stand animation.
		animation = new HashMap<>();
		animation.put(State.IDLE, 		Utils.createAnimation(getTexture(), 12f / Constants.PPM, 0, 7, 0 * 30, 34, 30));
		animation.put(State.RUNNING, 	Utils.createAnimation(getTexture(), 12f / Constants.PPM, 0, 5, 1 * 30, 34, 30));
		animation.put(State.JUMPING, 	Utils.createAnimation(getTexture(), 12f / Constants.PPM, 2, 3, 0 * 30, 34, 30));
		animation.put(State.FALLING, 	Utils.createAnimation(getTexture(), 12f / Constants.PPM, 4, 4, 0 * 30, 34, 30));
		animation.put(State.ATTACKING,  Utils.createAnimation(getTexture(), 10f / Constants.PPM, 0, 7, 2 * 30, 34, 30));
		animation.put(State.HIT, 		Utils.createAnimation(getTexture(), 12f / Constants.PPM, 0, 3, 3 * 30, 34, 30));
		animation.put(State.KILLED, 	Utils.createAnimation(getTexture(), 24f / Constants.PPM, 0, 4, 4 * 30, 34, 30));
		
		short bodyCategoryBits = CategoryBits.ENEMY;
		short bodyMaskBits = CategoryBits.GROUND | CategoryBits.PLATFORM | CategoryBits.WALL | CategoryBits.PLAYER | CategoryBits.MELEE_WEAPON | CategoryBits.CLIFF_MARKER | CategoryBits.DEATHPLACE;
		short feetMaskBits = CategoryBits.GROUND | CategoryBits.PLATFORM;
		short weaponMaskBits = CategoryBits.PLAYER;
		super.defineBody(BodyDef.BodyType.DynamicBody, bodyCategoryBits, bodyMaskBits, feetMaskBits, weaponMaskBits);
		
		setBounds(0, 0, 34 / Constants.PPM, 30 / Constants.PPM);
		textureRegion = animation.get(State.IDLE).getKeyFrame(stateTimer, true);
		textureRegion.flip(true, false);
		setRegion(textureRegion);
	}
	
	@Override
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
			case ATTACKING:
				textureRegion = animation.get(State.ATTACKING).getKeyFrame(stateTimer, true);
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
		if (!facingRight && textureRegion.isFlipX()) {
			textureRegion.flip(true, false);
			CircleShape shape = (CircleShape) meleeWeaponFixture.getShape();
			shape.setPosition(new Vector2(-attackRange / Constants.PPM, 0));
		} else if (facingRight && !textureRegion.isFlipX()) {
			textureRegion.flip(true, false);
			CircleShape shape = (CircleShape) meleeWeaponFixture.getShape();
			shape.setPosition(new Vector2(attackRange / Constants.PPM, 0));
		} 
		stateTimer = (currentState != previousState) ? 0 : stateTimer + delta;
		return textureRegion;
	}
	
}
