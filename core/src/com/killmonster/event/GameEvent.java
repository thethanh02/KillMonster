package com.killmonster.event;

public class GameEvent {
	
	private final GameEventType gameEventType;
	
	public GameEvent(GameEventType gameEventType) {
		this.gameEventType = gameEventType;
	}
	public GameEventType getGameEventType() {
		return gameEventType;
	}
	
}
