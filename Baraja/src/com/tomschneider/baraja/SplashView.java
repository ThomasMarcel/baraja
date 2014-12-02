package com.tomschneider.baraja;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

public class SplashView extends View {
	
	private static final String TAG = "Baraja";
	
	private Context mContext;
	
	private int screenWidth, screenHeight;
	
	private Bitmap titleGraphic;
	private Paint mPaint;
	private float scale;
	
	private static String stringPlay;
	private static String stringCredits;
	
	private boolean playButtonPressed = false;
	
	private Rect bounds;
	int stringPlayX, stringPlayY;

	public SplashView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		mContext = context;
		
		stringPlay = mContext.getString(R.string.play);
		stringCredits = mContext.getString(R.string.credits);
		
		scale = mContext.getResources().getDisplayMetrics().density;
		
		mPaint = new Paint();
		mPaint.setAntiAlias(true);
		mPaint.setTextAlign(Paint.Align.LEFT);
		mPaint.setStyle(Paint.Style.STROKE);
		mPaint.setTextSize(scale * 30);
		mPaint.setColor(Color.WHITE);
		
		titleGraphic = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.card_front);
		
		bounds = new Rect();
		
		mPaint.getTextBounds(stringPlay, 0, stringPlay.length(), bounds);
	}
	
	@Override
	public void onSizeChanged(int w, int h, int oldW, int oldH) {
		screenWidth = w;
		screenHeight = h;
		stringPlayX = (w - bounds.width()) / 2;
		stringPlayY = h * 5 / 6;
		
		titleGraphic = Bitmap.createScaledBitmap(titleGraphic, (int) (screenWidth * 0.7), (int) ((screenWidth * 0.7) * MainActivity.CARD_PROPORTIONS_FACTOR), false);
	}
	
	@Override
	public void onDraw(Canvas canvas) {
		canvas.drawBitmap(titleGraphic, (screenWidth - titleGraphic.getWidth()) / 2,
				screenHeight / 6, mPaint);
		canvas.drawText(stringPlay, stringPlayX, stringPlayY, mPaint);
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		int x = (int) event.getX();
		int y = (int) event.getY();
		
		Log.i(TAG, "Limits X: [" +
				(screenWidth - bounds.width()) / 2 + ":" +
				(((screenWidth - bounds.width()) / 2) + bounds.width()) + "] y: [" +
				(stringPlayY -  (bounds.height() / 2)) + ":" +
				(stringPlayY + (bounds.height() / 2)) + "]");
		
		switch(event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			if (x > (screenWidth - bounds.width()) / 2 &&
					x < ((screenWidth - bounds.width()) / 2 + bounds.width()) &&
					y > stringPlayY - (bounds.height() / 2) &&
					y < stringPlayY + (bounds.height() / 2)) {
				//Toast.makeText(mContext, "Play", Toast.LENGTH_LONG).show();
				Intent gameIntent = new Intent(mContext, GameActivity.class);
				mContext.startActivity(gameIntent);
			}
			Log.i(TAG, "Click on [" + x + ":" + y + "]");
			break;
		case MotionEvent.ACTION_MOVE:
			break;
		case MotionEvent.ACTION_UP:
			break;
		}
		
		return true;
	}
}
