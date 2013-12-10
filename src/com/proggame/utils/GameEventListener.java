package com.proggame.utils;

public interface GameEventListener {

	public enum GameEvent {
		GAME_OVER_GOOD, GAME_OVER_BAD, MOVE_DOWN, MOVE_RIGHT;
	}
	
	public void EventOccured(GameEvent event);
}
