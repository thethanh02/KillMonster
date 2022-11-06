package com.killmonster.component;

import com.badlogic.ashley.core.Component;

public class StateComponent implements Component{
		
	private CharacterState previousState;
	private CharacterState currentState;
	public float time;
	public boolean isLooping;
	
	public boolean isAlerted;
	public boolean facingRight;
	public boolean isJumping;
	public boolean isOnPlatform;
	public boolean isAttacking;
	public boolean isInvincible;
	public boolean isKilled;
	public boolean setToKill;
	
	public StateComponent(CharacterState state) {
		currentState = state;
		facingRight = true;
	}

	public CharacterState getPreviousState() {
		return previousState;
	}
	
	public CharacterState getCurrentState() {
		return currentState;
	}
	
	public void setCurrentState(CharacterState currentState) {
		this.previousState = this.currentState;
		this.currentState = currentState;
	}

	public boolean isAlerted() {
		return isAlerted;
	}

	public boolean isFacingRight() {
		return facingRight;
	}

	public boolean isJumping() {
		return isJumping;
	}

	public boolean isOnPlatform() {
		return isOnPlatform;
	}

	public boolean isAttacking() {
		return isAttacking;
	}

	public boolean isInvincible() {
		return isInvincible;
	}

	public boolean isKilled() {
		return isKilled;
	}

	public boolean isSetToKill() {
		return setToKill;
	}
	
}
