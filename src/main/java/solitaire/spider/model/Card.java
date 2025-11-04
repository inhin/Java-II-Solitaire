//*********************************************
// Spider Solitaire – Prototype 2
// File: Card.java
//
// Author: Indy Hinton
// Course: CPT-237-W38 Java Programming II
// Semester: Fall 2025
// Dates: 10/22/2025–11/2/2025
//
// Description:
// Spider variant specific cards
// Stores rank, suit, face-up state
//
//*********************************************

package solitaire.spider.model;

public class Card {
    private final Suit suit;
    private final int rank; // King starts rank 13, lowers with following ranks
    private boolean faceUp;


    public Card(Suit suit, int rank, boolean faceUp) {
        if (rank < 1 || rank > 13) throw new IllegalArgumentException("rank 1..13");
        this.suit = suit;
        this.rank = rank;
        this.faceUp = faceUp;
    }


    public Suit getSuit() { return suit; }
    public int getRank() { return rank; }


    public boolean isFaceUp() { return faceUp; }
    public void setFaceUp(boolean faceUp) { this.faceUp = faceUp; }


    public boolean isOneLowerSameSuit(Card other) {
        return other != null && this.suit == other.suit && this.rank + 1 == other.rank;
    }


    @Override public String toString() {
        String[] names = {"?","A","2","3","4","5","6","7","8","9","10","J","Q","K"};
        return names[rank] + " of " + suit;
    }
}
