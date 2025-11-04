//*********************************************
// Spider Solitaire – Prototype 2
// File: FoundationView.java
//
// Author: Indy Hinton
// Course: CPT-237-W38 Java Programming II
// Semester: Fall 2025
// Dates: 10/22/2025–11/2/2025
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

        refresh();
    }

    public void refresh() {
        getChildren().clear();

        Label title = new Label(label);
        title.setStyle("-fx-text-fill: #666; -fx-font-size: 11px;");
        getChildren().add(title);

        if (pile.getCards().isEmpty()) {
            StackPane slot = Basics.card(" ");
            slot.setStyle("""
                -fx-background-color: linear-gradient(#f7f7f7,#f0f0f0);
                -fx-border-color: #cfcfcf;
                -fx-background-radius: 6; -fx-border-radius: 6;
                -fx-opacity: 0.9;
            """);
            getChildren().add(slot);
        } else {
            StackPane card = Basics.card("✓");
            ((Label)card.getChildren().get(0)).setStyle("-fx-font-weight: 700; -fx-font-size: 18px; -fx-text-fill: #22863a;");
            getChildren().add(card);
        }
    }
}

