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

public class PileView extends VBox {
    private final Pile pile;
    private final String label;

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

        Label title = new Label(label);
        title.setStyle("-fx-text-fill: #444; -fx-font-size: 11px;");
        getChildren().add(title);

        // draw cards
        for (int i = 0; i < pile.getCards().size(); i++) {
            Card c = pile.getCards().get(i);
            StackPane cardNode = cardNodeFor(c);
            cardNode.setTranslateY(i * -10);  // overlap
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
        Label dots = new Label("🂠");
        dots.setStyle("-fx-text-fill: rgba(255,255,255,0.85); -fx-font-size: 20px;");
        back.getChildren().add(dots);
        return back;
    }

    private StackPane faceUpCard(Card c) {
        String rank = rankToText(c.getRank());
        String suit = suitToSymbol(c);

        boolean red = (c.getSuit().name().equals("HEARTS") || c.getSuit().name().equals("DIAMONDS"));
        String color = red ? "#c03232" : "#222";

        StackPane root = new StackPane();
        root.setMinSize(64, 90);
        root.setMaxSize(64, 90);
        root.setStyle("""
            -fx-background-color: white;
            -fx-border-color: #b5b5b5;
            -fx-background-radius: 8; -fx-border-radius: 8;
            -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.15), 6, 0, 0, 2);
        """);

        // top-left corner
        VBox tl = new VBox(
                new Label(rank),
                new Label(suit)
        );
        tl.setSpacing(-2);
        tl.setTranslateX(6);
        tl.setTranslateY(6);
        tl.getChildren().forEach(n ->
                ((Label) n).setStyle("-fx-text-fill: " + color + "; -fx-font-size: 11px; -fx-font-weight: bold;")
        );

        // bottom-right corner (rotated)
        VBox br = new VBox(
                new Label(rank),
                new Label(suit)
        );
        br.setSpacing(-2);
        br.setRotate(180);
        br.setTranslateX(-6);
        br.setTranslateY(-6);
        br.getChildren().forEach(n ->
                ((Label) n).setStyle("-fx-text-fill: " + color + "; -fx-font-size: 11px; -fx-font-weight: bold;")
        );

        // center suit
        Label center = new Label(suit);
        center.setStyle("-fx-text-fill: " + color + "; -fx-font-size: 20px; -fx-font-weight: bold;");

        root.getChildren().addAll(center, tl, br);
        StackPane.setAlignment(tl, Pos.TOP_LEFT);
        StackPane.setAlignment(br, Pos.BOTTOM_RIGHT);

        return root;
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