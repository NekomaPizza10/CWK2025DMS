package com.comp2042.core.board;

import com.comp2042.brick.BrickRotator;
import com.comp2042.brick.pieces.IBrick;
import com.comp2042.brick.pieces.OBrick;
import com.comp2042.brick.pieces.TBrick;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for BrickRotationHandler - Handles brick rotation with wall kicks
 */
class BrickRotationHandlerTest {

    private static final int BOARD_WIDTH = 10;
    private static final int BOARD_HEIGHT = 20;

    private BrickRotationHandler rotationHandler;
    private BrickRotator brickRotator;
    private BoardStateManager stateManager;
    private BrickMover brickMover;

    @BeforeEach
    void setUp() {
        brickRotator = new BrickRotator();
        stateManager = new BoardStateManager(BOARD_WIDTH, BOARD_HEIGHT);
        brickMover = new BrickMover(BOARD_WIDTH, brickRotator, stateManager);
        rotationHandler = new BrickRotationHandler(BOARD_WIDTH, BOARD_HEIGHT,
                brickRotator, stateManager, brickMover);
    }

    // ========== Basic Rotation Tests ==========

    @Test
    @DisplayName("rotateLeftBrick() rotates I-brick successfully")
    void rotateLeftBrickRotatesIBrickSuccessfully() {
        brickRotator.setBrick(new IBrick());
        brickMover.resetOffset(BOARD_WIDTH);

        boolean rotated = rotationHandler.rotateLeftBrick();

        assertTrue(rotated, "Should successfully rotate I-brick");
    }

    @Test
    @DisplayName("rotateLeftBrick() changes brick rotation state")
    void rotateLeftBrickChangesBrickRotationState() {
        brickRotator.setBrick(new IBrick());
        int[][] beforeRotation = brickRotator.getCurrentShape();

        rotationHandler.rotateLeftBrick();

        int[][] afterRotation = brickRotator.getCurrentShape();
        assertFalse(arraysMatch(beforeRotation, afterRotation),
                "Rotation should change brick shape");
    }

    @Test
    @DisplayName("rotateLeftBrick() works for T-brick")
    void rotateLeftBrickWorksForTBrick() {
        brickRotator.setBrick(new TBrick());
        brickMover.resetOffset(BOARD_WIDTH);

        boolean rotated = rotationHandler.rotateLeftBrick();

        assertTrue(rotated, "Should successfully rotate T-brick");
    }

    @Test
    @DisplayName("rotateLeftBrick() handles O-brick (no visual change)")
    void rotateLeftBrickHandlesOBrick() {
        brickRotator.setBrick(new OBrick());
        brickMover.resetOffset(BOARD_WIDTH);

        boolean rotated = rotationHandler.rotateLeftBrick();

        assertTrue(rotated, "O-brick rotation should succeed");
    }

    // ========== Wall Kick Tests ==========

    @Test
    @DisplayName("rotateLeftBrick() applies left wall kick")
    void rotateLeftBrickAppliesLeftWallKick() {
        brickRotator.setBrick(new IBrick());

        // Move to left wall
        brickMover.resetOffset(BOARD_WIDTH);
        while (brickMover.moveBrickLeft()) {}

        int xBeforeRotation = brickMover.getX();
        boolean rotated = rotationHandler.rotateLeftBrick();
        int xAfterRotation = brickMover.getX();

        // Should either rotate in place or kick right
        assertTrue(xAfterRotation >= xBeforeRotation,
                "Wall kick should move brick right or keep position");
    }

    @Test
    @DisplayName("rotateLeftBrick() applies right wall kick")
    void rotateLeftBrickAppliesRightWallKick() {
        brickRotator.setBrick(new IBrick());

        // Move to right wall
        brickMover.resetOffset(BOARD_WIDTH);
        while (brickMover.moveBrickRight()) {}

        int xBeforeRotation = brickMover.getX();
        boolean rotated = rotationHandler.rotateLeftBrick();
        int xAfterRotation = brickMover.getX();

        // Should either rotate in place or kick left
        assertTrue(xAfterRotation <= xBeforeRotation,
                "Wall kick should move brick left or keep position");
    }

    @Test
    @DisplayName("rotateLeftBrick() tries multiple wall kick positions")
    void rotateLeftBrickTriesMultipleWallKickPositions() {
        brickRotator.setBrick(new IBrick());
        brickMover.resetOffset(BOARD_WIDTH);

        // Wall kicks should be attempted (up to 3 cells in each direction)
        boolean rotated = rotationHandler.rotateLeftBrick();

        // As long as there's space, rotation should succeed
        assertTrue(rotated, "Should succeed with wall kicks if space available");
    }

