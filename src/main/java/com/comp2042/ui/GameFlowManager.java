package com.comp2042.ui;

import com.comp2042.controller.GameController;
import com.comp2042.state.GameState;
import com.comp2042.state.TimerManager;
import com.comp2042.event.*;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;

public class GameFlowManager {

    private final GameState gameState;
    private final TimerManager timerManager;
    private final GameRenderer renderer;
    private final UIUpdater uiUpdater;
    private final GameLogicHandler logicHandler;

    private GameController gameController;
    private StackPane countdownPanel;
    private Label countdownLabel;
    private Runnable updateNextDisplayCallback;
    private Runnable updateHoldDisplayCallback;
    private Runnable hideGameOverPanelCallback;
    private Runnable hidePausePanelCallback;

    public GameFlowManager(GameState gameState, TimerManager timerManager,
                           GameRenderer renderer, UIUpdater uiUpdater, GameLogicHandler logicHandler) {
        this.gameState = gameState;
        this.timerManager = timerManager;
        this.renderer = renderer;
        this.uiUpdater = uiUpdater;
        this.logicHandler = logicHandler;
    }

    public void setGameController(GameController gameController) { this.gameController = gameController; }
    public void setCountdownComponents(StackPane countdownPanel, Label countdownLabel) {
        this.countdownPanel = countdownPanel;
        this.countdownLabel = countdownLabel;
    }
    public void setUpdateCallbacks(Runnable updateNextDisplay, Runnable updateHoldDisplay) {
        this.updateNextDisplayCallback = updateNextDisplay;
        this.updateHoldDisplayCallback = updateHoldDisplay;
    }
    public void setHideGameOverPanelCallback(Runnable callback) { this.hideGameOverPanelCallback = callback; }
    public void setHidePausePanelCallback(Runnable callback) { this.hidePausePanelCallback = callback; }

    public void startGameWithCountdown() {
        renderer.clearBrickDisplay();
        renderer.getBrickPanel().setOpacity(0);
        renderer.refreshGameBackground(gameController.getBoard().getBoardMatrix());
        showCountdown(() -> {
            renderer.getBrickPanel().setOpacity(1);
            renderer.refreshBrick(gameController.getBoard().getViewData());
            if (updateNextDisplayCallback != null) updateNextDisplayCallback.run();
            if (updateHoldDisplayCallback != null) updateHoldDisplayCallback.run();
            timerManager.startGameTimer();
            timerManager.resetStartTime();
            timerManager.startDropTimer(gameState.getCurrentDropSpeed(), () ->
                    logicHandler.moveDown(new MoveEvent(EventType.DOWN, EventSource.THREAD)));
        });
    }

    public void showCountdown(Runnable onComplete) {
        gameState.setCountdownActive(true);
        countdownPanel.setVisible(true);
        renderer.clearBrickDisplay();
        renderer.getBrickPanel().setOpacity(0);
        timerManager.startCountdown(countdownLabel, () -> {
            countdownPanel.setVisible(false);
            renderer.getBrickPanel().setOpacity(1);
            gameState.setCountdownActive(false);
            onComplete.run();
        });
    }

    public void restartWithCountdown(Runnable removeCompletionPanelCallback) {
        timerManager.stopAllTimers();
        timerManager.stopCountdown();
        logicHandler.cancelLockDelay();
        if (hidePausePanelCallback != null) { hidePausePanelCallback.run(); }
        resetGameState(removeCompletionPanelCallback);
        showCountdown(() -> {
            renderer.getBrickPanel().setOpacity(1);
            renderer.refreshBrick(gameController.getBoard().getViewData());
            if (updateNextDisplayCallback != null) updateNextDisplayCallback.run();
            timerManager.startGameTimer();
            timerManager.startDropTimer(gameState.getCurrentDropSpeed(), () ->
                    logicHandler.moveDown(new MoveEvent(EventType.DOWN, EventSource.THREAD)));
        });
    }

    public void restartInstantly(Runnable removeCompletionPanelCallback) {
        timerManager.stopAllTimers();
        timerManager.stopCountdown();
        logicHandler.cancelLockDelay();
        if (hidePausePanelCallback != null) { hidePausePanelCallback.run(); }
        resetGameState(removeCompletionPanelCallback);
        countdownPanel.setVisible(false);
        renderer.getBrickPanel().setOpacity(1);
        renderer.refreshBrick(gameController.getBoard().getViewData());
        if (updateNextDisplayCallback != null) updateNextDisplayCallback.run();
        if (updateHoldDisplayCallback != null) updateHoldDisplayCallback.run();
        timerManager.startGameTimer();
        timerManager.startDropTimer(gameState.getCurrentDropSpeed(), () ->
                logicHandler.moveDown(new MoveEvent(EventType.DOWN, EventSource.THREAD)));
    }

    private void resetGameState(Runnable removeCompletionPanelCallback) {
        gameState.setCountdownActive(false);
        gameState.setChallengeCompleted(false);
        gameState.setHoldUsedThisTurn(false);
        if (removeCompletionPanelCallback != null) { removeCompletionPanelCallback.run(); }
        gameState.resetScores();
        if (hideGameOverPanelCallback != null) { hideGameOverPanelCallback.run(); }
        renderer.clearBrickDisplay();
        gameController.createNewGame();
        renderer.refreshGameBackground(gameController.getBoard().getBoardMatrix());
        timerManager.resetStartTime();
        uiUpdater.resetStats(gameState.getCurrentGameMode());
        if (updateHoldDisplayCallback != null) updateHoldDisplayCallback.run();
        if (updateNextDisplayCallback != null) updateNextDisplayCallback.run();
        gameState.setPaused(false);
        gameState.setGameOver(false);
        setInitialDropSpeed();
    }

    private void setInitialDropSpeed() {
        gameState.setCurrentDropSpeed(gameState.getBaseDropSpeed());
    }

    public void togglePause() {
        if (gameState.isGameOver() || gameState.isCountdownActive()) return;
        if (gameState.isPaused()) { resumeGame(); } else { pauseGame(); }
    }

    private void pauseGame() {
        gameState.setPaused(true);
        timerManager.pauseDropTimer();
        timerManager.pauseGameTimer();
        if (gameState.isLockDelayActive()) { timerManager.stopLockDelay(); }
    }

    private void resumeGame() {
        gameState.setPaused(false);
        timerManager.resumeDropTimer();
        timerManager.resumeGameTimer();
    }
}