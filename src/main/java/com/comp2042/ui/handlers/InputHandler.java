package com.comp2042.ui.handlers;

import com.comp2042.state.GameState;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import java.util.HashSet;
import java.util.Set;

/**
 * Handles all keyboard input and translates it into game actions for the Tetris game.
 * <p>
 * This class is responsible for processing keyboard events and delegating them to
 * the appropriate game actions through a callback interface. It manages the state
 * of various keys to prevent repeated triggering for actions that should only
 * occur once per key press (such as rotation, hard drop, and hold).
 * </p>
 *
 * <p>Supported key bindings:</p>
 * <table border="1">
 *     <caption>Keyboard Controls</caption>
 *     <tr><th>Action</th><th>Primary Key</th><th>Alternative Key</th></tr>
 *     <tr><td>Move Left</td><td>LEFT Arrow</td><td>A</td></tr>
 *     <tr><td>Move Right</td><td>RIGHT Arrow</td><td>D</td></tr>
 *     <tr><td>Rotate</td><td>UP Arrow</td><td>W</td></tr>
 *     <tr><td>Soft Drop</td><td>DOWN Arrow</td><td>S</td></tr>
 *     <tr><td>Hard Drop</td><td>SPACE</td><td>-</td></tr>
 *     <tr><td>Hold Piece</td><td>SHIFT</td><td>C</td></tr>
 *     <tr><td>Pause/Resume</td><td>P</td><td>ESCAPE</td></tr>
 *     <tr><td>Restart</td><td>N</td><td>-</td></tr>
 * </table>
 *
 * <p>The handler respects the current game state and will ignore or modify
 * input handling based on whether the game is paused, in countdown, or game over.</p>

 * @see InputCallback
 * @see GameState
 */
public class InputHandler {

    /** Flag to track if the rotate key is currently pressed to prevent repeated rotations. */
    private boolean rotateKeyPressed = false;

    /** Flag to track if the hard drop key is currently pressed to prevent multiple hard drops. */
    private boolean hardDropKeyPressed = false;

    /** Flag to track if the hold key is currently pressed to prevent repeated hold actions. */
    private boolean holdKeyPressed = false;

    /** Set of currently pressed keys for tracking multiple simultaneous key presses. */
    private Set<KeyCode> pressedKeys = new HashSet<>();

    /** The game state used to determine how to handle input based on current game conditions. */
    private final GameState gameState;

    /** The callback interface for delegating input actions to game logic. */
    private InputCallback callback;

    /**
     * Constructs a new InputHandler with the specified game state.
     *
     * @param gameState the {@link GameState} instance used to determine
     *                  input handling behavior based on game conditions;
     *                  must not be {@code null}
     */
    public InputHandler(GameState gameState) {
        this.gameState = gameState;
    }

    /**
     * Sets the callback interface for handling input actions.
     * <p>
     * The callback will receive notifications when the user performs
     * various input actions such as movement, rotation, dropping, etc.
     * </p>
     *
     * @param callback the {@link InputCallback} implementation to receive
     *                 input action notifications; may be {@code null} to
     *                 disable input handling
     */
    public void setCallback(InputCallback callback) {
        this.callback = callback;
    }

    /**
     * Handles key press events from the game panel.
     * <p>
     * This method processes keyboard input based on the current game state:
     * </p>
     * <ul>
     *     <li>During countdown: All input is consumed and ignored</li>
     *     <li>Restart key (N): Always processed regardless of pause state</li>
     *     <li>Game over: All input except restart is consumed and ignored</li>
     *     <li>During gameplay: Processes movement and action keys</li>
     *     <li>Pause toggle (P/ESC): Processed regardless of pause state</li>
     * </ul>
     *
     * @param keyEvent the {@link KeyEvent} representing the key press;
     *                 will be consumed if handled
     */
    public void handleKeyPress(KeyEvent keyEvent) {
        if (gameState.isCountdownActive()) {
            keyEvent.consume();
            return;
        }

        if (keyEvent.getCode() == KeyCode.N) {
            handleRestartKey();
            keyEvent.consume();
            return;
        }

        if (gameState.isGameOver()) {
            keyEvent.consume();
            return;
        }

        if (!gameState.isPaused()) {
            handleGameplayInput(keyEvent);
        }

        if (keyEvent.getCode() == KeyCode.P || keyEvent.getCode() == KeyCode.ESCAPE) {
            if (callback != null) callback.onTogglePause();
            keyEvent.consume();
        }
    }

    /**
     * Handles key release events from the game panel.
     * <p>
     * This method resets the key state flags for keys that require
     * single-press behavior (rotate, hard drop, hold). This ensures
     * that the player must release and press again to repeat these actions.
     * </p>
     *
     * @param keyEvent the {@link KeyEvent} representing the key release
     */
    public void handleKeyRelease(KeyEvent keyEvent) {
        pressedKeys.remove(keyEvent.getCode());

        if (keyEvent.getCode() == KeyCode.UP || keyEvent.getCode() == KeyCode.W) {
            rotateKeyPressed = false;
        }
        if (keyEvent.getCode() == KeyCode.SPACE) {
            hardDropKeyPressed = false;
        }
        if (keyEvent.getCode() == KeyCode.SHIFT || keyEvent.getCode() == KeyCode.C) {
            holdKeyPressed = false;
        }
    }

