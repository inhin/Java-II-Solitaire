//*********************************************
// Spider Solitaire – Prototype 2
// File: PileView.java
//
// Author: Indy Hinton
// Course: CPT-237-W38 Java Programming II
// Semester: Fall 2025
// Dates: 10/22/2025–11/2/2025
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
        setStyle("-fx-background-color: #f7f7f7; -fx-border-color: #ddd;");
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

        // Draw cards
        for (int i = 0; i < pile.getCards().size(); i++) {
            Card c = pile.getCards().get(i);
            StackPane cardNode = cardNodeFor(c);
            cardNode.setTranslateY(i * -10);
            getChildren().add(cardNode);
        }

        // Empty pile placeholder
        if (pile.getCards().isEmpty()) {
            StackPane slot = Basics.card(" ");
            getChildren().add(slot);
        }
    }

    private StackPane cardNodeFor(Card c) {
        if (!c.isFaceUp()) {
            StackPane back = Basics.card("🂠");
            back.setStyle("""
                -fx-background-color: linear-gradient(#e6e6e6,#d8d8d8);
                -fx-border-color: #bbb;
                -fx-background-radius: 6; -fx-border-radius: 6;
                -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.15), 6, 0, 0, 2);
            """);
            return back;
        }

        String text = rankToText(c.getRank()) + suitToSymbol(c);
        StackPane face = Basics.card(text);
        boolean red = (c.getSuit().name().equals("HEARTS") || c.getSuit().name().equals("DIAMONDS"));
        face.setStyle("""
            -fx-background-color: white;
            -fx-border-color: #bbb;
            -fx-background-radius: 6; -fx-border-radius: 6;
            -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.15), 6, 0, 0, 2);
        """);
        Label lbl = (Label) face.getChildren().get(0);
        lbl.setStyle(red ? "-fx-text-fill: #c03232; -fx-font-weight: bold;"
                : "-fx-text-fill: #222; -fx-font-weight: bold;");
        return face;
    }

    private String rankToText(int r) {
        return switch (r) { case 1 -> "A"; case 11 -> "J"; case 12 -> "Q"; case 13 -> "K"; default -> String.valueOf(r); };
    }
    private String suitToSymbol(Card c) {
        return switch (c.getSuit()) {
            case SPADES -> "♠";
            case HEARTS -> "♥";
            case DIAMONDS -> "♦";
            case CLUBS -> "♣";
        };
    }

    public Pile getPile() { return pile; }
}
