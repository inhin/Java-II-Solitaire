package solitaire.core;

import javafx.scene.control.Label;
import javafx.scene.layout.Region;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;

public class ThemeManager {

    private static Theme current = Theme.CLASSIC;

    public static void setTheme(Theme theme) {
        current = theme;
    }

    public static Theme getTheme() {
        return current;
    }

    public static void applyBackground(Region boardRoot) {
        if (boardRoot == null) return;

        String style = switch (current) {
            case CLASSIC -> "-fx-background-color: radial-gradient(radius 170%, #2b6e36, #1f4d28);";
            case OCEAN -> "-fx-background-color: linear-gradient(to bottom, #4fa3d1, #0d508c);";
            case DARK -> "-fx-background-color: linear-gradient(to bottom, #1a1a1a, #000000);";
        };

        boardRoot.setStyle(style);

        javafx.scene.Parent parent = boardRoot.getParent();
        while (parent != null) {
            if (parent instanceof Region r) {
                r.setStyle(style);
            }
            parent = parent.getParent();
        }
    }

    public static void styleCardText(Label lbl, boolean isRedSuit) {
        switch (current) {

            case CLASSIC -> {
                lbl.setStyle((isRedSuit)
                        ? "-fx-text-fill: #c03232; -fx-font-size:16px; -fx-font-weight:700;"
                        : "-fx-text-fill: #222222; -fx-font-size:16px; -fx-font-weight:700;");
            }

            case OCEAN -> {
                lbl.setStyle((isRedSuit)
                        ? "-fx-text-fill: #ff7171; -fx-font-size:16px; -fx-font-weight:700;"
                        : "-fx-text-fill: #0066aa; -fx-font-size:16px; -fx-font-weight:700;");
            }

            case DARK -> {
                lbl.setStyle((isRedSuit)
                        ? "-fx-text-fill: #ff5555; -fx-font-size:16px; -fx-font-weight:700;"
                        : "-fx-text-fill: #dddddd; -fx-font-size:16px; -fx-font-weight:700;");
            }
        }
    }

    public static void styleCardFace(StackPane card) {
        String style = switch (current) {
            case CLASSIC -> """
            -fx-background-color: white;
            -fx-border-color: #b5b5b5;
            -fx-background-radius: 8; 
            -fx-border-radius: 8;
            -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.15), 6, 0, 0, 2);
        """;

            case OCEAN -> """
            -fx-background-color: linear-gradient(#e6f3ff,#b5d9ff);
            -fx-border-color: #2970b8;
            -fx-background-radius: 8; 
            -fx-border-radius: 8;
            -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.25), 6, 0, 0, 2);
        """;

            case DARK -> """
            -fx-background-color: linear-gradient(#333333,#181818);
            -fx-border-color: #888888;
            -fx-background-radius: 8; 
            -fx-border-radius: 8;
            -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.6), 6, 0, 0, 2);
        """;
        };

        card.setStyle(style);
    }
}
