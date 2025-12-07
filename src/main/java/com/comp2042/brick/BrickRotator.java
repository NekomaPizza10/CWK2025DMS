package com.comp2042.brick;

import com.comp2042.model.NextShapeInfo;

/**
 * BrickRotator manages the current brick and its rotation state.
 * Handles cycling through rotation positions and provides access
 * to current and next shapes.
 *
 */
public class BrickRotator {

    private Brick brick;
    private int currentShape;

    /**
     * Sets the active brick and resets rotation to position 0.
     *
     * @param brick the brick to set as active
     */
    public void setBrick(Brick brick) {
        this.brick = brick;
        this.currentShape = 0;
    }

    /**
     * Gets information about the next rotation state.
     * Does not actually rotate the brick.
     *
     * @return NextShapeInfo containing the next shape and position
     */
    public NextShapeInfo getNextShape() {
        if (brick == null) {
            return new NextShapeInfo(new int[0][0], 0);
        }
        int nextPosition = (currentShape + 1) % brick.getShapeMatrix().size();
        return new NextShapeInfo(brick.getShapeMatrix().get(nextPosition), nextPosition);
    }

    /**
     * Gets the current rotation state matrix.
     *
     * @return 2D array representing the current shape
     */
    public int[][] getCurrentShape() {
        if (brick == null) {
            return new int[0][0];
        }
        return brick.getShapeMatrix().get(currentShape);
    }

    /**
     * Sets the current rotation position.
     *
     * @param currentShape the rotation index to set
     */
    public void setCurrentShape(int currentShape) {
        this.currentShape = currentShape;
    }

    /**
     * Gets the current brick instance.
     *
     * @return the active brick, or null if none set
     */
    public Brick getBrick() {
        return brick;
    }


}
