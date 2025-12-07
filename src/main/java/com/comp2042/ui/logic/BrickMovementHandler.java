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

    /**
     * Constructs a {@code BrickMovementHandler} with all required gameplay components.
     *
     * @param gameState        the current game state used to track pause, lock delay, etc.
     * @param scoringManager   the scoring manager responsible for updating score values.
     * @param renderer         the renderer that draws the brick and shadow.
     * @param uiUpdater        updates the UI for score and visuals.
     * @param shadowCalculator calculates the shadow (ghost piece) position.
     */
    public BrickMovementHandler(GameState gameState, ScoringManager scoringManager,
                                GameRenderer renderer, UIUpdater uiUpdater,
                                ShadowCalculator shadowCalculator) {
        this.gameState = gameState;
        this.scoringManager = scoringManager;
        this.renderer = renderer;
        this.uiUpdater = uiUpdater;
        this.shadowCalculator = shadowCalculator;
    }

    /**
     * Sets the game controller from which the board is retrieved.
     *
     * @param gc the game controller to assign.
     */
    public void setGameController(GameController gc) {
        this.gameController = gc;
    }

    /**
     * Sets the brick lock handler used when the piece finishes falling and must lock.
     *
     * @param lockHandler the lock handler to use.
     */
    public void setLockHandler(BrickLockHandler lockHandler) {
        this.lockHandler = lockHandler;
    }

    /**
     * Retrieves the active game board through the game controller.
     *
     * @return the current board, or {@code null} if not available.
     */
    private Board getBoard() {
        return gameController != null ? gameController.getBoard() : null;
    }

    /**
     * Determines whether movement is allowed based on active state,
     * pause status, and disposal status.
     *
     * @return {@code true} if the handler can process movement.
     */
    private boolean isActive() {
        return !isDisposed && gameState != null && !gameState.isPaused() && !gameState.isGameOver();
    }

    /**
     * Moves the active brick horizontally.
     *
     * @param dir direction of movement: negative for left, positive for right.
     * @return {@code true} if the move was successful; {@code false} otherwise.
     */
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

    /**
     * Attempts to rotate the current brick. If rotation fails and the brick
     * has just spawned, wall kicks will be attempted.
     *
     * @return {@code true} if rotation succeeds; {@code false} if it fails.
     */
    public boolean attemptRotation() {
        if (isDisposed || !isActive()) {return false;}

        Board board = getBoard();
        if (board == null) {return false;}

        boolean rotated = board.rotateLeftBrick();

        if (!rotated && pieceJustSpawned) {rotated = tryWallKicks(board);}

        if (rotated) {updateDisplay(board);}
        return rotated;
    }

    /**
     * Attempts wall-kick adjustments when a rotation fails.
     *
     * @param board the game board.
     * @return {@code true} if the rotation becomes possible after kicks.
     */
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

    /**
     * Attempts a single kick displacement in the given direction.
     *
     * @param board the board on which the kick is applied.
     * @param dx horizontal displacement (negative = left, positive = right).
     * @param dy vertical displacement (positive = downward).
     * @return {@code true} if the displacement was possible.
     */
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

    /**
     * Moves the brick downward by one cell. If the brick cannot move down,
     * lock delay handling begins.
     *
     * @param event the movement event indicating source (user or thread).
     */
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
        Board board = getBoard();
        if (board != null && isBrickTouchingGround(board)) {
            // Instant lock - cancel any existing lock delay and execute immediately
            if (lockHandler != null) {
                lockHandler.cancelLockDelay();
                lockHandler.lockAndSpawn(board);
            }
            return;
        }
        if (!gameState.isLockDelayActive()) {
            gameState.setLockDelayActive(true);
            gameState.resetLockDelayCount();
            if (lockHandler != null) {
                lockHandler.startLockDelay();
            }
        }
    }
    private boolean isBrickTouchingGround(Board board) {
        ViewData data = board.getViewData();
        if (data == null) return false;

        int[][] matrix = board.getBoardMatrix();
        if (matrix == null) return false;

        int currentY = data.getyPosition();
        int shadowY = shadowCalculator.calculateShadowY(data, matrix);

        // If current position equals shadow position, brick is touching ground
        return currentY == shadowY;
    }

    /**
     * Handles logic when the brick successfully moves downward.
     * Awards soft drop score for user-initiated drops and updates the display.
     *
     * @param event the movement event.
     * @param board the board containing the active brick.
     */
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

    /**
     * Calculates how far the brick will fall during a hard drop.
     *
     * @param board the board containing the active brick.
     * @return the number of rows the brick can fall.
     */
    private int calculateDropDistance(Board board) {
        ViewData data = board.getViewData();
        int[][] matrix = board.getBoardMatrix();

        if (data == null || matrix == null) {return 0;}

        int startY = data.getyPosition();
        int shadowY = shadowCalculator.calculateShadowY(data, matrix);
        return Math.max(0, shadowY - startY);
    }

    /**
     * Handles the hold feature, allowing the player to store or swap the current brick.
     * Executes an optional callback after a successful hold.
     *
     * @param callback code executed after holding the brick.
     */
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

    /**
     * Updates the rendering of the brick, background, and shadow.
     *
     * @param board the board containing the active brick.
     */
    public void updateDisplay(Board board) {
        if (board == null || renderer == null) {
            return;
        }

        ViewData data = board.getViewData();
        int[][] matrix = board.getBoardMatrix();

        if (data == null || matrix == null) {return;}

        renderer.refreshGameBackground(matrix);
        int shadowY = shadowCalculator.calculateShadowY(data, matrix);
        if (shadowY != data.getyPosition()) {renderer.renderShadow(data, shadowY, matrix);}
        renderer.refreshBrick(data);
    }

    /**
     * Returns whether this handler is currently processing an operation.
     *
     * @return {@code true} if processing; otherwise {@code false}.
     */
    public boolean isProcessing() { return isProcessing; }
    /**
     * Sets whether this handler is currently processing movement.
     *
     * @param processing {@code true} to mark processing active.
     */
    public void setProcessing(boolean processing) { this.isProcessing = processing; }
    /**
     * Indicates whether the current brick has just spawned.
     *
     * @return {@code true} if the piece just spawned.
     */
    public boolean isPieceJustSpawned() { return pieceJustSpawned; }
    /**
     * Marks whether the piece has just spawned.
     *
     * @param spawned {@code true} if the piece just spawned.
     */
    public void setPieceJustSpawned(boolean spawned) { this.pieceJustSpawned = spawned; }

    public void dispose() {isDisposed = true;}
}