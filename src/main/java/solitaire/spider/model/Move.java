//*********************************************
// Spider Solitaire – Prototype 2
// File: Move.java
//
// Author: Indy Hinton
// Course: CPT-237-W38 Java Programming II
// Semester: Fall 2025
// Dates: 10/22/2025–11/2/2025
//
// Description:
// Data regarding player movement
// Records movement for 'undo' and analytics
//
//*********************************************

package solitaire.spider.model;

import java.util.List;

public class Move {
    public enum Type { MOVE_RUN, DEAL_ROW, EXTRACT_RUN }


    private final Type type;
    private final int fromIndex; // Tableau index
    private final int toIndex; // Tableau/foundation index
    private final int count; // Cards moved (for MOVE_RUN)
    private final List<Card> payload; // List of cards moved/ dealt/ extracted

    private boolean flippedAfterMove;  // for MOVE_RUN
    private int foundationIndex = -1;  // for EXTRACT_RUN
    private boolean flippedAfterExtract;  // for EXTRACT_RUN

    public Move(Type type, int fromIndex, int toIndex, int count, List<Card> payload) {
        this.type = type; this.fromIndex = fromIndex; this.toIndex = toIndex; this.count = count; this.payload = payload;
    }

    public boolean isFlippedAfterMove() { return flippedAfterMove; }
    public void setFlippedAfterMove(boolean v) { flippedAfterMove = v; }

    public int getFoundationIndex() { return foundationIndex; }
    public void setFoundationIndex(int idx) { foundationIndex = idx; }

    public boolean isFlippedAfterExtract() { return flippedAfterExtract; }
    public void setFlippedAfterExtract(boolean v) { flippedAfterExtract = v; }


    public Type getType() { return type; }
    public int getFromIndex() { return fromIndex; }
    public int getToIndex() { return toIndex; }
    public int getCount() { return count; }
    public List<Card> getPayload() { return payload; }
}
