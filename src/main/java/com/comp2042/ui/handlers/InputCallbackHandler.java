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
 * Handles the setup and management of input callbacks for the Tetris game.
 * <p>
 * This class is responsible for configuring all user input interactions,
 * including movement controls, rotation, dropping pieces, holding pieces,
 * pausing the game, and restart functionality. It acts as a bridge between
 * the {@link InputHandler} and the game logic components.
 * </p>
 *
 * <p>Key responsibilities include:</p>
 * <ul>
 *     <li>Setting up callbacks for keyboard input events</li>
 *     <li>Delegating input actions to the appropriate game logic handlers</li>
 *     <li>Managing lock delay resets during piece movement</li>
 *     <li>Handling pause menu visibility and game state transitions</li>
 *     <li>Coordinating restart operations with UI cleanup</li>
 * </ul>
 *
 * @author [Your Name]
 * @version 1.0
 * @see InputHandler
 * @see GuiController
 * @see GameLogicHandler
 */
public class InputCallbackHandler {

    private final GuiController controller;

    /**
     * Constructs a new InputCallbackHandler with the specified GUI controller.
     *
     * @param controller the {@link GuiController} instance that provides access
     *                   to game components and handlers; must not be {@code null}
     */
    public InputCallbackHandler(GuiController controller) {
        this.controller = controller;
    }

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

    /**
     * Handles horizontal movement of the current piece.
     * <p>
     * Attempts to move the piece in the specified direction. If the move
     * is successful and lock delay is currently active, the lock delay
     * timer is reset to give the player more time to adjust the piece.
     * </p>
     *
     * @param direction the direction to move: {@code -1} for left, {@code 1} for right
     */
    private void handleHorizontalMove(int direction) {
        GameLogicHandler logicHandler = getLogicHandler();
        GameState gameState = controller.getGameState();
        if (logicHandler.moveBrickHorizontally(direction) && gameState.isLockDelayActive()) {logicHandler.resetLockDelay();}
    }

    /**
     * Handles rotation of the current piece.
     * <p>
     * Attempts to rotate the piece using the game's rotation system
     * (including wall kicks if applicable). If the rotation is successful
     * and lock delay is active, the lock delay timer is reset.
     * </p>
     */
    private void handleRotation() {
        GameLogicHandler logicHandler = getLogicHandler();
        GameState gameState = controller.getGameState();
        if (logicHandler.attemptRotation() && gameState.isLockDelayActive()) {
            logicHandler.resetLockDelay();
        }
    }

    /**
     * Handles soft drop input from the user.
     * <p>
     * Creates a {@link MoveEvent} with {@link EventType#DOWN} and
     * {@link EventSource#USER} to move the piece down one row.
     * This is typically faster than the automatic gravity drop.
     * </p>
     */
    private void handleSoftDrop() {
        getLogicHandler().moveDown(new MoveEvent(EventType.DOWN, EventSource.USER));
    }

    /**
     * Handles the hold piece functionality.
     * <p>
     * Delegates to the logic handler to swap the current piece with the
     * held piece (or store it if no piece is held). Upon successful hold,
     * updates the hold display and next piece preview in the UI.
     * </p>
     */
    private void handleHold() {
        getLogicHandler().handleHold(() -> {
            controller.getGameViewController().updateHoldDisplay();
            controller.updateNextDisplay();
        });
    }

    /**
     * Handles toggling the game pause state.
     * <p>
     * This method performs the following actions:
     * </p>
     * <ul>
     *     <li>Toggles the pause state via the flow manager</li>
     *     <li>Shows or hides the pause menu panel accordingly</li>
     *     <li>Pauses combo decay when game is paused</li>
     *     <li>Resumes combo decay and requests focus when unpaused</li>
     * </ul>
     */
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

    /**
     * Handles instant game restart without countdown.
     * <p>
     * Immediately restarts the game and cleans up UI elements including
     * completion panels and the combo meter.
     * </p>
     */
    private void handleInstantRestart() {
        controller.getFlowManager().restartInstantly(() -> {
            removeCompletionPanels();
            resetComboMeter();
        });
    }

    /**
     * Handles game restart with a countdown animation.
     * <p>
     * Initiates a restart sequence that includes a countdown before
     * gameplay resumes. Also cleans up completion panels and resets
     * the combo meter.
     * </p>
     */
    private void handleCountdownRestart() {
        controller.getFlowManager().restartWithCountdown(() -> {
            removeCompletionPanels();
            resetComboMeter();
        });
    }

    /**
     * Removes game completion panels from the root pane.
     * <p>
     * Retrieves the root pane from the navigation handler and delegates
     * to the completion manager to remove any visible completion panels
     * (such as game over or victory screens).
     * </p>
     */
    private void removeCompletionPanels() {
        StackPane rootPane = controller.getNavigationHandler().getRootPane();
        controller.getCompletionManager().removeCompletionPanels(rootPane);
    }

    /**
     * Resets the combo meter panel to its initial state.
     * <p>
     * If a combo meter panel exists, this method resets it to clear
     * any accumulated combo progress. Null-safe implementation.
     * </p>
     */
    private void resetComboMeter() {
        ComboMeterPanel comboMeterPanel = controller.getComboMeterPanel();
        if (comboMeterPanel != null) {
            comboMeterPanel.reset();
        }
    }

    /**
     * Sets up key event handlers on the game panel.
     * <p>
     * Configures the game panel to forward key pressed and key released
     * events to the {@link InputHandler} for processing.
     * </p>
     */
    private void setupKeyEventHandlers() {
        InputHandler inputHandler = controller.getInputHandler();
        controller.getGamePanel().setOnKeyPressed(inputHandler::handleKeyPress);
        controller.getGamePanel().setOnKeyReleased(inputHandler::handleKeyRelease);
    }

    /**
     * Retrieves the game logic handler from the controller.
     *
     * @return the {@link GameLogicHandler} instance responsible for
     *         processing game logic operations
     */
    private GameLogicHandler getLogicHandler() {return controller.getLogicHandler();
    }
}