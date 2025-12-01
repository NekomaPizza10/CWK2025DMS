package com.comp2042.state;

import com.comp2042.model.GameMode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.*;

// Tests for GameState - Game state management
class GameStateTest {

    private GameState gameState;

    @BeforeEach
    void setUp() {
        gameState = new GameState();
    }

    @AfterEach
    void tearDown() {
        // Clean up static state
        GameState.setTwoMinutesBestScore(0);
    }

    // ========== Game Mode ==========

    @Test
    @DisplayName("GameState initializes with NORMAL mode")
    void gameStateInitializesWithNormalMode() {
        // Then: Default is NORMAL
        assertEquals(GameMode.NORMAL, gameState.getCurrentGameMode(),
                "Should initialize to NORMAL mode");
    }

    @Test
    @DisplayName("setCurrentGameMode() changes mode")
    void setCurrentGameModeChangesMode() {
        // When: Change to FORTY_LINES
        gameState.setCurrentGameMode(GameMode.FORTY_LINES);
        // Then: Mode changed
        assertEquals(GameMode.FORTY_LINES, gameState.getCurrentGameMode(),
                "Should change to FORTY_LINES");

        // When: Change to TWO_MINUTES
        gameState.setCurrentGameMode(GameMode.TWO_MINUTES);
        // Then: Mode changed
        assertEquals(GameMode.TWO_MINUTES, gameState.getCurrentGameMode(),
                "Should change to TWO_MINUTES");
    }

    // ========== Pause State ==========

    @Test
    @DisplayName("isPaused() initializes to false")
    void isPausedInitializesToFalse() {
        // Then: Not paused initially
        assertFalse(gameState.isPaused(), "Game should not be paused initially");
    }

    @Test
    @DisplayName("setPaused() changes pause state")
    void setPausedChangesPauseState() {
        // When: Pause
        gameState.setPaused(true);
        // Then: Paused
        assertTrue(gameState.isPaused(), "Game should be paused");

        // When: Unpause
        gameState.setPaused(false);
        // Then: Not paused
        assertFalse(gameState.isPaused(), "Game should not be paused");
    }

    // ========== Game Over State ==========

    @Test
    @DisplayName("isGameOver() initializes to false")
    void isGameOverInitializesToFalse() {
        // Then: Not game over initially
        assertFalse(gameState.isGameOver(), "Game should not be over initially");
    }

    @Test
    @DisplayName("setGameOver() changes game over state")
    void setGameOverChangesState() {
        // When: Set game over
        gameState.setGameOver(true);
        // Then: Game over
        assertTrue(gameState.isGameOver(), "Game should be over");

        // When: Reset game over
        gameState.setGameOver(false);
        // Then: Not game over
        assertFalse(gameState.isGameOver(), "Game should not be over");
    }

    // ========== Countdown State ==========

    @Test
    @DisplayName("isCountdownActive() initializes to false")
    void isCountdownActiveInitializesToFalse() {
        // Then: Countdown not active
        assertFalse(gameState.isCountdownActive(), "Countdown should not be active initially");
    }

    @Test
    @DisplayName("setCountdownActive() changes countdown state")
    void setCountdownActiveChangesState() {
        // When: Start countdown
        gameState.setCountdownActive(true);
        // Then: Active
        assertTrue(gameState.isCountdownActive(), "Countdown should be active");

        // When: End countdown
        gameState.setCountdownActive(false);
        // Then: Not active
        assertFalse(gameState.isCountdownActive(), "Countdown should not be active");
    }

    // ========== Challenge Completion ==========

    @Test
    @DisplayName("isChallengeCompleted() initializes to false")
    void isChallengeCompletedInitializesToFalse() {
        // Then: Not completed
        assertFalse(gameState.isChallengeCompleted(),
                "Challenge should not be completed initially");
    }

    @Test
    @DisplayName("setChallengeCompleted() changes completion state")
    void setChallengeCompletedChangesState() {
        // When: Complete challenge
        gameState.setChallengeCompleted(true);
        // Then: Completed
        assertTrue(gameState.isChallengeCompleted(), "Challenge should be completed");
    }

    // ========== Drop Speed ==========

    @Test
    @DisplayName("getCurrentDropSpeed() initializes to 800ms")
    void getCurrentDropSpeedInitializesTo800() {
        // Then: Speed is 800ms
        assertEquals(800, gameState.getCurrentDropSpeed(),
                "Initial drop speed should be 800ms");
    }

