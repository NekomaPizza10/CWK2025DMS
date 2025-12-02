package com.comp2042.ui;

import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.effect.DropShadow;

import java.util.Timer;
import java.util.TimerTask;

public class ComboMeterPanel extends VBox {

    private Rectangle fillBar;
    private Rectangle border;

    private int targetCombo = 0;
    private int displayCombo = 0;
    private boolean isPaused = false;
    private boolean isDisposed = false;

    private Timer decayTimer;
    private Timer animTimer;

    private static final double BAR_WIDTH = 10;
    private static final double BAR_HEIGHT = 220;
    private static final long DECAY_START_DELAY = 3000;  // 3 seconds before decay starts
    private static final long DECAY_STEP_INTERVAL = 1200; // 1.2 seconds per decay step
    private static final long ANIM_STEP_INTERVAL = 80;   // Animation step for smooth movement

    public ComboMeterPanel() {
        setAlignment(Pos.CENTER);
        setStyle("-fx-background-color: transparent;");
        setMinWidth(BAR_WIDTH + 6);
        setMaxWidth(BAR_WIDTH + 6);
        setPrefWidth(BAR_WIDTH + 6);

        createBar();
    }

    private void createBar() {
        StackPane container = new StackPane();
        container.setAlignment(Pos.BOTTOM_CENTER);

        Rectangle bg = new Rectangle(BAR_WIDTH, BAR_HEIGHT);
        bg.setArcWidth(5);
        bg.setArcHeight(5);
        bg.setFill(Color.rgb(20, 20, 20, 0.85));

        fillBar = new Rectangle(BAR_WIDTH - 4, 0);
        fillBar.setArcWidth(3);
        fillBar.setArcHeight(3);
        fillBar.setFill(Color.TRANSPARENT);
        StackPane.setAlignment(fillBar, Pos.BOTTOM_CENTER);

        border = new Rectangle(BAR_WIDTH, BAR_HEIGHT);
        border.setArcWidth(5);
        border.setArcHeight(5);
        border.setFill(Color.TRANSPARENT);
        border.setStroke(Color.rgb(50, 50, 50));
        border.setStrokeWidth(1);

        container.getChildren().addAll(bg, fillBar, border);
        getChildren().add(container);
    }

    public void updateCombo(int combo) {
        if (isDisposed) return;
        targetCombo = Math.max(0, Math.min(combo, 15));
        // Stop decay - player is active
        stopDecayTimer();
        // Animate toward target
        animateToTarget();
        // Start decay timer (will trigger after 3 seconds of no updates)
        if (!isPaused && targetCombo > 0) {
            startDecayTimer();
        }
    }

    private void animateToTarget() {
        stopAnimTimer();
        if (displayCombo == targetCombo) {
            updateVisuals(displayCombo);
            return;
        }
        animTimer = new Timer(true);
        animTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (isDisposed) {
                    cancel();
                    return;
                }
                if (displayCombo < targetCombo) {
                    displayCombo++;
                } else if (displayCombo > targetCombo) {
                    displayCombo--;
                }
                runOnFx(() -> updateVisuals(displayCombo));
                if (displayCombo == targetCombo) {
                    cancel();
                }
            }
        }, 0, ANIM_STEP_INTERVAL);
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
                    runOnFx(() -> updateVisuals(displayCombo));
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

    private void stopAnimTimer() {
        if (animTimer != null) {
            animTimer.cancel();
            animTimer = null;
        }
    }

    private void updateVisuals(int combo) {
        if (fillBar == null || border == null || isDisposed) return;

        double maxH = BAR_HEIGHT - 4;
        double h = (combo / 10.0) * maxH;
        fillBar.setHeight(Math.min(h, maxH));

        Color color = getColor(combo);

        if (combo == 0) {
            fillBar.setFill(Color.TRANSPARENT);
            fillBar.setEffect(null);
            border.setStroke(Color.rgb(50, 50, 50));
            border.setEffect(null);
        } else {
            fillBar.setFill(color);
            border.setStroke(color);

            DropShadow glow = new DropShadow();
            glow.setColor(color);
            glow.setRadius(4);
            fillBar.setEffect(glow);
            border.setEffect(glow);
        }
    }

    private Color getColor(int combo) {
        if (combo >= 8) return Color.RED;
        if (combo >= 6) return Color.ORANGE;
        if (combo >= 4) return Color.YELLOW;
        if (combo >= 2) return Color.LIMEGREEN;
        if (combo >= 1) return Color.GREEN;
        return Color.GRAY;
    }

    public void reset() {
        targetCombo = 0;
        displayCombo = 0;
        isPaused = false;
        stopDecayTimer();
        stopAnimTimer();
        runOnFx(() -> updateVisuals(0));
    }

    public void dispose() {
        isDisposed = true;
        stopDecayTimer();
        stopAnimTimer();
    }

    public int getDisplayCombo() {
        return displayCombo;
    }

    private void runOnFx(Runnable r) {
        if (isDisposed) return;
        if (Platform.isFxApplicationThread()) {r.run();}
        else {
            Platform.runLater(() -> {
                if (!isDisposed) r.run();});
        }
    }
}