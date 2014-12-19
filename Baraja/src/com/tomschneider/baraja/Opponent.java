package com.tomschneider.baraja;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import android.content.Context;
import android.util.Log;

public class Opponent {
	private static final String TAG = "Baraja";
	
	public ArrayList<Card> mHand = new ArrayList<Card>();
	private String name;
	private Random r;
	private static final int NUMBER_OF_NAMES = 11;
	private Context mContext;
	
	public Opponent(Context context) {
		mContext = context;
		
		//name = mContext.getString(mContext.getResources().getIdentifier("name" + r.nextInt(NUMBER_OF_NAMES - 1), "name", mContext.getPackageCodePath()));
		name = "Thomas";
	}
	
	public void setName(String newName) {
		name = newName;
	}
	
	public String getName() {
		return name;
	}
	
	public ArrayList<Card> getHand() {
		return mHand;
	}
	
	public void setHand(ArrayList<Card> newHand) {
		mHand = newHand;
	}
	
	public Card getCard(int id) {
		return mHand.get(id);
	}
	
	public ArrayList<Card> makePlay(ArrayList<Card> cardsPlayed, ArrayList<Card> cardDrawn) {
		ArrayList<Card> choosenCards = GameView.isValidMove(mHand, cardDrawn, cardsPlayed, true);
		
		return choosenCards;
	}
}
