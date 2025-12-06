package com.comp2042.core.board;

import com.comp2042.brick.Brick;
import com.comp2042.brick.BrickRotator;
import com.comp2042.brick.pieces.IBrick;
import com.comp2042.brick.pieces.OBrick;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

import java.awt.Point;

/**
 * Tests for BrickMover - Handles brick movement (left, right, down)
 */
class BrickMoverTest {

    private static final int BOARD_WIDTH = 10;
    private static final int BOARD_HEIGHT = 20;

    private BrickMover brickMover;
    private BrickRotator brickRotator;
    private BoardStateManager stateManager;

    @BeforeEach
    void setUp() {
        brickRotator = new BrickRotator();
        stateManager = new BoardStateManager(BOARD_WIDTH, BOARD_HEIGHT);
        brickMover = new BrickMover(BOARD_WIDTH, brickRotator, stateManager);

        // Set up a brick for testing
        Brick testBrick = new OBrick();
        brickRotator.setBrick(testBrick);
    }

    // ========== Initialization Tests ==========

    @Test
    @DisplayName("BrickMover initializes with correct spawn position")
    void brickMoverInitializesWithCorrectSpawnPosition() {
        Point offset = brickMover.getCurrentOffset();

        // Default spawn: X = boardWidth/2 - 2 = 10/2 - 2 = 3
        // Default spawn: Y = -1
        assertEquals(3, offset.getX(), "X should be centered (width/2 - 2)");
        assertEquals(-1, offset.getY(), "Y should be -1 (above board)");
    }

    @Test
    @DisplayName("getCurrentOffset returns current position")
    void getCurrentOffsetReturnsCurrentPosition() {
        Point offset = brickMover.getCurrentOffset();
        assertNotNull(offset, "Offset should not be null");
        assertTrue(offset.getX() >= 0, "X should be non-negative");
    }

    @Test
    @DisplayName("getX and getY return correct coordinates")
    void getXAndYReturnCorrectCoordinates() {
        Point offset = brickMover.getCurrentOffset();
        assertEquals((int) offset.getX(), brickMover.getX(), "getX should match offset X");
        assertEquals((int) offset.getY(), brickMover.getY(), "getY should match offset Y");
    }

    // ========== Move Down Tests ==========

    @Test
    @DisplayName("moveBrickDown() moves brick down by 1")
    void moveBrickDownMovesByOne() {
        int initialY = brickMover.getY();

        boolean moved = brickMover.moveBrickDown();

        assertTrue(moved, "Should successfully move down");
        assertEquals(initialY + 1, brickMover.getY(), "Y should increase by 1");
    }

    @Test
    @DisplayName("moveBrickDown() returns true when space available")
    void moveBrickDownReturnsTrueWhenSpaceAvailable() {
        // Move down several times - should succeed initially
        for (int i = 0; i < 5; i++) {
            boolean moved = brickMover.moveBrickDown();
            assertTrue(moved, "Move " + (i + 1) + " should succeed");
        }
    }

    @Test
    @DisplayName("moveBrickDown() returns false at bottom")
    void moveBrickDownReturnsFalseAtBottom() {
        // Move down until collision
        boolean canMove = true;
        int moves = 0;
        while (canMove && moves < 30) {
            canMove = brickMover.moveBrickDown();
            moves++;
        }

        assertFalse(canMove, "Should stop at bottom");
        assertTrue(moves > 0, "Should have moved at least once");
    }

    @Test
    @DisplayName("moveBrickDown() doesn't change X position")
    void moveBrickDownDoesntChangeXPosition() {
        int initialX = brickMover.getX();

        brickMover.moveBrickDown();

        assertEquals(initialX, brickMover.getX(), "X should remain unchanged");
    }

    // ========== Move Left Tests ==========

    @Test
    @DisplayName("moveBrickLeft() moves brick left by 1")
    void moveBrickLeftMovesByOne() {
        int initialX = brickMover.getX();

        boolean moved = brickMover.moveBrickLeft();

        assertTrue(moved, "Should successfully move left");
        assertEquals(initialX - 1, brickMover.getX(), "X should decrease by 1");
    }

