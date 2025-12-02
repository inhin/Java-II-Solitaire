//*********************************************
// Spider Solitaire – Prototype 2
// File: SpiderController.java
//
// Author: Indy Hinton
// Course: CPT-237-W38 Java Programming II
// Semester: Fall 2025
// Dates: 10/22/2025–11/9/2025
//
// Description:
// Bridge between the game engine and user interface
// Handles player actions and manages visual feedback
//
//*********************************************

package solitaire.spider.ui;

import javafx.animation.*;
import javafx.geometry.Point2D;
import javafx.scene.control.Alert;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;
import java.util.ArrayList;
import java.util.List;
import javafx.scene.control.Label;


import solitaire.spider.engine.SpiderGame;
import solitaire.spider.model.Card;
import solitaire.core.GameEvents;
import solitaire.core.ThemeManager;

public class SpiderController {
    private SpiderGame game = new SpiderGame();
    private final List<PileView> pileViews = new ArrayList<>();
    private final List<FoundationView> foundationViews = new ArrayList<>();
    private final List<StockView> stockViews = new ArrayList<>();
    private final Pane overlay = new Pane();
    private StackPane boardRoot;
    private boolean animating = false;
    private final GameEvents events;
    private StackPane selectedCardNode = null;

    public javafx.scene.Node createSpiderBoard() {
        pileViews.clear();
        foundationViews.clear();
        stockViews.clear();
        overlay.setMouseTransparent(true);

        game.newGame();

        // Build the base board
        // Stack overlay on top for animations
        Pane board = SpiderBoardFactory.build(game, pileViews, foundationViews, stockViews);
        boardRoot = new StackPane(board, overlay);
        ThemeManager.applyBackground(boardRoot);

        // Add scroll bars
        // Temporary solution until flexible is solved
        // Updated 11-9-25 to green "felt" background
        ScrollPane sp = new ScrollPane(boardRoot);
        sp.setFitToWidth(true);
        sp.setFitToHeight(true);
        sp.setPannable(true);
        sp.setStyle("-fx-background: #325b3b; -fx-background-color: #325b3b;");

        refreshAll();
        wireClicks();

        return sp;
    }

    public void onNewGame() {
        game = new SpiderGame();
        refreshAll();
        events.onReset();
    }

    // Updated 11/19/25 to connect move and score to HUD
    public boolean onDeal() {
        // Check for empty tableau column
        boolean hasEmpty = game.tableaux.stream().anyMatch(p -> p.isEmpty());
        if (hasEmpty) {
            new Alert(Alert.AlertType.WARNING,
                    "You cannot deal when a tableau column is empty!")
                    .showAndWait();
            return false;
        }

        // Attempt to deal
        boolean ok = game.dealRow();
        if (!ok) {
            new Alert(Alert.AlertType.WARNING,
                    "No more deals available!")
                    .showAndWait();
            return false;
        }

        game.extractCompletedRuns();
        refreshAll();

        if (events != null) {
            events.onMove();
            events.onScore(-1);
        }

        // Detect win after deal
        if (game.isWin()) {
            if (events != null) events.onWin();
        }

        return true;
    }

    public void onUndo() {
        if (game.undo()) {
            refreshAll();
        }
    }

    public int getMoveCount() { return game.getMoveCount(); }
    public int getScore()     { return game.getScore(); }

    private void refreshAll() {
        pileViews.forEach(PileView::refresh);
        foundationViews.forEach(FoundationView::refresh);
        stockViews.forEach(StockView::refresh);
    }


    // Click-select move wiring
    private Integer selectedFrom = null;

    private void wireClicks() {
        for (int i = 0; i < pileViews.size(); i++) {
            final int idx = i;
            pileViews.get(i).setOnMouseClicked(e -> handleClick(idx));
        }
    }

    private void handleClick(int idx) {
        if (animating) return; // prevent actions during animation

        // Select from pile
        if (selectedFrom == null) {
            selectedFrom = idx;

            // Highlight only the top card in this pile
            selectedCardNode = pileViews.get(idx).getTopCardNode();
            if (selectedCardNode != null) {
                String base = selectedCardNode.getStyle();
                selectedCardNode.setStyle(base + """
                -fx-border-color: gold;
                -fx-border-width: 2;
            """);
            }
            return;
        }

        // Attempt move TO this pile
        tryMoveLongestRun(selectedFrom, idx);

        // Clear highlight
        if (selectedFrom >= 0 && selectedFrom < pileViews.size()) {
            pileViews.get(selectedFrom).refresh();
        }
        selectedFrom = null;
        selectedCardNode = null;

        refreshAll();
        if (game.isWin()) {
            if (events != null) events.onWin();
        }
    }

