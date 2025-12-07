package com.comp2042.brick.pieces;

import com.comp2042.core.MatrixOperations;
import com.comp2042.brick.Brick;

import java.util.ArrayList;
import java.util.List;

/**
 * T-brick (purple) - The T-shaped tetromino.
 * Has 4 rotation states.
 * Uses color code 6.
 *
 */
public class TBrick implements Brick {

    private final List<int[][]> brickMatrix = new ArrayList<>();

    /**
     * Creates a new T-brick with all 4 rotation matrices.
     */
    public TBrick() {
        brickMatrix.add(new int[][]{
                {0, 0, 0, 0},
                {6, 6, 6, 0},
                {0, 6, 0, 0},
                {0, 0, 0, 0}
        });
        brickMatrix.add(new int[][]{
                {0, 6, 0, 0},
                {0, 6, 6, 0},
                {0, 6, 0, 0},
                {0, 0, 0, 0}
        });
        brickMatrix.add(new int[][]{
                {0, 6, 0, 0},
                {6, 6, 6, 0},
                {0, 0, 0, 0},
                {0, 0, 0, 0}
        });
        brickMatrix.add(new int[][]{
                {0, 6, 0, 0},
                {6, 6, 0, 0},
                {0, 6, 0, 0},
                {0, 0, 0, 0}
        });
    }

    /**
     * Returns defensive copies of all 4 rotation states.
     *
     * @return list containing all T-brick orientations
     */
    @Override
    public List<int[][]> getShapeMatrix() {
        return MatrixOperations.deepCopyList(brickMatrix);
    }
}
