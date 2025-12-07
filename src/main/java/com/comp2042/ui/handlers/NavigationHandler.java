package com.comp2042.ui.handlers;

import com.comp2042.ui.initialization.GuiController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

/**
 * Handles screen navigation and root pane access for the Tetris game application.
 * <p>
 * This class is responsible for managing transitions between different screens
 * (such as navigating from the game view to the main menu) and providing access
 * to the root {@link StackPane} of the scene graph for overlay management.
 * </p>
 *
 * <p>Key responsibilities include:</p>
 * <ul>
 *     <li>Loading and displaying the main menu screen via FXML</li>
 *     <li>Retrieving the root {@link StackPane} for adding overlay panels</li>
 *     <li>Managing scene transitions on the primary stage</li>
 *     <li>Handling navigation errors gracefully</li>
 * </ul>
 *
 * <p>The root pane retrieval supports two strategies:</p>
 * <ol>
 *     <li><b>Direct access</b>: Checks if the scene root is a StackPane</li>
 *     <li><b>Parent traversal</b>: Traverses the parent hierarchy to find the root StackPane</li>
 * </ol>
 *
 * @see GuiController
 * @see StackPane
 */
public class NavigationHandler {

    /**
     * The resource path to the main menu FXML file.
     */
    private static final String MAIN_MENU_FXML = "/MainMenu.fxml";

    /**
     * The width of the main menu window in pixels.
     */
    private static final int MENU_WIDTH = 900;

    /**
     * The height of the main menu window in pixels.
     */
    private static final int MENU_HEIGHT = 700;

    /**
     * The GUI controller providing access to game UI components.
     */
    private final GuiController controller;

    /**
     * Constructs a new NavigationHandler with the specified GUI controller.
     *
     * @param controller the {@link GuiController} instance that provides access
     *                   to game UI components and panels; must not be {@code null}
     */
    public NavigationHandler(GuiController controller) {
        this.controller = controller;
    }

    /**
     * Navigates to the main menu screen.
     * <p>
     * This method loads the main menu FXML file and replaces the current scene
     * with the main menu scene. The new scene is configured with the predefined
     * menu dimensions ({@value #MENU_WIDTH} x {@value #MENU_HEIGHT} pixels).
     * </p>
     *
     * <p>If navigation fails (e.g., FXML file not found or loading error),
     * the error is handled gracefully and logged to the error stream.</p>
     *
     * @see #handleNavigationError(Exception)
     */
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

    /**
     * Retrieves the root {@link StackPane} of the current scene.
     * <p>
     * This method attempts to find the root StackPane using two strategies:
     * </p>
     * <ol>
     *     <li><b>Direct access</b>: Checks if the scene's root node is a StackPane</li>
     *     <li><b>Parent traversal</b>: If direct access fails, traverses up the
     *         parent hierarchy to find a StackPane with no parent (the root)</li>
     * </ol>
     *
     * <p>The root pane is commonly used for adding overlay panels such as
     * pause menus, game over screens, or modal dialogs.</p>
     *
     * @return the root {@link StackPane} of the scene, or {@code null} if:
     *         <ul>
     *             <li>No valid scene exists</li>
     *             <li>The root is not a StackPane</li>
     *             <li>No StackPane root can be found by traversal</li>
     *         </ul>
     */
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

    /**
     * Checks if the game panel has a valid scene attached.
     *
     * @return {@code true} if the game panel's scene is not {@code null},
     *         {@code false} otherwise
     */
    private boolean hasValidScene() {
        return controller.getGamePanel().getScene() != null;
    }

    /**
     * Attempts to get the root pane directly from the scene.
     * <p>
     * This method checks if the scene's root node is an instance of
     * {@link StackPane} and returns it if so.
     * </p>
     *
     * @return the root {@link StackPane} if the scene root is a StackPane,
     *         {@code null} otherwise
     */
    private StackPane getDirectRootPane() {
        Parent root = controller.getGamePanel().getScene().getRoot();
        if (root instanceof StackPane) {
            return (StackPane) root;
        }
        return null;
    }

    /**
     * Finds the root StackPane by traversing up the parent hierarchy.
     * <p>
     * Starting from the game panel's immediate parent, this method traverses
     * up through the scene graph looking for a {@link StackPane} that has
     * no parent (indicating it is the root of the scene).
     * </p>
     *
     * @return the root {@link StackPane} found by traversal, or {@code null}
     *         if no suitable StackPane is found in the hierarchy
     */
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

    /**
     * Determines if the given parent node is the root StackPane.
     * <p>
     * A node is considered the root StackPane if it is an instance of
     * {@link StackPane} and has no parent node.
     * </p>
     *
     * @param parent the {@link Parent} node to check
     * @return {@code true} if the parent is a StackPane with no parent,
     *         {@code false} otherwise
     */
    private boolean isRootStackPane(Parent parent) {
        return parent instanceof StackPane && parent.getParent() == null;
    }

    /**
     * Retrieves the current stage (window) containing the game panel.
     * <p>
     * This method obtains the stage by accessing the window property
     * of the game panel's scene.
     * </p>
     *
     * @return the current {@link Stage} if the game panel has a valid scene,
     *         {@code null} otherwise
     */
    private Stage getCurrentStage() {
        if (controller.getGamePanel().getScene() != null) {
            return (Stage) controller.getGamePanel().getScene().getWindow();
        }
        return null;
    }

    /**
     * Handles navigation errors by logging to the error stream.
     * <p>
     * This method provides a centralized error handling mechanism for
     * navigation failures. It prints an error message and the full
     * stack trace to {@code System.err}.
     * </p>
     *
     * @param ex the {@link Exception} that occurred during navigation
     */
    private void handleNavigationError(Exception ex) {
        System.err.println("Failed to navigate to main menu: " + ex.getMessage());
        ex.printStackTrace();
    }
}