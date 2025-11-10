//*********************************************
// Spider Solitaire – Prototype 2
// File: FoundationView.java
//
// Author: Indy Hinton
// Course: CPT-237-W38 Java Programming II
// Semester: Fall 2025
// Dates: 10/22/2025–11/9/2025
//
// Description:
// Visual representation of the Spider foundation area
//
//*********************************************

package solitaire.spider.ui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import solitaire.spider.model.Pile;

public class FoundationView extends VBox {
    private final Pile pile;
    private final String label;

    public FoundationView(Pile pile, String label) {
        this.pile = pile;
        this.label = label;

        setAlignment(Pos.TOP_CENTER);
        setSpacing(6);
        setPadding(new Insets(8));
        setMinWidth(90);
        setPrefWidth(90);
        setMaxWidth(90);
        setStyle("-fx-background-color: transparent;"); // Updated to match green background

        refresh();
    }

    public void refresh() {
        getChildren().clear();

        Label title = new Label(label);
        title.setStyle("-fx-text-fill: rgba(255,255,255,0.6); -fx-font-size: 11px;");
        getChildren().add(title);

        if (pile.getCards().isEmpty()) {
            StackPane slot = Basics.card(" ");
            slot.setMinSize(64, 90);
            slot.setMaxSize(64, 90);
            slot.setStyle("""
                -fx-background-color: transparent;
                -fx-border-color: rgba(255,255,255,0.25);
                -fx-border-radius: 8;
            """);
            getChildren().add(slot);
        } else {
            StackPane card = Basics.card("✓");
            card.setStyle("""
                -fx-background-color: white;
                -fx-border-color: #b5b5b5;
                -fx-background-radius: 8; -fx-border-radius: 8;
                -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.15), 6, 0, 0, 2);
            """);
            ((Label) card.getChildren().get(0))
                    .setStyle("-fx-font-weight: 700; -fx-font-size: 18px; -fx-text-fill: #22863a;");
            getChildren().add(card);

            // Show how many cards are in foundation
            Label count = new Label(pile.getCards().size() + " cards");
            count.setStyle("-fx-text-fill: rgba(255,255,255,0.6); -fx-font-size: 10px;");
            getChildren().add(count);
        }
    }
}

