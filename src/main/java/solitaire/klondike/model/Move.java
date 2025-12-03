package solitaire.klondike.model;

//*********************************************
// Klondike Solitaire – Prototype 2
// File: Move.java
//
// Author: Jenascia Drew
// Course: CPT-237-W38 Java Programming II
// Semester: Fall 2025
// Dates: 10/22/2025–11/11/2025
//
// Description:
// Records a single move made by the player in
// Klondike. Used for basic undo logic
// and analytics. Each move stores
// source and destination pile indexes, the cards
// moved, and any flip events
//*********************************************

import java.util.List;

public class Move {

    public enum Type {
        MOVE_CARDS,   // Move cards between tableau piles or to foundations
        FLIP_CARD     // Flip a card face up
    }

    private final Type type;
    private final int fromIndex;         // source pile index (tableau)
    private final int toIndex;           // destination pile index (tableau/foundation)
    private final int count;             // number of cards moved
    private final List<Card> payload;    // cards involved in  move

    private boolean flippedAfterMove;

    public Move(Type type, int fromIndex, int toIndex, int count, List<Card> payload) {
        this.type = type;
        this.fromIndex = fromIndex;
        this.toIndex = toIndex;
        this.count = count;
        this.payload = payload;
    }

    // ---- Accessors ----
    public Type getType() { return type; }
    public int getFromIndex() { return fromIndex; }
    public int getToIndex() { return toIndex; }
    public int getCount() { return count; }
    public List<Card> getPayload() { return payload; }

    public boolean isFlippedAfterMove() { return flippedAfterMove; }
    public void setFlippedAfterMove(boolean flippedAfterMove) { this.flippedAfterMove = flippedAfterMove; }
}