    private void tryMoveLongestRun(int fromIdx, int toIdx) {
        if (fromIdx == toIdx) return;

        var from = game.tableaux.get(fromIdx);
        int size = from.getCards().size();
        if (size == 0) return;

        // Longest face-up, same-suit, descending tail
        int max = 1;
        for (int k = size - 1; k > 0; k--) {
            Card above = from.getCards().get(k);
            Card below = from.getCards().get(k - 1);
            if (!below.isFaceUp() || !above.isFaceUp()
                    || below.getSuit() != above.getSuit()
                    || below.getRank() != above.getRank() + 1) break;
            max++;
        }

        for (int c = max; c >= 1; c--) {
            final int moveCount = c;
            List<Card> cards =
                    new ArrayList<>(from.getCards().subList(size - c, size));

            if (game.moveRun(fromIdx, moveCount, toIdx)) {
                game.undo(); // Test

                // Updated 11/19/25 to record moves and score
                animateMove(fromIdx, toIdx, cards, () -> {
                    game.moveRun(fromIdx, moveCount, toIdx);
                    game.extractCompletedRuns();
                    if (events != null) {
                        events.onMove();
                        events.onScore(5);
                    }

                    refreshAll();
                    animating = false;
                    if (game.isWin()) {
                        if (events != null) events.onWin();
                    }
                });
            }
        }
    }

    // Animation helpers

    private javafx.scene.layout.VBox buildGhost(List<Card> cards) {
        var box = new javafx.scene.layout.VBox(0);
        for (int i = 0; i < cards.size(); i++) {
            Card c = cards.get(i);
            String text;
            if (!c.isFaceUp()) {
                text = "🂠";
            } else {
                String r = switch (c.getRank()) {
                    case 1 -> "A"; case 11 -> "J"; case 12 -> "Q"; case 13 -> "K";
                    default -> String.valueOf(c.getRank());
                };
                String s = switch (c.getSuit()) {
                    case SPADES -> "♠"; case HEARTS -> "♥"; case DIAMONDS -> "♦"; case CLUBS -> "♣";
                };
                text = r + s;
            }
            var node = Basics.card(text);

            // Theme-based text color
            boolean isRed = (c.getSuit().name().equals("HEARTS") ||
                    c.getSuit().name().equals("DIAMONDS"));
            ThemeManager.styleCardText((Label) node.getChildren().get(0), isRed);

            box.getChildren().add(node);
        }
        return box;
    }

    private void animateMove(int fromIdx, int toIdx, List<Card> cards, Runnable onFinished) {
        animating = true;

        var ghost = buildGhost(cards);
        overlay.getChildren().add(ghost);

        // Start/end coordinates in overlay space
        Point2D startInScene = pileViews.get(fromIdx).localToScene(0, 0);
        Point2D endInScene   = pileViews.get(toIdx).localToScene(0, 0);

        Point2D start = overlay.sceneToLocal(startInScene);
        Point2D end   = overlay.sceneToLocal(endInScene);

        ghost.setLayoutX(start.getX());
        ghost.setLayoutY(start.getY());

        TranslateTransition tt = new TranslateTransition(Duration.millis(220), ghost);
        tt.setFromX(0); tt.setFromY(0);
        tt.setToX(end.getX() - start.getX());
        tt.setToY(end.getY() - start.getY());
        tt.setInterpolator(Interpolator.EASE_BOTH);

        FadeTransition ft = new FadeTransition(Duration.millis(220), ghost);
        ft.setFromValue(0.85);
        ft.setToValue(0.95);

        ParallelTransition pt = new ParallelTransition(tt, ft);
        pt.setOnFinished(e -> {
            overlay.getChildren().remove(ghost);
            onFinished.run();
        });
        pt.play();
    }

    public SpiderController(GameEvents events) {
        this.events = events;
        this.game = new SpiderGame();
    }
}