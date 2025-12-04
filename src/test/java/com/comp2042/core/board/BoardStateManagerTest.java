package com.comp2042.core.board;

import com.comp2042.model.ClearRow;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

import java.awt.Point;

/**
 * Tests for BoardStateManager - Manages game board matrix state
 */
class BoardStateManagerTest {

    private static final int WIDTH = 10;
    private static final int HEIGHT = 20;

    private BoardStateManager stateManager;

    @BeforeEach
    void setUp() {
        stateManager = new BoardStateManager(WIDTH, HEIGHT);
    }

    // ========== Initialization Tests ==========

    @Test
    @DisplayName("BoardStateManager initializes with empty matrix")
    void boardStateManagerInitializesWithEmptyMatrix() {
        int[][] matrix = stateManager.getBoardMatrix();

        for (int i = 0; i < HEIGHT; i++) {
            for (int j = 0; j < WIDTH; j++) {
                assertEquals(0, matrix[i][j],
                        "Cell [" + i + "][" + j + "] should be empty");
            }
        }
    }

    @Test
    @DisplayName("getBoardMatrix() returns correct dimensions")
    void getBoardMatrixReturnsCorrectDimensions() {
        int[][] matrix = stateManager.getBoardMatrix();

        assertEquals(HEIGHT, matrix.length, "Should have HEIGHT rows");
        assertEquals(WIDTH, matrix[0].length, "Should have WIDTH columns");
    }

    @Test
    @DisplayName("Pieces placed initializes to 0")
    void piecesPlacedInitializesToZero() {
        assertEquals(0, stateManager.getPiecesPlaced(),
                "Pieces placed should start at 0");
    }

    @Test
    @DisplayName("Lines cleared initializes to 0")
    void linesClearedInitializesToZero() {
        assertEquals(0, stateManager.getLinesCleared(),
                "Lines cleared should start at 0");
    }

    @Test
    @DisplayName("getWidth() returns correct width")
    void getWidthReturnsCorrectWidth() {
        assertEquals(WIDTH, stateManager.getWidth(),
                "Should return " + WIDTH);
    }

    @Test
    @DisplayName("getHeight() returns correct height")
    void getHeightReturnsCorrectHeight() {
        assertEquals(HEIGHT, stateManager.getHeight(),
                "Should return " + HEIGHT);
    }

    // ========== getBoardMatrixCopy Tests ==========

    @Test
    @DisplayName("getBoardMatrixCopy() returns independent copy")
    void getBoardMatrixCopyReturnsIndependentCopy() {
        // Get copy
        int[][] copy = stateManager.getBoardMatrixCopy();

        // Modify copy
        copy[5][5] = 99;

        // Original should be unchanged
        int[][] original = stateManager.getBoardMatrix();
        assertEquals(0, original[5][5],
                "Original matrix should not be affected by copy modification");
    }

    @Test
    @DisplayName("getBoardMatrixCopy() has same dimensions as original")
    void getBoardMatrixCopyHasSameDimensions() {
        int[][] copy = stateManager.getBoardMatrixCopy();

        assertEquals(HEIGHT, copy.length, "Copy should have same height");
        assertEquals(WIDTH, copy[0].length, "Copy should have same width");
    }

    @Test
    @DisplayName("getBoardMatrixCopy() has same values as original")
    void getBoardMatrixCopyHasSameValues() {
        // Modify original
        stateManager.getBoardMatrix()[10][5] = 7;

        // Get copy
        int[][] copy = stateManager.getBoardMatrixCopy();

        // Should match
        assertEquals(7, copy[10][5], "Copy should have same values as original");
    }

    // ========== mergeBrickToBackground Tests ==========

