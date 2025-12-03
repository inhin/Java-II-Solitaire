package solitaire.klondike.model;

//*********************************************
// Klondike Solitaire – Prototype 2
// File: Pile.java
//
// Author: Jenascia Drew
// Course: CPT-237-W38 Java Programming II
// Semester: Fall 2025
// Dates: 10/22/2025–11/11/2025
//
// Description:
// A pile of cards (tableau, foundation, or stock)
// used in Klondike Solitaire. Handles adding,
// removing, and flipping cards
//*********************************************


import java.util.ArrayList;
import java.util.List;
import solitaire.core.PileType;

public class Pile {

    private final PileType type;
    private final List<Card> cards = new ArrayList<>();

    public Pile(PileType type) {
        this.type = type;
    }

    public PileType getType() {
        return type;
    }

    public List<Card> getCards() {
        return cards;
    }

    public boolean isEmpty() {
        return cards.isEmpty();
    }

    public Card getTopCard() {
        return isEmpty() ? null : cards.get(cards.size() - 1);
    }

    public void push(Card c) {
        cards.add(c);
    }

    public Card pop() {
        return isEmpty() ? null : cards.remove(cards.size() - 1);
    }

    // Return view of top N cards without removal
    public List<Card> topRun(int count) {
        return cards.subList(cards.size() - count, cards.size());
    }

    // Remove and return top N cards
    public List<Card> takeTop(int count) {
        int start = cards.size() - count;
        List<Card> slice = new ArrayList<>(cards.subList(start, cards.size()));
        cards.subList(start, cards.size()).clear();
        return slice;
    }

    // Adds group of cards in order to the pile
    public void addRun(List<Card> run) {
        cards.addAll(run);
    }

    // Flips top card face-up
    public void flipTopUpIfNeeded() {
        if (!isEmpty()) {
            Card top = getTopCard();
            if (!top.isFaceUp()) {
                top.setFaceUp(true);
            }
        }
    }
}