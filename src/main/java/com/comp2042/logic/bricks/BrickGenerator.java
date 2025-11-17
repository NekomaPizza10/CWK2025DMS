package com.comp2042.logic.bricks;

import java.util.List;

public interface BrickGenerator {

    Brick getBrick();
    List<Brick> getNextBricks(int count);
}
