package com.example.minesweeper;

public class Grid {
    int rows;
    int cols;
    private Cell[][] cells;

    public Grid(int rows, int cols) {
        this.rows = rows;
        this.cols = cols;
        cells = new Cell[rows][cols];

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                cells[i][j] = new Cell();
            }
        }
    }

    public Cell getCell(int row, int col) {
        return cells[row][col];
    }

    public void placeMines(int count) {
        int placed = 0;
        while (placed < count) {
            int row = (int) (Math.random() * rows);
            int col = (int) (Math.random() * cols);

            if (!cells[row][col].isMine()) {
                cells[row][col].setMine(true);
                updateNeighboringCount(row, col);
                placed++;
            }
        }
    }

    private void updateNeighboringCount(int row, int col) {
        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                if (isInBounds(row + i, col + j) && !(i == 0 && j == 0)) {
                    cells[row + i][col + j].incrementNeighboringMines();
                }
            }
        }
    }

    boolean isInBounds(int row, int col) {
        return row >= 0 && col >= 0 && row < rows && col < cols;
    }
}
