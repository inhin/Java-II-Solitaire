package solitaire.klondike.rules;

//*********************************************
// Klondike Solitaire – Prototype 2
// File: KlondikeRules.java
//
// Author: Jenascia Drew
// Course: CPT-237-W38 Java Programming II
// Semester: Fall 2025
// Dates: 11/10/2025
//
// Description:
// Klondike rules and validation logic:
// - Validates tableau: descending, alternating colors, or Kings on empty piles
// - Validates foundation piles: same suit, ascending from Ace
//*********************************************

import solitaire.klondike.model.Card;
import solitaire.klondike.model.Pile;

public class KlondikeRules {

    // Tableau Rule
    // Empty: Only a King (rank 13)
    // Otherwise: target card must be exactly 1 higher and opposite color
    public static boolean canPlaceOnTableau(Card moving, Pile tableauPile) {
        if (moving == null) return false;

        if (tableauPile.getCards().isEmpty()) {
            return moving.getRank() == 13;
        }

        Card top = tableauPile.getTopCard();
        return isOneLower(moving, top) && isAlternatingColor(moving, top);
    }

    // Foundation Rule:
    // Empty: Only Ace (rank 1)
    // Otherwise: same suit, rank exactly 1 higher
    public static boolean canPlaceOnFoundation(Card moving, Pile foundationPile) {
        if (moving == null) return false;

        if (foundationPile.getCards().isEmpty()) {
            return moving.getRank() == 1; // Ace
        }

        Card top = foundationPile.getTopCard();
        return moving.getSuit() == top.getSuit()
                && moving.getRank() == top.getRank() + 1;
    }

    // ---------- Helper methods ----------

    // Moving must be exactly one lower than target
    private static boolean isOneLower(Card moving, Card target) {
        return moving.getRank() + 1 == target.getRank();
    }

    // Red on black or black on red
    private static boolean isAlternatingColor(Card a, Card b) {
        return isRed(a) != isRed(b);
    }

    private static boolean isRed(Card c) {
        return switch (c.getSuit()) {
            case HEARTS, DIAMONDS -> true;
            default -> false;
        };
    }
}
