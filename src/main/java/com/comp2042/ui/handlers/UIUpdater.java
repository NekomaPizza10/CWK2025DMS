package com.comp2042.ui.handlers;

import com.comp2042.model.GameMode;
import com.comp2042.state.GameState;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.layout.Region;

/**
 * Handles UI updates for labels and statistics display in the Tetris game.
 * <p>
 * This class is responsible for managing all score, statistics, and time-related
 * UI elements. It provides methods to update individual display elements and
 * configure the UI layout based on the current game mode.
 * </p>
 *
 * <p>Key responsibilities include:</p>
 * <ul>
 *     <li>Binding UI label references for centralized management</li>
 *     <li>Configuring UI visibility based on game mode requirements</li>
 *     <li>Updating score, pieces, lines, and time displays</li>
 *     <li>Resetting statistics when starting a new game</li>
 *     <li>Managing best score and best time displays</li>
 * </ul>
 *
 * <p>Game mode-specific UI configurations:</p>
 * <table border="1">
 *     <caption>UI Element Visibility by Game Mode</caption>
 *     <tr><th>Element</th><th>Endless</th><th>Forty Lines</th><th>Two Minutes</th></tr>
 *     <tr><td>Score Box</td><td>Visible</td><td>Hidden</td><td>Visible</td></tr>
 *     <tr><td>Best Score Box</td><td>Hidden</td><td>Hidden</td><td>Visible</td></tr>
 *     <tr><td>Best Time Box</td><td>Hidden</td><td>Visible</td><td>Hidden</td></tr>
 *     <tr><td>Score Separator</td><td>Hidden</td><td>Hidden</td><td>Visible</td></tr>
 *     <tr><td>Time Label Text</td><td>"TIME"</td><td>"TIME"</td><td>"TIME LEFT"</td></tr>
 * </table>
 *
 * <p><b>Note:</b> This class uses a deferred configuration pattern. If
 * {@link #configureForGameMode(GameMode)} is called before labels are bound,
 * the configuration is stored and applied once {@link #bindLabels} is called.</p>
 *
 * @see GameState
 * @see GameMode
 */
public class UIUpdater {

    /**
     * The game state containing current game data.
     */
    private final GameState gameState;
    private Label scoreValue, bestScoreValue, bestTimeLabel;
    private Label piecesValue, linesValue, linesLabel, timeValue, timeLabel;
    private VBox scoreBox, bestScoreBox, bestTimeBox, scoreDisplayContainer;
    private Region scoreSeparator;

    /**
     * Stores a pending game mode configuration when labels aren't yet bound.
     */
    private GameMode pendingGameMode;

    /**
     * Constructs a new UIUpdater with the specified game state.
     *
     * @param gameState the {@link GameState} instance containing game data;
     *                  must not be {@code null}
     */
    public UIUpdater(GameState gameState) {
        this.gameState = gameState;
    }

    /**
     * Binds all UI label references for centralized management.
     * <p>
     * This method must be called to connect the UIUpdater to the actual
     * UI components. All subsequent update operations will modify these
     * bound labels.
     * </p>
     *
     * <p>If a game mode configuration was requested before labels were bound
     * (via {@link #configureForGameMode(GameMode)}), that configuration will
     * be applied immediately after binding.</p>
     *
     * @param scoreValue           the label for displaying current score
     * @param bestScoreValue       the label for displaying best score
     * @param bestTimeLabel        the label for displaying best time
     * @param scoreBox             the container for score display
     * @param bestScoreBox         the container for best score display
     * @param bestTimeBox          the container for best time display
     * @param scoreSeparator       the visual separator between score elements
     * @param scoreDisplayContainer the main container for all score displays
     * @param piecesValue          the label for displaying pieces placed count
     * @param linesValue           the label for displaying lines cleared count
     * @param linesLabel           the label for the lines header text
     * @param timeValue            the label for displaying time
     * @param timeLabel            the label for the time header text
     */
    public void bindLabels(Label scoreValue, Label bestScoreValue, Label bestTimeLabel,
                           VBox scoreBox, VBox bestScoreBox, VBox bestTimeBox,
                           Region scoreSeparator, VBox scoreDisplayContainer,
                           Label piecesValue, Label linesValue, Label linesLabel,
                           Label timeValue, Label timeLabel) {
        this.scoreValue = scoreValue;
        this.bestScoreValue = bestScoreValue;
        this.bestTimeLabel = bestTimeLabel;
        this.scoreBox = scoreBox;
        this.bestScoreBox = bestScoreBox;
        this.bestTimeBox = bestTimeBox;
        this.scoreSeparator = scoreSeparator;
        this.scoreDisplayContainer = scoreDisplayContainer;
        this.piecesValue = piecesValue;
        this.linesValue = linesValue;
        this.linesLabel = linesLabel;
        this.timeValue = timeValue;
        this.timeLabel = timeLabel;
        if (pendingGameMode != null) { configureForGameMode(pendingGameMode); pendingGameMode = null; }
    }

