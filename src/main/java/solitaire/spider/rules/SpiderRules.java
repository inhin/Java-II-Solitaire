//*********************************************
// Spider Solitaire – Final Prototype
// File: SpiderRules.java
//
// Author: Indy Hinton
// Course: CPT-237-W38 Java Programming II
// Semester: Fall 2025
// Dates: 10/22/2025–12/2/2025
//
// Description:
// SpiderRules implements full rule validation for Spider Solitaire (1-Suit version)
// It enforces legal move rules, deal restrictions, and win conditions
// Scoring is calculated by using exact, rule-based logic
// based on player actions (moves, deals, completed runs, etc.)
//*********************************************

package solitaire.spider.rules;

import java.util.List;
import solitaire.core.GameRules;
import solitaire.spider.model.Card;
import solitaire.spider.model.Pile;


public class SpiderRules implements GameRules {

    /* Rules for 1-Suit Spider:
     - All cards must be face-up
     - All cards must be in the same suit
     - Cards must be in descending order by exactly one rank
     - Destination pile must be empty OR contain a card exactly one rank higher
     */
    @Override
    public boolean canMove(Pile from, int count, Pile to) {

        if (count <= 0) return false;
        var cards = from.getCards();
        if (cards.size() < count) return false;

        // Identify the moving run
        int start = cards.size() - count;

        // First card of run must be face-up
        if (!cards.get(start).isFaceUp()) return false;

        // Validate descending sequence
        for (int i = start + 1; i < cards.size(); i++) {
            Card below = cards.get(i - 1);
            Card above = cards.get(i);

            if (!above.isFaceUp() || !below.isFaceUp()) return false;
            if (below.getSuit() != above.getSuit()) return false;
            if (below.getRank() != above.getRank() + 1) return false;
        }

        // Destination rules
        if (to.isEmpty()) return true;

        Card movingHead = cards.get(start);
        Card destTop    = to.top();

        if (!destTop.isFaceUp()) return false;

        return destTop.getRank() == movingHead.getRank() + 1;
    }

    /* After a successful move has been performed:
       Extraction logic is handled by SpiderGame engine */
    @Override
    public void afterMove(Pile from, Pile to) {
        // Engine handles run extraction
    }

    /* A deal is only allowed if ALL columns contain at least one card */
    @Override
    public boolean canDeal(List<Pile> tableaux) {
        for (Pile p : tableaux)
            if (p.isEmpty()) return false;
        return true;
    }

    /* Win condition for 1-Suit Spider:
       When all 8 completed runs (13 cards each) are placed into foundation piles */
    @Override
    public boolean isWin(List<Pile> foundations) {
        int total = 0;
        for (Pile f : foundations)
            total += f.getCards().size();

        return total == 8 * 13;
    }
}