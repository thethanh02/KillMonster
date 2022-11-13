package com.killmonster.map;

import com.killmonster.entity.character.*;
import com.killmonster.entity.character.Character;
import com.killmonster.entity.objects.*;
import com.killmonster.entity.shooter.Bullet;
import com.killmonster.util.CategoryBits;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;

public class WorldContactListener implements ContactListener {

	public WorldContactListener() {
	}

	@Override
	public void beginContact(Contact contact) {
		// Some variables that might get reused for several times.
		Character character;
		Player player;
		Enemy enemy;
		Potion potion;
		Box box;
		Bullet bullet;
		
		Fixture fixtureA = contact.getFixtureA();
		Fixture fixtureB = contact.getFixtureB();
		int cDef = fixtureA.getFilterData().categoryBits | fixtureB.getFilterData().categoryBits;
		
		switch (cDef) {
			// When a character lands on the ground, make following changes.
			case CategoryBits.FEET | CategoryBits.GROUND:
				character = (Character) getTargetFixture(CategoryBits.FEET, fixtureA, fixtureB).getUserData();
				character.setIsJumping(false);
				break;
			
			// When a character lands on a platform, make following changes.
			case CategoryBits.FEET | CategoryBits.PLATFORM:
				character = (Character) getTargetFixture(CategoryBits.FEET, fixtureA, fixtureB).getUserData();
				character.setIsJumping(false);
				character.setIsOnPlatform(true);
				break;
			
			case CategoryBits.PLAYER | CategoryBits.DEATHPLACE:
				player = (Player) getTargetFixture(CategoryBits.PLAYER, fixtureA, fixtureB).getUserData();
				player.SetToDestroy();
				break;
			
			case CategoryBits.ENEMY | CategoryBits.DEATHPLACE:
				enemy = (Enemy) getTargetFixture(CategoryBits.ENEMY, fixtureA, fixtureB).getUserData();
				enemy.SetToDestroy();
				break;	
				
			// When a player bumps into an enemy, the enemy will inflict damage and knockback to the player.
			case CategoryBits.PLAYER | CategoryBits.ENEMY:
				player = (Player) getTargetFixture(CategoryBits.PLAYER, fixtureA, fixtureB).getUserData();
				enemy = (Enemy) getTargetFixture(CategoryBits.ENEMY, fixtureA, fixtureB).getUserData();
				enemy.inflictDamage(player, 25);
				break;
			
			// When an NPC hits a cliff marker, reverse the NPC's current direction.
			case CategoryBits.ENEMY | CategoryBits.CLIFF_MARKER:
				enemy = (Enemy) getTargetFixture(CategoryBits.ENEMY, fixtureA, fixtureB).getUserData();
				enemy.getBehavioralModel().reverseDirection();
				break;
			
			// Set enemy as player's current target (so player can inflict damage to enemy).
			case CategoryBits.MELEE_WEAPON | CategoryBits.ENEMY:
				player = (Player) getTargetFixture(CategoryBits.MELEE_WEAPON, fixtureA, fixtureB).getUserData();
				enemy = (Enemy) getTargetFixture(CategoryBits.ENEMY, fixtureA, fixtureB).getUserData();
				player.setInRangeTarget(enemy);
				break;
			
			// Set player as enemy's current target (so enemy can inflict damage to player).
			case CategoryBits.MELEE_WEAPON | CategoryBits.PLAYER:
				player = (Player) getTargetFixture(CategoryBits.PLAYER, fixtureA, fixtureB).getUserData();
				enemy = (Enemy) getTargetFixture(CategoryBits.MELEE_WEAPON, fixtureA, fixtureB).getUserData();
				enemy.setInRangeTarget(player);
				break;
			    
			case CategoryBits.MELEE_WEAPON | CategoryBits.BOX:
				player = (Player) getTargetFixture(CategoryBits.MELEE_WEAPON, fixtureA, fixtureB).getUserData();
				box = (Box) getTargetFixture(CategoryBits.BOX, fixtureA, fixtureB).getUserData();
				player.setInRangeTarget(box);
				break;
				
			case CategoryBits.PLAYER | CategoryBits.POTION:
				player = (Player) getTargetFixture(CategoryBits.PLAYER, fixtureA, fixtureB).getUserData();
				potion = (Potion) getTargetFixture(CategoryBits.POTION, fixtureA, fixtureB).getUserData();
				potion.healing(player);
				potion.SetToDestroy();
				break;
				
			case CategoryBits.PLAYER | CategoryBits.BULLET:
				player = (Player) getTargetFixture(CategoryBits.PLAYER, fixtureA, fixtureB).getUserData();
				bullet = (Bullet) getTargetFixture(CategoryBits.BULLET, fixtureA, fixtureB).getUserData();
				bullet.inflictDamage(player, 30);
				bullet.SetToDestroy();;
				break;
				
			case CategoryBits.WALL | CategoryBits.BULLET:
				bullet = (Bullet) getTargetFixture(CategoryBits.BULLET, fixtureA, fixtureB).getUserData();
				bullet.SetToDestroy();;
				break;
				
			default:
				break;
		}
	}

