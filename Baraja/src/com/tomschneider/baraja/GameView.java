package com.tomschneider.baraja;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.view.View;

public class GameView extends View {
	private Context mContext;
	
	private float scale;
	private int screenWidth, screenHeight;

	public GameView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		
		mContext = context;
		
		scale = mContext.getResources().getDisplayMetrics().density;
	}
	
	@Override
    public void onSizeChanged (int w, int h, int oldW, int oldH) {
        super.onSizeChanged(w, h, oldW, oldH);
        screenWidth = w;
        screenHeight = h;
    }
	
	@Override
	protected void onDraw(Canvas canvas) {
		canvas.drawColor(Color.DKGRAY);
	}

}
