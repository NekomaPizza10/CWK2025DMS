package com.comp2042;

import com.comp2042.logic.bricks.Brick;

public class BrickRotator {

    private Brick brick;
    private int currentShape;

    public void setBrick(Brick brick) {
        this.brick = brick;
        this.currentShape = 0;
    }

    public NextShapeInfo getNextShape() {
        if (brick == null) {
            return new NextShapeInfo(new int[0][0], 0);
        }
        int nextPosition = (currentShape + 1) % brick.getShapeMatrix().size();
        return new NextShapeInfo(brick.getShapeMatrix().get(nextPosition), nextPosition);
    }

    public int[][] getCurrentShape() {
        if (brick == null) {
            return new int[0][0];
        }
        return brick.getShapeMatrix().get(currentShape);
    }

    public void setCurrentShape(int currentShape) {
        this.currentShape = currentShape;
    }

    public Brick getBrick() {
        return brick;
    }


}
