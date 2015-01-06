package com.tomschneider.baraja;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;

public class GameActivity extends Activity implements GameNotificationListener {

	private EndTurn mEndTurn;
	private Context mContext;
	private static final String TAG = "Baraja";
	
	GameView gameView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		mContext = getApplicationContext();
		
		gameView = new GameView(this);
		gameView.setKeepScreenOn(true);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        
        gameView.setGameNotificationListener(this);
        
		setContentView(gameView);
	}

	@Override
	public void onEvent(int eventId) {
		Log.i(TAG, "GameActivity.onEvent");
		// TODO Auto-generated method stub
		switch (eventId) {
		case GameNotificationListener.ENDTURN_DIALOG:
			mEndTurn = EndTurn.newInstance(GameNotificationListener.ENDTURN_DIALOG);
			mEndTurn.show(getFragmentManager(), TAG);
			break;
		case GameNotificationListener.ENDTURN:
			gameView.setTurn(false);
			break;
		case GameNotificationListener.ENDGAME_DIALOG:
			mEndTurn = EndTurn.newInstance(GameNotificationListener.ENDGAME_DIALOG);
			mEndTurn.show(getFragmentManager(), TAG);
			break;
		case GameNotificationListener.ENDGAME:
			finish();
		}
	}
}
