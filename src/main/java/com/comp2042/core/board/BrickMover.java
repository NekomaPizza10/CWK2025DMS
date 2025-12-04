package com.comp2042.core.board;

import com.comp2042.brick.BrickRotator;
import com.comp2042.core.MatrixOperations;

import java.awt.Point;

/**
 * Handles brick movement including left, right, and down movements.
 * Manages the current position offset of the active brick.
 */
public class BrickMover {

    private static final int SPAWN_X_OFFSET = 2;
    private static final int DEFAULT_SPAWN_Y = -1;

    private final int boardWidth;
    private final BrickRotator brickRotator;
    private final BoardStateManager stateManager;

    private Point currentOffset;

    public BrickMover(int boardWidth, BrickRotator brickRotator, BoardStateManager stateManager) {
        this.boardWidth = boardWidth;
        this.brickRotator = brickRotator;
        this.stateManager = stateManager;
        this.currentOffset = new Point(boardWidth / 2 - SPAWN_X_OFFSET, DEFAULT_SPAWN_Y);
    }

    public Point getCurrentOffset() {return currentOffset;}

    public void setCurrentOffset(Point offset) {this.currentOffset = offset;}

    public boolean moveBrickDown() {return tryMove(0, 1);}

    public boolean moveBrickLeft() {return tryMove(-1, 0);}

    public boolean moveBrickRight() {return tryMove(1, 0);}

    private boolean tryMove(int dx, int dy) {
        Point newPosition = new Point(currentOffset);
        newPosition.translate(dx, dy);

        // Prevent X from going negative
        if (newPosition.getX() < 0) {
            return false;
        }

        if (wouldCollide(newPosition)) {
            return false;
        }
        currentOffset = newPosition;
        return true;
    }

    private boolean wouldCollide(Point position) {
        int[][] boardCopy = stateManager.getBoardMatrixCopy();
        int[][] currentShape = brickRotator.getCurrentShape();

        return MatrixOperations.intersect(
                boardCopy,
                currentShape,
                (int) position.getX(),
                (int) position.getY()
        );
    }

    public void resetOffset(int boardWidth) {
        currentOffset = new Point(boardWidth / 2 - SPAWN_X_OFFSET, DEFAULT_SPAWN_Y);
    }

    public void setToSpawnPoint(Point spawnPoint) {
        this.currentOffset = new Point(spawnPoint);
    }

    public int getX() {return (int) currentOffset.getX();}
    public int getY() {return (int) currentOffset.getY();}
}