package com.killmonster.entity.character;

import com.killmonster.GameWorldManager;
import com.killmonster.entity.Entity;
import com.killmonster.entity.objects.chest.Chest;
import com.killmonster.util.*;

import java.util.HashMap;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.Timer.Task;

public class Player extends Character {
    
	private static final String TEXTURE_FILE = "character/player/CaptainClownNose.png";
	
	private GameWorldManager gameWorldManager;
	private int score;
	private int key;
	private Chest chestTarget;
	
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
		attackPosX = 15;
		attackDamage = 25;
		
		startHitTime = 0f;
		endHitTime = .18f * 3f;
		startHitTime2 = 0f;
		endHitTime2 = .12f * 6f; 
		
		key = 0;
		typeMeleeShape = "Player";
		
		// Create animations by extracting frames from the spritesheet.
		animation = new HashMap<>();
		animation.put(State.IDLE, 	 	Utils.createAnimation(getTexture(), 14f / Constants.PPM, 0, 4, 0 * 40, 64, 40));
		animation.put(State.RUNNING, 	Utils.createAnimation(getTexture(), 9f / Constants.PPM,  0, 5, 1 * 40, 64, 40));
		animation.put(State.JUMPING, 	Utils.createAnimation(getTexture(), 10f / Constants.PPM, 0, 2, 2 * 40, 64, 40));
		animation.put(State.FALLING, 	Utils.createAnimation(getTexture(), 10f / Constants.PPM, 0, 0, 3 * 40, 64, 40));
		animation.put(State.ATTACKING,  Utils.createAnimation(getTexture(), 18f / Constants.PPM, 0, 2, 4 * 40, 64, 40));
		animation.put(State.HIT, 		Utils.createAnimation(getTexture(), 12f / Constants.PPM, 0, 3, 5 * 40, 64, 40));
		animation.put(State.KILLED, 	Utils.createAnimation(getTexture(), 24f / Constants.PPM, 0, 7, 6 * 40, 64, 40));
		animation.put(State.ATTACK2, 	Utils.createAnimation(getTexture(), 12f / Constants.PPM, 0, 5, 7 * 40, 64, 40));
		
		// Sounds.
		deathSound = gameWorldManager.getAssets().get("sound/die.wav");
		attackSound = gameWorldManager.getAssets().get("sound/attack1.wav");
		jumpSound = gameWorldManager.getAssets().get("sound/jump.wav");
		
		// Create body and fixtures.
		defineBody();
		
		setBounds(0, 0, 64 / Constants.PPM, 40 / Constants.PPM);
		setRegion(animation.get(State.IDLE).getKeyFrame(stateTimer, true));
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
				if (isAttacking2) {
					if (animation.get(State.ATTACK2).isAnimationFinished(stateTimer)) {
						isAttacking2 = false;
						isInvincible = false;
						stateTimer = 0;
					}
				} else if (isAttacking) {
					if (animation.get(State.ATTACKING).isAnimationFinished(stateTimer)) {
						isAttacking = false;
						stateTimer = 0;
					}
				} 
				
			} 

			float textureX = body.getPosition().x - offsetX;
			float textureY = body.getPosition().y - offsetY;
			setPosition(textureX, textureY);
		}
	}

	public void defineBody() {
		short bodyCategoryBits = CategoryBits.PLAYER;
		short bodyMaskBits = CategoryBits.GROUND | CategoryBits.WALL | CategoryBits.ENEMY | CategoryBits.MELEE_WEAPON | CategoryBits.POTION
				| CategoryBits.DEATHPLACE | CategoryBits.BULLET| CategoryBits.DIAMOND | CategoryBits.KEY | CategoryBits.CHEST;
		short feetMaskBits = CategoryBits.GROUND;
		short weaponMaskBits = CategoryBits.ENEMY | CategoryBits.CONTAINER | CategoryBits.SHOOTER;
		
		super.defineBody(BodyDef.BodyType.DynamicBody, bodyCategoryBits, bodyMaskBits, feetMaskBits, weaponMaskBits);
	}
	
	@Override
	protected void updateWeaponFixture() {
		if (!facingRight && !textureRegion.isFlipX()) {
			textureRegion.flip(true, false);
			CircleShape shape = (CircleShape) meleeWeaponFixture.getShape();
			shape.setPosition(new Vector2(-attackPosX / Constants.PPM, 0));
		} else if (facingRight && textureRegion.isFlipX()) {
			textureRegion.flip(true, false);
			CircleShape shape = (CircleShape) meleeWeaponFixture.getShape();
			shape.setPosition(new Vector2(attackPosX / Constants.PPM, 0));
		} 
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
		gameWorldManager.getMessageArea().show(String.format("You dealt %d pts damage to %s", damage, c.getName()));
	}
	
	@Override
	public void inflictDamage2(Entity c, int damage) {
		if ((this.facingRight && c.isFacingRight()) || (!this.facingRight && !c.isFacingRight())) {
			damage *= 2;
			gameWorldManager.getMessageArea().show("Critical hit!");
		}
		
		super.inflictDamage2(c, damage);
		gameWorldManager.getMessageArea().show(String.format("You dealt %d pts damage to %s", damage, c.getName()));
	}
    
	@Override
	public void receiveDamage(int damage) {
		super.receiveDamage(damage);
		// Sets the player to be untouchable for a while.
		if (!isInvincible) {
			gameWorldManager.getDamageIndicator().show(this, "-"+damage, Color.RED);
			CameraShake.shake(8 / Constants.PPM, .1f);
			isInvincible = true;
			
			Timer.schedule(new Task() {
				@Override
				public void run() {
					if (!setToDestroy) {
						isInvincible = false;
					}
				}
			}, 1.5f);
		}
	}

	public void healed(int health) {
		this.health = this.health + health > 100 ? 100 : this.health + health;
		gameWorldManager.getDamageIndicator().show(this, "+"+health, Color.GREEN);
		gameWorldManager.getMessageArea().show(String.format("You are healed %d HP", health));
	}
	
	public void receiveScore(int scorePoint) {
		score += scorePoint;
		gameWorldManager.getDamageIndicator().show(this, "+"+scorePoint, Color.YELLOW);
	}
	
	public void openChest() {
		if (chestTarget != null) {
			if (key > 0) {
				chestTarget.SetToDestroy();
			} else {
				gameWorldManager.getMessageArea().show("You need key to open the chest");
			}
		}
	}
	
	public void setScore(int score) {
		this.score = score;
	}
	
	public int getScore() {
		return score;
	}
	
	public boolean isAttacking2() {
		return isAttacking2;
	}
	
	public void setChestTarget(Chest chestTarget) {
		this.chestTarget = chestTarget;
	}
	
	public void addKey() {
		key += 1;
		gameWorldManager.getMessageArea().show(String.format("Key: %d", key));
	}
	
}