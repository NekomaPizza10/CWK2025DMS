package com.comp2042.state;

import com.comp2042.model.GameMode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

// Tests for ScoringManager - Tetris scoring system
class ScoringManagerTest {

    private GameState gameState;
    private ScoringManager scoringManager;

    @BeforeEach
    void setUp() {
        gameState = new GameState();
        scoringManager = new ScoringManager(gameState);
    }

    // ========== Base Line Clear Scoring ==========
    @Test
    @DisplayName("0 lines cleared gives 0 points")
    void zeroLinesClearedGivesZeroPoints() {
        // Given: Normal mode
        gameState.setCurrentGameMode(GameMode.NORMAL);
        // When: 0 lines cleared
        int score = scoringManager.calculateTetrisScore(0);
        // Then: 0 points
        assertEquals(0, score, "0 lines should give 0 points");
    }

    @Test
    @DisplayName("Single line clear gives 100 points")
    void singleLineClearGives100Points() {
        // Given: Normal mode, no combo
        gameState.setCurrentGameMode(GameMode.NORMAL);
        // When: Clear 1 line
        int score = scoringManager.calculateTetrisScore(1);
        // Then: 100 base points
        assertEquals(100, score, "1 line should give 100 points");
    }

    @Test
    @DisplayName("Double line clear gives 300 points")
    void doubleLineClearGives300Points() {
        // Given: Normal mode
        gameState.setCurrentGameMode(GameMode.NORMAL);
        // When: Clear 2 lines
        int score = scoringManager.calculateTetrisScore(2);
        // Then: 300 base points
        assertEquals(300, score, "2 lines should give 300 points");
    }

    @Test
    @DisplayName("Triple line clear gives 500 points")
    void tripleLineClearGives500Points() {
        // Given: Normal mode
        gameState.setCurrentGameMode(GameMode.NORMAL);
        // When: Clear 3 lines
        int score = scoringManager.calculateTetrisScore(3);
        // Then: 500 base points
        assertEquals(500, score, "3 lines should give 500 points");
    }

    @Test
    @DisplayName("Tetris (4 lines) gives 800 points")
    void tetrisFourLinesGives800Points() {
        // Given: Normal mode
        gameState.setCurrentGameMode(GameMode.NORMAL);
        // When: Clear 4 lines (Tetris!)
        int score = scoringManager.calculateTetrisScore(4);
        // Then: 800 base points
        assertEquals(800, score, "4 lines (Tetris) should give 800 points");
    }

    // ========== Combo System ==========

    @Test
    @DisplayName("Combo adds 50 points per consecutive clear")
    void comboAdds50PointsPerClear() {
        // Given: Normal mode
        gameState.setCurrentGameMode(GameMode.NORMAL);
        // When: First clear (combo = 0)
        int score1 = scoringManager.calculateTetrisScore(1);
        assertEquals(100, score1, "First clear: 100 base + 0 combo = 100");
        // When: Second clear (combo = 1)
        int score2 = scoringManager.calculateTetrisScore(1);
        assertEquals(150, score2, "Second clear: 100 base + 50 combo = 150");
        // When: Third clear (combo = 2)
        int score3 = scoringManager.calculateTetrisScore(1);
        assertEquals(200, score3, "Third clear: 100 base + 100 combo = 200");
    }

    @Test
    @DisplayName("resetCombo() resets combo to 0")
    void resetComboResetsToZero() {
        // Given: Build combo
        gameState.setCurrentGameMode(GameMode.NORMAL);
        scoringManager.calculateTetrisScore(1); // combo = 1
        scoringManager.calculateTetrisScore(1); // combo = 2
        assertEquals(2, gameState.getNormalModeCombo(), "Combo should be 2");
        // When: Reset combo
        scoringManager.resetCombo();
        // Then: Combo is 0
        assertEquals(0, gameState.getNormalModeCombo(), "Combo should reset to 0");
        // Then: Next score is base only
        int score = scoringManager.calculateTetrisScore(1);
        assertEquals(100, score, "After reset, should get base points only");
    }

    // ========== Back-to-Back Tetris Bonus ==========

