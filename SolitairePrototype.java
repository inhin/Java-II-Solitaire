//*********************************************
// Solitaire Prototype 1 -- GUI Components
// Version 2 -- Changes to exit button, event handlers, & menu bar made by Jenascia
//
// Author: Indy Hinton, Danahryien Dendy,
// Course: CPT-237-W38 Java Programming II
// Semester: Fall 2025
// Date: 10/13/2025-10/21/2025
//
// Description:
// This sets up the shell of the Solitaire GUI:
// - BorderPane layout
// - Menu/Toolbar at the top
// - Status bar at the bottom
// - Swappable board in the center
//*********************************************

// JavaFX core classes

//*********************************************
// Solitaire Prototype -- Combined Version
// - Merges playable Pyramid (Deal/Undo, win, HUD, timer)
// - Includes partner's Exit confirmation + Rules/Hints menus
//
// Requires: JavaFX 21 on module-path
//*********************************************

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

// ---------- tiny callback contracts ----------
interface GameEvents {
    default void onMove() {}
    default void onScore(int delta) {}
    default void onWin() {}
    default void onReset() {}
}
interface BoardActions {
    void deal();
    void undo();
}
final class BoardHandle {
    final Pane view;
    final BoardActions actions;
    BoardHandle(Pane view, BoardActions actions) {
        this.view = view; this.actions = actions;
    }
}

// ---------- App ----------
public class SolitairePrototype extends Application {

    // HUD labels
    private final Label lblScore = new Label("Score: 0");
    private final Label lblMoves = new Label("Moves: 0");
    private final Label lblTime  = new Label("Time: 00:00");

    // HUD state
    private int moves = 0;
    private int score = 0;
    private javafx.animation.Timeline timer;
    private int elapsedSec = 0;

    // Variant picker + center board
    private final ComboBox<String> cboVariant = new ComboBox<>();
    private Pane board = new Pane();

    // Actions for toolbar (set by current board)
    private BoardActions actions = new BoardActions() {
        public void deal() {}
        public void undo() {}
    };

    @Override
    public void start(Stage stage) {
        BorderPane root = new BorderPane();

        MenuBar menuBar = buildMenuBar(root);
        ToolBar toolBar = buildToolBar(root);
        VBox top = new VBox(menuBar, toolBar);

        root.setTop(top);
        root.setCenter(board);
        root.setBottom(buildStatusBar());

        Scene scene = new Scene(root, 1000, 650);
        stage.setTitle("Solitaire – GUI Prototype");
        stage.setScene(scene);
        stage.show();

        cboVariant.getItems().addAll("Klondike", "Spider", "Pyramid");
        cboVariant.getSelectionModel().selectFirst();
        cboVariant.valueProperty().addListener((obs, o, v) -> rebuildBoard(v, root));

        rebuildBoard(cboVariant.getValue(), root);
    }

    // ---------- Menu bar ----------
    private MenuBar buildMenuBar(BorderPane root) {
        // Game
        Menu mGame = new Menu("Game");
        MenuItem miNew   = new MenuItem("New Game");
        MenuItem miReset = new MenuItem("Reset");
        MenuItem miExit  = new MenuItem("Exit");

        // Exit confirmation (from partner’s version)
        Alert alertExitMB = new Alert(Alert.AlertType.CONFIRMATION);
        alertExitMB.setTitle("Exit Confirmation");
        alertExitMB.setHeaderText("Exit Solitaire?");
        alertExitMB.setContentText("Are you sure you want to close Solitaire?");

        // File-not-found alert (for Rules/Hints)
        Alert alertFNF = new Alert(Alert.AlertType.ERROR);
        alertFNF.setTitle("File Not Found");
        alertFNF.setHeaderText("File Not Found");
        alertFNF.setContentText("The requested file could not be located.");

        // Wire Game menu
        miNew.setOnAction(e -> rebuildBoard(cboVariant.getValue(), root));
        miReset.setOnAction(e -> rebuildBoard(cboVariant.getValue(), root));
        miExit.setOnAction(e -> {
            Optional<ButtonType> result = alertExitMB.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) System.exit(0);
        });
        mGame.getItems().addAll(miNew, miReset, new SeparatorMenuItem(), miExit);

        // View
        Menu mView = new Menu("View");
        MenuItem hintsItem = new MenuItem("Hints");
        hintsItem.setOnAction(e -> showTextFileInDialog("Hints", alertFNF));
        mView.getItems().add(hintsItem);

        // Help
        Menu mHelp = new Menu("Help");
        MenuItem rulesItem = new MenuItem("Rules");
        rulesItem.setOnAction(e -> showTextFileInDialog("Rules", alertFNF));
        mHelp.getItems().add(rulesItem);

