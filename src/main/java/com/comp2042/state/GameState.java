package com.comp2042.state;

import com.comp2042.model.GameMode;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

/**
 * Central game state manager handling all game flags and mode-specific data.
 * Manages game modes, pause/game-over states, speed settings, scoring data,
 * and lock delay mechanics.
 *
 * <p>Supports three game modes:
 * <ul>
 *   <li>NORMAL - Practice mode with increasing speed</li>
 *   <li>FORTY_LINES - Time trial to clear 40 lines</li>
 *   <li>TWO_MINUTES - Score as many points as possible in 2 minutes</li>
 * </ul>
 */

public class GameState {
    private GameMode currentGameMode = GameMode.NORMAL;
    private final BooleanProperty isPaused = new SimpleBooleanProperty(false);
    private final BooleanProperty isGameOver = new SimpleBooleanProperty(false);
    private boolean isCountdownActive = false;
    private boolean challengeCompleted = false;

    // Speed management
    private int baseDropSpeed = 800;
    private int currentDropSpeed = 800;
    private static final int MIN_DROP_SPEED = 100;
    private static final int SPEED_DECREASE_PER_LEVEL = 70;

    // Hold management
    private boolean holdUsedThisTurn = false;

    // Lock delay tracking
    private boolean isLockDelayActive = false;
    private int lockDelayResetCount = 0;
    private static final int MAX_LOCK_RESETS = 10;
    private static final int LOCK_DELAY_MS = 500;

    // Normal Mode scoring
    private int normalModeScore = 0;
    private int normalModeCombo = 0;
    private boolean normalModeLastWasTetris = false;

    // 2-Minute Challenge
    private int twoMinutesScore = 0;
    private int twoMinutesCombo = 0;
    private boolean twoMinutesLastWasTetris = false;
    private static int twoMinutesBestScore = 0;

    // 40 Lines Challenge
    private long fortyLinesBestTime = Long.MAX_VALUE;

    /**
     * Gets the current game mode.
     * @return the active GameMode
     */
    public GameMode getCurrentGameMode() { return currentGameMode; }
    /**
     * Sets the current game mode.
     * @param mode the GameMode to activate
     */
    public void setCurrentGameMode(GameMode mode) { this.currentGameMode = mode; }

    /**
     * Gets the paused state property for binding.
     * @return BooleanProperty indicating if game is paused
     */
    public BooleanProperty isPausedProperty() { return isPaused; }
    /**
     * Checks if the game is currently paused.
     * @return true if paused, false otherwise
     */
    public boolean isPaused() { return isPaused.get(); }
    /**
     * Sets the paused state.
     * @param paused true to pause, false to resume
     */
    public void setPaused(boolean paused) { isPaused.set(paused); }

    /**
     * Gets the game over state property for binding.
     * @return BooleanProperty indicating if game is over
     */
    public BooleanProperty isGameOverProperty() { return isGameOver; }
    /**
     * Checks if the game is over.
     * @return true if game over, false otherwise
     */
    public boolean isGameOver() { return isGameOver.get(); }
    /**
     * Sets the game over state.
     * @param gameOver true if game is over
     */
    public void setGameOver(boolean gameOver) { isGameOver.set(gameOver); }

    public boolean isCountdownActive() { return isCountdownActive; }
    public void setCountdownActive(boolean active) { isCountdownActive = active; }

    public boolean isChallengeCompleted() { return challengeCompleted; }
    public void setChallengeCompleted(boolean completed) { challengeCompleted = completed; }

    public int getBaseDropSpeed() { return baseDropSpeed; }
    /**
     * Gets the current drop speed in milliseconds.
     * @return current drop speed (time between automatic piece movements)
     */
    public int getCurrentDropSpeed() { return currentDropSpeed; }
    /**
     * Sets the drop speed.
     * @param speed new drop speed in milliseconds
     */
    public void setCurrentDropSpeed(int speed) { currentDropSpeed = speed; }
    public int getMinDropSpeed() { return MIN_DROP_SPEED; }
    public int getSpeedDecreasePerLevel() { return SPEED_DECREASE_PER_LEVEL; }

    public boolean isLockDelayActive() { return isLockDelayActive; }
    public void setLockDelayActive(boolean active) { isLockDelayActive = active; }

    public int getLockDelayResetCount() { return lockDelayResetCount; }
    public void incrementLockDelayResetCount() { lockDelayResetCount++; }
    public void resetLockDelayCount() { lockDelayResetCount = 0; }
    public int getMaxLockResets() { return MAX_LOCK_RESETS; }
    public int getLockDelayMs() { return LOCK_DELAY_MS; }

    // Hold Function
    public boolean isHoldUsedThisTurn() { return holdUsedThisTurn; }
    public void setHoldUsedThisTurn(boolean value) { this.holdUsedThisTurn = value; }

    // Normal Mode
    public int getNormalModeScore() { return normalModeScore; }
    public void setNormalModeScore(int score) { normalModeScore = score; }
    public int getNormalModeCombo() { return normalModeCombo; }
    public void incrementNormalModeCombo() { normalModeCombo++; }
    public void resetNormalModeCombo() { normalModeCombo = 0; }
    public boolean isNormalModeLastWasTetris() { return normalModeLastWasTetris; }
    public void setNormalModeLastWasTetris(boolean wasTetris) { normalModeLastWasTetris = wasTetris; }

    // 2-Minute Challenge
    public int getTwoMinutesScore() { return twoMinutesScore; }
    public void setTwoMinutesScore(int score) { twoMinutesScore = score; }
    public int getTwoMinutesCombo() { return twoMinutesCombo; }
    public void incrementTwoMinutesCombo() { twoMinutesCombo++; }
    public void resetTwoMinutesCombo() { twoMinutesCombo = 0; }
    public boolean isTwoMinutesLastWasTetris() { return twoMinutesLastWasTetris; }
    public void setTwoMinutesLastWasTetris(boolean wasTetris) { twoMinutesLastWasTetris = wasTetris; }
    public static int getTwoMinutesBestScore() { return twoMinutesBestScore; }
    public static void setTwoMinutesBestScore(int score) { twoMinutesBestScore = score; }

    // 40 Lines Challenge
    public long getFortyLinesBestTime() { return fortyLinesBestTime; }
    public void setFortyLinesBestTime(long time) { fortyLinesBestTime = time; }

    public void resetScores() {
        normalModeScore = 0;
        normalModeCombo = 0;
        normalModeLastWasTetris = false;
        twoMinutesScore = 0;
        twoMinutesCombo = 0;
        twoMinutesLastWasTetris = false;
    }
}