    @Test
    @DisplayName("moveBrickLeft() returns false at left wall")
    void moveBrickLeftReturnsFalseAtLeftWall() {
        // Move left until hitting wall
        boolean canMove = true;
        int moves = 0;
        while (canMove && moves < 15) {
            canMove = brickMover.moveBrickLeft();
            moves++;
        }

        assertFalse(canMove, "Should stop at left wall");
        assertTrue(moves > 0, "Should have moved at least once");
    }

    @Test
    @DisplayName("moveBrickLeft() doesn't change Y position")
    void moveBrickLeftDoesntChangeYPosition() {
        int initialY = brickMover.getY();

        brickMover.moveBrickLeft();

        assertEquals(initialY, brickMover.getY(), "Y should remain unchanged");
    }

    @Test
    @DisplayName("moveBrickLeft() multiple times reaches left edge")
    void moveBrickLeftMultipleTimesReachesLeftEdge() {
        // Move left 10 times (should hit wall before then)
        for (int i = 0; i < 10; i++) {
            brickMover.moveBrickLeft();
        }

        // Try one more - should fail
        boolean moved = brickMover.moveBrickLeft();
        assertFalse(moved, "Should not move past left wall");
    }

    // ========== Move Right Tests ==========

    @Test
    @DisplayName("moveBrickRight() moves brick right by 1")
    void moveBrickRightMovesByOne() {
        int initialX = brickMover.getX();

        boolean moved = brickMover.moveBrickRight();

        assertTrue(moved, "Should successfully move right");
        assertEquals(initialX + 1, brickMover.getX(), "X should increase by 1");
    }

    @Test
    @DisplayName("moveBrickRight() returns false at right wall")
    void moveBrickRightReturnsFalseAtRightWall() {
        // Move right until hitting wall
        boolean canMove = true;
        int moves = 0;
        while (canMove && moves < 15) {
            canMove = brickMover.moveBrickRight();
            moves++;
        }

        assertFalse(canMove, "Should stop at right wall");
        assertTrue(moves > 0, "Should have moved at least once");
    }

    @Test
    @DisplayName("moveBrickRight() doesn't change Y position")
    void moveBrickRightDoesntChangeYPosition() {
        int initialY = brickMover.getY();

        brickMover.moveBrickRight();

        assertEquals(initialY, brickMover.getY(), "Y should remain unchanged");
    }

    // ========== Collision Detection Tests ==========

    @Test
    @DisplayName("Movement stops at existing blocks")
    void movementStopsAtExistingBlocks() {
        // Fill bottom row
        int[][] board = stateManager.getBoardMatrix();
        for (int col = 0; col < BOARD_WIDTH; col++) {
            board[BOARD_HEIGHT - 1][col] = 1;
        }

        // Move brick to just above bottom
        while (brickMover.moveBrickDown()) {}

        // Brick should stop before merging with bottom row
        assertTrue(brickMover.getY() < BOARD_HEIGHT - 1,
                "Brick should stop before bottom row");
    }

    @Test
    @DisplayName("Can't move through side walls")
    void cantMoveThroughSideWalls() {
        // Move left to wall
        while (brickMover.moveBrickLeft()) {}
        int leftX = brickMover.getX();

        // Try moving left again - should fail
        assertFalse(brickMover.moveBrickLeft(), "Can't move past left wall");
        assertEquals(leftX, brickMover.getX(), "X should not change");

        // Reset and test right wall
        brickMover.resetOffset(BOARD_WIDTH);
        while (brickMover.moveBrickRight()) {}
        int rightX = brickMover.getX();

        assertFalse(brickMover.moveBrickRight(), "Can't move past right wall");
        assertEquals(rightX, brickMover.getX(), "X should not change");
    }

    // ========== setCurrentOffset Tests ==========

