package com.comp2042;

import com.comp2042.logic.bricks.RandomBrickGenerator;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.animation.AnimationTimer;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;

import javafx.scene.Group;
import javafx.scene.effect.Reflection;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.control.Label;

import javafx.util.Duration;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.List;

public class GuiController implements Initializable {

    private static final int BRICK_SIZE = 25;
    private static final int PREVIEW_BRICK_SIZE = 20;

    @FXML
    private GridPane gamePanel;

    @FXML
    private Group groupNotification;

    @FXML
    private GridPane brickPanel;

    @FXML
    private GridPane holdPanel;

    @FXML
    private GridPane nextPanel1, nextPanel2, nextPanel3, nextPanel4, nextPanel5;

    @FXML
    private GameOverPanel gameOverPanel;

    @FXML
    private Label piecesLabel, piecesValue, linesLabel, linesValue, timeLabel, timeValue;

    private Rectangle[][] displayMatrix;
    private InputEventListener eventListener;
    private Rectangle[][] rectangles;
    private Rectangle[][] holdRectangles;
    private Rectangle[][] nextRectangles1, nextRectangles2, nextRectangles3, nextRectangles4, nextRectangles5;
    private Timeline timeLine;

    private final BooleanProperty isPause = new SimpleBooleanProperty();
    private final BooleanProperty isGameOver = new SimpleBooleanProperty();

