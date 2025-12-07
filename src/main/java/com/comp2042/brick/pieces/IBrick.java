package com.comp2042.brick.pieces;

import com.comp2042.core.MatrixOperations;
import com.comp2042.brick.Brick;

import java.util.ArrayList;
import java.util.List;

/**
 * I-brick (cyan) - The straight line tetromino.
 * Has 2 rotation states: horizontal and vertical.
 * Uses color code 1.
 *
 */
public class IBrick implements Brick {

    private final List<int[][]> brickMatrix = new ArrayList<>();

    /**
     * Creates a new I-brick with its rotation matrices.
     */
    public IBrick() {
        brickMatrix.add(new int[][]{
                {0, 0, 0, 0},
                {1, 1, 1, 1},
                {0, 0, 0, 0},
                {0, 0, 0, 0}
        });
        brickMatrix.add(new int[][]{
                {0, 1, 0, 0},
                {0, 1, 0, 0},
                {0, 1, 0, 0},
                {0, 1, 0, 0}
        });
    }

    /**
     * Returns defensive copies of all rotation states.
     *
     * @return list containing horizontal and vertical orientations
     */
    @Override
    public List<int[][]> getShapeMatrix() {
        return MatrixOperations.deepCopyList(brickMatrix);
    }

}
