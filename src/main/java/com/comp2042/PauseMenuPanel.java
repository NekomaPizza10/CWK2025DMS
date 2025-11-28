package com.comp2042;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

public class PauseMenuPanel extends VBox {

    private Runnable onResume;
    private Runnable onRetry;
    private Runnable onMainMenu;

    public PauseMenuPanel() {
        // Dark semi-transparent background
        setBackground(new Background(new BackgroundFill(Color.rgb(0, 0, 0, 0.7), null, null)));
        setAlignment(Pos.CENTER);
        setSpacing(20); // Space between elements

        // 1. Title
        Label pauseLabel = new Label("PAUSED");
        // forcing large size for visibility as requested
        pauseLabel.setStyle("-fx-font-family: 'Segoe UI'; -fx-font-size: 50px; -fx-font-weight: bold; -fx-text-fill: white;");

        // 2. Buttons
        Button resumeButton = createButton("RESUME");
        resumeButton.setOnAction(e -> {
            if (onResume != null) onResume.run();
        });

        //Retry Button
        Button retryButton = createButton("RETRY");
        retryButton.setOnAction(e -> {
            if (onRetry != null) onRetry.run();
        });

        Button menuButton = createButton("MAIN MENU");
        menuButton.setOnAction(e -> {
            if (onMainMenu != null) onMainMenu.run();
        });

        getChildren().addAll(pauseLabel, resumeButton, retryButton, menuButton);
    }

    // Helper to keep buttons consistent
    private Button createButton(String text) {
        Button btn = new Button(text);
        btn.setStyle("-fx-background-color: white; -fx-text-fill: black; -fx-font-size: 18px; -fx-font-weight: bold; -fx-min-width: 200px; -fx-background-radius: 30;");

        // Hover effect
        btn.setOnMouseEntered(e -> btn.setStyle("-fx-background-color: #4FC3F7; -fx-text-fill: white; -fx-font-size: 18px; -fx-font-weight: bold; -fx-min-width: 200px; -fx-background-radius: 30;"));
        btn.setOnMouseExited(e -> btn.setStyle("-fx-background-color: white; -fx-text-fill: black; -fx-font-size: 18px; -fx-font-weight: bold; -fx-min-width: 200px; -fx-background-radius: 30;"));

        return btn;
    }

    public void setOnResume(Runnable onResume) {
        this.onResume = onResume;
    }

    public void setOnRetry(Runnable onRetry) {
        this.onRetry = onRetry;
    }

    public void setOnMainMenu(Runnable onMainMenu) {
        this.onMainMenu = onMainMenu;
    }
}