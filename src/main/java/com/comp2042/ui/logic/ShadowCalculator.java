package com.comp2042.ui.logic;

import com.comp2042.model.ViewData;

/**
 * Utility class for calculating ghost/shadow piece positions.
 * Determines where the current piece would land if dropped.
 */
public class ShadowCalculator {

    // Calculates the Y position where the shadow piece should be rendered.
    public int calculateShadowY(ViewData brick, int[][] boardMatrix) {
        if (brick == null || boardMatrix == null) {
            return 0;
        }

        int x = brick.getxPosition();
        int y = brick.getyPosition();
        int[][] shape = brick.getBrickData();

        if (shape == null) {
            return y;
        }
        // Move down until collision
        while (!hasCollision(boardMatrix, shape, x, y + 1)) {
            y++;
        }
        return y;
    }

    public boolean hasCollision(int[][] board, int[][] brick, int x, int y) {
        if (board == null || brick == null) {
            return true;
        }

        int boardHeight = board.length;
        int boardWidth = board[0].length;

        for (int row = 0; row < brick.length; row++) {
            for (int col = 0; col < brick[row].length; col++) {
                if (brick[row][col] != 0) {
                    int boardRow = y + row;
                    int boardCol = x + col;

                    // Check bounds
                    if (isOutOfBounds(boardRow, boardCol, boardHeight, boardWidth)) {
                        return true;
                    }

                    // Check collision with existing blocks (only for visible rows)
                    if (boardRow >= 0 && board[boardRow][boardCol] != 0) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    private boolean isOutOfBounds(int row, int col, int height, int width) {
        return row >= height || col < 0 || col >= width;
    }

}