    @Test
    @DisplayName("Back-to-back Tetris gives +400 bonus")
    void backToBackTetrisGivesBonus() {
        // Given: Normal mode
        gameState.setCurrentGameMode(GameMode.NORMAL);
        // When: First Tetris
        int score1 = scoringManager.calculateTetrisScore(4);
        assertEquals(800, score1, "First Tetris: 800 base (no back-to-back yet)");
        // When: Second Tetris immediately (back-to-back!)
        int score2 = scoringManager.calculateTetrisScore(4);
        assertEquals(1250, score2, "Second Tetris: 800 base + 50 combo + 400 back-to-back = 1250");
    }

    @Test
    @DisplayName("Non-Tetris clear breaks back-to-back streak")
    void nonTetrisClearBreaksBackToBack() {
        // Given: Establish back-to-back
        gameState.setCurrentGameMode(GameMode.NORMAL);
        scoringManager.calculateTetrisScore(4); // First Tetris
        scoringManager.calculateTetrisScore(4); // Second Tetris - back-to-back active

        // When: Clear single line (breaks streak)
        scoringManager.calculateTetrisScore(1);
        // Then: Back-to-back flag is false
        assertFalse(gameState.isNormalModeLastWasTetris(), "Single line should break back-to-back");
        // When: Next Tetris
        int score = scoringManager.calculateTetrisScore(4);
        // Should NOT have back-to-back bonus (800 base + 150 combo)
        assertEquals(950, score, "Tetris after break: 800 base + 150 combo (no back-to-back)");
    }

    // ========== Two Minutes Mode Scoring ==========

    @Test
    @DisplayName("Two Minutes mode has separate score tracking")
    void twoMinutesModeHasSeparateScore() {
        // Given: Two Minutes mode
        gameState.setCurrentGameMode(GameMode.TWO_MINUTES);
        // When: Clear lines
        scoringManager.calculateTetrisScore(2); // 300 points
        // Then: Two Minutes score increases, Normal score unchanged
        assertEquals(0, gameState.getNormalModeCombo(), "Normal combo should not increase");
        assertEquals(1, gameState.getTwoMinutesCombo(), "Two Minutes combo should increase");
    }

    @Test
    @DisplayName("Two Minutes mode has independent combo")
    void twoMinutesModeHasIndependentCombo() {
        // Given: Two Minutes mode
        gameState.setCurrentGameMode(GameMode.TWO_MINUTES);

        // When: Build combo
        int score1 = scoringManager.calculateTetrisScore(1); // 100
        int score2 = scoringManager.calculateTetrisScore(1); // 150

        assertEquals(100, score1, "First clear: 100 points");
        assertEquals(150, score2, "Second clear: 150 points (with combo)");

        // Then: Normal mode combo unaffected
        assertEquals(0, gameState.getNormalModeCombo(), "Normal combo should be independent");
    }

    // ========== Soft Drop Bonus ==========

    @Test
    @DisplayName("Soft drop adds 1 point per cell in Normal mode")
    void softDropAddsPointsInNormalMode() {
        // Given: Normal mode with score 100
        gameState.setCurrentGameMode(GameMode.NORMAL);
        gameState.setNormalModeScore(100);
        // When: Soft drop 5 cells
        scoringManager.addSoftDropBonus(5);
        // Then: Score increases by 5
        assertEquals(105, gameState.getNormalModeScore(), "Soft drop should add 1 point per cell");
    }

    @Test
    @DisplayName("Soft drop adds points in Two Minutes mode")
    void softDropAddsPointsInTwoMinutesMode() {
        // Given: Two Minutes mode with score 200
        gameState.setCurrentGameMode(GameMode.TWO_MINUTES);
        gameState.setTwoMinutesScore(200);
        // When: Soft drop 3 cells
        scoringManager.addSoftDropBonus(3);
        // Then: Score increases by 3
        assertEquals(203, gameState.getTwoMinutesScore(), "Soft drop should add 1 point per cell");
    }

    @Test
    @DisplayName("Soft drop does nothing in Forty Lines mode")
    void softDropDoesNothingInFortyLinesMode() {
        // Given: Forty Lines mode (no scoring)
        gameState.setCurrentGameMode(GameMode.FORTY_LINES);
        // When: Soft drop
        scoringManager.addSoftDropBonus(10);
        // Then: Scores remain 0
        assertEquals(0, gameState.getNormalModeScore(), "Normal score unchanged");
        assertEquals(0, gameState.getTwoMinutesScore(), "Two Minutes score unchanged");
    }

