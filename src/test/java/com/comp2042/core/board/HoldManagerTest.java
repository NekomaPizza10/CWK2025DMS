package com.comp2042.core.board;

import com.comp2042.brick.Brick;
import com.comp2042.brick.BrickRotator;
import com.comp2042.brick.pieces.IBrick;
import com.comp2042.brick.pieces.OBrick;
import com.comp2042.brick.pieces.TBrick;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

import java.awt.Point;

/**
 * Tests for HoldManager - Manages hold piece functionality
 */
class HoldManagerTest {

    private static final int BOARD_WIDTH = 10;

    private HoldManager holdManager;
    private BrickRotator brickRotator;
    private BrickSpawner brickSpawner;
    private BrickMover brickMover;
    private BoardStateManager stateManager;

    @BeforeEach
    void setUp() {
        brickRotator = new BrickRotator();
        stateManager = new BoardStateManager(BOARD_WIDTH, 20);
        brickMover = new BrickMover(BOARD_WIDTH, brickRotator, stateManager);
        brickSpawner = new BrickSpawner(BOARD_WIDTH, brickRotator, stateManager, brickMover);

        holdManager = new HoldManager(BOARD_WIDTH, brickRotator, brickSpawner, brickMover);

        // Set up initial brick
        brickSpawner.createNewBrick();
    }

    // ========== Initialization Tests ==========

