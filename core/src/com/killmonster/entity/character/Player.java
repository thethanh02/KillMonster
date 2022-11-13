package com.killmonster.entity.character;

import com.killmonster.GameWorldManager;
import com.killmonster.entity.Entity;
import com.killmonster.util.*;

import java.util.HashMap;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.Timer.Task;

public class Player extends Character {
    
	private static final String TEXTURE_FILE = "character/player/Player.png";
	
	private GameWorldManager gameWorldManager;
	
	public Player(GameWorldManager gameWorldManager, float x, float y) {
		super(gameWorldManager.getAssets().get(TEXTURE_FILE), gameWorldManager.getWorld(), x, y);
		this.gameWorldManager = gameWorldManager;
		
		name = "Player";
		bodyWidth = 19f;
		bodyHeight = 26f;
		offsetX = .31f;
		offsetY = .228f;
		
		health = 100;
		movementSpeed = .55f;
		jumpHeight = 4f;
		
		attackForce = 2f;
		attackRange = 15;
		attackDamage = 25;
		
		typeMeleeShape = "CircleShape";
		
		// Create animations by extracting frames from the spritesheet.
		animation = new HashMap<>();
		animation.put(State.IDLE, 	 	Utils.createAnimation(getTexture(), 14f / Constants.PPM, 0, 4, 0, 0 * 40, 64, 40));
		animation.put(State.RUNNING, 	Utils.createAnimation(getTexture(), 9f / Constants.PPM,  0, 5, 0, 1 * 40, 64, 40));
		animation.put(State.JUMPING, 	Utils.createAnimation(getTexture(), 10f / Constants.PPM, 0, 2, 0, 2 * 40, 64, 40));
		animation.put(State.FALLING, 	Utils.createAnimation(getTexture(), 10f / Constants.PPM, 0, 0, 0, 3 * 40, 64, 40));
		animation.put(State.ATTACKING,  Utils.createAnimation(getTexture(), 18f / Constants.PPM, 0, 2, 0, 4 * 40, 64, 40));
		animation.put(State.HIT, 		Utils.createAnimation(getTexture(), 12f / Constants.PPM, 0, 3, 0, 5 * 40, 64, 40));
		animation.put(State.KILLED, 	Utils.createAnimation(getTexture(), 24f / Constants.PPM, 0, 7, 0, 6 * 40, 64, 40));
		
		// Create body and fixtures.
		defineBody();
		
		setBounds(0, 0, 64 / Constants.PPM, 40 / Constants.PPM);
		setRegion(animation.get(State.IDLE).getKeyFrame(stateTimer, true));
	}

	public void defineBody() {
		short bodyCategoryBits = CategoryBits.PLAYER;
		short bodyMaskBits = CategoryBits.GROUND | CategoryBits.PLATFORM | CategoryBits.WALL | CategoryBits.ENEMY | CategoryBits.MELEE_WEAPON | CategoryBits.POTION | CategoryBits.DEATHPLACE;
		short feetMaskBits = CategoryBits.GROUND | CategoryBits.PLATFORM;
		short weaponMaskBits = CategoryBits.ENEMY | CategoryBits.BOX;
		
		super.defineBody(BodyDef.BodyType.DynamicBody, bodyCategoryBits, bodyMaskBits, feetMaskBits, weaponMaskBits);
	}

	public void reposition(Vector2 position) {
		body.setTransform(position, 0);
	}
	
	public void reposition(float x, float y) {
		body.setTransform(x, y, 0);
	}

	@Override
	public void inflictDamage(Entity c, int damage) {
		if ((this.facingRight && c.isFacingRight()) || (!this.facingRight && !c.isFacingRight())) {
			damage *= 2;
			gameWorldManager.getMessageArea().show("Critical hit!");
		}
		
		super.inflictDamage(c, damage);
		gameWorldManager.getDamageIndicator().show(c, damage);
		gameWorldManager.getMessageArea().show(String.format("You dealt %d pts damage to %s", damage, c.getName()));
//		CameraShake.shake(8 / Constants.PPM, .1f);
		
		if (c instanceof Enemy && c.isSetToKill()) {
			gameWorldManager.getMessageArea().show(String.format("You earned 10 exp."));
		}
	}
    
	@Override
	public void receiveDamage(int damage) {
		super.receiveDamage(damage);
		
		// Sets the player to be untouchable for a while.
		if (!isInvincible) {
			CameraShake.shake(8 / Constants.PPM, .1f);
			isInvincible = true;
			
			Timer.schedule(new Task() {
				@Override
				public void run() {
					if (!setToDestroy) {
						isInvincible = false;
					}
				}
			}, 2.5f);
		}
	}

	public void healed(int health) {
		this.health = this.health + health > 100 ? 100 : this.health + health;
		gameWorldManager.getMessageArea().show(String.format("You are healed %d HP", health));
	}
	
}