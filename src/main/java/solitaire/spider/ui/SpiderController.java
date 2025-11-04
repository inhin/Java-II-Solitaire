//*********************************************
// Spider Solitaire – Prototype 2
// File: SpiderController.java
//
// Author: Indy Hinton
// Course: CPT-237-W38 Java Programming II
// Semester: Fall 2025
// Dates: 10/22/2025–11/2/2025
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


import solitaire.spider.engine.SpiderGame;
import solitaire.spider.model.Card;

public class SpiderController {
    private final SpiderGame game = new SpiderGame();
    private final List<PileView> pileViews = new ArrayList<>();
    private final List<FoundationView> foundationViews = new ArrayList<>();
    private StackPane boardRoot;          // layered root (board + overlay)
    private final Pane overlay = new Pane(); // animation layer
    private boolean animating = false;

    public javafx.scene.Node createSpiderBoard() {
        pileViews.clear();
        foundationViews.clear();
        overlay.setMouseTransparent(true);

        game.newGame(); // sets up deck + tableau

        // Build the base board (foundation + tableau)
        // Stack overlay on top for animations
        Pane board = SpiderBoardFactory.build(game, pileViews, foundationViews);
        boardRoot = new StackPane(board, overlay);

        // Add scroll bars (temporary fix)
        ScrollPane scrollPane = new ScrollPane(boardRoot);
        scrollPane.setFitToWidth(false);     // Horizontal scroll
        scrollPane.setFitToHeight(false);    // Vertical scroll
        scrollPane.setPannable(true);        // Drag to scroll
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setStyle("""
        -fx-background: linear-gradient(to bottom, #c9d6e3, #edf2f7);
        -fx-border-color: #b0b0b0;
    """);

        refreshAll();
        wireClicks();

        return scrollPane;
    }

    public void onNewGame() {
        game.newGame();
        refreshAll();
    }

    public boolean onDeal() {
        boolean ok = game.dealRow();
        if (ok) {
            for (var pv : pileViews) {
                FadeTransition ft = new FadeTransition(Duration.millis(160), pv);
                ft.setFromValue(0.85);
                ft.setToValue(1.0);
                ft.play();
            }
        }
        refreshAll();
        if (game.isWin()) new Alert(Alert.AlertType.INFORMATION, "You win!").showAndWait();
        return ok;
    }

    public void onUndo() {
        if (game.undo()) {
            refreshAll();
        }
    }

    public int getMoveCount() { return game.getMoveCount(); }
    public int getScore()     { return game.getScore(); }
    public boolean isWin()    { return game.isWin(); }

    private void refreshAll() {
        pileViews.forEach(PileView::refresh);
        foundationViews.forEach(FoundationView::refresh);}

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
        if (selectedFrom == null) {
            selectedFrom = idx;
            pileViews.get(idx).setStyle("""
                -fx-border-color: #f5c542;
                -fx-border-width: 2;
                -fx-background-insets: 0, 1;
                -fx-effect: dropshadow(gaussian, rgba(245,197,66,0.6), 10, 0.2, 0, 0);
            """);
            return;
        }

        tryMoveLongestRun(selectedFrom, idx);
        pileViews.get(selectedFrom).setStyle("-fx-background-color: #f7f7f7; -fx-border-color: #ddd;");
        selectedFrom = null;
        refreshAll();
        if (game.isWin()) new Alert(Alert.AlertType.INFORMATION, "You win!").showAndWait();
    }

    private void tryMoveLongestRun(int fromIdx, int toIdx) {
        if (fromIdx == toIdx || animating) return;

        var from = game.tableaux.get(fromIdx);
        int size = from.getCards().size();
        if (size == 0) return;

        // Longest face-up, same-suit, descending tail
        int max = 1;
        for (int k = size - 1; k > 0; k--) {
            var above = from.getCards().get(k);
            var below = from.getCards().get(k - 1);
            if (!below.isFaceUp() || !above.isFaceUp()
                    || below.getSuit() != above.getSuit()
                    || below.getRank() != above.getRank() + 1) break;
            max++;
        }

        for (int c = max; c >= 1; c--) {
            final int moveCount = c;
            List<Card> cards = new ArrayList<>(from.getCards().subList(size - c, size));

            if (game.moveRun(fromIdx, moveCount, toIdx)) {
                game.undo();
                animateMove(fromIdx, toIdx, cards, () -> {
                    game.moveRun(fromIdx, moveCount, toIdx);
                    refreshAll();
                    animating = false;
                    if (game.isWin()) new Alert(Alert.AlertType.INFORMATION, "You win!").showAndWait();
                });
                return;
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
            node.setTranslateY(i * -12);
            if (c.isFaceUp() && (c.getSuit().name().equals("HEARTS") || c.getSuit().name().equals("DIAMONDS"))) {
                ((javafx.scene.control.Label) node.getChildren().get(0)).setStyle("-fx-text-fill:#c03232; -fx-font-weight:700; -fx-font-size:15px;");
            } else {
                ((javafx.scene.control.Label) node.getChildren().get(0)).setStyle("-fx-text-fill:#222; -fx-font-weight:700; -fx-font-size:15px;");
            }
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
}