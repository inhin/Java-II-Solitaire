package solitaire.core;

public interface GameEvents {
    default void onMove() {}
    default void onScore(int delta) {}
    default void onWin() {}
    default void onReset() {}
}
