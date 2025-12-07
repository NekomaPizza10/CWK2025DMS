package com.comp2042.ui.handlers;

import com.comp2042.model.GameMode;
import com.comp2042.ui.effect.ComboMeterPanel;
import com.comp2042.state.GameState;
import com.comp2042.ui.initialization.GuiController;
import com.comp2042.ui.panel.PauseMenuPanel;
import javafx.scene.layout.StackPane;

/**
 * Handles UI setup and game mode configuration for the Tetris game.
 * <p>
 * This class is responsible for initializing and configuring various UI components
 * during game startup, as well as adapting the UI based on the selected game mode.
 * It manages the initial state of panels such as the game panel, game over panel,
 * and pause menu panel.
 * </p>
 *
 * <p>Key responsibilities include:</p>
 * <ul>
 *     <li>Setting up the game panel with proper focus handling</li>
 *     <li>Configuring the game over panel's initial visibility</li>
 *     <li>Setting up pause menu panel callbacks for resume, retry, and main menu actions</li>
 *     <li>Configuring UI elements based on the selected {@link GameMode}</li>
 *     <li>Managing combo meter visibility based on game mode requirements</li>
 * </ul>
 *
 * <p>Game mode-specific configurations:</p>
 * <table border="1">
 *     <caption>UI Configuration by Game Mode</caption>
 *     <tr><th>Game Mode</th><th>Combo Meter</th></tr>
 *     <tr><td>Endless</td><td>Visible</td></tr>
 *     <tr><td>Forty Lines</td><td>Hidden</td></tr>
 *     <tr><td>Time Trial</td><td>Visible</td></tr>
 * </table>
 *
 * @see GuiController
 * @see GameMode
 * @see PauseMenuPanel
 */
public class UISetupHandler {

    /**
     * The GUI controller providing access to all game UI components and managers.
     */
    private final GuiController controller;

    /**
     * Constructs a new UISetupHandler with the specified GUI controller.
     *
     * @param controller the {@link GuiController} instance that provides access
     *                   to game UI components and managers; must not be {@code null}
     */
    public UISetupHandler(GuiController controller) {
        this.controller = controller;
    }

    /**
     * Performs the complete UI setup for the game.
     * <p>
     * This method orchestrates the initialization of all major UI components
     * by calling individual setup methods in sequence:
     * </p>
     * <ol>
     *     <li>Game panel setup (focus configuration)</li>
     *     <li>Game over panel setup (initial visibility)</li>
     *     <li>Pause menu panel setup (callbacks and visibility)</li>
     * </ol>
     *
     * <p>This method should be called once during game initialization,
     * typically after all UI components have been created.</p>
     *
     * @see #setupGamePanel()
     * @see #setupGameOverPanel()
     * @see #setupPauseMenuPanel()
     */
    public void setupUI() {
        setupGamePanel();
        setupGameOverPanel();
        setupPauseMenuPanel();
    }

    /**
     * Sets up the game panel with focus handling.
     * <p>
     * Configures the game panel to be focus traversable and immediately
     * requests focus. This ensures that keyboard input is properly
     * captured by the game panel from the start.
     * </p>
     */
    private void setupGamePanel() {
        controller.getGamePanel().setFocusTraversable(true);
        controller.getGamePanel().requestFocus();
    }

    /**
     * Sets up the game over panel's initial state.
     * <p>
     * Hides the game over panel initially. It will be made visible
     * when the game ends.
     * </p>
     */
    private void setupGameOverPanel() {controller.getGameOverPanel().setVisible(false);}

