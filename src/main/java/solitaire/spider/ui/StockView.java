//*********************************************
// Spider Solitaire – Prototype 2
// File: StockView.java
//
// Author: Indy Hinton
// Course: CPT-237-W38 Java Programming II
// Semester: Fall 2025
// Dates: 10/22/2025–11/9/2025
//
// Description:
// Builds the stockpile on the board showing face-down and
// number of cards left
//
//*********************************************

package solitaire.spider.ui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import solitaire.spider.model.Pile;
import solitaire.spider.model.Card;

public class StockView extends VBox {
    private final Pile stock;

    public StockView(Pile stock) {
        this.stock = stock;
        setAlignment(Pos.TOP_CENTER);
        setPadding(new Insets(8));
        setSpacing(4);
        setPrefWidth(90);
        setMinWidth(90);
        setMaxWidth(90);
        refresh();
    }

    public void refresh() {
        getChildren().clear();

        // Show face-down cards if left
        if (!stock.getCards().isEmpty()) {
            StackPane back = faceDownCard();
            getChildren().add(back);
        }
        else {
            StackPane slot = Basics.card(" ");
            slot.setStyle("""
                -fx-background-color: rgba(255,255,255,0.2);
                -fx-border-color: #cccccc;
                -fx-background-radius: 8; -fx-border-radius: 8;
            """);
            getChildren().add(slot);
        }

        // Show count of cards left
        Label count = new Label(stock.getCards().size() + " cards");
        count.setStyle("-fx-text-fill: #444; -fx-font-size: 10px;");
        getChildren().add(count);
    }

    private StackPane faceDownCard() {
        StackPane back = new StackPane();
        back.setMinSize(64, 90);
        back.setMaxSize(64, 90);
        back.setStyle("""
            -fx-background-color: linear-gradient(#2456a6,#123269);
            -fx-border-color: #0d2247;
            -fx-background-radius: 8; -fx-border-radius: 8;
            -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.25), 6, 0, 0, 2);
        """);
        Label dots = new Label("🕷");
        dots.setStyle("-fx-text-fill: rgba(255,255,255,0.85); -fx-font-size: 20px;");
        back.getChildren().add(dots);
        return back;
    }
}
