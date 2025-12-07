package com.comp2042.state;

import com.comp2042.model.GameMode;
import javafx.animation.AnimationTimer;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.control.Label;
import javafx.util.Duration;

// Manages all game timers including drop timer, game timer, lock delay, and countdown

public class TimerManager {
    private Timeline dropTimeline;
    private AnimationTimer gameTimer;
    private Timeline lockDelayTimeline;
    private Timeline countdownTimeline;

    private long gameStartTime;
    private Label timeValueLabel;
    private GameState gameState;

    private static final long TWO_MINUTES_MS = 120 * 1000;

    private long pausedElapsedTime = 0;
    private Runnable onTimeUp;
    private boolean timeUpTriggered = false;
    private boolean gameTimerPaused = false;

    /**
     * Creates a new TimerManager.
     * @param gameState the game state to monitor
     * @param timeValueLabel the label to update with time display
     */
    public TimerManager(GameState gameState, Label timeValueLabel) {
        this.gameState = gameState;
        this.timeValueLabel = timeValueLabel;
    }

    /**
     * Sets the callback to invoke when time runs out (2-minute mode).
     * @param onTimeUp the callback to execute
     */
    public void setOnTimeUp(Runnable onTimeUp) {
        this.onTimeUp = onTimeUp;
    }

    public void startGameTimer() {
        gameStartTime = System.currentTimeMillis() - pausedElapsedTime;
        gameTimerPaused = false;

        if (gameTimer != null) { gameTimer.stop(); }

        gameTimer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                if (!gameTimerPaused && !gameState.isPaused()) {
                    updateTimeDisplay();
                }
            }
        };
        gameTimer.start();
    }

    public void stopGameTimer() {
        if (gameTimer != null) {
            pausedElapsedTime = System.currentTimeMillis() - gameStartTime;
            gameTimer.stop();
        }
    }

    public void pauseGameTimer() {
        pausedElapsedTime = System.currentTimeMillis() - gameStartTime;
        gameTimerPaused = true;
    }

    public void resumeGameTimer() {
        gameStartTime = System.currentTimeMillis() - pausedElapsedTime;
        gameTimerPaused = false;
    }

    /**
     * Starts the drop timer that moves pieces down automatically.
     * @param speed drop interval in milliseconds
     * @param onTick callback to execute on each drop
     */
    public void startDropTimer(int speed, Runnable onTick) {
        if (dropTimeline != null) { dropTimeline.stop(); }
        dropTimeline = new Timeline(new KeyFrame(Duration.millis(speed), ae -> onTick.run()));
        dropTimeline.setCycleCount(Timeline.INDEFINITE);
        dropTimeline.play();
    }

    public void stopDropTimer() {
        if (dropTimeline != null) { dropTimeline.stop(); }
    }

    public void pauseDropTimer() {
        if (dropTimeline != null) { dropTimeline.pause(); }
    }

    public void resumeDropTimer() {
        if (dropTimeline != null) { dropTimeline.play(); }
    }

    /**
     * Starts the lock delay timer.
     * Gives player time to adjust piece before it locks.
     * @param onComplete callback when lock delay expires
     */
    public void startLockDelay(Runnable onComplete) {
        if (lockDelayTimeline != null) { lockDelayTimeline.stop(); }
        lockDelayTimeline = new Timeline(new KeyFrame(Duration.millis(gameState.getLockDelayMs()), ae -> onComplete.run()));
        lockDelayTimeline.setCycleCount(1);
        lockDelayTimeline.play();
    }

    public void stopLockDelay() {
        if (lockDelayTimeline != null) { lockDelayTimeline.stop(); }
    }

    /**
     * Starts the pre-game countdown (3, 2, 1, GO!).
     * @param countdownLabel label to display countdown
     * @param onComplete callback when countdown finishes
     */
    public void startCountdown(Label countdownLabel, Runnable onComplete) {
        final int[] count = {3};
        countdownLabel.setText("3");
        if (countdownTimeline != null) { countdownTimeline.stop(); }

        countdownTimeline = new Timeline(new KeyFrame(Duration.seconds(1), event -> {
            count[0]--;
            if (count[0] > 0) { countdownLabel.setText(String.valueOf(count[0])); }
            else {
                countdownLabel.setText("GO!");
                Timeline hideTimeline = new Timeline(new KeyFrame(Duration.millis(500), e -> onComplete.run()));
                hideTimeline.play();
            }
        }));
        countdownTimeline.setCycleCount(4);
        countdownTimeline.play();
    }

    public void stopCountdown() {
        if (countdownTimeline != null) { countdownTimeline.stop(); countdownTimeline = null; }
    }

    public void stopAllTimers() {
        stopDropTimer(); stopGameTimer(); stopLockDelay(); stopCountdown();
    }

    private void updateTimeDisplay() {
        if (gameState.isGameOver() || gameTimerPaused || gameState.isPaused()) return;
        long elapsed = System.currentTimeMillis() - gameStartTime;

        if (gameState.getCurrentGameMode() == GameMode.TWO_MINUTES) {
            long remaining = TWO_MINUTES_MS - elapsed;
            if (remaining <= 0) {
                remaining = 0;
                timeValueLabel.setText("0:00.00");
                if (!timeUpTriggered && onTimeUp != null) { timeUpTriggered = true; onTimeUp.run(); }
                return;
            }
            int minutes = (int) (remaining / 60000);
            int seconds = (int) ((remaining % 60000) / 1000);
            int millis = (int) (remaining % 100);
            timeValueLabel.setText(String.format("%d:%02d.%02d", minutes, seconds, millis));
        } else {
            int minutes = (int) (elapsed / 60000);
            int seconds = (int) ((elapsed % 60000) / 1000);
            int millis = (int) (elapsed % 100);
            timeValueLabel.setText(String.format("%d:%02d.%02d", minutes, seconds, millis));
        }
    }

    /**
     * Gets the elapsed time since game start.
     * @return elapsed time in milliseconds
     */
    public long getElapsedTime() { return System.currentTimeMillis() - gameStartTime; }

    public void resetStartTime() {
        gameStartTime = System.currentTimeMillis();
        pausedElapsedTime = 0;
        timeUpTriggered = false;
        gameTimerPaused = false;
    }
}