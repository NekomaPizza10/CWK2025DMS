package com.comp2042;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.animation.ScaleTransition;
import javafx.util.Duration;


public class MainMenuController {

    @FXML
    private Button normalModeButton;

    @FXML
    private Button fortyLinesButton;

    @FXML
    private Button twoMinutesButton;

    @FXML
    private Button howToPlayButton;

    @FXML
    private Button exitButton;

    @FXML
    private StackPane howToPlayPanel;

    @FXML
    private VBox mainMenuBox;

    @FXML
    public void initialize() {
        // Add hover effects for each button
        if (normalModeButton != null) {
            addButtonEffects(normalModeButton, "#00ff88", "#00dd77", "#00bb66");
        }
        if (fortyLinesButton != null) {
            addButtonEffects(fortyLinesButton, "#ff9900", "#ee8800", "#dd7700");
        }
        if (twoMinutesButton != null) {
            addButtonEffects(twoMinutesButton, "#00aaff", "#0099ee", "#0088dd");
        }
        if (howToPlayButton != null) {
            addButtonEffects(howToPlayButton, "#666666", "#777777", "#555555");
        }
        if (exitButton != null) {
            addButtonEffects(exitButton, "#ff4444", "#ee3333", "#dd2222");
        }
    }

    private void addButtonEffects(Button button, String normalColor, String hoverColor, String clickColor) {
        String originalStyle = button.getStyle();

        button.setOnMouseEntered(e -> {
            ScaleTransition scale = new ScaleTransition(Duration.millis(100), button);
            scale.setToX(1.05);
            scale.setToY(1.05);
            scale.play();

            if (button == normalModeButton) {
                button.setStyle(originalStyle.replace(normalColor, hoverColor));
            } else {
                button.setStyle(originalStyle
                        .replace("border-color: " + normalColor, "border-color: " + hoverColor)
                        .replace("text-fill: " + normalColor, "text-fill: " + hoverColor));
            }
        });

        button.setOnMouseExited(e -> {
            ScaleTransition scale = new ScaleTransition(Duration.millis(100), button);
            scale.setToX(1.0);
            scale.setToY(1.0);
            scale.play();
            button.setStyle(originalStyle);
        });

        button.setOnMousePressed(e -> {
            ScaleTransition scale = new ScaleTransition(Duration.millis(50), button);
            scale.setToX(0.95);
            scale.setToY(0.95);
            scale.play();

            if (button == normalModeButton) {
                button.setStyle(originalStyle.replace(normalColor, clickColor));
            } else {
                button.setStyle(originalStyle
                        .replace("border-color: " + normalColor, "border-color: " + clickColor)
                        .replace("text-fill: " + normalColor, "text-fill: " + clickColor));
            }
        });

        button.setOnMouseReleased(e -> {
            ScaleTransition scale = new ScaleTransition(Duration.millis(50), button);
            scale.setToX(1.05);
            scale.setToY(1.05);
            scale.play();
        });
    }

    @FXML
    private void handleNormalMode(ActionEvent event) {
        startGame(event, GameMode.NORMAL);
    }

    @FXML
    private void handleFortyLinesMode(ActionEvent event) {
        startGame(event, GameMode.FORTY_LINES);
    }

    @FXML
    private void handleTwoMinutesMode(ActionEvent event) {
        startGame(event, GameMode.TWO_MINUTES);
    }

    private void startGame(ActionEvent event, GameMode mode) {
        try {
            System.out.println("Loading game in " + mode.getDisplayName() + "...");

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/gameLayout.fxml"));
            Parent root = loader.load();

            GuiController guiController = loader.getController();
            guiController.setGameMode(mode); // Set the mode FIRST

            GameController gameController = new GameController(guiController);

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene gameScene = new Scene(root, 900, 700);
            stage.setScene(gameScene);
            stage.setTitle("Tetris - " + mode.getDisplayName());

            System.out.println("Game started in " + mode.getDisplayName());

        } catch (Exception e) {
            System.err.println("Error starting game:");
            e.printStackTrace();
        }
    }

    @FXML
    private void handleHowToPlay(ActionEvent event) {
        // Show the how to play overlay
        if (howToPlayPanel != null) {
            howToPlayPanel.setVisible(true);
            howToPlayPanel.toFront();
        }
    }

    @FXML
    private void handleCloseHowToPlay(ActionEvent event) {
        // Hide the how to play overlay
        if (howToPlayPanel != null) {
            howToPlayPanel.setVisible(false);
        }
    }

    @FXML
    private void handleExit(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();
    }
}