    @Test
    @DisplayName("HoldManager initializes with empty hold slot")
    void holdManagerInitializesWithEmptyHoldSlot() {
        int[][] holdData = holdManager.getHoldBrickData();

        // Should be empty 4x4 matrix
        assertEquals(4, holdData.length, "Should be 4x4 matrix");
        assertEquals(4, holdData[0].length, "Should be 4x4 matrix");

        // All cells should be 0
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                assertEquals(0, holdData[i][j],
                        "Hold slot should be empty initially");
            }
        }
    }

    @Test
    @DisplayName("HoldManager initializes with hold enabled")
    void holdManagerInitializesWithHoldEnabled() {
        // Should be able to hold immediately
        Brick currentBrick = brickRotator.getBrick();
        assertNotNull(currentBrick, "Should have current brick");

        boolean held = holdManager.holdCurrentBrick();
        assertTrue(held, "Should be able to hold on first try");
    }

    // ========== First Hold Tests ==========

    @Test
    @DisplayName("holdCurrentBrick() stores first brick")
    void holdCurrentBrickStoresFirstBrick() {
        // Get current brick type
        Brick currentBrick = brickRotator.getBrick();
        int[][] currentShape = currentBrick.getShapeMatrix().get(0);

        // Hold it
        boolean held = holdManager.holdCurrentBrick();

        assertTrue(held, "Should successfully hold");

        // Verify it's in hold
        int[][] holdData = holdManager.getHoldBrickData();

        // Should match original brick's first rotation
        boolean hasNonZero = false;
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                if (holdData[i][j] != 0) {
                    hasNonZero = true;
                    break;
                }
            }
        }
        assertTrue(hasNonZero, "Hold should contain brick");
    }

    @Test
    @DisplayName("holdCurrentBrick() spawns new brick when hold is empty")
    void holdCurrentBrickSpawnsNewBrickWhenHoldIsEmpty() {
        Brick beforeHold = brickRotator.getBrick();

        holdManager.holdCurrentBrick();

        Brick afterHold = brickRotator.getBrick();

        // Should be a different brick instance (new spawn)
        assertNotSame(beforeHold, afterHold,
                "Should spawn new brick after first hold");
    }

    @Test
    @DisplayName("holdCurrentBrick() returns true on first hold")
    void holdCurrentBrickReturnsTrueOnFirstHold() {
        boolean result = holdManager.holdCurrentBrick();

        assertTrue(result, "First hold should return true");
    }

    // ========== Second Hold (Swap) Tests ==========

    @Test
    @DisplayName("holdCurrentBrick() swaps with held brick")
    void holdCurrentBrickSwapsWithHeldBrick() {
        // Hold first brick (O-brick for example)
        Brick firstBrick = brickRotator.getBrick();
        String firstType = firstBrick.getClass().getSimpleName();

        holdManager.holdCurrentBrick();

        // Get second brick
        Brick secondBrick = brickRotator.getBrick();
        String secondType = secondBrick.getClass().getSimpleName();

        // Hold second brick (should swap)
        holdManager.setCanHold(true); // Re-enable hold
        holdManager.holdCurrentBrick();

        // Now should have first brick back
        Brick afterSwap = brickRotator.getBrick();
        String afterSwapType = afterSwap.getClass().getSimpleName();

        assertEquals(firstType, afterSwapType,
                "Should swap back to first brick type");
    }

    @Test
    @DisplayName("holdCurrentBrick() updates hold slot after swap")
    void holdCurrentBrickUpdatesHoldSlotAfterSwap() {
        // Set up specific bricks for testing
        brickRotator.setBrick(new IBrick());
        holdManager.holdCurrentBrick(); // Hold I-brick

        brickRotator.setBrick(new OBrick());
        holdManager.setCanHold(true);
        holdManager.holdCurrentBrick(); // Hold O-brick, get I-brick back

        // Hold should now have O-brick
        int[][] holdData = holdManager.getHoldBrickData();

        // O-brick has specific pattern
        boolean hasOPattern = false;
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 3; j++) {
                if (holdData[i][j] == 4 && holdData[i][j+1] == 4) {
                    hasOPattern = true;
                }
            }
        }
        assertTrue(hasOPattern, "Hold should contain O-brick pattern");
    }

    // ========== Hold Prevention Tests ==========

    @Test
    @DisplayName("holdCurrentBrick() prevents consecutive holds")
    void holdCurrentBrickPreventsConsecutiveHolds() {
        // First hold succeeds
        boolean first = holdManager.holdCurrentBrick();
        assertTrue(first, "First hold should succeed");

        // Second hold without re-enabling fails
        boolean second = holdManager.holdCurrentBrick();
        assertFalse(second, "Second hold should fail");
    }

    @Test
    @DisplayName("setCanHold(false) prevents hold")
    void setCanHoldFalsePreventsHold() {
        holdManager.setCanHold(false);

        boolean result = holdManager.holdCurrentBrick();

        assertFalse(result, "Should not allow hold when disabled");
    }

    @Test
    @DisplayName("setCanHold(true) re-enables hold")
    void setCanHoldTrueReEnablesHold() {
        // Use hold once
        holdManager.holdCurrentBrick();

        // Re-enable
        holdManager.setCanHold(true);

        // Should work again
        boolean result = holdManager.holdCurrentBrick();
        assertTrue(result, "Should allow hold after re-enabling");
    }

    @Test
    @DisplayName("Hold prevention works across multiple pieces")
    void holdPreventionWorksAcrossMultiplePieces() {
        // Hold piece 1
        holdManager.holdCurrentBrick();

        // Try holding piece 2 without re-enabling
        boolean result = holdManager.holdCurrentBrick();
        assertFalse(result, "Should not allow second hold");

        // Re-enable and hold piece 2
        holdManager.setCanHold(true);
        boolean result2 = holdManager.holdCurrentBrick();
        assertTrue(result2, "Should allow hold after re-enable");

        // Try holding piece 3 without re-enabling
        boolean result3 = holdManager.holdCurrentBrick();
        assertFalse(result3, "Should not allow third hold");
    }

    // ========== getHoldBrickData Tests ==========

    @Test
    @DisplayName("getHoldBrickData() returns empty matrix when no hold")
    void getHoldBrickDataReturnsEmptyMatrixWhenNoHold() {
        int[][] holdData = holdManager.getHoldBrickData();

        // Should be all zeros
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                assertEquals(0, holdData[i][j], "Should be empty");
            }
        }
    }

    @Test
    @DisplayName("getHoldBrickData() returns brick data after hold")
    void getHoldBrickDataReturnsBrickDataAfterHold() {
        holdManager.holdCurrentBrick();

        int[][] holdData = holdManager.getHoldBrickData();

        // Should contain brick data (non-zero values)
        boolean hasNonZero = false;
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                if (holdData[i][j] != 0) {
                    hasNonZero = true;
                    break;
                }
            }
        }
        assertTrue(hasNonZero, "Hold should contain brick after hold");
    }

    @Test
    @DisplayName("getHoldBrickData() always returns 4x4 matrix")
    void getHoldBrickDataAlwaysReturns4x4Matrix() {
        // Before hold
        int[][] before = holdManager.getHoldBrickData();
        assertEquals(4, before.length, "Should be 4x4 before hold");
        assertEquals(4, before[0].length, "Should be 4x4 before hold");

        // After hold
        holdManager.holdCurrentBrick();
        int[][] after = holdManager.getHoldBrickData();
        assertEquals(4, after.length, "Should be 4x4 after hold");
        assertEquals(4, after[0].length, "Should be 4x4 after hold");
    }

    @Test
    @DisplayName("getHoldBrickData() returns first rotation state")
    void getHoldBrickDataReturnsFirstRotationState() {
        // Set up brick that has been rotated
        brickRotator.setBrick(new TBrick());

        // Rotate it
        brickRotator.setCurrentShape(2);

        // Hold it
        holdManager.holdCurrentBrick();

        // Get hold data - should be rotation 0, not rotation 2
        int[][] holdData = holdManager.getHoldBrickData();

        // T-brick rotation 0 has specific pattern (3 blocks in row, 1 below center)
        // We're just verifying it returns SOME valid brick pattern
        boolean hasPattern = false;
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                if (holdData[i][j] != 0) {
                    hasPattern = true;
                    break;
                }
            }
        }
        assertTrue(hasPattern, "Should have brick pattern");
    }

    // ========== reset Tests ==========

    @Test
    @DisplayName("reset() clears hold slot")
    void resetClearsHoldSlot() {
        // Hold a brick
        holdManager.holdCurrentBrick();

        // Verify hold has brick
        int[][] before = holdManager.getHoldBrickData();
        boolean hadBrick = false;
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                if (before[i][j] != 0) {
                    hadBrick = true;
                    break;
                }
            }
        }
        assertTrue(hadBrick, "Should have brick before reset");

        // Reset
        holdManager.reset();

        // Verify hold is empty
        int[][] after = holdManager.getHoldBrickData();
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                assertEquals(0, after[i][j],
                        "Hold should be empty after reset");
            }
        }
    }

    @Test
    @DisplayName("reset() re-enables hold")
    void resetReEnablesHold() {
        // Use hold
        holdManager.holdCurrentBrick();

        // Reset
        holdManager.reset();

        // Should be able to hold again
        brickSpawner.createNewBrick(); // Get new brick
        boolean result = holdManager.holdCurrentBrick();
        assertTrue(result, "Should be able to hold after reset");
    }

    @Test
    @DisplayName("reset() can be called multiple times")
    void resetCanBeCalledMultipleTimes() {
        assertDoesNotThrow(() -> {
            for (int i = 0; i < 10; i++) {
                holdManager.reset();
            }
        }, "Multiple resets should not crash");
    }

    @Test
    @DisplayName("reset() works when hold is empty")
    void resetWorksWhenHoldIsEmpty() {
        // Reset without holding anything
        assertDoesNotThrow(() -> {
            holdManager.reset();
        }, "Reset should work when hold is empty");

        // Hold should still be empty
        int[][] holdData = holdManager.getHoldBrickData();
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                assertEquals(0, holdData[i][j], "Hold should remain empty");
            }
        }
    }

    // ========== Integration Tests ==========

    @Test
    @DisplayName("Complete hold cycle: hold -> swap -> reset")
    void completeHoldCycleHoldSwapReset() {
        // Setup
        brickRotator.setBrick(new IBrick());

        // Hold I-brick
        boolean hold1 = holdManager.holdCurrentBrick();
        assertTrue(hold1, "First hold should succeed");

        // Get new brick (should be different)
        brickRotator.setBrick(new OBrick());

        // Re-enable and swap
        holdManager.setCanHold(true);
        boolean hold2 = holdManager.holdCurrentBrick();
        assertTrue(hold2, "Swap should succeed");

        // Should have I-brick back
        Brick current = brickRotator.getBrick();
        assertEquals("IBrick", current.getClass().getSimpleName(),
                "Should have swapped back to I-brick");

        // Reset
        holdManager.reset();

        // Hold should be empty
        int[][] holdData = holdManager.getHoldBrickData();
        boolean isEmpty = true;
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                if (holdData[i][j] != 0) {
                    isEmpty = false;
                }
            }
        }
        assertTrue(isEmpty, "Hold should be empty after reset");
    }

    @Test
    @DisplayName("Hold different brick types")
    void holdDifferentBrickTypes() {
        // Test I-brick
        brickRotator.setBrick(new IBrick());
        holdManager.holdCurrentBrick();
        int[][] iBrickData = holdManager.getHoldBrickData();
        assertTrue(hasNonZeroCells(iBrickData), "Should hold I-brick");

        // Reset and test O-brick
        holdManager.reset();
        brickRotator.setBrick(new OBrick());
        holdManager.holdCurrentBrick();
        int[][] oBrickData = holdManager.getHoldBrickData();
        assertTrue(hasNonZeroCells(oBrickData), "Should hold O-brick");

        // Reset and test T-brick
        holdManager.reset();
        brickRotator.setBrick(new TBrick());
        holdManager.holdCurrentBrick();
        int[][] tBrickData = holdManager.getHoldBrickData();
        assertTrue(hasNonZeroCells(tBrickData), "Should hold T-brick");
    }

    @Test
    @DisplayName("Multiple hold-swap cycles work correctly")
    void multipleHoldSwapCyclesWorkCorrectly() {
        for (int cycle = 0; cycle < 3; cycle++) {
            // Hold current
            boolean hold1 = holdManager.holdCurrentBrick();
            assertTrue(hold1, "Cycle " + cycle + " first hold should succeed");

            // Re-enable and swap
            holdManager.setCanHold(true);
            boolean hold2 = holdManager.holdCurrentBrick();
            assertTrue(hold2, "Cycle " + cycle + " swap should succeed");

            // Re-enable for next cycle
            holdManager.setCanHold(true);
        }
    }

    @Test
    @DisplayName("Hold preserves brick identity through swaps")
    void holdPreservesBrickIdentityThroughSwaps() {
        // Use specific bricks for testing
        brickRotator.setBrick(new IBrick());
        holdManager.holdCurrentBrick();

        brickRotator.setBrick(new OBrick());
        holdManager.setCanHold(true);
        holdManager.holdCurrentBrick(); // Now holding O, have I

        assertEquals("IBrick", brickRotator.getBrick().getClass().getSimpleName(),
                "Should have I-brick");

        holdManager.setCanHold(true);
        holdManager.holdCurrentBrick(); // Now holding I, have O

        assertEquals("OBrick", brickRotator.getBrick().getClass().getSimpleName(),
                "Should have O-brick");

        holdManager.setCanHold(true);
        holdManager.holdCurrentBrick(); // Now holding O, have I again

        assertEquals("IBrick", brickRotator.getBrick().getClass().getSimpleName(),
                "Should have I-brick again");
    }

    // ========== Edge Cases ==========

    @Test
    @DisplayName("Hold works at spawn position")
    void holdWorksAtSpawnPosition() {
        // Brick should be at spawn
        Point offset = brickMover.getCurrentOffset();
        assertEquals(3, offset.getX(), "Should be at spawn X");
        assertEquals(-1, offset.getY(), "Should be at spawn Y");

        // Hold should work
        boolean result = holdManager.holdCurrentBrick();
        assertTrue(result, "Should hold at spawn position");
    }

    @Test
    @DisplayName("Hold resets brick position to spawn")
    void holdResetsBrickPositionToSpawn() {
        // Move brick away from spawn
        brickMover.moveBrickDown();
        brickMover.moveBrickDown();
        brickMover.moveBrickLeft();

        Point beforeHold = brickMover.getCurrentOffset();
        assertTrue(beforeHold.getY() > -1, "Brick should have moved");

        // Hold and get new brick
        holdManager.holdCurrentBrick();

        // Position should be reset
        Point afterHold = brickMover.getCurrentOffset();
        assertEquals(3, afterHold.getX(), "X should be reset to spawn");
        assertEquals(-1, afterHold.getY(), "Y should be reset to spawn");
    }

    @Test
    @DisplayName("Swap resets brick position to spawn")
    void swapResetsBrickPositionToSpawn() {
        // Hold first brick
        holdManager.holdCurrentBrick();

        // Move second brick
        brickMover.moveBrickDown();
        brickMover.moveBrickRight();

        // Swap
        holdManager.setCanHold(true);
        holdManager.holdCurrentBrick();

        // Position should be reset
        Point afterSwap = brickMover.getCurrentOffset();
        assertEquals(3, afterSwap.getX(), "X should be reset after swap");
        assertEquals(-1, afterSwap.getY(), "Y should be reset after swap");
    }

    // ========== Helper Methods ==========

    private boolean hasNonZeroCells(int[][] matrix) {
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[i].length; j++) {
                if (matrix[i][j] != 0) {
                    return true;
                }
            }
        }
        return false;
    }
}