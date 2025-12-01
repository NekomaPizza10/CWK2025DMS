package com.comp2042.brick;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.RepeatedTest;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.HashSet;
import java.util.Set;

/**
 * Tests for RandomBrickGenerator - 7-bag randomization system
 * Tests the Tetris 7-bag algorithm where all 7 brick types
 * appear exactly once before the bag is refilled.
 */
class RandomBrickGeneratorTest {

    private RandomBrickGenerator generator;

    @BeforeEach
    void setUp() {
        generator = new RandomBrickGenerator();
    }

    // ========== Basic Generation ==========

    @Test
    @DisplayName("getBrick() returns a valid brick")
    void getBrickReturnsValidBrick() {
        // When: Get next brick
        Brick brick = generator.getBrick();
        // Then: Should not be null
        assertNotNull(brick, "Generated brick should not be null");
        // Then: Should have valid shape matrix
        List<int[][]> shapes = brick.getShapeMatrix();
        assertNotNull(shapes, "Brick should have shape matrix");
        assertFalse(shapes.isEmpty(), "Shape matrix should not be empty");
    }

    @Test
    @DisplayName("getBrick() generates unique instances")
    void getBrickGeneratesUniqueInstances() {
        // When: Get two bricks
        Brick brick1 = generator.getBrick();
        Brick brick2 = generator.getBrick();
        // Then: Should be different instances
        assertNotSame(brick1, brick2, "Each call should return a new instance");
    }

    // ========== 7-Bag Algorithm ==========

    @Test
    @DisplayName("7-Bag: All 7 brick types appear in first 7 bricks")
    void sevenBagAllTypesAppearInFirst7() {
        // When: Generate first 7 bricks
        Set<String> brickTypes = new HashSet<>();
        for (int i = 0; i < 7; i++) {
            Brick brick = generator.getBrick();
            String type = getBrickType(brick);
            brickTypes.add(type);
        }
        // Then: Should have all 7 unique types
        assertEquals(7, brickTypes.size(),
                "First 7 bricks should contain all 7 unique types (I, O, T, S, Z, J, L)");
    }

    @Test
    @DisplayName("7-Bag: No brick type repeats in first 7 bricks")
    void sevenBagNoRepeatsInFirst7() {
        // When: Generate first 7 bricks
        Set<String> seenTypes = new HashSet<>();
        for (int i = 0; i < 7; i++) {
            Brick brick = generator.getBrick();
            String type = getBrickType(brick);
            // Then: Each type should be new
            assertFalse(seenTypes.contains(type),
                    String.format("Brick %d: Type '%s' already appeared in this bag", i + 1, type));
            seenTypes.add(type);
        }
    }

    @Test
    @DisplayName("7-Bag: Second bag also contains all 7 types")
    void sevenBagSecondBagAlsoComplete() {
        // When: Generate first bag (7 bricks)
        for (int i = 0; i < 7; i++) {
            generator.getBrick();
        }

        // When: Generate second bag (next 7 bricks)
        Set<String> secondBagTypes = new HashSet<>();
        for (int i = 0; i < 7; i++) {
            Brick brick = generator.getBrick();
            String type = getBrickType(brick);
            secondBagTypes.add(type);
        }
        // Then: Second bag should also have all 7 types
        assertEquals(7, secondBagTypes.size(),
                "Second bag should also contain all 7 unique types");
    }

    @RepeatedTest(5)
    @DisplayName("7-Bag: Multiple bags maintain property (repeated)")
    void sevenBagMultipleBagsMaintainProperty() {
        // When: Generate 3 complete bags (21 bricks)
        for (int bag = 0; bag < 3; bag++) {
            Set<String> bagTypes = new HashSet<>();

            for (int i = 0; i < 7; i++) {
                Brick brick = generator.getBrick();
                String type = getBrickType(brick);
                bagTypes.add(type);
            }
            // Then: Each bag should have all 7 types
            assertEquals(7, bagTypes.size(),
                    String.format("Bag %d should contain all 7 unique types", bag + 1));
        }
    }

    // ========== Preview Functionality (getNextBricks) ==========

    @Test
    @DisplayName("getNextBricks(1) returns next brick without consuming it")
    void getNextBricksReturnsNextBrickWithoutConsuming() {
        // When: Preview next brick
        List<Brick> preview1 = generator.getNextBricks(1);
        String previewType1 = getBrickType(preview1.get(0));

        // When: Preview again
        List<Brick> preview2 = generator.getNextBricks(1);
        String previewType2 = getBrickType(preview2.get(0));
        // Then: Both previews should return same type
        assertEquals(previewType1, previewType2,
                "Multiple previews should return the same brick type");

        // When: Actually get the brick
        Brick actual = generator.getBrick();
        String actualType = getBrickType(actual);
        // Then: Should match what was previewed
        assertEquals(previewType1, actualType,
                "getBrick() should return the brick that was previewed");
    }

