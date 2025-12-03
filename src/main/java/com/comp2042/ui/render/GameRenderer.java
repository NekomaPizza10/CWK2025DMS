package com.comp2042.ui.render;

import com.comp2042.model.ViewData;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;

/**
 * Handles all game rendering operations.
 */
public class GameRenderer {

    private static final int BRICK_SIZE = 25;
    private static final int PREVIEW_BRICK_SIZE = 20;
    private static final int PREVIEW_GRID_SIZE = 4;

    private Rectangle[][] displayMatrix;
    private Rectangle[][] brickRectangles;
    private Rectangle[][] holdRectangles;
    private Rectangle[][] nextRectangles1, nextRectangles2, nextRectangles3, nextRectangles4, nextRectangles5;
    private GridPane gamePanel;
    private GridPane brickPanel;

    private int boardWidth;
    private int boardHeight;

    public GameRenderer(GridPane gamePanel, GridPane brickPanel) {
        this.gamePanel = gamePanel;
        this.brickPanel = brickPanel;
    }

    public void initializeGameBoard(int height, int width) {
        this.boardHeight = height;
        this.boardWidth = width;
        displayMatrix = new Rectangle[height][width];
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                Rectangle rectangle = new Rectangle(BRICK_SIZE, BRICK_SIZE);
                rectangle.setFill(Color.TRANSPARENT);
                rectangle.setStroke(Color.rgb(40, 40, 40));
                rectangle.setStrokeWidth(0.5);
                displayMatrix[i][j] = rectangle;
                gamePanel.add(rectangle, j, i);
            }
        }
    }

    public void initializeBrickPanel(int[][] brickData) {
        brickRectangles = new Rectangle[brickData.length][brickData[0].length];
        for (int i = 0; i < brickData.length; i++) {
            for (int j = 0; j < brickData[i].length; j++) {
                Rectangle rectangle = new Rectangle(BRICK_SIZE, BRICK_SIZE);
                rectangle.setFill(getFillColor(brickData[i][j]));
                brickRectangles[i][j] = rectangle;
                brickPanel.add(rectangle, j, i);
            }
        }
        brickPanel.toFront();
    }

    public Rectangle[][] initializePreviewPanel(GridPane panel) {
        Rectangle[][] rects = new Rectangle[PREVIEW_GRID_SIZE][PREVIEW_GRID_SIZE];
        for (int i = 0; i < PREVIEW_GRID_SIZE; i++) {
            for (int j = 0; j < PREVIEW_GRID_SIZE; j++) {
                Rectangle rectangle = new Rectangle(PREVIEW_BRICK_SIZE, PREVIEW_BRICK_SIZE);
                rectangle.setFill(Color.TRANSPARENT);
                rects[i][j] = rectangle;
                panel.add(rectangle, j, i);
            }
        }
        return rects;
    }

    public void refreshBrick(ViewData brick) {
        if (brick == null || brickRectangles == null) {return;}
        // Remove existing brick rectangles from game panel
        for (Rectangle[] row : brickRectangles) {
            for (Rectangle r : row) {
                gamePanel.getChildren().remove(r);
            }
        }
        int[][] brickData = brick.getBrickData();
        if (brickData == null) {
            return;
        }
        int brickX = brick.getxPosition();
        int brickY = brick.getyPosition();

        for (int i = 0; i < brickData.length; i++) {
            for (int j = 0; j < brickData[i].length; j++) {
                if (brickData[i][j] != 0) {
                    int gridX = brickX + j;
                    int gridY = brickY + i;

                    brickRectangles[i][j].setFill(getFillColor(brickData[i][j]));
                    brickRectangles[i][j].setStroke(Color.BLACK);
                    brickRectangles[i][j].setStrokeWidth(0.5);

                    if (isValidGridPosition(gridX, gridY)) {
                        gamePanel.add(brickRectangles[i][j], gridX, gridY);
                    }
                } else {
                    brickRectangles[i][j].setFill(Color.TRANSPARENT);
                }
            }
        }
    }

    private boolean isValidGridPosition(int x, int y) {
        return x >= 0 && y >= 0 && x < boardWidth && y < boardHeight;

    }

    public void refreshGameBackground(int[][] board) {
        if (board == null || displayMatrix == null) {
            return;
        }
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[i].length; j++) {
                displayMatrix[i][j].setFill(getFillColor(board[i][j]));
            }
        }
    }

    public void updatePreviewPanel(Rectangle[][] rects, int[][] brickData) {
        if (rects == null) {return;}

        // Clear the preview panel
        for (int i = 0; i < PREVIEW_GRID_SIZE; i++) {
            for (int j = 0; j < PREVIEW_GRID_SIZE; j++) {
                rects[i][j].setFill(Color.TRANSPARENT);
                rects[i][j].setStroke(null);
            }
        }

        if (brickData == null) {return;}

        // Find bounding box of the brick
        int minRow = PREVIEW_GRID_SIZE, maxRow = -1;
        int minCol = PREVIEW_GRID_SIZE, maxCol = -1;

        for (int i = 0; i < brickData.length; i++) {
            for (int j = 0; j < brickData[i].length; j++) {
                if (brickData[i][j] != 0) {
                    minRow = Math.min(minRow, i);
                    maxRow = Math.max(maxRow, i);
                    minCol = Math.min(minCol, j);
                    maxCol = Math.max(maxCol, j);
                }
            }
        }

        if (maxRow < 0) {
            return;
        }

        // Center the brick in the preview
        int brickHeight = maxRow - minRow + 1;
        int brickWidth = maxCol - minCol + 1;
        int offsetRow = (PREVIEW_GRID_SIZE - brickHeight) / 2;
        int offsetCol = (PREVIEW_GRID_SIZE - brickWidth) / 2;

        for (int i = 0; i < brickData.length; i++) {
            for (int j = 0; j < brickData[i].length; j++) {
                if (brickData[i][j] != 0) {
                    int targetRow = offsetRow + (i - minRow);
                    int targetCol = offsetCol + (j - minCol);

                    if (targetRow >= 0 && targetRow < PREVIEW_GRID_SIZE &&
                            targetCol >= 0 && targetCol < PREVIEW_GRID_SIZE) {
                        rects[targetRow][targetCol].setFill(getFillColor(brickData[i][j]));
                        rects[targetRow][targetCol].setStroke(Color.BLACK);
                        rects[targetRow][targetCol].setStrokeWidth(0.5);
                    }
                }
            }
        }
    }

    public void renderShadow(ViewData brick, int shadowY, int[][] boardMatrix) {
        if (brick == null || boardMatrix == null || displayMatrix == null) {return;}
        int shadowX = brick.getxPosition();
        int[][] brickData = brick.getBrickData();

        if (brickData == null) {return;}

        for (int i = 0; i < brickData.length; i++) {
            for (int j = 0; j < brickData[i].length; j++) {
                if (brickData[i][j] != 0) {
                    int boardRow = shadowY + i;
                    int boardCol = shadowX + j;

                    if (boardRow >= 0 && boardRow < displayMatrix.length &&
                            boardCol >= 0 && boardCol < displayMatrix[0].length) {
                        if (boardMatrix[boardRow][boardCol] == 0) {
                            displayMatrix[boardRow][boardCol].setFill(Color.rgb(128, 128, 128, 0.3));
                        }
                    }
                }
            }
        }
    }

    public void clearBrickDisplay() {
        if (brickRectangles != null) {
            for (Rectangle[] row : brickRectangles) {
                for (Rectangle r : row) {
                    gamePanel.getChildren().remove(r);
                }
            }
        }
    }

    private Paint getFillColor(int value) {
        return switch (value) {
            case 0 -> Color.TRANSPARENT;
            case 1 -> Color.CYAN;
            case 2 -> Color.BLUE;
            case 3 -> Color.ORANGE;
            case 4 -> Color.YELLOW;
            case 5 -> Color.GREEN;
            case 6 -> Color.PURPLE;
            case 7 -> Color.RED;
            default -> Color.WHITE;
        };
    }

    // Getters and Setters
    public void setHoldRectangles(Rectangle[][] holdRectangles) {this.holdRectangles = holdRectangles;}

    public void setNextRectangles(Rectangle[][] n1, Rectangle[][] n2, Rectangle[][] n3,
                                  Rectangle[][] n4, Rectangle[][] n5) {
        this.nextRectangles1 = n1;
        this.nextRectangles2 = n2;
        this.nextRectangles3 = n3;
        this.nextRectangles4 = n4;
        this.nextRectangles5 = n5;
    }

    public Rectangle[][] getHoldRectangles() { return holdRectangles; }
    public Rectangle[][] getNextRectangles1() { return nextRectangles1; }
    public Rectangle[][] getNextRectangles2() { return nextRectangles2; }
    public Rectangle[][] getNextRectangles3() { return nextRectangles3; }
    public Rectangle[][] getNextRectangles4() { return nextRectangles4; }
    public Rectangle[][] getNextRectangles5() { return nextRectangles5; }
    public Rectangle[][] getDisplayMatrix() { return displayMatrix; }
    public GridPane getBrickPanel() { return brickPanel; }
}