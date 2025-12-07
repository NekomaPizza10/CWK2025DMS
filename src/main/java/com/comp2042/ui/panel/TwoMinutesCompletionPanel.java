package com.comp2042.ui.panel;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public class TwoMinutesCompletionPanel extends StackPane {

    private Runnable onRetry;
    private Runnable onMainMenu;

    /**
     * Creates a new TwoMinutesCompletionPanel.
     *
     * @param finalScore final score achieved
     * @param linesCleared total lines cleared
     * @param isNewBest whether this is a new best score
     * @param previousBest previous best score string (null if none)
     */
    public TwoMinutesCompletionPanel(int finalScore, int linesCleared, boolean isNewBest, String previousBest) {
        getStylesheets().add(getClass().getResource("/completion.css").toExternalForm());
        getStyleClass().add("completion-overlay");

        // Main Card Container
        VBox card = new VBox();
        card.getStyleClass().add("completion-card");

        card.setMaxHeight(Region.USE_PREF_SIZE);
        card.setMaxWidth(Region.USE_PREF_SIZE);


        Label title = new Label("TIME'S UP!");
        title.getStyleClass().add("completion-title");
        card.getChildren().add(title);

        addStat(card, "FINAL SCORE", String.valueOf(finalScore), true);

        if (isNewBest) {
            Label badge = new Label("★ NEW HIGH SCORE ★");
            badge.getStyleClass().add("completion-new-best");
            card.getChildren().add(badge);
        } else if (previousBest != null) {
            Label best = new Label("Best: " + previousBest);
            best.getStyleClass().add("stat-label");
            card.getChildren().add(best);
        }

        addStat(card, "LINES CLEARED", String.valueOf(linesCleared), false);

        HBox buttons = new HBox(15);
        buttons.setAlignment(Pos.CENTER);
        buttons.getStyleClass().add("button-box");

        Button retryBtn = new Button("RETRY");
        retryBtn.getStyleClass().add("unified-button");
        retryBtn.setOnAction(e -> { if (onRetry != null) onRetry.run(); });

        Button menuBtn = new Button("EXIT");
        menuBtn.getStyleClass().add("unified-button");
        menuBtn.setOnAction(e -> { if (onMainMenu != null) onMainMenu.run(); });

        buttons.getChildren().addAll(retryBtn, menuBtn);
        card.getChildren().add(buttons);

        Label hint = new Label("Press 'N' for Instant Restart");
        hint.getStyleClass().add("completion-hint");
        card.getChildren().add(hint);

        getChildren().add(card);
    }

    private void addStat(VBox container, String labelText, String valueText, boolean isHighlight) {
        VBox box = new VBox(0);
        box.setAlignment(Pos.CENTER);
        Label lbl = new Label(labelText);
        lbl.getStyleClass().add("stat-label");
        Label val = new Label(valueText);
        val.getStyleClass().add(isHighlight ? "stat-value-highlight" : "stat-value");
        box.getChildren().addAll(lbl, val);
        container.getChildren().add(box);
    }

    /**
     * Sets the retry callback.
     * @param r callback to execute
     */
    public void setOnRetry(Runnable r) { this.onRetry = r; }

    /**
     * Sets the main menu callback.
     * @param r callback to execute
     */
    public void setOnMainMenu(Runnable r) { this.onMainMenu = r; }
}