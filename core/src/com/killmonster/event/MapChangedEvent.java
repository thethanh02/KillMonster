package com.killmonster.event;

import com.killmonster.map.GameMap;

public class MapChangedEvent extends GameEvent {

	private GameMap gameMap;

	public MapChangedEvent(GameMap gameMap) {
		super(GameEventType.MAP_CHANGED);
		this.gameMap = gameMap;
	}
	
	public GameMap getGameMap() {
		return gameMap;
	}
	
}
