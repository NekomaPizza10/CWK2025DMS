package com.comp2042.model;

import com.comp2042.core.MatrixOperations;

public final class ViewData {

    private final int[][] brickData;
    private final int xPosition;
    private final int yPosition;
    private final int[][] nextBrickData;

    /**
     * Creates a new ViewData instance.
     *
     * @param brickData current brick shape matrix
     * @param xPosition horizontal position
     * @param yPosition vertical position
     * @param nextBrickData preview of next brick
     */
    public ViewData(int[][] brickData, int xPosition, int yPosition, int[][] nextBrickData) {
        this.brickData = brickData;
        this.xPosition = xPosition;
        this.yPosition = yPosition;
        this.nextBrickData = nextBrickData;
    }

    /**
     * Gets a defensive copy of the current brick data.
     * @return current brick shape matrix
     */
    // MatrixOperations.copy handles null
    public int[][] getBrickData() {
        return MatrixOperations.copy(brickData);
    }

    /**
     * Gets the horizontal position.
     * @return X coordinate
     */
    public int getxPosition() {
        return xPosition;
    }

    /**
     * Gets the vertical position.
     * @return Y coordinate
     */
    public int getyPosition() {
        return yPosition;
    }

    /**
     * Gets a defensive copy of the next brick data.
     * @return next brick preview matrix
     */
    public int[][] getNextBrickData() {
        return MatrixOperations.copy(nextBrickData);
    }
}