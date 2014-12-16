package com.tomschneider.baraja;

public interface GameNotificationListener {
	public static final int ENDGAME_DIALOG = 1;
	public static final int ENDTURN_DIALOG = 2;
	public static final int ENDTURN = 3;
	
	void onEvent(int evnetId);
}
