package com.example.minesweeper;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class GameViewModel extends ViewModel {

    private Game game;
    private final MutableLiveData<GameUIState> uiState = new MutableLiveData();

    public LiveData<GameUIState> getUIState() {
        return uiState;
    }

    public void startGame(int width, int height, int mineCount) {
        game = new Game(width, height, mineCount);
        updateState();
    }

    public void onCellClick(int x, int y){
        game.openCell(x, y);
        updateState();
    }
    public void onCellLongClick(int x, int y){
        game.toggleFlag(x, y);
        updateState();
    }
    public void updateState() {
        uiState.setValue(
                new GameUIState(
                        game.getGameState(),
                        game.getAvailableData()
                )
        );
    }
}
