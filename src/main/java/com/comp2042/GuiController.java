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
import javafx.scene.layout.*;
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
    private static final int TIME = 30000;     // 2 minutes = 120,000ms

    private long fortyLinesBestTime = Long.MAX_VALUE; // for Best time in milliseconds
    private boolean challengeCompleted = false;

    // For Normal Mode - NEW scoring system
    private int normalModeScore = 0;
    private int normalModeCombo = 0;
    private boolean normalModeLastWasTetris = false;

    // For 2-Minute Challenge
    private int currentScore = 0;
    private static int twoMinutesBestScore = 0;
    private int currentCombo = 0;
    private boolean lastClearWasTetris = false;

    @FXML
    private Label scoreLabel;

    @FXML
    private Label scoreValue;

    @FXML
    private Label bestScoreLabel;

    @FXML
    private Label bestScoreValue;

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

    @FXML
    private StackPane countdownPanel;


    @FXML
    private Label countdownLabel;

    @FXML
    private Label bestTimeLabel;

    @FXML
    private PauseMenuPanel pauseMenuPanel;

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
    private static final int MAX_LOCK_RESETS = 10; // Limit resets

    // For Countdown Function
    private Timeline countdownTimeline;
    private boolean isCountdownActive = false;

    private boolean rotateKeyPressed = false;
    private boolean hardDropKeyPressed = false;

    private TwoMinutesCompletionPanel currentCompletionPanel;
    private CompletionPanel currentFortyLinesPanel;

    public void setGameMode(GameMode mode) {
        this.currentGameMode = mode;
        challengeCompleted = false;

        // Reset score tracking
        if (mode == GameMode.NORMAL) {
            normalModeScore = 0;
            normalModeCombo = 0;
            normalModeLastWasTetris = false;
        } else if (mode == GameMode.TWO_MINUTES) {
            currentScore = 0;
            currentCombo = 0;
            lastClearWasTetris = false;
        }

        // Show/hide best time based on mode
        if (scoreLabel != null && scoreValue != null) {
            if (mode == GameMode.NORMAL || mode == GameMode.TWO_MINUTES) {
                // Show score for both Normal and 2-Minute modes
                scoreLabel.setVisible(true);
                scoreValue.setVisible(true);

                // Get the MAIN parent VBox that contains everything
                javafx.scene.Parent mainParent = scoreLabel.getParent();
                if (mainParent != null && mainParent.getParent() != null) {
                    mainParent.getParent().setVisible(true); // Make the outer VBox visible
                }

                scoreValue.setText("0");

                // Show best score ONLY for 2-minute mode, hide for Normal mode
                if (bestScoreLabel != null && bestScoreValue != null) {
                    if (mode == GameMode.TWO_MINUTES) {
                        // Two Minutes Mode: Show both score and best score
                        bestScoreLabel.setVisible(true);
                        bestScoreValue.setVisible(true);
                        updateBestScoreDisplay();
                    } else {
                        // Normal Mode: Show score but hide best score and separator
                        bestScoreLabel.setVisible(false);
                        bestScoreValue.setVisible(false);

                        // Also hide the separator line between score and best score
                        if (bestScoreLabel.getParent() != null && bestScoreLabel.getParent() instanceof javafx.scene.layout.VBox) {
                            javafx.scene.layout.VBox vbox = (javafx.scene.layout.VBox) bestScoreLabel.getParent();
                            // Hide separator (it's the 3rd child - index 2)
                            if (vbox.getChildren().size() > 2) {
                                vbox.getChildren().get(2).setVisible(false); // Hide separator Region
                            }
                        }
                    }
                }

            } else {
                // 40-Lines Mode: Hide entire score section
                scoreLabel.setVisible(false);
                scoreValue.setVisible(false);

                // Hide the MAIN parent VBox
                javafx.scene.Parent mainParent = scoreLabel.getParent();
                if (mainParent != null && mainParent.getParent() != null) {
                    mainParent.getParent().setVisible(false);
                }

                // Hide best score for other modes
                if (bestScoreLabel != null && bestScoreValue != null) {
                    bestScoreLabel.setVisible(false);
                    bestScoreValue.setVisible(false);
                }
            }
        }

        // Update lines label for 40-lines challenge mode
        if (linesLabel != null) {
            if (mode == GameMode.FORTY_LINES) {
                linesLabel.setText("LINES (Goal: " + GOAL + ")");
            } else {
                linesLabel.setText("LINES");
            }
        }

        // Update time label for 2-minute challenge mode
        if (timeLabel != null) {
            if (mode == GameMode.TWO_MINUTES) {
                timeLabel.setText("TIME LEFT");
            } else {
                timeLabel.setText("TIME");
            }
        }

        // Update lines label for 40-lines challenge mode
        if (linesLabel != null) {
            if (mode == GameMode.FORTY_LINES) {
                linesLabel.setText("LINES (Goal: " + GOAL + ")");
            } else {
                linesLabel.setText("LINES");
            }
        }

        // Update time label for 2-minute challenge mode
        if (timeLabel != null) {
            if (mode == GameMode.TWO_MINUTES) {
                timeLabel.setText("TIME LEFT");
            } else {
                timeLabel.setText("TIME");
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

        // --- NEW CODE START ---
        if (pauseMenuPanel != null) {
            pauseMenuPanel.setVisible(false);

            // Define what happens when Resume is clicked
            pauseMenuPanel.setOnResume(() -> {
                togglePause();
            });

            // Define what happens when Main Menu is clicked
            pauseMenuPanel.setOnMainMenu(() -> {
                try {
                    // Load Main Menu Scene
                    javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(getClass().getResource("/MainMenu.fxml"));
                    javafx.scene.Parent menuRoot = loader.load();
                    javafx.stage.Stage stage = (javafx.stage.Stage) gamePanel.getScene().getWindow();
                    javafx.scene.Scene menuScene = new javafx.scene.Scene(menuRoot, 900, 700);
                    stage.setScene(menuScene);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            });
        }
    }

    public void handleKeyPress(KeyEvent keyEvent) {

        if (isCountdownActive) {
            return;
        }

        if (isPause.getValue() == Boolean.FALSE && isGameOver.getValue() == Boolean.FALSE) {
            if (keyEvent.getCode() == KeyCode.LEFT || keyEvent.getCode() == KeyCode.A) {
                boolean moved = moveBrickHorizontally(-1);
                if (moved && isLockDelayActive) {
                    resetLockDelay();
                }
                keyEvent.consume();
            }
            if (keyEvent.getCode() == KeyCode.RIGHT || keyEvent.getCode() == KeyCode.D) {
                boolean moved = moveBrickHorizontally(1);
                if (moved && isLockDelayActive) {
                    resetLockDelay();
                }
                keyEvent.consume();
            }
            if ((keyEvent.getCode() == KeyCode.UP || keyEvent.getCode() == KeyCode.W) && !rotateKeyPressed) {
                rotateKeyPressed = true;
                boolean rotated = attemptRotation();
                if (rotated && isLockDelayActive) {
                    resetLockDelay();
                }
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
            if (challengeCompleted) {
                removeCompletionPanel();
                restartGameWithCountdown();
            } else {
                restartGameInstantly();
            }
            keyEvent.consume();
        }

        if (keyEvent.getCode() == KeyCode.P || keyEvent.getCode() == KeyCode.ESCAPE) {
            togglePause(); // Call the new method
            keyEvent.consume();
        }

    }

    private boolean moveBrickHorizontally(int direction) {
        if (!(eventListener instanceof GameController)) {
            return false;
        }

        GameController gc = (GameController) eventListener;
        Board board = gc.getBoard();

        boolean moved;
        if (direction < 0) {
            moved = board.moveBrickLeft();
        } else {
            moved = board.moveBrickRight();
        }

        if (moved) {
            refreshBrick(board.getViewData());
            return true;
        }
        return false;
    }

    private boolean attemptRotation() {
        if (!(eventListener instanceof GameController)) {
            return false;
        }

        GameController gc = (GameController) eventListener;
        Board board = gc.getBoard();

        // Simply attempt rotation without moving the brick
        boolean rotated = board.rotateLeftBrick();

        if (rotated) {
            // Rotation succeeded - just refresh display
            refreshBrick(board.getViewData());

            // Only reset lock delay if currently active
            // This prevents speed issues
            if (isLockDelayActive) {
                resetLockDelay();
            }
            return true;
        } else {
            // Rotation failed - brick would collide
            return false;
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
            cancelLockDelay();

            GameController gc = (GameController) eventListener;
            Board board = gc.getBoard();

            // Calculate drop distance for bonus points
            ViewData currentBrickData = board.getViewData();
            int currentY = currentBrickData.getyPosition();
            int shadowY = calculateShadowPosition(currentBrickData);
            int dropDistance = shadowY - currentY;

            // Drop brick to bottom instantly
            while (board.moveBrickDown()) {
                // Keep moving down until it can't move anymore
            }

            // Add hard drop bonus BEFORE locking (2 points per cell)
            if (currentGameMode == GameMode.NORMAL && dropDistance > 0) {
                int hardDropBonus = dropDistance * 2;
                normalModeScore += hardDropBonus;
                if (scoreValue != null) {
                    scoreValue.setText(String.valueOf(normalModeScore));
                }
            } else if (currentGameMode == GameMode.TWO_MINUTES && dropDistance > 0) {
                int hardDropBonus = dropDistance * 2;
                currentScore += hardDropBonus;
                if (scoreValue != null) {
                    scoreValue.setText(String.valueOf(currentScore));
                }
            }

            // Lock the brick
            board.mergeBrickToBackground();
            ClearRow clearRow = board.clearRows();

            // Handle line clear scoring
            if (clearRow != null && clearRow.getLinesRemoved() > 0) {
                int linesCleared = clearRow.getLinesRemoved();

                if (currentGameMode == GameMode.NORMAL) {
                    // Use Tetris scoring for Normal mode
                    int earnedScore = calculateTetrisScore(linesCleared, true);
                    normalModeScore += earnedScore;
                    if (scoreValue != null) {
                        scoreValue.setText(String.valueOf(normalModeScore));
                    }

                } else if (currentGameMode == GameMode.TWO_MINUTES) {
                    // Use Tetris scoring for 2-minute mode
                    int earnedScore = calculateTetrisScore(linesCleared, false);
                    currentScore += earnedScore;
                    if (scoreValue != null) {
                        scoreValue.setText(String.valueOf(currentScore));
                    }
                }

                updateStatsDisplay();
                updateGameSpeed();

            } else {
                // No lines cleared - reset combo
                if (currentGameMode == GameMode.NORMAL) {
                    normalModeCombo = 0;
                    normalModeLastWasTetris = false;
                } else if (currentGameMode == GameMode.TWO_MINUTES) {
                    currentCombo = 0;
                    lastClearWasTetris = false;
                }
            }

            // Reset lock delay BEFORE spawning new brick
            isLockDelayActive = false;
            lockDelayResetCount = 0;

            // Spawn next brick
            if (board.createNewBrick()) {
                gameOver();
            }

            // Update displays
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
        for (int i = 0; i < PREVIEW_GRID_SIZE; i++) {
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
            case 0:
                return Color.TRANSPARENT;
            case 1:
                return Color.CYAN;
            case 2:
                return Color.BLUE;
            case 3:
                return Color.ORANGE;
            case 4:
                return Color.YELLOW;
            case 5:
                return Color.GREEN;
            case 6:
                return Color.PURPLE;
            case 7:
                return Color.RED;
            default:
                return Color.WHITE;
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

            } else {
                // Brick moved successfully - cancel lock delay
                if (isLockDelayActive) {
                    cancelLockDelay();
                }

                // Soft drop bonus (1 point per cell)
                if (event.getEventSource() == EventSource.USER) {
                    if (currentGameMode == GameMode.NORMAL) {
                        normalModeScore += 1;
                        if (scoreValue != null) {
                            scoreValue.setText(String.valueOf(normalModeScore));
                        }
                    } else if (currentGameMode == GameMode.TWO_MINUTES) {
                        currentScore += 1;
                        if (scoreValue != null) {
                            scoreValue.setText(String.valueOf(currentScore));
                        }
                    }
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

            // Show countdown from 2:00 to 0:00
            if (currentGameMode == GameMode.TWO_MINUTES) {
                long remaining = TIME - elapsed;
                if (remaining < 0) remaining = 0;

                int minutes = (int) (remaining / 60000);
                int seconds = (int) ((remaining % 60000) / 1000);
                int millis = (int) (remaining % 1000);
                timeValue.setText(String.format("%d:%02d.%03d", minutes, seconds, millis));

                checkTwoMinutesComplete();

            } else {
                int minutes = (int) (elapsed / 60000);
                int seconds = (int) ((elapsed % 60000) / 1000);
                int millis = (int) (elapsed % 1000);
                timeValue.setText(String.format("%d:%02d.%03d", minutes, seconds, millis));
            }
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

    private void updateBestScoreDisplay() {
        if (bestScoreValue != null) {
            if (twoMinutesBestScore > 0) {
                bestScoreValue.setText(String.valueOf(twoMinutesBestScore));
            } else {
                bestScoreValue.setText("--");
            }
        }
    }

    private void completeFortyLinesChallenge() {
        // Stop the game
        if (timeLine != null) timeLine.stop();
        if (timer != null) timer.stop();

        // Calculate final time
        long finalTime = System.currentTimeMillis() - gameStartTime;

        // Check if it's a new best time
        boolean NewBest = finalTime < fortyLinesBestTime;
        if (NewBest) {
            fortyLinesBestTime = finalTime;
            updateBestTimeDisplay();
        }

        // Format the time
        int minutes = (int) (finalTime / 60000);
        int seconds = (int) ((finalTime % 60000) / 1000);
        int millis = (int) (finalTime % 1000);
        String timeString = String.format("%d:%02d.%03d", minutes, seconds, millis);

        // Show completion message
        showCompletionMessage(timeString, NewBest);
    }

    private void showCompletionMessage(String timeString, boolean NewBest) {
        // Format previous best time
        String previousBest = null;

        if (fortyLinesBestTime != Long.MAX_VALUE && !NewBest) {
            int minutes = (int) (fortyLinesBestTime / 60000);
            int seconds = (int) ((fortyLinesBestTime % 60000) / 1000);
            int millis = (int) (fortyLinesBestTime % 1000);
            previousBest = String.format("%d:%02d.%03d", minutes, seconds, millis);
        }

        // Show completion panel
        CompletionPanel panel = new CompletionPanel(timeString, NewBest, previousBest);

        // Store reference to current panel
        currentFortyLinesPanel = panel;

        panel.setOnRetry(() -> {
            removeCompletionPanel();
            restartGameWithCountdown();
        });

        panel.setOnMainMenu(() -> {
            try {
                // Best score persists because  is static
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

    private void removeFortyLinesPanel() {
        if (currentFortyLinesPanel != null) {
            javafx.scene.Parent parent = gamePanel.getParent();
            if (parent instanceof javafx.scene.layout.Pane) {
                ((javafx.scene.layout.Pane) parent).getChildren().remove(currentFortyLinesPanel);
            }
            currentFortyLinesPanel = null;
        }
    }

    private void updateShadow(ViewData brick) {
        if (!(eventListener instanceof GameController)) {
            return;
        }

        GameController gc = (GameController) eventListener;
        Board board = gc.getBoard();
        int[][] boardMatrix = board.getBoardMatrix();

        //Refresh the background to clear old shadow
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

            // Cancel the current timer
            if (lockDelayTimeline != null) {
                lockDelayTimeline.stop();
            }

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
        lockDelayResetCount = 0;
    }

    private void executeLock() {
        if (eventListener instanceof GameController) {
            GameController gc = (GameController) eventListener;
            Board board = gc.getBoard();

            // Lock the brick in place
            board.mergeBrickToBackground();
            ClearRow clearRow = board.clearRows();

            if (clearRow != null && clearRow.getLinesRemoved() > 0) {
                int linesCleared = clearRow.getLinesRemoved();

                if (currentGameMode == GameMode.NORMAL) {
                    // Use Tetris scoring for Normal mode
                    int earnedScore = calculateTetrisScore(linesCleared, true);
                    normalModeScore += earnedScore;
                    if (scoreValue != null) {
                        scoreValue.setText(String.valueOf(normalModeScore));
                    }

                } else if (currentGameMode == GameMode.TWO_MINUTES) {
                    // Use Tetris scoring for 2-minute mode
                    int earnedScore = calculateTetrisScore(linesCleared, false);
                    currentScore += earnedScore;
                    if (scoreValue != null) {
                        scoreValue.setText(String.valueOf(currentScore));
                    }
                }

                updateStatsDisplay();
                updateGameSpeed();

            } else {
                // No lines cleared - reset combo
                if (currentGameMode == GameMode.NORMAL) {
                    normalModeCombo = 0;
                    normalModeLastWasTetris = false;
                } else if (currentGameMode == GameMode.TWO_MINUTES) {
                    currentCombo = 0;
                    lastClearWasTetris = false;
                }
            }

            //Reset lock delay state BEFORE creating new brick
            isLockDelayActive = false;
            lockDelayResetCount = 0;
            if (lockDelayTimeline != null) {
                lockDelayTimeline.stop();
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

        removeCompletionPanel();

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

        challengeCompleted = false;

        // Reset score tracking
        if (currentGameMode == GameMode.NORMAL) {
            normalModeScore = 0;
            normalModeCombo = 0;
            normalModeLastWasTetris = false;
            if (scoreValue != null) {
                scoreValue.setText("0");
            }
        } else if (currentGameMode == GameMode.TWO_MINUTES) {
            currentScore = 0;
            currentCombo = 0;
            lastClearWasTetris = false;
            if (scoreValue != null) {
                scoreValue.setText("0");
            }
        }


        gameOverPanel.setVisible(false);
        countdownPanel.setVisible(false);
        brickPanel.setVisible(false);  // Hide brick during countdown

        // Clear the board
        eventListener.createNewGame();

        // Force refresh
        if (eventListener instanceof GameController) {
            GameController gc = (GameController) eventListener;
            Board board = gc.getBoard();
            board.newGame();    // Resets board
            refreshGameBackground(board.getBoardMatrix());

            clearBrickDisplay();    // Clear brick display
        }

        gamePanel.requestFocus();

        // Reset displays
        gameStartTime = System.currentTimeMillis();
        piecesValue.setText("0");
        linesValue.setText("0");

        // Set initial time display based on mode
        if (currentGameMode == GameMode.TWO_MINUTES) {
            timeValue.setText("2:00.000");
        } else {
            timeValue.setText("0:00.000");
        }

        // Clear hold and next displays
        updateHoldDisplay();
        updateNextDisplay();

        isPause.setValue(Boolean.FALSE);
        isGameOver.setValue(Boolean.FALSE);

        // Reset speed
        if (currentGameMode == GameMode.NORMAL) {
            currentDropSpeed = baseDropSpeed;
        } else if (currentGameMode == GameMode.FORTY_LINES) {
            currentDropSpeed = 400;
        } else if (currentGameMode == GameMode.TWO_MINUTES) {
            currentDropSpeed = 400;
        }

        // Show countdown before starting
        showCountdown(() -> {
            // Create the first brick AFTER countdown
            if (eventListener instanceof GameController) {
                GameController gc = (GameController) eventListener;
                Board board = gc.getBoard();
                board.createNewBrick();
                refreshBrick(board.getViewData());
                updateNextDisplay();
            }

            if (timer != null) timer.start();

            timeLine = new Timeline(new KeyFrame(
                    Duration.millis(currentDropSpeed),
                    ae -> moveDown(new MoveEvent(EventType.DOWN, EventSource.THREAD))
            ));
            timeLine.setCycleCount(Timeline.INDEFINITE);
            timeLine.play();
        });

        pauseMenuPanel.setVisible(false);
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
        // Stop everything COMPLETELY
        if (timeLine != null) {
            timeLine.stop();
            timeLine = null; // Clear reference
        }
        if (timer != null) timer.stop();
        if (countdownTimeline != null) countdownTimeline.stop();
        cancelLockDelay();

        challengeCompleted = false;
        removeCompletionPanel();

        // Reset score tracking
        if (currentGameMode == GameMode.NORMAL) {
            normalModeScore = 0;
            normalModeCombo = 0;
            normalModeLastWasTetris = false;
            if (scoreValue != null) {
                scoreValue.setText("0");
            }
        } else if (currentGameMode == GameMode.TWO_MINUTES) {
            currentScore = 0;
            currentCombo = 0;
            lastClearWasTetris = false;
            if (scoreValue != null) {
                scoreValue.setText("0");
            }
        }

        gameOverPanel.setVisible(false);
        countdownPanel.setVisible(false);
        brickPanel.setVisible(true);

        // Clear old brick display
        clearBrickDisplay();

        // Create new game
        eventListener.createNewGame();

        // Create and display new brick
        if (eventListener instanceof GameController) {
            GameController gc = (GameController) eventListener;
            Board board = gc.getBoard();

            refreshGameBackground(board.getBoardMatrix());

            boolean gameOverOnSpawn = board.createNewBrick();
            if (gameOverOnSpawn) {
                gameOver();
                return;
            }

            refreshBrick(board.getViewData());
        }

        gamePanel.requestFocus();

        // Reset displays
        gameStartTime = System.currentTimeMillis();
        piecesValue.setText("0");
        linesValue.setText("0");

        if (currentGameMode == GameMode.TWO_MINUTES) {
            timeValue.setText("2:00.000");
        } else {
            timeValue.setText("0:00.000");
        }

        updateHoldDisplay();
        updateNextDisplay();

        isPause.setValue(Boolean.FALSE);
        isGameOver.setValue(Boolean.FALSE);

        // Reset speed to base speed
        if (currentGameMode == GameMode.NORMAL) {
            currentDropSpeed = baseDropSpeed;
        } else if (currentGameMode == GameMode.FORTY_LINES) {
            currentDropSpeed = 400;
        } else if (currentGameMode == GameMode.TWO_MINUTES) {
            currentDropSpeed = 400;
        }

        // Start timer
        if (timer != null) timer.start();

        // Create NEW timeline with correct speed
        timeLine = new Timeline(new KeyFrame(
                Duration.millis(currentDropSpeed),
                ae -> moveDown(new MoveEvent(EventType.DOWN, EventSource.THREAD))
        ));
        timeLine.setCycleCount(Timeline.INDEFINITE);
        timeLine.play();

        pauseMenuPanel.setVisible(false);
    }

    public void setEventListener(InputEventListener eventListener) {
        this.eventListener = eventListener;
    }

    /**
     * Calculate score based on official Tetris scoring system
     * Includes: base line clear points, combo bonus, and back-to-back Tetris bonus
     */
    private int calculateTetrisScore(int linesCleared, boolean isNormalMode) {
        if (linesCleared == 0) return 0;

        // Base points for line clears (standard Tetris scoring)
        int baseScore = 0;
        switch (linesCleared) {
            case 1:
                baseScore = 100;  // Single
                break;
            case 2:
                baseScore = 300;  // Double
                break;
            case 3:
                baseScore = 500;  // Triple
                break;
            case 4:
                baseScore = 800;  // 4 lines at once
                break;
            default:
                // Clearing more than 4 lines
                baseScore = 800 + (linesCleared - 4) * 200;
                break;
        }

        // Combo bonus and back-to-back tracking
        int comboBonus, backToBackBonus;

        if (isNormalMode) {
            // Use Normal mode's separate combo tracking
            comboBonus = normalModeCombo * 50;
            backToBackBonus = 0;
            if (linesCleared == 4 && normalModeLastWasTetris) {
                backToBackBonus = 400; // 50% bonus for back-to-back Tetris
            }
            normalModeCombo++;  // Increment combo counter
            normalModeLastWasTetris = (linesCleared == 4);  // Track if this was a Tetris
        } else {
            // Use 2-minute mode's separate combo tracking
            comboBonus = currentCombo * 50;
            backToBackBonus = 0;
            if (linesCleared == 4 && lastClearWasTetris) {
                backToBackBonus = 400;
            }
            currentCombo++;
            lastClearWasTetris = (linesCleared == 4);
        }

        int totalScore = baseScore + comboBonus + backToBackBonus;

        return totalScore;
    }


    private void checkTwoMinutesComplete() {
        if (currentGameMode == GameMode.TWO_MINUTES && !challengeCompleted) {
            long elapsed = System.currentTimeMillis() - gameStartTime;
            if (elapsed >= TIME) { // 2 minutes = 120,000ms
                challengeCompleted = true;
                completeTwoMinutesChallenge();
            }
        }
    }

    private void completeTwoMinutesChallenge() {
        // Stop the game
        if (timeLine != null) timeLine.stop();
        if (timer != null) timer.stop();

        // Check if it's a new best score
        boolean isNewBest = currentScore > twoMinutesBestScore;
        if (isNewBest) {
            twoMinutesBestScore = currentScore;
            updateBestScoreDisplay();
        }


        int linesCleared = 0;
        if (eventListener instanceof GameController) {
            linesCleared = ((GameController) eventListener).getLinesCleared();
        }

        showTwoMinutesCompletion(currentScore, linesCleared, isNewBest);
    }

    private void showTwoMinutesCompletion(int finalScore, int linesCleared, boolean isNewBest) {
        String previousBest = (twoMinutesBestScore > 0 && !isNewBest) ?
                String.valueOf(twoMinutesBestScore) : null;

        TwoMinutesCompletionPanel panel = new TwoMinutesCompletionPanel(
                finalScore, linesCleared, isNewBest, previousBest
        );

        currentCompletionPanel = panel;

        panel.setOnRetry(() -> {
            removeCompletionPanel();
            restartGameWithCountdown();
        });

        panel.setOnMainMenu(() -> {
            try {
                // Best score persists because twoMinutesBestScore is static
                javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(
                        getClass().getResource("/MainMenu.fxml")
                );
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

    private void removeCompletionPanel() {
        if (currentCompletionPanel != null) {
            javafx.scene.Parent parent = gamePanel.getParent();
            if (parent instanceof javafx.scene.layout.Pane) {
                ((javafx.scene.layout.Pane) parent).getChildren().remove(currentCompletionPanel);
            }
            currentCompletionPanel = null;
        }
        removeFortyLinesPanel();
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

    private void clearBrickDisplay() {
        // Remove all brick rectangles from the game panel
        if (rectangles != null) {
            for (Rectangle[] row : rectangles) {
                for (Rectangle r : row) {
                    gamePanel.getChildren().remove(r);
                }
            }
        }
    }

    public void newGame(ActionEvent actionEvent) {
        restartGameInstantly();

    }

    public void pauseGame(ActionEvent actionEvent) {
        gamePanel.requestFocus();
    }

    private void togglePause() {
        if (isGameOver.getValue()) return; // Cannot pause if game over
        if (isCountdownActive) return;     // Cannot pause during countdown

        if (isPause.getValue()) {
            // RESUME GAME
            isPause.setValue(false);
            pauseMenuPanel.setVisible(false);

            // Restart timers
            if (timeLine != null) timeLine.play();
            if (timer != null) timer.start();

            // Focus back on game panel so keys work immediately
            gamePanel.requestFocus();

        } else {
            // PAUSE GAME
            isPause.setValue(true);
            pauseMenuPanel.setVisible(true);

            // Stop timers
            if (timeLine != null) timeLine.stop(); // Stop the brick dropping
            if (timer != null) timer.stop();       // Stop the clock

            // Note: We intentionally do NOT stop lockDelayTimeline here because
            // stopping it completely might reset the lock timer, making the game easier.
            // If you want strict pausing, you can pause it, but standard Timeline.stop()
            // resets position. Usually, for simple Tetris, pausing the drop loop is enough.
            if (lockDelayTimeline != null) {
                lockDelayTimeline.stop(); // Reset lock delay on pause prevents exploits
            }
        }
    }

}
