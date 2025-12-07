package com.comp2042.state;

import com.comp2042.model.GameMode;

/**
 * Handles all score calculations using Tetris scoring system
 */
public class ScoringManager {
    private final GameState gameState;

    /**
     * Creates a new ScoringManager.
     * @param gameState the game state to manage scoring for
     */
    public ScoringManager(GameState gameState) {
        this.gameState = gameState;
    }

    /**
     * Calculates score for clearing lines based on official Tetris scoring.
     * Includes combo and back-to-back bonuses.
     * @param linesCleared number of lines cleared (0-4)
     * @return total points earned
     */
    public int calculateTetrisScore(int linesCleared) {
        if (linesCleared == 0) return 0;

        GameMode mode = gameState.getCurrentGameMode();

        if (mode == GameMode.NORMAL) {
            gameState.incrementNormalModeCombo();
        } else {
            gameState.incrementTwoMinutesCombo();
        }

        // Base points for line clears
        int baseScore = switch (linesCleared) {
            case 1 -> 100;  // Single
            case 2 -> 300;  // Double
            case 3 -> 500;  // Triple
            case 4 -> 800;  // Tetris
            default -> 800 + (linesCleared - 4) * 200;
        };

        int comboBonus;
        int backToBackBonus = 0;

        if (mode == GameMode.NORMAL) {
            int currentCombo = gameState.getNormalModeCombo();
            comboBonus = (currentCombo - 1) * 50;

            if (linesCleared == 4 && gameState.isNormalModeLastWasTetris()) {
                backToBackBonus = 400; 
            }
            gameState.setNormalModeLastWasTetris(linesCleared == 4);

        } else { // TWO_MINUTES
            int currentCombo = gameState.getTwoMinutesCombo();
            comboBonus = (currentCombo - 1) * 50;

            if (linesCleared == 4 && gameState.isTwoMinutesLastWasTetris()) {
                backToBackBonus = 400;
            }
            gameState.setTwoMinutesLastWasTetris(linesCleared == 4);
        }

        return baseScore + comboBonus + backToBackBonus;
    }

    /**
     * Adds bonus points for soft drop (manual downward movement).
     * @param distance number of cells dropped
     */
    public void addSoftDropBonus(int distance) {
        GameMode mode = gameState.getCurrentGameMode();
        if (mode == GameMode.NORMAL) {
            gameState.setNormalModeScore(gameState.getNormalModeScore() + distance);
        } else if (mode == GameMode.TWO_MINUTES) {
            gameState.setTwoMinutesScore(gameState.getTwoMinutesScore() + distance);
        }
    }

    /**
     * Adds bonus points for hard drop (instant drop to bottom).
     * @param distance number of cells dropped
     */
    public void addHardDropBonus(int distance) {
        GameMode mode = gameState.getCurrentGameMode();
        int bonus = distance * 5; // 5 points per cell for hard drop

        if (mode == GameMode.NORMAL) {
            gameState.setNormalModeScore(gameState.getNormalModeScore() + bonus);
        } else if (mode == GameMode.TWO_MINUTES) {
            gameState.setTwoMinutesScore(gameState.getTwoMinutesScore() + bonus);
        }
    }

    public void resetCombo() {
        GameMode mode = gameState.getCurrentGameMode();
        if (mode == GameMode.NORMAL) {
            gameState.resetNormalModeCombo();
            gameState.setNormalModeLastWasTetris(false);
        } else if (mode == GameMode.TWO_MINUTES) {
            gameState.resetTwoMinutesCombo();
            gameState.setTwoMinutesLastWasTetris(false);
        }
    }

    /**
     * Gets the current score for the active game mode.
     * @return current score
     */
    public int getCurrentScore() {
        GameMode mode = gameState.getCurrentGameMode();
        if (mode == GameMode.NORMAL) {
            return gameState.getNormalModeScore();
        } else if (mode == GameMode.TWO_MINUTES) {
            return gameState.getTwoMinutesScore();
        }
        return 0;
    }
}