    @Test
    @DisplayName("mergeBrickToBackground() adds brick to board")
    void mergeBrickToBackgroundAddsBrickToBoard() {

        int[][] brick = {
                {0, 0, 0, 0},
                {0, 1, 1, 0},
                {0, 1, 1, 0},
                {0, 0, 0, 0}
        };
        Point offset = new Point(4, 10);

        // When: Merge
        stateManager.mergeBrickToBackground(brick, offset);

        // Then: Board should have the brick at transposed positions
        int[][] board = stateManager.getBoardMatrix();
        assertEquals(1, board[11][5], "Should have brick at (11, 5)");
        assertEquals(1, board[11][6], "Should have brick at (11, 6)");
        assertEquals(1, board[12][5], "Should have brick at (12, 5)");
        assertEquals(1, board[12][6], "Should have brick at (12, 6)");
    }

    @Test
    @DisplayName("mergeBrickToBackground() increments pieces placed")
    void mergeBrickToBackgroundIncrementsPiecesPlaced() {
        int[][] brick = createSquareBrick();
        Point offset = new Point(4, 10);

        assertEquals(0, stateManager.getPiecesPlaced(), "Should start at 0");

        stateManager.mergeBrickToBackground(brick, offset);

        assertEquals(1, stateManager.getPiecesPlaced(), "Should be 1 after merge");
    }

    @Test
    @DisplayName("mergeBrickToBackground() can be called multiple times")
    void mergeBrickToBackgroundCanBeCalledMultipleTimes() {
        int[][] brick = createSquareBrick();

        stateManager.mergeBrickToBackground(brick, new Point(2, 15));
        stateManager.mergeBrickToBackground(brick, new Point(5, 15));
        stateManager.mergeBrickToBackground(brick, new Point(7, 15));

        assertEquals(3, stateManager.getPiecesPlaced(),
                "Should have 3 pieces placed");
    }

    @Test
    @DisplayName("mergeBrickToBackground() doesn't overwrite existing blocks with zeros")
    void mergeBrickToBackgroundDoesntOverwriteWithZeros() {
        // Place first brick
        int[][] brick1 = createSquareBrick();
        stateManager.mergeBrickToBackground(brick1, new Point(4, 10));

        // Place second brick that has zeros where first brick is
        int[][] brick2 = createLShapeBrick();
        stateManager.mergeBrickToBackground(brick2, new Point(3, 10));

        // Original brick blocks should still exist
        int[][] board = stateManager.getBoardMatrix();
        assertTrue(board[11][5] != 0 || board[11][6] != 0,
                "Original blocks should remain");
    }

    // ========== clearRows Tests ==========

    @Test
    @DisplayName("clearRows() clears complete row")
    void clearRowsClearsCompleteRow() {
        // Fill bottom row
        int[][] board = stateManager.getBoardMatrix();
        for (int col = 0; col < WIDTH; col++) {
            board[HEIGHT - 1][col] = 1;
        }

        // Clear rows
        ClearRow result = stateManager.clearRows();

        assertEquals(1, result.getLinesRemoved(), "Should clear 1 line");
        assertEquals(1, stateManager.getLinesCleared(), "Counter should be 1");
    }

    @Test
    @DisplayName("clearRows() clears multiple complete rows")
    void clearRowsClearsMultipleCompleteRows() {
        // Fill bottom 3 rows
        int[][] board = stateManager.getBoardMatrix();
        for (int row = HEIGHT - 3; row < HEIGHT; row++) {
            for (int col = 0; col < WIDTH; col++) {
                board[row][col] = 1;
            }
        }

        ClearRow result = stateManager.clearRows();

        assertEquals(3, result.getLinesRemoved(), "Should clear 3 lines");
        assertEquals(3, stateManager.getLinesCleared(), "Counter should be 3");
    }

    @Test
    @DisplayName("clearRows() doesn't clear incomplete rows")
    void clearRowsDoesntClearIncompleteRows() {
        // Fill bottom row with gap
        int[][] board = stateManager.getBoardMatrix();
        for (int col = 0; col < WIDTH - 1; col++) {
            board[HEIGHT - 1][col] = 1;
        }
        // Leave board[HEIGHT-1][WIDTH-1] empty

        ClearRow result = stateManager.clearRows();

        assertEquals(0, result.getLinesRemoved(), "Should not clear incomplete row");
    }

