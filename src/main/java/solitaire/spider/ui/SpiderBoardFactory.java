//*********************************************
// Spider Solitaire – Prototype 2
// File: SpiderBoardFactory.java
//
// Author: Indy Hinton
// Course: CPT-237-W38 Java Programming II
// Semester: Fall 2025
// Dates: 10/22/2025–11/2/2025
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
                             List<FoundationView> outFoundationViews) {

        // Foundations row
        HBox foundationsRow = new HBox(12);
        foundationsRow.setPadding(new Insets(12, 32, 0, 32));
        for (int i = 0; i < SpiderGame.FOUNDATION_COUNT; i++) {
            FoundationView fv = new FoundationView(game.foundations.get(i), "F" + (i + 1));
            outFoundationViews.add(fv);
            foundationsRow.getChildren().add(fv);
        }

        // Tableau grid
        // Fixed widths so all 10 columns show
        // Will update to flexible
        GridPane gp = new GridPane();
        double colW = 90, hgap = 18;
        gp.setHgap(hgap);
        gp.setVgap(0);
        gp.setPadding(new Insets(12, 32, 16, 32));

        for (int i = 0; i < 10; i++) {
            ColumnConstraints cc = new ColumnConstraints();
            cc.setMinWidth(colW);
            cc.setPrefWidth(colW);
            cc.setMaxWidth(Region.USE_PREF_SIZE);
            gp.getColumnConstraints().add(cc);
        }
        double prefW = (colW * 10) + (hgap * 9) + gp.getPadding().getLeft() + gp.getPadding().getRight();
        gp.setPrefWidth(prefW);

        for (int col = 0; col < SpiderGame.TABLEAU_COUNT; col++) {
            PileView pv = new PileView(game.tableaux.get(col), "S" + (col + 1));
            outPileViews.add(pv);
            gp.add(pv, col, 0);
        }

        BorderPane root = new BorderPane();
        root.setTop(foundationsRow);
        root.setCenter(gp);

        return Basics.wrap(root);
    }
}