        return new MenuBar(mGame, mView, mHelp);
    }

    // Shows text file content in a dialog (reads from working dir)
    private void showTextFileInDialog(String fileName, Alert alertFNF) {
        File f = new File(fileName);
        StringBuilder sb = new StringBuilder();
        try (Scanner sc = new Scanner(f)) {
            while (sc.hasNextLine()) sb.append(sc.nextLine()).append("\n");
            TextArea area = new TextArea(sb.toString());
            area.setEditable(false);
            area.setWrapText(true);
            area.setPrefSize(600, 400);
            Dialog<Void> d = new Dialog<>();
            d.setTitle(fileName);
            d.getDialogPane().setContent(area);
            d.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
            d.showAndWait();
        } catch (FileNotFoundException ex) {
            alertFNF.setContentText(fileName + " file could not be located.");
            alertFNF.showAndWait();
        }
    }

    // ---------- Toolbar ----------
    private ToolBar buildToolBar(BorderPane root) {
        Button btnNew  = new Button("New");
        Button btnDeal = new Button("Deal/Draw");
        Button btnUndo = new Button("Undo");
        Button btnExit = new Button("Exit");

        // Exit confirmation (from partner’s version)
        Alert alertExitB = new Alert(Alert.AlertType.CONFIRMATION);
        alertExitB.setTitle("Exit Confirmation");
        alertExitB.setHeaderText("Exit Solitaire?");
        alertExitB.setContentText("Are you sure you want to close Solitaire?");

        btnNew.setOnMouseReleased(e -> rebuildBoard(cboVariant.getValue(), root));
        btnDeal.setOnMouseReleased(e -> actions.deal());
        btnUndo.setOnMouseReleased(e -> actions.undo());
        btnExit.setOnMouseReleased(e -> {
            Optional<ButtonType> result = alertExitB.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) System.exit(0);
        });

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

    // ---------- Status bar ----------
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

    // ---------- HUD helpers ----------
    private void resetHud() {
        moves = 0;
        score = 0;
        elapsedSec = 0;
        lblMoves.setText("Moves: 0");
        lblScore.setText("Score: 0");
        lblTime.setText("Time: 00:00");
    }

    private void startTimer() {
        if (timer != null) timer.stop();
        timer = new javafx.animation.Timeline(
                new javafx.animation.KeyFrame(javafx.util.Duration.seconds(1), e -> {
                    elapsedSec++;
                    int m = elapsedSec / 60, s = elapsedSec % 60;
                    lblTime.setText(String.format("Time: %02d:%02d", m, s));
                })
        );
        timer.setCycleCount(javafx.animation.Animation.INDEFINITE);
        timer.play();
    }

    // ---------- Board rebuild ----------
    private void rebuildBoard(String variant, BorderPane root) {
        resetHud();
        startTimer();

        GameEvents events = new GameEvents() {
            @Override public void onMove() {
                moves++;
                lblMoves.setText("Moves: " + moves);
            }
            @Override public void onScore(int delta) {
                score += delta;
                lblScore.setText("Score: " + score);
            }
            @Override public void onWin() {
                if (timer != null) timer.stop();
                Alert a = new Alert(Alert.AlertType.INFORMATION,
                        "You cleared the pyramid!\n" +
                                "Moves: " + moves + "\n" +
                                lblTime.getText());
                a.setHeaderText("You Win!");
                a.showAndWait();
            }
            @Override public void onReset() {
                resetHud();
            }
        };

        BoardHandle handle = switch (variant) {
            case "Spider"  -> new BoardHandle(SpiderBoardFactory.build(), noopActions());
            case "Pyramid" -> PyramidBoardFactory.build(events);
            default        -> new BoardHandle(KlondikeBoardFactory.build(), noopActions());
        };

        root.setCenter(handle.view);
        board   = handle.view;
        actions = handle.actions;
    }

    private BoardActions noopActions() {
        return new BoardActions() { public void deal() {} public void undo() {} };
    }

    public static void main(String[] args) { launch(); }
}

// ---------- Visual-only Klondike ----------
class KlondikeBoardFactory {
    public static Pane build() {
        GridPane gp = Basics.grid(7);
        for (int col = 0; col < 7; col++)
            gp.add(Basics.pile("T" + (col + 1), 5 + col), col, 0);
        return Basics.wrap(gp);
    }
}

// ---------- Visual-only Spider ----------
class SpiderBoardFactory {
    public static Pane build() {
        GridPane gp = Basics.grid(10);
        for (int col = 0; col < 10; col++)
            gp.add(Basics.pile("S" + (col + 1), 6), col, 0);
        return Basics.wrap(gp);
    }
}