    private long gameStartTime;
    private AnimationTimer timer;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        gamePanel.setFocusTraversable(true);
        gamePanel.requestFocus();
        gamePanel.setOnKeyPressed(this::handleKeyPress);
        gameOverPanel.setVisible(false);
    }

        public void handleKeyPress(KeyEvent keyEvent) {
            if (isPause.getValue() == Boolean.FALSE && isGameOver.getValue() == Boolean.FALSE) {
                if (keyEvent.getCode() == KeyCode.LEFT || keyEvent.getCode() == KeyCode.A) {
                    refreshBrick(eventListener.onLeftEvent(new MoveEvent(EventType.LEFT, EventSource.USER)));
                    keyEvent.consume();
                }
                if (keyEvent.getCode() == KeyCode.RIGHT || keyEvent.getCode() == KeyCode.D) {
                    refreshBrick(eventListener.onRightEvent(new MoveEvent(EventType.RIGHT, EventSource.USER)));
                    keyEvent.consume();
                }
                if (keyEvent.getCode() == KeyCode.UP || keyEvent.getCode() == KeyCode.W) {
                    refreshBrick(eventListener.onRotateEvent(new MoveEvent(EventType.ROTATE, EventSource.USER)));
                    keyEvent.consume();
                }
                if (keyEvent.getCode() == KeyCode.DOWN || keyEvent.getCode() == KeyCode.S) {
                    moveDown(new MoveEvent(EventType.DOWN, EventSource.USER));
                    keyEvent.consume();
                }
                if (keyEvent.getCode() == KeyCode.SHIFT || keyEvent.getCode() == KeyCode.C) {
                    handleHold();
                    keyEvent.consume();
                }
            }
            if (keyEvent.getCode() == KeyCode.N) {
                newGame(null);
            }
        }

    private void handleHold() {
        if (eventListener instanceof GameController) {
            ((GameController) eventListener).holdBrick();
            updateHoldDisplay();
            updateNextDisplay();
        }
    }

    public void initGameView(int[][] boardMatrix, ViewData brick) {
        //Initialize game baord
        displayMatrix = new Rectangle[boardMatrix.length][boardMatrix[0].length];
        for (int i = 0; i < boardMatrix.length; i++) {
            for (int j = 0; j < boardMatrix[i].length; j++) {
                Rectangle rectangle = new Rectangle(BRICK_SIZE, BRICK_SIZE);
                rectangle.setFill(Color.TRANSPARENT);
                rectangle.setStroke(Color.rgb(40, 40, 40));
                rectangle.setStrokeWidth(0.5);
                displayMatrix[i][j] = rectangle;
                gamePanel.add(rectangle, j, i);
            }
        }

        // Initialize current brick display
        rectangles = new Rectangle[brick.getBrickData().length][brick.getBrickData()[0].length];
        for (int i = 0; i < brick.getBrickData().length; i++) {
            for (int j = 0; j < brick.getBrickData()[i].length; j++) {
                Rectangle rectangle = new Rectangle(BRICK_SIZE, BRICK_SIZE);
                rectangle.setFill(getFillColor(brick.getBrickData()[i][j]));
                rectangles[i][j] = rectangle;
                brickPanel.add(rectangle, j, i);
            }
        }
        brickPanel.setLayoutX(gamePanel.getLayoutX() + brick.getxPosition() * BRICK_SIZE);
        brickPanel.setLayoutY(gamePanel.getLayoutY() + brick.getyPosition() * BRICK_SIZE);

        // Initialize HOLD panel
        initializePreviewPanel(holdPanel, PREVIEW_BRICK_SIZE);
        holdRectangles = createPreviewRectangles(holdPanel, PREVIEW_BRICK_SIZE);

        // Initialize NEXT panels
        initializePreviewPanel(nextPanel1, PREVIEW_BRICK_SIZE);
        initializePreviewPanel(nextPanel2, PREVIEW_BRICK_SIZE);
        initializePreviewPanel(nextPanel3, PREVIEW_BRICK_SIZE);
        initializePreviewPanel(nextPanel4, PREVIEW_BRICK_SIZE);
        initializePreviewPanel(nextPanel5, PREVIEW_BRICK_SIZE);

        nextRectangles1 = createPreviewRectangles(nextPanel1, PREVIEW_BRICK_SIZE);
        nextRectangles2 = createPreviewRectangles(nextPanel2, PREVIEW_BRICK_SIZE);
        nextRectangles3 = createPreviewRectangles(nextPanel3, PREVIEW_BRICK_SIZE);
        nextRectangles4 = createPreviewRectangles(nextPanel4, PREVIEW_BRICK_SIZE);
        nextRectangles5 = createPreviewRectangles(nextPanel5, PREVIEW_BRICK_SIZE);

        // Start game timer
        gameStartTime = System.currentTimeMillis();
        timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                updateTimeDisplay();
            }
        };
        timer.start();

        //Start game loop
        timeLine = new Timeline(new KeyFrame(
                Duration.millis(400),
                ae -> moveDown(new MoveEvent(EventType.DOWN, EventSource.THREAD))
        ));
        timeLine.setCycleCount(Timeline.INDEFINITE);
        timeLine.play();
    }

    private void initializePreviewPanel(GridPane panel, int size) {
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                Rectangle rectangle = new Rectangle(size, size);
                rectangle.setFill(Color.TRANSPARENT);
                panel.add(rectangle, j, i);
            }
        }
    }

    private Rectangle[][] createPreviewRectangles(GridPane panel, int size) {
        Rectangle[][] rects = new Rectangle[4][4];
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                Rectangle rectangle = new Rectangle(size, size);
                rectangle.setFill(Color.TRANSPARENT);
                rects[i][j] = rectangle;
                panel.add(rectangle, j, i);
            }
        }
        return rects;
    }

    private void updatePreviewPanel(Rectangle[][] rects, int[][] brickData) {
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                if (i < brickData.length && j < brickData[i].length) {
                    rects[i][j].setFill(getFillColor(brickData[i][j]));
                } else {
                    rects[i][j].setFill(Color.TRANSPARENT);
                }
            }
        }
    }

    private Paint getFillColor(int i) {
        switch (i) {
            case 0: return Color.TRANSPARENT;
            case 1: return Color.CYAN;
            case 2: return Color.BLUE;
            case 3: return Color.ORANGE;
            case 4: return Color.YELLOW;
            case 5: return Color.GREEN;
            case 6: return Color.PURPLE;
            case 7: return Color.RED;
            default: return Color.WHITE;
        }
    }


    private void refreshBrick(ViewData brick) {
        if (isPause.getValue() == Boolean.FALSE) {
            brickPanel.setLayoutX(gamePanel.getLayoutX() + brick.getxPosition() * BRICK_SIZE);
            brickPanel.setLayoutY(gamePanel.getLayoutY() + brick.getyPosition() * BRICK_SIZE);
            for (int i = 0; i < brick.getBrickData().length; i++) {
                for (int j = 0; j < brick.getBrickData()[i].length; j++) {
                    setRectangleData(brick.getBrickData()[i][j], rectangles[i][j]);
                }
            }
        }
    }

    public void refreshGameBackground(int[][] board) {
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[i].length; j++) {
                setRectangleData(board[i][j], displayMatrix[i][j]);
            }
        }
    }

    private void setRectangleData(int color, Rectangle rectangle) {
        rectangle.setFill(getFillColor(color));
    }

    private void moveDown(MoveEvent event) {
        if (isPause.getValue() == Boolean.FALSE) {
            DownData downData = eventListener.onDownEvent(event);
            if (downData.getClearRow() != null && downData.getClearRow().getLinesRemoved() > 0) {
                NotificationPanel notificationPanel = new NotificationPanel("+" + downData.getClearRow().getScoreBonus());
                groupNotification.getChildren().add(notificationPanel);
                notificationPanel.showScore(groupNotification.getChildren());
                updateStatsDisplay();
            }
            refreshBrick(downData.getViewData());
            updateNextDisplay();
        }
        gamePanel.requestFocus();
    }

    public void updateHoldDisplay() {
        if (eventListener instanceof GameController) {
            int[][] holdData = ((GameController) eventListener).getHoldBrickData();
            updatePreviewPanel(holdRectangles, holdData);
        }
    }

    public void updateNextDisplay() {
        if (eventListener instanceof GameController) {
            List<int[][]> nextBricks = ((GameController) eventListener).getNextBricksData();
            if (nextBricks.size() > 0) updatePreviewPanel(nextRectangles1, nextBricks.get(0));
            if (nextBricks.size() > 1) updatePreviewPanel(nextRectangles2, nextBricks.get(1));
            if (nextBricks.size() > 2) updatePreviewPanel(nextRectangles3, nextBricks.get(2));
            if (nextBricks.size() > 3) updatePreviewPanel(nextRectangles4, nextBricks.get(3));
            if (nextBricks.size() > 4) updatePreviewPanel(nextRectangles5, nextBricks.get(4));
        }
    }

    private void updateStatsDisplay() {
        if (eventListener instanceof GameController) {
            GameController gc = (GameController) eventListener;
            piecesValue.setText(String.valueOf(gc.getPiecesPlaced()));
            linesValue.setText(gc.getLinesCleared() + "/40");
        }
    }

    private void updateTimeDisplay() {
        if (!isGameOver.getValue()) {
            long elapsed = System.currentTimeMillis() - gameStartTime;
            int minutes = (int) (elapsed / 60000);
            int seconds = (int) ((elapsed % 60000) / 1000);
            int millis = (int) (elapsed % 1000);
            timeValue.setText(String.format("%d:%02d.%03d", minutes, seconds, millis));
        }
    }

    public void setEventListener(InputEventListener eventListener) {
        this.eventListener = eventListener;
    }

    public void bindScore(IntegerProperty integerProperty) {
        //Score binding
    }

    public void gameOver() {
        timeLine.stop();
        if (timer != null) timer.stop(); //Stop the timer
        gameOverPanel.setVisible(true);
        isGameOver.setValue(Boolean.TRUE);
    }

    public void newGame(ActionEvent actionEvent) {
        timeLine.stop();
        if (timer != null) timer.stop();
        gameOverPanel.setVisible(false);
        eventListener.createNewGame();
        gamePanel.requestFocus();

        //Reset Display
        gameStartTime = System.currentTimeMillis();
        piecesValue.setText("0");
        linesValue.setText("0/40");
        timeValue.setText("0:00.000");
        updateHoldDisplay();
        updateNextDisplay();

        if (timer != null) timer.start();   //Timer starts when new game starts
        timeLine.play();
        isPause.setValue(Boolean.FALSE);
        isGameOver.setValue(Boolean.FALSE);
    }

    public void pauseGame(ActionEvent actionEvent) {
        gamePanel.requestFocus();
    }
}
