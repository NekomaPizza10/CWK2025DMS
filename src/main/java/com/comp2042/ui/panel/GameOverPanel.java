package com.comp2042.ui.panel;

import com.comp2042.model.*;
import javafx.animation.FadeTransition;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

public class GameOverPanel extends StackPane {

    private Runnable onRetry;
    private Runnable onMainMenu;

    public GameOverPanel() {
        getStylesheets().add(getClass().getResource("/completion.css").toExternalForm());
        getStyleClass().add("completion-overlay");
        setVisible(false);
    }

    public void showGameOver(int pieces, int lines, long timeMs, String timeStr, int score, GameMode mode) {
        getChildren().clear();

        VBox card = new VBox();
        card.getStyleClass().add("completion-card");

        // This prevents the VBox from stretching to the top and bottom of the screen
        card.setMaxHeight(Region.USE_PREF_SIZE);
        card.setMaxWidth(Region.USE_PREF_SIZE);

        Label title = new Label("GAME OVER");
        title.getStyleClass().add("game-over-title");
        card.getChildren().add(title);

        // Stats
        addStat(card, "FINAL SCORE", String.valueOf(score), true);
        addStat(card, "TIME PLAYED", timeStr, false);
        addStat(card, "LINES CLEARED", String.valueOf(lines), false);

        // Buttons
        HBox buttons = new HBox(15);
        buttons.setAlignment(Pos.CENTER);
        buttons.getStyleClass().add("button-box");

        Button retryBtn = new Button("RETRY");
        retryBtn.getStyleClass().add("unified-button");
        retryBtn.setOnAction(e -> { if (onRetry != null) onRetry.run(); });

        Button menuBtn = new Button("EXIT");
        menuBtn.getStyleClass().add("unified-button");
        menuBtn.setOnAction(e -> { if (onMainMenu != null) onMainMenu.run(); });

        buttons.getChildren().addAll(retryBtn, menuBtn);
        card.getChildren().add(buttons);

        Label hint = new Label("Press 'N' for Instant Restart");
        hint.getStyleClass().add("completion-hint");
        card.getChildren().add(hint);

        getChildren().add(card);

        // Fade in animation
        setVisible(true);
        setOpacity(0);
        FadeTransition fade = new FadeTransition(Duration.millis(300), this);
        fade.setFromValue(0);
        fade.setToValue(1);
        fade.play();
    }

    private void addStat(VBox container, String labelText, String valueText, boolean isHighlight) {
        VBox box = new VBox(0);
        box.setAlignment(Pos.CENTER);
        Label lbl = new Label(labelText);
        lbl.getStyleClass().add("stat-label");
        Label val = new Label(valueText);
        val.getStyleClass().add(isHighlight ? "stat-value-highlight" : "stat-value");
        box.getChildren().addAll(lbl, val);
        container.getChildren().add(box);
    }

    public void setOnRetry(Runnable r) { this.onRetry = r; }
    public void setOnMainMenu(Runnable r) { this.onMainMenu = r; }
}