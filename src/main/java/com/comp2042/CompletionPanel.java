package com.comp2042;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public class CompletionPanel extends StackPane {

    private Runnable onRetry;
    private Runnable onMainMenu;

    // Constructor matches GuiController usage: (String timeString, boolean isNewBest, String previousBest)
    public CompletionPanel(String timeString, boolean isNewBest, String previousBest) {
        getStylesheets().add(getClass().getResource("/completion.css").toExternalForm());
        getStyleClass().add("completion-overlay");

        // Main Card Container
        VBox card = new VBox();
        card.getStyleClass().add("completion-card");

        // 1. Title
        Label title = new Label("COURSE COMPLETED!");
        title.getStyleClass().add("completion-title");

        // 2. Time Section
        VBox timeBox = new VBox(5);
        timeBox.setAlignment(Pos.CENTER);

        Label timeTitle = new Label("FINAL TIME");
        timeTitle.getStyleClass().add("completion-sub-label");

        Label timeValue = new Label(timeString);
        timeValue.getStyleClass().add("completion-main-stat");

        timeBox.getChildren().addAll(timeTitle, timeValue);

        // 3. Best Time Indicator
        if (isNewBest) {
            Label newBestBadge = new Label("NEW RECORD");
            newBestBadge.getStyleClass().add("completion-new-best");
            card.getChildren().addAll(title, timeBox, newBestBadge);
        } else {
            card.getChildren().addAll(title, timeBox);
            if (previousBest != null) {
                Label bestLabel = new Label("Best: " + previousBest);
                bestLabel.getStyleClass().add("completion-best-text");
                card.getChildren().add(bestLabel);
            }
        }

        // Spacer to balance layout since 40 lines doesn't show score here usually
        Label spacer = new Label("40 Lines Cleared");
        spacer.getStyleClass().add("completion-secondary-info");
        card.getChildren().add(spacer);

        // 4. Buttons
        HBox buttonBox = createButtons();
        card.getChildren().add(buttonBox);

        // 5. Hint
        Label hint = new Label("Press 'N' for Quick Restart");
        hint.getStyleClass().add("completion-hint");
        card.getChildren().add(hint);

        getChildren().add(card);
    }

    private HBox createButtons() {
        HBox box = new HBox(15);
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