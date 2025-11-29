package com.comp2042.ui;

import com.comp2042.controller.GameController;
import com.comp2042.core.Board;
import com.comp2042.event.*;
import com.comp2042.model.*;
import com.comp2042.state.*;

// Handles game logic operations including movement, locking, and line clearing.
public class GameLogicHandler {

    private static final int FORTY_LINES_GOAL = 3;
    private static final long TWO_MINUTES_MS = 10 * 1000;

    private final GameState gameState;
    private final TimerManager timerManager;
    private final ScoringManager scoringManager;
    private final GameRenderer renderer;
    private final UIUpdater uiUpdater;

    private GameController gameController;
    private boolean isProcessingDrop = false;

    private Runnable onGameOver;
    private Runnable onChallengeComplete40Lines;
    private Runnable onChallengeComplete2Minutes;
    private Runnable onUpdateNextDisplay;

    public GameLogicHandler(GameState gameState, TimerManager timerManager,
                            ScoringManager scoringManager, GameRenderer renderer,
                            UIUpdater uiUpdater) {
        this.gameState = gameState;
        this.timerManager = timerManager;
        this.scoringManager = scoringManager;
        this.renderer = renderer;
        this.uiUpdater = uiUpdater;
    }

    public void setGameController(GameController gameController) { this.gameController = gameController; }
    public void setOnGameOver(Runnable callback) { this.onGameOver = callback; }
    public void setOnChallengeComplete40Lines(Runnable callback) { this.onChallengeComplete40Lines = callback; }
    public void setOnChallengeComplete2Minutes(Runnable callback) { this.onChallengeComplete2Minutes = callback; }
    public void setOnUpdateNextDisplay(Runnable callback) { this.onUpdateNextDisplay = callback; }

    public boolean moveBrickHorizontally(int direction) {
        if (gameController == null) return false;
        Board board = gameController.getBoard();
        boolean moved = direction < 0 ? board.moveBrickLeft() : board.moveBrickRight();
        if (moved) { updateBrickWithShadow(board); return true; }
        return false;
    }

    public boolean attemptRotation() {
        if (gameController == null) return false;
        Board board = gameController.getBoard();
        boolean rotated = board.rotateLeftBrick();
        if (rotated) { updateBrickWithShadow(board); return true; }
        return false;
    }

    public void moveDown(MoveEvent event) {
        if (isProcessingDrop || gameState.isPaused() || gameState.isGameOver() || gameController == null) return;
        Board board = gameController.getBoard();
        boolean canMove = board.moveBrickDown();

        if (!canMove) {
            if (!gameState.isLockDelayActive()) {
                gameState.setLockDelayActive(true);
                gameState.resetLockDelayCount();
                timerManager.startLockDelay(this::executeLock);
            }
        } else {
            if (gameState.isLockDelayActive()) { cancelLockDelay(); }
            if (event.getEventSource() == EventSource.USER) {
                scoringManager.addSoftDropBonus(1);
                uiUpdater.updateScore(scoringManager.getCurrentScore());
            }
            updateBrickWithShadow(board);
        }
    }

    public void handleHardDrop() {
        if (isProcessingDrop || gameController == null) return;
        isProcessingDrop = true;
        timerManager.stopDropTimer();
        cancelLockDelay();

        Board board = gameController.getBoard();
        ViewData currentBrickData = board.getViewData();
        int startY = currentBrickData.getyPosition();
        int shadowY = calculateShadowPosition(currentBrickData, board.getBoardMatrix());
        int dropDistance = shadowY - startY;

        while (board.moveBrickDown()) { }

        if (dropDistance > 0) {
            scoringManager.addHardDropBonus(dropDistance);
            uiUpdater.updateScore(scoringManager.getCurrentScore());
        }
        lockAndSpawnNext(board);
    }

    public void handleHold(Runnable updateHoldDisplayCallback) {
        if (gameController == null || gameState.isHoldUsedThisTurn()) return;
        if (gameState.isLockDelayActive()) { cancelLockDelay(); }
        boolean holdSuccess = gameController.holdBrick();
        if (holdSuccess) {
            gameState.setHoldUsedThisTurn(true);
            if (updateHoldDisplayCallback != null) { updateHoldDisplayCallback.run(); }
            updateBrickWithShadow(gameController.getBoard());
            if (onUpdateNextDisplay != null) { onUpdateNextDisplay.run(); }
        }
    }

    private void executeLock() {
        if (gameState.isGameOver() || isProcessingDrop || gameController == null) return;
        isProcessingDrop = true;
        lockAndSpawnNext(gameController.getBoard());
        isProcessingDrop = false;
    }

