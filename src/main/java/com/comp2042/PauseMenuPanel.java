package com.comp2042;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;

public class PauseMenuPanel extends StackPane {
    private final Runnable onResume;
    private final Runnable onMainMenu;

    public PauseMenuPanel() {
        // Default constructor for FXML
        this(null, null);
    }

    public PauseMenuPanel(Runnable onResume, Runnable onMainMenu) {
        this.onResume = onResume;
        this.onMainMenu = onMainMenu;

        // Semi-transparent background
        this.setStyle("-fx-background-color: rgba(0, 0, 0, 0.85);");
        this.setAlignment(Pos.CENTER);

        VBox contentBox = new VBox(20); // Spacing between elements
        contentBox.setAlignment(Pos.CENTER);

        // 1. Title
        Label pauseLabel = new Label("PAUSED");
        pauseLabel.setStyle("-fx-font-family: 'Let\\'s go Digital', 'Arial'; " +
                "-fx-font-size: 50px; " +
                "-fx-text-fill: white; " +
                "-fx-font-weight: bold; " +
                "-fx-effect: dropshadow(gaussian, cyan, 10, 0, 0, 0);");

        // 2. Resume Button
        Button resumeBtn = createButton("RESUME", "#00ff88");
        resumeBtn.setOnAction(e -> {
            if (this.onResume != null) this.onResume.run();
        });

        // 3. Main Menu Button
        Button menuBtn = createButton("MAIN MENU", "#ff4444");
        menuBtn.setOnAction(e -> {
            if (this.onMainMenu != null) this.onMainMenu.run();
        });

        contentBox.getChildren().addAll(pauseLabel, resumeBtn, menuBtn);
        this.getChildren().add(contentBox);
    }

    // Helper to style buttons consistently
    private Button createButton(String text, String colorHex) {
        Button btn = new Button(text);
        btn.setPrefWidth(200);
        btn.setPrefHeight(40);
        btn.setStyle(
                "-fx-background-color: transparent; " +
                        "-fx-border-color: " + colorHex + "; " +
                        "-fx-border-width: 2; " +
                        "-fx-text-fill: " + colorHex + "; " +
                        "-fx-font-size: 16px; " +
                        "-fx-font-weight: bold; " +
                        "-fx-background-radius: 5; " +
                        "-fx-border-radius: 5; " +
                        "-fx-cursor: hand;"
        );

        // Hover effects
        btn.setOnMouseEntered(e -> btn.setStyle(
                "-fx-background-color: " + colorHex + "; " +
                        "-fx-border-color: " + colorHex + "; " +
                        "-fx-border-width: 2; " +
                        "-fx-text-fill: black; " +
                        "-fx-font-size: 16px; " +
                        "-fx-font-weight: bold; " +
                        "-fx-background-radius: 5; " +
                        "-fx-border-radius: 5; " +
                        "-fx-cursor: hand;"
        ));

        btn.setOnMouseExited(e -> btn.setStyle(
                "-fx-background-color: transparent; " +
                        "-fx-border-color: " + colorHex + "; " +
                        "-fx-border-width: 2; " +
                        "-fx-text-fill: " + colorHex + "; " +
                        "-fx-font-size: 16px; " +
                        "-fx-font-weight: bold; " +
                        "-fx-background-radius: 5; " +
                        "-fx-border-radius: 5; " +
                        "-fx-cursor: hand;"
        ));

        return btn;
    }

    // Setters for actions (used when loaded via FXML)
    public void setOnResume(Runnable onResume) {
        // Re-bind the resume button action
        VBox box = (VBox) this.getChildren().get(0);
        Button btn = (Button) box.getChildren().get(1);
        btn.setOnAction(e -> {
            if (onResume != null) onResume.run();
        });
    }

    public void setOnMainMenu(Runnable onMainMenu) {
        // Re-bind the menu button action
        VBox box = (VBox) this.getChildren().get(0);
        Button btn = (Button) box.getChildren().get(2);
        btn.setOnAction(e -> {
            if (onMainMenu != null) onMainMenu.run();
        });
    }
}
