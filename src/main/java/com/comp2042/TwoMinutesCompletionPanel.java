package com.comp2042;

import javafx.animation.FadeTransition;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.util.Duration;

public class TwoMinutesCompletionPanel extends StackPane{

    private Runnable onRetry;
    private Runnable onMainMenu;

    public TwoMinutesCompletionPanel(int score, int lines, boolean isNewBest, String previousBest) {
        setStyle("-fx-background-color: rgba(0, 0, 0, 0.95);");

        VBox content = new VBox(25);
        content.setAlignment(Pos.CENTER);
        content.setMaxWidth(500);

        // Title
        Label title = new Label("TIME'S UP!");
        title.setStyle("-fx-text-fill: #00aaff; -fx-font-size: 48px; -fx-font-weight: bold;");

        // Score card
        VBox scoreCard = new VBox(15);
        scoreCard.setAlignment(Pos.CENTER);
        scoreCard.setStyle("-fx-background-color: rgba(255,255,255,0.05); " +
                "-fx-padding: 30; -fx-background-radius: 15; " +
                "-fx-border-color: rgba(255,255,255,0.1); -fx-border-radius: 15; -fx-border-width: 2;");

        Label scoreLabel = new Label("FINAL SCORE");
        scoreLabel.setStyle("-fx-text-fill: #888888; -fx-font-size: 14px; -fx-font-weight: bold;");

        Label scoreValue = new Label(String.valueOf(score));
        scoreValue.setStyle("-fx-text-fill: white; -fx-font-size: 52px; -fx-font-weight: bold;");

        Label linesLabel = new Label("Lines Cleared: " + lines);
        linesLabel.setStyle("-fx-text-fill: #aaaaaa; -fx-font-size: 16px;");

        scoreCard.getChildren().addAll(scoreLabel, scoreValue, linesLabel);

        // New best badge
        if (isNewBest) {
            HBox bestBadge = new HBox(10);
            bestBadge.setAlignment(Pos.CENTER);
            bestBadge.setStyle("-fx-background-color: rgba(255,170,0,0.2); -fx-padding: 10 20; " +
                    "-fx-background-radius: 20; -fx-border-color: #ffaa00; " +
                    "-fx-border-radius: 20; -fx-border-width: 2;");

            Label star1 = new Label("★");
            star1.setStyle("-fx-text-fill: #ffaa00; -fx-font-size: 20px;");

            Label bestText = new Label("NEW HIGH SCORE!");
            bestText.setStyle("-fx-text-fill: #ffaa00; -fx-font-size: 16px; -fx-font-weight: bold;");

            Label star2 = new Label("★");
            star2.setStyle("-fx-text-fill: #ffaa00; -fx-font-size: 20px;");

            bestBadge.getChildren().addAll(star1, bestText, star2);
            scoreCard.getChildren().add(bestBadge);
        } else if (previousBest != null) {
            Label prevBest = new Label("Previous Best: " + previousBest);
            prevBest.setStyle("-fx-text-fill: #ffaa00; -fx-font-size: 14px;");
            scoreCard.getChildren().add(prevBest);
        }

        // Buttons
        HBox buttons = new HBox(20);
        buttons.setAlignment(Pos.CENTER);

        Button retryBtn = createButton("RETRY", "#00aaff", "#000000");
        retryBtn.setOnAction(e -> { if (onRetry != null) onRetry.run(); });

        Button menuBtn = createButton("MAIN MENU", "transparent", "#ffffff");
        menuBtn.setOnAction(e -> { if (onMainMenu != null) onMainMenu.run(); });

        buttons.getChildren().addAll(retryBtn, menuBtn);

        Label hint = new Label("Press N or click RETRY to play again");
        hint.setStyle("-fx-text-fill: #555555; -fx-font-size: 12px;");

        content.getChildren().addAll(title, scoreCard, buttons, hint);
        getChildren().add(content);

        // Fade in
        setOpacity(0);
        FadeTransition fade = new FadeTransition(Duration.millis(300), this);
        fade.setFromValue(0);
        fade.setToValue(1);
        fade.play();
    }

    private Button createButton(String text, String bgColor, String textColor) {
        Button btn = new Button(text);
        btn.setPrefSize(170, 50);
        btn.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; " +
                "-fx-background-radius: 10; -fx-cursor: hand; " +
                "-fx-background-color: " + bgColor + "; -fx-text-fill: " + textColor + ";");
        return btn;
    }

    public void setOnRetry(Runnable action) { this.onRetry = action; }
    public void setOnMainMenu(Runnable action) { this.onMainMenu = action; }

}
