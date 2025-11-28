package com.comp2042;
import com.comp2042.logic.bricks.Brick;

import java.util.List;

public interface Board {

    boolean moveBrickDown();

    boolean moveBrickLeft();

    boolean moveBrickRight();

    boolean rotateLeftBrick();

    boolean createNewBrick();

    boolean checkGameOver();

    int[][] getBoardMatrix();

    ViewData getViewData();

    void mergeBrickToBackground();

    ClearRow clearRows();

    Score getScore();

    void newGame();

    boolean holdCurrentBrick();

    int[][] getHoldBrickData();

    List<int[][]> getNextBricksData(int count);

    int getPiecesPlaced();

    int getLinesCleared();
    // Get current brick
    Brick getCurrentBrick();
}