    /**
     * Configures the UI layout for the specified game mode.
     * <p>
     * This method adjusts the visibility and managed state of various UI
     * elements based on the game mode's requirements. Different modes
     * display different information:
     * </p>
     *
     * <ul>
     *     <li><b>FORTY_LINES:</b> Shows best time, hides score-related elements,
     *         time label shows "TIME"</li>
     *     <li><b>TWO_MINUTES:</b> Shows score and best score, hides best time,
     *         time label shows "TIME LEFT"</li>
     *     <li><b>ENDLESS (default):</b> Shows only current score,
     *         time label shows "TIME"</li>
     * </ul>
     *
     * <p>If labels haven't been bound yet (via {@link #bindLabels}), the
     * configuration is stored as pending and will be applied when labels
     * are bound.</p>
     *
     * @param mode the {@link GameMode} to configure the UI for
     * @see GameMode#FORTY_LINES
     * @see GameMode#TWO_MINUTES
     */
    public void configureForGameMode(GameMode mode) {
        if (scoreBox == null) { pendingGameMode = mode; return; }
        if (mode == GameMode.FORTY_LINES) {
            scoreBox.setVisible(false); scoreBox.setManaged(false);
            bestScoreBox.setVisible(false); bestScoreBox.setManaged(false);
            bestTimeBox.setVisible(true); bestTimeBox.setManaged(true);
            scoreSeparator.setVisible(false); scoreSeparator.setManaged(false);
            timeLabel.setText("TIME");
        } else if (mode == GameMode.TWO_MINUTES) {
            scoreBox.setVisible(true); scoreBox.setManaged(true);
            bestScoreBox.setVisible(true); bestScoreBox.setManaged(true);
            bestTimeBox.setVisible(false); bestTimeBox.setManaged(false);
            scoreSeparator.setVisible(true); scoreSeparator.setManaged(true);
            timeLabel.setText("TIME LEFT");
        } else {
            scoreBox.setVisible(true); scoreBox.setManaged(true);
            bestScoreBox.setVisible(false); bestScoreBox.setManaged(false);
            bestTimeBox.setVisible(false); bestTimeBox.setManaged(false);
            scoreSeparator.setVisible(false); scoreSeparator.setManaged(false);
            timeLabel.setText("TIME");
        }
    }

    /**
     * Updates the score display with the specified value.
     * <p>
     * This method is null-safe and will do nothing if the score label
     * hasn't been bound.
     * </p>
     *
     * @param score the current score value to display
     */
    public void updateScore(int score) {
        if (scoreValue != null) { scoreValue.setText(String.valueOf(score)); }
    }

    /**
     * Updates the statistics display with pieces, lines, and time values.
     * <p>
     * This method updates multiple statistics labels in a single call.
     * Each update is null-safe and will be skipped if the corresponding
     * label hasn't been bound.
     * </p>
     *
     * @param pieces the number of pieces placed
     * @param lines  the number of lines cleared
     * @param time   the time string to display (e.g., "1:30.500");
     *               if {@code null}, the time display is not updated
     */
    public void updateStats(int pieces, int lines, String time) {
        if (piecesValue != null) { piecesValue.setText(String.valueOf(pieces)); }
        if (linesValue != null) { linesValue.setText(String.valueOf(lines)); }
        if (time != null && timeValue != null) { timeValue.setText(time); }
    }

    /**
     * Resets all statistics displays to their initial values for the specified game mode.
     * <p>
     * This method sets all counters to zero and configures the time display
     * based on the game mode:
     * </p>
     * <ul>
     *     <li><b>TWO_MINUTES:</b> Time is set to "2:00" (countdown timer)</li>
     *     <li><b>Other modes:</b> Time is set to "0:00.000" (elapsed timer)</li>
     * </ul>
     *
     * <p>All updates are null-safe and will be skipped for unbound labels.</p>
     *
     * @param mode the {@link GameMode} determining the initial time display format
     */
    public void resetStats(GameMode mode) {
        if (piecesValue != null) { piecesValue.setText("0"); }
        if (linesValue != null) { linesValue.setText("0"); }
        if (scoreValue != null) { scoreValue.setText("0"); }
        if (timeValue != null) {
            if (mode == GameMode.TWO_MINUTES) { timeValue.setText("2:00"); }
            else { timeValue.setText("0:00.000"); }
        }
    }

    /**
     * Updates the best score display with the specified value.
     * <p>
     * This is typically used in timed modes (like Two Minutes) where
     * players compete for the highest score within a time limit.
     * </p>
     *
     * <p>This method is null-safe and will do nothing if the best score
     * label hasn't been bound.</p>
     *
     * @param bestScore the best score value to display
     */
    public void updateBestScoreDisplay(int bestScore) {
        if (bestScoreValue != null) { bestScoreValue.setText(String.valueOf(bestScore)); }
    }

    /**
     * Updates the best time display with the specified formatted time string.
     * <p>
     * This is typically used in line-clearing modes (like Forty Lines) where
     * players compete for the fastest completion time.
     * </p>
     *
     * <p>This method is null-safe and will do nothing if the best time
     * label hasn't been bound.</p>
     *
     * @param bestTime the formatted best time string to display
     *                 (e.g., "1:23.456" or "--:--.---" if no record exists)
     */
    public void updateBestTimeDisplay(String bestTime) {
        if (bestTimeLabel != null) { bestTimeLabel.setText(bestTime); }
    }
}