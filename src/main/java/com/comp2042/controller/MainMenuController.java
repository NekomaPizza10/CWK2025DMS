package com.comp2042.controller;

import com.comp2042.ui.initialization.GuiController;
import com.comp2042.model.GameMode;
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

    @FXML private Button normalModeButton;
    @FXML private Button fortyLinesButton;
    @FXML private Button twoMinutesButton;
    @FXML private Button howToPlayButton;
    @FXML private Button exitButton;
    @FXML private StackPane howToPlayPanel;

    @FXML
    public void initialize() {
        // Mode Buttons with NEW colors
        setupButton(normalModeButton, "#EAE2B7"); // Beige
        setupButton(fortyLinesButton, "#FCBF49"); // Orange-Yellow
        setupButton(twoMinutesButton, "#F77F00"); // Orange

        // System Buttons with NEW colors
        setupButton(howToPlayButton, "#93E1D8");  // Light Teal
        setupButton(exitButton, "#ff4444");       // Red
    }

    private void setupButton(Button btn, String colorHex) {
        if (btn == null) return;
        // Base Style
        String baseStyle = String.format(
                "-fx-font-family: 'Segoe UI'; -fx-font-size: %s; -fx-font-weight: bold; " +
                        "-fx-background-color: transparent; " +
                        "-fx-border-color: %s; -fx-border-width: 2; " +
                        "-fx-text-fill: %s; " +
                        "-fx-background-radius: %s; -fx-border-radius: %s; -fx-cursor: hand;",
                (btn == howToPlayButton || btn == exitButton) ? "14px" : "18px",
                colorHex, colorHex,
                (btn == howToPlayButton || btn == exitButton) ? "30" : "10",
                (btn == howToPlayButton || btn == exitButton) ? "30" : "10"
        );

        // Hover Style
        String hoverStyle = String.format(
                "-fx-font-family: 'Segoe UI'; -fx-font-size: %s; -fx-font-weight: bold; " +
                        "-fx-background-color: %s; " +
                        "-fx-border-color: %s; -fx-border-width: 2; " +
                        "-fx-text-fill: #1a1a1a; " + // Dark text for contrast
                        "-fx-background-radius: %s; -fx-border-radius: %s; -fx-cursor: hand; " +
                        "-fx-effect: dropshadow(gaussian, %s, 10, 0, 0, 0);",
                (btn == howToPlayButton || btn == exitButton) ? "14px" : "18px",
                colorHex, colorHex,
                (btn == howToPlayButton || btn == exitButton) ? "30" : "10",
                (btn == howToPlayButton || btn == exitButton) ? "30" : "10",
                colorHex
        );

        // Set initial style
        btn.setStyle(baseStyle);

        // Animations & Style Swapping
        btn.setOnMouseEntered(e -> {
            btn.setStyle(hoverStyle);
            ScaleTransition st = new ScaleTransition(Duration.millis(100), btn);
            st.setToX(1.03);
            st.setToY(1.03);
            st.play();
        });

        btn.setOnMouseExited(e -> {
            btn.setStyle(baseStyle);
            ScaleTransition st = new ScaleTransition(Duration.millis(100), btn);
            st.setToX(1.0);
            st.setToY(1.0);
            st.play();
        });

        btn.setOnMousePressed(e -> {
            ScaleTransition st = new ScaleTransition(Duration.millis(50), btn);
            st.setToX(0.96);
            st.setToY(0.96);
            st.play();
        });

        btn.setOnMouseReleased(e -> {
            ScaleTransition st = new ScaleTransition(Duration.millis(50), btn);
            st.setToX(1.03);
            st.setToY(1.03);
            st.play();
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
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/gameLayout.fxml"));
            Parent root = loader.load();

            GuiController guiController = loader.getController();
            guiController.setGameMode(mode);

            GameController gameController = new GameController(guiController);

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene gameScene = new Scene(root, 900, 700);
            stage.setScene(gameScene);
            stage.setTitle("Tetris - " + mode.getDisplayName());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleHowToPlay(ActionEvent event) {
        if (howToPlayPanel != null) {
            howToPlayPanel.setVisible(true);
            howToPlayPanel.toFront();
        }
    }

    @FXML
    private void handleCloseHowToPlay(ActionEvent event) {
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