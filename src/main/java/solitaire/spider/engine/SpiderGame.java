//*********************************************
// Spider Solitaire – Prototype 2
// File: SpiderGame.java
//
// Author: Indy Hinton
// Course: CPT-237-W38 Java Programming II
// Semester: Fall 2025
// Dates: 10/22/2025–11/2/2025
//
// Description:
// Game engine that communicates between UI and model classes
// Manages the Spider game state and mechanics
// Handles movement, scoring, and win detection
//
//*********************************************

package solitaire.spider.engine;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.List;
import java.util.Random;

import solitaire.core.PileType;
import solitaire.spider.model.Card;
import solitaire.spider.model.Move;
import solitaire.spider.model.Pile;
import solitaire.spider.model.Suit;
import solitaire.spider.rules.SpiderRules;


public class SpiderGame {
    public static final int TABLEAU_COUNT = 10;
    public static final int FOUNDATION_COUNT = 8;


    private final SpiderRules rules = new SpiderRules();


    // Model for UI binding
    public final List<Pile> tableaux = new ArrayList<>();
    public final List<Pile> foundations = new ArrayList<>();
    public final Pile stock = new Pile(PileType.STOCK);


    private final Deque<Move> undo = new ArrayDeque<>();


    // Count/score metrics
    // Will adjust more later as suits are added
    private int moveCount = 0;
    private int score = 500;

    // New Game
    // Base 1-suit Spider table
    public void newGame(long seed, boolean oneSuit) {
        tableaux.clear();
        foundations.clear();
        undo.clear();
        for (int i = 0; i < TABLEAU_COUNT; i++) tableaux.add(new Pile(PileType.TABLEAU));
        for (int i = 0; i < FOUNDATION_COUNT; i++) foundations.add(new Pile(PileType.FOUNDATION));


    // Build deck: 2 decks = 104 cards
        // For one-suit, use SPADES
        List<Card> deck = new ArrayList<>(104);
        for (int d = 0; d < 2; d++) {
            for (int r = 1; r <= 13; r++) {
                deck.add(new Card(oneSuit ? Suit.SPADES : Suit.SPADES, r, false));
                deck.add(new Card(oneSuit ? Suit.SPADES : Suit.HEARTS, r, false));
                deck.add(new Card(oneSuit ? Suit.SPADES : Suit.DIAMONDS, r, false));
                deck.add(new Card(oneSuit ? Suit.SPADES : Suit.CLUBS, r, false));
            }
        }
        Collections.shuffle(deck, new Random(seed));

        // Initial deal layout:
        // Piles 0-3 get 6
        // 4-9 get 5
        // Top cards face up
        for (int i = 0; i < TABLEAU_COUNT; i++) {
            int count = (i < 4) ? 6 : 5;
            for (int k = 0; k < count; k++) tableaux.get(i).push(deck.remove(deck.size() - 1));
            tableaux.get(i).top().setFaceUp(true);
        }


        // Remaining cards go to stock
        for (Card c : deck) stock.push(c);
        moveCount = 0; score = 500;
    }


    public void newGame() { newGame(System.nanoTime(), true); }

    // Enforce rule:
    // Moving run is face-up
    public boolean moveRun(int fromIndex, int count, int toIndex) {
        Pile from = tableaux.get(fromIndex);
        Pile to   = tableaux.get(toIndex);
        if (!rules.canMove(from, count, to)) return false;

        int beforeSize = from.getCards().size();
        boolean willReveal = false;
        int revealIdx = beforeSize - count - 1;
        if (revealIdx >= 0) {
            Card newTop = from.getCards().get(revealIdx);
            willReveal = !newTop.isFaceUp();
        }

        // Move the cards
        List<Card> run = from.takeTop(count);
        to.addRun(run);

        // Auto-flip per Spider game rules
        from.flipTopUpIfNeeded();

        // Record move
        Move rec = new Move(Move.Type.MOVE_RUN, fromIndex, toIndex, count, new ArrayList<>(run));
        rec.setFlippedAfterMove(willReveal);
        undo.push(rec);

        moveCount++; score -= 1;
        extractCompletedRuns();
        return true;
    }


