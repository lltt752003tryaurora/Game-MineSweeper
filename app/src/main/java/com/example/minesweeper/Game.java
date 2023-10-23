package com.example.minesweeper;

public class Game {
    private Grid grid;
    private boolean isGameOver;

    public Game(int rows, int cols, int mines) {
        grid = new Grid(rows, cols);
        grid.placeMines(mines);
        isGameOver = false;
    }

    public Grid getGrid() {
        return grid;
    }

    public void reveal(int row, int col) {
        if (isGameOver || grid.getCell(row, col).isRevealed() || grid.getCell(row, col).isFlagged()) {
            return;
        }

        grid.getCell(row, col).reveal();

        if (grid.getCell(row, col).isMine()) {
            isGameOver = true;
            revealAllCells();
            return;
        }

        if (grid.getCell(row, col).getNeighboringMines() == 0) {
            for (int i = -1; i <= 1; i++) {
                for (int j = -1; j <= 1; j++) {
                    if (grid.isInBounds(row + i, col + j)) {
                        reveal(row + i, col + j);
                    }
                }
            }
        }
    }

    private void revealAllCells() {
        for (int i = 0; i < grid.rows; i++) {
            for (int j = 0; j < grid.cols; j++) {
                grid.getCell(i, j).reveal();
            }
        }
    }


    public boolean isGameOver() {
        return isGameOver;
    }

    // Additional logic can be added for game win conditions, etc.
}
