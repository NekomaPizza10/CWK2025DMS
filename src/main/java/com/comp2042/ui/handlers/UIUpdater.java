package com.comp2042.ui.handlers;

import com.comp2042.model.GameMode;
import com.comp2042.state.GameState;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.layout.Region;

// Handles UI updates for labels and stats display.
public class UIUpdater {

    private final GameState gameState;
    private Label scoreValue, bestScoreValue, bestTimeLabel;
    private Label piecesValue, linesValue, linesLabel, timeValue, timeLabel;
    private VBox scoreBox, bestScoreBox, bestTimeBox, scoreDisplayContainer;
    private Region scoreSeparator;
    private GameMode pendingGameMode;

    public UIUpdater(GameState gameState) {
        this.gameState = gameState;
    }

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

    public void updateScore(int score) {
        if (scoreValue != null) { scoreValue.setText(String.valueOf(score)); }
    }

    public void updateStats(int pieces, int lines, String time) {
        if (piecesValue != null) { piecesValue.setText(String.valueOf(pieces)); }
        if (linesValue != null) { linesValue.setText(String.valueOf(lines)); }
        if (time != null && timeValue != null) { timeValue.setText(time); }
    }

    public void resetStats(GameMode mode) {
        if (piecesValue != null) { piecesValue.setText("0"); }
        if (linesValue != null) { linesValue.setText("0"); }
        if (scoreValue != null) { scoreValue.setText("0"); }
        if (timeValue != null) {
            if (mode == GameMode.TWO_MINUTES) { timeValue.setText("2:00"); }
            else { timeValue.setText("0:00.000"); }
        }
    }

    public void updateBestScoreDisplay(int bestScore) {
        if (bestScoreValue != null) { bestScoreValue.setText(String.valueOf(bestScore)); }
    }

    public void updateBestTimeDisplay(String bestTime) {
        if (bestTimeLabel != null) { bestTimeLabel.setText(bestTime); }
    }
}