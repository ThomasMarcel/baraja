package com.tomschneider.baraja;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

public class GameView extends View {
	private Context mContext;
	
	private static final String TAG = "Baraja";
	
	private float scale;
	private int screenWidth, screenHeight;
	
	private ArrayList<Card> deck = new ArrayList<Card>();
	private ArrayList<Card> cardsPlayed = new ArrayList<Card>();
	private ArrayList<Card> hand = new ArrayList<Card>();
	
	private int initCardsNumber;
	private static final int MIN_SEQUENCE = 2;
	
	private int movingCardIdx = -1;
	private int movingX;
	private int movingY;
	
	private int scaledCardW, scaledCardH;
	
	private Paint mPaint, textPaint;
	
	private static String stringEndTurn;
	private Rect endTurnBounds;
	
	private Bitmap cardBack;
	
	private boolean mTurn;
	
	// Card drawn by player at each turn
	private ArrayList<Card> cardDrawn = new ArrayList<Card>();
	
	// Cards the player wants to play
	private ArrayList<Card> choosenCards = new ArrayList<Card>();

	public GameView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		
		mContext = context;
		
		stringEndTurn = mContext.getString(R.string.end_turn);
		
		scale = mContext.getResources().getDisplayMetrics().density;
		
		initCardsNumber = 8;
		
		textPaint = new Paint();
		textPaint.setAntiAlias(true);
		textPaint.setTextAlign(Paint.Align.LEFT);
		textPaint.setStyle(Paint.Style.STROKE);
		textPaint.setTextSize(scale * 30);
		textPaint.setColor(Color.WHITE);
		endTurnBounds = new Rect();
		textPaint.getTextBounds(stringEndTurn, 0, stringEndTurn.length(), endTurnBounds);
		