    @Test
    @DisplayName("clearRows() drops rows above cleared lines")
    void clearRowsDropsRowsAboveClearedLines() {
        // Create pattern:
        // Row 17: [1,1,1,1,1,1,1,1,1,1] (will be cleared)
        // Row 18: [2,0,2,0,2,0,2,0,2,0] (should drop to row 18)
        // Row 19: [3,3,3,3,3,3,3,3,3,3] (will be cleared)

        int[][] board = stateManager.getBoardMatrix();

        // Fill row 17
        for (int col = 0; col < WIDTH; col++) {
            board[17][col] = 1;
        }

        // Fill row 18 with pattern
        for (int col = 0; col < WIDTH; col += 2) {
            board[18][col] = 2;
        }

        // Fill row 19
        for (int col = 0; col < WIDTH; col++) {
            board[19][col] = 3;
        }

        // Clear
        stateManager.clearRows();

        // Row 18's pattern should now be at row 19
        int[][] newBoard = stateManager.getBoardMatrix();
        assertEquals(2, newBoard[19][0], "Row 18 should drop to row 19");
        assertEquals(0, newBoard[19][1], "Pattern should be preserved");
    }

    @Test
    @DisplayName("clearRows() accumulates lines cleared")
    void clearRowsAccumulatesLinesCleared() {
        // First clear
        int[][] board = stateManager.getBoardMatrix();
        for (int col = 0; col < WIDTH; col++) {
            board[HEIGHT - 1][col] = 1;
        }
        stateManager.clearRows();

        assertEquals(1, stateManager.getLinesCleared(), "Should have 1 line cleared");

        // IMPORTANT: Get fresh reference after clearRows() since matrix is replaced
        board = stateManager.getBoardMatrix();

        // Second clear
        for (int col = 0; col < WIDTH; col++) {
            board[HEIGHT - 1][col] = 1;
        }
        stateManager.clearRows();

        assertEquals(2, stateManager.getLinesCleared(), "Should have 2 lines total");
    }


    // ========== checkGameOver Tests ==========

    @Test
    @DisplayName("checkGameOver() returns false for empty board")
    void checkGameOverReturnsFalseForEmptyBoard() {
        assertFalse(stateManager.checkGameOver(),
                "Empty board should not be game over");
    }

    @Test
    @DisplayName("checkGameOver() returns true when top row has blocks")
    void checkGameOverReturnsTrueWhenTopRowHasBlocks() {
        // Fill top row
        int[][] board = stateManager.getBoardMatrix();
        board[0][5] = 1;

        assertTrue(stateManager.checkGameOver(),
                "Should be game over when top row has blocks");
    }

    @Test
    @DisplayName("checkGameOver() returns false when only lower rows filled")
    void checkGameOverReturnsFalseWhenOnlyLowerRowsFilled() {
        // Fill rows 1-10, leave row 0 empty
        int[][] board = stateManager.getBoardMatrix();
        for (int row = 1; row < 11; row++) {
            for (int col = 0; col < WIDTH; col++) {
                board[row][col] = 1;
            }
        }

        assertFalse(stateManager.checkGameOver(),
                "Should not be game over if top row is empty");
    }

    @Test
    @DisplayName("checkGameOver() checks all columns of top row")
    void checkGameOverChecksAllColumnsOfTopRow() {
        int[][] board = stateManager.getBoardMatrix();

        // Test each column
        for (int col = 0; col < WIDTH; col++) {
            // Reset board
            for (int c = 0; c < WIDTH; c++) {
                board[0][c] = 0;
            }

            // Fill this column
            board[0][col] = 1;

            assertTrue(stateManager.checkGameOver(),
                    "Should be game over with block at column " + col);
        }
    }

    // ========== isStackNearTop Tests ==========

    @Test
    @DisplayName("isStackNearTop() returns false for empty board")
    void isStackNearTopReturnsFalseForEmptyBoard() {
        assertFalse(stateManager.isStackNearTop(),
                "Empty board should not have stack near top");
    }

