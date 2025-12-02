package com.comp2042.brick;

import com.comp2042.brick.pieces.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for individual Tetris brick pieces (Tetrominos)
 * Tests the 7 standard Tetris pieces: I, O, T, S, Z, J, L
 */
class BrickPiecesTest {

    // ========== I-Brick Tests ==========

    @Test
    @DisplayName("I-Brick has 2 rotation states")
    void iBrickHasTwoRotations() {
        IBrick brick = new IBrick();
        List<int[][]> shapes = brick.getShapeMatrix();
        assertEquals(2, shapes.size(), "I-Brick should have 2 rotation states");
    }
    @Test
    @DisplayName("I-Brick first rotation is horizontal")
    void iBrickFirstRotationIsHorizontal() {
        IBrick brick = new IBrick();
        int[][] shape = brick.getShapeMatrix().get(0);

        // Should be 4x4 matrix
        assertEquals(4, shape.length, "Should be 4x4 matrix");

        // Check for horizontal line (row 1)
        int filledCount = 0;
        for (int j = 0; j < 4; j++) {
            if (shape[1][j] == 1) filledCount++;
        }
        assertEquals(4, filledCount, "Should have 4 consecutive blocks horizontally");
    }
    @Test
    @DisplayName("I-Brick second rotation is vertical")
    void iBrickSecondRotationIsVertical() {
        IBrick brick = new IBrick();
        int[][] shape = brick.getShapeMatrix().get(1);
        // Check for vertical line (column 1)
        int filledCount = 0;
        for (int i = 0; i < 4; i++) {
            if (shape[i][1] == 1) filledCount++;
        }
        assertEquals(4, filledCount, "Should have 4 consecutive blocks vertically");
    }
    @Test
    @DisplayName("I-Brick uses color code 1 (cyan)")
    void iBrickUsesColorCode1() {
        IBrick brick = new IBrick();
        int[][] shape = brick.getShapeMatrix().get(0);
        boolean hasColorCode1 = false;
        for (int i = 0; i < shape.length; i++) {
            for (int j = 0; j < shape[i].length; j++) {
                if (shape[i][j] == 1) hasColorCode1 = true;
            }
        }
        assertTrue(hasColorCode1, "I-Brick should use color code 1");
    }
    @Test
    @DisplayName("I-Brick has exactly 4 blocks")
    void iBrickHas4Blocks() {
        IBrick brick = new IBrick();
        int[][] shape = brick.getShapeMatrix().get(0);

        int filledCount = 0;
        for (int i = 0; i < shape.length; i++) {
            for (int j = 0; j < shape[i].length; j++) {
                if (shape[i][j] == 1) filledCount++;
            }
        }
        assertEquals(4, filledCount, "I-Brick should have exactly 4 blocks");
    }
    @Test
    @DisplayName("I-Brick returns 4x4 matrix")
    void iBrickReturns4x4Matrix() {
        IBrick brick = new IBrick();
        int[][] shape = brick.getShapeMatrix().get(0);
        assertEquals(4, shape.length, "I-Brick should have 4 rows");
        assertEquals(4, shape[0].length, "I-Brick should have 4 columns");
    }

    // ========== O-Brick Tests ==========
    @Test
    @DisplayName("O-Brick has 1 rotation state")
    void oBrickHasOneRotation() {
        OBrick brick = new OBrick();
        List<int[][]> shapes = brick.getShapeMatrix();
        assertEquals(1, shapes.size(), "O-Brick should have 1 rotation state (square)");
    }
    @Test
    @DisplayName("O-Brick is a 2x2 square")
    void oBrickIs2x2Square() {
        OBrick brick = new OBrick();
        int[][] shape = brick.getShapeMatrix().get(0);

        // Count filled cells in 2x2 area (should be in center of 4x4 matrix)
        int filledCount = 0;
        for (int i = 1; i <= 2; i++) {
            for (int j = 1; j <= 2; j++) {
                if (shape[i][j] == 4) filledCount++;
            }
        }
        assertEquals(4, filledCount, "O-Brick should have 4 blocks in 2x2 pattern");
    }

