package com.comp2042;

import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.TranslateTransition;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.effect.Effect;
import javafx.scene.effect.Glow;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.util.Duration;

public class NotificationPanel extends BorderPane {

    public NotificationPanel(String text) {
        setMinHeight(100);
        setMinWidth(200);
        setMaxHeight(100);
        setMaxWidth(200);

        final Label score = new Label(text);
        score.getStyleClass().add("bonusStyle");
        final Effect glow = new Glow(0.6);
        score.setEffect(glow);
        score.setTextFill(Color.WHITE);
        score.setStyle("-fx-font-size: 36px; -fx-font-weight: bold;");
        setCenter(score);

        // Semi-transparent background for better visibility
        setStyle("-fx-background-color: rgba(0, 0, 0, 0.7); " +
                "-fx-background-radius: 10; " +
                "-fx-border-color: rgba(0, 255, 136, 0.5); " +
                "-fx-border-radius: 10; " +
                "-fx-border-width: 2;");

    }

    public void showScore(ObservableList<Node> list) {
        FadeTransition ft = new FadeTransition(Duration.millis(1500), this);
        ft.setFromValue(1.0);
        ft.setToValue(0.0);
        ft.setDelay(Duration.millis(500));      // Stay visible for 500ms first


        TranslateTransition tt = new TranslateTransition(Duration.millis(2000), this);
        tt.setToY(this.getLayoutY() - 60);

        ParallelTransition transition = new ParallelTransition(tt, ft);
        transition.setOnFinished(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                list.remove(NotificationPanel.this);

                // Force garbage collection hint to prevent memory buildup
                System.gc();
            }
        });
        transition.play();
    }
}
