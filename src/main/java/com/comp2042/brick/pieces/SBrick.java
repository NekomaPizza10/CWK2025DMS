package com.comp2042.brick.pieces;

import com.comp2042.core.MatrixOperations;
import com.comp2042.brick.Brick;

import java.util.ArrayList;
import java.util.List;

/**
 * S-brick (green) - The S-shaped tetromino.
 * Has 2 rotation states.
 * Uses color code 5.
 */

public class SBrick implements Brick {

    private final List<int[][]> brickMatrix = new ArrayList<>();

    /**
     * Creates a new S-brick with its 2 rotation matrices.
     */
    public SBrick() {
        brickMatrix.add(new int[][]{
                {0, 0, 0, 0},
                {0, 5, 5, 0},
                {5, 5, 0, 0},
                {0, 0, 0, 0}
        });
        brickMatrix.add(new int[][]{
                {5, 0, 0, 0},
                {5, 5, 0, 0},
                {0, 5, 0, 0},
                {0, 0, 0, 0}
        });
    }

    /**
     * Returns defensive copies of both rotation states.
     *
     * @return list containing horizontal and vertical orientations
     */
    @Override
    public List<int[][]> getShapeMatrix() {
        return MatrixOperations.deepCopyList(brickMatrix);
    }
}