    /**
     * Sets up the pause menu panel with callbacks and initial visibility.
     * <p>
     * This method configures the pause menu panel with the following:
     * </p>
     * <ul>
     *     <li><b>Initial visibility</b>: Hidden by default</li>
     *     <li><b>Resume callback</b>: Toggles pause, hides panel, restores focus</li>
     *     <li><b>Retry callback</b>: Restarts game instantly with cleanup</li>
     *     <li><b>Main Menu callback</b>: Navigates to main menu screen</li>
     * </ul>
     *
     * <p>If the pause menu panel is {@code null}, this method returns
     * immediately without performing any setup.</p>
     */
    private void setupPauseMenuPanel() {
        PauseMenuPanel pauseMenuPanel = controller.getPauseMenuPanel();

        if (pauseMenuPanel == null) {return;}

        pauseMenuPanel.setVisible(false);

        pauseMenuPanel.setOnResume(() -> {
            controller.getFlowManager().togglePause();
            pauseMenuPanel.setVisible(false);
            controller.getGamePanel().requestFocus();
        });

        pauseMenuPanel.setOnRetry(() -> {
            controller.getFlowManager().restartInstantly(() -> {
                removeCompletionPanels();
                resetComboMeter();
            });
        });

        pauseMenuPanel.setOnMainMenu(() -> {
            controller.getNavigationHandler().goToMainMenu();
        });
    }

    /**
     * Configures the game for the specified game mode.
     * <p>
     * This method performs the following configuration steps:
     * </p>
     * <ol>
     *     <li>Sets the current game mode in the game state</li>
     *     <li>Resets the challenge completed flag</li>
     *     <li>Resets all scores to initial values</li>
     *     <li>Configures the UI updater for the game mode</li>
     *     <li>Configures combo meter visibility based on game mode</li>
     * </ol>
     *
     * <p>Different game modes may have different UI requirements. For example,
     * the Forty Lines mode hides the combo meter since it focuses on
     * line clearing rather than combo scoring.</p>
     *
     * @param mode the {@link GameMode} to configure the game for;
     *             determines UI layout and game rules
     * @see GameMode
     * @see #configureComboMeterForMode(GameMode)
     */
    public void configureGameMode(GameMode mode) {
        GameState gameState = controller.getGameState();

        gameState.setCurrentGameMode(mode);
        gameState.setChallengeCompleted(false);
        gameState.resetScores();

        controller.getUiUpdater().configureForGameMode(mode);

        configureComboMeterForMode(mode);
    }

    /**
     * Configures the combo meter panel visibility based on the game mode.
     * <p>
     * The combo meter is hidden for certain game modes where combo scoring
     * is not relevant to the gameplay objective:
     * </p>
     * <ul>
     *     <li><b>Forty Lines mode</b>: Combo meter is hidden (focus on line clearing)</li>
     *     <li><b>Other modes</b>: Combo meter is visible</li>
     * </ul>
     *
     * <p>Both visibility and managed properties are set to ensure proper
     * layout behavior when the combo meter is hidden.</p>
     *
     * @param mode the {@link GameMode} to configure the combo meter for
     */
    private void configureComboMeterForMode(GameMode mode) {
        ComboMeterPanel comboMeterPanel = controller.getComboMeterPanel();

        if (comboMeterPanel == null) {
            return;
        }

        boolean isFortyLines = (mode == GameMode.FORTY_LINES);
        comboMeterPanel.setVisible(!isFortyLines);
        comboMeterPanel.setManaged(!isFortyLines);
    }

    /**
     * Removes game completion panels from the root pane.
     * <p>
     * This method retrieves the root {@link StackPane} from the navigation
     * handler and delegates to the completion manager to remove any
     * visible completion panels (such as game over or victory screens).
     * </p>
     *
     * <p>Typically called during game restart to clean up the UI.</p>
     */
    private void removeCompletionPanels() {
        StackPane rootPane = controller.getNavigationHandler().getRootPane();
        controller.getCompletionManager().removeCompletionPanels(rootPane);
    }

    /**
     * Resets the combo meter panel to its initial state.
     * <p>
     * If a combo meter panel exists, this method resets it to clear
     * any accumulated combo progress. This is typically called during
     * game restart to ensure a fresh start.
     * </p>
     *
     * <p>This method is null-safe and will do nothing if the combo
     * meter panel is {@code null}.</p>
     */
    private void resetComboMeter() {
        ComboMeterPanel comboMeterPanel = controller.getComboMeterPanel();
        if (comboMeterPanel != null) {
            comboMeterPanel.reset();
        }
    }
}