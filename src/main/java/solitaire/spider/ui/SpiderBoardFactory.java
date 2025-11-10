//*********************************************
// Spider Solitaire – Prototype 2
// File: SpiderBoardFactory.java
//
// Author: Indy Hinton
// Course: CPT-237-W38 Java Programming II
// Semester: Fall 2025
// Dates: 10/22/2025–11/9/2025
//
// Description:
// Builds the layout of the Spider game board
//
//*********************************************

package solitaire.spider.ui;

import javafx.geometry.Insets;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;

import solitaire.spider.engine.SpiderGame;
import java.util.List;

public class SpiderBoardFactory {

    // List parameter
    public static Pane build(SpiderGame game,
                             List<PileView> outPileViews,
                             List<FoundationView> outFoundationViews,
                             List<StockView> outStockViews) {

        // Top Row
        HBox topRow = new HBox(12);
        topRow.setPadding(new Insets(12, 32, 0, 32));
        topRow.setStyle("-fx-background-color: transparent;");

        // Stock
        StockView stockView = new StockView(game.stock);
        outStockViews.add(stockView);
        topRow.getChildren().add(stockView);

        // Foundation
        for (int i = 0; i < SpiderGame.FOUNDATION_COUNT; i++) {
            FoundationView fv = new FoundationView(game.foundations.get(i), "F" + (i + 1));
            outFoundationViews.add(fv);
            topRow.getChildren().add(fv);
        }

        // Tableau grid
        // Fixed widths so all 10 columns show
        // Will update to flexible
        GridPane gp = new GridPane();
        gp.setHgap(18);
        gp.setPadding(new Insets(12, 32, 16, 32));
        gp.setStyle("-fx-background-color: transparent;");

        for (int col = 0; col < SpiderGame.TABLEAU_COUNT; col++) {
            ColumnConstraints cc = new ColumnConstraints();
            cc.setMinWidth(90);
            cc.setPrefWidth(90);
            gp.getColumnConstraints().add(cc);

            PileView pv = new PileView(game.tableaux.get(col), "S" + (col + 1));
            outPileViews.add(pv);
            gp.add(pv, col, 0);
        }

        BorderPane root = new BorderPane();
        root.setTop(topRow);
        root.setCenter(gp);
        root.setStyle("-fx-background-color: transparent;");

        return root;
    }
}
