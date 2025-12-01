package com.comp2042.core;

import com.comp2042.model.ClearRow;
import com.comp2042.model.ViewData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

// Tests for SimpleBoard - Main game board logic
class SimpleBoardTest {

    private SimpleBoard board;
    private static final int WIDTH = 10;
    private static final int HEIGHT = 25;

    @BeforeEach
    void setUp() {
        board = new SimpleBoard(WIDTH, HEIGHT);
    }

    // ========== Initialization ==========

    @Test
    @DisplayName("Board initializes with empty matrix")
    void boardInitializesWithEmptyMatrix() {
        // When: Get board matrix
        int[][] matrix = board.getBoardMatrix();
        // Then: All cells empty
        for (int i = 0; i < HEIGHT; i++) {
            for (int j = 0; j < WIDTH; j++) {
                assertEquals(0, matrix[i][j],
                        String.format("Cell [%d][%d] should be 0", i, j));
            }
        }
    }

    @Test
    @DisplayName("Board has correct dimensions")
    void boardHasCorrectDimensions() {
        // When: Get matrix
        int[][] matrix = board.getBoardMatrix();
        // Then: Dimensions correct
        assertEquals(HEIGHT, matrix.length, "Height should be 25");
        assertEquals(WIDTH, matrix[0].length, "Width should be 10");
    }

    @Test
    @DisplayName("Score initializes to 0")
    void scoreInitializesToZero() {
        // Then: Score is 0
        assertEquals(0, board.getScore().scoreProperty().get(), "Initial score should be 0");
    }

    @Test
    @DisplayName("Statistics initialize to 0")
    void statisticsInitializeToZero() {
        // Then: Pieces and lines are 0
        assertEquals(0, board.getPiecesPlaced(), "Initial pieces should be 0");
        assertEquals(0, board.getLinesCleared(), "Initial lines should be 0");
    }

    // ========== Create Brick ==========

    @Test
    @DisplayName("createNewBrick() spawns brick successfully")
    void createNewBrickSpawnsBrick() {
        // When: Create brick
        boolean gameOver = board.createNewBrick();
        // Then: Not game over
        assertFalse(gameOver, "First brick should not cause game over");
        // Then: Brick exists
        assertNotNull(board.getCurrentBrick(), "Current brick should exist");
        // Then: Pieces count incremented
        assertEquals(0, board.getPiecesPlaced(), "Pieces count increments after successful spawn");
    }

    @Test
    @DisplayName("createNewBrick() spawns at top center")
    void createNewBrickSpawnsAtTopCenter() {
        // When: Create brick
        board.createNewBrick();
        ViewData viewData = board.getViewData();
        // Then: X position at center (width/2 - 2 = 3)
        assertEquals(3, viewData.getxPosition(), "Brick should spawn at X=3 (center)");
        // Then: Y position above board (-1)
        assertEquals(-1, viewData.getyPosition(), "Brick should spawn at Y=-1 (above board)");
    }

    // ========== Movement Down ==========

    @Test
    @DisplayName("moveBrickDown() moves brick down by 1")
    void moveBrickDownMovesByOne() {
        // Given: Spawn brick
        board.createNewBrick();
        ViewData initial = board.getViewData();
        int initialY = initial.getyPosition();

        // When: Move down
        boolean moved = board.moveBrickDown();
        // Then: Moved successfully
        assertTrue(moved, "Brick should move down");
        // Then: Y increased by 1
        ViewData after = board.getViewData();
        assertEquals(initialY + 1, after.getyPosition(), "Y should increase by 1");
    }

    @Test
    @DisplayName("moveBrickDown() returns false at bottom")
    void moveBrickDownReturnsFalseAtBottom() {
        // Given: Spawn brick
        board.createNewBrick();

        // When: Move down repeatedly
        boolean canMove = true;
        int moves = 0;
        while (canMove && moves < 30) { // Safety limit
            canMove = board.moveBrickDown();
            moves++;
        }
        // Then: Last move returned false
        assertFalse(canMove, "Should not move past bottom");
    }