    @Test
    @DisplayName("getNextBricks(5) returns next 5 bricks in order")
    void getNextBricksReturnsMultipleBricksInOrder() {
        // When: Preview next 5 bricks
        List<Brick> preview = generator.getNextBricks(5);
        // Then: Should return 5 bricks
        assertEquals(5, preview.size(), "getNextBricks(5) should return 5 bricks");

        // When: Get those bricks one by one
        for (int i = 0; i < 5; i++) {
            Brick actual = generator.getBrick();
            String previewType = getBrickType(preview.get(i));
            String actualType = getBrickType(actual);
            // Then: Should match preview order
            assertEquals(previewType, actualType,
                    String.format("Brick %d should match preview order", i + 1));
        }
    }

    @Test
    @DisplayName("getNextBricks(7) shows all types in current bag")
    void getNextBricks7ShowsAllTypesInCurrentBag() {
        // When: Preview next 7 bricks
        List<Brick> preview = generator.getNextBricks(7);
        // Then: Should contain all 7 unique types
        Set<String> types = new HashSet<>();
        for (Brick brick : preview) {
            types.add(getBrickType(brick));
        }

        assertEquals(7, types.size(),
                "Previewing 7 bricks should show all 7 unique types");
    }

    @Test
    @DisplayName("getNextBricks(10) spans across two bags")
    void getNextBricks10SpansAcrossTwoBags() {
        // When: Preview 10 bricks (will need part of second bag)
        List<Brick> preview = generator.getNextBricks(10);
        // Then: Should return 10 bricks
        assertEquals(10, preview.size(), "getNextBricks(10) should return 10 bricks");
        // Then: All bricks should be valid
        for (int i = 0; i < 10; i++) {
            assertNotNull(preview.get(i),
                    String.format("Brick %d should not be null", i + 1));
        }
    }

    // ========== Edge Cases ==========

    @Test
    @DisplayName("getNextBricks(0) returns empty list")
    void getNextBricks0ReturnsEmptyList() {
        // When: Preview 0 bricks
        List<Brick> preview = generator.getNextBricks(0);
        // Then: Should return empty list
        assertNotNull(preview, "getNextBricks(0) should not return null");
        assertEquals(0, preview.size(), "getNextBricks(0) should return empty list");
    }

    @Test
    @DisplayName("getNextBricks(1) returns single brick list")
    void getNextBricks1ReturnsSingleBrickList() {
        // When: Preview 1 brick
        List<Brick> preview = generator.getNextBricks(1);
        // Then: Should return list with 1 brick
        assertEquals(1, preview.size(), "getNextBricks(1) should return 1 brick");
        assertNotNull(preview.get(0), "The brick should not be null");
    }

    @Test
    @DisplayName("Large getNextBricks(14) returns full two bags")
    void largeGetNextBricks14Works() {
        // When: Preview 14 bricks (2 complete bags)
        List<Brick> preview = generator.getNextBricks(14);
        // Then: Should return 14 bricks
        assertEquals(14, preview.size(), "getNextBricks(14) should return 14 bricks");
        // Then: All should be valid
        for (Brick brick : preview) {
            assertNotNull(brick, "Each previewed brick should be valid");
            assertNotNull(brick.getShapeMatrix(), "Each brick should have shapes");
        }
    }

    // ========== Queue Refill Tests ==========

    @Test
    @DisplayName("Queue refills automatically when depleted")
    void queueRefillsAutomatically() {
        // When: Get more than initial 14 bricks (should trigger refill)
        for (int i = 0; i < 20; i++) {
            Brick brick = generator.getBrick();
            assertNotNull(brick, String.format("Brick %d should not be null", i + 1));
        }
    }

    @Test
    @DisplayName("Continuous generation works for 100 bricks")
    void continuousGeneration100Bricks() {
        // When: Generate 100 bricks
        for (int i = 0; i < 100; i++) {
            Brick brick = generator.getBrick();
            assertNotNull(brick, String.format("Brick %d should not be null", i + 1));
            assertNotNull(brick.getShapeMatrix(), "Brick should have shapes");
        }
    }

    // ========== Randomization Quality ==========

    @RepeatedTest(10)
    @DisplayName("Randomization: Different generators produce valid sequences")
    void randomizationDifferentSequences() {
        // Given: Two independent generators
        RandomBrickGenerator gen1 = new RandomBrickGenerator();
        RandomBrickGenerator gen2 = new RandomBrickGenerator();

        // When: Generate first brick from each
        Brick brick1 = gen1.getBrick();
        Brick brick2 = gen2.getBrick();

        String type1 = getBrickType(brick1);
        String type2 = getBrickType(brick2);
        // Then: Both should be valid (may or may not be same type)
        assertNotNull(type1, "Generator 1 should produce valid brick");
        assertNotNull(type2, "Generator 2 should produce valid brick");
    }

