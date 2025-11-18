package com.comp2042;

import com.comp2042.logic.bricks.Brick;
import com.comp2042.logic.bricks.BrickGenerator;
import com.comp2042.logic.bricks.RandomBrickGenerator;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class SimpleBoard implements Board {

    private final int width;
    private final int height;
    private final BrickGenerator brickGenerator;
    private final BrickRotator brickRotator;
    private int[][] currentGameMatrix;
    private Point currentOffset;
    private final Score score;
    private Brick holdBrick;
    private boolean canHold;
    private int piecesPlaced;
    private int linesCleared;

    public SimpleBoard(int width, int height) {
        this.width = width;     // 10 (columns)
        this.height = height;   // 20 (rows)
        currentGameMatrix = new int[height][width]; // [20][10] = rows, columns
        brickGenerator = new RandomBrickGenerator();
        brickRotator = new BrickRotator();
        score = new Score();
        holdBrick = null;
        canHold = true;
        piecesPlaced = 0;
        linesCleared = 0;
    }

    @Override
    public Brick getCurrentBrick() {
        return brickRotator.getBrick();
    }

    @Override
    public boolean moveBrickDown() {
        int[][] currentMatrix = MatrixOperations.copy(currentGameMatrix);
        Point p = new Point(currentOffset);
        p.translate(0, 1);
        boolean conflict = MatrixOperations.intersect(currentMatrix, brickRotator.getCurrentShape(), (int) p.getX(), (int) p.getY());
        if (conflict) {
            return false;
        } else {
            currentOffset = p;
            return true;
        }
    }


    @Override
    public boolean moveBrickLeft() {
        int[][] currentMatrix = MatrixOperations.copy(currentGameMatrix);
        Point p = new Point(currentOffset);
        p.translate(-1, 0);
        boolean conflict = MatrixOperations.intersect(currentMatrix, brickRotator.getCurrentShape(), (int) p.getX(), (int) p.getY());
        if (conflict) {
            return false;
        } else {
            currentOffset = p;
            return true;
        }
    }

    @Override
    public boolean moveBrickRight() {
        int[][] currentMatrix = MatrixOperations.copy(currentGameMatrix);
        Point p = new Point(currentOffset);
        p.translate(1, 0);
        boolean conflict = MatrixOperations.intersect(currentMatrix, brickRotator.getCurrentShape(), (int) p.getX(), (int) p.getY());
        if (conflict) {
            return false;
        } else {
            currentOffset = p;
            return true;
        }
    }

    @Override
    public boolean rotateLeftBrick() {
        int[][] currentMatrix = MatrixOperations.copy(currentGameMatrix);
        NextShapeInfo nextShape = brickRotator.getNextShape();
        boolean conflict = MatrixOperations.intersect(currentMatrix, nextShape.getShape(), (int) currentOffset.getX(), (int) currentOffset.getY());
        if (conflict) {
            return false;
        } else {
            brickRotator.setCurrentShape(nextShape.getPosition());
            return true;
        }
    }

    @Override
    public boolean createNewBrick() {
        Brick currentBrick = brickGenerator.getBrick();
        brickRotator.setBrick(currentBrick);
        currentOffset = new Point(width / 2 - 2, 0);
        canHold = true;
        piecesPlaced++;
        return MatrixOperations.intersect(currentGameMatrix, brickRotator.getCurrentShape(), (int) currentOffset.getX(), (int) currentOffset.getY());
    }

    @Override
    public int[][] getBoardMatrix() {
        return currentGameMatrix;
    }

    @Override
    public ViewData getViewData() {
        List<int[][]> nextBricks = getNextBricksData(5);
        return new ViewData(
                brickRotator.getCurrentShape(),
                (int) currentOffset.getX(),
                (int) currentOffset.getY(),
                nextBricks.size() > 0 ? nextBricks.get(0) : new int[4][4]
        );
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
    public Score getScore() {
        return score;
    }


    @Override
    public void newGame() {
        currentGameMatrix = new int[height][width];
        score.reset();
        holdBrick = null;
        canHold = true;
        piecesPlaced = 0;
        linesCleared = 0;
        createNewBrick();
    }

    //Hold Function
    @Override
    public boolean holdCurrentBrick() {
        if (!canHold) {
            return false;
        }

        Brick currentBrick = brickRotator.getBrick();
        if (holdBrick == null) {
            // First time holding - store current brick and get new one
            holdBrick = currentBrick;
            createNewBrick();
        } else {
            // Swap current with hold
            Brick temp = holdBrick;
            holdBrick = currentBrick;
            brickRotator.setBrick(temp);
            currentOffset = new Point(height / 2 - 2, 0);
        }

        canHold = false;
        return true;
    }

    @Override
    public int[][] getHoldBrickData() {
        if (holdBrick == null) {
            return new int[4][4];
        }
        return holdBrick.getShapeMatrix().get(0);
    }

    // NEXT BRICK function
    @Override
    public List<int[][]> getNextBricksData(int count) {
        List<int[][]> nextBricks = new ArrayList<>();
        // This is a simplified version - you may need to update RandomBrickGenerator
        // to support peeking at multiple upcoming bricks
        List<Brick> bricks = brickGenerator.getNextBricks(count);
        for (Brick brick : bricks) {
            if (brick != null && brick.getShapeMatrix() != null && brick.getShapeMatrix().size() > 0) {
                nextBricks.add(brick.getShapeMatrix().get(0));
            }
        }
        return nextBricks;
    }

    @Override
    public int getPiecesPlaced() {
        return piecesPlaced;
    }

    @Override
    public int getLinesCleared() {
        return linesCleared;
    }


}
