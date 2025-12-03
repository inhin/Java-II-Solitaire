package solitaire.klondike.ui;

//*********************************************
// Klondike Solitaire – Prototype 2
// File: KlondikeController.java
//
// Author: Jenascia Drew
// Course: CPT-237-W38 Java Programming II
// Semester: Fall 2025
// Dates: 10/22/2025–11/11/2025
//
// Description:
// Bridge between the Klondike game engine and the UI.
// Builds the board, starts a new game, and refreshes
// pile/foundation views
//*********************************************

import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;

import java.util.ArrayList;
import java.util.List;

import solitaire.klondike.engine.KlondikeGame;

public class KlondikeController {

    private final KlondikeGame game = new KlondikeGame();

    // views the factory will fill
    private final List<PileView> pileViews = new ArrayList<>();
    private final List<FoundationView> foundationViews = new ArrayList<>();

    private StackPane boardRoot;

    public Node createKlondikeBoard() {
        try {
            pileViews.clear();
            foundationViews.clear();

            // Start a new Klondike game
            game.newGame();

            // Build the board
            Pane board = KlondikeBoardFactory.build(game, pileViews, foundationViews);

            boardRoot = new StackPane(board);
            boardRoot.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);

            // Scrolling
            ScrollPane scrollPane = new ScrollPane(boardRoot);
            scrollPane.setFitToWidth(true);
            scrollPane.setFitToHeight(true);
            scrollPane.setPannable(true);

            refreshAll();

            return scrollPane;

        } catch (Exception ex) {
            System.out.println("KLONDIKE CRASHED:");
            ex.printStackTrace();
            return new Label("KLONDIKE FAILED TO LOAD");
        }
    }


    // Start a new game
    public void onNewGame() {
        game.newGame();
        refreshAll();
    }

    // ====== STUBS for launcher ======

    public void onDeal() {
        // Flip 3 from stock to waste
        System.out.println("[Klondike] deal not implemented.");
    }

    public void onUndo() {
        // Undo
        System.out.println("[Klondike] undo not implemented.");
    }

    private void refreshAll() {
        for (PileView pv : pileViews) {
            pv.refresh();
        }
        for (FoundationView fv : foundationViews) {
            fv.refresh();
        }
    }
}