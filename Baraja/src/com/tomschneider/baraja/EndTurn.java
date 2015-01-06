package com.tomschneider.baraja;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;

public class EndTurn extends DialogFragment {
	
	private static final String TAG = "Baraja";
	
	private GameNotificationListener activity;
	
	private static int option = -1;
	
	public static EndTurn newInstance(int newOption) {
		option = newOption;
		return new EndTurn();
	}
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		activity = (GameNotificationListener) getActivity();		
		
		switch (option) {
		case GameNotificationListener.ENDGAME_DIALOG:
			return new AlertDialog.Builder(getActivity())
			.setMessage(R.string.game_over)
			.setCancelable(false)
			.setPositiveButton(R.string.dialog_ok, new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					Log.i(TAG, "Positive button " + which);
					activity.onEvent(GameNotificationListener.ENDGAME);
				}
			}).create();
		default:
			return new AlertDialog.Builder(getActivity())
						.setMessage(R.string.end_turn_confirmation)
						.setCancelable(false)
						.setPositiveButton(R.string.dialog_ok, new DialogInterface.OnClickListener() {
							
							@Override
							public void onClick(DialogInterface dialog, int which) {
								// TODO Auto-generated method stub
								Log.i(TAG, "Positive button " + which);
								activity.onEvent(GameNotificationListener.ENDTURN);
							}
						}).setNegativeButton(R.string.dialog_decline, new DialogInterface.OnClickListener() {
							
							@Override
							public void onClick(DialogInterface dialog, int which) {
								// TODO Auto-generated method stub
								Log.i(TAG, "Negative button " + which);
							}
						}).create();
		}
	}
}
