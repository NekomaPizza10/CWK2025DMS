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

    public BoardStateManager(int width, int height) {
        this.width = width;
        this.height = height;
        this.currentGameMatrix = new int[height][width];
        this.piecesPlaced = 0;
        this.linesCleared = 0;
    }

    public int[][] getBoardMatrix() {return currentGameMatrix;}

    // For collision testing.
    public int[][] getBoardMatrixCopy() {return MatrixOperations.copy(currentGameMatrix);}

    public int getPiecesPlaced() {return piecesPlaced;}

    public int getLinesCleared() {return linesCleared;}

     //Merges the current brick shape into the board at the given position.
    public void mergeBrickToBackground(int[][] brickShape, Point offset) {
        currentGameMatrix = MatrixOperations.merge(
                currentGameMatrix,
                brickShape,
                (int) offset.getX(),
                (int) offset.getY()
        );
        piecesPlaced++;
    }

    public ClearRow clearRows() {
        ClearRow clearRow = MatrixOperations.checkRemoving(currentGameMatrix);
        currentGameMatrix = clearRow.getNewMatrix();
        linesCleared += clearRow.getLinesRemoved();
        return clearRow;
    }

    public boolean checkGameOver() {
        for (int col = 0; col < width; col++) {
            if (currentGameMatrix[0][col] != 0) {
                return true;
            }
        }
        return false;
    }

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

    public int getWidth() {return width;}

    public int getHeight() {return height;}
}