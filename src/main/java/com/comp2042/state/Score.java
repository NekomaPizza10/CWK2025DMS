package com.comp2042.state;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;

/**
 * Score tracking class with JavaFX property binding support.
 * Maintains the current game score and provides observable property
 * for UI updates.
 */

public final class Score {

    private final IntegerProperty score = new SimpleIntegerProperty(0);

    /**
     * Returns the observable score property for binding to UI.
     *
     * @return IntegerProperty containing the current score
     */
    public IntegerProperty scoreProperty() {
        return score;
    }

    /**
     * Adds points to the current score.
     *
     * @param i the number of points to add (can be negative for penalties)
     */
    public void add(int i){
        score.setValue(score.getValue() + i);
    }

    /**
     * Resets the score to 0.
     * Called when starting a new game.
     */
    public void reset() {
        score.setValue(0);
    }
}
