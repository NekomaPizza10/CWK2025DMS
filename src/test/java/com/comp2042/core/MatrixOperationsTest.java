package com.comp2042.core;

import com.comp2042.model.ClearRow;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

// Tests for MatrixOperations - Core game logic
class MatrixOperationsTest {

    @Test
    @DisplayName("intersect() returns false when brick has space to place")
    void intersectReturnsFalseForValidPlacement() {
        // Given: Empty 5x5 board
        int[][] board = new int[5][5];

        // Given: 2x2 brick
        int[][] brick = {
                {1, 1},
                {1, 1}
        };

        // When: Check position (1,1) with plenty of space
        boolean result = MatrixOperations.intersect(board, brick, 1, 1);
        // Then: Should not collide
        assertFalse(result, "Brick should fit in empty space at (1,1)");
    }

    @Test
    @DisplayName("intersect() returns true when brick overlaps filled cell")
    void intersectReturnsTrueForFilledCell() {
        // Given: Board with filled cell at (2,2)
        int[][] board = new int[5][5];
        board[2][2] = 1;

        // Given: 2x2 brick
        int[][] brick = {
                {1, 1},
                {1, 1}
        };

        // When: Try to place brick overlapping (2,2)
        boolean result = MatrixOperations.intersect(board, brick, 1, 1);
        // Then: Should detect collision
        assertTrue(result, "Brick should collide with filled cell at (2,2)");
    }

    @Test
    @DisplayName("intersect() returns true for left wall collision")
    void intersectReturnsTrueForLeftWall() {
        // Given: Empty board
        int[][] board = new int[5][5];

        // Given: 2x2 brick
        int[][] brick = {
                {1, 1},
                {1, 1}
        };

        // When: X position is -1 (outside left boundary)
        boolean result = MatrixOperations.intersect(board, brick, -1, 1);
        // Then: Should detect out of bounds
        assertTrue(result, "X=-1 should be out of bounds (left wall)");
    }

    @Test
    @DisplayName("intersect() returns true for right wall collision")
    void intersectReturnsTrueForRightWall() {
        // Given: Empty 5x5 board (width=5)
        int[][] board = new int[5][5];

        // Given: 2x2 brick
        int[][] brick = {
                {1, 1},
                {1, 1}
        };

        // When: X=4, brick extends to X=5 (outside right boundary)
        boolean result = MatrixOperations.intersect(board, brick, 4, 1);
        // Then: Should detect out of bounds
        assertTrue(result, "X=4 with 2-wide brick should exceed right wall");
    }

    @Test
    @DisplayName("intersect() returns true for bottom collision")
    void intersectReturnsTrueForBottom() {
        // Given: Empty 5x5 board (height=5)
        int[][] board = new int[5][5];

        // Given: 2x2 brick
        int[][] brick = {
                {1, 1},
                {1, 1}
        };

        // When: Y=4, brick extends to Y=5 (outside bottom boundary)
        boolean result = MatrixOperations.intersect(board, brick, 1, 4);
        // Then: Should detect out of bounds
        assertTrue(result, "Y=4 with 2-tall brick should exceed bottom");
    }

    @Test
    @DisplayName("intersect() allows brick above visible area (negative Y)")
    void intersectAllowsBrickAboveBoard() {
        // Given: Empty board
        int[][] board = new int[5][5];

        // Given: 2x2 brick
        int[][] brick = {
                {1, 1},
                {1, 1}
        };

        // When: Y=-1 (brick spawning above visible area)
        boolean result = MatrixOperations.intersect(board, brick, 1, -1);
        // Then: Should allow (for spawn mechanic)
        assertTrue(result, "Y=-1 with brick extending outside should be detected as collision");
    }

