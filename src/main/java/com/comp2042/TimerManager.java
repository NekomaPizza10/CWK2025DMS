package com.comp2042;

import javafx.animation.AnimationTimer;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.control.Label;
import javafx.util.Duration;

/**
 * Manages all game timers including drop timer, game timer, lock delay, and countdown
 */
public class TimerManager {
    private Timeline dropTimeline;
    private AnimationTimer gameTimer;
    private Timeline lockDelayTimeline;
    private Timeline countdownTimeline;

    private long gameStartTime;
    private Label timeValueLabel;
    private GameState gameState;

    private static final long TWO_MINUTES_MS = 10 * 1000;

    private long pausedElapsedTime = 0;
    private Runnable onTimeUp;
    private boolean timeUpTriggered = false;

    public TimerManager(GameState gameState, Label timeValueLabel) {
        this.gameState = gameState;
        this.timeValueLabel = timeValueLabel;
    }

    public void setOnTimeUp(Runnable onTimeUp) {
        this.onTimeUp = onTimeUp;
    }

    public void startGameTimer() {
        gameStartTime = System.currentTimeMillis() - pausedElapsedTime;

        if (gameTimer != null) {
            gameTimer.stop();
        }

        gameTimer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                updateTimeDisplay();
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

    public void startDropTimer(int speed, Runnable onTick) {
        if (dropTimeline != null) {
            dropTimeline.stop();
        }

        dropTimeline = new Timeline(new KeyFrame(
                Duration.millis(speed),
                ae -> onTick.run()
        ));
        dropTimeline.setCycleCount(Timeline.INDEFINITE);
        dropTimeline.play();
    }

    public void stopDropTimer() {
        if (dropTimeline != null) {
            dropTimeline.stop();
        }
    }

    public void pauseDropTimer() {
        if (dropTimeline != null) {
            dropTimeline.pause();
        }
    }

    public void resumeDropTimer() {
        if (dropTimeline != null) {
            dropTimeline.play();
        }
    }

    public void startLockDelay(Runnable onComplete) {
        if (lockDelayTimeline != null) {
            lockDelayTimeline.stop();
        }

        lockDelayTimeline = new Timeline(new KeyFrame(
                Duration.millis(gameState.getLockDelayMs()),
                ae -> onComplete.run()
        ));
        lockDelayTimeline.setCycleCount(1);
        lockDelayTimeline.play();
    }

    public void stopLockDelay() {
        if (lockDelayTimeline != null) {
            lockDelayTimeline.stop();
        }
    }

    public void startCountdown(Label countdownLabel, Runnable onComplete) {
        final int[] count = {3};
        countdownLabel.setText("3");

        if (countdownTimeline != null) {
            countdownTimeline.stop();
        }

        countdownTimeline = new Timeline(new KeyFrame(Duration.seconds(1), event -> {
            count[0]--;
            if (count[0] > 0) {
                countdownLabel.setText(String.valueOf(count[0]));
            } else {
                countdownLabel.setText("GO!");

                Timeline hideTimeline = new Timeline(new KeyFrame(Duration.millis(500), e -> {
                    onComplete.run();
                }));
                hideTimeline.play();
            }
        }));

        countdownTimeline.setCycleCount(4);
        countdownTimeline.play();
    }

    public void stopCountdown() {
        if (countdownTimeline != null) {
            countdownTimeline.stop();
        }
    }

    public void stopAllTimers() {
        stopDropTimer();
        stopGameTimer();
        stopLockDelay();
        stopCountdown();
    }

    private void updateTimeDisplay() {
        if (!gameState.isGameOver()) {
            long elapsed = System.currentTimeMillis() - gameStartTime;

            if (gameState.getCurrentGameMode() == GameMode.TWO_MINUTES) {
                long remaining = TWO_MINUTES_MS - elapsed;

                // FIX: Trigger completion when time hits 0
                if (remaining <= 0) {
                    remaining = 0;
                    timeValueLabel.setText("0:00.000");

                    if (!timeUpTriggered && onTimeUp != null) {
                        timeUpTriggered = true;
                        onTimeUp.run();
                    }
                    return;
                }

                int minutes = (int) (remaining / 60000);
                int seconds = (int) ((remaining % 60000) / 1000);
                int millis = (int) (remaining % 1000);
                timeValueLabel.setText(String.format("%d:%02d.%03d", minutes, seconds, millis));

            } else {
                int minutes = (int) (elapsed / 60000);
                int seconds = (int) ((elapsed % 60000) / 1000);
                int millis = (int) (elapsed % 1000);
                timeValueLabel.setText(String.format("%d:%02d.%03d", minutes, seconds, millis));
            }
        }
    }

    public long getElapsedTime() {
        return System.currentTimeMillis() - gameStartTime;
    }

    public void resetStartTime() {
        gameStartTime = System.currentTimeMillis();
        pausedElapsedTime = 0;
        timeUpTriggered = false;
    }
}