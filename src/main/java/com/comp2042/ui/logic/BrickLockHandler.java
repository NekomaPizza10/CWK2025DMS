package com.comp2042.ui.logic;

import com.comp2042.controller.GameController;
import com.comp2042.core.Board;
import com.comp2042.event.*;
import com.comp2042.model.ClearRow;
import com.comp2042.state.*;
import com.comp2042.ui.handlers.UIUpdater;
import com.comp2042.ui.render.GameRenderer;

/**
 * Handles brick locking mechanism including lock delay,
 * piece spawning, and line clear processing.
 */
public class BrickLockHandler {

    private final GameState gameState;
    private final TimerManager timerManager;
    private final GameRenderer renderer;
    private final UIUpdater uiUpdater;
    private final ScoringManager scoringManager;
    private final GameProgressHandler progressHandler;
    private final ComboEffectHandler comboHandler;
    private final ShadowCalculator shadowCalculator;

    private GameController gameController;
    private BrickMovementHandler movementHandler;

    private Runnable onGameOver;
    private Runnable onUpdateNextDisplay;

    private boolean isDisposed = false;

    public BrickLockHandler(GameState gameState, TimerManager timerManager,
                            GameRenderer renderer, UIUpdater uiUpdater,
                            ScoringManager scoringManager, GameProgressHandler progressHandler,
                            ComboEffectHandler comboHandler, ShadowCalculator shadowCalculator) {
        this.gameState = gameState;
        this.timerManager = timerManager;
        this.renderer = renderer;
        this.uiUpdater = uiUpdater;
        this.scoringManager = scoringManager;
        this.progressHandler = progressHandler;
        this.comboHandler = comboHandler;
        this.shadowCalculator = shadowCalculator;
    }

    public void setGameController(GameController gc) {
        this.gameController = gc;
    }

    public void setMovementHandler(BrickMovementHandler handler) {
        this.movementHandler = handler;
    }

    public void setOnGameOver(Runnable r) {
        this.onGameOver = r;
    }

    public void setOnUpdateNextDisplay(Runnable r) {
        this.onUpdateNextDisplay = r;
    }

    private Board getBoard() {
        return gameController != null ? gameController.getBoard() : null;
    }

    /**
     * Starts the lock delay timer.
     */
    public void startLockDelay() {
        if (timerManager != null) {
            timerManager.startLockDelay(this::executeLock);
        }
    }

    public void resetLockDelay() {
        if (gameState.getLockDelayResetCount() < gameState.getMaxLockResets()) {
            gameState.incrementLockDelayResetCount();
            if (timerManager != null) {
                timerManager.stopLockDelay();
                timerManager.startLockDelay(this::executeLock);
            }
        } else {
            executeLock();
        }
    }

    public void cancelLockDelay() {
        if (timerManager != null) {
            timerManager.stopLockDelay();
        }
        gameState.setLockDelayActive(false);
        gameState.resetLockDelayCount();
    }

    private void executeLock() {
        if (isDisposed || gameState.isGameOver() || movementHandler.isProcessing()) {
            return;
        }

        movementHandler.setProcessing(true);
        Board board = getBoard();
        if (board != null) {
            lockAndSpawn(board);
        }
        movementHandler.setProcessing(false);
    }

    public void lockAndSpawn(Board board) {
        if (board == null || isDisposed) {
            movementHandler.setProcessing(false);
            return;
        }

        board.mergeBrickToBackground();
        if (gameController != null) {
            gameController.incrementPiecesPlaced();
        }

        ClearRow clearRow = board.clearRows();
        handleLineClears(clearRow);

        resetLockState();

        if (board.checkGameOver()) {
            movementHandler.setProcessing(false);
            movementHandler.setPieceJustSpawned(false);
            if (onGameOver != null) {
                onGameOver.run();
            }
            return;
        }
        spawnNewBrick(board);
    }

    private void handleLineClears(ClearRow clearRow) {
        if (clearRow == null || clearRow.getLinesRemoved() <= 0) {
            scoringManager.resetCombo();
            return;
        }

        int lines = clearRow.getLinesRemoved();
        int score = scoringManager.calculateTetrisScore(lines);
        int combo = getCurrentCombo();

        comboHandler.triggerComboEffects(combo, lines);

        uiUpdater.updateScore(scoringManager.getCurrentScore());

        progressHandler.updateSpeed();
    }

    private int getCurrentCombo() {
        switch (gameState.getCurrentGameMode()) {
            case NORMAL:
                return gameState.getNormalModeCombo();
            case TWO_MINUTES:
                return gameState.getTwoMinutesCombo();
            default:
                return 0;
        }
    }

    private void resetLockState() {
        gameState.setLockDelayActive(false);
        gameState.resetLockDelayCount();
        gameState.setHoldUsedThisTurn(false);
        if (timerManager != null) {
            timerManager.stopLockDelay();
        }
    }

    private void spawnNewBrick(Board board) {
        board.createNewBrick();
        movementHandler.setPieceJustSpawned(true);

        renderer.refreshGameBackground(board.getBoardMatrix());
        movementHandler.updateDisplay(board);

        progressHandler.updateStats();

        startDropTimer();

        movementHandler.setProcessing(false);
    }

    private void startDropTimer() {
        if (timerManager != null) {
            timerManager.stopDropTimer();
            timerManager.startDropTimer(
                    gameState.getCurrentDropSpeed(),
                    () -> movementHandler.moveDown(new MoveEvent(EventType.DOWN, EventSource.THREAD))
            );
        }
    }

    public void triggerHardDropEffect() {
        comboHandler.triggerHardDropEffect();
    }

    public void dispose() {
        isDisposed = true;
    }
}