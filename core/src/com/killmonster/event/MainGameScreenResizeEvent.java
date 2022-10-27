package com.killmonster.event;

public class MainGameScreenResizeEvent extends GameEvent {
	
	private final int viewportX;
	private final int viewportY;
	private final int viewportWidth;
	private final int viewportHeight;

	public MainGameScreenResizeEvent(int viewportX, int viewportY, int viewportWidth, int viewportHeight) {
		super(GameEventType.MAINGAME_SCREEN_RESIZED);
		
		this.viewportX = viewportX;
		this.viewportY = viewportY;
		this.viewportWidth = viewportWidth;
		this.viewportHeight = viewportHeight;
	}
	
	public int getViewportX() {
		return viewportX;
	}
	
	public int getViewportY() {
		return viewportY;
	}
	
	public int getViewportWidth() {
		return viewportWidth;
	}
	
	public int getViewportHeight() {
		return viewportHeight;
	}
	
}
