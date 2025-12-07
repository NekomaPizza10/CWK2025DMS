package com.comp2042.ui.render;

import com.comp2042.model.ViewData;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;

/**
 * Handles all game rendering operations.
 *  * @see ViewData
 *  * @see GameLogicHandler
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

    /**
     * Constructs a GameRenderer with the specified panels.
     *
     * @param gamePanel the main game board panel
     * @param brickPanel the current brick display panel
     */
    public GameRenderer(GridPane gamePanel, GridPane brickPanel) {
        this.gamePanel = gamePanel;
        this.brickPanel = brickPanel;
    }

    /**
     * Initializes the game board grid with rectangles.
     *
     * Creates a height × width grid of Rectangle objects, each representing
     * one cell of the game board. Rectangles are initially transparent with
     * subtle grid lines.
     *
     *
     * @param height the board height in cells
     * @param width the board width in cells
     */
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

    /**
     * Initializes the brick panel for displaying the current piece.
     * Creates a 4×4 grid of rectangles for showing the active piece.
     * This panel is layered on top of the game board.
     *
     * @param brickData the initial brick shape data
     */
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

    /**
     * Initializes a preview panel for next/hold pieces.
     * Creates a 4×4 grid of smaller rectangles (20px) for preview displays.
     * Returns the rectangle array for later updates.
     *
     * @param panel the GridPane to initialize
     * @return the 4×4 rectangle array
     */
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

    /**
     * Refreshes the current brick display.
     * Removes existing brick rectangles from the game panel, then adds
     * them back at the current brick position. Only adds rectangles for
     * filled cells. Handles pieces partially above the visible board.
     *
     * @param brick the current brick view data with position and shape
     */
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

    /**
     * Refreshes the game board background.
     * Updates all board cells to reflect the current board state.
     * Called after piece placement or line clears.
     *
     * @param board the current board matrix
     */
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

    /**
     * Updates a preview panel with centered brick display.
     * Clears the preview panel, calculates the brick's bounding box,
     * centers it in the 4×4 grid, and renders with proper colors.
     * Handles all brick shapes and rotations.
     *
     * @param rects the preview panel rectangle array
     * @param brickData the brick shape to display (4×4 matrix)
     */
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

    /**
     * Renders the shadow (ghost) piece at the drop position.
     * Overlays semi-transparent gray cells on the board to show where
     * the current piece would land. Only renders on empty cells to avoid
     * covering placed pieces. Shadow is cleared on next board refresh.
     *
     * @param brick the current brick view data
     * @param shadowY the Y position where brick would land
     * @param boardMatrix the current board matrix
     */
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

    /**
     * Sets the hold panel rectangle array.
     *
     * @param holdRectangles the hold panel rectangles
     */
    public void setHoldRectangles(Rectangle[][] holdRectangles) {this.holdRectangles = holdRectangles;}

    /**
     * Sets the next piece panel rectangle arrays.
     *
     * @param n1 next piece panel 1 rectangles
     * @param n2 next piece panel 2 rectangles
     * @param n3 next piece panel 3 rectangles
     * @param n4 next piece panel 4 rectangles
     * @param n5 next piece panel 5 rectangles
     */
    public void setNextRectangles(Rectangle[][] n1, Rectangle[][] n2, Rectangle[][] n3,
                                  Rectangle[][] n4, Rectangle[][] n5) {
        this.nextRectangles1 = n1;
        this.nextRectangles2 = n2;
        this.nextRectangles3 = n3;
        this.nextRectangles4 = n4;
        this.nextRectangles5 = n5;
    }

    /**
     * Gets the hold panel rectangles.
     * @return the hold panel rectangle array
     */
    public Rectangle[][] getHoldRectangles() { return holdRectangles; }
    /**
     * Gets the next piece panel 1 rectangles.
     * @return next panel 1 rectangle array
     */
    public Rectangle[][] getNextRectangles1() { return nextRectangles1; }
    /**
     * Gets the next piece panel 2 rectangles.
     * @return next panel 2 rectangle array
     */
    public Rectangle[][] getNextRectangles2() { return nextRectangles2; }
    /**
     * Gets the next piece panel 3 rectangles.
     * @return next panel 3 rectangle array
     */
    public Rectangle[][] getNextRectangles3() { return nextRectangles3; }
    /**
     * Gets the next piece panel 4 rectangles.
     * @return next panel 4 rectangle array
     */
    public Rectangle[][] getNextRectangles4() { return nextRectangles4; }
    /**
     * Gets the next piece panel 5 rectangles.
     * @return next panel 5 rectangle array
     */
    public Rectangle[][] getNextRectangles5() { return nextRectangles5; }
    /**
     * Gets the display matrix (board grid rectangles).
     * @return the board rectangle matrix
     */
    public Rectangle[][] getDisplayMatrix() { return displayMatrix; }
    /**
     * Gets the brick panel.
     * @return the brick panel GridPane
     */
    public GridPane getBrickPanel() { return brickPanel; }
}