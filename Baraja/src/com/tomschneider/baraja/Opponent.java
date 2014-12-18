package com.tomschneider.baraja;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import android.content.Context;

public class Opponent {
	public ArrayList<Card> hand = new ArrayList<Card>();
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
		
		if (GameView.isValidMove(hand, cardDrawn, cardsPlayed, true, choosenCards)) {
			if (! choosenCards.isEmpty()) {
				for (Card card : choosenCards) {
					cardsPlayed.add(card);
					if (hand.contains(card)) {
						hand.remove(hand.indexOf(card));
					}
				}
			}
			return choosenCards;
		} else {
			return null;
		}
	}
}
