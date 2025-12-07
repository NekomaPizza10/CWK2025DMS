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

    /**
     * Creates a new BrickMover.
     *
     * @param boardWidth width of the game board
     * @param brickRotator the brick rotator managing current brick
     * @param stateManager the board state manager for collision checks
     */
    public BrickMover(int boardWidth, BrickRotator brickRotator, BoardStateManager stateManager) {
        this.boardWidth = boardWidth;
        this.brickRotator = brickRotator;
        this.stateManager = stateManager;
        this.currentOffset = new Point(boardWidth / 2 - SPAWN_X_OFFSET, DEFAULT_SPAWN_Y);
    }

    /**
     * Gets the current brick offset position.
     * @return Point containing X and Y coordinates
     */
    public Point getCurrentOffset() {return currentOffset;}

    /**
     * Sets the current brick offset position.
     * @param offset new position Point
     */
    public void setCurrentOffset(Point offset) {this.currentOffset = offset;}

    /**
     * Attempts to move the brick down by one cell.
     * @return true if movement succeeded, false if blocked
     */
    public boolean moveBrickDown() {return tryMove(0, 1);}

    /**
     * Attempts to move the brick left by one cell.
     * @return true if movement succeeded, false if blocked
     */
    public boolean moveBrickLeft() {return tryMove(-1, 0);}

    /**
     * Attempts to move the brick right by one cell.
     * @return true if movement succeeded, false if blocked
     */
    public boolean moveBrickRight() {return tryMove(1, 0);}

    private boolean tryMove(int dx, int dy) {
        Point newPosition = new Point(currentOffset);
        newPosition.translate(dx, dy);

        // REMOVED: Early negative X check - let collision detection handle all bounds
        // The brick offset CAN be negative if the brick's filled cells don't start at column 0

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

    /**
     * Resets the offset to default spawn position.
     * @param boardWidth width of the board for centering
     */
    public void resetOffset(int boardWidth) {
        currentOffset = new Point(boardWidth / 2 - SPAWN_X_OFFSET, DEFAULT_SPAWN_Y);
    }

    /**
     * Sets the brick position to a specific spawn point.
     * Used for spawning new bricks or swapping with hold.
     *
     * @param spawnPoint the Point to set as current position
     */
    public void setToSpawnPoint(Point spawnPoint) {
        this.currentOffset = new Point(spawnPoint);
    }

    /**
     * Gets the current X coordinate.
     * @return horizontal position
     */
    public int getX() {return (int) currentOffset.getX();}

    /**
     * Gets the current Y coordinate.
     * @return vertical position
     */
    public int getY() {return (int) currentOffset.getY();}
}