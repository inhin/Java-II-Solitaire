//*********************************************
// Solitaire Prototype 2 — GUI Shell + Spider Wiring
// Version 3 — Spider controller integration, board swap, toolbar/menu wiring
//
// Author: Indy Hinton
// Course: CPT-237-W38 Java Programming II
// Semester: Fall 2025
// Dates: 10/22/2025–11/9/2025
//
// Description:
// Application shell using BorderPane with:
// - Menu + Toolbar (top)
// - Status bar (bottom)
// - Swappable board in the center (Klondike/Spider/Pyramid)
// - Spider variant uses game-backed SpiderController
//*********************************************

// JavaFX core classes
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.stage.Stage;

// Spider UI
import solitaire.spider.ui.SpiderController;
import solitaire.spider.ui.Basics;

public class SolitairePrototype extends Application {

    // ====== UI State ======
    private final Label lblScore = new Label("Score: 0");
    private final Label lblMoves = new Label("Moves: 0");
    private final Label lblTime  = new Label("Time: 00:00");

    private final ComboBox<String> cboVariant = new ComboBox<>();
    private Pane board = new Pane();          // center board
    private BorderPane root;                  // keep a reference to root layout

    // Spider controller
    private final SpiderController spider = new SpiderController();

    @Override
    public void start(Stage stage) {
        // Top: menu + toolbar
        MenuBar menuBar = buildMenuBar();
        ToolBar toolBar = buildToolBar();
        VBox top = new VBox(menuBar, toolBar);

        // Bottom: status bar
        HBox status = buildStatusBar();

        // Root layout
        root = new BorderPane();
        root.setStyle("-fx-background-color: #325b3b;"); // Updated 11-9-25 to green "felt" background
        root.setTop(top);
        root.setCenter(board);
        root.setBottom(status);

        // Scene
        Scene scene = new Scene(root, 1000, 650);
        stage.setTitle("Solitaire – GUI Prototype");
        stage.setScene(scene);
        stage.show();

        // Variants
        cboVariant.getItems().addAll("Klondike", "Spider", "Pyramid");
        cboVariant.getSelectionModel().selectFirst();
        cboVariant.valueProperty().addListener((obs, oldV, newV) -> rebuildBoard(newV));

        // Initial board
        rebuildBoard(cboVariant.getValue());
    }

    // ====== Menus ======
    private MenuBar buildMenuBar() {
        Menu mGame = new Menu("Game");
        MenuItem miNew   = new MenuItem("New Game");
        MenuItem miReset = new MenuItem("Reset");
        MenuItem miExit  = new MenuItem("Exit");

        // Menu actions
        miNew.setOnAction(e -> {
            if ("Spider".equals(cboVariant.getValue())) {
                spider.onNewGame();
                updateStatusBar();
            } else {
                rebuildBoard(cboVariant.getValue());
            }
        });

        // Reset can simply rebuild current variant (for now)
        miReset.setOnAction(e -> rebuildBoard(cboVariant.getValue()));
        miExit.setOnAction(e -> System.exit(0));

        mGame.getItems().addAll(miNew, miReset, new SeparatorMenuItem(), miExit);

        Menu mView = new Menu("View");
        mView.getItems().add(new CheckMenuItem("Show Hints"));

        Menu mHelp = new Menu("Help");
        mHelp.getItems().add(new MenuItem("Rules"));

        return new MenuBar(mGame, mView, mHelp);
    }

    // ====== Toolbar ======
    private ToolBar buildToolBar() {
        Button btnNew  = new Button("New");
        Button btnDeal = new Button("Deal/Draw");
        Button btnUndo = new Button("Undo");
        Button btnExit = new Button("Exit");

        // New
        btnNew.setOnMouseReleased(e -> {
            if ("Spider".equals(cboVariant.getValue())) {
                spider.onNewGame();
                updateStatusBar();
            } else {
                rebuildBoard(cboVariant.getValue());
            }
        });

        // Deal
        btnDeal.setOnMouseReleased(e -> {
            if ("Spider".equals(cboVariant.getValue())) {
                boolean ok = spider.onDeal();
                if (!ok) {
                    Alert a = new Alert(Alert.AlertType.INFORMATION, "Cannot deal while any column is empty.");
                    a.setHeaderText(null);
                    a.showAndWait();
                }
                updateStatusBar();
            }
        });

        // Undo
        btnUndo.setOnMouseReleased(e -> {
            if ("Spider".equals(cboVariant.getValue())) {
                spider.onUndo();
                updateStatusBar();
            }
        });

        // Exit
        btnExit.setOnMouseReleased(e -> System.exit(0));

        cboVariant.setPrefWidth(160);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        return new ToolBar(
                new Label("Variant:"), cboVariant,
                new Separator(),
                btnNew, btnDeal, btnUndo,
                spacer,
                btnExit
        );
    }

    // ====== Status Bar ======
    private HBox buildStatusBar() {
        lblScore.setFont(Font.font(13));
        lblMoves.setFont(Font.font(13));
        lblTime.setFont(Font.font(13));

        HBox status = new HBox(20, lblScore, lblMoves, lblTime);
        status.setPadding(new Insets(8));
        status.setAlignment(Pos.CENTER_LEFT);
        status.setStyle("""
            -fx-background-color: linear-gradient(to right, #f4f4f4, #eaeaea);
            -fx-border-color: #d0d0d0;
            -fx-border-width: 1 0 0 0;
        """);
        return status;
    }

    // ====== Board Swap ======
    private void rebuildBoard(String variant) {
        javafx.scene.Node newBoard = switch (variant) {
            case "Spider"  -> spider.createSpiderBoard();   // built via controller
            case "Pyramid" -> PyramidBoardFactory.build();  // visual only stub for now
            default        -> KlondikeBoardFactory.build(); // visual only stub for now
        };
        root.setCenter(newBoard);
        board = (Pane) newBoard;
        updateStatusBar();
    }

    // Only Spider has live metrics for now
    private void updateStatusBar() {
        lblMoves.setText("Moves: " + spider.getMoveCount());
        lblScore.setText("Score: " + spider.getScore());
        // lblTime will be wired later
    }

    public static void main(String[] args) {
        launch();
    }
}

// Klondike Board (visual-only)
class KlondikeBoardFactory {
    public static Pane build() {
        GridPane gp = Basics.grid(7);
        for (int col = 0; col < 7; col++)
            gp.add(Basics.pile("T" + (col + 1), 5 + col), col, 0);
        return Basics.wrap(gp);
    }
}

// Pyramid Board (visual-only)
class PyramidBoardFactory {
    public static Pane build() {
        VBox root = new VBox(10);
        root.setPadding(new Insets(16));
        for (int r = 1; r <= 7; r++) {
            HBox row = new HBox(8);
            row.setAlignment(Pos.CENTER);
            for (int i = 0; i < r; i++)
                row.getChildren().add(Basics.card("■"));
            root.getChildren().add(row);
        }
        return root;
    }
}
