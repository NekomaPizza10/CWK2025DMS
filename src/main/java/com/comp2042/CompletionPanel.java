package com.comp2042;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public class CompletionPanel extends StackPane {

    private Runnable onRetry;
    private Runnable onMainMenu;

    public CompletionPanel(String timeString, boolean isNewBest, String previousBest) {
        getStylesheets().add(getClass().getResource("/completion.css").toExternalForm());
        getStyleClass().add("completion-overlay");

        VBox card = new VBox();
        card.getStyleClass().add("completion-card");

        card.setMaxHeight(Region.USE_PREF_SIZE);
        card.setMaxWidth(Region.USE_PREF_SIZE);

        Label title = new Label("COMPLETED!");
        title.getStyleClass().add("completion-title");
        card.getChildren().add(title);

        addStat(card, "FINAL TIME", timeString, true);

        if (isNewBest) {
            Label badge = new Label("★ NEW RECORD ★");
            badge.getStyleClass().add("completion-new-best");
            card.getChildren().add(badge);
        } else if (previousBest != null) {
            Label best = new Label("Best: " + previousBest);
            best.getStyleClass().add("stat-label");
            card.getChildren().add(best);
        }

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

    public void setOnRetry(Runnable r) { this.onRetry = r; }
    public void setOnMainMenu(Runnable r) { this.onMainMenu = r; }
}