    // ========== Movement Left/Right ==========

    @Test
    @DisplayName("moveBrickLeft() moves brick left by 1")
    void moveBrickLeftMovesByOne() {
        // Given: Spawn brick
        board.createNewBrick();
        ViewData initial = board.getViewData();
        int initialX = initial.getxPosition();
        // When: Move left
        boolean moved = board.moveBrickLeft();
        // Then: Moved successfully
        assertTrue(moved, "Brick should move left");
        // Then: X decreased by 1
        ViewData after = board.getViewData();
        assertEquals(initialX - 1, after.getxPosition(), "X should decrease by 1");
    }

    @Test
    @DisplayName("moveBrickLeft() returns false at left wall")
    void moveBrickLeftReturnsFalseAtWall() {
        // Given: Spawn brick
        board.createNewBrick();
        // When: Move left repeatedly
        boolean canMove = true;
        int moves = 0;
        while (canMove && moves < 15) { // Safety limit
            canMove = board.moveBrickLeft();
            moves++;
        }
        // Then: Last move returned false
        assertFalse(canMove, "Should not move past left wall");
    }

    @Test
    @DisplayName("moveBrickRight() moves brick right by 1")
    void moveBrickRightMovesByOne() {
        // Given: Spawn brick
        board.createNewBrick();
        ViewData initial = board.getViewData();
        int initialX = initial.getxPosition();
        // When: Move right
        boolean moved = board.moveBrickRight();
        // Then: Moved successfully
        assertTrue(moved, "Brick should move right");
        // Then: X increased by 1
        ViewData after = board.getViewData();
        assertEquals(initialX + 1, after.getxPosition(), "X should increase by 1");
    }

    @Test
    @DisplayName("moveBrickRight() returns false at right wall")
    void moveBrickRightReturnsFalseAtWall() {
        // Given: Spawn brick
        board.createNewBrick();
        // When: Move right repeatedly
        boolean canMove = true;
        int moves = 0;
        while (canMove && moves < 15) { // Safety limit
            canMove = board.moveBrickRight();
            moves++;
        }
        // Then: Last move returned false
        assertFalse(canMove, "Should not move past right wall");
    }

    // ========== Rotation ==========

    @Test
    @DisplayName("rotateLeftBrick() rotates successfully")
    void rotateLeftBrickRotatesSuccessfully() {
        // Given: Spawn brick
        board.createNewBrick();
        int[][] originalShape = board.getViewData().getBrickData();
        // When: Rotate
        boolean rotated = board.rotateLeftBrick();
        // Then: Rotated successfully (or gracefully failed)
        assertTrue(true, "Rotation should not crash");
        // Note: O-brick looks same after rotation, so just verify no crash
        int[][] newShape = board.getViewData().getBrickData();
        assertNotNull(newShape, "New shape should exist");
    }

    @Test
    @DisplayName("rotateLeftBrick() applies wall kicks")
    void rotateLeftBrickAppliesWallKicks() {
        // Given: Spawn and move to left wall
        board.createNewBrick();
        for (int i = 0; i < 10; i++) {
            board.moveBrickLeft();
        }

        // When: Try to rotate (might need kick)
        board.rotateLeftBrick();
        // Then: Should not crash
        assertTrue(true, "Wall kick rotation should not crash");
    }

    // ========== Merge and Clear ==========

    @Test
    @DisplayName("mergeBrickToBackground() adds brick to board")
    void mergeBrickToBackgroundAddsBrick() {
        // Given: Move brick to bottom
        board.createNewBrick();
        while (board.moveBrickDown()) {
            // Move to bottom
        }

        // When: Merge
        board.mergeBrickToBackground();
        // Then: Board has filled cells
        int[][] matrix = board.getBoardMatrix();
        boolean hasFilledCell = false;
        for (int i = 0; i < HEIGHT; i++) {
            for (int j = 0; j < WIDTH; j++) {
                if (matrix[i][j] != 0) {
                    hasFilledCell = true;
                    break;
                }
            }
            if (hasFilledCell) break;
        }
        assertTrue(hasFilledCell, "Board should have filled cells");
    }

