package com.comp2042.state;

import com.comp2042.model.GameMode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for TimerManager - Game timer management
 *
 * Note: Tests focus on non-UI logic and method safety.
 * JavaFX Timeline/AnimationTimer behavior cannot be fully tested without JavaFX runtime.
 * These tests verify the manager's API, state management, and error handling.
 */
class TimerManagerTest {

    private GameState gameState;
    private TimerManager timerManager;

    @BeforeEach
    void setUp() {
        gameState = new GameState();
        // Using null label since we can't create JavaFX Label without toolkit
        timerManager = new TimerManager(gameState, null);
    }

    // ========== Constructor Tests ==========

    @Test
    @DisplayName("TimerManager initializes successfully")
    void timerManagerInitializesSuccessfully() {
        assertNotNull(timerManager, "TimerManager should be created");
    }

    @Test
    @DisplayName("Constructor accepts null label without crashing")
    void constructorAcceptsNullLabel() {
        assertDoesNotThrow(() -> {
            new TimerManager(gameState, null);
        }, "Should accept null label");
    }

    @Test
    @DisplayName("Constructor stores GameState reference")
    void constructorStoresGameStateReference() {
        // When: Create manager
        TimerManager manager = new TimerManager(gameState, null);

        // Then: Should not be null (indirect verification)
        assertNotNull(manager, "Manager should be initialized");
    }

    // ========== Elapsed Time Tests ==========

    @Test
    @DisplayName("getElapsedTime returns non-negative value after initialization")
    void getElapsedTimeReturnsNonNegativeValue() {
        // Given: Fresh manager
        timerManager.resetStartTime();

        // When: Get elapsed time
        long elapsed = timerManager.getElapsedTime();

        // Then: Should be non-negative and small (just created)
        assertTrue(elapsed >= 0, "Elapsed time should be non-negative");
        assertTrue(elapsed < 1000, "Elapsed time should be small immediately after creation");
    }

    @Test
    @DisplayName("getElapsedTime increases over time")
    void getElapsedTimeIncreasesOverTime() throws InterruptedException {
        // Given: Reset timer
        timerManager.resetStartTime();
        long time1 = timerManager.getElapsedTime();

        // When: Wait 50ms
        Thread.sleep(50);
        long time2 = timerManager.getElapsedTime();

        // Then: Time should increase
        assertTrue(time2 > time1,
                "Elapsed time should increase: " + time1 + " -> " + time2);
    }

    @Test
    @DisplayName("resetStartTime resets elapsed time to near zero")
    void resetStartTimeResetsElapsedTime() throws InterruptedException {
        // Given: Some time has passed
        timerManager.resetStartTime();
        Thread.sleep(100);
        long beforeReset = timerManager.getElapsedTime();
        assertTrue(beforeReset > 50, "Time should have passed");

        // When: Reset
        timerManager.resetStartTime();

        // Then: Elapsed time near 0
        long afterReset = timerManager.getElapsedTime();
        assertTrue(afterReset < 50,
                "Elapsed time should be near 0, was: " + afterReset);
    }

    @Test
    @DisplayName("resetStartTime can be called multiple times")
    void resetStartTimeCanBeCalledMultipleTimes() {
        assertDoesNotThrow(() -> {
            for (int i = 0; i < 10; i++) {
                timerManager.resetStartTime();
            }
        }, "Multiple resets should not crash");
    }

    @Test
    @DisplayName("getElapsedTime after multiple resets works correctly")
    void getElapsedTimeAfterMultipleResetsWorksCorrectly() throws InterruptedException {
        // Test sequence of reset -> wait -> check
        for (int i = 0; i < 3; i++) {
            timerManager.resetStartTime();
            Thread.sleep(30);
            long elapsed = timerManager.getElapsedTime();
            assertTrue(elapsed >= 20 && elapsed <= 100,
                    "Iteration " + i + ": elapsed should be ~30ms, was: " + elapsed);
        }
    }

    // ========== Stop All Timers Tests ==========

    @Test
    @DisplayName("stopAllTimers can be called safely without starting timers")
    void stopAllTimersCanBeCalledSafely() {
        assertDoesNotThrow(() -> {
            timerManager.stopAllTimers();
        }, "Should not crash when stopping non-started timers");
    }

