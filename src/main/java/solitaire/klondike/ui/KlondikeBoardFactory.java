package solitaire.klondike.ui;

//*********************************************
// Klondike Solitaire – Prototype 2
// File: KlondikeController.java
//
// Author: Jenascia Drew
// Course: CPT-237-W38 Java Programming II
// Semester: Fall 2025
// Dates: 10/22/2025–11/11/2025
//
// Description:
// Visual layout for Klondike Solitaire:
// - The top bar: Stock, Waste, and four Foundations
// - The lower row: 7 Tableau piles
// - Arranges all components using JavaFX layout panes
//*********************************************

import javafx.geometry.Insets;
import javafx.scene.layout.*;
import javafx.scene.control.Label;
import javafx.scene.Node;
import solitaire.core.ThemeManager;
import solitaire.klondike.engine.KlondikeGame;

import java.util.List;

public class KlondikeBoardFactory {

    public static Pane build(KlondikeGame game,
                             List<PileView> outPileViews,
                             List<FoundationView> outFoundationViews) {

        // ---------- TOP ----------
        HBox topBar = new HBox(12);
        topBar.setPadding(new Insets(12, 32, 0, 32));

        // Current placeholders: Stock/Waste
        Node stockView = makePlaceholder("Stock");
        Node wasteView = makePlaceholder("Waste");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox foundationsRow = new HBox(10);
        for (int i = 0; i < KlondikeGame.FOUNDATION_COUNT; i++) {
            FoundationView fv = new FoundationView(game.foundations.get(i), "F" + (i + 1));
            outFoundationViews.add(fv);
            foundationsRow.getChildren().add(fv);
        }

        topBar.getChildren().addAll(stockView, wasteView, spacer, foundationsRow);

        // ---------- TABLEAU ROW ----------
        GridPane gp = new GridPane();
        gp.setHgap(18);
        gp.setVgap(0);
        gp.setPadding(new Insets(16, 32, 16, 32));

        double colW = 90;

        // 7 columns wide enough for each pile
        for (int i = 0; i < KlondikeGame.TABLEAU_COUNT; i++) {
            ColumnConstraints cc = new ColumnConstraints();
            cc.setMinWidth(colW);
            cc.setPrefWidth(colW);
            cc.setMaxWidth(Region.USE_PREF_SIZE);
            gp.getColumnConstraints().add(cc);
        }

        // Add tableau pile views
        for (int col = 0; col < KlondikeGame.TABLEAU_COUNT; col++) {
            PileView pv = new PileView(game.tableaux.get(col), "T" + (col + 1));
            outPileViews.add(pv);
            gp.add(pv, col, 0);
        }

        // ---------- ROOT LAYOUT ----------
        BorderPane layout = new BorderPane();
        layout.setTop(topBar);
        layout.setCenter(gp);

        layout.setPrefSize(1000, 650);
        layout.setMinSize(Region.USE_COMPUTED_SIZE, Region.USE_COMPUTED_SIZE);
        return layout;

    }

    // Placeholder
    private static Node makePlaceholder(String text) {
        StackPane box = new StackPane();
        box.setPrefSize(80, 110);
        box.setStyle("-fx-border-color: #333; -fx-border-radius: 6;");

        Label lbl = new Label(text);
        box.getChildren().add(lbl);
        return box;
    }
}