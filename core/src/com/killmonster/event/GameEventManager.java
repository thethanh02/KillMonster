package com.killmonster.event;

import java.util.HashMap;

import com.badlogic.gdx.utils.Array;

public class GameEventManager {
	
	private static GameEventManager INSTANCE;
	
	private HashMap<GameEventType, Array<GameEventListener>> listeners;
	
	public GameEventManager() {
		listeners = new HashMap<>();
		
		listeners.put(GameEventType.MAP_CHANGED, new Array<>());
		listeners.put(GameEventType.MAP_COMPLETED, new Array<>());
		listeners.put(GameEventType.MAINGAME_SCREEN_RESIZED, new Array<>());
		
	}
	
	public static GameEventManager getINSTANCE() {
		if (INSTANCE == null)
			INSTANCE = new GameEventManager();
		
		return INSTANCE;
	}
	
	public void addEventListener(GameEventType gameEventType, GameEventListener<? extends GameEvent> handler) {
		this.listeners.get(gameEventType).add(handler);
	}
	
	public void clearEventListeners() {
		for (Array<GameEventListener> obj : listeners.values())
			obj.clear();
	}
	
	public void fireEvent(GameEvent gameEvent) {
		GameEventType gameEventType = gameEvent.getGameEventType();
		
		for (GameEventListener<? super GameEvent> obj : listeners.get(gameEventType))
			obj.handle(gameEvent);
	}
}