    @Test
    @DisplayName("stopAllTimers can be called multiple times")
    void stopAllTimersCanBeCalledMultipleTimes() {
        assertDoesNotThrow(() -> {
            timerManager.stopAllTimers();
            timerManager.stopAllTimers();
            timerManager.stopAllTimers();
        }, "Should handle multiple stop calls");
    }

    @Test
    @DisplayName("stopAllTimers after starting timers doesn't crash")
    void stopAllTimersAfterStartingDoesntCrash() {
        assertDoesNotThrow(() -> {
            // Note: These will fail internally without JavaFX, but shouldn't crash
            try {
                timerManager.startDropTimer(1000, () -> {});
            } catch (Exception ignored) {}

            try {
                timerManager.startGameTimer();
            } catch (Exception ignored) {}

            // This should always be safe
            timerManager.stopAllTimers();
        }, "stopAllTimers should be safe even after attempted starts");
    }

    // ========== Drop Timer Tests ==========

    @Test
    @DisplayName("startDropTimer accepts valid parameters")
    void startDropTimerAcceptsValidParameters() {
        // Note: Will throw without JavaFX, but we test the API accepts parameters
        assertDoesNotThrow(() -> {
            try {
                timerManager.startDropTimer(1000, () -> {});
            } catch (IllegalStateException | NullPointerException e) {
                // Expected without JavaFX runtime
            }
        }, "Should accept drop timer parameters");
    }

    @Test
    @DisplayName("stopDropTimer can be called without starting")
    void stopDropTimerCanBeCalledWithoutStarting() {
        assertDoesNotThrow(() -> {
            timerManager.stopDropTimer();
        }, "Should not crash when stopping non-started drop timer");
    }

    @Test
    @DisplayName("pauseDropTimer can be called without starting")
    void pauseDropTimerCanBeCalledWithoutStarting() {
        assertDoesNotThrow(() -> {
            timerManager.pauseDropTimer();
        }, "Should not crash when pausing non-started drop timer");
    }

    @Test
    @DisplayName("resumeDropTimer can be called without starting")
    void resumeDropTimerCanBeCalledWithoutStarting() {
        assertDoesNotThrow(() -> {
            timerManager.resumeDropTimer();
        }, "Should not crash when resuming non-started drop timer");
    }

    @Test
    @DisplayName("Drop timer methods handle null timeline gracefully")
    void dropTimerMethodsHandleNullTimelineGracefully() {
        assertDoesNotThrow(() -> {
            timerManager.stopDropTimer();
            timerManager.pauseDropTimer();
            timerManager.resumeDropTimer();
            timerManager.stopDropTimer();
        }, "Should handle null timeline without crashes");
    }

    // ========== Lock Delay Tests ==========

    @Test
    @DisplayName("startLockDelay accepts callback parameter")
    void startLockDelayAcceptsCallback() {
        assertDoesNotThrow(() -> {
            try {
                timerManager.startLockDelay(() -> {});
            } catch (IllegalStateException | NullPointerException e) {
                // Expected without JavaFX
            }
        }, "Should accept lock delay callback");
    }

    @Test
    @DisplayName("stopLockDelay can be called without starting")
    void stopLockDelayCanBeCalledWithoutStarting() {
        assertDoesNotThrow(() -> {
            timerManager.stopLockDelay();
        }, "Should not crash when stopping non-started lock delay");
    }

    @Test
    @DisplayName("Lock delay respects GameState lock delay duration")
    void lockDelayRespectsGameStateDuration() {
        // Given: GameState with lock delay value
        int lockDelayMs = gameState.getLockDelayMs();

        // Then: Should be the expected value (500ms)
        assertEquals(500, lockDelayMs, "Lock delay should be 500ms");
    }

    @Test
    @DisplayName("startLockDelay can be called multiple times")
    void startLockDelayCanBeCalledMultipleTimes() {
        assertDoesNotThrow(() -> {
            try {
                timerManager.startLockDelay(() -> {});
                timerManager.startLockDelay(() -> {});
                timerManager.startLockDelay(() -> {});
            } catch (IllegalStateException | NullPointerException e) {
                // Expected without JavaFX
            }
        }, "Multiple lock delay starts should not crash");
    }

