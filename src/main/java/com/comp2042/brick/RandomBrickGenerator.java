package com.comp2042.brick;

import com.comp2042.brick.pieces.*;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class RandomBrickGenerator implements BrickGenerator {

    private final Deque<Brick> nextBricks = new ArrayDeque<>();

    private static final int QUEUE_SIZE = 14; // Keep 2 bags worth (2 × 7 = 14)

    public RandomBrickGenerator() {
        // Initialize with 2 full bags
        fillBag();
        fillBag();
    }

    @Override
    public Brick getBrick() {
        // Maintain queue size
        if (nextBricks.size() <= 7) {
            fillBag();
        }

        return nextBricks.poll();
    }

    // Get multiple next bricks for preview
    public List<Brick> getNextBricks(int count) {
        // FIX: Ensure we have enough bricks in the queue for the requested count
        while (nextBricks.size() < count) {
            fillBag();
        }

        List<Brick> preview = new ArrayList<>();
        int index = 0;
        for (Brick brick : nextBricks) {
            if (index >= count) break;
            preview.add(brick);
            index++;
        }
        return preview;
    }

    /**
     * 7-Bag System: Creates a "bag" with all 7 Tetris pieces,
     * shuffles them, and adds to queue.
     * This ensures fair distribution and prevents long runs of same piece.
     */
    private void fillBag() {
        List<Brick> bag = new ArrayList<>();

        // Add one of each piece type
        bag.add(new IBrick());
        bag.add(new JBrick());
        bag.add(new LBrick());
        bag.add(new OBrick());
        bag.add(new SBrick());
        bag.add(new TBrick());
        bag.add(new ZBrick());

        // Shuffle the bag using Fisher-Yates algorithm
        for (int i = bag.size() - 1; i > 0; i--) {
            int j = ThreadLocalRandom.current().nextInt(i + 1);
            // Swap
            Brick temp = bag.get(i);
            bag.set(i, bag.get(j));
            bag.set(j, temp);
        }

        // Add shuffled bag to queue
        nextBricks.addAll(bag);
    }
}