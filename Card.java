package solitaire.klondike.model;

//*********************************************
// Klondike Solitaire – Prototype 2
// File: Card.java
//
// Author: Jenascia Drew
// Course: CPT-237-W38 Java Programming II
// Semester: Fall 2025
// Dates: 10/22/2025–11/11/2025
//
// Description:
// A single playing card used in Klondike. Stores suit, rank,
// and face-up state, and includes helper methods
// for comparing card relationships
//*********************************************


public class Card {
    private final Suit suit;
    private final int rank;   // 1 = Ace --- 13 = King
    private boolean faceUp;

    public Card(Suit suit, int rank, boolean faceUp) {
        if (rank < 1 || rank > 13) {
            throw new IllegalArgumentException("Rank must be between 1 and 13");
        }
        this.suit = suit;
        this.rank = rank;
        this.faceUp = faceUp;
    }

    public Suit getSuit() { return suit; }
    public int getRank() { return rank; }

    public boolean isFaceUp() { return faceUp; }
    public void setFaceUp(boolean faceUp) { this.faceUp = faceUp; }

    // Checks if card is exactly 1 rank lower and same suit
    public boolean isOneLowerSameSuit(Card other) {
        return other != null && this.suit == other.suit && this.rank + 1 == other.rank;
    }

    @Override
    public String toString() {
        String[] names = {
                "?", "A", "2", "3", "4", "5", "6",
                "7", "8", "9", "10", "J", "Q", "K"
        };
        return names[rank] + " of " + suit;
    }
}

