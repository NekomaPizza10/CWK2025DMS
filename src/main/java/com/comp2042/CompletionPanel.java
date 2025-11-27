package com.comp2042;

import javafx.animation.FadeTransition;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.util.Duration;

public class CompletionPanel extends StackPane{
    private Runnable onRetry;
    private Runnable onMainMenu;

    public CompletionPanel(String timeString, boolean isNewBest, String previousBest) {
        setStyle("-fx-background-color: rgba(0, 0, 0, 0.95);");

        VBox content = new VBox(25);
        content.setAlignment(Pos.CENTER);
        content.setMaxWidth(500);

        // Title
        Label title = new Label("FINISH !!!");
        title.setStyle("-fx-text-fill: #00ff88; -fx-font-size: 48px; -fx-font-weight: bold;");

        // Time card
        VBox timeCard = new VBox(10);
        timeCard.setAlignment(Pos.CENTER);
        timeCard.setStyle("-fx-background-color: rgba(255,255,255,0.05); " +
                "-fx-padding: 30; -fx-background-radius: 15; " +
                "-fx-border-color: rgba(255,255,255,0.1); -fx-border-radius: 15; -fx-border-width: 2;");

        Label timeLabel = new Label("FINAL TIME");
        timeLabel.setStyle("-fx-text-fill: #888888; -fx-font-size: 14px; -fx-font-weight: bold;");

        Label timeValue = new Label(timeString);
        timeValue.setStyle("-fx-text-fill: white; -fx-font-size: 52px; -fx-font-weight: bold;");

        timeCard.getChildren().addAll(timeLabel, timeValue);

        // Add timeCard to content first
        content.getChildren().addAll(title, timeCard);

        // New best badge or previous best - ADD TO CONTENT, NOT TIMECARD
        if (isNewBest) {
            HBox bestBadge = new HBox(10);
            bestBadge.setAlignment(Pos.CENTER);
            bestBadge.setStyle("-fx-background-color: rgba(255,170,0,0.2); -fx-padding: 10 20; " +
                    "-fx-background-radius: 20; -fx-border-color: #ffaa00; " +
                    "-fx-border-radius: 20; -fx-border-width: 2;");

            Label star1 = new Label("★");
            star1.setStyle("-fx-text-fill: #ffaa00; -fx-font-size: 20px;");

            Label bestText = new Label("NEW PERSONAL BEST!");
            bestText.setStyle("-fx-text-fill: #ffaa00; -fx-font-size: 16px; -fx-font-weight: bold;");

            Label star2 = new Label("★");
            star2.setStyle("-fx-text-fill: #ffaa00; -fx-font-size: 20px;");

            bestBadge.getChildren().addAll(star1, bestText, star2);
            content.getChildren().add(bestBadge); // Add to content, not timeCard
        } else if (previousBest != null) {
            Label prevBest = new Label("Previous Best: " + previousBest);
            prevBest.setStyle("-fx-text-fill: #ffaa00; -fx-font-size: 14px;");
            content.getChildren().add(prevBest); // Add to content, not timeCard
        }

        // Buttons
        HBox buttons = new HBox(20);
        buttons.setAlignment(Pos.CENTER);

        Button retryBtn = createButton("RETRY", "#00ff88", "#000000", true);
        retryBtn.setOnAction(e -> { if (onRetry != null) onRetry.run(); });

        Button menuBtn = createButton("MAIN MENU", "transparent", "#ffffff", false);
        menuBtn.setOnAction(e -> { if (onMainMenu != null) onMainMenu.run(); });

        buttons.getChildren().addAll(retryBtn, menuBtn);

        Label hint = new Label("Press N or click RETRY to play again");
        hint.setStyle("-fx-text-fill: #555555; -fx-font-size: 12px;");

        content.getChildren().addAll(buttons, hint);
        getChildren().add(content);

        // Fade in
        setOpacity(0);
        FadeTransition fade = new FadeTransition(Duration.millis(300), this);
        fade.setFromValue(0);
        fade.setToValue(1);
        fade.play();
    }

    private Button createButton(String text, String bgColor, String textColor, boolean filled) {
        Button btn = new Button(text);
        btn.setPrefSize(170, 50);
        btn.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; " +
                "-fx-background-radius: 10; -fx-cursor: hand; " +
                "-fx-background-color: " + bgColor + "; -fx-text-fill: " + textColor + ";");
        return btn;
    }

    public void setOnRetry(Runnable action) {
        this.onRetry = action;
    }

    public void setOnMainMenu(Runnable action) {
        this.onMainMenu = action;
    }
}