package com.comp2042;

import com.comp2042.logic.bricks.RandomBrickGenerator;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.animation.AnimationTimer;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;

import javafx.event.ActionEvent;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;

import javafx.scene.Group;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import javafx.scene.control.Label;


import javafx.util.Duration;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.List;

public class GuiController implements Initializable {

    private GameMode currentGameMode = GameMode.NORMAL;
    private int baseDropSpeed = 800; // Starting speed in ms
    private int currentDropSpeed = 800;
    private static final int MIN_DROP_SPEED = 200; // Fastest speed
    private static final int SPEED_DECREASE_PER_LEVEL = 50; // Speed increases after each level

    private static final int BRICK_SIZE = 25;
    private static final int PREVIEW_BRICK_SIZE = 20;
    private static final int PREVIEW_GRID_SIZE = 4;
    private static final int GOAL = 5;

    private long fortyLinesBestTime = Long.MAX_VALUE; // for Best time in milliseconds
    private boolean challengeCompleted = false;

    @FXML
    private GridPane gamePanel;

    @FXML
    private Group groupNotification;

    @FXML
    private GridPane brickPanel;

    @FXML
    private Pane brickPaneContainer;

    @FXML
    private GridPane holdPanel;

    @FXML
    private GridPane nextPanel1, nextPanel2, nextPanel3, nextPanel4, nextPanel5;

    @FXML
    private GameOverPanel gameOverPanel;

    @FXML
    private Label piecesLabel, piecesValue, linesLabel, linesValue, timeLabel, timeValue;

    @FXML
    private StackPane countdownPanel;

    @FXML
    private StackPane gameBoardContainer;

    @FXML
    private Label countdownLabel;

    @FXML
    private Label bestTimeLabel;

    private Rectangle[][] displayMatrix;
    private InputEventListener eventListener;
    private Rectangle[][] rectangles;
    private Rectangle[][] holdRectangles;   // For Hold pieces
    private Rectangle[][] nextRectangles1, nextRectangles2, nextRectangles3, nextRectangles4, nextRectangles5;  // For next preview pieces
    private Timeline timeLine;

    private final BooleanProperty isPause = new SimpleBooleanProperty();
    private final BooleanProperty isGameOver = new SimpleBooleanProperty();

    // Time
    private long gameStartTime;
    private AnimationTimer timer;

    private Timeline lockDelayTimeline;
    private boolean isLockDelayActive = false;
    private static final int LOCK_DELAY_MS = 700; // 500ms delay before locking
    private int lockDelayResetCount = 0;  // Track resets
    private static final int MAX_LOCK_RESETS = 15; // Limit resets

    // For Countdown Function
    private Timeline countdownTimeline;
    private boolean isCountdownActive = false;

    private boolean rotateKeyPressed = false;
    private boolean hardDropKeyPressed = false;


