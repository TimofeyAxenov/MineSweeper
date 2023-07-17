package com.example.minesweeper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class Game {

    private final boolean[][] mineField;
    private final CellState[][] cellStates;

    private int[][] numberField;

    private GameState gameState = GameState.GAME;

    private final int width;
    private final int height;
    private final int mineCount;
    private final Random random = new Random();

    public Game(int width, int height, int mineCount) {
        this.width = width;
        this.height = height;
        this.mineCount = mineCount;
        mineField = generateMineField(width, height, mineCount);
        numberField = getNumberField();
        cellStates = new CellState[height][width];
        for (CellState[] state : cellStates) {
            Arrays.fill(state, CellState.NONE);
        }
    }

    public boolean toggleFlag(int x, int y) {
        if (!checkAction(x, y)) return false;

        if (cellStates[y][x] == CellState.FLAG) {
            return unsetFlag(x, y);
        } else {
            return setFlag(x, y);
        }
    }

    public boolean setFlag(int x, int y) {
        if (!checkAction(x, y)) return false;
        if (cellStates[y][x] == CellState.NONE){
            cellStates[y][x] = CellState.FLAG;
            if (checkWin()) {
                gameState = GameState.WIN;
            }
            return true;
        }
        return false;
    }

    public boolean unsetFlag(int x, int y){
        if (!checkAction(x, y)) return false;
        if (cellStates[y][x] == CellState.FLAG){
            cellStates[y][x] = CellState.NONE;
            return true;
        }
        return false;
    }

    public boolean openCell(int x, int y) {
        if (!checkAction(x, y)) return false;
        if (cellStates[y][x] == CellState.NONE){
            cellStates[y][x] = CellState.OPENED;
            if(mineField[y][x]) {
                gameState = GameState.LOSE;
            }
            else if (checkWin()){
                gameState = GameState.WIN;
            } else if (countNeighborMines(x, y) == 0) {
                openZeroIsland(x, y);
            }
            return true;
        } else if (cellStates[y][x] == CellState.OPENED &&
                checknearbyFlags(x, y) == numberField[y][x]) {
            for (Coordinate coordinate : getNeighbourCoordinates(x, y)) {
                if (cellStates[coordinate.getY()][coordinate.getX()] == CellState.NONE) {
                    cellStates[coordinate.getY()][coordinate.getX()] = CellState.OPENED;
                    openZeroIsland(coordinate.getX(), coordinate.getY());
                }
            }
            if (checkWin()) {
                gameState = GameState.WIN;
            } else if(mineField[y][x]) {
                gameState = GameState.LOSE;
            }
        }
        return false;

    }

    public boolean[][] getMineField() {
        return mineField;
    }

    public CellState[][] getCellStates() {
        return cellStates;
    }

    public int[][] getNumberField() {
        int[][] field = new int[height][width];

        for (int y = 0; y < field.length; y++) {
            for (int x = 0; x < field[y].length; x++) {
                field[y][x] = countNeighborMines(x, y);
            }
        }

        return field;
    }

    private int countNeighborMines(int x, int y) {
        int count = 0;

        for (Coordinate coordinate : getNeighbourCoordinates(x, y)) {
            if (getMineOrFalse(coordinate.getX(), coordinate.getY())) {
                count++;
            }
        }

        return count;
    }

    private boolean getMineOrFalse(int x, int y) {
        if (0 <= x && x < width && 0 <= y && y < height) {
            return mineField[y][x];
        } else return false;
    }

    public GameState getGameState() {
        return gameState;
    }

    public Cell[][] getAvailableData() {
        int[][] numberField = getNumberField();

        Cell[][] field = new Cell[height][width];

        for (int i = 0; i < field.length; i++) {
            for (int j = 0; j < field[i].length; j++) {
                Boolean mine = null;
                Integer number = null;

                if (cellStates[i][j] == CellState.OPENED) {
                    if (mineField[i][j]) {
                        mine = true;
                    } else {
                        mine = false;
                        number = numberField[i][j];
                    }
                }
                field[i][j] = new Cell(cellStates[i][j], mine, number);

            }
        }

        return field;
    }

    private boolean checkWin(){
        for (int y = 0; y < mineField.length; y++) {
            for (int x = 0; x < mineField[0].length; x++) {
                if (cellStates[y][x] != CellState.OPENED && !mineField[y][x]){
                    return false;
                }
                if (cellStates[y][x] != CellState.FLAG && mineField[y][x]){
                    return false;
                }
            }
        }
        return true;
    }

    private boolean checkAction(int x, int y){
        if (gameState != GameState.GAME) return false;
        return 0 <= x && x < width && 0 <= y && y < height;
    }

    private void openZeroIsland(int x, int y){
        if (numberField[y][x] == 0) {
            for (Coordinate coordinate : getNeighbourCoordinates(x, y)) {
                if (cellStates[coordinate.getY()][coordinate.getX()] != CellState.OPENED) {
                    cellStates[coordinate.getY()][coordinate.getX()] = CellState.OPENED;
                    openZeroIsland(coordinate.getX(), coordinate.getY());
                }

            }
        }
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int getMineCount() {
        return mineCount;
    }

    private boolean[][] generateMineField(int width, int height, int mineCount){
        boolean[][] Field = new boolean[height][width];
        for (int i = 0; i < mineCount; i++) {
            int x = random.nextInt(width);
            int y = random.nextInt(height);

            if (!Field[y][x]){
                Field[y][x] = true;
            }
            else {
                i--;
            }
        }
        return Field;
    }


    private ArrayList<Coordinate> getNeighbourCoordinates(int x, int y) {
        int[][] coordinates = {
                {x-1, y-1},
                {x-1, y},
                {x-1, y+1},
                {x, y+1},
                {x+1, y+1},
                {x+1, y},
                {x+1, y-1},
                {x, y-1}
        };
        ArrayList<Coordinate> coordinatesList = new ArrayList<>();
        for (int[] coordinate : coordinates) {
            if (0 <= coordinate[0] && coordinate[0] < width && 0 <= coordinate[1] && coordinate[1] < height){
                coordinatesList.add(new Coordinate(coordinate[0], coordinate[1]));
            }
        }
        return coordinatesList;
    }

    private int checknearbyFlags(int x, int y) {
        int flagCount = 0;

        for (Coordinate coordinate : getNeighbourCoordinates(x, y)) {
            if (cellStates[coordinate.getY()][coordinate.getX()] == CellState.FLAG) {
                flagCount += 1;
            }
        }

        return flagCount;
    }



}
