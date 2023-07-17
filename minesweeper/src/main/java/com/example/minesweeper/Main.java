package com.example.minesweeper;

public class Main {
    public static void main(String[] args) {
        Game game = new Game(4, 4, 10);

        printAvailable(game);

        game.setFlag(0, 0);
        game.openCell(1, 0);
        game.openCell(2, 0);

        printAvailable(game);
    }

    private static void printAvailable(Game game){
        System.out.println(game.getGameState());

        Cell[][] field = game.getAvailableData();
        for (Cell[] row : field) {
            for (Cell cell : row) {
                if(cell.getState() == CellState.NONE) {
                    System.out.print(".");
                } else if (cell.getState() == CellState.FLAG) {
                    System.out.print("F");
                } else {
                    if (cell.getMine() == true) {
                        System.out.print("X");
                    } else {
                        System.out.print(cell.getNumber());
                    }
                }
            }
            System.out.println();
        }
    }
    private static void print(Game game){
        System.out.println(game.getGameState());

       boolean[][] mineField = game.getMineField();
        for (boolean[] row : mineField) {
            for (boolean cell : row) {
                String printableValue;
                if (cell) {
                    printableValue = "X";
                }
                else {
                    printableValue = ".";
                }
                System.out.print(printableValue + " ");
            }
            System.out.println();
        }

        System.out.println();

        int[][] numberField = game.getNumberField();
        for (int[] row : numberField) {
            for (int cell : row) {
                if (cell > 0) {
                    System.out.print(cell + " ");
                } else {
                    System.out.print(". ");
                }
            }
            System.out.println();
        }

        System.out.println();

        CellState[][] cellStates = game.getCellStates();
        for (CellState[] row : cellStates) {
            for (CellState cell : row) {
                System.out.print(cell + "\t");
            }
            System.out.println();
        }

        System.out.println("------");

    }
}