//*********************************************
// Spider Solitaire – Prototype 2
// File: Basics.java
//
// Author: Indy Hinton
// Course: CPT-237-W38 Java Programming II
// Semester: Fall 2025
// Dates: 10/22/2025–11/2/2025
//
// Description:
// Utility methods for JavaFX UI components
// Includes helpers to keep UI consistent
//
//*********************************************

package solitaire.spider.ui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public class Basics {
    public static GridPane grid(int cols) {
        GridPane gp = new GridPane();
        gp.setHgap(8);
        gp.setVgap(8);
        gp.setPadding(new Insets(16));

        ColumnConstraints cc = new ColumnConstraints();
        cc.setPercentWidth(100.0 / cols);
        for (int i = 0; i < cols; i++)
            gp.getColumnConstraints().add(new ColumnConstraints(cc.getPercentWidth()));
        return gp;
    }

    public static VBox pile(String label, int height) {
        VBox pile = new VBox(6);
        pile.setAlignment(Pos.TOP_CENTER);

        Label title = new Label(label);
        title.setStyle("-fx-text-fill: #444; -fx-font-size: 11px;");
        pile.getChildren().add(title);

        for (int i = 0; i < height; i++) {
            StackPane card = card(" ");
            card.setTranslateY(i * -18);
            pile.getChildren().add(card);
        }

        pile.setStyle("-fx-background-color: #f7f7f7; -fx-border-color: #ddd;");
        pile.setPadding(new Insets(8));
        return pile;
    }

    // Updated 11-9-25 to look more like real cards
    public static StackPane card(String text) {
        StackPane c = new StackPane();
        c.setMinSize(64, 90);
        c.setMaxSize(64, 90);
        c.setPrefSize(64, 90);
        c.setStyle("""
            -fx-background-color: white;
            -fx-border-color: #b5b5b5;
            -fx-background-radius: 8;
            -fx-border-radius: 8;
            -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.15), 6, 0, 0, 2);
        """);
        Label center = new Label(text);
        center.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #222;");
        c.getChildren().add(center);
        return c;
    }

    public static Pane wrap(Pane p) {
        StackPane w = new StackPane(p);
        w.setPadding(new Insets(8));
        return w;
    }
}