		//mTurn = new Random().nextBoolean();
		mTurn = true;
	}
	
	@Override
    public void onSizeChanged (int w, int h, int oldW, int oldH) {
        super.onSizeChanged(w, h, oldW, oldH);
        screenWidth = w;
        screenHeight = h;
        
        scaledCardW = (int) (screenWidth / 10);
		scaledCardH = (int) (scaledCardW * 1.28);
        
        Bitmap tempBitmap = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.card_back);
        cardBack = Bitmap.createScaledBitmap(tempBitmap, scaledCardW, scaledCardH, false);
        
        initCards();
        dealCards();
    }
	
	@Override
	protected void onDraw(Canvas canvas) {
		canvas.drawColor(Color.DKGRAY);
		mPaint = new Paint();
		
		// Draw the end turn button
		canvas.drawText(stringEndTurn, screenWidth - endTurnBounds.width() - 15, screenHeight - 15, textPaint);
		
		// Draw the pickup pile
		if (! deck.isEmpty()) {
			canvas.drawBitmap(cardBack, (screenWidth / 2) - scaledCardW - 5, 
					((screenHeight / 4) - (scaledCardH / 2)), mPaint);
		}
		
		// Draw the card the player has to draw from the pickup pile at each turn
		if (! cardDrawn.isEmpty()) {
			canvas.drawBitmap(cardDrawn.get(0).getBitmap(), (screenWidth / 2) + 5,
					((screenHeight / 4) - (scaledCardH / 2)), mPaint);
		}
		
		// Draw the cards the player is choosing to play
		if (! choosenCards.isEmpty()) {
			for (int i = 0; i < choosenCards.size(); i++) {
				canvas.drawBitmap(choosenCards.get(i).getBitmap(), (float) (i * (scaledCardW + 5)),
						((screenHeight * 3 / 4) - (scaledCardH / 2)), mPaint);
			}
		}
		
		// Draw the player's hand
		for (int i = 0; i < hand.size(); i++) {
			canvas.drawBitmap(hand.get(i).getBitmap(), (float) (i * (scaledCardW + 5)), (float) ((screenHeight - scaledCardH - (endTurnBounds.height() * 2))), mPaint);
			
			// If the user clicked on a card, draw an extra card of the same tipe
			if (i == movingCardIdx) {
				canvas.drawBitmap(hand.get(i).getBitmap(), movingX - (scaledCardW / 2), movingY - (scaledCardH / 2), mPaint);
			}
		}
		
		invalidate();
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		int x = (int) event.getX();
		int y = (int) event.getY();
		
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			if (mTurn) {
				for (int i = 0; i < hand.size(); i++) {
					if (x > i * (scaledCardW + 5) &&
							x < (i * (scaledCardW + 5)) + scaledCardW &&
							y > (screenHeight - scaledCardH - (2 * endTurnBounds.height())) &&
							y < (screenHeight - scaledCardH - (2 * endTurnBounds.height())) + scaledCardH) {
						movingCardIdx = i;
						movingX = x;
						movingY = y;
					}
				}
				
				if (movingCardIdx < 0) {
					if (cardDrawn.isEmpty() &&
							x > (screenWidth / 2) - scaledCardW - 5 &&
							x < (screenWidth / 2) - 5 &&
							y > ((screenHeight / 4) - (scaledCardH / 2)) &&
							y < ((screenHeight / 4) + (scaledCardH / 2))) {
						drawCard(cardDrawn);
					}
					
					if (! choosenCards.isEmpty()) {
						for (int i = 0; i < choosenCards.size(); i++) {
							if (x > i * (scaledCardW + 5) &&
									x < (i * (scaledCardW + 5)) + scaledCardW &&
									y > ((screenHeight * 3 / 4) - (scaledCardH / 2)) &&
									y < ((screenHeight * 3 / 4) - (scaledCardH / 2)) + scaledCardH) {
								hand.add(choosenCards.get(i));
								choosenCards.remove(i);
							}
						}
					}
					
					if (x > screenWidth - endTurnBounds.width() - 15 &&
							x < screenWidth - 15 &&
							y > screenHeight - endTurnBounds.height() - 15 &&
							y < screenHeight - 15) {
						if (isValidMove()) {
							Log.i(TAG, "End turn with valid move");
						} else {
							Log.i(TAG, "End turn with invalid move");
						}
					}
				}
			}
			break;
		
		case MotionEvent.ACTION_UP:
			if (movingCardIdx >= 0 &&
					y < ((screenHeight * 3 / 4) - (scaledCardH / 2)) + scaledCardH) {
				choosenCards.add(hand.get(movingCardIdx));
				hand.remove(movingCardIdx);
			}
			movingCardIdx = -1;
			break;
			
		case MotionEvent.ACTION_MOVE:
			movingX = x;
			movingY = y;
			break;
		}
		
		invalidate();
		return true;
	}
	
	private void initCards() {
		for (int i = 0; i < 4; i++) {
			for (int j = 101; j < 111; j++) {
				int tempId = j + (i * 100);
				Card tempCard = new Card(tempId);
				int resourceId = getResources().getIdentifier("card_" + tempId, "drawable", mContext.getPackageName());
				Bitmap tempBitmap = BitmapFactory.decodeResource(mContext.getResources(), resourceId);
				Bitmap scaledBitmap = Bitmap.createScaledBitmap(tempBitmap, scaledCardW, scaledCardH, false);
				tempCard.setBitmap(scaledBitmap);
				deck.add(tempCard);
			}
		}
	}
	
	private boolean drawCard(List<Card> handToDraw) {
		if (deck.size() > 0) {
			handToDraw.add(0, deck.get(0));
			deck.remove(0);
			return true;
		} else {
			return false;
		}
	}
	
	private void dealCards() {
		Collections.shuffle(deck, new Random());
		for (int i = 0; i < initCardsNumber; i++) {
			drawCard(hand);
		}
	}
	
	private boolean isValidMove() {
		ArrayList<Card> tempCards = new ArrayList<Card>();
		ArrayList<Card> wellPlayed = new ArrayList<Card>();
		
		if (! cardDrawn.isEmpty()) {
			for (Card card : choosenCards) {
				tempCards.add(card);
			}
			tempCards.add(cardDrawn.get(0));
			
			Collections.sort(tempCards);
			int sequence = 1;
			for (int i = 1; i < tempCards.size(); i++) {
				if (tempCards.get(i).getSuit() == tempCards.get(i - 1).getSuit() &&
						tempCards.get(i).getRank() == tempCards.get(i - 1).getRank() + 1) {
					sequence += 1;
				}
				
				Log.i(TAG, "Sequence: " + sequence);
				
				if (sequence >= MIN_SEQUENCE) {
					Log.i(TAG, "Adding well played cards");
					for (int j = i; j > i - sequence; j--) {
						if (! wellPlayed.contains(tempCards.get(i))) {
							wellPlayed.add(tempCards.get(i));
						}
					}
				}
			}
			
			if (! wellPlayed.contains(cardDrawn.get(0))) {
				wellPlayed.add(cardDrawn.get(0));
			}
			
			Log.i(TAG, "Well played cards: " + wellPlayed.toString());
			Log.i(TAG, "tempCards: " + tempCards.toString());
			
			if (tempCards.size() == wellPlayed.size()) {
				Toast.makeText(mContext, "Valid move", Toast.LENGTH_LONG).show();
				return true;
			} else {
				Toast.makeText(mContext, "Invalid move", Toast.LENGTH_LONG).show();
				return false;
			}
		} else {
			Toast.makeText(mContext, mContext.getString(R.string.error_no_drawn_card), Toast.LENGTH_LONG).show();
			return false;
		}
	}

}
