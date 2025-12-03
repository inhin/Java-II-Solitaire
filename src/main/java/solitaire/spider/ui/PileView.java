//*********************************************
// Spider Solitaire – Prototype 2
// File: PileView.java
//
// Author: Indy Hinton
// Course: CPT-237-W38 Java Programming II
// Semester: Fall 2025
// Dates: 10/22/2025–11/9/2025
//
// Description:
// This is the user interface of each tableau pile
// Manages how cards are drawn, layered, and updated
//
//*********************************************

package solitaire.spider.ui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import solitaire.spider.model.Card;
import solitaire.spider.model.Pile;
import solitaire.core.ThemeManager;
import java.util.function.IntConsumer;

public class PileView extends VBox {
    private final Pile pile;
    private final String label;
    public IntConsumer onCardClicked;


    public PileView(Pile pile, String label) {
        this.pile = pile;
        this.label = label;

        setSpacing(6);
        setAlignment(Pos.TOP_CENTER);
        setPadding(new Insets(8));
        setStyle("-fx-background-color: transparent; -fx-border-color: transparent;");
        setPrefWidth(90);
        setMinWidth(90);
        setMaxWidth(90);

        refresh();
    }

    public void refresh() {
        getChildren().clear();

        // draw cards
        // Updated 12/1/25: Fixed highlight to selected card, not just bottom?
        for (int i = 0; i < pile.getCards().size(); i++) {
            final int cardIndex = i;
            Card c = pile.getCards().get(i);

            StackPane cardNode = cardNodeFor(c);
            cardNode.setTranslateY(i * -10);

            cardNode.setOnMouseClicked(e ->
                    onCardClicked.accept(cardIndex)
            );

            getChildren().add(cardNode);
        }


        // Empty pile placeholder
        // Updated 11-9-25 to match green background
        if (pile.getCards().isEmpty()) {
            StackPane slot = new StackPane();
            slot.setMinSize(64, 90);
            slot.setMaxSize(64, 90);
            slot.setStyle("""
                 -fx-background-color: transparent;
                 -fx-border-color: rgba(255,255,255,0.25);
                 -fx-border-radius: 8;
            """);
            getChildren().add(slot);
        }
    }

    // Updated to build more realistic cards with suits/colors
    private StackPane cardNodeFor(Card c) {
        if (!c.isFaceUp()) {
            return faceDownCard();
        }
        return faceUpCard(c);
    }

    private StackPane faceDownCard() {
        StackPane back = new StackPane();
        back.setMinSize(64, 90);
        back.setMaxSize(64, 90);
        back.setStyle("""
            -fx-background-color: linear-gradient(#2456a6,#123269);
            -fx-border-color: #0d2247;
            -fx-background-radius: 8; -fx-border-radius: 8;
            -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.25), 6, 0, 0, 2);
        """);
        Label dots = new Label("🕷");
        dots.setStyle("-fx-text-fill: rgba(255,255,255,0.85); -fx-font-size: 20px;");
        back.getChildren().add(dots);
        return back;
    }

    private StackPane faceUpCard(Card c) {
        String rank = rankToText(c.getRank());
        String suit = suitToSymbol(c);

        StackPane root = new StackPane();
        root.setMinSize(64, 90);
        root.setMaxSize(64, 90);

        // Themed card face style
        ThemeManager.styleCardFace(root);

        boolean isRed = (c.getSuit().name().equals("HEARTS") ||
                c.getSuit().name().equals("DIAMONDS"));

        // --- Top left ---
        Label tlRank = new Label(rank);
        solitaire.core.ThemeManager.styleCardText(tlRank, isRed);
        VBox tl = new VBox(tlRank);
        tl.setSpacing(-2);
        tl.setTranslateX(6);
        tl.setTranslateY(6);
        StackPane.setAlignment(tl, Pos.TOP_LEFT);

        // --- Bottom right ---
        Label brRank = new Label(rank);
        solitaire.core.ThemeManager.styleCardText(brRank, isRed);
        VBox br = new VBox(brRank);
        br.setSpacing(-2);
        br.setRotate(180);
        br.setTranslateX(-6);
        br.setTranslateY(-6);
        StackPane.setAlignment(br, Pos.BOTTOM_RIGHT);

        // --- Center ---
        Label center = new Label(suit);
        solitaire.core.ThemeManager.styleCardText(center, isRed);

        root.getChildren().addAll(center, tl, br);
        return root;
    }

    // The top (visible) card node or null if empty
    public StackPane getTopCardNode() {
        if (pile.getCards().isEmpty()) return null;
        // children: [0] = title label, then cards/slot
        var last = getChildren().get(getChildren().size() - 1);
        if (last instanceof StackPane sp) {
            return sp;
        }
        return null;
    }

    private String rankToText(int r) {
        return switch (r) {
            case 1 -> "A";
            case 11 -> "J";
            case 12 -> "Q";
            case 13 -> "K";
            default -> String.valueOf(r);
        };
    }

    private String suitToSymbol(Card c) {
        return switch (c.getSuit()) {
            case SPADES -> "♠";
            case HEARTS -> "♥";
            case DIAMONDS -> "♦";
            case CLUBS -> "♣";
        };
    }

    public Pile getPile() {
        return pile;
    }
}