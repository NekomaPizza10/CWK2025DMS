package com.comp2042.ui.manager;

import com.comp2042.controller.GameController;
import com.comp2042.state.GameState;
import com.comp2042.state.TimerManager;
import com.comp2042.ui.handlers.UIUpdater;
import com.comp2042.ui.panel.CompletionPanel;
import com.comp2042.ui.panel.TwoMinutesCompletionPanel;
import javafx.scene.layout.StackPane;

/**
 * Manages challenge completion screens (40 Lines and 2 Minutes modes).
 * Handles best score/time tracking and completion panel display.
 */
public class ChallengeCompletionManager {

    private final GameState gameState;
    private final TimerManager timerManager;
    private final UIUpdater uiUpdater;

    private GameController gameController;
    private TwoMinutesCompletionPanel currentCompletionPanel;
    private CompletionPanel currentFortyLinesPanel;

    public ChallengeCompletionManager(GameState gameState, TimerManager timerManager,
                                      UIUpdater uiUpdater) {
        this.gameState = gameState;
        this.timerManager = timerManager;
        this.uiUpdater = uiUpdater;
    }

    public void setGameController(GameController gameController) {
        this.gameController = gameController;
    }

     // Handle 40 Lines challenge completion
    public void completeFortyLinesChallenge(StackPane rootPane, Runnable onRetry, Runnable onMainMenu) {
        timerManager.stopAllTimers();
        gameState.setGameOver(true);

        long finalTime = timerManager.getElapsedTime();
        boolean isNewBest = finalTime < gameState.getFortyLinesBestTime();

        if (isNewBest) {
            gameState.setFortyLinesBestTime(finalTime);
            uiUpdater.updateBestTimeDisplay(formatTime(finalTime));
        }

        String timeString = formatTime(finalTime);
        String previousBest = (gameState.getFortyLinesBestTime() != Long.MAX_VALUE && !isNewBest) ?
                formatTime(gameState.getFortyLinesBestTime()) : null;

        CompletionPanel panel = new CompletionPanel(timeString, isNewBest, previousBest);
        currentFortyLinesPanel = panel;
        panel.setOnRetry(onRetry);
        panel.setOnMainMenu(onMainMenu);

        if (rootPane != null) {
            rootPane.getChildren().add(panel);
            StackPane.setAlignment(panel, javafx.geometry.Pos.CENTER);
        }
    }

     // Handle 2 Minutes challenge completion
    public void completeTwoMinutesChallenge(StackPane rootPane, Runnable onRetry, Runnable onMainMenu) {
        timerManager.stopAllTimers();
        gameState.setGameOver(true);

        int finalScore = gameState.getTwoMinutesScore();
        boolean isNewBest = finalScore > GameState.getTwoMinutesBestScore();

        if (isNewBest) {
            GameState.setTwoMinutesBestScore(finalScore);
            uiUpdater.updateBestScoreDisplay(finalScore);
        }

        int linesCleared = gameController.getLinesCleared();
        String previousBest = (GameState.getTwoMinutesBestScore() > 0 && !isNewBest) ?
                String.valueOf(GameState.getTwoMinutesBestScore()) : null;

        TwoMinutesCompletionPanel panel = new TwoMinutesCompletionPanel(
                finalScore, linesCleared, isNewBest, previousBest);
        currentCompletionPanel = panel;
        panel.setOnRetry(onRetry);
        panel.setOnMainMenu(onMainMenu);

        if (rootPane != null) {
            rootPane.getChildren().add(panel);
            StackPane.setAlignment(panel, javafx.geometry.Pos.CENTER);
        }
    }

     // Remove completion panels from display
    public void removeCompletionPanels(StackPane rootPane) {
        if (rootPane == null) return;

        if (currentCompletionPanel != null) {
            rootPane.getChildren().remove(currentCompletionPanel);
            currentCompletionPanel = null;
        }
        if (currentFortyLinesPanel != null) {
            rootPane.getChildren().remove(currentFortyLinesPanel);
            currentFortyLinesPanel = null;
        }
    }

     // Format time in mm:ss.SSS format
    private String formatTime(long timeMs) {
        int minutes = (int) (timeMs / 60000);
        int seconds = (int) ((timeMs % 60000) / 1000);
        int millis = (int) (timeMs % 1000);
        return String.format("%d:%02d.%03d", minutes, seconds, millis);
    }
}