package solitaire.klondike.engine;

//*********************************************
// Klondike Solitaire – Prototype 2
// File: KlondikeGame.java
//
// Author: Jenascia Drew
// Course: CPT-237-W38 Java Programming II
// Semester: Fall 2025
// Dates: 10/22/2025–11/11/2025
//
// Description:
// Klondike game engine that builds:
// - a standard deck
// - deals 7 tableau piles
// - creates 4 foundations
// - keeps track of stock, moves, and win detection
//*********************************************

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.List;
import java.util.Random;

import solitaire.core.PileType;
import solitaire.klondike.model.Card;
import solitaire.klondike.model.Move;
import solitaire.klondike.model.Pile;
import solitaire.klondike.model.Suit;

public class KlondikeGame {

    public static final int TABLEAU_COUNT = 7;
    public static final int FOUNDATION_COUNT = 4;

    public final List<Pile> tableaux = new ArrayList<>();
    public final List<Pile> foundations = new ArrayList<>();
    public final Pile stock = new Pile(PileType.STOCK);

    private final Deque<Move> undo = new ArrayDeque<>();

    private int moveCount = 0;
    private int score = 500;

    // ----- public API -----

    // New game with shuffle
    public void newGame() {
        newGame(System.currentTimeMillis());
    }

    // New game
    public void newGame(long seed) {
        tableaux.clear();
        foundations.clear();
        stock.getCards().clear();
        undo.clear();
        moveCount = 0;
        score = 500;

        // Create piles
        for (int i = 0; i < TABLEAU_COUNT; i++) {
            tableaux.add(new Pile(PileType.TABLEAU));
        }
        for (int i = 0; i < FOUNDATION_COUNT; i++) {
            foundations.add(new Pile(PileType.FOUNDATION));
        }

        // Build and shuffle deck
        List<Card> deck = buildDeck();
        Collections.shuffle(deck, new Random(seed));

        // Deal to tableau:
        // 1 - 7
        // last card face up
        int deckIndex = 0;
        for (int col = 0; col < TABLEAU_COUNT; col++) {
            Pile pile = tableaux.get(col);
            for (int r = 0; r <= col; r++) {
                Card c = deck.get(deckIndex++);
                c.setFaceUp(r == col);
                pile.push(c);
            }
        }

        // Remaining cards go to stock (face down)
        while (deckIndex < deck.size()) {
            Card c = deck.get(deckIndex++);
            c.setFaceUp(false);
            stock.push(c);
        }
    }

    // ----- helpers -----

    private List<Card> buildDeck() {
        List<Card> deck = new ArrayList<>(52);
        for (Suit s : Suit.values()) {
            for (int rank = 1; rank <= 13; rank++) {
                deck.add(new Card(s, rank, false));
            }
        }
        return deck;
    }

    // win check: all 4 foundations have 13 cards
    public boolean isWin() {
        for (Pile f : foundations) {
            if (f.getCards().size() < 13) {
                return false;
            }
        }
        return true;
    }

    // ----- metrics -----
    public int getMoveCount() {
        return moveCount;
    }

    public int getScore() {
        return score;
    }

    public void registerMove(Move m) {
        undo.push(m);
        moveCount++;
    }

    public boolean undo() {
        if (undo.isEmpty()) return false;
        return true;
    }
}