// ---------- Pyramid (playable with Deal + Undo) ----------
class PyramidBoardFactory {

    enum Suit { CLUBS, DIAMONDS, HEARTS, SPADES }

    static final class CardModel {
        final int rank;          // 1..13 (Ace=1, King=13)
        final Suit suit;
        boolean removed = false; // when taken from pyramid
        CardModel(int r, Suit s) { rank = r; suit = s; }
        boolean isKing() { return rank == 13; }
        int value() { return rank; }
    }

    static final class CardView extends StackPane {
        final CardModel model;
        boolean selected = false;

        CardView(CardModel m) {
            this.model = m;
            setMinSize(64, 86);
            setMaxSize(64, 86);
            setStyle("""
                -fx-background-color: white;
                -fx-border-color: #bbb;
                -fx-background-radius: 6;
                -fx-border-radius: 6;
                -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.15), 6, 0, 0, 2);
            """);
            Label face = new Label(faceText(m));
            face.setFont(Font.font(16));
            getChildren().add(face);
        }

        static String faceText(CardModel c) {
            String r = switch (c.rank) { case 1 -> "A"; case 11 -> "J"; case 12 -> "Q"; case 13 -> "K"; default -> String.valueOf(c.rank); };
            String s = switch (c.suit) { case CLUBS -> "♣"; case DIAMONDS -> "♦"; case HEARTS -> "♥"; case SPADES -> "♠"; };
            return r + s;
        }

        void setSelected(boolean on) {
            selected = on;
            setStyle( on
                    ? "-fx-background-color: #e8f0fe; -fx-border-color: #3b6df6; -fx-background-radius: 6; -fx-border-radius: 6;"
                    : "-fx-background-color: white; -fx-border-color: #bbb; -fx-background-radius: 6; -fx-border-radius: 6;");
        }
    }

