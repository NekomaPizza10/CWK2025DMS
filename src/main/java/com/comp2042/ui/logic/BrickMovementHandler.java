package com.comp2042.ui.logic;

import com.comp2042.controller.GameController;
import com.comp2042.core.Board;
import com.comp2042.event.*;
import com.comp2042.model.ViewData;
import com.comp2042.state.*;
import com.comp2042.ui.render.*;
import com.comp2042.ui.handlers.UIUpdater;

/**
 * Handles all brick movement operations including horizontal moves,
 * rotation, soft drop, hard drop, and hold functionality.
 */
public class BrickMovementHandler {

    private static final int[][] WALL_KICKS = {
            {0, 1}, {-1, 0}, {1, 0}, {0, 2}, {-1, 1}, {1, 1}
    };

    private final GameState gameState;
    private final ScoringManager scoringManager;
    private final GameRenderer renderer;
    private final UIUpdater uiUpdater;
    private final ShadowCalculator shadowCalculator;

    private GameController gameController;
    private BrickLockHandler lockHandler;

    private boolean isProcessing = false;
    private boolean pieceJustSpawned = false;
    private boolean isDisposed = false;

    public BrickMovementHandler(GameState gameState, ScoringManager scoringManager,
                                GameRenderer renderer, UIUpdater uiUpdater,
                                ShadowCalculator shadowCalculator) {
        this.gameState = gameState;
        this.scoringManager = scoringManager;
        this.renderer = renderer;
        this.uiUpdater = uiUpdater;
        this.shadowCalculator = shadowCalculator;
    }

    public void setGameController(GameController gc) {
        this.gameController = gc;
    }

    public void setLockHandler(BrickLockHandler lockHandler) {
        this.lockHandler = lockHandler;
    }

    private Board getBoard() {
        return gameController != null ? gameController.getBoard() : null;
    }

    private boolean isActive() {
        return !isDisposed && gameState != null && !gameState.isPaused() && !gameState.isGameOver();
    }

    public boolean moveBrickHorizontally(int dir) {
        if (isDisposed || (isProcessing && !pieceJustSpawned)) {
            return false;
        }

        Board board = getBoard();
        if (board == null) {return false;}

        boolean moved = (dir < 0) ? board.moveBrickLeft() : board.moveBrickRight();
        if (moved) {updateDisplay(board);}
        return moved;
    }

    public boolean attemptRotation() {
        if (isDisposed || !isActive()) {return false;}

        Board board = getBoard();
        if (board == null) {return false;}

        boolean rotated = board.rotateLeftBrick();

        if (!rotated && pieceJustSpawned) {rotated = tryWallKicks(board);}

        if (rotated) {updateDisplay(board);}
        return rotated;
    }

    private boolean tryWallKicks(Board board) {
        for (int[] kick : WALL_KICKS) {
            if (tryKick(board, kick[0], kick[1])) {
                if (board.rotateLeftBrick()) {
                    return true;
                }
                // Undo the kick if rotation still failed
                tryKick(board, -kick[0], -kick[1]);
            }
        }
        return false;
    }

    private boolean tryKick(Board board, int dx, int dy) {
        boolean success = true;

        // Apply horizontal displacement
        for (int i = 0; i < Math.abs(dx) && success; i++) {
            success = (dx < 0) ? board.moveBrickLeft() : board.moveBrickRight();
        }

        // Apply vertical displacement
        for (int i = 0; i < dy && success; i++) {
            success = board.moveBrickDown();
        }

        return success;
    }

    public void moveDown(MoveEvent event) {
        if (isDisposed || !isActive() || isProcessing) {return;}

        pieceJustSpawned = false;
        Board board = getBoard();
        if (board == null) {return;}
        boolean canMove = board.moveBrickDown();
        if (!canMove) {
            handleCannotMoveDown();
        } else {
            handleSuccessfulMoveDown(event, board);
        }
    }

    private void handleCannotMoveDown() {
        if (!gameState.isLockDelayActive()) {
            gameState.setLockDelayActive(true);
            gameState.resetLockDelayCount();
            if (lockHandler != null) {
                lockHandler.startLockDelay();
            }
        }
    }

    private void handleSuccessfulMoveDown(MoveEvent event, Board board) {
        if (gameState.isLockDelayActive()) {lockHandler.cancelLockDelay();}

        // Soft drop bonus for user-initiated drops
        if (event != null && event.getEventSource() == EventSource.USER) {
            scoringManager.addSoftDropBonus(1);
            uiUpdater.updateScore(scoringManager.getCurrentScore());
        }
        updateDisplay(board);
    }

    public void handleHardDrop() {
        if (isDisposed || isProcessing) {return;}
        isProcessing = true;
        pieceJustSpawned = false;
        if (lockHandler != null) {lockHandler.cancelLockDelay();}
        Board board = getBoard();
        if (board == null) {
            isProcessing = false;
            return;
        }
        int dropDistance = calculateDropDistance(board);
        while (board.moveBrickDown()) {}
        // Award hard drop bonus
        if (dropDistance > 0) {
            scoringManager.addHardDropBonus(dropDistance);
            uiUpdater.updateScore(scoringManager.getCurrentScore());
            lockHandler.triggerHardDropEffect();
        }
        lockHandler.lockAndSpawn(board);
    }

    private int calculateDropDistance(Board board) {
        ViewData data = board.getViewData();
        int[][] matrix = board.getBoardMatrix();

        if (data == null || matrix == null) {return 0;}

        int startY = data.getyPosition();
        int shadowY = shadowCalculator.calculateShadowY(data, matrix);
        return Math.max(0, shadowY - startY);
    }

    public void handleHold(Runnable callback) {
        if (isDisposed || gameController == null || gameState.isHoldUsedThisTurn()) {
            return;
        }

        if (gameState.isLockDelayActive()) {
            lockHandler.cancelLockDelay();
        }

        if (gameController.holdBrick()) {
            gameState.setHoldUsedThisTurn(true);
            pieceJustSpawned = true;

            if (callback != null) {
                callback.run();
            }

            Board board = getBoard();
            if (board != null) {
                updateDisplay(board);
            }
        }
    }

    public void updateDisplay(Board board) {
        if (board == null || renderer == null) {
            return;
        }

        ViewData data = board.getViewData();
        int[][] matrix = board.getBoardMatrix();

        if (data == null || matrix == null) {return;}

        renderer.refreshGameBackground(matrix);
        int shadowY = shadowCalculator.calculateShadowY(data, matrix);
        renderer.renderShadow(data, shadowY, matrix);
        renderer.refreshBrick(data);
    }

    public boolean isProcessing() { return isProcessing; }
    public void setProcessing(boolean processing) { this.isProcessing = processing; }
    public boolean isPieceJustSpawned() { return pieceJustSpawned; }
    public void setPieceJustSpawned(boolean spawned) { this.pieceJustSpawned = spawned; }

    public void dispose() {isDisposed = true;}
}