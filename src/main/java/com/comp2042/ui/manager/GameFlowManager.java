package com.comp2042.ui.manager;

import com.comp2042.controller.GameController;
import com.comp2042.state.GameState;
import com.comp2042.state.TimerManager;
import com.comp2042.event.*;
import com.comp2042.ui.handlers.UIUpdater;
import com.comp2042.ui.logic.GameLogicHandler;
import com.comp2042.ui.render.GameRenderer;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;

/**
 * Manages the overall flow of gameplay, including starting the game with a
 * countdown, restarting rounds, pausing/resuming, and coordinating updates
 * between game logic, timers, UI displays, and rendering.
 */
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

    /**
     * Creates a GameFlowManager responsible for orchestrating gameplay flow,
     * coordinating timers, visuals, and logic updates.
     *
     * @param gameState   the state of the current game session
     * @param timerManager handles game timers (game, drop, countdown)
     * @param renderer     the game renderer used for updating visuals
     * @param uiUpdater    updates UI components such as stats and previews
     * @param logicHandler handles movement and game logic operations
     */
    public GameFlowManager(GameState gameState, TimerManager timerManager,
                           GameRenderer renderer, UIUpdater uiUpdater, GameLogicHandler logicHandler) {
        this.gameState = gameState;
        this.timerManager = timerManager;
        this.renderer = renderer;
        this.uiUpdater = uiUpdater;
        this.logicHandler = logicHandler;
    }

    /**
     * Sets the active game controller to be used for accessing board and gameplay data.
     *
     * @param gameController the game controller instance
     */
    public void setGameController(GameController gameController) { this.gameController = gameController; }

    /**
     * Sets the UI elements used for the countdown sequence.
     *
     * @param countdownPanel the root container of the countdown UI
     * @param countdownLabel the label displaying countdown numbers
     */
    public void setCountdownComponents(StackPane countdownPanel, Label countdownLabel) {
        this.countdownPanel = countdownPanel;
        this.countdownLabel = countdownLabel;
    }

    /**
     * Sets callbacks that update the Next and Hold displays when needed.
     *
     * @param updateNextDisplay callback to refresh next-piece UI
     * @param updateHoldDisplay callback to refresh hold-piece UI
     */
    public void setUpdateCallbacks(Runnable updateNextDisplay, Runnable updateHoldDisplay) {
        this.updateNextDisplayCallback = updateNextDisplay;
        this.updateHoldDisplayCallback = updateHoldDisplay;
    }

    /**
     * Sets callback used to hide the game-over panel when restarting the game.
     *
     * @param callback the runnable to execute
     */
    public void setHideGameOverPanelCallback(Runnable callback) { this.hideGameOverPanelCallback = callback; }

    /**
     * Sets callback used to hide the pause panel when unpausing or restarting.
     *
     * @param callback the runnable to execute
     */
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

    /**
     * Displays the countdown UI and runs a callback once it completes.
     *
     * @param onComplete the action to run when the countdown finishes
     */
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

    /**
     * Restarts the game with a countdown sequence.
     * Resets game state, clears displays, removes completion panels,
     * and starts a new round after the countdown finishes.
     *
     * @param removeCompletionPanelCallback callback that removes any completion UI
     */
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

    /**
     * Restarts the game immediately without showing a countdown.
     *
     * @param removeCompletionPanelCallback callback that removes any completion UI
     */
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

    /**
     * Resets all relevant game state variables, UI elements, and timers
     * in preparation for restarting a game.
     *
     * @param removeCompletionPanelCallback removes completion panels if present
     */
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