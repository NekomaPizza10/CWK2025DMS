package com.comp2042.core.board;

import com.comp2042.brick.BrickRotator;
import com.comp2042.core.MatrixOperations;
import com.comp2042.model.NextShapeInfo;

import java.awt.Point;

/**
 * Handles brick rotation including wall kick logic.
 */
public class BrickRotationHandler {

    private static final int MAX_WALL_KICK = 3;

    private final int boardWidth;
    private final int boardHeight;
    private final BrickRotator brickRotator;
    private final BoardStateManager stateManager;
    private final BrickMover brickMover;

    /**
     * Creates a new BrickRotationHandler.
     *
     * @param boardWidth width of the game board
     * @param boardHeight height of the game board
     * @param brickRotator the brick rotator managing rotations
     * @param stateManager board state for collision checking
     * @param brickMover brick mover for position adjustments
     */
    public BrickRotationHandler(int boardWidth, int boardHeight,
                                BrickRotator brickRotator,
                                BoardStateManager stateManager,
                                BrickMover brickMover) {
        this.boardWidth = boardWidth;
        this.boardHeight = boardHeight;
        this.brickRotator = brickRotator;
        this.stateManager = stateManager;
        this.brickMover = brickMover;
    }

    /**
     * Attempts to rotate the brick counter-clockwise.
     * Tries wall kicks if direct rotation is blocked.
     * @return true if rotation succeeded (possibly with wall kick)
     */
    public boolean rotateLeftBrick() {
        NextShapeInfo nextShape = brickRotator.getNextShape();
        int[][] nextShapeArray = nextShape.getShape();
        Point currentOffset = brickMover.getCurrentOffset();
        // Try basic rotation
        if (tryRotationAt(nextShapeArray, currentOffset, nextShape.getPosition())) {return true;}
        // Try wall kicks to the left
        if (tryWallKicks(nextShapeArray, currentOffset, nextShape.getPosition(), -1)) {return true;}
        // Try wall kicks to the right
        if (tryWallKicks(nextShapeArray, currentOffset, nextShape.getPosition(), 1)) {return true;}
        return false;
    }

    private boolean tryRotationAt(int[][] shape, Point offset, int shapePosition) {
        int x = (int) offset.getX();
        int y = (int) offset.getY();

        if (!isRotationInBounds(shape, x, y)) {return false;}
        int[][] boardCopy = stateManager.getBoardMatrixCopy();
        if (MatrixOperations.intersect(boardCopy, shape, x, y)) {return false;}
        brickRotator.setCurrentShape(shapePosition);
        return true;
    }

    private boolean tryWallKicks(int[][] shape, Point originalOffset,
                                 int shapePosition, int direction) {
        for (int kick = 1; kick <= MAX_WALL_KICK; kick++) {
            Point testOffset = new Point(originalOffset);
            testOffset.translate(direction * kick, 0);

            int x = (int) testOffset.getX();
            int y = (int) testOffset.getY();

            if (!isRotationInBounds(shape, x, y)) {continue;}

            int[][] boardCopy = stateManager.getBoardMatrixCopy();
            if (!MatrixOperations.intersect(boardCopy, shape, x, y)) {
                brickMover.setCurrentOffset(testOffset);
                brickRotator.setCurrentShape(shapePosition);
                return true;
            }
        }

        return false;
    }

    private boolean isRotationInBounds(int[][] shape, int x, int y) {
        for (int row = 0; row < shape.length; row++) {
            for (int col = 0; col < shape[row].length; col++) {
                if (shape[row][col] != 0) {
                    int boardX = x + col;
                    int boardY = y + row;

                    if (isOutOfBounds(boardX, boardY)) {return false;}
                }
            }
        }
        return true;
    }

    private boolean isOutOfBounds(int x, int y) {
        return x < 0 || x >= boardWidth || y >= boardHeight;
    }
}