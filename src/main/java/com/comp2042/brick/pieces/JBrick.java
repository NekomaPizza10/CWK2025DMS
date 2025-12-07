package com.comp2042.brick.pieces;

import com.comp2042.core.MatrixOperations;
import com.comp2042.brick.Brick;

import java.util.ArrayList;
import java.util.List;

/**
 * J-brick (blue) - The J-shaped tetromino.
 * Has 4 rotation states.
 * Uses color code 2.
 *
 */
public class JBrick implements Brick {

    private final List<int[][]> brickMatrix = new ArrayList<>();

    /**
     * Creates a new J-brick with all 4 rotation matrices.
     */
    public JBrick() {
        brickMatrix.add(new int[][]{
                {0, 0, 0, 0},
                {2, 2, 2, 0},
                {0, 0, 2, 0},
                {0, 0, 0, 0}
        });
        brickMatrix.add(new int[][]{
                {0, 0, 0, 0},
                {0, 2, 2, 0},
                {0, 2, 0, 0},
                {0, 2, 0, 0}
        });
        brickMatrix.add(new int[][]{
                {0, 0, 0, 0},
                {0, 2, 0, 0},
                {0, 2, 2, 2},
                {0, 0, 0, 0}
        });
        brickMatrix.add(new int[][]{
                {0, 0, 2, 0},
                {0, 0, 2, 0},
                {0, 2, 2, 0},
                {0, 0, 0, 0}
        });
    }

    /**
     * Returns defensive copies of all 4 rotation states.
     *
     * @return list containing all J-brick orientations
     */
    @Override
    public List<int[][]> getShapeMatrix() {
        return MatrixOperations.deepCopyList(brickMatrix);
    }
}
