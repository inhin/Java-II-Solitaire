//*********************************************
// Spider Solitaire – Prototype 2
// File: GameRules.java
//
// Author: Indy Hinton
// Course: CPT-237-W38 Java Programming II
// Semester: Fall 2025
// Dates: 10/22/2025–11/2/2025
//
// Description:
// Shared interface between game variants
//
//*********************************************

package solitaire.core;

import java.util.List;
import solitaire.spider.model.Pile;

public interface GameRules {
    /**
     * @param from source pile (TABLEAU)
     * @param count number of cards to move from the top of the source run
     * @param to destination pile (TABLEAU)
     * @return true if the move is legal per rules
     */
    boolean canMove(Pile from, int count, Pile to);


    /** Invoked after a successful move */
    void afterMove(Pile from, Pile to);


    /** @return true if a deal from stock is allowed */
    boolean canDeal(List<Pile> tableaux);


    /** @return true if win condition met */
    boolean isWin(List<Pile> foundations);
}
