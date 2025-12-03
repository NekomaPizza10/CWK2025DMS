package com.comp2042.ui.logic;

import com.comp2042.controller.GameController;
import com.comp2042.model.GameMode;
import com.comp2042.state.*;
import com.comp2042.ui.handlers.UIUpdater;

/**
 * Handles game progress tracking including statistics,
 * speed updates, and challenge completion checks.
 */
public class GameProgressHandler {

    private static final int FORTY_LINES_GOAL = 40;
    private static final long TWO_MINUTES_MS = 120_000;
    private static final int LINES_PER_LEVEL = 10;

    private final GameState gameState;
    private final TimerManager timerManager;
    private final ScoringManager scoringManager;
    private final UIUpdater uiUpdater;

    private GameController gameController;

    private Runnable onChallengeComplete40Lines;
    private Runnable onChallengeComplete2Minutes;
    private Runnable onUpdateNextDisplay;

    public GameProgressHandler(GameState gameState, TimerManager timerManager,
                               ScoringManager scoringManager, UIUpdater uiUpdater) {
        this.gameState = gameState;
        this.timerManager = timerManager;
        this.scoringManager = scoringManager;
        this.uiUpdater = uiUpdater;
    }

    public void setGameController(GameController gc) {
        this.gameController = gc;
    }

    public void setOnChallengeComplete40Lines(Runnable r) {
        this.onChallengeComplete40Lines = r;
    }

    public void setOnChallengeComplete2Minutes(Runnable r) {
        this.onChallengeComplete2Minutes = r;
    }

    public void setOnUpdateNextDisplay(Runnable r) {
        this.onUpdateNextDisplay = r;
    }

    public void updateStats() {
        if (gameController == null) {
            return;
        }

        int pieces = gameController.getPiecesPlaced();
        int lines = gameController.getLinesCleared();

        uiUpdater.updateStats(pieces, lines, null);

        if (onUpdateNextDisplay != null) {
            onUpdateNextDisplay.run();
        }

        checkChallengeCompletion(lines);
    }

    private void checkChallengeCompletion(int lines) {
        GameMode mode = gameState.getCurrentGameMode();

        if (mode == GameMode.FORTY_LINES) {
            checkFortyLinesCompletion(lines);
        } else if (mode == GameMode.TWO_MINUTES) {
            checkTwoMinutesCompletion();
        }
    }

    private void checkFortyLinesCompletion(int lines) {
        if (lines >= FORTY_LINES_GOAL && !gameState.isChallengeCompleted()) {
            gameState.setChallengeCompleted(true);
            if (onChallengeComplete40Lines != null) {
                onChallengeComplete40Lines.run();
            }
        }
    }

    private void checkTwoMinutesCompletion() {
        if (gameState.isChallengeCompleted() || timerManager == null) {
            return;
        }

        if (timerManager.getElapsedTime() >= TWO_MINUTES_MS) {
            gameState.setChallengeCompleted(true);
            if (onChallengeComplete2Minutes != null) {
                onChallengeComplete2Minutes.run();
            }
        }
    }

    public void updateSpeed() {
        if (gameController == null || timerManager == null) {
            return;
        }

        int lines = gameController.getLinesCleared();
        int level = lines / LINES_PER_LEVEL;

        int newSpeed = calculateNewSpeed(level);
        gameState.setCurrentDropSpeed(newSpeed);

        restartDropTimer(newSpeed);
    }

    private int calculateNewSpeed(int level) {
        int baseSpeed = gameState.getBaseDropSpeed();
        int decreasePerLevel = gameState.getSpeedDecreasePerLevel();
        int minSpeed = gameState.getMinDropSpeed();

        int calculatedSpeed = baseSpeed - (level * decreasePerLevel);
        return Math.max(minSpeed, calculatedSpeed);
    }

    private void restartDropTimer(int speed) {
        timerManager.stopDropTimer();
        timerManager.startDropTimer(speed, this::triggerGravityDrop);
    }

    private void triggerGravityDrop() {
        // This will be called by the timer - needs to be connected to movement handler
        // The actual implementation connects through GameLogicHandler
    }

}