    @Test
    @DisplayName("setCurrentDropSpeed() changes speed")
    void setCurrentDropSpeedChangesSpeed() {
        // When: Set speed to 400ms
        gameState.setCurrentDropSpeed(400);
        // Then: Speed changed
        assertEquals(400, gameState.getCurrentDropSpeed(), "Speed should be 400ms");
    }

    @Test
    @DisplayName("Speed constants have expected values")
    void speedConstantsHaveExpectedValues() {
        assertEquals(100, gameState.getMinDropSpeed(), "Min speed should be 100ms");
        assertEquals(70, gameState.getSpeedDecreasePerLevel(), "Decrease should be 70ms");
    }

    // ========== Lock Delay ==========

    @Test
    @DisplayName("isLockDelayActive() initializes to false")
    void isLockDelayActiveInitializesToFalse() {
        // Then: Not active
        assertFalse(gameState.isLockDelayActive(), "Lock delay should not be active initially");
    }

    @Test
    @DisplayName("setLockDelayActive() changes state")
    void setLockDelayActiveChangesState() {
        // When: Activate
        gameState.setLockDelayActive(true);
        // Then: Active
        assertTrue(gameState.isLockDelayActive(), "Lock delay should be active");
    }

    @Test
    @DisplayName("Lock delay reset count tracks correctly")
    void lockDelayResetCountTracksCorrectly() {
        // Given: Initial count is 0
        assertEquals(0, gameState.getLockDelayResetCount(), "Initial count should be 0");

        // When: Increment twice
        gameState.incrementLockDelayResetCount();
        gameState.incrementLockDelayResetCount();
        // Then: Count is 2
        assertEquals(2, gameState.getLockDelayResetCount(), "Count should be 2");

        // When: Reset
        gameState.resetLockDelayCount();
        // Then: Count is 0
        assertEquals(0, gameState.getLockDelayResetCount(), "Count should reset to 0");
    }

    @Test
    @DisplayName("Lock delay constants have expected values")
    void lockDelayConstantsHaveExpectedValues() {
        assertEquals(10, gameState.getMaxLockResets(), "Max resets should be 10");
        assertEquals(500, gameState.getLockDelayMs(), "Lock delay should be 500ms");
    }

    // ========== Normal Mode Scoring ==========

    @Test
    @DisplayName("Normal mode score initializes to 0")
    void normalModeScoreInitializesToZero() {
        // Then: Score is 0
        assertEquals(0, gameState.getNormalModeScore(), "Normal score should be 0");
    }

    @Test
    @DisplayName("setNormalModeScore() changes score")
    void setNormalModeScoreChangesScore() {
        // When: Set to 1000
        gameState.setNormalModeScore(1000);

        // Then: Score changed
        assertEquals(1000, gameState.getNormalModeScore(), "Normal score should be 1000");
    }

    @Test
    @DisplayName("Normal mode combo increments correctly")
    void normalModeComboIncrementsCorrectly() {
        // Given: Initial combo is 0
        assertEquals(0, gameState.getNormalModeCombo(), "Initial combo should be 0");

        // When: Increment twice
        gameState.incrementNormalModeCombo();
        gameState.incrementNormalModeCombo();
        // Then: Combo is 2
        assertEquals(2, gameState.getNormalModeCombo(), "Combo should be 2");

        // When: Reset
        gameState.resetNormalModeCombo();
        // Then: Combo is 0
        assertEquals(0, gameState.getNormalModeCombo(), "Combo should reset to 0");
    }

    @Test
    @DisplayName("Normal mode back-to-back flag works")
    void normalModeBackToBackFlagWorks() {
        // Given: Initial is false
        assertFalse(gameState.isNormalModeLastWasTetris(), "Initial back-to-back should be false");

        // When: Set to true
        gameState.setNormalModeLastWasTetris(true);
        // Then: Is true
        assertTrue(gameState.isNormalModeLastWasTetris(), "Back-to-back should be true");

        // When: Set to false
        gameState.setNormalModeLastWasTetris(false);
        // Then: Is false
        assertFalse(gameState.isNormalModeLastWasTetris(), "Back-to-back should be false");
    }

    // ========== Two Minutes Mode Scoring ==========

    @Test
    @DisplayName("Two Minutes score initializes to 0")
    void twoMinutesScoreInitializesToZero() {
        // Then: Score is 0
        assertEquals(0, gameState.getTwoMinutesScore(), "Two Minutes score should be 0");
    }

