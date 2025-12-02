package com.comp2042.ui;

import javafx.application.Platform;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.effect.DropShadow;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Board glow effect matching combo meter behavior
 */
public class BoardGlowEffect {

    private GridPane gamePanel;
    private DropShadow glow;

    private int targetCombo = 0;
    private int displayCombo = 0;
    private boolean isPaused = false;
    private boolean isDisposed = false;

    private Timer decayTimer;

    private static final long DECAY_START_DELAY = 3000;
    private static final long DECAY_STEP_INTERVAL = 1200;

    public BoardGlowEffect(GridPane gamePanel) {this.gamePanel = gamePanel;}

    public void updateGlow(int combo) {
        if (isDisposed) return;
        targetCombo = Math.max(0, Math.min(combo, 15));
        displayCombo = targetCombo;
        stopDecayTimer();
        runOnFx(() -> applyGlow(displayCombo));
        if (!isPaused && targetCombo > 0) {startDecayTimer();}
    }

    public void pauseDecay() {
        isPaused = true;
        stopDecayTimer();
    }

    public void resumeDecay() {
        isPaused = false;
        if (targetCombo > 0) {
            startDecayTimer();
        }
    }

    private void startDecayTimer() {
        stopDecayTimer();
        if (targetCombo <= 0 || isDisposed || isPaused) return;
        decayTimer = new Timer(true);
        decayTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (isDisposed || isPaused) {
                    cancel();
                    return;
                }
                startDecayLoop();
            }
        }, DECAY_START_DELAY);
    }

    private void startDecayLoop() {
        stopDecayTimer();
        if (targetCombo <= 0 || isDisposed || isPaused) return;
        decayTimer = new Timer(true);
        decayTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (isDisposed || isPaused) {
                    cancel();
                    return;
                }
                if (targetCombo > 0) {
                    targetCombo--;
                    displayCombo = targetCombo;
                    runOnFx(() -> applyGlow(displayCombo));
                }
                if (targetCombo <= 0) {
                    cancel();
                }
            }
        }, 0, DECAY_STEP_INTERVAL);
    }

    private void stopDecayTimer() {
        if (decayTimer != null) {
            decayTimer.cancel();
            decayTimer = null;
        }
    }

    private void applyGlow(int combo) {
        if (gamePanel == null || isDisposed) return;
        if (combo == 0) {
            gamePanel.setEffect(null);
            glow = null;
        } else {
            if (glow == null) glow = new DropShadow();
            Color color = getColor(combo);
            glow.setColor(color);
            glow.setRadius(4 + combo * 1.5);
            glow.setSpread(0.1 + combo * 0.02);
            gamePanel.setEffect(glow);
        }
    }

    private Color getColor(int combo) {
        if (combo >= 8) return Color.rgb(255, 50, 50, 0.7);
        if (combo >= 6) return Color.rgb(255, 130, 0, 0.65);
        if (combo >= 4) return Color.rgb(255, 200, 0, 0.6);
        if (combo >= 2) return Color.rgb(180, 255, 0, 0.5);
        if (combo >= 1) return Color.rgb(80, 200, 80, 0.4);
        return Color.TRANSPARENT;
    }

    public void reset() {
        targetCombo = 0;
        displayCombo = 0;
        isPaused = false;
        stopDecayTimer();
        runOnFx(() -> {
            if (gamePanel != null) gamePanel.setEffect(null);
            glow = null;
        });
    }

    public void dispose() {
        isDisposed = true;
        stopDecayTimer();
        if (gamePanel != null) gamePanel.setEffect(null);
        gamePanel = null;
        glow = null;
    }

    private void runOnFx(Runnable r) {
        if (isDisposed) return;
        if (Platform.isFxApplicationThread()) {r.run();}
        else {
            Platform.runLater(() -> {if (!isDisposed) r.run();});
        }
    }
}