	@Override
	public void endContact(Contact contact) {
		// Some variables that might get reused for several times.
		Character character;
		Player player;
		Enemy enemy;
		
		Fixture fixtureA = contact.getFixtureA();
		Fixture fixtureB = contact.getFixtureB();
		int cDef = fixtureA.getFilterData().categoryBits | fixtureB.getFilterData().categoryBits;
		
		switch (cDef) {
			// When a character leaves the ground, make following changes.
			case CategoryBits.FEET | CategoryBits.GROUND:
				character = (Character) getTargetFixture(CategoryBits.FEET, fixtureA, fixtureB).getUserData();
				if (character.getBody().getLinearVelocity().y > .5f) {
					character.setIsJumping(true);
				}
				break;
			
			// When a character leaves the platform, make following changes.
			case CategoryBits.FEET | CategoryBits.PLATFORM:
				character = (Character) getTargetFixture(CategoryBits.FEET, fixtureA, fixtureB).getUserData();
				if (character.getBody().getLinearVelocity().y < -.5f) {
					character.setIsJumping(true);
					character.setIsOnPlatform(false);
				}
				break;
			
			// Clear player's current target (so player cannot inflict damage to enemy from a distance).
			case CategoryBits.MELEE_WEAPON | CategoryBits.ENEMY:
				player = (Player) getTargetFixture(CategoryBits.MELEE_WEAPON, fixtureA, fixtureB).getUserData();
				player.setInRangeTarget(null);
				break;
			
			// Clear enemy's current target (so enemy cannot inflict damage to player from a distance).
			case CategoryBits.MELEE_WEAPON | CategoryBits.PLAYER:
				enemy = (Enemy) getTargetFixture(CategoryBits.MELEE_WEAPON, fixtureA, fixtureB).getUserData();
				enemy.setInRangeTarget(null);
				break;
			    
			case CategoryBits.MELEE_WEAPON | CategoryBits.BOX:
				player = (Player) getTargetFixture(CategoryBits.MELEE_WEAPON, fixtureA, fixtureB).getUserData();
				player.setInRangeTarget(null);
				break;
				
			default:
				break;
		}
	}

	@Override
	public void preSolve(Contact contact, Manifold oldManifold) {
		
		Fixture fixtureA = contact.getFixtureA();
		Fixture fixtureB = contact.getFixtureB();
		
		int cDef = fixtureA.getFilterData().categoryBits | fixtureB.getFilterData().categoryBits;
		
		switch (cDef) {
			// Allow player to pass through platforms and collide on the way down.
			case CategoryBits.PLAYER | CategoryBits.PLATFORM:
				Fixture playerBody = getTargetFixture(CategoryBits.PLAYER, fixtureA, fixtureB);
				Fixture platform = getTargetFixture(CategoryBits.PLATFORM, fixtureA, fixtureB);
				
				float playerY = playerBody.getBody().getPosition().y;
				float platformY = platform.getBody().getPosition().y;
				
				// Enable contact if the player is about to land on the platform.
				// .15f is a value that works fine in my world.
				contact.setEnabled((playerY > platformY + .15f));
				break;
			    
			default:
				break;
		}
        
	}

	@Override
	public void postSolve(Contact contact, ContactImpulse impulse) {
    
	}


	/**
	 * Gets the target fixture which holds the specified CategoryBits from two candidates.
	 * @param targetCategoryBits target category bits.
	 * @param fixtureA candidate A.
	 * @param fixtureB candidate B.
	 * @return target fixture. If the target cannot be found, it returns null.
	 */
	public static Fixture getTargetFixture(short targetCategoryBits, Fixture fixtureA, Fixture fixtureB) {
		Fixture targetFixture;
		
		if (fixtureA.getFilterData().categoryBits == targetCategoryBits) {
			targetFixture = fixtureA;
		} else if (fixtureB.getFilterData().categoryBits == targetCategoryBits) {
			targetFixture = fixtureB;
		} else {
			targetFixture = null;
		}
		
		return targetFixture;
	}

}