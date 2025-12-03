package com.comp2042.ui.handlers;

import com.comp2042.ui.initialization.GuiController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

/**
 * Handles screen navigation and root pane access.
 */
public class NavigationHandler {

    private static final String MAIN_MENU_FXML = "/MainMenu.fxml";
    private static final int MENU_WIDTH = 900;
    private static final int MENU_HEIGHT = 700;

    private final GuiController controller;

    public NavigationHandler(GuiController controller) {
        this.controller = controller;
    }

    public void goToMainMenu() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(MAIN_MENU_FXML));
            Parent menuRoot = loader.load();

            Stage stage = getCurrentStage();
            if (stage != null) {
                stage.setScene(new Scene(menuRoot, MENU_WIDTH, MENU_HEIGHT));
            }
        } catch (Exception ex) {
            handleNavigationError(ex);
        }
    }

    public StackPane getRootPane() {
        if (!hasValidScene()) {
            return null;
        }

        // Try direct root access first
        StackPane directRoot = getDirectRootPane();
        if (directRoot != null) {
            return directRoot;
        }

        // Fall back to parent traversal
        return findRootPaneByTraversal();
    }

    private boolean hasValidScene() {
        return controller.getGamePanel().getScene() != null;
    }

    private StackPane getDirectRootPane() {
        Parent root = controller.getGamePanel().getScene().getRoot();
        if (root instanceof StackPane) {
            return (StackPane) root;
        }
        return null;
    }

    private StackPane findRootPaneByTraversal() {
        Parent parent = controller.getGamePanel().getParent();

        while (parent != null) {
            if (isRootStackPane(parent)) {
                return (StackPane) parent;
            }
            parent = parent.getParent();
        }

        return null;
    }

    private boolean isRootStackPane(Parent parent) {
        return parent instanceof StackPane && parent.getParent() == null;
    }

    private Stage getCurrentStage() {
        if (controller.getGamePanel().getScene() != null) {
            return (Stage) controller.getGamePanel().getScene().getWindow();
        }
        return null;
    }

    private void handleNavigationError(Exception ex) {
        System.err.println("Failed to navigate to main menu: " + ex.getMessage());
        ex.printStackTrace();
    }
}