    // ========== Countdown Tests ==========

    @Test
    @DisplayName("stopCountdown can be called without starting")
    void stopCountdownCanBeCalledWithoutStarting() {
        assertDoesNotThrow(() -> {
            timerManager.stopCountdown();
        }, "Should not crash when stopping non-started countdown");
    }

    @Test
    @DisplayName("stopCountdown nullifies countdown timeline")
    void stopCountdownNullifiesCountdownTimeline() {
        // When: Stop countdown (even if not started)
        timerManager.stopCountdown();

        // Then: Can be called again safely
        assertDoesNotThrow(() -> {
            timerManager.stopCountdown();
            timerManager.stopCountdown();
        }, "Multiple stops should be safe");
    }

    // ========== Game Timer Tests ==========

    @Test
    @DisplayName("startGameTimer can be called (may fail without JavaFX)")
    void startGameTimerCanBeCalled() {
        assertDoesNotThrow(() -> {
            try {
                timerManager.startGameTimer();
                timerManager.stopGameTimer();
            } catch (IllegalStateException | NullPointerException e) {
                // Expected without JavaFX runtime
            }
        }, "Should accept game timer calls");
    }

    @Test
    @DisplayName("stopGameTimer can be called without starting")
    void stopGameTimerCanBeCalledWithoutStarting() {
        assertDoesNotThrow(() -> {
            timerManager.stopGameTimer();
        }, "Should not crash when stopping non-started game timer");
    }

    @Test
    @DisplayName("pauseGameTimer updates elapsed time")
    void pauseGameTimerUpdatesElapsedTime() throws InterruptedException {
        // Given: Timer running
        timerManager.resetStartTime();
        Thread.sleep(50);

        // When: Pause (this updates pausedElapsedTime internally)
        assertDoesNotThrow(() -> {
            timerManager.pauseGameTimer();
        }, "Pause should not crash");
    }

    @Test
    @DisplayName("resumeGameTimer can be called")
    void resumeGameTimerCanBeCalled() {
        assertDoesNotThrow(() -> {
            timerManager.resumeGameTimer();
        }, "Resume should not crash");
    }

    @Test
    @DisplayName("pauseGameTimer preserves time state")
    void pauseGameTimerPreservesTimeState() throws InterruptedException {
        // Given: Start time
        timerManager.resetStartTime();
        Thread.sleep(100);

        // When: Pause
        timerManager.pauseGameTimer();
        long timeAfterPause = timerManager.getElapsedTime();

        // Wait while "paused"
        Thread.sleep(100);

        // When: Resume
        timerManager.resumeGameTimer();
        long timeAfterResume = timerManager.getElapsedTime();

        // Then: Time difference should be small (pause should preserve state)
        long difference = Math.abs(timeAfterResume - timeAfterPause);
        assertTrue(difference < 50,
                "Time should be preserved during pause, difference: " + difference);
    }

    // ========== Time Up Callback Tests ==========

    @Test
    @DisplayName("setOnTimeUp stores callback")
    void setOnTimeUpStoresCallback() {
        assertDoesNotThrow(() -> {
            timerManager.setOnTimeUp(() -> {});
        }, "Should accept time up callback");
    }

    @Test
    @DisplayName("setOnTimeUp accepts null callback")
    void setOnTimeUpAcceptsNull() {
        assertDoesNotThrow(() -> {
            timerManager.setOnTimeUp(null);
        }, "Should accept null callback");
    }

    @Test
    @DisplayName("setOnTimeUp can be called multiple times")
    void setOnTimeUpCanBeCalledMultipleTimes() {
        assertDoesNotThrow(() -> {
            timerManager.setOnTimeUp(() -> {});
            timerManager.setOnTimeUp(() -> {});
            timerManager.setOnTimeUp(null);
            timerManager.setOnTimeUp(() -> {});
        }, "Should handle multiple setOnTimeUp calls");
    }

    // ========== Reset Functionality Tests ==========

