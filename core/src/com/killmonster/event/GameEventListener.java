package com.killmonster.event;

public interface GameEventListener<T extends GameEvent> {
	
	public void handle(T e);
	
}
