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
        this.height = height;   // 27 (rows)
        currentGameMatrix = new int[height][width]; // [27][10] = rows, columns
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
        int[][] nextShapeArray = nextShape.getShape();

        // FIX: Check if initial rotation would be within bounds
        if (isRotationInBounds(nextShapeArray, (int) currentOffset.getX(), (int) currentOffset.getY())) {
            boolean conflict = MatrixOperations.intersect(currentMatrix, nextShapeArray, (int) currentOffset.getX(), (int) currentOffset.getY());

            if (!conflict) {
                // Rotation succeeded at current position
                brickRotator.setCurrentShape(nextShape.getPosition());
                return true;
            }
        }

        // Wall kicks: one space left or right
        // Kick right (for left wall)
        for (int kickDistance = 1; kickDistance <= 3; kickDistance++) {
            Point testOffset = new Point(currentOffset);
            testOffset.translate(-kickDistance, 0);  // Move LEFT

            // FIX: Check if the new position would be within bounds before testing collision
            if (isRotationInBounds(nextShapeArray, (int) testOffset.getX(), (int) testOffset.getY())) {
                boolean conflict = MatrixOperations.intersect(currentMatrix, nextShapeArray,
                        (int) testOffset.getX(),
                        (int) testOffset.getY());

                if (!conflict) {
                    currentOffset = testOffset;
                    brickRotator.setCurrentShape(nextShape.getPosition());
                    return true;
                }
            }
        }

        // Kick left (for right wall)
        for (int kickDistance = 1; kickDistance <= 3; kickDistance++) {
            Point testOffset = new Point(currentOffset);
            testOffset.translate(kickDistance, 0);  // Move RIGHT

            // FIX: Check if the new position would be within bounds before testing collision
            if (isRotationInBounds(nextShapeArray, (int) testOffset.getX(), (int) testOffset.getY())) {
                boolean conflict = MatrixOperations.intersect(currentMatrix, nextShapeArray,
                        (int) testOffset.getX(),
                        (int) testOffset.getY());

                if (!conflict) {
                    currentOffset = testOffset;
                    brickRotator.setCurrentShape(nextShape.getPosition());
                    return true;
                }
            }
        }

        // Rotation failed
        return false;
    }

    /**
     * Helper method to check if a rotated shape would be within game board boundaries
     */
    private boolean isRotationInBounds(int[][] shape, int x, int y) {
        for (int i = 0; i < shape.length; i++) {
            for (int j = 0; j < shape[i].length; j++) {
                if (shape[i][j] != 0) {
                    int boardX = x + j;
                    int boardY = y + i;

                    // Check if this block would be out of bounds
                    if (boardX < 0 || boardX >= width || boardY >= height) {
                        return false;
                    }
                    // Note: boardY can be negative (above the board) during rotation, which is allowed
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
        piecesPlaced++;

        // Determine spawn height - spawn one cell higher if stack is near top
        // This gives player one final chance to place a piece
        int spawnY = isStackNearTop() ? -2 : -1;
        currentOffset = new Point(width / 2 - 2, spawnY);

        // Game over ONLY if piece cannot move down at all (can't enter visible area)
        Point testPoint = new Point(currentOffset);
        testPoint.translate(0, 1);
        boolean cannotMoveDown = MatrixOperations.intersect(
                currentGameMatrix,
                brickRotator.getCurrentShape(),
                (int) testPoint.getX(),
                (int) testPoint.getY()
        );

        return cannotMoveDown; // True = game over, False = continue
    }

    private boolean isStackNearTop() {
        for (int row = 0; row <= 1; row++) {
            for (int col = 0; col < width; col++) {
                if (currentGameMatrix[row][col] != 0) {
                    return true;
                }
            }
        }
        return false;
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
            currentOffset = new Point(width/ 2 - 2, -1);
            // new Point(3, 0)  "Column 3, top row"

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

    @Override
    public boolean checkGameOver() {
        // Game over when any block reaches row 0
        for (int col = 0; col < width; col++) {
            if (currentGameMatrix[0][col] != 0) {
                return true;
            }
        }
        return false;
    }

}