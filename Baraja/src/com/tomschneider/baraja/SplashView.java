package com.tomschneider.baraja;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

public class SplashView extends View {
	
	private Context mContext;
	
	private int screenWidth, screenHeight;
	
	private Bitmap titleGraphic;
	private Paint mPaint;

	public SplashView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		mContext = context;
		
		mPaint = new Paint();
	}
	
	@Override
	public void onSizeChanged(int w, int h, int oldW, int oldH) {
		screenWidth = w;
		screenHeight = h;
	}
	
	@Override
	public void onDraw(Canvas canvas) {
		
	}
}
