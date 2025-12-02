package com.comp2042.ui;

import com.comp2042.controller.GameController;
import com.comp2042.core.Board;
import com.comp2042.event.*;
import com.comp2042.model.*;
import com.comp2042.state.*;

public class GameLogicHandler {

    private static final int FORTY_LINES_GOAL = 40;
    private static final long TWO_MINUTES_MS = 120000;

    private final GameState gameState;
    private final TimerManager timerManager;
    private final ScoringManager scoringManager;
    private final GameRenderer renderer;
    private final UIUpdater uiUpdater;

    private GameController gameController;
    private boolean isProcessing = false;
    private boolean pieceJustSpawned = false;
    private boolean isDisposed = false;

    private Runnable onGameOver;
    private Runnable onChallengeComplete40Lines;
    private Runnable onChallengeComplete2Minutes;
    private Runnable onUpdateNextDisplay;

    private ComboAnimationManager comboAnimation;
    private ComboMeterPanel comboMeter;
    private BoardGlowEffect boardGlow;

    public GameLogicHandler(GameState gameState, TimerManager timerManager,
                            ScoringManager scoringManager, GameRenderer renderer, UIUpdater uiUpdater) {
        this.gameState = gameState;
        this.timerManager = timerManager;
        this.scoringManager = scoringManager;
        this.renderer = renderer;
        this.uiUpdater = uiUpdater;
    }

    public void setGameController(GameController gc) { this.gameController = gc; }
    public void setOnGameOver(Runnable r) { this.onGameOver = r; }
    public void setOnChallengeComplete40Lines(Runnable r) { this.onChallengeComplete40Lines = r; }
    public void setOnChallengeComplete2Minutes(Runnable r) { this.onChallengeComplete2Minutes = r; }
    public void setOnUpdateNextDisplay(Runnable r) { this.onUpdateNextDisplay = r; }

    public void setComboComponents(ComboAnimationManager anim, ComboMeterPanel meter, BoardGlowEffect glow) {
        this.comboAnimation = anim;
        this.comboMeter = meter;
        this.boardGlow = glow;
    }

    public void pauseComboDecay() {
        if (comboMeter != null) comboMeter.pauseDecay();
        if (boardGlow != null) boardGlow.pauseDecay();
    }

    public void resumeComboDecay() {
        if (comboMeter != null) comboMeter.resumeDecay();
        if (boardGlow != null) boardGlow.resumeDecay();
    }

    private Board getBoard() {
        return gameController != null ? gameController.getBoard() : null;
    }

    private boolean isActive() {
        return !isDisposed && gameState != null && !gameState.isPaused() && !gameState.isGameOver();
    }

    public boolean moveBrickHorizontally(int dir) {
        if (isDisposed || (isProcessing && !pieceJustSpawned)) return false;

        Board board = getBoard();
        if (board == null) return false;

        boolean moved = dir < 0 ? board.moveBrickLeft() : board.moveBrickRight();
        if (moved) updateDisplay(board);
        return moved;
    }

    public boolean attemptRotation() {
        if (isDisposed || gameState == null || gameState.isPaused() || gameState.isGameOver()) return false;

        Board board = getBoard();
        if (board == null) return false;

        boolean rotated = board.rotateLeftBrick();
        if (!rotated && pieceJustSpawned) rotated = tryWallKicks(board);
        if (rotated) updateDisplay(board);
        return rotated;
    }

    private boolean tryWallKicks(Board board) {
        int[][] kicks = {{0,1},{-1,0},{1,0},{0,2},{-1,1},{1,1}};
        for (int[] k : kicks) {
            if (tryKick(board, k[0], k[1])) {
                if (board.rotateLeftBrick()) return true;
                tryKick(board, -k[0], -k[1]);
            }
        }
        return false;
    }

    private boolean tryKick(Board board, int dx, int dy) {
        boolean ok = true;
        for (int i = 0; i < Math.abs(dx) && ok; i++) {
            ok = dx < 0 ? board.moveBrickLeft() : board.moveBrickRight();
        }
        for (int i = 0; i < dy && ok; i++) {
            ok = board.moveBrickDown();
        }
        return ok;
    }

