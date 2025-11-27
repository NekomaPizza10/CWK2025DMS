package com.comp2042;

import javafx.animation.FadeTransition;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.util.Duration;

public class GameOverPanel extends StackPane {

    private Runnable onRetry;
    private Runnable onMainMenu;

    public GameOverPanel() {
        // Default constructor for FXML
        setStyle("-fx-background-color: rgba(0, 0, 0, 0.92);");
        setVisible(false);
    }

    public void showGameOver(int pieces, int lines, long timeMs, String timeStr, int score, GameMode mode) {
        // Clear existing content
        getChildren().clear();

        VBox content = new VBox(20);
        content.setAlignment(Pos.CENTER);
        content.setMaxWidth(600);

        // Title
        Label title = new Label("GAME OVER");
        title.setStyle("-fx-text-fill: #ff4444; -fx-font-size: 56px; -fx-font-weight: bold; " +
                "-fx-effect: dropshadow(gaussian, rgba(255,68,68,0.8), 20, 0, 0, 0);");

        // Stats card
        VBox statsCard = new VBox(15);
        statsCard.setAlignment(Pos.CENTER);
        statsCard.setStyle("-fx-background-color: rgba(255,255,255,0.05); " +
                "-fx-padding: 40; -fx-background-radius: 15; " +
                "-fx-border-color: rgba(255,255,255,0.2); " +
                "-fx-border-radius: 15; -fx-border-width: 2;");

        // Mode-specific display
        if (mode == GameMode.NORMAL) {
            addStatRow(statsCard, "SCORE", String.valueOf(score), "#00ff88");
            addStatRow(statsCard, "PIECES", String.valueOf(pieces), "#ffffff");
            addStatRow(statsCard, "LINES", String.valueOf(lines), "#ffffff");
            addStatRow(statsCard, "TIME", timeStr, "#ffffff");

        } else if (mode == GameMode.FORTY_LINES) {
            addStatRow(statsCard, "TIME", timeStr, "#ff9900");
            addStatRow(statsCard, "PIECES", String.valueOf(pieces), "#ffffff");
            addStatRow(statsCard, "LINES", String.valueOf(lines), "#ffffff");

        } else if (mode == GameMode.TWO_MINUTES) {
            addStatRow(statsCard, "SCORE", String.valueOf(score), "#00aaff");
            addStatRow(statsCard, "LINES", String.valueOf(lines), "#ffffff");
            addStatRow(statsCard, "PIECES", String.valueOf(pieces), "#ffffff");
        }

        // Buttons
        HBox buttons = new HBox(20);
        buttons.setAlignment(Pos.CENTER);

        Button retryBtn = createButton("RETRY", "#00ff88", "#000000");
        retryBtn.setOnAction(e -> { if (onRetry != null) onRetry.run(); });

        Button menuBtn = createButton("MAIN MENU", "transparent", "#ffffff");
        menuBtn.setOnAction(e -> { if (onMainMenu != null) onMainMenu.run(); });

        buttons.getChildren().addAll(retryBtn, menuBtn);

        Label hint = new Label("Press N to retry");
        hint.setStyle("-fx-text-fill: #666666; -fx-font-size: 14px;");

        content.getChildren().addAll(title, statsCard, buttons, hint);
        getChildren().add(content);

        // Fade in animation
        setVisible(true);
        setOpacity(0);
        FadeTransition fade = new FadeTransition(Duration.millis(400), this);
        fade.setFromValue(0);
        fade.setToValue(1);
        fade.play();
    }

    private void addStatRow(VBox container, String label, String value, String color) {
        VBox row = new VBox(5);
        row.setAlignment(Pos.CENTER);

        Label labelText = new Label(label);
        labelText.setStyle("-fx-text-fill: #888888; -fx-font-size: 14px; " +
                "-fx-font-weight: bold; -fx-letter-spacing: 2;");

        Label valueText = new Label(value);
        valueText.setStyle("-fx-text-fill: " + color + "; -fx-font-size: 36px; " +
                "-fx-font-weight: bold;");

        row.getChildren().addAll(labelText, valueText);
        container.getChildren().add(row);

        // Add separator except for last item
        if (container.getChildren().size() < 8) {
            Region separator = new Region();
            separator.setPrefHeight(1);
            separator.setStyle("-fx-background-color: rgba(255,255,255,0.1);");
            separator.setMaxWidth(300);
            container.getChildren().add(separator);
        }
    }

    private Button createButton(String text, String bgColor, String textColor) {
        Button btn = new Button(text);
        btn.setPrefSize(180, 55);
        btn.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; " +
                "-fx-background-radius: 10; -fx-cursor: hand; " +
                "-fx-background-color: " + bgColor + "; -fx-text-fill: " + textColor + "; " +
                "-fx-border-color: " + (bgColor.equals("transparent") ? "#ffffff" : "transparent") + "; " +
                "-fx-border-width: 2; -fx-border-radius: 10;");

        // Hover effect
        btn.setOnMouseEntered(e -> {
            if (bgColor.equals("transparent")) {
                btn.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; " +
                        "-fx-background-radius: 10; -fx-cursor: hand; " +
                        "-fx-background-color: #ffffff; -fx-text-fill: #000000; " +
                        "-fx-border-color: #ffffff; -fx-border-width: 2; -fx-border-radius: 10;");
            }
        });

        btn.setOnMouseExited(e -> {
            btn.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; " +
                    "-fx-background-radius: 10; -fx-cursor: hand; " +
                    "-fx-background-color: " + bgColor + "; -fx-text-fill: " + textColor + "; " +
                    "-fx-border-color: " + (bgColor.equals("transparent") ? "#ffffff" : "transparent") + "; " +
                    "-fx-border-width: 2; -fx-border-radius: 10;");
        });

        return btn;
    }

    public void setOnRetry(Runnable action) {
        this.onRetry = action;
    }

    public void setOnMainMenu(Runnable action) {
        this.onMainMenu = action;
    }
}