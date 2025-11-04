//*********************************************
// Spider Solitaire – Prototype 2
// File: SpiderRules.java
//
// Author: Indy Hinton
// Course: CPT-237-W38 Java Programming II
// Semester: Fall 2025
// Dates: 10/22/2025–11/2/2025
//
// Description:
// Spider rules and validation logic (for 1-suit game)
//
//*********************************************

package solitaire.spider.rules;

import java.util.List;
import solitaire.core.GameRules;
import solitaire.spider.model.Card;
import solitaire.spider.model.Pile;


public class SpiderRules implements GameRules {
    @Override
    public boolean canMove(Pile from, int count, Pile to) {
        if (count <= 0) return false;
        var cards = from.getCards();
        if (cards.size() < count) return false;

        // The run that's moving:
        // Must all be face-up,
        // Same suit,
        // Descending by 1
        int start = cards.size() - count;
        if (!cards.get(start).isFaceUp()) return false; // first of the run must be face-up
        for (int i = start + 1; i < cards.size(); i++) {
            var below = cards.get(i - 1);
            var above = cards.get(i);
            if (!above.isFaceUp() || !below.isFaceUp()) return false;
            if (below.getSuit() != above.getSuit()) return false;
            if (below.getRank() != above.getRank() + 1) return false;
        }

        // Destination rule:
        // Empty is OK;
        // Otherwise top must be exactly +1 over moving top
        if (to.isEmpty()) return true;
        Card movingHead = cards.get(start);
        Card destTop    = to.top();
        if (!destTop.isFaceUp()) return false;
        return destTop.getRank() == movingHead.getRank() + 1;
    }

    // Extraction handled by engine
    @Override
    public void afterMove(Pile from, Pile to) {
    }

    // Deal only allowed if no tableau is empty
    @Override
    public boolean canDeal(List<Pile> tableaux) {
        for (Pile p : tableaux) if (p.isEmpty()) return false;
        return true;
    }

    // 1-suit Spider win:
    // 8 completed stacks in foundations
    @Override
    public boolean isWin(List<Pile> foundations) {
        int total = 0;
        for (Pile f : foundations) total += f.getCards().size();
        return total == 8 * 13;
    }
}
