package com.comp2042.core.board;

import com.comp2042.brick.Brick;
import com.comp2042.brick.BrickRotator;

import java.awt.Point;

/**
 * Manages the hold piece functionality.
 * Allows players to store one piece for later use.
 */
public class HoldManager {

    private static final int EMPTY_HOLD_SIZE = 4;

    private final int boardWidth;
    private final BrickRotator brickRotator;
    private final BrickSpawner brickSpawner;
    private final BrickMover brickMover;

    private Brick holdBrick;
    private boolean canHold;

    public HoldManager(int boardWidth, BrickRotator brickRotator,
                       BrickSpawner brickSpawner, BrickMover brickMover) {
        this.boardWidth = boardWidth;
        this.brickRotator = brickRotator;
        this.brickSpawner = brickSpawner;
        this.brickMover = brickMover;
        this.holdBrick = null;
        this.canHold = true;
    }

    public boolean holdCurrentBrick() {
        if (!canHold) {
            return false;
        }
        Brick currentBrick = brickRotator.getBrick();
        if (holdBrick == null) {
            holdFirstBrick(currentBrick);
        } else {
            swapWithHoldBrick(currentBrick);
        }

        canHold = false;
        return true;
    }

    // Holds the first brick (when hold slot is empty).
    private void holdFirstBrick(Brick currentBrick) {
        holdBrick = currentBrick;
        brickSpawner.createNewBrick();

        // Update position from spawner
        Point spawnPoint = brickSpawner.calculateSpawnPoint();
        brickMover.setToSpawnPoint(spawnPoint);
    }

    // Swaps the current brick with the held brick.

    private void swapWithHoldBrick(Brick currentBrick) {
        Brick temp = holdBrick;
        holdBrick = currentBrick;
        brickRotator.setBrick(temp);

        // Reset to default spawn position
        Point defaultSpawn = brickSpawner.getDefaultSpawnPoint();
        brickMover.setToSpawnPoint(defaultSpawn);
    }

    public int[][] getHoldBrickData() {
        if (holdBrick == null) {
            return createEmptyMatrix();
        }
        return holdBrick.getShapeMatrix().get(0);
    }

    private int[][] createEmptyMatrix() {
        return new int[EMPTY_HOLD_SIZE][EMPTY_HOLD_SIZE];
    }

    public void setCanHold(boolean canHold) {this.canHold = canHold;}

    public void reset() {
        holdBrick = null;
        canHold = true;
    }
}