    public void moveDown(MoveEvent event) {
        if (isDisposed || !isActive() || isProcessing) return;

        pieceJustSpawned = false;
        Board board = getBoard();
        if (board == null) return;

        boolean canMove = board.moveBrickDown();

        if (!canMove) {
            if (!gameState.isLockDelayActive()) {
                gameState.setLockDelayActive(true);
                gameState.resetLockDelayCount();
                if (timerManager != null) timerManager.startLockDelay(this::executeLock);
            }
        } else {
            if (gameState.isLockDelayActive()) cancelLockDelay();
            if (event != null && event.getEventSource() == EventSource.USER) {
                scoringManager.addSoftDropBonus(1);
                uiUpdater.updateScore(scoringManager.getCurrentScore());
            }
            updateDisplay(board);
        }
    }

    public void handleHardDrop() {
        if (isDisposed || isProcessing) return;

        isProcessing = true;
        pieceJustSpawned = false;

        if (timerManager != null) timerManager.stopDropTimer();
        cancelLockDelay();

        Board board = getBoard();
        if (board == null) { isProcessing = false; return; }

        ViewData data = board.getViewData();
        int[][] matrix = board.getBoardMatrix();
        if (data == null || matrix == null) { isProcessing = false; return; }

        int startY = data.getyPosition();
        int shadowY = calcShadow(data, matrix);
        int dist = Math.max(0, shadowY - startY);

        while (board.moveBrickDown()) {}

        if (dist > 0) {
            scoringManager.addHardDropBonus(dist);
            uiUpdater.updateScore(scoringManager.getCurrentScore());
            if (comboAnimation != null) comboAnimation.shakeOnHardDrop();
        }

        lockAndSpawn(board);
    }

    public void handleHold(Runnable callback) {
        if (isDisposed || gameController == null || gameState.isHoldUsedThisTurn()) return;

        if (gameState.isLockDelayActive()) cancelLockDelay();

        if (gameController.holdBrick()) {
            gameState.setHoldUsedThisTurn(true);
            pieceJustSpawned = true;
            if (callback != null) callback.run();
            Board board = getBoard();
            if (board != null) updateDisplay(board);
            if (onUpdateNextDisplay != null) onUpdateNextDisplay.run();
        }
    }

    private void executeLock() {
        if (isDisposed || gameState.isGameOver() || isProcessing) return;

        isProcessing = true;
        Board board = getBoard();
        if (board != null) lockAndSpawn(board);
        isProcessing = false;
    }

    private void lockAndSpawn(Board board) {
        if (board == null || isDisposed) { isProcessing = false; return; }

        board.mergeBrickToBackground();
        if (gameController != null) gameController.incrementPiecesPlaced();

        ClearRow clearRow = board.clearRows();
        handleLineClears(clearRow);

        gameState.setLockDelayActive(false);
        gameState.resetLockDelayCount();
        gameState.setHoldUsedThisTurn(false);
        if (timerManager != null) timerManager.stopLockDelay();

        if (board.checkGameOver()) {
            isProcessing = false;
            pieceJustSpawned = false;
            if (onGameOver != null) onGameOver.run();
            return;
        }

        board.createNewBrick();
        pieceJustSpawned = true;

        renderer.refreshGameBackground(board.getBoardMatrix());
        updateDisplay(board);
        updateStats();

        if (timerManager != null) {
            timerManager.startDropTimer(gameState.getCurrentDropSpeed(),
                    () -> moveDown(new MoveEvent(EventType.DOWN, EventSource.THREAD)));
        }

        isProcessing = false;
    }

    private void handleLineClears(ClearRow clearRow) {
        if (clearRow != null && clearRow.getLinesRemoved() > 0) {
            int lines = clearRow.getLinesRemoved();
            int score = scoringManager.calculateTetrisScore(lines);
            int combo = 0;

            GameMode mode = gameState.getCurrentGameMode();
            if (mode == GameMode.NORMAL) {
                gameState.setNormalModeScore(gameState.getNormalModeScore() + score);
                combo = gameState.getNormalModeCombo();
            } else if (mode == GameMode.TWO_MINUTES) {
                gameState.setTwoMinutesScore(gameState.getTwoMinutesScore() + score);
                combo = gameState.getTwoMinutesCombo();
            }

            // Trigger combo effects
            if (comboAnimation != null) {
                comboAnimation.triggerComboEffects(combo);
                comboAnimation.shakeOnLineClear(lines);
            }
            if (comboMeter != null) comboMeter.updateCombo(combo);
            if (boardGlow != null) boardGlow.updateGlow(combo);

            uiUpdater.updateScore(scoringManager.getCurrentScore());
            updateSpeed();
        } else {
            scoringManager.resetCombo();
            // Don't reset meter/glow here - they decay on their own
        }
    }

