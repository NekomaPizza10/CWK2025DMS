package com.comp2042.ui.handlers;

import com.comp2042.state.GameState;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import java.util.HashSet;
import java.util.Set;

// Handles all keyboard input and translates it into game actions.

public class InputHandler {

    private boolean rotateKeyPressed = false;
    private boolean hardDropKeyPressed = false;
    private boolean holdKeyPressed = false;
    private Set<KeyCode> pressedKeys = new HashSet<>();

    private final GameState gameState;
    private InputCallback callback;

    public InputHandler(GameState gameState) {
        this.gameState = gameState;
    }

    public void setCallback(InputCallback callback) {
        this.callback = callback;
    }

    public void handleKeyPress(KeyEvent keyEvent) {
        if (gameState.isCountdownActive()) { keyEvent.consume(); return; }

        if (keyEvent.getCode() == KeyCode.N) {
            handleRestartKey();
            keyEvent.consume();
            return;
        }

        if (gameState.isGameOver()) { keyEvent.consume(); return; }

        if (!gameState.isPaused()) { handleGameplayInput(keyEvent); }

        if (keyEvent.getCode() == KeyCode.P || keyEvent.getCode() == KeyCode.ESCAPE) {
            if (callback != null) callback.onTogglePause();
            keyEvent.consume();
        }
    }

    public void handleKeyRelease(KeyEvent keyEvent) {
        pressedKeys.remove(keyEvent.getCode());
        if (keyEvent.getCode() == KeyCode.UP || keyEvent.getCode() == KeyCode.W) { rotateKeyPressed = false; }
        if (keyEvent.getCode() == KeyCode.SPACE) { hardDropKeyPressed = false; }
        if (keyEvent.getCode() == KeyCode.SHIFT || keyEvent.getCode() == KeyCode.C) { holdKeyPressed = false; }
    }

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

    private void handleRestartKey() {
        if (gameState.isChallengeCompleted() || gameState.isGameOver()) {
            if (callback != null) callback.onRestartWithCountdown();
        } else {
            if (callback != null) callback.onRestartInstant();
        }
    }

    public interface InputCallback {
        void onMoveLeft();
        void onMoveRight();
        void onRotate();
        void onSoftDrop();
        void onHardDrop();
        void onHold();
        void onTogglePause();
        void onRestartInstant();
        void onRestartWithCountdown();
    }
}