    @Test
    @DisplayName("isStackNearTop() returns true when row 0 has blocks")
    void isStackNearTopReturnsTrueWhenRow0HasBlocks() {
        int[][] board = stateManager.getBoardMatrix();
        board[0][5] = 1;

        assertTrue(stateManager.isStackNearTop(),
                "Should detect block in row 0");
    }

    @Test
    @DisplayName("isStackNearTop() returns true when row 1 has blocks")
    void isStackNearTopReturnsTrueWhenRow1HasBlocks() {
        int[][] board = stateManager.getBoardMatrix();
        board[1][5] = 1;

        assertTrue(stateManager.isStackNearTop(),
                "Should detect block in row 1");
    }

    @Test
    @DisplayName("isStackNearTop() returns false when only row 2+ has blocks")
    void isStackNearTopReturnsFalseWhenOnlyRow2HasBlocks() {
        int[][] board = stateManager.getBoardMatrix();
        board[2][5] = 1;
        board[3][5] = 1;

        assertFalse(stateManager.isStackNearTop(),
                "Should not detect blocks below row 2");
    }

    // ========== checkIntersection Tests ==========

    @Test
    @DisplayName("checkIntersection() returns false for valid placement")
    void checkIntersectionReturnsFalseForValidPlacement() {
        int[][] brick = createSquareBrick();

        boolean intersects = stateManager.checkIntersection(brick, 4, 10);

        assertFalse(intersects, "Should not intersect in empty space");
    }

    @Test
    @DisplayName("checkIntersection() returns true for collision with existing block")
    void checkIntersectionReturnsTrueForCollisionWithExistingBlock() {
        // Place block on board
        int[][] board = stateManager.getBoardMatrix();
        board[10][5] = 1;

        // Try to place brick that would overlap
        int[][] brick = createSquareBrick();
        boolean intersects = stateManager.checkIntersection(brick, 4, 9);

        assertTrue(intersects, "Should detect intersection with existing block");
    }

    @Test
    @DisplayName("checkIntersection() returns true for out of bounds")
    void checkIntersectionReturnsTrueForOutOfBounds() {
        int[][] brick = createSquareBrick();

        // Try to place outside right edge
        boolean intersects = stateManager.checkIntersection(brick, WIDTH, 10);

        assertTrue(intersects, "Should detect out of bounds");
    }

    @Test
    @DisplayName("checkIntersection() works at different positions")
    void checkIntersectionWorksAtDifferentPositions() {
        int[][] brick = createSquareBrick();

        // Test various valid positions
        for (int x = 0; x < WIDTH - 2; x++) {
            for (int y = 0; y < HEIGHT - 2; y++) {
                boolean intersects = stateManager.checkIntersection(brick, x, y);
                assertFalse(intersects,
                        "Should not intersect at (" + x + ", " + y + ")");
            }
        }
    }

    // ========== reset Tests ==========

    @Test
    @DisplayName("reset() clears board matrix")
    void resetClearsBoardMatrix() {
        // Fill board with blocks
        int[][] board = stateManager.getBoardMatrix();
        for (int i = 0; i < HEIGHT; i++) {
            for (int j = 0; j < WIDTH; j++) {
                board[i][j] = 5;
            }
        }

        // Reset
        stateManager.reset();

        // Check all cells are empty
        int[][] newBoard = stateManager.getBoardMatrix();
        for (int i = 0; i < HEIGHT; i++) {
            for (int j = 0; j < WIDTH; j++) {
                assertEquals(0, newBoard[i][j],
                        "Cell [" + i + "][" + j + "] should be empty after reset");
            }
        }
    }

    @Test
    @DisplayName("reset() resets pieces placed to 0")
    void resetResetsPiecesPlacedToZero() {
        // Place some pieces
        int[][] brick = createSquareBrick();
        stateManager.mergeBrickToBackground(brick, new Point(4, 10));
        stateManager.mergeBrickToBackground(brick, new Point(4, 12));

        assertEquals(2, stateManager.getPiecesPlaced(), "Should have 2 pieces");

        // Reset
        stateManager.reset();

        assertEquals(0, stateManager.getPiecesPlaced(),
                "Pieces placed should be 0 after reset");
    }

