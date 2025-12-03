package com.comp2042.ui.handlers;

import com.comp2042.model.GameMode;
import com.comp2042.ui.effect.ComboMeterPanel;
import com.comp2042.state.GameState;
import com.comp2042.ui.initialization.GuiController;
import com.comp2042.ui.panel.PauseMenuPanel;
import javafx.scene.layout.StackPane;

/**
 * Handles UI setup and game mode configuration.
 */
public class UISetupHandler {

    private final GuiController controller;

    public UISetupHandler(GuiController controller) {this.controller = controller;}

    public void setupUI() {
        setupGamePanel();
        setupGameOverPanel();
        setupPauseMenuPanel();
    }

    private void setupGamePanel() {
        controller.getGamePanel().setFocusTraversable(true);
        controller.getGamePanel().requestFocus();
    }

    private void setupGameOverPanel() {controller.getGameOverPanel().setVisible(false);}

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

    public void configureGameMode(GameMode mode) {
        GameState gameState = controller.getGameState();

        gameState.setCurrentGameMode(mode);
        gameState.setChallengeCompleted(false);
        gameState.resetScores();

        controller.getUiUpdater().configureForGameMode(mode);

        configureComboMeterForMode(mode);
    }

    private void configureComboMeterForMode(GameMode mode) {
        ComboMeterPanel comboMeterPanel = controller.getComboMeterPanel();

        if (comboMeterPanel == null) {
            return;
        }

        boolean isFortyLines = (mode == GameMode.FORTY_LINES);
        comboMeterPanel.setVisible(!isFortyLines);
        comboMeterPanel.setManaged(!isFortyLines);
    }

    private void removeCompletionPanels() {
        StackPane rootPane = controller.getNavigationHandler().getRootPane();
        controller.getCompletionManager().removeCompletionPanels(rootPane);
    }

    private void resetComboMeter() {
        ComboMeterPanel comboMeterPanel = controller.getComboMeterPanel();
        if (comboMeterPanel != null) {
            comboMeterPanel.reset();
        }
    }
}