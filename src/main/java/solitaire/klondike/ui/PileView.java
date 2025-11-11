package solitaire.klondike.ui;

//*********************************************
// Klondike Solitaire – Prototype 2
// File: PileView.java
//
// Author: Jenascia Drew
// Course: CPT-237-W38 Java Programming II
// Semester: Fall 2025
// Dates: 10/22/2025–11/11/2025
//
// Description:
// The user interface for a single tableau pile
// in Klondike. Displays cards vertically with
// overlap, supports face-up and face-down rendering, and
// updates dynamically as the pile changes
//*********************************************


import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import solitaire.klondike.model.Card;
import solitaire.klondike.model.Pile;

public class PileView extends VBox {
    private final Pile pile;
    private final String label;

    public PileView(Pile pile, String label) {
        this.pile = pile;
        this.label = label;

        setSpacing(6);
        setAlignment(Pos.TOP_CENTER);
        setPadding(new Insets(8));
        setStyle("-fx-background-color: transparent;");
        setPrefWidth(90);
        setMinWidth(90);
        setMaxWidth(90);

        refresh();
    }

    // Refreshes based on current state of cards
    public void refresh() {
        getChildren().clear();

        Label title = new Label(label);
        title.setStyle("-fx-text-fill: #444; -fx-font-size: 11px;");
        getChildren().add(title);

        // Draw cards in order
        for (int i = 0; i < pile.getCards().size(); i++) {
            Card c = pile.getCards().get(i);
            StackPane cardNode = cardNodeFor(c);
            cardNode.setTranslateY(i * -10);
            getChildren().add(cardNode);
        }

        // If empty, show slot
        if (pile.getCards().isEmpty()) {
            StackPane slot = Basics.card(" ");
            getChildren().add(slot);
        }
    }

    // Front or back of card
    private StackPane cardNodeFor(Card c) {
        // this can be as simple as:
        if (!c.isFaceUp()) {
            StackPane back = Basics.card("🂠");
            return back;
        }
        String text = rankToText(c.getRank()) + suitToSymbol(c);
        StackPane face = Basics.card(text);
        return face;
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
}

