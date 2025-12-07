package com.comp2042.controller;

import com.comp2042.core.Board;
import com.comp2042.core.SimpleBoard;
import com.comp2042.model.*;
import com.comp2042.event.*;
import com.comp2042.ui.initialization.GuiController;

import java.util.List;

/**
 * Main game controller that implements the InputEventListener interface.
 * This class acts as the bridge between user input and game logic,
 * managing the game board and coordinating responses to player actions.
 *
 * Responsibilities include:
 *
 *   Processing movement events (down, left, right, rotate)
 *   Handling hard drops and hold functionality
 *   Managing game state transitions
 *   Tracking game statistics (pieces placed, lines cleared)
 *
 * @see InputEventListener
 * @see Board
 */

public class GameController implements InputEventListener {

    private Board board = new SimpleBoard(10, 25);
    private int piecesPlaced = 0;
    private final GuiController viewGuiController;

    public GameController(GuiController c) {
        viewGuiController = c;
        board.createNewBrick();
        viewGuiController.setEventListener(this);
        viewGuiController.initGameView(board.getBoardMatrix(), board.getViewData());
        viewGuiController.bindScore(board.getScore().scoreProperty());
        viewGuiController.updateNextDisplay();
    }

    /**
     * Returns the current game board instance.
     * @return the Board managing the game state
     */

    public Board getBoard() { return this.board; }

    /**
     * Handles the down movement event for the current brick.
     * Awards 1 point for user-initiated soft drops.
     *
     * @param event the movement event containing source information
     * @return DownData containing clear row information and updated view data
     */

    @Override
    public DownData onDownEvent(MoveEvent event) {
        boolean canMove = board.moveBrickDown();
        if (canMove && event.getEventSource() == EventSource.USER) { board.getScore().add(1); }
        return new DownData(null, board.getViewData());
    }

    /**
     * Handles the left movement event for the current brick.
     *
     * @param event the movement event
     * @return ViewData containing the updated brick position
     */

    @Override
    public ViewData onLeftEvent(MoveEvent event) {
        board.moveBrickLeft();
        return board.getViewData();
    }

    /**
     * Handles the right movement event for the current brick.
     *
     * @param event the movement event
     * @return ViewData containing the updated brick position
     */

    @Override
    public ViewData onRightEvent(MoveEvent event) {
        board.moveBrickRight();
        return board.getViewData();
    }

    /**
     * Handles the rotation event for the current brick.
     *
     * @param event the movement event
     * @return ViewData containing the updated brick orientation
     */

    @Override
    public ViewData onRotateEvent(MoveEvent event) {
        board.rotateLeftBrick();
        return board.getViewData();
    }

    /**
     * Creates a new game by resetting all game state.
     * Clears the board, resets statistics, and spawns a new brick.
     */

    @Override
    public void createNewGame() {
        piecesPlaced = 0;
        board.newGame();
        viewGuiController.refreshGameBackground(board.getBoardMatrix());
    }

    public boolean holdBrick() {
        board.holdCurrentBrick();
        viewGuiController.refreshGameBackground(board.getBoardMatrix());
        return true;
    }

    public int[][] getHoldBrickData() { return board.getHoldBrickData(); }

    public List<int[][]> getNextBricksData() { return board.getNextBricksData(5); }

    /**
     * Returns the number of pieces placed on the board.
     *
     * @return the total count of placed pieces
     */

    public int getPiecesPlaced() { return piecesPlaced; }

    /**
     * Increments the pieces placed counter by one.
     * Called when a brick is merged to the background.
     */

    public void incrementPiecesPlaced() { piecesPlaced++; }

    public int getLinesCleared() { return board.getLinesCleared(); }

    /**
     * Performs a hard drop operation.
     * Moves the brick to the bottom instantly, merges it to the board,
     * clears any complete lines, and spawns a new brick.
     *
     * @return true if the game continues, false if game over
     */

    public boolean hardDrop() {
        while (board.moveBrickDown()) { }
        board.mergeBrickToBackground();
        ClearRow clearRow = board.clearRows();
        if (board.createNewBrick()) {
            viewGuiController.gameOver();
            return false;
        }
        viewGuiController.refreshGameBackground(board.getBoardMatrix());
        return true;
    }
}