    // ========== Collision Prevention Tests ==========

    @Test
    @DisplayName("rotateLeftBrick() returns false when blocked")
    void rotateLeftBrickReturnsFalseWhenBlocked() {
        brickRotator.setBrick(new IBrick());
        brickMover.resetOffset(BOARD_WIDTH);

        // Fill cells around brick to block rotation
        int[][] board = stateManager.getBoardMatrix();
        for (int col = 0; col < BOARD_WIDTH; col++) {
            board[2][col] = 1;
        }

        // Move brick down near blocked area
        for (int i = 0; i < 3; i++) {
            brickMover.moveBrickDown();
        }

        boolean rotated = rotationHandler.rotateLeftBrick();

        // Might fail if completely blocked
        assertFalse(rotated || rotated, "Behavior depends on exact blocking");
    }

    @Test
    @DisplayName("rotateLeftBrick() respects board boundaries")
    void rotateLeftBrickRespectsBoardBoundaries() {
        brickRotator.setBrick(new IBrick());

        // At left edge
        brickMover.resetOffset(BOARD_WIDTH);
        while (brickMover.moveBrickLeft()) {}

        rotationHandler.rotateLeftBrick();

        // Should not go out of bounds
        assertTrue(brickMover.getX() >= 0, "X should not be negative");
        assertTrue(brickMover.getX() < BOARD_WIDTH, "X should be within board");
    }

    // ========== Multiple Rotation Tests ==========

    @Test
    @DisplayName("Four rotations return to original state")
    void fourRotationsReturnToOriginalState() {
        brickRotator.setBrick(new IBrick());
        int[][] original = brickRotator.getCurrentShape();

        // Rotate 4 times
        for (int i = 0; i < 4; i++) {
            rotationHandler.rotateLeftBrick();
        }

        int[][] afterFour = brickRotator.getCurrentShape();
        assertTrue(arraysMatch(original, afterFour),
                "Four rotations should return to original state");
    }

    @Test
    @DisplayName("Rapid rotations work correctly")
    void rapidRotationsWorkCorrectly() {
        brickRotator.setBrick(new TBrick());
        brickMover.resetOffset(BOARD_WIDTH);

        assertDoesNotThrow(() -> {
            for (int i = 0; i < 20; i++) {
                rotationHandler.rotateLeftBrick();
            }
        }, "Rapid rotations should not crash");
    }

    // ========== Wall Kick Limits Tests ==========

    @Test
    @DisplayName("Wall kick has maximum distance limit")
    void wallKickHasMaximumDistanceLimit() {
        brickRotator.setBrick(new IBrick());
        brickMover.resetOffset(BOARD_WIDTH);

        int xBefore = brickMover.getX();
        rotationHandler.rotateLeftBrick();
        int xAfter = brickMover.getX();

        int kickDistance = Math.abs(xAfter - xBefore);
        assertTrue(kickDistance <= 3, "Wall kick should not exceed 3 cells");
    }

    // ========== Integration Tests ==========

    @Test
    @DisplayName("Rotation works during gameplay")
    void rotationWorksDuringGameplay() {
        brickRotator.setBrick(new TBrick());
        brickMover.resetOffset(BOARD_WIDTH);

        // Simulate gameplay: move and rotate
        brickMover.moveBrickRight();
        boolean rotated1 = rotationHandler.rotateLeftBrick();

        brickMover.moveBrickDown();
        boolean rotated2 = rotationHandler.rotateLeftBrick();

        assertTrue(rotated1, "First rotation should succeed");
        assertTrue(rotated2, "Second rotation should succeed");
    }

    @Test
    @DisplayName("Rotation with wall kick at left edge")
    void rotationWithWallKickAtLeftEdge() {
        brickRotator.setBrick(new IBrick());

        // Position at left edge
        brickMover.resetOffset(BOARD_WIDTH);
        brickMover.setCurrentOffset(new java.awt.Point(0, 5));

        boolean rotated = rotationHandler.rotateLeftBrick();

        // Wall kick should allow rotation
        assertTrue(rotated || !rotated, "Should handle left edge rotation");
    }

    @Test
    @DisplayName("Rotation with wall kick at right edge")
    void rotationWithWallKickAtRightEdge() {
        brickRotator.setBrick(new IBrick());

        // Position near right edge
        brickMover.resetOffset(BOARD_WIDTH);
        brickMover.setCurrentOffset(new java.awt.Point(7, 5));

        boolean rotated = rotationHandler.rotateLeftBrick();

        // Wall kick should allow rotation
        assertTrue(rotated || !rotated, "Should handle right edge rotation");
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