    @Test
    @DisplayName("O-Brick uses color code 4 (yellow)")
    void oBrickUsesColorCode4() {
        OBrick brick = new OBrick();
        int[][] shape = brick.getShapeMatrix().get(0);

        boolean hasColorCode4 = false;
        for (int i = 0; i < shape.length; i++) {
            for (int j = 0; j < shape[i].length; j++) {
                if (shape[i][j] == 4) hasColorCode4 = true;
            }
        }
        assertTrue(hasColorCode4, "O-Brick should use color code 4");
    }
    @Test
    @DisplayName("O-Brick has exactly 4 blocks")
    void oBrickHas4Blocks() {
        OBrick brick = new OBrick();
        int[][] shape = brick.getShapeMatrix().get(0);

        int filledCount = 0;
        for (int i = 0; i < shape.length; i++) {
            for (int j = 0; j < shape[i].length; j++) {
                if (shape[i][j] == 4) filledCount++;
            }
        }
        assertEquals(4, filledCount, "O-Brick should have exactly 4 blocks");
    }
    @Test
    @DisplayName("O-Brick returns 4x4 matrix")
    void oBrickReturns4x4Matrix() {
        OBrick brick = new OBrick();
        int[][] shape = brick.getShapeMatrix().get(0);
        assertEquals(4, shape.length, "O-Brick should have 4 rows");
        assertEquals(4, shape[0].length, "O-Brick should have 4 columns");
    }

    // ========== T-Brick Tests ==========
    @Test
    @DisplayName("T-Brick has 4 rotation states")
    void tBrickHasFourRotations() {
        TBrick brick = new TBrick();
        List<int[][]> shapes = brick.getShapeMatrix();
        assertEquals(4, shapes.size(), "T-Brick should have 4 rotation states");
    }
    @Test
    @DisplayName("T-Brick has T-shape with 4 blocks")
    void tBrickHasTShapeWith4Blocks() {
        TBrick brick = new TBrick();
        int[][] shape = brick.getShapeMatrix().get(0);

        int filledCount = 0;
        for (int i = 0; i < shape.length; i++) {
            for (int j = 0; j < shape[i].length; j++) {
                if (shape[i][j] == 6) filledCount++;
            }
        }
        assertEquals(4, filledCount, "T-Brick should have exactly 4 blocks");
    }
    @Test
    @DisplayName("T-Brick uses color code 6 (purple)")
    void tBrickUsesColorCode6() {
        TBrick brick = new TBrick();
        int[][] shape = brick.getShapeMatrix().get(0);

        boolean hasColorCode6 = false;
        for (int i = 0; i < shape.length; i++) {
            for (int j = 0; j < shape[i].length; j++) {
                if (shape[i][j] == 6) hasColorCode6 = true;
            }
        }
        assertTrue(hasColorCode6, "T-Brick should use color code 6");
    }

    @Test
    @DisplayName("T-Brick returns 4x4 matrix")
    void tBrickReturns4x4Matrix() {
        TBrick brick = new TBrick();
        int[][] shape = brick.getShapeMatrix().get(0);
        assertEquals(4, shape.length, "T-Brick should have 4 rows");
        assertEquals(4, shape[0].length, "T-Brick should have 4 columns");
    }

    // ========== S-Brick Tests ==========

    @Test
    @DisplayName("S-Brick has 2 rotation states")
    void sBrickHasTwoRotations() {
        SBrick brick = new SBrick();
        List<int[][]> shapes = brick.getShapeMatrix();
        assertEquals(2, shapes.size(), "S-Brick should have 2 rotation states");
    }

    @Test
    @DisplayName("S-Brick has 4 blocks")
    void sBrickHas4Blocks() {
        SBrick brick = new SBrick();
        int[][] shape = brick.getShapeMatrix().get(0);

        int filledCount = 0;
        for (int i = 0; i < shape.length; i++) {
            for (int j = 0; j < shape[i].length; j++) {
                if (shape[i][j] == 5) filledCount++;
            }
        }
        assertEquals(4, filledCount, "S-Brick should have exactly 4 blocks");
    }

    @Test
    @DisplayName("S-Brick uses color code 5 (green)")
    void sBrickUsesColorCode5() {
        SBrick brick = new SBrick();
        int[][] shape = brick.getShapeMatrix().get(0);

        boolean hasColorCode5 = false;
        for (int i = 0; i < shape.length; i++) {
            for (int j = 0; j < shape[i].length; j++) {
                if (shape[i][j] == 5) hasColorCode5 = true;
            }
        }
        assertTrue(hasColorCode5, "S-Brick should use color code 5");
    }

