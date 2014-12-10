package com.tomschneider.baraja;

import android.graphics.Bitmap;

public class Card implements Comparable<Card> {
	/*
	 * 101 - 110: Oros
	 * 201 - 210: Bastos
	 * 301 - 310: Espadas
	 * 401 - 410: Copas
	 */
	private int id;
	private Bitmap bmp;
	
	private int suit;
	private int rank;
	
	public Card(int newId) {
		id = newId;
		suit = id - Integer.parseInt(String.valueOf(id).substring(1));
		rank = id - suit;
	}
	
	public void setBitmap(Bitmap tmpBmp) {
		bmp = tmpBmp;
	}
	
	public Bitmap getBitmap() {
		return bmp;
	}
	
	public int getId() {
		return id;
	}
	
	public int getSuit() {
		return suit;
	}
	
	public int getRank() {
		return rank;
	}
	
	@Override
	public String toString() {
		return "Card " + getId();
	}

	@Override
	public int compareTo(Card another) {
		// TODO Auto-generated method stub
		if (getId() < another.getId()) {
			return -1;
		} else if (getId() == another.getId()) {
			return 0;
		} else {
			return 1;
		}
	}
}
