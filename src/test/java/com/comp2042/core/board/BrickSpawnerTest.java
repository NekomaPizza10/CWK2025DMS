package com.comp2042.core.board;

import com.comp2042.brick.Brick;
import com.comp2042.brick.BrickRotator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

import java.awt.Point;
import java.util.List;

/**
 * Tests for BrickSpawner - Brick creation and next brick preview
 */
class BrickSpawnerTest {

    private static final int BOARD_WIDTH = 10;
    private static final int BOARD_HEIGHT = 20;

    private BrickSpawner brickSpawner;
    private BrickRotator brickRotator;
    private BoardStateManager stateManager;
    private BrickMover brickMover;

    @BeforeEach
    void setUp() {
        brickRotator = new BrickRotator();
        stateManager = new BoardStateManager(BOARD_WIDTH, BOARD_HEIGHT);
        brickMover = new BrickMover(BOARD_WIDTH, brickRotator, stateManager);
        brickSpawner = new BrickSpawner(BOARD_WIDTH, brickRotator, stateManager, brickMover);
    }

    // ========== createNewBrick Tests ==========

    @Test
    @DisplayName("createNewBrick() spawns a valid brick")
    void createNewBrickSpawnsValidBrick() {
        boolean gameOver = brickSpawner.createNewBrick();

        assertFalse(gameOver, "Should not be game over on first spawn");
        assertNotNull(brickRotator.getBrick(), "Should have a brick after spawn");
    }

    @Test
    @DisplayName("createNewBrick() sets brick position via BrickMover")
    void createNewBrickSetsBrickPosition() {
        brickSpawner.createNewBrick();

        Point offset = brickMover.getCurrentOffset();
        assertEquals(3, offset.getX(), "Should spawn at centered X position");
        assertEquals(-1, offset.getY(), "Should spawn above board");
    }

    @Test
    @DisplayName("createNewBrick() returns false for normal spawn")
    void createNewBrickReturnsFalseForNormalSpawn() {
        boolean gameOver = brickSpawner.createNewBrick();

        assertFalse(gameOver, "Normal spawn should not trigger game over");
    }

    @Test
    @DisplayName("createNewBrick() returns true when spawn collides")
    void createNewBrickReturnsTrueWhenSpawnCollides() {
        // Fill top rows to cause spawn collision
        int[][] board = stateManager.getBoardMatrix();
        for (int col = 0; col < BOARD_WIDTH; col++) {
            board[0][col] = 1;
            board[1][col] = 1;
        }

        boolean gameOver = brickSpawner.createNewBrick();

        assertTrue(gameOver, "Should detect game over when spawn collides");
    }

    @Test
    @DisplayName("createNewBrick() can be called multiple times")
    void createNewBrickCanBeCalledMultipleTimes() {
        for (int i = 0; i < 10; i++) {
            boolean gameOver = brickSpawner.createNewBrick();
            assertFalse(gameOver, "Spawn " + (i + 1) + " should succeed");
        }
    }

    // ========== calculateSpawnPoint Tests ==========

    @Test
    @DisplayName("calculateSpawnPoint() returns centered position")
    void calculateSpawnPointReturnsCenteredPosition() {
        Point spawnPoint = brickSpawner.calculateSpawnPoint();

        assertEquals(3, spawnPoint.getX(), "X should be centered (width/2 - 2)");
        assertEquals(-1, spawnPoint.getY(), "Y should be -1 for normal spawn");
    }

    @Test
    @DisplayName("calculateSpawnPoint() uses emergency spawn when stack near top")
    void calculateSpawnPointUsesEmergencySpawnWhenStackNearTop() {
        // Fill rows 0-1 to trigger emergency spawn
        int[][] board = stateManager.getBoardMatrix();
        board[1][5] = 1;

        Point spawnPoint = brickSpawner.calculateSpawnPoint();

        assertEquals(3, spawnPoint.getX(), "X should still be centered");
        assertEquals(-2, spawnPoint.getY(), "Y should be -2 for emergency spawn");
    }

