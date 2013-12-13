package com.proggame.utils;

public interface GameOverListener {
	
	public enum GameOverEvent {
		GOOD, BAD, CHEAT;
	}

	public void gameOverEventOccured(GameOverEvent event);
}
