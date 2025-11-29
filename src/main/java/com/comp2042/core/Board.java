package com.comp2042.core;
import com.comp2042.brick.Brick;
import com.comp2042.brick.BrickGenerator;
import com.comp2042.brick.RandomBrickGenerator;
import com.comp2042.brick.BrickRotator;
import com.comp2042.model.*;
import com.comp2042.state.Score;

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

    Brick getCurrentBrick();
}
