package solitaire.klondike.ui;

//*********************************************
// Klondike Solitaire – Prototype 2
// File: FoundationView.java
//
// Author: Jenascia Drew
// Course: CPT-237-W38 Java Programming II
// Semester: Fall 2025
//
// Description:
// Visual of a single foundation pile for Klondike Solitaire.
// Displays a placeholder when
// empty and a checkmark when the foundation is complete
//*********************************************

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import solitaire.klondike.model.Pile;

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

        refresh();
    }

    public void refresh() {
        getChildren().clear();

        // Small label above the pile
        Label title = new Label(label);
        title.setStyle("-fx-text-fill: #666; -fx-font-size: 11px;");
        getChildren().add(title);

        if (pile.getCards().isEmpty()) {
            StackPane slot = makeCardPlaceholder(" ");
            slot.setStyle("""
                -fx-background-color: linear-gradient(#f7f7f7, #f0f0f0);
                -fx-border-color: #cfcfcf;
                -fx-background-radius: 6; -fx-border-radius: 6;
                -fx-opacity: 0.9;
            """);
            getChildren().add(slot);
        } else {
            // Mark completed foundation
            StackPane card = makeCardPlaceholder("✓");
            ((Label) card.getChildren().get(0)).setStyle(
                    "-fx-font-weight: 700; -fx-font-size: 18px; -fx-text-fill: #22863a;"
            );
            getChildren().add(card);
        }
    }

    // Create a simple card-like shape
    private StackPane makeCardPlaceholder(String text) {
        StackPane pane = new StackPane();
        pane.setPrefSize(80, 110);
        pane.setStyle("-fx-border-color: #999; -fx-background-color: white; -fx-border-radius: 6;");
        Label lbl = new Label(text);
        pane.getChildren().add(lbl);
        return pane;
    }
}