    @Test
    @DisplayName("setTwoMinutesScore() changes score")
    void setTwoMinutesScoreChangesScore() {
        // When: Set to 2500
        gameState.setTwoMinutesScore(2500);
        // Then: Score changed
        assertEquals(2500, gameState.getTwoMinutesScore(), "Two Minutes score should be 2500");
    }

    @Test
    @DisplayName("Two Minutes combo is independent")
    void twoMinutesComboIsIndependent() {
        // When: Increment both
        gameState.incrementNormalModeCombo();
        gameState.incrementNormalModeCombo();
        gameState.incrementTwoMinutesCombo();
        // Then: Independent values
        assertEquals(2, gameState.getNormalModeCombo(), "Normal combo should be 2");
        assertEquals(1, gameState.getTwoMinutesCombo(), "Two Minutes combo should be 1");
    }

    @Test
    @DisplayName("Two Minutes best score persists")
    void twoMinutesBestScorePersists() {
        // Given: Initial is 0
        assertEquals(0, GameState.getTwoMinutesBestScore(), "Initial best should be 0");
        // When: Set best
        GameState.setTwoMinutesBestScore(5000);
        // Then: Best is 5000
        assertEquals(5000, GameState.getTwoMinutesBestScore(), "Best should be 5000");

        // When: Create new instance
        GameState newState = new GameState();
        // Then: Still accessible (static)
        assertEquals(5000, GameState.getTwoMinutesBestScore(), "Best should persist (static)");
    }

    // ========== Forty Lines Mode ==========

    @Test
    @DisplayName("Forty Lines best time initializes to MAX_VALUE")
    void fortyLinesBestTimeInitializesToMaxValue() {
        // Then: Best time is MAX_VALUE
        assertEquals(Long.MAX_VALUE, gameState.getFortyLinesBestTime(),
                "Initial best time should be MAX_VALUE");
    }

    @Test
    @DisplayName("setFortyLinesBestTime() changes time")
    void setFortyLinesBestTimeChangesTime() {
        // When: Set to 45 seconds
        gameState.setFortyLinesBestTime(45000);
        // Then: Time changed
        assertEquals(45000, gameState.getFortyLinesBestTime(), "Best time should be 45000ms");
    }

    // ========== resetScores() ==========

    @Test
    @DisplayName("resetScores() resets all mode scores")
    void resetScoresResetsAllModeScores() {
        // Given: Set up scores
        gameState.setNormalModeScore(1000);
        gameState.incrementNormalModeCombo();
        gameState.setNormalModeLastWasTetris(true);

        gameState.setTwoMinutesScore(2000);
        gameState.incrementTwoMinutesCombo();
        gameState.setTwoMinutesLastWasTetris(true);

        // When: Reset
        gameState.resetScores();
        // Then: Normal mode reset
        assertEquals(0, gameState.getNormalModeScore(), "Normal score should reset");
        assertEquals(0, gameState.getNormalModeCombo(), "Normal combo should reset");
        assertFalse(gameState.isNormalModeLastWasTetris(), "Normal back-to-back should reset");

        // Then: Two Minutes mode reset
        assertEquals(0, gameState.getTwoMinutesScore(), "Two Minutes score should reset");
        assertEquals(0, gameState.getTwoMinutesCombo(), "Two Minutes combo should reset");
        assertFalse(gameState.isTwoMinutesLastWasTetris(), "Two Minutes back-to-back should reset");
    }

    // ========== Integration ==========

    @Test
    @DisplayName("Mode independence: scores stay separate")
    void modeIndependenceScoresStaySeparate() {
        // Normal mode gameplay
        gameState.setCurrentGameMode(GameMode.NORMAL);
        gameState.setNormalModeScore(5000);
        gameState.incrementNormalModeCombo();
        gameState.incrementNormalModeCombo();

        // Switch to Two Minutes
        gameState.setCurrentGameMode(GameMode.TWO_MINUTES);
        gameState.setTwoMinutesScore(3000);
        gameState.incrementTwoMinutesCombo();

        // Verify independence
        assertEquals(GameMode.TWO_MINUTES, gameState.getCurrentGameMode(), "Mode should be TWO_MINUTES");
        assertEquals(5000, gameState.getNormalModeScore(), "Normal score preserved");
        assertEquals(2, gameState.getNormalModeCombo(), "Normal combo preserved");
        assertEquals(3000, gameState.getTwoMinutesScore(), "Two Minutes score tracked");
        assertEquals(1, gameState.getTwoMinutesCombo(), "Two Minutes combo independent");
    }
}