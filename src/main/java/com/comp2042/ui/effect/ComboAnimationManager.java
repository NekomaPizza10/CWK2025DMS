package com.comp2042.ui.effect;

import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.effect.DropShadow;

import java.util.Timer;
import java.util.TimerTask;

public class ComboAnimationManager {

    private GridPane gamePanel;
    private Pane effectsLayer;
    private Label comboLabel;
    private StackPane textContainer;

    private double originalY = 0;
    private boolean isDisposed = false;
    private boolean isBouncing = false;

    /**
     * Creates a new ComboAnimationManager.
     *
     * @param gamePanel game panel for shake effects
     * @param effectsLayer layer for text effects
     */
    public ComboAnimationManager(GridPane gamePanel, Pane effectsLayer) {
        this.gamePanel = gamePanel;
        this.effectsLayer = effectsLayer;
        if (gamePanel != null) {
            originalY = gamePanel.getTranslateY();
        }
        runOnFx(this::init);
    }

    private void init() {
        if (effectsLayer == null || isDisposed) return;

        textContainer = new StackPane();
        textContainer.setMouseTransparent(true);
        textContainer.setAlignment(Pos.CENTER);
        textContainer.prefWidthProperty().bind(effectsLayer.widthProperty());
        textContainer.prefHeightProperty().bind(effectsLayer.heightProperty());

        comboLabel = new Label();
        comboLabel.setMouseTransparent(true);
        comboLabel.setVisible(false);

        DropShadow shadow = new DropShadow();
        shadow.setColor(Color.BLACK);
        shadow.setRadius(6);
        comboLabel.setEffect(shadow);

        textContainer.getChildren().add(comboLabel);
        effectsLayer.getChildren().add(textContainer);
    }

    /**
     * Triggers combo visual effects.
     * Shows combo text based on level.
     *
     * @param combo combo count
     */
    public void triggerComboEffects(int combo) {
        if (isDisposed || combo < 2) return;
        runOnFx(() -> showText(combo));
    }

    public void shakeOnHardDrop() {
        if (isDisposed) return;
        runOnFx(() -> bounce(10));
    }

    /**
     * Triggers screen shake for line clear.
     *
     * @param lines number of lines cleared
     */
    public void shakeOnLineClear(int lines) {
        if (isDisposed || lines < 1) return;
        runOnFx(() -> bounce(5 + lines * 2));
    }

    private void bounce(double strength) {
        if (gamePanel == null || isDisposed || isBouncing) return;

        isBouncing = true;
        strength = Math.min(strength, 14);
        final double s = strength;

        gamePanel.setTranslateY(originalY + s);

        Timer timer = new Timer(true);
        timer.schedule(new TimerTask() {
            int step = 0;
            @Override
            public void run() {
                runOnFx(() -> {
                    if (isDisposed || gamePanel == null) {
                        isBouncing = false;
                        cancel();
                        return;
                    }

                    switch (step) {
                        case 0: gamePanel.setTranslateY(originalY - s * 0.4); break;
                        case 1: gamePanel.setTranslateY(originalY + s * 0.15); break;
                        case 2:
                            gamePanel.setTranslateY(originalY);
                            isBouncing = false;
                            cancel();
                            break;
                    }
                    step++;
                });
            }
        }, 40, 40);
    }

    private void showText(int combo) {
        if (comboLabel == null || isDisposed) return;

        String text = getText(combo);
        Color color = getColor(combo);

        comboLabel.setText(text);
        comboLabel.setTextFill(color);
        comboLabel.setStyle("-fx-font-size: " + (22 + combo) + "px; -fx-font-weight: bold;");
        comboLabel.setVisible(true);

        if (textContainer != null) textContainer.toFront();

        Timer timer = new Timer(true);
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                runOnFx(() -> {
                    if (comboLabel != null && !isDisposed) {
                        comboLabel.setVisible(false);
                    }
                });
            }
        }, 600);
    }

    private String getText(int combo) {
        if (combo >= 10) return "GODLIKE!";
        if (combo >= 8) return "LEGENDARY!";
        if (combo >= 6) return "INSANE!";
        if (combo >= 4) return "GREAT!";
        return "NICE!";
    }

    private Color getColor(int combo) {
        if (combo >= 10) return Color.MAGENTA;
        if (combo >= 8) return Color.RED;
        if (combo >= 6) return Color.ORANGE;
        if (combo >= 4) return Color.YELLOW;
        return Color.LIGHTGREEN;
    }

    public void stopAllAnimations() {
        if (gamePanel != null) gamePanel.setTranslateY(originalY);
        if (comboLabel != null) comboLabel.setVisible(false);
        isBouncing = false;
    }

    public void dispose() {
        isDisposed = true;
        stopAllAnimations();
        gamePanel = null;
        effectsLayer = null;
    }

    private void runOnFx(Runnable r) {
        if (isDisposed) return;
        if (Platform.isFxApplicationThread()) {
            r.run();
        } else {
            Platform.runLater(() -> {
                if (!isDisposed) r.run();
            });
        }
    }
}