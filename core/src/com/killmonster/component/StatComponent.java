package com.killmonster.component;

import com.badlogic.ashley.core.Component;

public class StatComponent implements Component{
	
	public enum State { IDLE, RUNNING, JUMPING, FALLING, ATTACKING, KILLED };
	
	private State state;
	public float time;
	public boolean isLooping;
	
	public StatComponent(State state) {
		this.state = state;
	}
	
	public void setState(State state) {
		this.state = state;
	}
	
	public State getState() {
		return state;
	}
	
}
