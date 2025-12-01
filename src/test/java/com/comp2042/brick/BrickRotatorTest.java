package com.comp2042.brick;

import com.comp2042.model.NextShapeInfo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

// Tests for BrickRotator - Rotation mechanics
class BrickRotatorTest {

    private BrickRotator rotator;

    @BeforeEach
    void setUp() {
        rotator = new BrickRotator();
    }

    @Test
    @DisplayName("setBrick() initializes brick at rotation 0")
    void setBrickInitializesAtRotationZero() {
        // Given: Test brick with 2 rotations
        Brick testBrick = createTestBrick();
        // When: Set the brick
        rotator.setBrick(testBrick);
        // Then: Brick should be set
        assertNotNull(rotator.getBrick(), "Brick should be set");
        // Then: Should be at rotation 0 (horizontal)
        int[][] shape = rotator.getCurrentShape();
        assertEquals(1, shape[1][0], "Should start at rotation 0 (horizontal)");
    }

    @Test
    @DisplayName("getNextShape() returns next rotation position")
    void getNextShapeReturnsNextRotation() {
        // Given: Brick at rotation 0
        rotator.setBrick(createTestBrick());

        // When: Get next shape
        NextShapeInfo nextShape = rotator.getNextShape();

        // Then: Should return rotation 1
        assertEquals(1, nextShape.getPosition(), "Next position should be 1");

        // Then: Shape should be vertical
        int[][] shape = nextShape.getShape();
        assertEquals(1, shape[0][1], "Next shape should be vertical");
    }

    @Test
    @DisplayName("getNextShape() wraps to rotation 0 after last")
    void getNextShapeWrapsAround() {
        // Given: Brick at last rotation (1)
        rotator.setBrick(createTestBrick());
        rotator.setCurrentShape(1);

        // When: Get next shape
        NextShapeInfo nextShape = rotator.getNextShape();

        // Then: Should wrap to 0
        assertEquals(0, nextShape.getPosition(), "Should wrap to rotation 0");

        // Then: Shape should be horizontal again
        int[][] shape = nextShape.getShape();
        assertEquals(1, shape[1][0], "Should be back to horizontal");
    }

    @Test
    @DisplayName("getCurrentShape() returns current rotation state")
    void getCurrentShapeReturnsCorrectState() {
        // Given: Brick is set
        rotator.setBrick(createTestBrick());

        // When: Get shape at position 0
        int[][] shape0 = rotator.getCurrentShape();

        // Then: Should be horizontal
        assertEquals(1, shape0[1][0], "Position 0 should be horizontal");

        // When: Change to position 1
        rotator.setCurrentShape(1);
        int[][] shape1 = rotator.getCurrentShape();

        // Then: Should be vertical
        assertEquals(1, shape1[0][1], "Position 1 should be vertical");
    }

    @Test
    @DisplayName("getCurrentShape() returns empty when no brick set")
    void getCurrentShapeReturnsEmptyWhenNoBrick() {
        // Given: No brick set (fresh rotator)
        // When: Get current shape
        int[][] shape = rotator.getCurrentShape();
        // Then: Should return empty array
        assertEquals(0, shape.length, "Should return empty array when no brick");
    }

    @Test
    @DisplayName("getNextShape() returns empty when no brick set")
    void getNextShapeReturnsEmptyWhenNoBrick() {
        // Given: No brick set
        // When: Get next shape
        NextShapeInfo nextShape = rotator.getNextShape();
        // Then: Should return empty info
        assertEquals(0, nextShape.getShape().length, "Shape should be empty");
        assertEquals(0, nextShape.getPosition(), "Position should be 0");
    }

    @Test
    @DisplayName("setCurrentShape() changes rotation position")
    void setCurrentShapeChangesPosition() {
        // Given: Brick at position 0
        rotator.setBrick(createTestBrick());
        // Then: Initially horizontal
        assertEquals(1, rotator.getCurrentShape()[1][0], "Should start horizontal");
        // When: Set to position 1
        rotator.setCurrentShape(1);
        // Then: Should be vertical
        assertEquals(1, rotator.getCurrentShape()[0][1], "Should change to vertical");
    }

    @Test
    @DisplayName("getBrick() returns the set brick")
    void getBrickReturnsSetBrick() {
        // Given: Set a brick
        Brick testBrick = createTestBrick();
        rotator.setBrick(testBrick);
        // When: Get the brick
        Brick result = rotator.getBrick();
        // Then: Should return same brick
        assertSame(testBrick, result, "Should return the same brick instance");
    }

    @Test
    @DisplayName("getBrick() returns null when no brick set")
    void getBrickReturnsNullWhenNoBrick() {
        // Given: No brick set
        // When: Get brick
        Brick result = rotator.getBrick();
        // Then: Should be null
        assertNull(result, "Should return null when no brick set");
    }

    @Test
    @DisplayName("Full rotation cycle: 0 -> 1 -> 0")
    void fullRotationCycleWorks() {
        // Given: Brick at rotation 0
        rotator.setBrick(createTestBrick());
        // Test: 0 -> 1
        rotator.setCurrentShape(0);
        assertEquals(1, rotator.getNextShape().getPosition(), "From 0, next should be 1");
        // Test: 1 -> 0 (wrap)
        rotator.setCurrentShape(1);
        assertEquals(0, rotator.getNextShape().getPosition(), "From 1, next should wrap to 0");
    }

    @Test
    @DisplayName("Multiple setCurrentShape() calls update correctly")
    void multipleSetCurrentShapeCallsWork() {
        // Given: Brick is set
        rotator.setBrick(createTestBrick());
        // Test multiple changes
        rotator.setCurrentShape(0);
        assertEquals(1, rotator.getCurrentShape()[1][0], "Position 0: horizontal");

        rotator.setCurrentShape(1);
        assertEquals(1, rotator.getCurrentShape()[0][1], "Position 1: vertical");

        rotator.setCurrentShape(0);
        assertEquals(1, rotator.getCurrentShape()[1][0], "Back to position 0: horizontal");
    }


    private Brick createTestBrick() {
        return new Brick() {
            @Override
            public java.util.List<int[][]> getShapeMatrix() {
                java.util.List<int[][]> shapes = new java.util.ArrayList<>();

                // Rotation 0: Horizontal
                shapes.add(new int[][]{
                        {0, 0, 0, 0},
                        {1, 1, 1, 1},
                        {0, 0, 0, 0},
                        {0, 0, 0, 0}
                });

                // Rotation 1: Vertical
                shapes.add(new int[][]{
                        {0, 1, 0, 0},
                        {0, 1, 0, 0},
                        {0, 1, 0, 0},
                        {0, 1, 0, 0}
                });

                return shapes;
            }
        };
    }
}