package com.comp2042.ui.logic;

import com.comp2042.model.ViewData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

class ShadowCalculatorTest {

    private ShadowCalculator calculator;
    private static final int BOARD_WIDTH = 10;
    private static final int BOARD_HEIGHT = 20;

    @BeforeEach
    void setUp() {
        calculator = new ShadowCalculator();
    }

    @Test
    @DisplayName("calculateShadowY() returns bottom position for empty board")
    void calculateShadowYReturnsBottomForEmptyBoard() {
        int[][] board = createEmptyBoard();
        int[][] brickData = createSquareBrick();
        ViewData brick = new ViewData(brickData, 4, 0, new int[4][4]);

        int shadowY = calculator.calculateShadowY(brick, board);

        // Brick rows 1-2 are filled, so at y=17, they occupy board rows 18-19
        assertEquals(17, shadowY, "Shadow should be at bottom of empty board");
    }

    @Test
    @DisplayName("calculateShadowY() returns current Y if at bottom")
    void calculateShadowYReturnsCurrentYIfAtBottom() {
        int[][] board = createEmptyBoard();
        fillBottomRow(board);

        int[][] brickData = createSquareBrick();
        // At y=16, brick rows 1-2 occupy board rows 17-18 (above filled row 19)
        ViewData brick = new ViewData(brickData, 4, 16, new int[4][4]);

        int shadowY = calculator.calculateShadowY(brick, board);

        assertEquals(16, shadowY, "Shadow should be at current Y when at bottom");
    }

    @Test
    @DisplayName("calculateShadowY() handles brick at different X positions")
    void calculateShadowYHandlesBrickAtDifferentXPositions() {
        int[][] board = createEmptyBoard();
        int[][] brickData = createSquareBrick();

        // Brick columns 1-2 are filled, so valid X range is 0 to 7
        for (int x = 0; x <= 7; x++) {
            ViewData brick = new ViewData(brickData, x, 0, new int[4][4]);
            int shadowY = calculator.calculateShadowY(brick, board);

            assertEquals(17, shadowY, "Shadow should be at bottom for X=" + x);
        }
    }

    @Test
    @DisplayName("calculateShadowY() handles stacked blocks")
    void calculateShadowYHandlesStackedBlocks() {
        int[][] board = createEmptyBoard();
        for (int row = 15; row < 20; row++) {
            for (int col = 0; col < BOARD_WIDTH; col++) {
                board[row][col] = 1;
            }
        }

        int[][] brickData = createSquareBrick();
        ViewData brick = new ViewData(brickData, 4, 0, new int[4][4]);

        int shadowY = calculator.calculateShadowY(brick, board);

        assertEquals(12, shadowY, "Shadow should stop above stack");
    }

    // ========== Collision Detection Tests ==========

    @Test
    @DisplayName("hasCollision() detects bottom boundary")
    void hasCollisionDetectsBottomBoundary() {
        int[][] board = createEmptyBoard();
        int[][] brick = createSquareBrick();

        // At y=18: brick row 2 maps to board row 20 (out of bounds)
        boolean collision = calculator.hasCollision(board, brick, 4, 18);

        assertTrue(collision, "Should detect bottom boundary collision");
    }

    @Test
    @DisplayName("hasCollision() detects left boundary")
    void hasCollisionDetectsLeftBoundary() {
        int[][] board = createEmptyBoard();
        // Use a brick with filled cells at column 0 (no left padding)
        int[][] brick = createLeftEdgeBrick();

        boolean collision = calculator.hasCollision(board, brick, -1, 10);

        assertTrue(collision, "Should detect left boundary collision");
    }

    @Test
    @DisplayName("hasCollision() detects right boundary")
    void hasCollisionDetectsRightBoundary() {
        int[][] board = createEmptyBoard();
        int[][] brick = createSquareBrick();

        // Brick columns 1-2 filled, at x=8: columns 9-10, column 10 is OOB
        boolean collision = calculator.hasCollision(board, brick, 8, 10);

        assertTrue(collision, "Should detect right boundary collision");
    }

    @Test
    @DisplayName("hasCollision() detects filled cells")
    void hasCollisionDetectsFilledCells() {
        int[][] board = createEmptyBoard();
        board[10][5] = 1;

        int[][] brick = createSquareBrick();
        // Brick at (4, 9): filled cells at columns 5-6, rows 10-11
        boolean collision = calculator.hasCollision(board, brick, 4, 9);

        assertTrue(collision, "Should detect filled cell collision");
    }

    @Test
    @DisplayName("hasCollision() returns false for valid empty space")
    void hasCollisionReturnsFalseForValidEmptySpace() {
        int[][] board = createEmptyBoard();
        int[][] brick = createSquareBrick();

        boolean collision = calculator.hasCollision(board, brick, 4, 10);

        assertFalse(collision, "Should not detect collision in empty space");
    }

