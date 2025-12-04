package com.comp2042.ui.logic;

import com.comp2042.model.ViewData;

/**
 * Utility class for calculating ghost/shadow piece positions.
 * Determines where the current piece would land if dropped.
 */
public class ShadowCalculator {

    public int calculateShadowY(ViewData brick, int[][] boardMatrix) {
        // Handle null brick
        if (brick == null) {
            return 0;
        }

        // Handle null/empty board
        if (boardMatrix == null || boardMatrix.length == 0) {
            return brick.getyPosition();
        }

        int x = brick.getxPosition();
        int y = brick.getyPosition();
        int[][] shape = brick.getBrickData();

        // Handle null/empty shape
        if (shape == null || shape.length == 0) {
            return y;
        }
        // Move down until collision
        while (!hasCollision(boardMatrix, shape, x, y + 1)) {
            y++;
        }
        return y;
    }

    public boolean hasCollision(int[][] board, int[][] brick, int x, int y) {
        if (board == null || brick == null || brick.length == 0) {
            return true;
        }

        if (board.length == 0 || board[0].length == 0) {
            return true;
        }

        int boardHeight = board.length;
        int boardWidth = board[0].length;

        // MUST match MatrixOperations.intersect convention exactly
        for (int i = 0; i < brick.length; i++) {
            for (int j = 0; j < brick[i].length; j++) {
                int targetX = x + i;  // Column position
                int targetY = y + j;  // Row position

                // Use brick[j][i] - same transposed access as MatrixOperations
                if (brick[j][i] != 0) {
                    // Check horizontal bounds and bottom bound
                    if (targetX < 0 || targetX >= boardWidth || targetY >= boardHeight) {
                        return true;
                    }

                    // Check collision with existing blocks (only for visible rows)
                    if (targetY >= 0 && board[targetY][targetX] != 0) {
                        return true;
                    }
                }
            }
        }

        return false;
    }
}