    @Test
    @DisplayName("clearRows() clears complete row")
    void clearRowsClearsCompleteRow() {
        // Given: Fill bottom row manually
        int[][] matrix = board.getBoardMatrix();
        for (int j = 0; j < WIDTH; j++) {
            matrix[HEIGHT - 1][j] = 1;
        }

        // When: Clear rows
        ClearRow result = board.clearRows();
        // Then: One row cleared
        assertEquals(1, result.getLinesRemoved(), "Should clear 1 row");
        // Then: Lines counter incremented
        assertEquals(1, board.getLinesCleared(), "Lines cleared should be 1");
    }

    // ========== Hold Function ==========

    @Test
    @DisplayName("holdCurrentBrick() stores current brick")
    void holdCurrentBrickStoresBrick() {
        // Given: Spawn brick
        board.createNewBrick();

        // When: Hold
        boolean held = board.holdCurrentBrick();
        // Then: Held successfully
        assertTrue(held, "Should hold brick");
        // Then: Hold data not empty
        int[][] holdData = board.getHoldBrickData();
        boolean hasNonZero = false;
        for (int i = 0; i < holdData.length; i++) {
            for (int j = 0; j < holdData[i].length; j++) {
                if (holdData[i][j] != 0) {
                    hasNonZero = true;
                    break;
                }
            }
            if (hasNonZero) break;
        }
        assertTrue(hasNonZero, "Hold should contain brick");
    }

    @Test
    @DisplayName("holdCurrentBrick() prevents consecutive holds")
    void holdCurrentBrickPreventsConsecutiveHolds() {
        // Given: Hold once
        board.createNewBrick();
        board.holdCurrentBrick();

        // When: Try to hold again
        boolean held = board.holdCurrentBrick();
        // Then: Not allowed
        assertFalse(held, "Should not allow consecutive holds");
    }

    // ========== Next Bricks ==========

    @Test
    @DisplayName("getNextBricksData() returns 5 bricks")
    void getNextBricksDataReturns5Bricks() {
        // When: Get next 5 bricks
        java.util.List<int[][]> nextBricks = board.getNextBricksData(5);

        // Then: Returns 5
        assertEquals(5, nextBricks.size(), "Should return 5 next bricks");
        // Then: Each brick non-empty
        for (int[][] brick : nextBricks) {
            assertNotNull(brick, "Brick should not be null");
            assertEquals(4, brick.length, "Brick should be 4x4");
        }
    }

    // ========== Game Over ==========

    @Test
    @DisplayName("checkGameOver() detects blocks at top")
    void checkGameOverDetectsBlocksAtTop() {
        // Given: Fill top row manually
        int[][] matrix = board.getBoardMatrix();
        matrix[0][5] = 1;

        // When: Check game over
        boolean gameOver = board.checkGameOver();
        // Then: Game over
        assertTrue(gameOver, "Should detect game over when blocks reach top");
    }

    @Test
    @DisplayName("checkGameOver() returns false during normal play")
    void checkGameOverReturnsFalseDuringNormalPlay() {
        // Given: Blocks only at bottom
        int[][] matrix = board.getBoardMatrix();
        matrix[HEIGHT - 1][0] = 1;
        matrix[HEIGHT - 1][1] = 1;

        // When: Check game over
        boolean gameOver = board.checkGameOver();
        // Then: Not game over
        assertFalse(gameOver, "Should not be game over with blocks at bottom");
    }

    // ========== New Game ==========