    @Test
    @DisplayName("setCurrentOffset changes position")
    void setCurrentOffsetChangesPosition() {
        Point newOffset = new Point(5, 10);

        brickMover.setCurrentOffset(newOffset);

        assertEquals(5, brickMover.getX(), "X should be 5");
        assertEquals(10, brickMover.getY(), "Y should be 10");
    }

    @Test
    @DisplayName("setCurrentOffset accepts any Point")
    void setCurrentOffsetAcceptsAnyPoint() {
        assertDoesNotThrow(() -> {
            brickMover.setCurrentOffset(new Point(0, 0));
            brickMover.setCurrentOffset(new Point(5, 5));
            brickMover.setCurrentOffset(new Point(-1, -1));
        }, "Should accept any Point value");
    }

    // ========== setToSpawnPoint Tests ==========

    @Test
    @DisplayName("setToSpawnPoint sets position to given point")
    void setToSpawnPointSetsPositionToGivenPoint() {
        Point spawnPoint = new Point(7, 3);

        brickMover.setToSpawnPoint(spawnPoint);

        assertEquals(7, brickMover.getX(), "X should be 7");
        assertEquals(3, brickMover.getY(), "Y should be 3");
    }

    @Test
    @DisplayName("setToSpawnPoint doesn't modify original point")
    void setToSpawnPointDoesntModifyOriginalPoint() {
        Point originalSpawn = new Point(4, 2);

        brickMover.setToSpawnPoint(originalSpawn);
        brickMover.moveBrickDown();

        assertEquals(4, originalSpawn.getX(), "Original point X unchanged");
        assertEquals(2, originalSpawn.getY(), "Original point Y unchanged");
    }

    // ========== resetOffset Tests ==========

    @Test
    @DisplayName("resetOffset returns to default spawn position")
    void resetOffsetReturnsToDefaultSpawn() {
        // Move brick around
        brickMover.moveBrickLeft();
        brickMover.moveBrickDown();
        brickMover.moveBrickDown();

        // Reset
        brickMover.resetOffset(BOARD_WIDTH);

        // Should be back at spawn
        assertEquals(3, brickMover.getX(), "X should be 3 (centered)");
        assertEquals(-1, brickMover.getY(), "Y should be -1 (above board)");
    }

    @Test
    @DisplayName("resetOffset works with different board widths")
    void resetOffsetWorksWithDifferentBoardWidths() {
        BrickMover mover8 = new BrickMover(8, brickRotator, stateManager);
        mover8.resetOffset(8);
        assertEquals(2, mover8.getX(), "8/2 - 2 = 2");

        BrickMover mover12 = new BrickMover(12, brickRotator, stateManager);
        mover12.resetOffset(12);
        assertEquals(4, mover12.getX(), "12/2 - 2 = 4");
    }

    // ========== Movement Sequence Tests ==========

    @Test
    @DisplayName("Left-Down-Right sequence works")
    void leftDownRightSequenceWorks() {
        int startX = brickMover.getX();
        int startY = brickMover.getY();

        brickMover.moveBrickLeft();
        brickMover.moveBrickDown();
        brickMover.moveBrickRight();

        assertEquals(startX, brickMover.getX(), "Should return to start X");
        assertEquals(startY + 1, brickMover.getY(), "Should be 1 down from start Y");
    }

    @Test
    @DisplayName("Multiple down moves accumulate")
    void multipleDownMovesAccumulate() {
        int startY = brickMover.getY();

        for (int i = 0; i < 5; i++) {
            brickMover.moveBrickDown();
        }

        assertEquals(startY + 5, brickMover.getY(), "Y should increase by 5");
    }

    @Test
    @DisplayName("Alternating left-right returns to start")
    void alternatingLeftRightReturnsToStart() {
        int startX = brickMover.getX();

        for (int i = 0; i < 3; i++) {
            brickMover.moveBrickLeft();
            brickMover.moveBrickRight();
        }

        assertEquals(startX, brickMover.getX(), "Should return to start X");
    }