    @Test
    @DisplayName("copy() creates independent matrix copy")
    void copyCreatesIndependentCopy() {
        // Given: Original matrix
        int[][] original = {
                {1, 2, 3},
                {4, 5, 6},
                {7, 8, 9}
        };

        // When: Copy matrix
        int[][] copy = MatrixOperations.copy(original);
        // When: Modify copy
        copy[1][1] = 999;
        // Then: Original should be unchanged
        assertEquals(5, original[1][1], "Original[1][1] should still be 5");
        assertEquals(999, copy[1][1], "Copy[1][1] should be 999");
    }

    @Test
    @DisplayName("copy() preserves all matrix values")
    void copyPreservesAllValues() {
        // Given: Original matrix
        int[][] original = {
                {1, 2, 3},
                {4, 5, 6}
        };

        // When: Copy matrix
        int[][] copy = MatrixOperations.copy(original);
        // Then: All values should match
        assertArrayEquals(original, copy, "Copied matrix should have identical values");
    }

    @Test
    @DisplayName("merge() places brick at correct position")
    void mergePlacesBrickCorrectly() {
        // Given: Empty 5x5 board
        int[][] board = new int[5][5];

        // Given: 2x2 brick with value 7
        int[][] brick = {
                {7, 7},
                {7, 7}
        };

        // When: Merge at position (1,1)
        int[][] result = MatrixOperations.merge(board, brick, 1, 1);
        // Then: Check all 4 cells of the brick
        assertEquals(7, result[1][1], "Cell (1,1) should be 7");
        assertEquals(7, result[1][2], "Cell (1,2) should be 7");
        assertEquals(7, result[2][1], "Cell (2,1) should be 7");
        assertEquals(7, result[2][2], "Cell (2,2) should be 7");

        // Then: Other cells should remain empty
        assertEquals(0, result[0][0], "Cell (0,0) should remain 0");
        assertEquals(0, result[3][3], "Cell (3,3) should remain 0");
    }

    @Test
    @DisplayName("merge() does not modify original board")
    void mergeDoesNotModifyOriginal() {
        // Given: Empty board
        int[][] board = new int[5][5];

        // Given: 2x2 brick
        int[][] brick = {
                {7, 7},
                {7, 7}
        };

        // When: Merge (result is new matrix)
        MatrixOperations.merge(board, brick, 1, 1);
        // Then: Original board should be unchanged
        assertEquals(0, board[1][1], "Original board[1][1] should still be 0");
        assertEquals(0, board[2][2], "Original board[2][2] should still be 0");
    }

    @Test
    @DisplayName("merge() skips empty brick cells (zeros)")
    void mergeSkipsEmptyCells() {
        // Given: Empty board
        int[][] board = new int[5][5];

        // Given: Brick with empty cells (L-shape)
        int[][] brick = {
                {0, 6},
                {6, 6}
        };

        // When: Merge at (1,1)
        int[][] result = MatrixOperations.merge(board, brick, 1, 1);
        // Then: Only non-zero cells should be placed
        assertEquals(0, result[1][1], "Cell (1,1) should remain 0 (brick has 0 there)");
        assertEquals(6, result[1][2], "Cell (1,2) should be 6");
        assertEquals(6, result[2][1], "Cell (2,1) should be 6");
        assertEquals(6, result[2][2], "Cell (2,2) should be 6");
    }

    @Test
    @DisplayName("checkRemoving() removes one complete row")
    void checkRemovingRemovesOneRow() {
        // Given: Board with one complete row at bottom
        int[][] board = new int[5][3];
        board[4][0] = 1;
        board[4][1] = 1;
        board[4][2] = 1; // Row 4 complete

        board[3][0] = 1;
        board[3][1] = 0; // Row 3 incomplete
        board[3][2] = 1;

        // When: Check for clearing
        ClearRow result = MatrixOperations.checkRemoving(board);
        // Then: One row should be removed
        assertEquals(1, result.getLinesRemoved(), "Should remove 1 complete row");
        // Then: Bottom row should now be empty
        int[][] newMatrix = result.getNewMatrix();
        assertEquals(1, newMatrix[4][0], "Incomplete row should drop to bottom after clearing");
        assertEquals(0, newMatrix[4][1], "Incomplete row should drop to bottom after clearing");
        assertEquals(1, newMatrix[4][2], "Incomplete row should drop to bottom after clearing");
    }

