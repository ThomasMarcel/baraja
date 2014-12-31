package com.tomschneider.baraja;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import android.app.Activity;
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
	private static Context mContext;
	
	private static final String TAG = "Baraja";
	
	private float scale;
	private int screenWidth, screenHeight;
	
	private ArrayList<Card> deck = new ArrayList<Card>();
	private ArrayList<Card> cardsPlayed = new ArrayList<Card>();
	private ArrayList<Card> hand = new ArrayList<Card>();
	
	private int initCardsNumber;
	private static final int MIN_SEQUENCE = 3;
	
	private int movingCardIdx = -1;
	private int movingX;
	private int movingY;
	
	private int scaledCardW, scaledCardH;
	
	private Paint mPaint, textPaint;
	
	private static String stringEndTurn;
	private Rect endTurnBounds;
	private static String stringTurn;
	
	private Bitmap cardBack;
	
	private boolean mTurn;
	private int mTurnNumber = 1;
	
	// Card drawn by player at each turn
	private ArrayList<Card> cardDrawn = new ArrayList<Card>();
	
	// Cards the player wants to play
	private ArrayList<Card> choosenCards = new ArrayList<Card>();
	
	ArrayList<GameNotificationListener> notificationListeners = new ArrayList<GameNotificationListener>();
	
	private static final int NUMBER_OF_OPPONENTS = 1;
	private Opponent[] opponents;

	public GameView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		
		mContext = context;
		
		stringEndTurn = mContext.getString(R.string.end_turn);
		stringTurn = mContext.getString(R.string.turn);
		
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
		
		opponents = new Opponent[NUMBER_OF_OPPONENTS];
		
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
		
		// Draw the turn number
		canvas.drawText(stringTurn + " " + mTurnNumber, 5.0f, 5.0f + endTurnBounds.height(), textPaint);
		
		// Draw the opponent's hands
		for (int i = 0; i < opponents.length; i++) {
			//int cardX = (screenWidth / (opponents.length + 1));
			int cardY = 5 + endTurnBounds.height();
			for (int j = 0; j < opponents[i].getHand().size(); j++) {
				int cardX = (screenWidth - ((scaledCardW + (opponents[i].getHand().size() * 15)))) / (opponents.length + 1);
				canvas.drawBitmap(cardBack, (float) (cardX + (j * 15)),  (float) cardY, mPaint);
			}
			//Log.i(TAG, "Cards in opponent's hand (" + opponents[i].getHand().size() + "): " + opponents[i].getHand());
		}
		
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
		
		// Draw the cards already played
		if (! cardsPlayed.isEmpty()) {
			float cardsPlayedTop = (screenHeight / 2) - (scaledCardH / 2);
			float cardsPlayedLeft = 0;
			int row = 1;
			int cardsInRow = 0;
			//Log.i(TAG, "Displaying cards played");
			for (int i = 0; i < cardsPlayed.size(); i++) {
				if (row == 1) {
					cardsPlayedLeft = i * (scaledCardW + 5);
				} else {
					cardsPlayedLeft = (i - (cardsInRow * (row - 1))) * (scaledCardW + 5);
				}
				if (cardsPlayedLeft + scaledCardW > screenWidth) {
					if (cardsInRow == 0) {
						cardsInRow = i;
					}
					cardsPlayedTop += scaledCardH;
					row += 1;
					cardsPlayedLeft = (i - (cardsInRow * (row - 1))) * (scaledCardW + 5);
					//Log.i(TAG, "Displaying cards played on row " + row + ". Cards per row: " + cardsInRow);
				}
				canvas.drawBitmap(cardsPlayed.get(i).getBitmap(), cardsPlayedLeft,
						cardsPlayedTop, mPaint);
				//Log.i(TAG, "i: " + i + ", cardsPlayed.size: " + cardsPlayed.size());
			}
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
		
		if (! mTurn) {
			endTurn();
		}
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
						if (! cardDrawn.isEmpty()) {
							ArrayList<Card> cardsToPlay = isValidMove(choosenCards, cardDrawn, cardsPlayed, false);
							if (cardsToPlay.size() == 1 && cardsToPlay.get(0).getId() == -1) {
								Log.i(TAG, "Invalid move, not ending turn");
							} else {
								Log.i(TAG, "End turn with valid move");
								endTurn();
							}
						} else {
							Toast.makeText(mContext, mContext.getString(R.string.error_no_drawn_card), Toast.LENGTH_LONG).show();
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

		for (int i = 0; i < NUMBER_OF_OPPONENTS; i++) {
			opponents[i] = new Opponent(mContext);
			for (int j = 0; j < initCardsNumber; j++) {
				drawCard(opponents[i].mHand);
			}
		}
	}
	
	public static ArrayList<Card> isValidMove(ArrayList<Card> mChoosenCards, ArrayList<Card> mCardDrawn, ArrayList<Card> mCardsPlayed, boolean automation) {
		ArrayList<Card> tempCards = new ArrayList<Card>();
		ArrayList<Card> wellPlayed = new ArrayList<Card>();
		
		if (! mCardDrawn.isEmpty()) {
			
			if (! mChoosenCards.isEmpty()) {
				for (Card card : mChoosenCards) {
					tempCards.add(card);
				}
			}
			
			tempCards.add(mCardDrawn.get(0));
			wellPlayed.add(mCardDrawn.get(0));
			
			if (! mCardsPlayed.isEmpty()) {
				for (Card card : mCardsPlayed) {
					tempCards.add(card);
					wellPlayed.add(card);
				}
			}
			
			Collections.sort(tempCards);
			int sequence = 1;
			for (int i = 1; i < tempCards.size(); i++) {
					
				if (tempCards.get(i).getSuit() == tempCards.get(i - 1).getSuit() &&
						tempCards.get(i).getRank() == tempCards.get(i - 1).getRank() + 1) {
					sequence += 1;
					//Log.i(TAG, "Sequence between i-i (" + tempCards.get(i - 1).getId() + ") and i (" + tempCards.get(i).getId() + ")");
				} else {
					sequence = 1;
				}
				
				//Log.i(TAG, "Sequence: " + sequence);
				
				if (sequence >= MIN_SEQUENCE) {
					//Log.i(TAG, "Adding well played cards");
					ArrayList<Card> cardSequence = new ArrayList<Card>();
					for (int j = i; j > i - sequence; j--) {
						if (! wellPlayed.contains(tempCards.get(j))) {
							wellPlayed.add(tempCards.get(j));
							cardSequence.add(tempCards.get(j));
							//Log.i(TAG, "Sequence match, adding " + tempCards.get(j).getId());
						}
					}
					Log.i(TAG, "Adding card sequence " + cardSequence + " to well played");
				}
			}
				
			//Log.i(TAG, "Checking for same rank different suit");
			for (int i = 0; i < mChoosenCards.size(); i++) {
				sequence = 1;
				ArrayList<Card> cardSequence = new ArrayList<Card>();
				cardSequence.add(mChoosenCards.get(i));
				for (int j = 0; j < tempCards.size(); j++) {
					if (mChoosenCards.get(i).getRank() == tempCards.get(j).getRank() &&
							mChoosenCards.get(i).getSuit() != tempCards.get(j).getSuit()) {
						sequence += 1;
						cardSequence.add(tempCards.get(j));
						//Log.i(TAG, "Found a possible match " + choosenCards.get(i).getId() + " - " + tempCards.get(j).getId());
					}
					
					if (sequence >= MIN_SEQUENCE) {
						Log.i(TAG, "Same rank sequence going on. Sequence: " + cardSequence);
						for (Card card : cardSequence) {
							if (! wellPlayed.contains(card)) {
								wellPlayed.add(card);
								//Log.i(TAG, "Same rank match, adding " + mChoosenCards.get(i).getId());
							}
						}
					}
				}
				cardSequence.clear();
				cardSequence.add(tempCards.get(i));
				sequence = 1;
			}
			
			//Log.i(TAG, "tempCards: " + tempCards.toString());
			
			if (! wellPlayed.contains(mCardDrawn.get(0))) {
				wellPlayed.add(mCardDrawn.get(0));
			}
				
			Log.i(TAG, "Well played cards: " + wellPlayed.toString());
			boolean validMove = true;
			for (Card card : mChoosenCards) {
				if (! automation && ! wellPlayed.contains(card)) {
					Log.i(TAG, "well played doesn't contain choosen card " + card + ". Wrong move.");
					validMove = false;
				}
			}
			if (! mCardsPlayed.isEmpty()) {
				for (int i = 0; i < mCardsPlayed.size(); i++) {
					if (wellPlayed.contains(mCardsPlayed.get(i))) {
						wellPlayed.remove(wellPlayed.indexOf(mCardsPlayed.get(i)));
					}
				}
			}
			if (! mCardDrawn.isEmpty()) {
				if (wellPlayed.contains(mCardDrawn.get(0))) {
					wellPlayed.remove(wellPlayed.indexOf(mCardDrawn.get(0)));
				}
			}
				
			if (validMove) {
				//Log.i(TAG, "Valid move");
				if (automation) {
					Log.i(TAG, "AI well played cards: " + wellPlayed);
				}
				//Toast.makeText(mContext, "Valid move", Toast.LENGTH_LONG).show();
				//return mChoosenCards;
				return wellPlayed;
			} else {
				wellPlayed.clear();
				wellPlayed.add(new Card(-1));
				//Toast.makeText(mContext, "Invalid move", Toast.LENGTH_LONG).show();
				return wellPlayed;
			}
		} else {
			return wellPlayed;
		}
	}

	private void endTurn() {
		//mTurn = false;
		//Log.i(TAG, "GameView.endTurn mTurn: " + mTurn);
		if (! hand.isEmpty()) {
			if (mTurn) {
				for (GameNotificationListener listener : notificationListeners) {
					listener.onEvent(GameNotificationListener.ENDTURN_DIALOG);
				}
			} else {
				for (GameNotificationListener listener : notificationListeners) {
					listener.onEvent(GameNotificationListener.ENDTURN);
				}
			}
		} else {
			Toast.makeText(mContext, mContext.getString(R.string.you_win), Toast.LENGTH_LONG).show();
			Log.i(TAG, "Yon win!");
		}
	}
	
	public void setGameNotificationListener(GameNotificationListener listener) {
		//Log.i(TAG, "GameView.setGameNotificationListener");
		if (! notificationListeners.contains(listener)) {
			notificationListeners.add(listener);
		}
	}
	
	public void setTurn(boolean turn) {
		if (mTurn && ! turn) {
			for (int i = 0; i < choosenCards.size(); i++) {
				cardsPlayed.add(choosenCards.get(i));
			}
			//cardsPlayed.add(cardDrawn.get(0));
			mTurn = false;
		} else {
			Log.i(TAG, "Opponents playing");
			Log.i(TAG, "Cards already played: " + cardsPlayed);
			for (Opponent opponent : opponents) {
				Log.i(TAG, "Opponent " + opponent.getName() + " playing with hand " + opponent.getHand());
				if (cardDrawn.isEmpty()) {
					drawCard(cardDrawn);
					Log.i(TAG, "Opponent " + opponent.getName() + " drawing card " + cardDrawn);
				}
				ArrayList<Card> opponentMove = opponent.makePlay(cardsPlayed, cardDrawn);
				if (! opponentMove.isEmpty()) {
					for (Card card : opponentMove) {
						cardsPlayed.add(card);
					}
				}
				for (int i = 0; i < cardsPlayed.size(); i++) {
					if (opponent.getHand().contains(cardsPlayed.get(i))) {
						opponent.mHand.remove(opponent.mHand.indexOf(cardsPlayed.get(i)));
					}
				}
			}
			mTurnNumber += 1;
			mTurn = true;
		}
		cardsPlayed.add(cardDrawn.get(0));
		Collections.sort(cardsPlayed);
		cardDrawn.clear();
		choosenCards.clear();
		invalidate();
	}
}