    public static BoardHandle build(GameEvents events) {
        events.onReset();

        Pane root = new Pane();
        root.setPrefSize(1000, 520);

        // Deck and deal
        List<CardModel> deck = freshDeck();
        Collections.shuffle(deck, new Random());

        CardView[][] py = new CardView[7][];
        int di = 0;
        for (int r = 0; r < 7; r++) {
            py[r] = new CardView[r + 1];
            for (int c = 0; c <= r; c++) {
                py[r][c] = new CardView(deck.get(di++));
            }
        }
        Deque<CardModel> stock = new ArrayDeque<>();
        while (di < deck.size()) stock.push(deck.get(di++));

        Deque<CardModel> waste = new ArrayDeque<>();

        // Layout
        double centerX = 500, topY = 40, dx = 78, dy = 28;
        for (int r = 0; r < 7; r++) {
            double rowWidth = (r + 1) * dx;
            double startX = centerX - rowWidth / 2.0;
            for (int c = 0; c <= r; c++) {
                CardView cv = py[r][c];
                cv.setLayoutX(startX + c * dx);
                cv.setLayoutY(topY + r * dy);
                root.getChildren().add(cv);
            }
        }

        StackPane stockSlot = Basics.card(""); stockSlot.setLayoutX(140); stockSlot.setLayoutY(380);
        StackPane wasteSlot = Basics.card(""); wasteSlot.setLayoutX(230); wasteSlot.setLayoutY(380);
        Label lblStock = new Label("Stock"); lblStock.setLayoutX(146); lblStock.setLayoutY(470);
        Label lblWaste = new Label("Waste"); lblWaste.setLayoutX(240); lblWaste.setLayoutY(470);

        Runnable refreshWaste = () -> {
            wasteSlot.getChildren().clear();
            if (!waste.isEmpty()) {
                wasteSlot.getChildren().add(new Label(CardView.faceText(waste.peek())));
            }
        };

        final CardView[] sel = new CardView[1];
        final int[] removedCount = {0};

        Runnable checkWin = () -> {
            if (removedCount[0] >= 28) events.onWin();
        };

        java.util.function.BiPredicate<Integer,Integer> isUncovered = (row,col) -> {
            if (row == 6) return true;
            CardView dl = py[row+1][col];
            CardView dr = py[row+1][col+1];
            return (dl.model.removed) && (dr.model.removed);
        };

        // ---- UNDO STACK ----
        Deque<Runnable> undo = new ArrayDeque<>();

        // helpers to hide/show pyramid cards
        java.util.function.Consumer<CardView> hide = (cv) -> {
            cv.setDisable(true);
            cv.setOpacity(0.0);
            cv.model.removed = true;
        };
        java.util.function.Consumer<CardView> show = (cv) -> {
            cv.setDisable(false);
            cv.setOpacity(1.0);
            cv.model.removed = false;
        };

        // ---- Deal/Recycle implementation (also used by toolbar) ----
        Runnable doFlip = () -> {
            if (!stock.isEmpty()) {
                CardModel cm = stock.pop();
                waste.push(cm);
                refreshWaste.run();
                events.onMove();
                undo.push(() -> {
                    CardModel x = waste.pop();
                    stock.push(x);
                    refreshWaste.run();
                });
            } else {
                if (waste.isEmpty()) return;
                List<CardModel> moved = new ArrayList<>();
                while (!waste.isEmpty()) {
                    CardModel cm = waste.pop();
                    stock.push(cm);
                    moved.add(cm);
                }
                refreshWaste.run();
                events.onMove();
                undo.push(() -> {
                    for (int i = moved.size() - 1; i >= 0; i--) {
                        CardModel cm = moved.get(i);
                        CardModel chk = stock.pop(); // sanity
                        waste.push(chk);
                    }
                    refreshWaste.run();
                });
            }
        };
        stockSlot.setOnMouseClicked(e -> doFlip.run());

        // Pyramid clicks
        for (int r = 0; r < 7; r++) {
            for (int c = 0; c <= r; c++) {
                final int rr = r, cc = c;
                CardView cv = py[r][c];
                cv.setOnMouseClicked(e -> {
                    if (cv.model.removed) return;
                    if (!isUncovered.test(rr, cc)) return;

                    if (cv.model.isKing()) {
                        hide.accept(cv);
                        removedCount[0]++;
                        events.onMove();
                        events.onScore(100);
                        undo.push(() -> { show.accept(cv); removedCount[0]--; });
                        checkWin.run();
                        return;
                    }

                    if (sel[0] == null) {
                        sel[0] = cv;
                        cv.setSelected(true);
                    } else if (sel[0] == cv) {
                        cv.setSelected(false);
                        sel[0] = null;
                    } else {
                        int sum = sel[0].model.value() + cv.model.value();
                        if (sum == 13) {
                            CardView a = sel[0], b = cv;
                            hide.accept(a); hide.accept(b);
                            removedCount[0] += 2;
                            events.onMove();
                            events.onScore(150);
                            undo.push(() -> { show.accept(a); show.accept(b); removedCount[0] -= 2; });
                            checkWin.run();
                        } else {
                            sel[0].setSelected(false);
                        }
                        sel[0] = null;
                    }
                });
            }
        }

        // Waste pairing
        wasteSlot.setOnMouseClicked(e -> {
            if (waste.isEmpty()) return;
            CardModel top = waste.peek();
            if (sel[0] == null && top.rank == 13) {
                waste.pop();
                refreshWaste.run();
                events.onMove();
                events.onScore(80);
                undo.push(() -> { waste.push(top); refreshWaste.run(); });
                return;
            }
            if (sel[0] != null) {
                CardView a = sel[0]; sel[0] = null;
                int[] pos = findRow(py, a);
                if (pos[0] == -1) { a.setSelected(false); return; }
                int sum = a.model.value() + top.value();
                if (sum == 13 && isUncovered.test(pos[0], pos[1])) {
                    waste.pop();
                    hide.accept(a);
                    removedCount[0] += 1;
                    events.onMove();
                    events.onScore(120);
                    undo.push(() -> { show.accept(a); waste.push(top); refreshWaste.run(); removedCount[0] -= 1; });
                    refreshWaste.run();
                    checkWin.run();
                } else {
                    a.setSelected(false);
                }
            }
        });

        // Add nodes
        root.getChildren().addAll(stockSlot, wasteSlot, lblStock, lblWaste);

        // Actions (for toolbar)
        BoardActions actions = new BoardActions() {
            @Override public void deal() { doFlip.run(); }
            @Override public void undo() {
                if (!undo.isEmpty()) undo.pop().run();
            }
        };

        return new BoardHandle(root, actions);
    }

    private static int[] findRow(CardView[][] py, CardView target) {
        for (int r = 0; r < py.length; r++) {
            for (int c = 0; c < py[r].length; c++) {
                if (py[r][c] == target) return new int[]{r,c};
            }
        }
        return new int[]{-1,-1};
    }

    private static List<CardModel> freshDeck() {
        List<CardModel> d = new ArrayList<>(52);
        for (Suit s : Suit.values()) for (int r = 1; r <= 13; r++) d.add(new CardModel(r, s));
        return d;
    }
}

// ---------- Basics ----------
class Basics {
    static GridPane grid(int cols) {
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

    static VBox pile(String label, int height) {
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

    static StackPane card(String text) {
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
        c.getChildren().add(new Label(text));
        return c;
    }

    static Pane wrap(Pane p) {
        StackPane w = new StackPane(p);
        w.setPadding(new Insets(8));
        return w;
    }
}