    public void setGameMode(GameMode mode) {
        this.currentGameMode = mode;
        challengeCompleted = false;

        // Show/hide best time based on mode
        if (bestTimeLabel != null) {
            if (mode == GameMode.FORTY_LINES) {
                bestTimeLabel.setVisible(true);
                bestTimeLabel.getParent().setVisible(true);
                updateBestTimeDisplay();
            } else {
                bestTimeLabel.setVisible(false);
                if (bestTimeLabel.getParent() != null) {
                    bestTimeLabel.getParent().setVisible(false);
                }
            }
        }

        // Update lines label based on mode
        if (linesLabel != null) {
            if (mode == GameMode.FORTY_LINES) {
                linesLabel.setText("LINES (Goal: )" + GOAL);
            } else {
                linesLabel.setText("LINES");
            }
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        gamePanel.setFocusTraversable(true);
        gamePanel.requestFocus();
        gamePanel.setOnKeyPressed(this::handleKeyPress);
        gamePanel.setOnKeyReleased(this::handleKeyRelease);
        gameOverPanel.setVisible(false);
    }

        public void handleKeyPress(KeyEvent keyEvent) {

            if (isCountdownActive) {
                return;
            }

            if (isPause.getValue() == Boolean.FALSE && isGameOver.getValue() == Boolean.FALSE) {
                if (keyEvent.getCode() == KeyCode.LEFT || keyEvent.getCode() == KeyCode.A) {
                    refreshBrick(eventListener.onLeftEvent(new MoveEvent(EventType.LEFT, EventSource.USER)));
                    if (isLockDelayActive) startLockDelay(); // Reset timer
                    keyEvent.consume();
                }
                if (keyEvent.getCode() == KeyCode.RIGHT || keyEvent.getCode() == KeyCode.D) {
                    refreshBrick(eventListener.onRightEvent(new MoveEvent(EventType.RIGHT, EventSource.USER)));
                    if (isLockDelayActive) startLockDelay(); // Reset timer
                    keyEvent.consume();
                }
                if ((keyEvent.getCode() == KeyCode.UP || keyEvent.getCode() == KeyCode.W) && !rotateKeyPressed) {
                    rotateKeyPressed = true;
                    refreshBrick(eventListener.onRotateEvent(new MoveEvent(EventType.ROTATE, EventSource.USER)));
                    if (isLockDelayActive) startLockDelay(); // Reset timer
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
                if (keyEvent.getCode() == KeyCode.SPACE && !hardDropKeyPressed) {
                    hardDropKeyPressed = true;
                    handleHardDrop();
                    keyEvent.consume();
                }

            }
            if (keyEvent.getCode() == KeyCode.N) {
                restartGameInstantly();
                keyEvent.consume();
            }
        }

    public void handleKeyRelease(KeyEvent keyEvent) {
        // Reset rotation key
        if (keyEvent.getCode() == KeyCode.UP || keyEvent.getCode() == KeyCode.W) {
            rotateKeyPressed = false;
        }

        // Reset hard drop key
        if (keyEvent.getCode() == KeyCode.SPACE) {
            hardDropKeyPressed = false;
        }
    }

    private void handleHold() {
        if (eventListener instanceof GameController) {
            GameController gc = (GameController) eventListener;
            gc.holdBrick();

            updateHoldDisplay();

            // Refresh the game view with new brick
            Board board = gc.getBoard();
            refreshGameBackground(board.getBoardMatrix());
            refreshBrick(board.getViewData());

            updateNextDisplay();
        }
    }

    private void handleHardDrop() {
        if (eventListener instanceof GameController) {
            GameController gc = (GameController) eventListener;
            gc.hardDrop();

            // Update displays after hard drop
            Board board = gc.getBoard();
            refreshGameBackground(board.getBoardMatrix());
            refreshBrick(board.getViewData());
            updateNextDisplay();
            updateStatsDisplay();
        }
    }

    public void initGameView(int[][] boardMatrix, ViewData brick) {

        //Initialize game board
        int height = boardMatrix.length;
        int width = boardMatrix[0].length;

        // Board of Grid lines
        displayMatrix = new Rectangle[height][width];
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                Rectangle rectangle = new Rectangle(BRICK_SIZE, BRICK_SIZE);
                rectangle.setFill(Color.TRANSPARENT);
                rectangle.setStroke(Color.rgb(40, 40, 40));
                rectangle.setStrokeWidth(0.5);
                brickPanel.setHgap(0.5);
                brickPanel.setVgap(0.5);
                displayMatrix[i][j] = rectangle;
                gamePanel.add(rectangle, j, i);     // column, row
            }
        }

        // Initialize current brick display
        rectangles = new Rectangle[brick.getBrickData().length][brick.getBrickData()[0].length];
        for (int i = 0; i < brick.getBrickData().length; i++) {
            for (int j = 0; j < brick.getBrickData()[i].length; j++) {
                Rectangle rectangle = new Rectangle(BRICK_SIZE, BRICK_SIZE);  // Slightly bigger
                rectangle.setFill(getFillColor(brick.getBrickData()[i][j]));
                rectangles[i][j] = rectangle;
                brickPanel.add(rectangle, j, i);
            }
        }

        brickPanel.toFront();  // Force brickPanel to render on top

        // Position relative to Pane container (0,0), not gamePanel
        brickPanel.setLayoutX(brick.getxPosition() * BRICK_SIZE);
        brickPanel.setLayoutY(brick.getyPosition() * BRICK_SIZE);


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

        // Hide brick initially
        brickPanel.setVisible(false);

        // Start game timer
        gameStartTime = System.currentTimeMillis();
        timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                updateTimeDisplay();
            }
        };

        // Set initial drop speed based on mode
        if (currentGameMode == GameMode.NORMAL) {
            currentDropSpeed = baseDropSpeed;
        } else if (currentGameMode == GameMode.FORTY_LINES) {
            currentDropSpeed = 400;
        } else if (currentGameMode == GameMode.TWO_MINUTES) {
            currentDropSpeed = 400;
        }

        // Start countdown when initial game start
        startGameWithCountdown();
    }

    private void initializePreviewPanel(GridPane panel, int size) {
        // Set the GridPane to not grow larger than needed
        panel.setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);

        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                Rectangle rectangle = new Rectangle(size, size);
                rectangle.setFill(Color.TRANSPARENT);
                panel.add(rectangle, j, i);
            }
        }
    }

    private Rectangle[][] createPreviewRectangles(GridPane panel, int size) {
        Rectangle[][] rects = new Rectangle[PREVIEW_GRID_SIZE][PREVIEW_GRID_SIZE];
        for (int i = 0; i <PREVIEW_GRID_SIZE ; i++) {
            for (int j = 0; j < PREVIEW_GRID_SIZE; j++) {
                Rectangle rectangles = new Rectangle(size, size);
                rectangles.setFill(Color.TRANSPARENT);

                // Add stroke for brick block outlines
                rectangles.setStroke(Color.BLACK); // A darker color for better contrast
                rectangles.setStrokeWidth(0.5); // A slightly visible border

                rects[i][j] = rectangles;
                panel.add(rectangles, j, i);
            }
        }
        return rects;
    }

    private void updatePreviewPanel(Rectangle[][] rects, int[][] brickData) {
        // Clear all cells first
        for (int i = 0; i < PREVIEW_GRID_SIZE; i++) {
            for (int j = 0; j < PREVIEW_GRID_SIZE; j++) {
                    rects[i][j].setFill(Color.TRANSPARENT);

            }
        }

        // Find the bounds of the actual brick shape
        int minRow = PREVIEW_GRID_SIZE, maxRow = -1, minCol = PREVIEW_GRID_SIZE, maxCol = -1;
        for (int i = 0; i < brickData.length; i++) {
            for (int j = 0; j < brickData[i].length; j++) {
                if (brickData[i][j] != 0) {
                    minRow = Math.min(minRow, i);
                    maxRow = Math.max(maxRow, i);
                    minCol = Math.min(minCol, j);
                    maxCol = Math.max(maxCol, j);
                }
            }
        }

        // Calculate centering offset
        int brickHeight = maxRow - minRow + 1;
        int brickWidth = maxCol - minCol + 1;
        int offsetRow = (4 - brickHeight) / 2;
        int offsetCol = (4 - brickWidth) / 2;

        // Draw brick centered
        for (int i = 0; i < brickData.length; i++) {
            for (int j = 0; j < brickData[i].length; j++) {
                if (brickData[i][j] != 0) {
                    int targetRow = offsetRow + (i - minRow);
                    int targetCol = offsetCol + (j - minCol);
                    if (targetRow >= 0 && targetRow < 4 && targetCol >= 0 && targetCol < 4) {
                        rects[targetRow][targetCol].setFill(getFillColor(brickData[i][j]));
                    }
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
            // 1. Clear old brick from gamePanel
            for (Rectangle[] row : rectangles) {
                for (Rectangle r : row) {
                    gamePanel.getChildren().remove(r);
                }
            }

            // 2. Draw new brick at correct grid position
            for (int i = 0; i < brick.getBrickData().length; i++) {
                for (int j = 0; j < brick.getBrickData()[i].length; j++) {
                    if (brick.getBrickData()[i][j] != 0) {
                        int gridX = brick.getxPosition() + j;
                        int gridY = brick.getyPosition() + i;

                        // Draw the brick cell
                        rectangles[i][j].setFill(getFillColor(brick.getBrickData()[i][j]));
                        rectangles[i][j].setStroke(Color.BLACK); // A darker color for better contrast
                        rectangles[i][j].setStrokeWidth(0.5); // A slightly visible border

                        // Add to gamePanel at exact grid position
                        gamePanel.add(rectangles[i][j], gridX, gridY);
                    } else {
                        rectangles[i][j].setFill(Color.TRANSPARENT);
                    }
                }
            }

            updateShadow(brick);
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
            GameController gc = (GameController) eventListener;
            Board board = gc.getBoard();

            // Check if brick can move down
            boolean canMove = board.moveBrickDown();

            if (!canMove) {
                // Brick hit the ground - start lock delay
                if (!isLockDelayActive) {
                    isLockDelayActive = true;
                    lockDelayResetCount = 0;
                    startLockDelay();
                }
                refreshBrick(board.getViewData());
            } else {
                // Brick moved successfully - cancel lock delay
                cancelLockDelay();

                // Add score for user movement
                if (event.getEventSource() == EventSource.USER) {
                    board.getScore().add(1);
                }

                refreshBrick(board.getViewData());
            }
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
            int linesCleared = gc.getLinesCleared();

            piecesValue.setText(String.valueOf(gc.getPiecesPlaced()));
            linesValue.setText(String.valueOf(linesCleared));

            // Check if 40 lines challenge is complete
            if (currentGameMode == GameMode.FORTY_LINES &&
                    linesCleared >= GOAL &&
                    !challengeCompleted) {
                challengeCompleted = true;
                completeFortyLinesChallenge();
            }
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

    private void updateBestTimeDisplay() {
        if (fortyLinesBestTime == Long.MAX_VALUE) {
            bestTimeLabel.setText("--:--");
        } else {
            int minutes = (int) (fortyLinesBestTime / 60000);
            int seconds = (int) ((fortyLinesBestTime % 60000) / 1000);
            int millis = (int) (fortyLinesBestTime % 1000);
            bestTimeLabel.setText(String.format("%d:%02d.%03d", minutes, seconds, millis));
        }
    }

    private void completeFortyLinesChallenge() {
        // Stop the game
        if (timeLine != null) timeLine.stop();
        if (timer != null) timer.stop();

        // Calculate final time
        long finalTime = System.currentTimeMillis() - gameStartTime;

        // Check if it's a new best time
        boolean isNewBest = finalTime < fortyLinesBestTime;
        if (isNewBest) {
            fortyLinesBestTime = finalTime;
            updateBestTimeDisplay();
        }

        // Format the time
        int minutes = (int) (finalTime / 60000);
        int seconds = (int) ((finalTime % 60000) / 1000);
        int millis = (int) (finalTime % 1000);
        String timeString = String.format("%d:%02d.%03d", minutes, seconds, millis);

        // Show completion message
        showCompletionMessage(timeString, isNewBest);
    }

    private void showCompletionMessage(String timeString, boolean isNewBest) {
        // Format previous best time
        String previousBest = null;
        if (fortyLinesBestTime != Long.MAX_VALUE && !isNewBest) {
            int minutes = (int) (fortyLinesBestTime / 60000);
            int seconds = (int) ((fortyLinesBestTime % 60000) / 1000);
            int millis = (int) (fortyLinesBestTime % 1000);
            previousBest = String.format("%d:%02d.%03d", minutes, seconds, millis);
        }

        // Show completion panel
        CompletionPanel panel = new CompletionPanel(timeString, isNewBest, previousBest);

        panel.setOnRetry(() -> {
            // Find and remove the panel
            javafx.scene.Parent parent = gamePanel.getParent();
            if (parent instanceof javafx.scene.layout.Pane) {
                ((javafx.scene.layout.Pane) parent).getChildren().remove(panel);
            }
            restartGameInstantly();
        });

        panel.setOnMainMenu(() -> {
            try {
                javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(getClass().getResource("/MainMenu.fxml"));
                javafx.scene.Parent menuRoot = loader.load();
                javafx.stage.Stage stage = (javafx.stage.Stage) gamePanel.getScene().getWindow();
                javafx.scene.Scene menuScene = new javafx.scene.Scene(menuRoot, 900, 700);
                stage.setScene(menuScene);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        javafx.scene.Parent parent = gamePanel.getParent();
        if (parent instanceof javafx.scene.layout.Pane) {
            ((javafx.scene.layout.Pane) parent).getChildren().add(panel);
        }

    }

    private void updateShadow(ViewData brick) {
        if (!(eventListener instanceof GameController)) {
            return;
        }

        GameController gc = (GameController) eventListener;
        Board board = gc.getBoard();
        int[][] boardMatrix = board.getBoardMatrix();

        // First, refresh the background to clear old shadow
        refreshGameBackground(boardMatrix);

        // Calculate where the brick would land
        int shadowY = calculateShadowPosition(brick);
        int shadowX = brick.getxPosition();
        int[][] brickData = brick.getBrickData();

        // Draw shadow directly on the board grid (displayMatrix)
        for (int i = 0; i < brickData.length; i++) {
            for (int j = 0; j < brickData[i].length; j++) {
                if (brickData[i][j] != 0) {
                    int boardRow = shadowY + i;
                    int boardCol = shadowX + j;

                    // Make sure it's within bounds
                    if (boardRow >= 0 && boardRow < displayMatrix.length &&
                            boardCol >= 0 && boardCol < displayMatrix[0].length) {

                        // Only draw shadow if the cell is empty
                        if (boardMatrix[boardRow][boardCol] == 0) {
                            // Draw shadow with semi-transparent color
                            Color shadowColor = Color.rgb(128, 128, 128, 0.3);
                            displayMatrix[boardRow][boardCol].setFill(shadowColor);
                        }
                    }
                }
            }
        }
    }

    private void updateGameSpeed() {
        if (currentGameMode == GameMode.NORMAL && eventListener instanceof GameController) {
            GameController gc = (GameController) eventListener;
            int linesCleared = gc.getLinesCleared();

            // Increase speed every 10 lines
            int level = linesCleared / 10;
            currentDropSpeed = Math.max(MIN_DROP_SPEED, baseDropSpeed - (level * SPEED_DECREASE_PER_LEVEL));

            // Update the timeline with new speed
            if (timeLine != null) {
                timeLine.stop();
                timeLine = new Timeline(new KeyFrame(
                        Duration.millis(currentDropSpeed),
                        ae -> moveDown(new MoveEvent(EventType.DOWN, EventSource.THREAD))
                ));
                timeLine.setCycleCount(Timeline.INDEFINITE);
                timeLine.play();
            }

            System.out.println("Speed updated! Level: " + level + ", Speed: " + currentDropSpeed + "ms");
        }
    }

    private int calculateShadowPosition(ViewData brick) {
        // Get current board state
        if (!(eventListener instanceof GameController)) {
            return brick.getyPosition();
        }

        GameController gc = (GameController) eventListener;
        Board board = gc.getBoard();
        int[][] boardMatrix = board.getBoardMatrix();
        int[][] brickShape = brick.getBrickData();

        int currentX = brick.getxPosition();
        int currentY = brick.getyPosition();

        // Start from current position and drop down
        int dropY = currentY;

        // Keep moving down while there's no collision
        while (!checkCollision(boardMatrix, brickShape, currentX, dropY + 1)) {
            dropY++;
        }

        // If we got here, return the last valid position
        return dropY;
    }

    private boolean checkCollision(int[][] board, int[][] brick, int x, int y) {
        for (int i = 0; i < brick.length; i++) {
            for (int j = 0; j < brick[i].length; j++) {
                if (brick[i][j] != 0) {  // Only check filled cells
                    int boardRow = y + i;
                    int boardCol = x + j;

                    // Check bottom boundary
                    if (boardRow >= board.length) {
                        return true;
                    }

                    // Check side boundaries
                    if (boardCol < 0 || boardCol >= board[0].length) {
                        return true;
                    }

                    // Check collision with placed blocks
                    if (boardRow >= 0 && board[boardRow][boardCol] != 0) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private void startLockDelay() {

        // Cancel any existing lock delay timer
        if (lockDelayTimeline != null) {
            lockDelayTimeline.stop();
        }

        // Create new lock delay timer
        lockDelayTimeline = new Timeline(new KeyFrame(
                Duration.millis(LOCK_DELAY_MS),
                ae -> executeLock()
        ));
        lockDelayTimeline.setCycleCount(1);
        lockDelayTimeline.play();
    }

    private void resetLockDelay() {
        // Limit the number of resets (prevents infinite delay)
        if (lockDelayResetCount < MAX_LOCK_RESETS) {
            lockDelayResetCount++;
            startLockDelay();
        } else {
            // Force lock if too many resets
            executeLock();
        }
    }

    private void cancelLockDelay() {
        if (lockDelayTimeline != null) {
            lockDelayTimeline.stop();
        }
        isLockDelayActive = false;
    }

    private void executeLock() {
        if (eventListener instanceof GameController) {
            GameController gc = (GameController) eventListener;
            Board board = gc.getBoard();

            // Lock the brick in place
            board.mergeBrickToBackground();
            ClearRow clearRow = board.clearRows();

            if (clearRow != null && clearRow.getLinesRemoved() > 0) {
                board.getScore().add(clearRow.getScoreBonus());

                NotificationPanel notificationPanel = new NotificationPanel(
                        "+" + clearRow.getScoreBonus()
                );
                groupNotification.getChildren().add(notificationPanel);
                notificationPanel.showScore(groupNotification.getChildren());

                updateStatsDisplay();
                updateGameSpeed();
            }

            // Create new brick
            if (board.createNewBrick()) {
                gameOver();
            }

            refreshGameBackground(board.getBoardMatrix());
            refreshBrick(board.getViewData());
            updateNextDisplay();

            isLockDelayActive = false;
            lockDelayResetCount = 0;
        }
    }

    private void showCountdown(Runnable onComplete) {
        isCountdownActive = true;
        countdownPanel.setVisible(true);
        brickPanel.setVisible(false);       //Hides the brick during countdown

        final int[] count = {3};  // Start at 3

        countdownTimeline = new Timeline(new KeyFrame(Duration.seconds(1), event -> {
            if (count[0] > 0) {
                countdownLabel.setText(String.valueOf(count[0]));
                count[0]--;
            } else {
                countdownLabel.setText("GO!");
                countdownTimeline.stop();

                // Hide after showing GO!
                Timeline hideTimeline = new Timeline(new KeyFrame(Duration.millis(500), e -> {
                    countdownPanel.setVisible(false);
                    brickPanel.setVisible(true);  // Show the brick when game starts
                    isCountdownActive = false;
                    onComplete.run();  // Start the game
                }));
                hideTimeline.play();
            }
        }));

        countdownTimeline.setCycleCount(4);  // 3, 2, 1, GO!
        countdownTimeline.play();
    }

    private void restartGameWithCountdown() {
        // Stop everything
        if (timeLine != null) timeLine.stop();
        if (timer != null) timer.stop();
        if (countdownTimeline != null) countdownTimeline.stop();
        cancelLockDelay();

        gameOverPanel.setVisible(false);
        countdownPanel.setVisible(false);
        brickPanel.setVisible(false);

        // Clear the entire board immediately
        eventListener.createNewGame();

        // Force immediate refresh of background to clear all blocks
        if (eventListener instanceof GameController) {
            GameController gc = (GameController) eventListener;
            Board board = gc.getBoard();
            refreshGameBackground(board.getBoardMatrix());
        }

        gamePanel.requestFocus();

        // Reset ALL displays immediately
        gameStartTime = System.currentTimeMillis();
        piecesValue.setText("0");
        linesValue.setText("0");
        timeValue.setText("0:00.000");

        // Clear hold and next displays
        updateHoldDisplay();
        updateNextDisplay();

        isPause.setValue(Boolean.FALSE);
        isGameOver.setValue(Boolean.FALSE);

        // Reset speed for normal mode
        if (currentGameMode == GameMode.NORMAL) {
            currentDropSpeed = baseDropSpeed;
        } else if (currentGameMode == GameMode.FORTY_LINES) {
            currentDropSpeed = 400;
        } else if (currentGameMode == GameMode.TWO_MINUTES) {
            currentDropSpeed = 400;
        }

        // Show countdown before starting
        showCountdown(() -> {
            if (timer != null) timer.start();

            timeLine = new Timeline(new KeyFrame(
                    Duration.millis(currentDropSpeed),
                    ae -> moveDown(new MoveEvent(EventType.DOWN, EventSource.THREAD))
            ));
            timeLine.setCycleCount(Timeline.INDEFINITE);
            timeLine.play();
        });
    }

    private void startGameWithCountdown() {
        // This is ONLY called when starting from main menu
        brickPanel.setVisible(false);  // Hide brick during countdown

        // Show countdown before starting
        showCountdown(() -> {

            // start the timer (after countdown)
            if (timer != null) timer.start();

            gameStartTime = System.currentTimeMillis();

            timeLine = new Timeline(new KeyFrame(
                    Duration.millis(currentDropSpeed),
                    ae -> moveDown(new MoveEvent(EventType.DOWN, EventSource.THREAD))
            ));
            timeLine.setCycleCount(Timeline.INDEFINITE);
            timeLine.play();
        });
    }

    private void restartGameInstantly() {
        // Stop everything
        if (timeLine != null) timeLine.stop();
        if (timer != null) timer.stop();
        if (countdownTimeline != null) countdownTimeline.stop();
        cancelLockDelay();

        challengeCompleted = false;

        gameOverPanel.setVisible(false);
        countdownPanel.setVisible(false);
        brickPanel.setVisible(true);  // Keep brick visible - no countdown

        // Clear the entire board immediately
        eventListener.createNewGame();

        // Force immediate refresh of background to clear all blocks
        if (eventListener instanceof GameController) {
            GameController gc = (GameController) eventListener;
            Board board = gc.getBoard();
            refreshGameBackground(board.getBoardMatrix());
            refreshBrick(board.getViewData());  // Show new brick immediately
        }

        gamePanel.requestFocus();

        // Reset ALL displays immediately
        gameStartTime = System.currentTimeMillis();
        piecesValue.setText("0");
        linesValue.setText("0");
        timeValue.setText("0:00.000");

        // Clear hold and next displays
        updateHoldDisplay();
        updateNextDisplay();

        isPause.setValue(Boolean.FALSE);
        isGameOver.setValue(Boolean.FALSE);

        // Reset speed for normal mode
        if (currentGameMode == GameMode.NORMAL) {
            currentDropSpeed = baseDropSpeed;
        } else if (currentGameMode == GameMode.FORTY_LINES) {
            currentDropSpeed = 400;
        } else if (currentGameMode == GameMode.TWO_MINUTES) {
            currentDropSpeed = 400;
        }

        // Start immediately - NO countdown
        if (timer != null) timer.start();

        timeLine = new Timeline(new KeyFrame(
                Duration.millis(currentDropSpeed),
                ae -> moveDown(new MoveEvent(EventType.DOWN, EventSource.THREAD))
        ));
        timeLine.setCycleCount(Timeline.INDEFINITE);
        timeLine.play();
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
        cancelLockDelay();
        gameOverPanel.setVisible(true);
        isGameOver.setValue(Boolean.TRUE);
    }

    public void newGame(ActionEvent actionEvent) {
        restartGameInstantly();
    }

    public void pauseGame(ActionEvent actionEvent) {
        gamePanel.requestFocus();
    }
}
