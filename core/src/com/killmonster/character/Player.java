package com.killmonster.character;

import com.killmonster.GameWorldManager;
import com.killmonster.component.CharacterState;
import com.killmonster.component.PlayerComponent;
import com.killmonster.util.*;

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
		
		add(new PlayerComponent());
		
		stats.name = "Ten12";
		stats.bodyWidth = 10;
		stats.bodyHeight = 34;
		stats.offsetX = .31f;
		stats.offsetY = .228f;
		
		stats.health = 100;
		stats.movementSpeed = .5f;
		stats.jumpHeight = 4.5f;
		
		stats.attackForce = 1f;
		stats.attackRange = 15;
		stats.attackDamage = 25;
		
		stats.typeFixtureShape = "CircleShape";
		
		// Create animations by extracting frames from the spritesheet.
		animations.put(CharacterState.IDLE, 	Utils.createAnimation(sprite.sprite.getTexture(), 14f / Constants.PPM, 0, 4,  0, 0 * 40,  64, 40));
		animations.put(CharacterState.RUNNING, 	Utils.createAnimation(sprite.sprite.getTexture(), 9f / Constants.PPM,  0, 5,  0, 1 * 40,  64, 40));
		animations.put(CharacterState.JUMPING, 	Utils.createAnimation(sprite.sprite.getTexture(), 10f / Constants.PPM, 0, 2,  0, 2 * 40,  64, 40));
		animations.put(CharacterState.FALLING,	Utils.createAnimation(sprite.sprite.getTexture(), 10f / Constants.PPM, 0, 0,  0, 3 * 40,  64, 40));
		animations.put(CharacterState.ATTACKING, Utils.createAnimation(sprite.sprite.getTexture(), 18f / Constants.PPM, 0, 2,  0, 4 * 40,  64, 40));
		animations.put(CharacterState.KILLED,	Utils.createAnimation(sprite.sprite.getTexture(), 24f / Constants.PPM, 0, 7,  0, 6 * 40,  64, 40));
		
		stats.attackTime = animations.get(CharacterState.ATTACKING).getFrameDuration() * 3;
		// Create body and fixtures.
		defineBody();
		
		sprite.sprite.setBounds(0, 0, 64 / Constants.PPM, 40 / Constants.PPM);
	}

	public void defineBody() {
		short bodyCategoryBits = CategoryBits.PLAYER;
		short bodyMaskBits = CategoryBits.GROUND | CategoryBits.PLATFORM | CategoryBits.WALL | CategoryBits.ENEMY | CategoryBits.MELEE_WEAPON;
		short feetMaskBits = CategoryBits.GROUND | CategoryBits.PLATFORM;
		short weaponMaskBits = CategoryBits.ENEMY | CategoryBits.OBJECT;
		
		body.body = body.bodyBuilder
				.type(BodyDef.BodyType.DynamicBody)
				.position(sprite.sprite.getX(), sprite.sprite.getY(), Constants.PPM)
				.buildBody();

		createBodyFixture(bodyCategoryBits, bodyMaskBits);
		createFeetFixture(feetMaskBits);
		super.createMeleeWeaponFixture(weaponMaskBits);
	}
	
	public void createBodyFixture(short categoryBits, short maskBits) {
		body.bodyFixture = body.bodyBuilder
				.newRectangleFixture(body.body.getPosition(), 9.5f, 13f, Constants.PPM)
				.categoryBits(categoryBits)
				.maskBits(maskBits)
				.setUserData(this)
				.buildFixture();
	}
    
	public void createFeetFixture(short maskBits) {
		Vector2[] feetPolyVertices = new Vector2[4];
		feetPolyVertices[0] =  new Vector2(-stats.bodyWidth / 2 + 1, -stats.bodyHeight / 2 + 3);
		feetPolyVertices[1] =  new Vector2(stats.bodyWidth / 2 - 1, -stats.bodyHeight / 2 + 3);
		feetPolyVertices[2] =  new Vector2(-stats.bodyWidth / 2 + 1, -stats.bodyHeight / 2 + 2);
		feetPolyVertices[3] =  new Vector2(stats.bodyWidth / 2 - 1, -stats.bodyHeight / 2 + 2);
		
		body.feetFixture = body.bodyBuilder
				.newPolygonFixture(feetPolyVertices, Constants.PPM)
				.categoryBits(CategoryBits.FEET)
				.maskBits(maskBits)
				.isSensor(true)
				.setUserData(this)
				.buildFixture();
	}

	public void reposition(Vector2 position) {
		body.body.setTransform(position, 0);
	}
	
	public void reposition(float x, float y) {
		body.body.setTransform(x, y, 0);
	}

	@Override
	public void inflictDamage(Character c, int damage) {
		if ((this.state.facingRight && c.state.facingRight) || (!this.state.facingRight && !c.state.facingRight)) {
			damage *= 2;
			gameWorldManager.getNotificationArea().show("Critical hit!");
		}
		
		super.inflictDamage(c, damage);
		gameWorldManager.getDamageIndicator().show(c, damage);
		gameWorldManager.getNotificationArea().show(String.format("You dealt %d pts damage to %s", damage, c.getName()));
		CameraShake.shake(8 / Constants.PPM, .1f);
		
		if (c.state.isSetToKill()) {
			gameWorldManager.getNotificationArea().show(String.format("You earned 10 exp."));
		}
	}
    
	@Override
	public void receiveDamage(int damage) {
		super.receiveDamage(damage);
		
		// Sets the player to be untouchable for a while.
		if (!state.isInvincible) {
			CameraShake.shake(8 / Constants.PPM, .1f);
			state.isInvincible = true;
			
			Timer.schedule(new Task() {
				@Override
				public void run() {
					if (!state.setToKill) {
						state.isInvincible = false;
					}
				}
			}, 3f);
		}
	}

}