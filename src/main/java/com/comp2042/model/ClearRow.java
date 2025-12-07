package com.comp2042.model;

import com.comp2042.core.MatrixOperations;

public final class ClearRow {

    private final int linesRemoved;
    private final int[][] newMatrix;
    private final int scoreBonus;

    /**
     * Creates a new ClearRow result.
     *
     * @param linesRemoved number of lines that were cleared
     * @param newMatrix the updated board matrix after clearing
     * @param scoreBonus bonus points earned
     */
    public ClearRow(int linesRemoved, int[][] newMatrix, int scoreBonus) {
        this.linesRemoved = linesRemoved;
        this.newMatrix = newMatrix;
        this.scoreBonus = scoreBonus;
    }

    /**
     * Gets the number of lines that were removed.
     * @return lines cleared (0-4)
     */
    public int getLinesRemoved() {
        return linesRemoved;
    }

    /**
     * Gets a defensive copy of the new board matrix.
     * @return updated board state after line clear
     */
    public int[][] getNewMatrix() {
        return MatrixOperations.copy(newMatrix);
    }

    /**
     * Gets the score bonus earned from clearing lines.
     *
     * @return bonus points (50 * lines²)
     */
    public int getScoreBonus() {
        return scoreBonus;
    }
}
