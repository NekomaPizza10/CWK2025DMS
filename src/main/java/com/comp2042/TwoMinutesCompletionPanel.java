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
        getStylesheets().add(getClass().getResource("/completion.css").toExternalForm());
        getStyleClass().add("completion-overlay");

        // Main Card Container
        VBox card = new VBox();
        card.getStyleClass().add("completion-card");

        // 1. Title
        Label title = new Label("TIME'S UP!");
        title.getStyleClass().add("completion-title");

        // 2. Score Section
        VBox scoreBox = new VBox(5);
        scoreBox.setAlignment(Pos.CENTER);

        Label scoreTitle = new Label("FINAL SCORE");
        scoreTitle.getStyleClass().add("completion-sub-label");

        Label scoreValue = new Label(String.valueOf(finalScore));
        scoreValue.getStyleClass().add("completion-main-stat");

        scoreBox.getChildren().addAll(scoreTitle, scoreValue);

        // 3. High Score Indicator
        if (isNewBest) {
            Label newBestBadge = new Label("NEW HIGH SCORE");
            newBestBadge.getStyleClass().add("completion-new-best");
            card.getChildren().addAll(title, scoreBox, newBestBadge);
        } else {
            card.getChildren().addAll(title, scoreBox);
            if (previousBest != null) {
                Label bestLabel = new Label("Best: " + previousBest);
                bestLabel.getStyleClass().add("completion-best-text");
                card.getChildren().add(bestLabel);
            }
        }

        // 4. Secondary Stats
        Label linesLabel = new Label(linesCleared + " Lines Cleared");
        linesLabel.getStyleClass().add("completion-secondary-info");
        card.getChildren().add(linesLabel);

        // 5. Buttons
        HBox buttonBox = createButtons();
        card.getChildren().add(buttonBox);

        // 6. Hint
        Label hint = new Label("Press 'N' for Quick Restart");
        hint.getStyleClass().add("completion-hint");
        card.getChildren().add(hint);

        getChildren().add(card);
    }

    private HBox createButtons() {
        HBox box = new HBox(15); // Space between buttons
        box.setAlignment(Pos.CENTER);

        Button btnRetry = new Button("PLAY AGAIN");
        btnRetry.getStyleClass().add("completion-button-primary");
        btnRetry.setOnAction(e -> {
            if (onRetry != null) onRetry.run();
        });

        Button btnMenu = new Button("MAIN MENU");
        btnMenu.getStyleClass().add("completion-button-secondary");
        btnMenu.setOnAction(e -> {
            if (onMainMenu != null) onMainMenu.run();
        });

        box.getChildren().addAll(btnRetry, btnMenu);
        return box;
    }

    public void setOnRetry(Runnable onRetry) {
        this.onRetry = onRetry;
    }

    public void setOnMainMenu(Runnable onMainMenu) {
        this.onMainMenu = onMainMenu;
    }
}