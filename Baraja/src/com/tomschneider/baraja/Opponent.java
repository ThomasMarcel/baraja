package com.tomschneider.baraja;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import android.content.Context;

public class Opponent {
	private ArrayList<Card> hand;
	private String name;
	private Random r;
	private static final int NUMBER_OF_NAMES = 11;
	private Context mContext;
	
	public Opponent(Context context, ArrayList<Card> newHand) {
		mContext = context;
		hand = newHand;
		
		name = mContext.getString(mContext.getResources().getIdentifier("name" + r.nextInt(NUMBER_OF_NAMES - 1), "name", mContext.getPackageCodePath()));
	}
	
	public void setName(String newName) {
		name = newName;
	}
	
	public String getName() {
		return name;
	}
	
	public ArrayList<Card> getHand() {
		return hand;
	}
	
	public void setHand(ArrayList<Card> newHand) {
		hand = newHand;
	}
	
	public Card getCard(int id) {
		return hand.get(id);
	}
	
	public ArrayList<Card> makePlay(ArrayList<Card> cardsPlayed, ArrayList<Card> cardDrawn) {
		ArrayList<Card> choosenCards = new ArrayList<Card>();
		
		GameView.isValidMove(hand, cardDrawn, cardsPlayed, true, choosenCards);
		
		return choosenCards;
	}
}
