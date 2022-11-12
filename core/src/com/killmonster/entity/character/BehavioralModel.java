package com.killmonster.entity.character;

import java.util.concurrent.ThreadLocalRandom;
import com.killmonster.util.Utils;
import com.badlogic.gdx.math.Vector2;

public class BehavioralModel {

	private enum Direction { LEFT, RIGHT };


	private Character character;
	
	/* The following variables are used in moveRandomly() */
	private Direction direction;
	private float moveDuration;
	private float moveTimer;
	private float waitDuration;
	private float waitTimer;
	
	/* The following variables are used in jumpIfStucked() */
	private Vector2 lastStoppedPosition;
	private float lastTraveledDistance;
	private float calculateDistanceTimer;
	
	public BehavioralModel(Character character) {
		this.character = character;
		lastStoppedPosition = new Vector2();
	}


	/**
	 * Moves the character toward the specified target.
	 * @param c the target character to move to.
	 */
	public void moveTowardTarget(Character c) {
		if (character.getBody().getPosition().x > c.getBody().getPosition().x) {
			character.moveLeft();
		} else {
			character.moveRight();
		}
	}

	/**
	 * Makes the character move randomly, either to its left or to its right.
	 * The character will move for a given period of time, and then sleep for another given period of time.
	 * @param delta delta time.
	 * @param minMoveDuration minimum amount of time the character will keep moving.
	 * @param maxMoveDuration maximum amount of time the character will keep moving.
	 * @param minWaitDuration minimum amount of time the character will wait after moving.
	 * @param maxWaitDuration maximum amount of time the character will wait after moving.
	 */
	public void moveRandomly(float delta, int minMoveDuration, int maxMoveDuration, int minWaitDuration, int maxWaitDuration) {
		// If the character has finished moving and waiting, regenerate random values for
		// moveDuration and waitDuration within the specified range.
		if (moveTimer >= moveDuration && waitTimer >= waitDuration) {
			direction = Direction.values()[ThreadLocalRandom.current().nextInt(0, 1 + 1)];
			moveDuration = ThreadLocalRandom.current().nextInt(minMoveDuration, maxMoveDuration + 1);
			waitDuration = ThreadLocalRandom.current().nextInt(minWaitDuration, maxWaitDuration + 1);
			
			moveTimer = 0;
			waitTimer = 0;
		}

		if (moveTimer < moveDuration) {
			switch (direction) {
				case LEFT:
					character.moveLeft();
					break;
				
				case RIGHT:
					character.moveRight();
					break;
				
				default:
					break;
            }

			// Make sure the character doesn't get stucked somewhere along the way.
			jumpIfStucked(delta, .1f);
			
			moveTimer += delta;
		} else {
			waitTimer += delta;
		}
	}

	/**
	 * Reverses the current moving direction of character. Must be used with moveRandomly().
	 */
	public void reverseDirection() {
		direction = (direction == Direction.LEFT) ? Direction.RIGHT : Direction.LEFT;
	}

	/**
	 * This will make the character jump if it is stucked somewhere by
	 * checking the distance it had traveled. If it is stucked, the travel distance
	 * will be zero.
	 * @param delta delta time.
	 * @param checkInterval interval between checks.
	 */
	public void jumpIfStucked(float delta, float checkInterval) {
		if (calculateDistanceTimer > checkInterval) {
			lastTraveledDistance = Utils.getDistance(character.getBody().getPosition().x, lastStoppedPosition.x);
			lastStoppedPosition.set(character.getBody().getPosition());
			
			if (lastTraveledDistance == 0) {
				character.jump();
			}
			
			calculateDistanceTimer = 0;
		} else {
			calculateDistanceTimer += delta;
		}
	}

}