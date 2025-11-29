package com.comp2042.state;

import com.comp2042.model.GameMode;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

// Manages game state flags and mode-specific data

public class GameState {
    private GameMode currentGameMode = GameMode.NORMAL;
    private final BooleanProperty isPaused = new SimpleBooleanProperty(false);
    private final BooleanProperty isGameOver = new SimpleBooleanProperty(false);
    private boolean isCountdownActive = false;
    private boolean challengeCompleted = false;

    // Speed management
    private int baseDropSpeed = 800;
    private int currentDropSpeed = 800;
    private static final int MIN_DROP_SPEED = 200;
    private static final int SPEED_DECREASE_PER_LEVEL = 50;

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

    // Getters and Setters
    public GameMode getCurrentGameMode() { return currentGameMode; }
    public void setCurrentGameMode(GameMode mode) { this.currentGameMode = mode; }

    public BooleanProperty isPausedProperty() { return isPaused; }
    public boolean isPaused() { return isPaused.get(); }
    public void setPaused(boolean paused) { isPaused.set(paused); }

    public BooleanProperty isGameOverProperty() { return isGameOver; }
    public boolean isGameOver() { return isGameOver.get(); }
    public void setGameOver(boolean gameOver) { isGameOver.set(gameOver); }

    public boolean isCountdownActive() { return isCountdownActive; }
    public void setCountdownActive(boolean active) { isCountdownActive = active; }

    public boolean isChallengeCompleted() { return challengeCompleted; }
    public void setChallengeCompleted(boolean completed) { challengeCompleted = completed; }

    public int getBaseDropSpeed() { return baseDropSpeed; }
    public int getCurrentDropSpeed() { return currentDropSpeed; }
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