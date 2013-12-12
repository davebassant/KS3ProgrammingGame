package com.proggame.utils;

public interface GameOverListener {
	
	public enum GameOverEvent {
		GOOD, BAD;
	}

	public void gameOverEventOccured(GameOverEvent event);
}
