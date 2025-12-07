package com.comp2042;

import com.comp2042.controller.MainMenuController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class Main extends Application {

    /**
     * Starts the JavaFX application.
     * Loads main menu FXML and displays window.
     *
     * @param primaryStage the primary stage
     * @throws Exception if FXML loading fails
     */
    @Override
    public void start(Stage primaryStage) throws Exception {

        // Load from root of resources (no package path)
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/MainMenu.fxml"));
        Parent root = loader.load();

        Scene scene = new Scene(root, 900, 700);
        primaryStage.setTitle("Tetris - Main Menu");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    /**
     * Main entry point.
     * @param args command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

}
