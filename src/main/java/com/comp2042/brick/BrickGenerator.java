package com.comp2042.brick;

import java.util.List;

/**
 * BrickGenerator interface for creating new bricks.
 * Implementations define the brick generation strategy (random, sequential, etc).
 *
 */
public interface BrickGenerator {

    /**
     * Generates and returns the next brick.
     *
     * @return the next Brick to be used in the game
     */
    Brick getBrick();

    /**
     * Previews the next N bricks without consuming them.
     *
     * @param count number of bricks to preview
     * @return list of upcoming bricks
     */
    List<Brick> getNextBricks(int count);
}