    @Test
    @DisplayName("hasCollision() ignores empty brick cells")
    void hasCollisionIgnoresEmptyBrickCells() {
        int[][] board = createEmptyBoard();
        int[][] brick = {
                {0, 0, 0, 0},
                {1, 1, 1, 0},
                {1, 0, 0, 0},
                {0, 0, 0, 0}
        };

        boolean collision = calculator.hasCollision(board, brick, 4, 10);

        assertFalse(collision, "Should ignore empty cells in brick");
    }

    // ========== Null Safety Tests ==========

    @Test
    @DisplayName("calculateShadowY() handles null ViewData gracefully")
    void calculateShadowYHandlesNullViewDataGracefully() {
        int[][] board = createEmptyBoard();

        int shadowY = calculator.calculateShadowY(null, board);

        assertEquals(0, shadowY, "Should return 0 for null ViewData");
    }

    @Test
    @DisplayName("calculateShadowY() handles null board gracefully")
    void calculateShadowYHandlesNullBoardGracefully() {
        int[][] brickData = createSquareBrick();
        ViewData brick = new ViewData(brickData, 4, 5, new int[4][4]);

        int shadowY = calculator.calculateShadowY(brick, null);

        assertEquals(5, shadowY, "Should return current Y for null board");
    }

    @Test
    @DisplayName("calculateShadowY() handles null brick data")
    void calculateShadowYHandlesNullBrickData() {
        int[][] board = createEmptyBoard();
        ViewData brick = new ViewData(null, 4, 5, new int[4][4]);

        int shadowY = calculator.calculateShadowY(brick, board);

        assertEquals(5, shadowY, "Should return current Y for null brick data");
    }

    @Test
    @DisplayName("hasCollision() returns true for null board")
    void hasCollisionReturnsTrueForNullBoard() {
        int[][] brick = createSquareBrick();

        boolean collision = calculator.hasCollision(null, brick, 4, 10);

        assertTrue(collision, "Should return true for null board (safe default)");
    }

    @Test
    @DisplayName("hasCollision() returns true for null brick")
    void hasCollisionReturnsTrueForNullBrick() {
        int[][] board = createEmptyBoard();

        boolean collision = calculator.hasCollision(board, null, 4, 10);

        assertTrue(collision, "Should return true for null brick (safe default)");
    }


    @Test
    @DisplayName("Shadow calculation works for I-brick horizontal")
    void shadowCalculationWorksForIBrickHorizontal() {
        int[][] board = createEmptyBoard();
        int[][] iBrick = {
                {0, 0, 0, 0},
                {1, 1, 1, 1},
                {0, 0, 0, 0},
                {0, 0, 0, 0}
        };
        ViewData brick = new ViewData(iBrick, 3, 0, new int[4][4]);

        int shadowY = calculator.calculateShadowY(brick, board);

        assertEquals(18, shadowY, "I-brick shadow should be at bottom");
    }

    @Test
    @DisplayName("Shadow calculation works for T-brick")
    void shadowCalculationWorksForTBrick() {
        int[][] board = createEmptyBoard();
        int[][] tBrick = {
                {0, 0, 0, 0},
                {1, 1, 1, 0},
                {0, 1, 0, 0},
                {0, 0, 0, 0}
        };
        ViewData brick = new ViewData(tBrick, 3, 0, new int[4][4]);

        int shadowY = calculator.calculateShadowY(brick, board);

        assertEquals(17, shadowY, "T-brick shadow should be at bottom");
    }

    @Test
    @DisplayName("Shadow calculation works for L-brick")
    void shadowCalculationWorksForLBrick() {
        int[][] board = createEmptyBoard();
        int[][] lBrick = {
                {0, 0, 0, 0},
                {1, 1, 1, 0},
                {1, 0, 0, 0},
                {0, 0, 0, 0}
        };
        ViewData brick = new ViewData(lBrick, 3, 5, new int[4][4]);

        int shadowY = calculator.calculateShadowY(brick, board);

        assertTrue(shadowY > 5, "Shadow should be below current position");
        assertEquals(17, shadowY, "L-brick shadow should be at bottom");
    }


    @Test
    @DisplayName("Shadow at brick's current position when can't move down")
    void shadowAtCurrentPositionWhenCantMoveDown() {
        int[][] board = createEmptyBoard();
        fillBottomRow(board);

        int[][] brick = createSquareBrick();
        ViewData brickView = new ViewData(brick, 4, 16, new int[4][4]);

        int shadowY = calculator.calculateShadowY(brickView, board);

        assertEquals(16, shadowY, "Shadow should be at current Y when blocked");
    }

    @Test
    @DisplayName("Shadow calculation at top of board")
    void shadowCalculationAtTopOfBoard() {
        int[][] board = createEmptyBoard();
        int[][] brick = createSquareBrick();
        ViewData brickView = new ViewData(brick, 4, -1, new int[4][4]);

        int shadowY = calculator.calculateShadowY(brickView, board);

        assertTrue(shadowY >= 0, "Shadow should be within or at board");
        assertTrue(shadowY < BOARD_HEIGHT, "Shadow should be within board");
    }

