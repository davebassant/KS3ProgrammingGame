package com.proggame.utils;

public interface GameEventListener {

	public enum GameEvent {
		GAME_OVER_GOOD, GAME_OVER_BAD;
	}
	
	public void eventOccured(GameEvent event);
}