    @Test
    @DisplayName("S-Brick returns 4x4 matrix")
    void sBrickReturns4x4Matrix() {
        SBrick brick = new SBrick();
        int[][] shape = brick.getShapeMatrix().get(0);
        assertEquals(4, shape.length, "S-Brick should have 4 rows");
        assertEquals(4, shape[0].length, "S-Brick should have 4 columns");
    }

    // ========== Z-Brick Tests ==========

    @Test
    @DisplayName("Z-Brick has 2 rotation states")
    void zBrickHasTwoRotations() {
        ZBrick brick = new ZBrick();
        List<int[][]> shapes = brick.getShapeMatrix();
        assertEquals(2, shapes.size(), "Z-Brick should have 2 rotation states");
    }

    @Test
    @DisplayName("Z-Brick has 4 blocks")
    void zBrickHas4Blocks() {
        ZBrick brick = new ZBrick();
        int[][] shape = brick.getShapeMatrix().get(0);

        int filledCount = 0;
        for (int i = 0; i < shape.length; i++) {
            for (int j = 0; j < shape[i].length; j++) {
                if (shape[i][j] == 7) filledCount++;
            }
        }
        assertEquals(4, filledCount, "Z-Brick should have exactly 4 blocks");
    }

    @Test
    @DisplayName("Z-Brick uses color code 7 (red)")
    void zBrickUsesColorCode7() {
        ZBrick brick = new ZBrick();
        int[][] shape = brick.getShapeMatrix().get(0);

        boolean hasColorCode7 = false;
        for (int i = 0; i < shape.length; i++) {
            for (int j = 0; j < shape[i].length; j++) {
                if (shape[i][j] == 7) hasColorCode7 = true;
            }
        }
        assertTrue(hasColorCode7, "Z-Brick should use color code 7");
    }

    @Test
    @DisplayName("Z-Brick returns 4x4 matrix")
    void zBrickReturns4x4Matrix() {
        ZBrick brick = new ZBrick();
        int[][] shape = brick.getShapeMatrix().get(0);
        assertEquals(4, shape.length, "Z-Brick should have 4 rows");
        assertEquals(4, shape[0].length, "Z-Brick should have 4 columns");
    }

    // ========== J-Brick Tests ==========
    @Test
    @DisplayName("J-Brick has 4 rotation states")
    void jBrickHasFourRotations() {
        JBrick brick = new JBrick();
        List<int[][]> shapes = brick.getShapeMatrix();
        assertEquals(4, shapes.size(), "J-Brick should have 4 rotation states");
    }
    @Test
    @DisplayName("J-Brick has 4 blocks")
    void jBrickHas4Blocks() {
        JBrick brick = new JBrick();
        int[][] shape = brick.getShapeMatrix().get(0);

        int filledCount = 0;
        for (int i = 0; i < shape.length; i++) {
            for (int j = 0; j < shape[i].length; j++) {
                if (shape[i][j] == 2) filledCount++;
            }
        }
        assertEquals(4, filledCount, "J-Brick should have exactly 4 blocks");
    }
    @Test
    @DisplayName("J-Brick uses color code 2 (blue)")
    void jBrickUsesColorCode2() {
        JBrick brick = new JBrick();
        int[][] shape = brick.getShapeMatrix().get(0);

        boolean hasColorCode2 = false;
        for (int i = 0; i < shape.length; i++) {
            for (int j = 0; j < shape[i].length; j++) {
                if (shape[i][j] == 2) hasColorCode2 = true;
            }
        }
        assertTrue(hasColorCode2, "J-Brick should use color code 2");
    }
    @Test
    @DisplayName("J-Brick returns 4x4 matrix")
    void jBrickReturns4x4Matrix() {
        JBrick brick = new JBrick();
        int[][] shape = brick.getShapeMatrix().get(0);
        assertEquals(4, shape.length, "J-Brick should have 4 rows");
        assertEquals(4, shape[0].length, "J-Brick should have 4 columns");
    }

