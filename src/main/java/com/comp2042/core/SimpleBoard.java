package com.comp2042.core;

import com.comp2042.brick.Brick;
import com.comp2042.brick.BrickRotator;
import com.comp2042.core.board.*;
import com.comp2042.model.*;
import com.comp2042.state.Score;

import java.awt.Point;
import java.util.List;

/**
 * Main board implementation that coordinates all board-related operations.
 * Delegates specific functionality to specialized managers.
 */
public class SimpleBoard implements Board {

    private final int width;
    private final int height;

    private final BoardStateManager stateManager;
    private final BrickSpawner brickSpawner;
    private final BrickMover brickMover;
    private final BrickRotationHandler rotationHandler;
    private final HoldManager holdManager;

    private final BrickRotator brickRotator;
    private final Score score;

    public SimpleBoard(int width, int height) {
        this.width = width;
        this.height = height;

        this.brickRotator = new BrickRotator();
        this.score = new Score();

        this.stateManager = new BoardStateManager(width, height);
        this.brickMover = new BrickMover(width, brickRotator, stateManager);

        this.brickSpawner = new BrickSpawner(width, brickRotator, stateManager, brickMover);

        this.rotationHandler = new BrickRotationHandler(width, height, brickRotator, stateManager, brickMover);
        this.holdManager = new HoldManager(width, brickRotator, brickSpawner, brickMover);
    }

    @Override
    public Brick getCurrentBrick() {
        return brickRotator.getBrick();
    }

    @Override
    public int[][] getBoardMatrix() {
        return stateManager.getBoardMatrix();
    }

    @Override
    public Score getScore() {
        return score;
    }

    @Override
    public int getPiecesPlaced() {
        return stateManager.getPiecesPlaced();
    }

    @Override
    public int getLinesCleared() {
        return stateManager.getLinesCleared();
    }

    @Override
    public boolean moveBrickDown() {
        return brickMover.moveBrickDown();
    }

    @Override
    public boolean moveBrickLeft() {
        return brickMover.moveBrickLeft();
    }

    @Override
    public boolean moveBrickRight() {
        return brickMover.moveBrickRight();
    }

    @Override
    public boolean rotateLeftBrick() {
        return rotationHandler.rotateLeftBrick();
    }

    @Override
    public boolean createNewBrick() {
        holdManager.setCanHold(true);
        return brickSpawner.createNewBrick();
    }

    @Override
    public ViewData getViewData() {
        Point offset = brickMover.getCurrentOffset();
        List<int[][]> nextBricks = brickSpawner.getNextBricksData(5);
        int[][] nextBrick = nextBricks.isEmpty() ? new int[4][4] : nextBricks.get(0);

        return new ViewData(
                brickRotator.getCurrentShape(),
                (int) offset.getX(),
                (int) offset.getY(),
                nextBrick
        );
    }

    @Override
    public void mergeBrickToBackground() {
        Point offset = brickMover.getCurrentOffset();
        stateManager.mergeBrickToBackground(brickRotator.getCurrentShape(), offset);
    }

    @Override
    public ClearRow clearRows() {
        return stateManager.clearRows();
    }

    @Override
    public void newGame() {
        stateManager.reset();
        brickSpawner.reset();
        holdManager.reset();
        score.reset();
        brickMover.resetOffset(width);
        createNewBrick();
    }

    @Override
    public boolean checkGameOver() {
        return stateManager.checkGameOver();
    }

    @Override
    public boolean holdCurrentBrick() {
        return holdManager.holdCurrentBrick();
    }

    @Override
    public int[][] getHoldBrickData() {
        return holdManager.getHoldBrickData();
    }

    @Override
    public List<int[][]> getNextBricksData(int count) {
        return brickSpawner.getNextBricksData(count);
    }
}