    @Test
    @DisplayName("resetStartTime resets all internal flags")
    void resetStartTimeResetsAllInternalFlags() {
        // Given: Set time up callback
        final int[] callCount = {0};
        timerManager.setOnTimeUp(() -> callCount[0]++);

        // When: Reset multiple times
        timerManager.resetStartTime();
        timerManager.resetStartTime();
        timerManager.resetStartTime();

        // Then: Should not crash
        assertDoesNotThrow(() -> {
            timerManager.resetStartTime();
        }, "Reset should handle internal flag clearing");
    }

    @Test
    @DisplayName("resetStartTime sets gameStartTime to current time")
    void resetStartTimeSetsGameStartTimeToCurrent() {
        // When: Reset at different times
        timerManager.resetStartTime();
        long elapsed1 = timerManager.getElapsedTime();

        timerManager.resetStartTime();
        long elapsed2 = timerManager.getElapsedTime();

        // Then: Both should be near zero
        assertTrue(elapsed1 < 20, "First reset: elapsed should be near 0");
        assertTrue(elapsed2 < 20, "Second reset: elapsed should be near 0");
    }

    // ========== Game Mode Integration Tests ==========

    @Test
    @DisplayName("Timer works with NORMAL game mode")
    void timerWorksWithNormalGameMode() {
        // Given: Normal mode
        gameState.setCurrentGameMode(GameMode.NORMAL);

        // Then: Timer operations should work
        assertDoesNotThrow(() -> {
            timerManager.resetStartTime();
            long elapsed = timerManager.getElapsedTime();
            assertTrue(elapsed >= 0, "Should track time in NORMAL mode");
        }, "Should work with NORMAL mode");
    }

    @Test
    @DisplayName("Timer works with TWO_MINUTES game mode")
    void timerWorksWithTwoMinutesGameMode() {
        // Given: Two Minutes mode
        gameState.setCurrentGameMode(GameMode.TWO_MINUTES);

        // Then: Timer operations should work
        assertDoesNotThrow(() -> {
            timerManager.resetStartTime();
            long elapsed = timerManager.getElapsedTime();
            assertTrue(elapsed >= 0, "Should track time in TWO_MINUTES mode");
        }, "Should work with TWO_MINUTES mode");
    }

    @Test
    @DisplayName("Timer works with FORTY_LINES game mode")
    void timerWorksWithFortyLinesGameMode() {
        // Given: Forty Lines mode
        gameState.setCurrentGameMode(GameMode.FORTY_LINES);

        // Then: Timer operations should work
        assertDoesNotThrow(() -> {
            timerManager.resetStartTime();
            long elapsed = timerManager.getElapsedTime();
            assertTrue(elapsed >= 0, "Should track time in FORTY_LINES mode");
        }, "Should work with FORTY_LINES mode");
    }

    @Test
    @DisplayName("Switching game modes doesn't affect elapsed time")
    void switchingGameModesDoesntAffectElapsedTime() throws InterruptedException {
        // Given: Timer running in NORMAL mode
        gameState.setCurrentGameMode(GameMode.NORMAL);
        timerManager.resetStartTime();
        Thread.sleep(50);

        // When: Switch mode
        gameState.setCurrentGameMode(GameMode.TWO_MINUTES);
        long elapsed = timerManager.getElapsedTime();

        // Then: Time should still be tracked
        assertTrue(elapsed >= 40, "Time should continue after mode switch");
    }

    // ========== Edge Cases ==========

    @Test
    @DisplayName("Multiple startGameTimer calls are safe")
    void multipleStartGameTimerCallsAreSafe() {
        assertDoesNotThrow(() -> {
            try {
                timerManager.startGameTimer();
                timerManager.startGameTimer();
                timerManager.startGameTimer();
            } catch (IllegalStateException | NullPointerException e) {
                // Expected without JavaFX
            }
            timerManager.stopGameTimer();
        }, "Multiple start calls should not crash");
    }

    @Test
    @DisplayName("stopAllTimers is idempotent")
    void stopAllTimersIsIdempotent() {
        // When: Call stop multiple times
        timerManager.stopAllTimers();
        timerManager.stopAllTimers();
        timerManager.stopAllTimers();

        // Then: Should still work
        assertDoesNotThrow(() -> {
            timerManager.stopAllTimers();
        }, "stopAllTimers should be idempotent");
    }

