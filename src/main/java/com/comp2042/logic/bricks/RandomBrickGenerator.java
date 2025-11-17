package com.comp2042.logic.bricks;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class RandomBrickGenerator implements BrickGenerator {

    private final List<Brick> brickList;

    private final Deque<Brick> nextBricks = new ArrayDeque<>();

    private static final int QUEUE_SIZE = 7; // Keep 7 bricks in queue

    public RandomBrickGenerator() {
        brickList = new ArrayList<>();
        brickList.add(new IBrick());
        brickList.add(new JBrick());
        brickList.add(new LBrick());
        brickList.add(new OBrick());
        brickList.add(new SBrick());
        brickList.add(new TBrick());
        brickList.add(new ZBrick());
        // Initialize queue
        for (int i = 0; i < QUEUE_SIZE; i++) {
            nextBricks.add(brickList.get(ThreadLocalRandom.current().nextInt(brickList.size())));
        }
    }

    @Override
    public Brick getBrick() {
        // Maintain queue size
        if (nextBricks.size() < QUEUE_SIZE) {
            nextBricks.add(brickList.get(ThreadLocalRandom.current().nextInt(brickList.size())));
        }
        Brick brick = nextBricks.poll();
        // Immediately add a new brick to maintain queue
        nextBricks.add(brickList.get(ThreadLocalRandom.current().nextInt(brickList.size())));
        return brick;
    }


    // Get multiple next bricks for preview
    public List<Brick> getNextBricks(int count) {
        List<Brick> preview = new ArrayList<>();
        int index = 0;
        for (Brick brick : nextBricks) {
            if (index >= count) break;
            preview.add(brick);
            index++;
        }
        return preview;
    }

}