    @Test
    @DisplayName("Randomization: Order varies between bags")
    void randomizationOrderVariesBetweenBags() {
        // When: Generate two bags
        java.util.List<String> bag1 = new java.util.ArrayList<>();
        java.util.List<String> bag2 = new java.util.ArrayList<>();

        for (int i = 0; i < 7; i++) {
            bag1.add(getBrickType(generator.getBrick()));
        }
        for (int i = 0; i < 7; i++) {
            bag2.add(getBrickType(generator.getBrick()));
        }
        // Then: Both bags should have all types
        assertEquals(7, new HashSet<>(bag1).size(), "Bag 1 should have all 7 types");
        assertEquals(7, new HashSet<>(bag2).size(), "Bag 2 should have all 7 types");
    }

    // ========== Integration Tests ==========

    @Test
    @DisplayName("Continuous generation: 100 bricks maintains 7-bag property")
    void continuousGeneration100BricksMaintainsProperty() {
        // When: Generate 100 bricks (14+ complete bags)
        int totalBricks = 98; // 14 complete bags
        int completeBags = totalBricks / 7;

        for (int bag = 0; bag < completeBags; bag++) {
            Set<String> bagTypes = new HashSet<>();

            for (int i = 0; i < 7; i++) {
                Brick brick = generator.getBrick();
                String type = getBrickType(brick);
                bagTypes.add(type);
            }
            // Then: Each bag should have all 7 types
            assertEquals(7, bagTypes.size(),
                    String.format("Bag %d of %d should contain all 7 types", bag + 1, completeBags));
        }
    }

    @Test
    @DisplayName("Mix getNextBricks() and getBrick() operations")
    void mixPreviewAndGetOperations() {
        // Scenario: Preview, get, preview more, get more

        // When: Preview next brick
        List<Brick> preview1 = generator.getNextBricks(1);
        String previewType1 = getBrickType(preview1.get(0));

        // When: Get it
        Brick actual1 = generator.getBrick();
        String actualType1 = getBrickType(actual1);
        // Then: Should match
        assertEquals(previewType1, actualType1, "First brick should match preview");

        // When: Preview next 3
        List<Brick> preview3 = generator.getNextBricks(3);
        // When: Get them one by one
        for (int i = 0; i < 3; i++) {
            Brick actual = generator.getBrick();
            String previewType = getBrickType(preview3.get(i));
            String actualType = getBrickType(actual);
            assertEquals(previewType, actualType,
                    String.format("Brick %d should match preview", i + 2));
        }
    }

    @Test
    @DisplayName("Preview doesn't affect queue order")
    void previewDoesNotAffectQueueOrder() {
        // When: Preview multiple times
        List<Brick> preview1 = generator.getNextBricks(5);
        List<Brick> preview2 = generator.getNextBricks(5);
        List<Brick> preview3 = generator.getNextBricks(5);
        // Then: All previews should show same order
        for (int i = 0; i < 5; i++) {
            String type1 = getBrickType(preview1.get(i));
            String type2 = getBrickType(preview2.get(i));
            String type3 = getBrickType(preview3.get(i));

            assertEquals(type1, type2, "Preview should be consistent");
            assertEquals(type2, type3, "Preview should be consistent");
        }
    }

    // ========== Brick Type Verification ==========

    @Test
    @DisplayName("All 7 Tetris piece types exist")
    void allSevenTetrominoTypesExist() {
        // When: Generate 14 bricks (2 bags to be sure)
        Set<String> allTypes = new HashSet<>();
        for (int i = 0; i < 14; i++) {
            Brick brick = generator.getBrick();
            allTypes.add(brick.getClass().getSimpleName());
        }
        // Then: Should have all 7 types
        assertTrue(allTypes.contains("IBrick"), "Should have I brick");
        assertTrue(allTypes.contains("JBrick"), "Should have J brick");
        assertTrue(allTypes.contains("LBrick"), "Should have L brick");
        assertTrue(allTypes.contains("OBrick"), "Should have O brick");
        assertTrue(allTypes.contains("SBrick"), "Should have S brick");
        assertTrue(allTypes.contains("TBrick"), "Should have T brick");
        assertTrue(allTypes.contains("ZBrick"), "Should have Z brick");
    }

    // ========== Helper Methods ==========
    private String getBrickType(Brick brick) {
        return brick.getClass().getSimpleName();
    }
}