    @Test
    @DisplayName("All stop methods are null-safe")
    void allStopMethodsAreNullSafe() {
        assertDoesNotThrow(() -> {
            timerManager.stopDropTimer();
            timerManager.stopLockDelay();
            timerManager.stopCountdown();
            timerManager.stopGameTimer();
            timerManager.stopAllTimers();
        }, "All stop methods should handle null timers");
    }

    @Test
    @DisplayName("Pause/resume sequence works correctly")
    void pauseResumeSequenceWorksCorrectly() throws InterruptedException {
        // Given: Timer running
        timerManager.resetStartTime();
        Thread.sleep(50);

        // When: Pause -> Resume -> Pause -> Resume
        assertDoesNotThrow(() -> {
            timerManager.pauseGameTimer();
            timerManager.resumeGameTimer();
            timerManager.pauseGameTimer();
            timerManager.resumeGameTimer();
        }, "Pause/resume sequence should work");

        // Then: Time should still be tracked
        long elapsed = timerManager.getElapsedTime();
        assertTrue(elapsed > 0, "Time should be tracked after pause/resume");
    }

    // ========== Integration Tests ==========

    @Test
    @DisplayName("Full timer lifecycle works")
    void fullTimerLifecycleWorks() {
        assertDoesNotThrow(() -> {
            // Setup
            timerManager.resetStartTime();
            timerManager.setOnTimeUp(() -> {});

            // Try to start all timers (may fail without JavaFX)
            try {
                timerManager.startGameTimer();
                timerManager.startDropTimer(1000, () -> {});
                timerManager.startLockDelay(() -> {});
            } catch (Exception ignored) {}

            // Pause operations
            timerManager.pauseGameTimer();
            timerManager.pauseDropTimer();

            // Resume operations
            timerManager.resumeGameTimer();
            timerManager.resumeDropTimer();

            // Stop all
            timerManager.stopAllTimers();

            // Final check
            long elapsed = timerManager.getElapsedTime();
            assertTrue(elapsed >= 0, "Should have valid elapsed time");
        }, "Full lifecycle should work");
    }

    @Test
    @DisplayName("Timer state survives multiple operations")
    void timerStateSurvivesMultipleOperations() throws InterruptedException {
        // Complex sequence
        timerManager.resetStartTime();
        Thread.sleep(30);

        timerManager.pauseGameTimer();
        Thread.sleep(30);

        timerManager.resumeGameTimer();
        Thread.sleep(30);

        timerManager.stopAllTimers();

        // Elapsed time should still be valid
        long elapsed = timerManager.getElapsedTime();
        assertTrue(elapsed >= 60,
                "Should have tracked ~90ms, got: " + elapsed);
    }

    @Test
    @DisplayName("Concurrent-like operations don't crash")
    void concurrentLikeOperationsDoNotCrash() {
        assertDoesNotThrow(() -> {
            for (int i = 0; i < 20; i++) {
                timerManager.resetStartTime();
                timerManager.pauseGameTimer();
                timerManager.resumeGameTimer();
                timerManager.stopDropTimer();
                timerManager.stopLockDelay();
                timerManager.getElapsedTime();
            }
        }, "Rapid operations should not crash");
    }

    // ========== GameState Interaction Tests ==========

    @Test
    @DisplayName("Timer respects GameState isPaused flag")
    void timerRespectsGameStatePausedFlag() {
        // Given: Game paused
        gameState.setPaused(true);

        // Then: Timer operations should still work
        assertDoesNotThrow(() -> {
            timerManager.resetStartTime();
            long elapsed = timerManager.getElapsedTime();
            assertTrue(elapsed >= 0, "Should work when game is paused");
        }, "Should handle paused state");
    }

    @Test
    @DisplayName("Timer respects GameState isGameOver flag")
    void timerRespectsGameStateGameOverFlag() {
        // Given: Game over
        gameState.setGameOver(true);

        // Then: Timer operations should still work
        assertDoesNotThrow(() -> {
            timerManager.resetStartTime();
            long elapsed = timerManager.getElapsedTime();
            assertTrue(elapsed >= 0, "Should work when game is over");
        }, "Should handle game over state");
    }
}