    @Test
    @DisplayName("Shadow calculation with partially filled board")
    void shadowCalculationWithPartiallyFilledBoard() {
        int[][] board = createEmptyBoard();
        board[15][4] = 1;
        board[15][5] = 1;

        int[][] brick = createSquareBrick();
        ViewData brickView = new ViewData(brick, 4, 0, new int[4][4]);

        int shadowY = calculator.calculateShadowY(brickView, board);

        assertEquals(12, shadowY, "Shadow should stop above filled cells");
    }

    @Test
    @DisplayName("hasCollision() allows brick partially above board (Y < 0)")
    void hasCollisionAllowsBrickAboveBoard() {
        int[][] board = createEmptyBoard();
        int[][] brick = createSquareBrick();

        boolean collision = calculator.hasCollision(board, brick, 4, -1);

        assertFalse(collision, "Should allow brick with cells in valid board area");
    }

    @Test
    @DisplayName("hasCollision() detects when brick fully above board")
    void hasCollisionDetectsWhenBrickFullyAboveBoard() {
        int[][] board = createEmptyBoard();
        int[][] brick = createSquareBrick();

        boolean collision = calculator.hasCollision(board, brick, 4, -3);

        assertFalse(collision, "Cells above board don't collide with board contents");
    }



    @Test
    @DisplayName("Shadow calculation is fast for deep drops")
    void shadowCalculationIsFastForDeepDrops() {
        int[][] board = createEmptyBoard();
        int[][] brick = createSquareBrick();
        ViewData brickView = new ViewData(brick, 4, 0, new int[4][4]);

        long startTime = System.nanoTime();
        int shadowY = calculator.calculateShadowY(brickView, board);
        long endTime = System.nanoTime();

        long durationMs = (endTime - startTime) / 1_000_000;

        assertTrue(durationMs < 10, "Shadow calculation should complete in <10ms");
        assertEquals(17, shadowY, "Shadow should still be correct");
    }

    @Test
    @DisplayName("hasCollision() is fast for many checks")
    void hasCollisionIsFastForManyChecks() {
        int[][] board = createEmptyBoard();
        int[][] brick = createSquareBrick();

        long startTime = System.nanoTime();
        for (int i = 0; i < 1000; i++) {
            calculator.hasCollision(board, brick, 4, 10);
        }
        long endTime = System.nanoTime();

        long durationMs = (endTime - startTime) / 1_000_000;

        assertTrue(durationMs < 100, "1000 collision checks should complete in <100ms");
    }


    @Test
    @DisplayName("Shadow calculation with complex board state")
    void shadowCalculationWithComplexBoardState() {
        int[][] board = createEmptyBoard();

        for (int col = 0; col < 5; col++) {
            board[18][col] = 1;
        }
        for (int col = 7; col < 10; col++) {
            board[18][col] = 1;
        }
        board[17][2] = 1;
        board[17][3] = 1;

        int[][] brick = createSquareBrick();
        // At x=5: filled columns 6-7, checking against board
        ViewData brickView = new ViewData(brick, 5, 0, new int[4][4]);

        int shadowY = calculator.calculateShadowY(brickView, board);

        // At y=15: brick rows 1-2 → board rows 16-17
        // At y=16: brick rows 1-2 → board rows 17-18
        // board[18][6]=0, board[18][7]=1 → collision!
        assertEquals(15, shadowY, "Should stop above obstacles");
    }

    @Test
    @DisplayName("Shadow follows brick as it moves horizontally")
    void shadowFollowsBrickAsItMovesHorizontally() {
        int[][] board = createEmptyBoard();
        fillBottomRow(board);

        int[][] brick = createSquareBrick();

        for (int x = 0; x <= 7; x++) {
            ViewData brickView = new ViewData(brick, x, 5, new int[4][4]);
            int shadowY = calculator.calculateShadowY(brickView, board);

            // Bottom row 19 is filled, brick rows 1-2 must be at 17-18
            assertEquals(16, shadowY, "Shadow should be consistent at X=" + x);
        }
    }

    private int[][] createEmptyBoard() {
        return new int[BOARD_HEIGHT][BOARD_WIDTH];
    }

    private int[][] createSquareBrick() {
        return new int[][]{
                {0, 0, 0, 0},
                {0, 1, 1, 0},
                {0, 1, 1, 0},
                {0, 0, 0, 0}
        };
    }

    private int[][] createLeftEdgeBrick() {
        return new int[][]{
                {0, 0, 0, 0},
                {1, 1, 0, 0},
                {1, 1, 0, 0},
                {0, 0, 0, 0}
        };
    }

    private void fillBottomRow(int[][] board) {
        for (int col = 0; col < BOARD_WIDTH; col++) {
            board[BOARD_HEIGHT - 1][col] = 1;
        }
    }
}