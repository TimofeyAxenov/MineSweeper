package com.example.minesweeper;

public class GameUIState {
    private GameState gameState;
    private Cell[][] field;

    public GameUIState(GameState gameState, Cell[][] field) {
        this.gameState = gameState;
        this.field = field;
    }

    public GameState getGameState() {
        return gameState;
    }

    public Cell[][] getField() {
        return field;
    }
}
