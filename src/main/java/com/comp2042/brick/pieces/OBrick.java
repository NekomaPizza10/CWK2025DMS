package com.comp2042.brick.pieces;

import com.comp2042.core.MatrixOperations;
import com.comp2042.brick.Brick;

import java.util.ArrayList;
import java.util.List;

/**
 * O-brick (yellow) - The square tetromino.
 * Has 1 rotation state (looks the same when rotated).
 * Uses color code 4.
 *
 */
public class OBrick implements Brick {

    private final List<int[][]> brickMatrix = new ArrayList<>();

    /**
     * Creates a new O-brick with its single rotation matrix.
     */
    public OBrick() {
        brickMatrix.add(new int[][]{
                {0, 0, 0, 0},
                {0, 4, 4, 0},
                {0, 4, 4, 0},
                {0, 0, 0, 0}
        });
    }

    /**
     * Returns defensive copy of the rotation state.
     *
     * @return list containing single 2x2 square orientation
     */
    @Override
    public List<int[][]> getShapeMatrix() {
        return MatrixOperations.deepCopyList(brickMatrix);
    }

}