    // ========== Different Brick Types Tests ==========

    @Test
    @DisplayName("I-brick movement works correctly")
    void iBrickMovementWorksCorrectly() {
        Brick iBrick = new IBrick();
        brickRotator.setBrick(iBrick);
        brickMover.resetOffset(BOARD_WIDTH);

        // I-brick should move normally
        assertTrue(brickMover.moveBrickDown(), "I-brick should move down");
        assertTrue(brickMover.moveBrickLeft(), "I-brick should move left");
        assertTrue(brickMover.moveBrickRight(), "I-brick should move right");
    }

    @Test
    @DisplayName("O-brick movement works correctly")
    void oBrickMovementWorksCorrectly() {
        Brick oBrick = new OBrick();
        brickRotator.setBrick(oBrick);
        brickMover.resetOffset(BOARD_WIDTH);

        // O-brick should move normally
        assertTrue(brickMover.moveBrickDown(), "O-brick should move down");
        assertTrue(brickMover.moveBrickLeft(), "O-brick should move left");
        assertTrue(brickMover.moveBrickRight(), "O-brick should move right");
    }

    // ========== Edge Cases ==========

    @Test
    @DisplayName("Movement at spawn height works")
    void movementAtSpawnHeightWorks() {
        // At spawn (Y = -1), movement should still work
        assertTrue(brickMover.moveBrickLeft(), "Should move left at spawn");
        brickMover.moveBrickRight(); // Back to center
        assertTrue(brickMover.moveBrickRight(), "Should move right at spawn");
    }

    @Test
    @DisplayName("Rapid movement sequences don't break state")
    void rapidMovementSequencesDontBreakState() {
        assertDoesNotThrow(() -> {
            for (int i = 0; i < 20; i++) {
                brickMover.moveBrickLeft();
                brickMover.moveBrickRight();
                brickMover.moveBrickDown();
            }
        }, "Rapid movements should not crash");
    }

    @Test
    @DisplayName("Brick cells never go past left wall")
    void brickCellsNeverGoPastLeftWall() {
        // Move left as much as possible
        int moves = 0;
        while (brickMover.moveBrickLeft() && moves < 20) {
            moves++;
        }

        // Should have stopped at the wall - can't move further left
        assertFalse(brickMover.moveBrickLeft(), "Should not be able to move past left wall");

    }

    @Test
    @DisplayName("Position never exceeds board width")
    void positionNeverExceedsBoardWidth() {
        // Move right as much as possible
        for (int i = 0; i < 20; i++) {
            brickMover.moveBrickRight();
        }

        assertTrue(brickMover.getX() < BOARD_WIDTH,
                "X should never reach board width");
    }

    // ========== Integration Tests ==========

    @Test
    @DisplayName("Full drop sequence works")
    void fullDropSequenceWorks() {
        int startY = brickMover.getY();

        // Drop to bottom
        int moves = 0;
        while (brickMover.moveBrickDown() && moves < 30) {
            moves++;
        }

        assertTrue(moves > 0, "Should have moved down at least once");
        assertTrue(brickMover.getY() > startY, "Y should have increased");
    }

    @Test
    @DisplayName("Move left to wall then drop works")
    void moveLeftToWallThenDropWorks() {
        // Move to left wall
        while (brickMover.moveBrickLeft()) {}
        int leftX = brickMover.getX();

        // Drop
        while (brickMover.moveBrickDown()) {}

        // Should still be at left edge
        assertEquals(leftX, brickMover.getX(), "Should remain at left edge");
    }

    @Test
    @DisplayName("Move right to wall then drop works")
    void moveRightToWallThenDropWorks() {
        // Move to right wall
        while (brickMover.moveBrickRight()) {}
        int rightX = brickMover.getX();

        // Drop
        while (brickMover.moveBrickDown()) {}

        // Should still be at right edge
        assertEquals(rightX, brickMover.getX(), "Should remain at right edge");
    }
}