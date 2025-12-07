package com.comp2042.core;

import com.comp2042.model.ClearRow;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Utility class providing matrix operations for Tetris game mechanics.
 * This class contains static methods for collision detection, matrix manipulation,
 * line clearing, and board state management.
 *
 * All methods are static and the class cannot be instantiated.
 *
 */
public class MatrixOperations {

    /**
     * Private constructor to prevent instantiation.
     */
    private MatrixOperations() {
    }

    /**
     * Checks if a brick intersects with the board boundaries or existing blocks.
     * Uses transposed coordinate system where rows map to Y and columns map to X.
     *
     * @param matrix the game board matrix
     * @param brick the brick shape matrix to check
     * @param x the horizontal position (column)
     * @param y the vertical position (row)
     * @return true if collision detected, false otherwise
     */
    public static boolean intersect(final int[][] matrix, final int[][] brick, int x, int y) {
        for (int row = 0; row < brick.length; row++) {
            for (int col = 0; col < brick[row].length; col++) {
                if (brick[row][col] != 0) {
                    int targetX = x + col;  // Column → X (horizontal)
                    int targetY = y + row;  // Row → Y (vertical)

                    // Cells above the board (negative Y) only check horizontal bounds
                    if (targetY < 0) {
                        if (targetX < 0 || targetX >= matrix[0].length) {
                            return true;
                        }
                        continue;
                    }
                    if (checkOutOfBound(matrix, targetX, targetY) || matrix[targetY][targetX] != 0) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private static boolean checkOutOfBound(int[][] matrix, int targetX, int targetY) {
        return !(targetX >= 0 && targetY >= 0 && targetY < matrix.length && targetX < matrix[targetY].length);
    }

    /**
     * Creates a deep copy of a 2D integer array.
     *
     * @param original the array to copy
     * @return a new array with copied values, or null if input is null
     */
    public static int[][] copy(int[][] original) {
        if (original == null) {
            return null;
        }
        int[][] myInt = new int[original.length][];
        for (int i = 0; i < original.length; i++) {
            int[] aMatrix = original[i];
            int aLength = aMatrix.length;
            myInt[i] = new int[aLength];
            System.arraycopy(aMatrix, 0, myInt[i], 0, aLength);
        }
        return myInt;
    }

    /**
     * Merges a brick into the board matrix at the specified position.
     * Only non-zero values from the brick are copied.
     *
     * @param filledFields the current board state
     * @param brick the brick to merge
     * @param x the horizontal position (column)
     * @param y the vertical position (row)
     * @return a new matrix with the brick merged
     */
    public static int[][] merge(int[][] filledFields, int[][] brick, int x, int y) {
        int[][] copy = copy(filledFields);
        for (int row = 0; row < brick.length; row++) {
            for (int col = 0; col < brick[row].length; col++) {
                if (brick[row][col] != 0) {
                    int targetX = x + col;  // Column → X (horizontal)
                    int targetY = y + row;  // Row → Y (vertical)

                    if (targetY >= 0
                            && targetX >= 0
                            && targetY < copy.length
                            && targetX < copy[0].length) {
                        copy[targetY][targetX] = brick[row][col];
                    }
                }
            }
        }
        return copy;
    }

    /**
     * Checks for and removes complete rows from the board.
     * Complete rows are removed and remaining rows drop down.
     *
     * @param matrix the game board to check
     * @return ClearRow object containing lines removed, new matrix, and score bonus
     */
    public static ClearRow checkRemoving(final int[][] matrix) {
        int[][] tmp = new int[matrix.length][matrix[0].length];
        Deque<int[]> newRows = new ArrayDeque<>();
        List<Integer> clearedRows = new ArrayList<>();

        for (int i = 0; i < matrix.length; i++) {
            int[] tmpRow = new int[matrix[i].length];
            boolean rowToClear = true;
            for (int j = 0; j < matrix[0].length; j++) {
                if (matrix[i][j] == 0) {
                    rowToClear = false;
                }
                tmpRow[j] = matrix[i][j];
            }
            if (rowToClear) {
                clearedRows.add(i);
            } else {
                newRows.add(tmpRow);
            }
        }
        for (int i = matrix.length - 1; i >= 0; i--) {
            int[] row = newRows.pollLast();
            if (row != null) {
                tmp[i] = row;
            } else {
                break;
            }
        }
        int scoreBonus = 50 * clearedRows.size() * clearedRows.size();
        return new ClearRow(clearedRows.size(), tmp, scoreBonus);
    }

    /**
     * Creates a deep copy of a list of 2D integer arrays.
     *
     * @param list the list to copy
     * @return a new list with deep-copied arrays
     */
    public static List<int[][]> deepCopyList(List<int[][]> list) {
        return list.stream().map(MatrixOperations::copy).collect(Collectors.toList());
    }
}