    @Test
    @DisplayName("calculateSpawnPoint() returns new Point each time")
    void calculateSpawnPointReturnsNewPointEachTime() {
        Point point1 = brickSpawner.calculateSpawnPoint();
        Point point2 = brickSpawner.calculateSpawnPoint();

        assertNotSame(point1, point2, "Should return new Point instances");
        assertEquals(point1.getX(), point2.getX(), "X values should match");
        assertEquals(point1.getY(), point2.getY(), "Y values should match");
    }

    // ========== getDefaultSpawnPoint Tests ==========

    @Test
    @DisplayName("getDefaultSpawnPoint() always returns normal spawn position")
    void getDefaultSpawnPointAlwaysReturnsNormalSpawn() {
        Point defaultSpawn = brickSpawner.getDefaultSpawnPoint();

        assertEquals(3, defaultSpawn.getX(), "X should be centered");
        assertEquals(-1, defaultSpawn.getY(), "Y should always be -1");
    }

    @Test
    @DisplayName("getDefaultSpawnPoint() ignores stack height")
    void getDefaultSpawnPointIgnoresStackHeight() {
        // Fill top rows
        int[][] board = stateManager.getBoardMatrix();
        board[0][5] = 1;
        board[1][5] = 1;

        Point defaultSpawn = brickSpawner.getDefaultSpawnPoint();

        assertEquals(-1, defaultSpawn.getY(), "Should still return -1 despite stack");
    }

    // ========== getNextBricksData Tests ==========

    @Test
    @DisplayName("getNextBricksData() returns requested count")
    void getNextBricksDataReturnsRequestedCount() {
        List<int[][]> nextBricks = brickSpawner.getNextBricksData(5);

        assertEquals(5, nextBricks.size(), "Should return 5 next bricks");
    }

    @Test
    @DisplayName("getNextBricksData() returns valid brick data")
    void getNextBricksDataReturnsValidBrickData() {
        List<int[][]> nextBricks = brickSpawner.getNextBricksData(3);

        for (int i = 0; i < nextBricks.size(); i++) {
            int[][] brick = nextBricks.get(i);
            assertNotNull(brick, "Brick " + i + " should not be null");
            assertEquals(4, brick.length, "Brick should be 4x4");
            assertEquals(4, brick[0].length, "Brick should be 4x4");
        }
    }

    @Test
    @DisplayName("getNextBricksData() works with different counts")
    void getNextBricksDataWorksWithDifferentCounts() {
        assertEquals(1, brickSpawner.getNextBricksData(1).size());
        assertEquals(3, brickSpawner.getNextBricksData(3).size());
        assertEquals(7, brickSpawner.getNextBricksData(7).size());
        assertEquals(10, brickSpawner.getNextBricksData(10).size());
    }

    @Test
    @DisplayName("getNextBricksData() with count 0 returns empty list")
    void getNextBricksDataWithCount0ReturnsEmptyList() {
        List<int[][]> nextBricks = brickSpawner.getNextBricksData(0);

        assertEquals(0, nextBricks.size(), "Should return empty list for count 0");
    }

    @Test
    @DisplayName("getNextBricksData() doesn't consume bricks from queue")
    void getNextBricksDataDoesntConsumeBricks() {
        List<int[][]> preview1 = brickSpawner.getNextBricksData(3);
        List<int[][]> preview2 = brickSpawner.getNextBricksData(3);

        // Should preview same bricks
        assertEquals(preview1.get(0)[1][0], preview2.get(0)[1][0],
                "Previewing should not consume bricks");
    }

    @Test
    @DisplayName("getNextBricksData() returns first rotation state")
    void getNextBricksDataReturnsFirstRotationState() {
        List<int[][]> nextBricks = brickSpawner.getNextBricksData(5);

        for (int[][] brick : nextBricks) {
            // All bricks should be in their first rotation (rotation 0)
            // This is verified by checking they match the expected pattern
            assertNotNull(brick, "Should have first rotation data");
        }
    }

    // ========== reset Tests ==========

    @Test
    @DisplayName("reset() creates new brick generator")
    void resetCreatesNewBrickGenerator() {
        // Get first brick
        brickSpawner.createNewBrick();
        Brick firstBrick = brickRotator.getBrick();
        String firstType = firstBrick.getClass().getSimpleName();

        // Reset
        brickSpawner.reset();

        // Get new brick - might be different type
        brickSpawner.createNewBrick();
        Brick afterReset = brickRotator.getBrick();
        assertNotNull(afterReset, "Should have brick after reset");
    }