    /**
     * Processes gameplay-related input during active gameplay.
     * <p>
     * Handles the following actions:
     * </p>
     * <ul>
     *     <li><b>Move Left</b> (LEFT/A): Continuous movement allowed</li>
     *     <li><b>Move Right</b> (RIGHT/D): Continuous movement allowed</li>
     *     <li><b>Rotate</b> (UP/W): Single press only, must release to rotate again</li>
     *     <li><b>Soft Drop</b> (DOWN/S): Continuous movement allowed</li>
     *     <li><b>Hold</b> (SHIFT/C): Single press only, must release to hold again</li>
     *     <li><b>Hard Drop</b> (SPACE): Single press only, must release to drop again</li>
     * </ul>
     *
     * @param keyEvent the {@link KeyEvent} to process; will be consumed if handled
     */
    private void handleGameplayInput(KeyEvent keyEvent) {
        KeyCode code = keyEvent.getCode();
        pressedKeys.add(code);

        if (code == KeyCode.LEFT || code == KeyCode.A) {
            if (callback != null) callback.onMoveLeft();
            keyEvent.consume();
        }
        if (code == KeyCode.RIGHT || code == KeyCode.D) {
            if (callback != null) callback.onMoveRight();
            keyEvent.consume();
        }
        if ((code == KeyCode.UP || code == KeyCode.W) && !rotateKeyPressed) {
            rotateKeyPressed = true;
            if (callback != null) callback.onRotate();
            keyEvent.consume();
        }
        if (code == KeyCode.DOWN || code == KeyCode.S) {
            if (callback != null) callback.onSoftDrop();
            keyEvent.consume();
        }
        if ((code == KeyCode.SHIFT || code == KeyCode.C) && !holdKeyPressed) {
            holdKeyPressed = true;
            if (callback != null) callback.onHold();
            keyEvent.consume();
        }
        if (code == KeyCode.SPACE && !hardDropKeyPressed) {
            hardDropKeyPressed = true;
            if (callback != null) callback.onHardDrop();
            keyEvent.consume();
        }
    }

    /**
     * Handles the restart key (N) press based on current game state.
     * <p>
     * The restart behavior differs based on game completion status:
     * </p>
     * <ul>
     *     <li><b>Challenge completed or Game over</b>: Triggers restart with countdown</li>
     *     <li><b>During active gameplay</b>: Triggers instant restart without countdown</li>
     * </ul>
     */
    private void handleRestartKey() {
        if (gameState.isChallengeCompleted() || gameState.isGameOver()) {
            if (callback != null) callback.onRestartWithCountdown();
        } else {
            if (callback != null) callback.onRestartInstant();
        }
    }

    /**
     * Callback interface for receiving input action notifications.
     * <p>
     * Implementations of this interface handle the translation of
     * raw input events into game logic operations. Each method
     * corresponds to a specific game action triggered by user input.
     * </p>
     *
     * @see InputHandler#setCallback(InputCallback)
     */
    public interface InputCallback {

        /**
         * Called when the player requests to move the piece left.
         * <p>
         * Triggered by LEFT arrow or A key press.
         * </p>
         */
        void onMoveLeft();

        /**
         * Called when the player requests to move the piece right.
         * <p>
         * Triggered by RIGHT arrow or D key press.
         * </p>
         */
        void onMoveRight();

        /**
         * Called when the player requests to rotate the piece.
         * <p>
         * Triggered by UP arrow or W key press.
         * Only fires once per key press (must release to rotate again).
         * </p>
         */
        void onRotate();

        /**
         * Called when the player requests a soft drop (accelerated descent).
         * <p>
         * Triggered by DOWN arrow or S key press.
         * Can fire continuously while key is held.
         * </p>
         */
        void onSoftDrop();

        /**
         * Called when the player requests a hard drop (instant placement).
         * <p>
         * Triggered by SPACE key press.
         * Only fires once per key press (must release to drop again).
         * </p>
         */
        void onHardDrop();

        /**
         * Called when the player requests to hold the current piece.
         * <p>
         * Triggered by SHIFT or C key press.
         * Only fires once per key press (must release to hold again).
         * </p>
         */
        void onHold();

        /**
         * Called when the player requests to toggle the pause state.
         * <p>
         * Triggered by P or ESCAPE key press.
         * </p>
         */
        void onTogglePause();

        /**
         * Called when the player requests an instant restart during active gameplay.
         * <p>
         * Triggered by N key press when game is in progress.
         * </p>
         */
        void onRestartInstant();

        /**
         * Called when the player requests a restart with countdown.
         * <p>
         * Triggered by N key press when game is over or challenge is completed.
         * </p>
         */
        void onRestartWithCountdown();
    }
}