    // ========== L-Brick Tests ==========
    @Test
    @DisplayName("L-Brick has 4 rotation states")
    void lBrickHasFourRotations() {
        LBrick brick = new LBrick();
        List<int[][]> shapes = brick.getShapeMatrix();
        assertEquals(4, shapes.size(), "L-Brick should have 4 rotation states");
    }
    @Test
    @DisplayName("L-Brick has 4 blocks")
    void lBrickHas4Blocks() {
        LBrick brick = new LBrick();
        int[][] shape = brick.getShapeMatrix().get(0);

        int filledCount = 0;
        for (int i = 0; i < shape.length; i++) {
            for (int j = 0; j < shape[i].length; j++) {
                if (shape[i][j] == 3) filledCount++;
            }
        }
        assertEquals(4, filledCount, "L-Brick should have exactly 4 blocks");
    }
    @Test
    @DisplayName("L-Brick uses color code 3 (orange)")
    void lBrickUsesColorCode3() {
        LBrick brick = new LBrick();
        int[][] shape = brick.getShapeMatrix().get(0);

        boolean hasColorCode3 = false;
        for (int i = 0; i < shape.length; i++) {
            for (int j = 0; j < shape[i].length; j++) {
                if (shape[i][j] == 3) hasColorCode3 = true;
            }
        }
        assertTrue(hasColorCode3, "L-Brick should use color code 3");
    }
    @Test
    @DisplayName("L-Brick returns 4x4 matrix")
    void lBrickReturns4x4Matrix() {
        LBrick brick = new LBrick();
        int[][] shape = brick.getShapeMatrix().get(0);
        assertEquals(4, shape.length, "L-Brick should have 4 rows");
        assertEquals(4, shape[0].length, "L-Brick should have 4 columns");
    }

    // ========== Tests for All Bricks ==========
    @Test
    @DisplayName("All bricks have exactly 4 blocks")
    void allBricksHaveExactly4Blocks() {
        Brick[] bricks = {
                new IBrick(), new OBrick(), new TBrick(),
                new SBrick(), new ZBrick(), new JBrick(), new LBrick()
        };

        for (Brick brick : bricks) {
            int[][] shape = brick.getShapeMatrix().get(0);
            int blockCount = countBlocks(shape);
            assertEquals(4, blockCount,
                    brick.getClass().getSimpleName() + " should have exactly 4 blocks");
        }
    }
    @Test
    @DisplayName("All bricks return 4x4 matrices")
    void allBricksReturn4x4Matrices() {
        Brick[] bricks = {
                new IBrick(), new OBrick(), new TBrick(),
                new SBrick(), new ZBrick(), new JBrick(), new LBrick()
        };

        for (Brick brick : bricks) {
            List<int[][]> shapes = brick.getShapeMatrix();
            for (int rot = 0; rot < shapes.size(); rot++) {
                int[][] shape = shapes.get(rot);
                assertEquals(4, shape.length,
                        brick.getClass().getSimpleName() + " rotation " + rot + " should have 4 rows");
                assertEquals(4, shape[0].length,
                        brick.getClass().getSimpleName() + " rotation " + rot + " should have 4 columns");
            }
        }
    }
    @Test
    @DisplayName("I-Brick has correct rotation count")
    void iBrickHasCorrectRotationCount() {
        assertEquals(2, new IBrick().getShapeMatrix().size());
    }
    @Test
    @DisplayName("O-Brick has correct rotation count")
    void oBrickHasCorrectRotationCount() {
        assertEquals(1, new OBrick().getShapeMatrix().size());
    }
    @Test
    @DisplayName("T-Brick has correct rotation count")
    void tBrickHasCorrectRotationCount() {
        assertEquals(4, new TBrick().getShapeMatrix().size());
    }
    @Test
    @DisplayName("S-Brick has correct rotation count")
    void sBrickHasCorrectRotationCount() {
        assertEquals(2, new SBrick().getShapeMatrix().size());
    }
    @Test
    @DisplayName("Z-Brick has correct rotation count")
    void zBrickHasCorrectRotationCount() {
        assertEquals(2, new ZBrick().getShapeMatrix().size());
    }
    @Test
    @DisplayName("J-Brick has correct rotation count")
    void jBrickHasCorrectRotationCount() {
        assertEquals(4, new JBrick().getShapeMatrix().size());
    }
    @Test
    @DisplayName("L-Brick has correct rotation count")
    void lBrickHasCorrectRotationCount() {
        assertEquals(4, new LBrick().getShapeMatrix().size());
    }

    // ========== Shape Matrix Deep Copy Tests ==========