    // ========== Hard Drop Bonus ==========

    @Test
    @DisplayName("Hard drop adds 5 points per cell in Normal mode")
    void hardDropAdds5PointsPerCellInNormalMode() {
        // Given: Normal mode with score 100
        gameState.setCurrentGameMode(GameMode.NORMAL);
        gameState.setNormalModeScore(100);
        // When: Hard drop 10 cells
        scoringManager.addHardDropBonus(10);
        // Then: Score increases by 50 (5 per cell)
        assertEquals(150, gameState.getNormalModeScore(), "Hard drop should add 5 points per cell");
    }

    @Test
    @DisplayName("Hard drop adds 5 points per cell in Two Minutes mode")
    void hardDropAdds5PointsPerCellInTwoMinutesMode() {
        // Given: Two Minutes mode with score 50
        gameState.setCurrentGameMode(GameMode.TWO_MINUTES);
        gameState.setTwoMinutesScore(50);
        // When: Hard drop 5 cells
        scoringManager.addHardDropBonus(5);
        // Then: Score increases by 25
        assertEquals(75, gameState.getTwoMinutesScore(), "Hard drop should add 5 points per cell");
    }

    // ========== getCurrentScore() ==========

    @Test
    @DisplayName("getCurrentScore() returns Normal mode score")
    void getCurrentScoreReturnsNormalModeScore() {
        // Given: Normal mode with score 1500
        gameState.setCurrentGameMode(GameMode.NORMAL);
        gameState.setNormalModeScore(1500);
        // When: Get current score
        int score = scoringManager.getCurrentScore();
        // Then: Returns Normal mode score
        assertEquals(1500, score, "Should return Normal mode score");
    }

    @Test
    @DisplayName("getCurrentScore() returns Two Minutes mode score")
    void getCurrentScoreReturnsTwoMinutesScore() {
        // Given: Two Minutes mode with score 2500
        gameState.setCurrentGameMode(GameMode.TWO_MINUTES);
        gameState.setTwoMinutesScore(2500);
        // When: Get current score
        int score = scoringManager.getCurrentScore();
        // Then: Returns Two Minutes mode score
        assertEquals(2500, score, "Should return Two Minutes mode score");
    }

    @Test
    @DisplayName("getCurrentScore() returns 0 for Forty Lines mode")
    void getCurrentScoreReturns0ForFortyLinesMode() {
        // Given: Forty Lines mode (no scoring)
        gameState.setCurrentGameMode(GameMode.FORTY_LINES);
        // When: Get current score
        int score = scoringManager.getCurrentScore();
        // Then: Returns 0
        assertEquals(0, score, "Forty Lines mode has no score");
    }

    // ========== Complex Scenario ==========

    @Test
    @DisplayName("Complex scoring: combos + back-to-back + drops")
    void complexScoringScenario() {
        // Given: Normal mode
        gameState.setCurrentGameMode(GameMode.NORMAL);
        int totalScore = 0;

        // 1. Single line (100 + 0 combo = 100)
        totalScore += scoringManager.calculateTetrisScore(1);
        assertEquals(100, totalScore, "After single: 100");

        // 2. Double (300 + 50 combo = 350)
        totalScore += scoringManager.calculateTetrisScore(2);
        assertEquals(450, totalScore, "After double: 450");

        // 3. Tetris! (800 + 100 combo = 900)
        totalScore += scoringManager.calculateTetrisScore(4);
        assertEquals(1350, totalScore, "After first Tetris: 1350");

        // 4. Another Tetris! (800 + 150 combo + 400 back-to-back = 1350)
        totalScore += scoringManager.calculateTetrisScore(4);
        assertEquals(2700, totalScore, "After back-to-back Tetris: 2700");

        // 5. Hard drop 15 cells (+75 points)
        scoringManager.addHardDropBonus(15);
        gameState.setNormalModeScore(gameState.getNormalModeScore() + 75);

        // 6. Miss (no clear) - breaks combo
        scoringManager.resetCombo();

        // 7. New single (100 + 0 = 100)
        totalScore += scoringManager.calculateTetrisScore(1);
        assertEquals(2800, totalScore, "After reset and new single: 2800");
    }
}