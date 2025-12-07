package com.comp2042.core.board;

import com.comp2042.model.ClearRow;
import com.comp2042.core.MatrixOperations;

import java.awt.Point;

/**
 * Manages the game board matrix state including merging bricks,
 * clearing rows, and checking game over conditions.
 */
public class BoardStateManager {

    private final int width;
    private final int height;

    private int[][] currentGameMatrix;
    private int piecesPlaced;
    private int linesCleared;

    /**
     * Creates a new BoardStateManager with specified dimensions.
     *
     * @param width board width in cells
     * @param height board height in cells
     */
    public BoardStateManager(int width, int height) {
        this.width = width;
        this.height = height;
        this.currentGameMatrix = new int[height][width];
        this.piecesPlaced = 0;
        this.linesCleared = 0;
    }

    /**
     * Gets direct reference to the board matrix.
     * Use with caution - prefer getBoardMatrixCopy() for collision testing.
     * @return the current board matrix
     */
    public int[][] getBoardMatrix() {return currentGameMatrix;}

    /**
     * Gets a defensive copy of the board matrix for collision testing.
     * @return copy of the current board state
     */
    // For collision testing.
    public int[][] getBoardMatrixCopy() {return MatrixOperations.copy(currentGameMatrix);}

    /**
     * Gets the number of pieces placed on the board.
     * @return pieces placed count
     */
    public int getPiecesPlaced() {return piecesPlaced;}

    /**
     * Gets the total number of lines cleared.
     * @return lines cleared count
     */
    public int getLinesCleared() {return linesCleared;}

    /**
     * Merges the current brick shape into the board at the given position.
     * Increments pieces placed counter.
     *
     * @param brickShape the brick shape matrix to merge
     * @param offset the position to merge at (X, Y)
     */
    public void mergeBrickToBackground(int[][] brickShape, Point offset) {
        currentGameMatrix = MatrixOperations.merge(
                currentGameMatrix,
                brickShape,
                (int) offset.getX(),
                (int) offset.getY()
        );
        piecesPlaced++;
    }

    /**
     * Checks for and removes complete rows.
     * Updates lines cleared counter.
     * @return ClearRow with lines removed, new matrix, and score bonus
     */
    public ClearRow clearRows() {
        ClearRow clearRow = MatrixOperations.checkRemoving(currentGameMatrix);
        currentGameMatrix = clearRow.getNewMatrix();
        linesCleared += clearRow.getLinesRemoved();
        return clearRow;
    }

    /**
     * Checks if the game is over by examining the top row.
     * @return true if any blocks exist in the top row
     */
    public boolean checkGameOver() {
        for (int col = 0; col < width; col++) {
            if (currentGameMatrix[0][col] != 0) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks if the stack is near the top (within top 2 rows).
     * Used for emergency spawn position adjustment.
     * @return true if blocks detected in top 2 rows
     */
    public boolean isStackNearTop() {
        for (int row = 0; row <= 1; row++) {
            for (int col = 0; col < width; col++) {
                if (currentGameMatrix[row][col] != 0) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Checks if a shape at given position would intersect with existing blocks.
     *
     * @param shape the shape to check
     * @param x horizontal position
     * @param y vertical position
     * @return true if intersection detected
     */
    public boolean checkIntersection(int[][] shape, int x, int y) {
        return MatrixOperations.intersect(
                MatrixOperations.copy(currentGameMatrix),
                shape,
                x,
                y
        );
    }

    public void reset() {
        currentGameMatrix = new int[height][width];
        piecesPlaced = 0;
        linesCleared = 0;
    }

    /**
     * Gets the board width.
     *
     * @return width in cells
     */
    public int getWidth() {return width;}

    /**
     * Gets the board height.
     *
     * @return height in cells
     */
    public int getHeight() {return height;}
}