package solitaire.klondike.ui;

//*********************************************
// Klondike Solitaire – Prototype 2
// File: Basics.java
//
// Author: Jenascia Drew
// Course: CPT-237-W38 Java Programming II
// Semester: Fall 2025
// Dates: 10/22/2025–11/2/2025
//
// Description:
// Utility methods for Klondike Solitaire. Provides consistent styling
// and layout helpers for cards, piles, and grids
//*********************************************

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.*;

public class Basics {

    // GridPane with even columns and padding
    public static GridPane grid(int cols) {
        GridPane gp = new GridPane();
        gp.setHgap(8);
        gp.setVgap(8);
        gp.setPadding(new Insets(16));

        double widthPercent = 100.0 / cols;
        for (int i = 0; i < cols; i++) {
            ColumnConstraints cc = new ColumnConstraints();
            cc.setPercentWidth(widthPercent);
            gp.getColumnConstraints().add(cc);
        }
        return gp;
    }

    // Vertical stack pile
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

    // Card placeholder
    public static StackPane card(String text) {
        StackPane c = new StackPane();
        c.setMinSize(64, 86);
        c.setMaxSize(64, 86);
        c.setStyle("""
            -fx-background-color: white;
            -fx-border-color: #bbb;
            -fx-background-radius: 6;
            -fx-border-radius: 6;
            -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.15), 6, 0, 0, 2);
        """);

        Label center = new Label(text);
        center.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        // Themed text color
        boolean isRed = text.contains("♥") || text.contains("♦");
        solitaire.core.ThemeManager.styleCardText(center, isRed);

        c.getChildren().add(center);
        return c;
    }

    public static Pane wrap(Pane p) {
        StackPane w = new StackPane(p);
        w.setPadding(new Insets(8));
        return w;
    }
}