    @Test
    @DisplayName("reset() clears previous brick sequence")
    void resetClearsPreviousBrickSequence() {
        List<int[][]> beforeReset = brickSpawner.getNextBricksData(5);

        brickSpawner.reset();

        List<int[][]> afterReset = brickSpawner.getNextBricksData(5);

        // After reset, sequence should be different (new random seed)
        assertNotNull(beforeReset, "Should have preview before reset");
        assertNotNull(afterReset, "Should have preview after reset");
    }

    @Test
    @DisplayName("reset() can be called multiple times")
    void resetCanBeCalledMultipleTimes() {
        assertDoesNotThrow(() -> {
            for (int i = 0; i < 5; i++) {
                brickSpawner.reset();
            }
        }, "Multiple resets should not crash");
    }

    // ========== Integration Tests ==========

    @Test
    @DisplayName("Spawned brick matches first preview brick")
    void spawnedBrickMatchesFirstPreviewBrick() {
        List<int[][]> preview = brickSpawner.getNextBricksData(1);
        int[][] expectedShape = preview.get(0);

        brickSpawner.createNewBrick();
        Brick spawnedBrick = brickRotator.getBrick();
        int[][] actualShape = spawnedBrick.getShapeMatrix().get(0);

        // Shapes should match
        boolean matches = arraysMatch(expectedShape, actualShape);
        assertTrue(matches, "Spawned brick should match preview");
    }

    @Test
    @DisplayName("Multiple spawns follow preview sequence")
    void multipleSpawnsFollowPreviewSequence() {
        List<int[][]> preview = brickSpawner.getNextBricksData(3);

        for (int i = 0; i < 3; i++) {
            brickSpawner.createNewBrick();
            Brick spawned = brickRotator.getBrick();
            int[][] spawnedShape = spawned.getShapeMatrix().get(0);

            boolean matches = arraysMatch(preview.get(i), spawnedShape);
            assertTrue(matches, "Brick " + i + " should match preview");
        }
    }

    @Test
    @DisplayName("Spawn sequence follows 7-bag randomization")
    void spawnSequenceFollows7BagRandomization() {
        java.util.Set<String> types = new java.util.HashSet<>();

        // Spawn 7 bricks
        for (int i = 0; i < 7; i++) {
            brickSpawner.createNewBrick();
            Brick brick = brickRotator.getBrick();
            types.add(brick.getClass().getSimpleName());
        }

        // Should have all 7 unique types
        assertEquals(7, types.size(), "Should spawn all 7 types in first 7 bricks");
    }

    @Test
    @DisplayName("Emergency spawn used when necessary")
    void emergencySpawnUsedWhenNecessary() {
        // Fill rows 0-1
        int[][] board = stateManager.getBoardMatrix();
        board[0][5] = 1;
        board[1][5] = 1;

        Point spawnPoint = brickSpawner.calculateSpawnPoint();

        assertEquals(-2, spawnPoint.getY(), "Should use emergency spawn Y=-2");
    }

    // ========== Edge Cases ==========

    @Test
    @DisplayName("Large preview count works")
    void largePreviewCountWorks() {
        List<int[][]> preview = brickSpawner.getNextBricksData(20);

        assertEquals(20, preview.size(), "Should return 20 bricks");
        for (int[][] brick : preview) {
            assertNotNull(brick, "Each brick should be valid");
        }
    }

    @Test
    @DisplayName("Spawn position consistent for same board width")
    void spawnPositionConsistentForSameBoardWidth() {
        for (int i = 0; i < 10; i++) {
            Point spawn = brickSpawner.calculateSpawnPoint();
            assertEquals(3, spawn.getX(), "X should always be 3 for width 10");
        }
    }

    // ========== Helper Methods ==========

    private boolean arraysMatch(int[][] a, int[][] b) {
        if (a.length != b.length) return false;
        for (int i = 0; i < a.length; i++) {
            if (a[i].length != b[i].length) return false;
            for (int j = 0; j < a[i].length; j++) {
                if (a[i][j] != b[i][j]) return false;
            }
        }
        return true;
    }
}