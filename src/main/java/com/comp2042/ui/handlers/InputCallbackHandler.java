package com.comp2042.ui.handlers;

import com.comp2042.event.*;
import com.comp2042.state.GameState;
import com.comp2042.ui.initialization.GuiController;
import com.comp2042.ui.logic.GameLogicHandler;
import com.comp2042.ui.manager.GameFlowManager;
import com.comp2042.ui.effect.ComboMeterPanel;
import com.comp2042.ui.panel.PauseMenuPanel;
import javafx.scene.layout.StackPane;

/**
 * Handles setup and management of input callbacks.
 */
public class InputCallbackHandler {

    private final GuiController controller;

    public InputCallbackHandler(GuiController controller) {this.controller = controller;}

    public void setupInputCallbacks() {
        InputHandler inputHandler = controller.getInputHandler();

        inputHandler.setCallback(new InputHandler.InputCallback() {
            @Override
            public void onMoveLeft() {handleHorizontalMove(-1);}

            @Override
            public void onMoveRight() {handleHorizontalMove(1);}

            @Override
            public void onRotate() {handleRotation();}

            @Override
            public void onSoftDrop() {handleSoftDrop();}

            @Override
            public void onHardDrop() {getLogicHandler().handleHardDrop();}

            @Override
            public void onHold() {handleHold();}

            @Override
            public void onTogglePause() {handlePauseToggle();}

            @Override
            public void onRestartInstant() {handleInstantRestart();}

            @Override
            public void onRestartWithCountdown() {handleCountdownRestart();}
        });

        setupKeyEventHandlers();
    }

    private void handleHorizontalMove(int direction) {
        GameLogicHandler logicHandler = getLogicHandler();
        GameState gameState = controller.getGameState();
        if (logicHandler.moveBrickHorizontally(direction) && gameState.isLockDelayActive()) {logicHandler.resetLockDelay();}
    }

    private void handleRotation() {
        GameLogicHandler logicHandler = getLogicHandler();
        GameState gameState = controller.getGameState();
        if (logicHandler.attemptRotation() && gameState.isLockDelayActive()) {logicHandler.resetLockDelay();}
    }

    private void handleSoftDrop() {
        getLogicHandler().moveDown(new MoveEvent(EventType.DOWN, EventSource.USER));
    }

    private void handleHold() {
        getLogicHandler().handleHold(() -> {
            controller.getGameViewController().updateHoldDisplay();
            controller.updateNextDisplay();
        });
    }

    private void handlePauseToggle() {
        GameFlowManager flowManager = controller.getFlowManager();
        GameState gameState = controller.getGameState();
        PauseMenuPanel pauseMenuPanel = controller.getPauseMenuPanel();

        flowManager.togglePause();
        pauseMenuPanel.setVisible(gameState.isPaused());

        if (gameState.isPaused()) {
            getLogicHandler().pauseComboDecay();
        } else {
            getLogicHandler().resumeComboDecay();
            controller.getGamePanel().requestFocus();
        }
    }

    private void handleInstantRestart() {
        controller.getFlowManager().restartInstantly(() -> {
            removeCompletionPanels();
            resetComboMeter();
        });
    }

    private void handleCountdownRestart() {
        controller.getFlowManager().restartWithCountdown(() -> {
            removeCompletionPanels();
            resetComboMeter();
        });
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

    private void setupKeyEventHandlers() {
        InputHandler inputHandler = controller.getInputHandler();
        controller.getGamePanel().setOnKeyPressed(inputHandler::handleKeyPress);
        controller.getGamePanel().setOnKeyReleased(inputHandler::handleKeyRelease);
    }

    private GameLogicHandler getLogicHandler() {return controller.getLogicHandler();}
}