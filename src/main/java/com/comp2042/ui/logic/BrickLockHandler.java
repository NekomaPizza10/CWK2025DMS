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

    /**
     * Constructs a {@code BrickLockHandler} with the required game components.
     *
     * @param gameState         the current game state containing game mode and timers.
     * @param timerManager      manages lock delay and drop interval timers.
     * @param renderer          responsible for drawing the game board and background.
     * @param uiUpdater         updates score, combo, and other UI elements.
     * @param scoringManager    handles score calculation and combo tracking.
     * @param progressHandler   updates difficulty/speed progression.
     * @param comboHandler      triggers combo and line clear visual effects.
     * @param shadowCalculator  calculates shadow/ghost piece positions.
     */
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

    /**
     * Assigns the game controller used to retrieve the current board.
     *
     * @param gc the game controller instance.
     */
    public void setGameController(GameController gc) {
        this.gameController = gc;
    }

    /**
     * Assigns the brick movement handler responsible for moving the active piece.
     *
     * @param handler the movement handler.
     */
    public void setMovementHandler(BrickMovementHandler handler) {
        this.movementHandler = handler;
    }

    /**
     * Sets a callback to trigger when a game over condition occurs.
     *
     * @param r a runnable executed upon game over.
     */
    public void setOnGameOver(Runnable r) {
        this.onGameOver = r;
    }

    /**
     * Sets a callback to trigger when the next-brick display should update.
     *
     * @param r the runnable to execute for updating the next preview.
     */
    public void setOnUpdateNextDisplay(Runnable r) {
        this.onUpdateNextDisplay = r;
    }

    /**
     * Retrieves the current board from the assigned game controller.
     *
     * @return the board instance, or {@code null} if no controller is assigned.
     */
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

    /**
     * Locks the active brick into the board, processes line clears,
     * checks for game over, and spawns the next brick if the game continues.
     *
     * @param board the game board where the brick will be merged.
     */
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

    /**
     * Handles line clear events by awarding score, updating combo state,
     * applying visual effects, and adjusting speed progression.
     *
     * @param clearRow contains information about removed rows.
     */
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

    /**
     * Retrieves the current combo value depending on the active game mode.
     *
     * @return the player's current combo count.
     */
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

    /**
     * Spawns a new brick on the board, refreshes the renderer,
     * updates UI/game statistics, and restarts the drop timer.
     *
     * @param board the board on which the new brick will be created.
     */
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