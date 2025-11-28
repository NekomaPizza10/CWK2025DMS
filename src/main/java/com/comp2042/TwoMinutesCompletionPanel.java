package com.comp2042;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public class TwoMinutesCompletionPanel extends StackPane {

    private Runnable onRetry;
    private Runnable onMainMenu;

    public TwoMinutesCompletionPanel(int finalScore, int linesCleared, boolean isNewBest, String previousBest) {
        // Load CSS stylesheet
        getStylesheets().add(getClass().getResource("/completion.css").toExternalForm());

        getStyleClass().add("completion-overlay");

        VBox content = new VBox(25);
        content.setAlignment(Pos.CENTER);
        content.getStyleClass().add("completion-content");

        // Title
        Label title = new Label("TIME'S UP!");
        title.getStyleClass().add("completion-title");

        // Score display
        Label scoreLabel = new Label("Score: " + finalScore);
        scoreLabel.getStyleClass().add("completion-score");

        // Lines display
        Label linesLabel = new Label("Lines Cleared: " + linesCleared);
        linesLabel.getStyleClass().add("completion-lines");

        content.getChildren().addAll(title, scoreLabel, linesLabel);

        // New best indicator
        if (isNewBest) {
            Label newBestLabel = new Label("★ NEW BEST! ★");
            newBestLabel.getStyleClass().add("completion-new-best");
            content.getChildren().add(newBestLabel);
        } else if (previousBest != null) {
            Label bestLabel = new Label("Best: " + previousBest);
            bestLabel.getStyleClass().add("completion-best");
            content.getChildren().add(bestLabel);
        }

        // Spacer
        VBox spacer = new VBox();
        spacer.setMinHeight(20);
        content.getChildren().add(spacer);

        // Buttons
        HBox buttons = createButtons();
        content.getChildren().add(buttons);

        // Hint
        Label hint = new Label("Press N to retry");
        hint.getStyleClass().add("completion-hint");
        content.getChildren().add(hint);

        getChildren().add(content);
        setAlignment(Pos.CENTER);
    }

    private HBox createButtons() {
        HBox buttonBox = new HBox(30);
        buttonBox.setAlignment(Pos.CENTER);

        Button retryButton = new Button("RETRY");
        retryButton.getStyleClass().add("completion-button");
        retryButton.setOnAction(e -> {
            if (onRetry != null) onRetry.run();
        });

        Button menuButton = new Button("MAIN MENU");
        menuButton.getStyleClass().add("completion-button");
        menuButton.setOnAction(e -> {
            if (onMainMenu != null) onMainMenu.run();
        });

        buttonBox.getChildren().addAll(retryButton, menuButton);
        return buttonBox;
    }

    public void setOnRetry(Runnable onRetry) {
        this.onRetry = onRetry;
    }

    public void setOnMainMenu(Runnable onMainMenu) {
        this.onMainMenu = onMainMenu;
    }
}