    // Enforce rule:
    // Cannot deal if any tableau is empty
    public boolean dealRow() {
        if (!rules.canDeal(tableaux)) return false;
        List<Card> dealt = new ArrayList<>(TABLEAU_COUNT);
        for (int i = 0; i < TABLEAU_COUNT; i++) {
            Card c = stock.pop();
            if (c == null) return false; // no more stock
            c.setFaceUp(true);
            tableaux.get(i).push(c);
            dealt.add(c);
        }
        undo.push(new Move(Move.Type.DEAL_ROW, -1, -1, TABLEAU_COUNT, dealt));
        moveCount++; score -= 5;
        extractCompletedRuns();
        return true;
    }

    // Look for a 13 card completed run
    // Same suit, face up, descending by 1
    // Move it to the first foundation with space
    public void extractCompletedRuns() {
        for (int i = 0; i < TABLEAU_COUNT; i++) {
            Pile t = tableaux.get(i);
            int size = t.getCards().size();
            if (size < 13) continue;

            List<Card> cards = t.getCards();
            int topIdx = size - 1;
            int start = size - 13;

            boolean ok = true;

            // Check all 13
            for (int k = start; k < size; k++) {
                Card c = cards.get(k);
                if (!c.isFaceUp()) {
                    ok = false;
                    break;
                }
                if (k > start) {
                    Card prev = cards.get(k - 1);
                    // Check same suit
                    if (c.getSuit() != prev.getSuit()) {
                        ok = false;
                        break;
                    }
                    // Check descending by 1
                    if (c.getRank() != prev.getRank() - 1) {
                        ok = false;
                        break;
                    }
                }
            }

            if (!ok) continue;

            // Move 13 cards
            List<Card> extracted = t.takeTop(13);

            // Put into first empty foundation
            for (Pile f : foundations) {
                if (f.getCards().isEmpty()) {
                    f.addRun(extracted);
                    break;
                }
            }

            t.flipTopUpIfNeeded();

            undo.push(new solitaire.spider.model.Move(
                    solitaire.spider.model.Move.Type.EXTRACT_RUN,
                    i, -1, 13,
                    new java.util.ArrayList<>(extracted)
            ));
        }
    }

    // Enforce rule:
    // Cards stacked properly in foundations
    public boolean isWin() { return rules.isWin(foundations); }

    // Undo conditions
    public boolean undo() {
        if (undo.isEmpty()) return false;
        Move m = undo.pop();
        switch (m.getType()) {
            case MOVE_RUN -> {
                List<Card> run = m.getPayload();
                Pile to   = tableaux.get(m.getToIndex());
                Pile from = tableaux.get(m.getFromIndex());

                // Remove the run from destination (preserve order)
                for (int i = 0; i < run.size(); i++) to.pop();

                // Add it back to source in original order
                for (Card c : run) from.push(c);

                // The moved card should be face-up as before
                if (!from.isEmpty()) from.top().setFaceUp(true);

                // If the forward move auto-revealed the underlying card:
                // Flip that card back down
                if (m.isFlippedAfterMove()) {
                    int idx = from.getCards().size() - m.getCount() - 1;
                    if (idx >= 0) {
                        from.getCards().get(idx).setFaceUp(false);
                    }
                }
            }

            // Remove last card from each tableau in reverse order back to stock
            case DEAL_ROW -> {
                for (int i = TABLEAU_COUNT - 1; i >= 0; i--) {
                    Card c = tableaux.get(i).pop();
                    if (c != null) { c.setFaceUp(false); stock.push(c); }
                }
            }

            // Take from last non-empty foundation back to the given tableau
            case EXTRACT_RUN -> {
                List<Card> run = m.getPayload();
                Pile fromF = null;
                for (int i = foundations.size() - 1; i >= 0; i--) {
                    if (!foundations.get(i).isEmpty()) { fromF = foundations.get(i); break; }
                }
                if (fromF != null) {
                    for (int i = 0; i < 13; i++) fromF.pop();
                }
                Pile toT = tableaux.get(m.getFromIndex());
                toT.addRun(run);
                toT.top().setFaceUp(true);
            }
        }
        moveCount = Math.max(0, moveCount - 1); score += 1;
        return true;
    }

    // Metrics
    public int getMoveCount() { return moveCount; }
    public int getScore() { return score; }
}