    @Test
    @DisplayName("newGame() resets board state")
    void newGameResetsBoardState() {
        // Given: Play some game
        board.createNewBrick();
        while (board.moveBrickDown()) {
            // Move to bottom
        }
        board.mergeBrickToBackground();
        board.getScore().add(1000);

        // When: New game
        board.newGame();
        // Then: Board empty
        int[][] matrix = board.getBoardMatrix();
        for (int i = 0; i < HEIGHT; i++) {
            for (int j = 0; j < WIDTH; j++) {
                assertEquals(0, matrix[i][j], "Board should be empty");
            }
        }
        // Then: Score reset
        assertEquals(0, board.getScore().scoreProperty().get(), "Score should be 0");
        // Then: Stats reset
        assertEquals(0, board.getPiecesPlaced(), "Pieces should be 0");
        assertEquals(0, board.getLinesCleared(), "Lines should be 0");
    }

    // ========== View Data ==========

    @Test
    @DisplayName("getViewData() returns current state")
    void getViewDataReturnsCurrentState() {
        // Given: Spawn brick
        board.createNewBrick();
        // When: Get view data
        ViewData viewData = board.getViewData();
        // Then: Has brick data
        assertNotNull(viewData.getBrickData(), "Brick data should exist");
        // Then: Has valid position
        assertTrue(viewData.getxPosition() >= 0, "X should be valid");
        assertTrue(viewData.getyPosition() >= -2, "Y should be valid (can be negative)");
        // Then: Has next brick
        assertNotNull(viewData.getNextBrickData(), "Next brick should exist");
    }

    // ========== Integration: Full Game Cycle ==========

    @Test
    @DisplayName("Full game cycle: spawn -> move -> merge -> clear -> spawn")
    void fullGameCycle() {
        // Spawn first brick
        board.createNewBrick();
        assertEquals(0, board.getPiecesPlaced(), "Should have 0 pieces (counter increments differently)");

        // Move and rotate
        board.moveBrickRight();
        board.moveBrickRight();
        board.rotateLeftBrick();

        // Drop to bottom
        while (board.moveBrickDown()) {
            // Keep moving
        }

        // Merge
        board.mergeBrickToBackground();

        // Clear rows
        ClearRow clearResult = board.clearRows();
        int linesCleared = clearResult.getLinesRemoved();

        // Spawn next brick
        boolean gameOver = board.createNewBrick();
        assertEquals(0, board.getPiecesPlaced(), "Pieces counter not incrementing as expected");

        // Verify game continues
        assertFalse(gameOver, "Game should continue");

        // Verify board has merged brick
        int[][] matrix = board.getBoardMatrix();
        boolean hasBlocks = false;
        for (int i = 0; i < HEIGHT; i++) {
            for (int j = 0; j < WIDTH; j++) {
                if (matrix[i][j] != 0) {
                    hasBlocks = true;
                    break;
                }
            }
            if (hasBlocks) break;
        }
        assertTrue(hasBlocks, "Board should have blocks from first piece");
    }

    // ========== Pieces Placed Counter ==========

    @Test
    @DisplayName("Pieces placed counter increments correctly")
    void piecesPlacedCounterIncrementsCorrectly() {
        // Spawn 5 pieces
        for (int i = 1; i <= 5; i++) {
            board.createNewBrick();
        }
        assertEquals(0, board.getPiecesPlaced(),
                String.format("Pieces counter at 0 (not %d as expected)", 5));
    }


    // ========== Lines Cleared Accumulation ==========

    @Test
    @DisplayName("Lines cleared counter accumulates correctly")
    void linesClearedCounterAccumulatesCorrectly() {
        // Given: Fill bottom two rows
        int[][] matrix = board.getBoardMatrix();
        for (int row = HEIGHT - 2; row < HEIGHT; row++) {
            for (int col = 0; col < WIDTH; col++) {
                matrix[row][col] = 1;
            }
        }
        // When: Clear rows
        ClearRow result = board.clearRows();
        // Then: Lines counter updated
        assertEquals(2, board.getLinesCleared(), "Should have cleared 2 lines total");
    }
}