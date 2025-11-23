package com.comp2042;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;


public class MainMenuController {

    @FXML
    private void handlePlay(ActionEvent event) {
        try {
            // Load game layout
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/gameLayout.fxml"));
            Parent root = loader.load();

            // Get controller and initialize game
            GuiController guiController = loader.getController();
            GameController gameController = new GameController(guiController);

            // Switch scene
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene gameScene = new Scene(root, 900, 700);
            stage.setScene(gameScene);
            stage.setTitle("Tetris Game");

        } catch (Exception e) {
            System.err.println("Error starting game:");
            e.printStackTrace();
        }
    }

    @FXML
    private void handleHowToPlay(ActionEvent event) {
        Stage helpStage = new Stage();
        helpStage.setTitle("How to Play");

        javafx.scene.control.Label helpText = new javafx.scene.control.Label(
                "OBJECTIVE:\n" +
                        "Arrange falling blocks to create complete horizontal lines.\n\n" +
                        "CONTROLS:\n" +
                        "← / A : Move Left\n" +
                        "→ / D : Move Right\n" +
                        "↑ / W : Rotate\n" +
                        "↓ / S : Soft Drop\n" +
                        "SPACE : Hard Drop\n" +
                        "SHIFT / C : Hold piece\n" +
                        "N : New Game\n\n" +
                        "Clear 40 lines to win!"
        );
        helpText.setStyle("-fx-font-size: 14px; -fx-text-fill: white; -fx-padding: 20;");

        javafx.scene.layout.VBox helpLayout = new javafx.scene.layout.VBox(helpText);
        helpLayout.setStyle("-fx-background-color: #1a1a1a;");
        helpLayout.setAlignment(javafx.geometry.Pos.CENTER);

        Scene helpScene = new Scene(helpLayout, 400, 400);
        helpStage.setScene(helpScene);
        helpStage.show();
    }

    @FXML
    private void handleExit(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();
    }
}