    @Test
    @DisplayName("reset() resets lines cleared to 0")
    void resetResetsLinesClearedToZero() {
        // Clear some lines
        int[][] board = stateManager.getBoardMatrix();
        for (int col = 0; col < WIDTH; col++) {
            board[HEIGHT - 1][col] = 1;
        }
        stateManager.clearRows();

        assertEquals(1, stateManager.getLinesCleared(), "Should have 1 line cleared");

        // Reset
        stateManager.reset();

        assertEquals(0, stateManager.getLinesCleared(),
                "Lines cleared should be 0 after reset");
    }

    @Test
    @DisplayName("reset() can be called multiple times")
    void resetCanBeCalledMultipleTimes() {
        assertDoesNotThrow(() -> {
            for (int i = 0; i < 10; i++) {
                stateManager.reset();
            }
        }, "Multiple resets should not crash");
    }

    // ========== Integration Tests ==========

    @Test
    @DisplayName("Full game cycle: merge -> clear -> reset")
    void fullGameCycleMergeClearReset() {
        // Fill two complete rows directly on the board to avoid merge coordinate issues
        int[][] board = stateManager.getBoardMatrix();
        for (int row = HEIGHT - 2; row < HEIGHT; row++) {
            for (int col = 0; col < WIDTH; col++) {
                board[row][col] = 1;
            }
        }

        // Track that we "placed" pieces (simulate with direct counter if needed)
        // Or just merge one brick to test the counter
        int[][] brick = createSquareBrick();
        stateManager.mergeBrickToBackground(brick, new Point(4, 10));
        assertEquals(1, stateManager.getPiecesPlaced(), "Should have 1 piece");

        // Clear rows (should clear 2 rows that we filled)
        ClearRow result = stateManager.clearRows();
        assertEquals(2, result.getLinesRemoved(), "Should clear 2 rows");
        assertEquals(2, stateManager.getLinesCleared(), "Lines counter should be 2");

        // Reset
        stateManager.reset();

        // Verify everything reset
        assertEquals(0, stateManager.getPiecesPlaced(), "Pieces should be 0");
        assertEquals(0, stateManager.getLinesCleared(), "Lines should be 0");

        board = stateManager.getBoardMatrix();
        for (int i = 0; i < HEIGHT; i++) {
            for (int j = 0; j < WIDTH; j++) {
                assertEquals(0, board[i][j], "Board should be empty");
            }
        }
    }


    @Test
    @DisplayName("Complex clearing scenario")
    void complexClearingScenario() {
        // Create a complex board state
        int[][] board = stateManager.getBoardMatrix();

        // Row 17: Complete
        for (int col = 0; col < WIDTH; col++) {
            board[17][col] = 1;
        }

        // Row 18: Incomplete
        for (int col = 0; col < WIDTH - 1; col++) {
            board[18][col] = 2;
        }

        // Row 19: Complete
        for (int col = 0; col < WIDTH; col++) {
            board[19][col] = 3;
        }

        // Clear
        ClearRow result = stateManager.clearRows();

        // Should clear 2 rows
        assertEquals(2, result.getLinesRemoved(), "Should clear 2 complete rows");

        // Row 18 should drop to row 19
        int[][] newBoard = stateManager.getBoardMatrix();
        assertEquals(2, newBoard[19][0], "Row 18 should drop to row 19");
        assertEquals(0, newBoard[19][WIDTH - 1], "Gap should be preserved");
    }

    // ========== Helper Methods ==========

    private int[][] createSquareBrick() {
        return new int[][]{
                {0, 0, 0, 0},
                {0, 1, 1, 0},
                {0, 1, 1, 0},
                {0, 0, 0, 0}
        };
    }

    private int[][] createLShapeBrick() {
        return new int[][]{
                {0, 0, 0, 0},
                {1, 1, 1, 0},
                {1, 0, 0, 0},
                {0, 0, 0, 0}
        };
    }
}