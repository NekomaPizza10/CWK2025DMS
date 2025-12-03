package com.comp2042.core.board;

import com.comp2042.brick.Brick;
import com.comp2042.brick.BrickGenerator;
import com.comp2042.brick.BrickRotator;
import com.comp2042.brick.RandomBrickGenerator;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

/**
 * Handles brick creation and next brick preview management.
 */
public class BrickSpawner {

    private static final int SPAWN_X_OFFSET = 2;
    private static final int NORMAL_SPAWN_Y = -1;
    private static final int EMERGENCY_SPAWN_Y = -2;

    private final int boardWidth;
    private final BrickRotator brickRotator;
    private final BoardStateManager stateManager;
    private final BrickMover brickMover;  // Added reference to BrickMover

    private BrickGenerator brickGenerator;

    public BrickSpawner(int boardWidth, BrickRotator brickRotator,
                        BoardStateManager stateManager, BrickMover brickMover) {
        this.boardWidth = boardWidth;
        this.brickRotator = brickRotator;
        this.stateManager = stateManager;
        this.brickMover = brickMover;  // Store reference
        this.brickGenerator = new RandomBrickGenerator();
    }

    public boolean createNewBrick() {
        Brick currentBrick = brickGenerator.getBrick();
        brickRotator.setBrick(currentBrick);

        Point spawnPoint = calculateSpawnPoint();
        brickMover.setToSpawnPoint(spawnPoint);

        // Check if the new brick collides when moved down one step
        Point testPoint = new Point(spawnPoint);
        testPoint.translate(0, 1);

        return stateManager.checkIntersection(
                brickRotator.getCurrentShape(),
                (int) testPoint.getX(),
                (int) testPoint.getY()
        );
    }

    public Point calculateSpawnPoint() {
        int spawnX = boardWidth / 2 - SPAWN_X_OFFSET;
        int spawnY = stateManager.isStackNearTop() ? EMERGENCY_SPAWN_Y : NORMAL_SPAWN_Y;
        return new Point(spawnX, spawnY);
    }

    public Point getDefaultSpawnPoint() {
        return new Point(boardWidth / 2 - SPAWN_X_OFFSET, NORMAL_SPAWN_Y);
    }

    public List<int[][]> getNextBricksData(int count) {
        List<int[][]> nextBricks = new ArrayList<>();
        List<Brick> bricks = brickGenerator.getNextBricks(count);

        for (Brick brick : bricks) {
            if (isValidBrick(brick)) {
                nextBricks.add(brick.getShapeMatrix().get(0));
            }
        }
        return nextBricks;
    }

    private boolean isValidBrick(Brick brick) {
        return brick != null
                && brick.getShapeMatrix() != null
                && !brick.getShapeMatrix().isEmpty();
    }

    public void reset() {brickGenerator = new RandomBrickGenerator();}
}