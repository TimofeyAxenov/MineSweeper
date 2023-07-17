package com.example.minesweeper;

public class Cell {
    private CellState state;
    private Boolean mine;
    private Integer number;


    public Cell(CellState state, Boolean mine, Integer number) {
        this.state = state;
        this.mine = mine;
        this.number = number;
    }

    public CellState getState() {
        return state;
    }

    public Boolean getMine() {
        return mine;
    }

    public Integer getNumber() {
        return number;
    }
}
