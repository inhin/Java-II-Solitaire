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
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;

import java.util.ArrayList;
import java.util.List;

import solitaire.klondike.engine.KlondikeGame;
import solitaire.core.ThemeManager;

public class KlondikeController {

    private final KlondikeGame game = new KlondikeGame();

    // views the factory will fill
    private final List<PileView> pileViews = new ArrayList<>();
    private final List<FoundationView> foundationViews = new ArrayList<>();

    private StackPane boardRoot;

    public Node createKlondikeBoard() {
        pileViews.clear();
        foundationViews.clear();

        // Start a new Klondike game
        game.newGame();

        // build the board
        Pane board = KlondikeBoardFactory.build(game, pileViews, foundationViews);

        boardRoot = new StackPane(board);
        ThemeManager.applyBackground(boardRoot);


        // Scrolling
        ScrollPane scrollPane = new ScrollPane(boardRoot);
        scrollPane.setFitToWidth(false);
        scrollPane.setFitToHeight(false);
        scrollPane.setPannable(true);

        refreshAll();

        return scrollPane;
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