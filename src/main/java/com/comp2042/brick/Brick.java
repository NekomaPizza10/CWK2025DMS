package com.comp2042.brick;

import java.util.List;

/**
 * Brick interface representing a Tetris piece (tetromino).
 * Each brick implementation provides shape matrices for all rotation states.
 *
 * <p>Standard Tetris has 7 brick types:
 * <ul>
 *   <li>I-brick (cyan) - straight line</li>
 *   <li>O-brick (yellow) - square</li>
 *   <li>T-brick (purple) - T-shape</li>
 *   <li>S-brick (green) - S-shape</li>
 *   <li>Z-brick (red) - Z-shape</li>
 *   <li>J-brick (blue) - J-shape</li>
 *   <li>L-brick (orange) - L-shape</li>
 * </ul>
 */

public interface Brick {

    /**
     * Returns all rotation states of this brick as a list of matrices.
     * Each matrix is 4x4 with non-zero values representing filled cells.
     *
     * @return list of shape matrices, one for each rotation state
     */
    List<int[][]> getShapeMatrix();
}
