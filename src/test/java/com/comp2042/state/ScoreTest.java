package com.comp2042.state;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

// Tests for Score class
class ScoreTest {

    private Score score;

    @BeforeEach
    void setUp() {
        score = new Score();
    }

    @Test
    @DisplayName("Score initializes to 0")
    void scoreInitializesToZero() {
        // Then: Initial score is 0
        assertEquals(0, score.scoreProperty().get(), "Score should start at 0");
    }

    @Test
    @DisplayName("add() increases score by given amount")
    void addIncreasesScore() {
        // When: Add 100 points
        score.add(100);
        // Then: Score should be 100
        assertEquals(100, score.scoreProperty().get(), "Score should be 100 after adding 100");
        // When: Add 50 more
        score.add(50);
        // Then: Score should be 150
        assertEquals(150, score.scoreProperty().get(), "Score should be 150 after adding 50");
    }

    @Test
    @DisplayName("add() handles large values")
    void addHandlesLargeValues() {
        // When: Add 1 million points
        score.add(1000000);
        // Then: Score should reflect large value
        assertEquals(1000000, score.scoreProperty().get(), "Should handle large values (1,000,000)");
    }

    @Test
    @DisplayName("add() accumulates multiple additions")
    void addAccumulatesMultipleValues() {
        // When: Add several values
        score.add(10);
        score.add(20);
        score.add(30);
        score.add(40);
        // Then: Total should be sum
        assertEquals(100, score.scoreProperty().get(), "10+20+30+40 should equal 100");
    }

    @Test
    @DisplayName("reset() sets score back to 0")
    void resetSetsScoreToZero() {
        // Given: Score is 500
        score.add(500);
        assertEquals(500, score.scoreProperty().get(), "Score should be 500 before reset");
        // When: Reset
        score.reset();
        // Then: Score should be 0
        assertEquals(0, score.scoreProperty().get(), "Score should be 0 after reset");
    }

    @Test
    @DisplayName("reset() can be called multiple times")
    void resetWorksMultipleTimes() {
        // First game
        score.add(100);
        score.reset();
        assertEquals(0, score.scoreProperty().get(), "First reset should work");

        // Second game
        score.add(200);
        score.reset();
        assertEquals(0, score.scoreProperty().get(), "Second reset should work");

        // Third game
        score.add(300);
        score.reset();
        assertEquals(0, score.scoreProperty().get(), "Third reset should work");
    }

    @Test
    @DisplayName("add(0) does not change score")
    void addZeroDoesNotChangeScore() {
        // Given: Score is 100
        score.add(100);
        // When: Add 0
        score.add(0);
        // Then: Score unchanged
        assertEquals(100, score.scoreProperty().get(), "Adding 0 should not change score");
    }

    @Test
    @DisplayName("add() with negative value decreases score")
    void addNegativeDecreasesScore() {
        // Given: Score is 100
        score.add(100);
        // When: Add -25 (penalty)
        score.add(-25);
        // Then: Score decreases
        assertEquals(75, score.scoreProperty().get(), "100 - 25 should equal 75");
    }

    @Test
    @DisplayName("Score can become negative")
    void scoreCanBeNegative() {
        // Given: Score is 10
        score.add(10);
        // When: Apply large penalty
        score.add(-50);
        // Then: Score is negative
        assertEquals(-40, score.scoreProperty().get(), "10 - 50 should equal -40");
    }

    @Test
    @DisplayName("scoreProperty() is not null")
    void scorePropertyIsNotNull() {
        // Then: Property should exist
        assertNotNull(score.scoreProperty(), "scoreProperty() should not return null");
    }

    @Test
    @DisplayName("scoreProperty() reflects add() changes")
    void scorePropertyReflectsChanges() {
        // Given: Get property reference
        var property = score.scoreProperty();
        // When: Add 75 points
        score.add(75);
        // Then: Property reflects change
        assertEquals(75, property.get(), "Property should reflect score changes");
    }
}