    @Test
    @DisplayName("I-Brick getShapeMatrix returns independent copies")
    void iBrickGetShapeMatrixReturnsIndependentCopies() {
        IBrick brick = new IBrick();

        List<int[][]> shapes1 = brick.getShapeMatrix();
        List<int[][]> shapes2 = brick.getShapeMatrix();

        shapes1.get(0)[0][0] = 999;

        assertNotEquals(999, shapes2.get(0)[0][0],
                "I-Brick getShapeMatrix should return independent copies");
    }
    @Test
    @DisplayName("All bricks return defensive copies")
    void allBricksReturnDefensiveCopies() {
        Brick[] bricks = {
                new IBrick(), new OBrick(), new TBrick(),
                new SBrick(), new ZBrick(), new JBrick(), new LBrick()
        };

        for (Brick brick : bricks) {
            List<int[][]> shapes1 = brick.getShapeMatrix();
            List<int[][]> shapes2 = brick.getShapeMatrix();

            if (shapes1.size() > 0 && shapes1.get(0).length > 0) {
                int original = shapes1.get(0)[0][0];
                shapes1.get(0)[0][0] = 999;

                assertEquals(original, shapes2.get(0)[0][0],
                        brick.getClass().getSimpleName() + " should return defensive copies");
            }
        }
    }

    // ========== Rotation Consistency Tests ==========
    @Test
    @DisplayName("All rotations have same number of blocks")
    void allRotationsHaveSameNumberOfBlocks() {
        Brick[] bricks = {
                new IBrick(), new OBrick(), new TBrick(),
                new SBrick(), new ZBrick(), new JBrick(), new LBrick()
        };

        for (Brick brick : bricks) {
            List<int[][]> shapes = brick.getShapeMatrix();
            int firstRotationBlocks = countBlocks(shapes.get(0));

            for (int i = 1; i < shapes.size(); i++) {
                int rotationBlocks = countBlocks(shapes.get(i));
                assertEquals(firstRotationBlocks, rotationBlocks,
                        brick.getClass().getSimpleName() + " rotation " + i +
                                " should have same block count as rotation 0");
            }
        }
    }
    @Test
    @DisplayName("All rotations use same color code")
    void allRotationsUseSameColorCode() {
        Brick[] bricks = {
                new IBrick(), new JBrick(), new LBrick(),
                new TBrick(), new SBrick(), new ZBrick()
        };

        for (Brick brick : bricks) {
            List<int[][]> shapes = brick.getShapeMatrix();
            int colorCode = getColorCode(shapes.get(0));

            for (int i = 1; i < shapes.size(); i++) {
                int rotationColor = getColorCode(shapes.get(i));
                assertEquals(colorCode, rotationColor,
                        brick.getClass().getSimpleName() + " rotation " + i +
                                " should use same color as rotation 0");
            }
        }
    }

    // ========== Edge Cases ==========
    @Test
    @DisplayName("Empty cells in matrix are zeros")
    void emptyCellsInMatrixAreZeros() {
        Brick[] bricks = {
                new IBrick(), new OBrick(), new TBrick(),
                new SBrick(), new ZBrick(), new JBrick(), new LBrick()
        };

        for (Brick brick : bricks) {
            int[][] shape = brick.getShapeMatrix().get(0);
            for (int i = 0; i < shape.length; i++) {
                for (int j = 0; j < shape[i].length; j++) {
                    int value = shape[i][j];
                    assertTrue(value >= 0 && value <= 7,
                            brick.getClass().getSimpleName() +
                                    " should only have values 0-7, found: " + value);
                }
            }
        }
    }
    @Test
    @DisplayName("No brick has all zeros in any rotation")
    void noBrickHasAllZerosInAnyRotation() {
        Brick[] bricks = {
                new IBrick(), new OBrick(), new TBrick(),
                new SBrick(), new ZBrick(), new JBrick(), new LBrick()
        };

        for (Brick brick : bricks) {
            List<int[][]> shapes = brick.getShapeMatrix();
            for (int rot = 0; rot < shapes.size(); rot++) {
                int blockCount = countBlocks(shapes.get(rot));
                assertTrue(blockCount > 0,
                        brick.getClass().getSimpleName() + " rotation " + rot +
                                " should have at least one block");
            }
        }
    }

    // ========== Helper Methods ==========
    private int countBlocks(int[][] shape) {
        int count = 0;
        for (int i = 0; i < shape.length; i++) {
            for (int j = 0; j < shape[i].length; j++) {
                if (shape[i][j] != 0) count++;
            }
        }
        return count;
    }

    private int getColorCode(int[][] shape) {
        for (int i = 0; i < shape.length; i++) {
            for (int j = 0; j < shape[i].length; j++) {
                if (shape[i][j] != 0) return shape[i][j];
            }
        }
        return 0;
    }
}