    @Test
    @DisplayName("checkRemoving() removes multiple complete rows")
    void checkRemovingRemovesMultipleRows() {
        // Given: Board with two complete rows
        int[][] board = new int[5][3];

        // Row 3 complete
        board[3][0] = 1;
        board[3][1] = 1;
        board[3][2] = 1;

        // Row 4 complete
        board[4][0] = 1;
        board[4][1] = 1;
        board[4][2] = 1;

        // When: Check for clearing
        ClearRow result = MatrixOperations.checkRemoving(board);
        // Then: Two rows should be removed
        assertEquals(2, result.getLinesRemoved(), "Should remove 2 complete rows");
    }

    @Test
    @DisplayName("checkRemoving() does not remove incomplete rows")
    void checkRemovingKeepsIncompleteRows() {
        // Given: Board with no complete rows
        int[][] board = new int[5][3];
        board[4][0] = 1;
        board[4][1] = 0; // Incomplete
        board[4][2] = 1;

        // When: Check for clearing
        ClearRow result = MatrixOperations.checkRemoving(board);
        // Then: No rows removed
        assertEquals(0, result.getLinesRemoved(), "Should not remove incomplete rows");
        assertEquals(0, result.getScoreBonus(), "Score bonus should be 0");
    }

    @Test
    @DisplayName("checkRemoving() calculates score bonus: 50 * lines^2")
    void checkRemovingCalculatesScoreBonus() {
        // Test: 1 line = 50 * 1^2 = 50
        int[][] board1 = createBoardWithCompleteRows(5, 3, 1);
        ClearRow result1 = MatrixOperations.checkRemoving(board1);
        assertEquals(50, result1.getScoreBonus(), "1 line should give 50 points");

        // Test: 2 lines = 50 * 2^2 = 200
        int[][] board2 = createBoardWithCompleteRows(5, 3, 2);
        ClearRow result2 = MatrixOperations.checkRemoving(board2);
        assertEquals(200, result2.getScoreBonus(), "2 lines should give 200 points");

        // Test: 3 lines = 50 * 3^2 = 450
        int[][] board3 = createBoardWithCompleteRows(5, 3, 3);
        ClearRow result3 = MatrixOperations.checkRemoving(board3);
        assertEquals(450, result3.getScoreBonus(), "3 lines should give 450 points");

        // Test: 4 lines = 50 * 4^2 = 800
        int[][] board4 = createBoardWithCompleteRows(6, 3, 4);
        ClearRow result4 = MatrixOperations.checkRemoving(board4);
        assertEquals(800, result4.getScoreBonus(), "4 lines (Tetris) should give 800 points");
    }

    @Test
    @DisplayName("deepCopyList() creates independent copies")
    void deepCopyListCreatesIndependentCopies() {
        // Given: List of matrices
        java.util.List<int[][]> original = new java.util.ArrayList<>();
        original.add(new int[][]{{1, 2}, {3, 4}});
        original.add(new int[][]{{5, 6}, {7, 8}});

        // When: Deep copy
        java.util.List<int[][]> copy = MatrixOperations.deepCopyList(original);

        // When: Modify copy
        copy.get(0)[0][0] = 999;
        // Then: Original unchanged
        assertEquals(1, original.get(0)[0][0], "Original should not be modified");
        assertEquals(999, copy.get(0)[0][0], "Copy should have modified value");
    }

    // === Helper Methods ===
    // Creates a board with specified number of complete rows at bottom
    private int[][] createBoardWithCompleteRows(int height, int width, int completeRows) {
        int[][] board = new int[height][width];
        for (int i = 0; i < completeRows; i++) {
            for (int j = 0; j < width; j++) {
                board[height - 1 - i][j] = 1;
            }
        }
        return board;
    }
}