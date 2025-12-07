package com.comp2042.core;
import com.comp2042.brick.Brick;
import com.comp2042.brick.BrickGenerator;
import com.comp2042.brick.RandomBrickGenerator;
import com.comp2042.brick.BrickRotator;
import com.comp2042.model.*;
import com.comp2042.state.Score;

import java.util.List;

public interface Board {

    /**
     * Moves the current brick down by one cell.
     * @return true if movement succeeded, false if blocked
     */
    boolean moveBrickDown();

    /**
     * Moves the current brick left by one cell.
     * @return true if movement succeeded, false if blocked
     */
    boolean moveBrickLeft();

    /**
     * Moves the current brick right by one cell.
     * @return true if movement succeeded, false if blocked
     */
    boolean moveBrickRight();

    /**
     * Rotates the current brick counter-clockwise.
     * Applies wall kick logic if rotation is blocked.
     *
     * @return true if rotation succeeded, false if blocked
     */
    boolean rotateLeftBrick();

    /**
     * Creates and spawns a new brick at the top of the board.
     * @return true if spawn caused game over, false if successful
     */
    boolean createNewBrick();

    /**
     * Checks if the game is over (blocks reached top).
     * @return true if game over condition met
     */
    boolean checkGameOver();

    /**
     * Gets a copy of the current board matrix.
     * @return 2D array representing board state
     */
    int[][] getBoardMatrix();

    /**
     * Gets current view data for rendering.
     * @return ViewData containing brick and position information
     */
    ViewData getViewData();

    void mergeBrickToBackground();

    /**
     * Checks for and removes complete rows.
     * @return ClearRow containing lines removed and score data
     */
    ClearRow clearRows();

    /**
     * Gets the score tracking object.
     * @return Score instance with current points
     */
    Score getScore();

    void newGame();

    /**
     * Attempts to hold the current brick.
     * Swaps current brick with held brick, or stores if empty.
     * @return true if hold succeeded, false if not allowed
     */
    boolean holdCurrentBrick();

    /**
     * Gets the shape matrix of the held brick.
     * @return 2D array of held brick, or empty array if none
     */
    int[][] getHoldBrickData();

    /**
     * Gets preview data for upcoming bricks.
     *
     * @param count number of bricks to preview
     * @return list of shape matrices for next bricks
     */
    List<int[][]> getNextBricksData(int count);

    /**
     * Gets the number of pieces placed on the board.
     *
     * @return total pieces placed count
     */
    int getPiecesPlaced();

    /**
     * Gets the total number of lines cleared.
     *
     * @return lines cleared count
     */
    int getLinesCleared();

    /**
     * Gets the current active brick.
     *
     * @return the current Brick instance
     */
    Brick getCurrentBrick();
}
