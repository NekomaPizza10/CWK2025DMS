package com.comp2042.model;

import com.comp2042.core.MatrixOperations;

public final class NextShapeInfo {

    private final int[][] shape;
    private final int position;

    /**
     * Creates a new NextShapeInfo.
     *
     * @param shape the shape matrix for next rotation
     * @param position the rotation index
     */
    public NextShapeInfo(final int[][] shape, final int position) {
        this.shape = shape;
        this.position = position;
    }

    /**
     * Gets a defensive copy of the shape matrix.
     *
     * @return 2D array of next rotation state
     */
    public int[][] getShape() {
        return MatrixOperations.copy(shape);
    }

    /**
     * Gets the rotation position index.
     *
     * @return rotation index (0 to N-1)
     */
    public int getPosition() {
        return position;
    }
}
