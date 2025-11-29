package com.comp2042.core;

import com.comp2042.brick.Brick;
import com.comp2042.brick.BrickGenerator;
import com.comp2042.brick.RandomBrickGenerator;
import com.comp2042.brick.BrickRotator;
import com.comp2042.model.*;
import com.comp2042.state.Score;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class SimpleBoard implements Board {

    private final int width;
    private final int height;
    private BrickGenerator brickGenerator;
    private final BrickRotator brickRotator;
    private int[][] currentGameMatrix;
    private Point currentOffset;
    private final Score score;
    private Brick holdBrick;
    private boolean canHold;
    private int piecesPlaced;
    private int linesCleared;

    public SimpleBoard(int width, int height) {
        this.width = width;
        this.height = height;
        currentGameMatrix = new int[height][width];
        brickGenerator = new RandomBrickGenerator();
        brickRotator = new BrickRotator();
        score = new Score();
        holdBrick = null;
        canHold = true;
        piecesPlaced = 0;
        linesCleared = 0;
    }

    @Override
    public Brick getCurrentBrick() { return brickRotator.getBrick(); }

    @Override
    public boolean moveBrickDown() {
        int[][] currentMatrix = MatrixOperations.copy(currentGameMatrix);
        Point p = new Point(currentOffset);
        p.translate(0, 1);
        boolean conflict = MatrixOperations.intersect(currentMatrix, brickRotator.getCurrentShape(), (int) p.getX(), (int) p.getY());
        if (conflict) { return false; }
        else { currentOffset = p; return true; }
    }

    @Override
    public boolean moveBrickLeft() {
        int[][] currentMatrix = MatrixOperations.copy(currentGameMatrix);
        Point p = new Point(currentOffset);
        p.translate(-1, 0);
        boolean conflict = MatrixOperations.intersect(currentMatrix, brickRotator.getCurrentShape(), (int) p.getX(), (int) p.getY());
        if (conflict) { return false; }
        else { currentOffset = p; return true; }
    }

    @Override
    public boolean moveBrickRight() {
        int[][] currentMatrix = MatrixOperations.copy(currentGameMatrix);
        Point p = new Point(currentOffset);
        p.translate(1, 0);
        boolean conflict = MatrixOperations.intersect(currentMatrix, brickRotator.getCurrentShape(), (int) p.getX(), (int) p.getY());
        if (conflict) { return false; }
        else { currentOffset = p; return true; }
    }

    @Override
    public boolean rotateLeftBrick() {
        int[][] currentMatrix = MatrixOperations.copy(currentGameMatrix);
        NextShapeInfo nextShape = brickRotator.getNextShape();
        int[][] nextShapeArray = nextShape.getShape();

        if (isRotationInBounds(nextShapeArray, (int) currentOffset.getX(), (int) currentOffset.getY())) {
            boolean conflict = MatrixOperations.intersect(currentMatrix, nextShapeArray, (int) currentOffset.getX(), (int) currentOffset.getY());
            if (!conflict) { brickRotator.setCurrentShape(nextShape.getPosition()); return true; }
        }

        for (int kickDistance = 1; kickDistance <= 3; kickDistance++) {
            Point testOffset = new Point(currentOffset);
            testOffset.translate(-kickDistance, 0);
            if (isRotationInBounds(nextShapeArray, (int) testOffset.getX(), (int) testOffset.getY())) {
                boolean conflict = MatrixOperations.intersect(currentMatrix, nextShapeArray, (int) testOffset.getX(), (int) testOffset.getY());
                if (!conflict) { currentOffset = testOffset; brickRotator.setCurrentShape(nextShape.getPosition()); return true; }
            }
        }

        for (int kickDistance = 1; kickDistance <= 3; kickDistance++) {
            Point testOffset = new Point(currentOffset);
            testOffset.translate(kickDistance, 0);
            if (isRotationInBounds(nextShapeArray, (int) testOffset.getX(), (int) testOffset.getY())) {
                boolean conflict = MatrixOperations.intersect(currentMatrix, nextShapeArray, (int) testOffset.getX(), (int) testOffset.getY());
                if (!conflict) { currentOffset = testOffset; brickRotator.setCurrentShape(nextShape.getPosition()); return true; }
            }
        }
        return false;
    }

    private boolean isRotationInBounds(int[][] shape, int x, int y) {
        for (int i = 0; i < shape.length; i++) {
            for (int j = 0; j < shape[i].length; j++) {
                if (shape[i][j] != 0) {
                    int boardX = x + j;
                    int boardY = y + i;
                    if (boardX < 0 || boardX >= width || boardY >= height) { return false; }
                }
            }
        }
        return true;
    }

    @Override
    public boolean createNewBrick() {
        Brick currentBrick = brickGenerator.getBrick();
        brickRotator.setBrick(currentBrick);
        canHold = true;

        int spawnY = isStackNearTop() ? -2 : -1;
        currentOffset = new Point(width / 2 - 2, spawnY);

        Point testPoint = new Point(currentOffset);
        testPoint.translate(0, 1);
        boolean cannotMoveDown = MatrixOperations.intersect(currentGameMatrix, brickRotator.getCurrentShape(), (int) testPoint.getX(), (int) testPoint.getY());
        return cannotMoveDown;
    }

    private boolean isStackNearTop() {
        for (int row = 0; row <= 1; row++) {
            for (int col = 0; col < width; col++) {
                if (currentGameMatrix[row][col] != 0) { return true; }
            }
        }
        return false;
    }

    @Override
    public int[][] getBoardMatrix() { return currentGameMatrix; }

    @Override
    public ViewData getViewData() {
        List<int[][]> nextBricks = getNextBricksData(5);
        return new ViewData(brickRotator.getCurrentShape(), (int) currentOffset.getX(), (int) currentOffset.getY(),
                nextBricks.size() > 0 ? nextBricks.get(0) : new int[4][4]);
    }

    @Override
    public void mergeBrickToBackground() {
        currentGameMatrix = MatrixOperations.merge(currentGameMatrix, brickRotator.getCurrentShape(), (int) currentOffset.getX(), (int) currentOffset.getY());
    }

    @Override
    public ClearRow clearRows() {
        ClearRow clearRow = MatrixOperations.checkRemoving(currentGameMatrix);
        currentGameMatrix = clearRow.getNewMatrix();
        linesCleared += clearRow.getLinesRemoved();
        return clearRow;
    }

    @Override
    public Score getScore() { return score; }

    @Override
    public void newGame() {
        currentGameMatrix = new int[height][width];
        brickGenerator = new RandomBrickGenerator();
        score.reset();
        holdBrick = null;
        canHold = true;
        piecesPlaced = 0;
        linesCleared = 0;
        currentOffset = new Point(width / 2 - 2, -1);
        createNewBrick();
    }

    @Override
    public boolean holdCurrentBrick() {
        if (!canHold) { return false; }
        Brick currentBrick = brickRotator.getBrick();
        if (holdBrick == null) {
            holdBrick = currentBrick;
            createNewBrick();
        } else {
            Brick temp = holdBrick;
            holdBrick = currentBrick;
            brickRotator.setBrick(temp);
            currentOffset = new Point(width / 2 - 2, -1);
        }
        canHold = false;
        return true;
    }

    @Override
    public int[][] getHoldBrickData() {
        if (holdBrick == null) { return new int[4][4]; }
        return holdBrick.getShapeMatrix().get(0);
    }

    @Override
    public List<int[][]> getNextBricksData(int count) {
        List<int[][]> nextBricks = new ArrayList<>();
        List<Brick> bricks = brickGenerator.getNextBricks(count);
        for (Brick brick : bricks) {
            if (brick != null && brick.getShapeMatrix() != null && brick.getShapeMatrix().size() > 0) {
                nextBricks.add(brick.getShapeMatrix().get(0));
            }
        }
        return nextBricks;
    }

    @Override
    public int getPiecesPlaced() { return piecesPlaced; }

    @Override
    public int getLinesCleared() { return linesCleared; }

    @Override
    public boolean checkGameOver() {
        for (int col = 0; col < width; col++) {
            if (currentGameMatrix[0][col] != 0) { return true; }
        }
        return false;
    }
}