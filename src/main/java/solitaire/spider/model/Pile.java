//*********************************************
// Spider Solitaire – Prototype 2
// File: Pile.java
//
// Author: Indy Hinton
// Course: CPT-237-W38 Java Programming II
// Semester: Fall 2025
// Dates: 10/22/2025–11/2/2025
//
// Description:
// Spider card column with storage
// Provides operations for card movements
// As well as methods for flipping and game integrity
//
//*********************************************

package solitaire.spider.model;

import java.util.ArrayList;
import java.util.List;
import solitaire.core.PileType;


public class Pile {
    private final PileType type;
    private final List<Card> cards = new ArrayList<>();


    public Pile(PileType type) { this.type = type; }


    public List<Card> getCards() { return cards; }


    public boolean isEmpty() { return cards.isEmpty(); }


    public Card top() { return isEmpty() ? null : cards.get(cards.size() - 1); }


    public void push(Card c) { cards.add(c); }


    public Card pop() { return isEmpty() ? null : cards.remove(cards.size() - 1); }


    // Remove the top N cards
    // Return in correct order
    public List<Card> takeTop(int count) {
        List<Card> out = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            Card c = pop();
            out.add(0, c);
        }
        return out;
    }


    // Add a run in order
    // Add to the foundations
    public void addRun(List<Card> run) {
        if (type == PileType.FOUNDATION) {
            cards.addAll(run);
            return;
        }

        cards.addAll(run);
    }


    // Flip top card face up if needed
    public void flipTopUpIfNeeded() {
        if (type == PileType.TABLEAU && !cards.isEmpty()) {
            Card t = top();
            if (!t.isFaceUp()) t.setFaceUp(true);
        }
    }
}