    private void lockAndSpawnNext(Board board) {
        board.mergeBrickToBackground();
        gameController.incrementPiecesPlaced();
        ClearRow clearRow = board.clearRows();
        handleLineClears(clearRow);

        gameState.setLockDelayActive(false);
        gameState.resetLockDelayCount();
        gameState.setHoldUsedThisTurn(false);
        timerManager.stopLockDelay();

        if (board.checkGameOver()) {
            isProcessingDrop = false;
            if (onGameOver != null) onGameOver.run();
            return;
        }

        board.createNewBrick();
        renderer.refreshGameBackground(board.getBoardMatrix());
        updateBrickWithShadow(board);
        updateStatsDisplay();

        timerManager.startDropTimer(gameState.getCurrentDropSpeed(), () ->
                moveDown(new MoveEvent(EventType.DOWN, EventSource.THREAD)));
        isProcessingDrop = false;
    }

    private void handleLineClears(ClearRow clearRow) {
        if (clearRow != null && clearRow.getLinesRemoved() > 0) {
            int linesCleared = clearRow.getLinesRemoved();
            int earnedScore = scoringManager.calculateTetrisScore(linesCleared);
            if (gameState.getCurrentGameMode() == GameMode.NORMAL) {
                gameState.setNormalModeScore(gameState.getNormalModeScore() + earnedScore);
            } else if (gameState.getCurrentGameMode() == GameMode.TWO_MINUTES) {
                gameState.setTwoMinutesScore(gameState.getTwoMinutesScore() + earnedScore);
            }
            uiUpdater.updateScore(scoringManager.getCurrentScore());
            updateGameSpeed();
        } else { scoringManager.resetCombo(); }
    }

    private void updateGameSpeed() {
        if (gameState.getCurrentGameMode() != GameMode.NORMAL || gameController == null) return;
        int level = gameController.getLinesCleared() / 10;
        int newSpeed = Math.max(gameState.getMinDropSpeed(),
                gameState.getBaseDropSpeed() - (level * gameState.getSpeedDecreasePerLevel()));
        gameState.setCurrentDropSpeed(newSpeed);
        timerManager.stopDropTimer();
        timerManager.startDropTimer(newSpeed, () -> moveDown(new MoveEvent(EventType.DOWN, EventSource.THREAD)));
    }

    private void updateStatsDisplay() {
        if (gameController == null) return;
        int pieces = gameController.getPiecesPlaced();
        int linesCleared = gameController.getLinesCleared();
        uiUpdater.updateStats(pieces, linesCleared, null);
        if (onUpdateNextDisplay != null) { onUpdateNextDisplay.run(); }

        if (gameState.getCurrentGameMode() == GameMode.FORTY_LINES &&
                linesCleared >= FORTY_LINES_GOAL && !gameState.isChallengeCompleted()) {
            gameState.setChallengeCompleted(true);
            if (onChallengeComplete40Lines != null) { onChallengeComplete40Lines.run(); }
        }
        if (gameState.getCurrentGameMode() == GameMode.TWO_MINUTES && !gameState.isChallengeCompleted()) {
            if (timerManager.getElapsedTime() >= TWO_MINUTES_MS) {
                gameState.setChallengeCompleted(true);
                if (onChallengeComplete2Minutes != null) { onChallengeComplete2Minutes.run(); }
            }
        }
    }

    public void resetLockDelay() {
        if (gameState.getLockDelayResetCount() < gameState.getMaxLockResets()) {
            gameState.incrementLockDelayResetCount();
            timerManager.stopLockDelay();
            timerManager.startLockDelay(this::executeLock);
        } else { executeLock(); }
    }

    public void cancelLockDelay() {
        timerManager.stopLockDelay();
        gameState.setLockDelayActive(false);
        gameState.resetLockDelayCount();
    }

    private void updateBrickWithShadow(Board board) {
        ViewData viewData = board.getViewData();
        renderer.refreshGameBackground(board.getBoardMatrix());
        int shadowY = calculateShadowPosition(viewData, board.getBoardMatrix());
        renderer.renderShadow(viewData, shadowY, board.getBoardMatrix());
        renderer.refreshBrick(viewData);
    }

    private int calculateShadowPosition(ViewData brick, int[][] boardMatrix) {
        int currentX = brick.getxPosition();
        int dropY = brick.getyPosition();
        int[][] brickShape = brick.getBrickData();
        while (!checkCollision(boardMatrix, brickShape, currentX, dropY + 1)) { dropY++; }
        return dropY;
    }

    private boolean checkCollision(int[][] board, int[][] brick, int x, int y) {
        for (int i = 0; i < brick.length; i++) {
            for (int j = 0; j < brick[i].length; j++) {
                if (brick[i][j] != 0) {
                    int boardRow = y + i, boardCol = x + j;
                    if (boardRow >= board.length || boardCol < 0 || boardCol >= board[0].length) return true;
                    if (boardRow >= 0 && board[boardRow][boardCol] != 0) return true;
                }
            }
        }
        return false;
    }

    public boolean isProcessingDrop() { return isProcessingDrop; }
}