    private void updateStats() {
        if (gameController == null) return;

        int pieces = gameController.getPiecesPlaced();
        int lines = gameController.getLinesCleared();
        uiUpdater.updateStats(pieces, lines, null);
        if (onUpdateNextDisplay != null) onUpdateNextDisplay.run();

        GameMode mode = gameState.getCurrentGameMode();
        if (mode == GameMode.FORTY_LINES && lines >= FORTY_LINES_GOAL && !gameState.isChallengeCompleted()) {
            gameState.setChallengeCompleted(true);
            if (onChallengeComplete40Lines != null) onChallengeComplete40Lines.run();
        }
        if (mode == GameMode.TWO_MINUTES && !gameState.isChallengeCompleted() && timerManager != null) {
            if (timerManager.getElapsedTime() >= TWO_MINUTES_MS) {
                gameState.setChallengeCompleted(true);
                if (onChallengeComplete2Minutes != null) onChallengeComplete2Minutes.run();
            }
        }
    }

    private void updateSpeed() {
        if (gameController == null || timerManager == null) return;

        int lines = gameController.getLinesCleared();
        int level = lines / 10;
        int speed = Math.max(gameState.getMinDropSpeed(),
                gameState.getBaseDropSpeed() - (level * gameState.getSpeedDecreasePerLevel()));
        gameState.setCurrentDropSpeed(speed);

        timerManager.stopDropTimer();
        timerManager.startDropTimer(speed, () -> moveDown(new MoveEvent(EventType.DOWN, EventSource.THREAD)));
    }

    private void updateDisplay(Board board) {
        if (board == null || renderer == null) return;

        ViewData data = board.getViewData();
        int[][] matrix = board.getBoardMatrix();
        if (data == null || matrix == null) return;

        renderer.refreshGameBackground(matrix);
        int shadowY = calcShadow(data, matrix);
        renderer.renderShadow(data, shadowY, matrix);
        renderer.refreshBrick(data);
    }

    private int calcShadow(ViewData brick, int[][] matrix) {
        if (brick == null || matrix == null) return 0;

        int x = brick.getxPosition();
        int y = brick.getyPosition();
        int[][] shape = brick.getBrickData();
        if (shape == null) return y;

        while (!hasCollision(matrix, shape, x, y + 1)) y++;
        return y;
    }

    private boolean hasCollision(int[][] board, int[][] brick, int x, int y) {
        for (int i = 0; i < brick.length; i++) {
            for (int j = 0; j < brick[i].length; j++) {
                if (brick[i][j] != 0) {
                    int row = y + i, col = x + j;
                    if (row >= board.length || col < 0 || col >= board[0].length) return true;
                    if (row >= 0 && board[row][col] != 0) return true;
                }
            }
        }
        return false;
    }

    public void resetLockDelay() {
        if (gameState.getLockDelayResetCount() < gameState.getMaxLockResets()) {
            gameState.incrementLockDelayResetCount();
            if (timerManager != null) {
                timerManager.stopLockDelay();
                timerManager.startLockDelay(this::executeLock);
            }
        } else {executeLock();}
    }

    public void cancelLockDelay() {
        if (timerManager != null) timerManager.stopLockDelay();
        gameState.setLockDelayActive(false);
        gameState.resetLockDelayCount();
    }

    public boolean isProcessingDrop() { return isProcessing; }
    public boolean isPieceJustSpawned() { return pieceJustSpawned; }

    public void dispose() {
        isDisposed = true;
        if (timerManager != null) {
            timerManager.stopDropTimer();
            timerManager.stopLockDelay();
        }
    }
}