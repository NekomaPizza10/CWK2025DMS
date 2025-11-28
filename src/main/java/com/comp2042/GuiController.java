package com.comp2042;

import javafx.beans.property.IntegerProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import javafx.scene.control.Label;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class GuiController implements Initializable {

    private static final int BRICK_SIZE = 25;
    private static final int PREVIEW_BRICK_SIZE = 20;
    private static final int PREVIEW_GRID_SIZE = 4;
    private static final int FORTY_LINES_GOAL = 3;
    // 2 minutes = 120000 | 10 seconds = 10000 | 30 seconds = 30000
    private static final int TWO_MIN_GOAL = 10 * 1000; // 10 seconds for testing

    @FXML
    private Label scoreLabel, scoreValue;
    @FXML
    private Label bestScoreLabel, bestScoreValue;
    @FXML
    private GridPane gamePanel, brickPanel;
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
    @FXML
    private VBox scoreDisplayContainer, scoreBox, bestScoreBox, bestTimeBox;
    @FXML
    private Region scoreSeparator;

    private Rectangle[][] displayMatrix;
    private InputEventListener eventListener;
    private Rectangle[][] rectangles;
    private Rectangle[][] holdRectangles;
    private Rectangle[][] nextRectangles1, nextRectangles2, nextRectangles3, nextRectangles4, nextRectangles5;

    private boolean rotateKeyPressed = false;
    private boolean hardDropKeyPressed = false;

    private TwoMinutesCompletionPanel currentCompletionPanel;
    private CompletionPanel currentFortyLinesPanel;

    private GameState gameState;
    private TimerManager timerManager;
    private ScoringManager scoringManager;

    public void setGameMode(GameMode mode) {
        gameState.setCurrentGameMode(mode);
        gameState.setChallengeCompleted(false);
        gameState.resetScores();

        if (scoreDisplayContainer != null) {
            scoreDisplayContainer.setVisible(true);

            if (mode == GameMode.NORMAL) {
                // Normal: Show ONLY score
                scoreBox.setVisible(true);
                scoreBox.setManaged(true);
                scoreSeparator.setVisible(false);
                scoreSeparator.setManaged(false);
                bestScoreBox.setVisible(false);
                bestScoreBox.setManaged(false);
                bestTimeBox.setVisible(false);
                bestTimeBox.setManaged(false);
                scoreValue.setText("0");

            } else if (mode == GameMode.FORTY_LINES) {
                // 40 Lines: Show ONLY best time
                scoreBox.setVisible(false);
                scoreBox.setManaged(false);
                scoreSeparator.setVisible(false);
                scoreSeparator.setManaged(false);
                bestScoreBox.setVisible(false);
                bestScoreBox.setManaged(false);
                bestTimeBox.setVisible(true);
                bestTimeBox.setManaged(true);
                updateBestTimeDisplay();

            } else if (mode == GameMode.TWO_MINUTES) {
                // 2 Minutes: Show score + separator + best score
                scoreBox.setVisible(true);
                scoreBox.setManaged(true);
                scoreSeparator.setVisible(true);
                scoreSeparator.setManaged(true);
                bestScoreBox.setVisible(true);
                bestScoreBox.setManaged(true);
                bestTimeBox.setVisible(false);
                bestTimeBox.setManaged(false);
                scoreValue.setText("0");
                updateBestScoreDisplay();
            }
        }

        if (linesLabel != null) {
            if (mode == GameMode.FORTY_LINES) {
                linesLabel.setText("LINES (Goal: " + FORTY_LINES_GOAL + ")");
            } else {
                linesLabel.setText("LINES");
            }
        }

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
        gameState = new GameState();

        gamePanel.setFocusTraversable(true);
        gamePanel.requestFocus();
        gamePanel.setOnKeyPressed(this::handleKeyPress);
        gamePanel.setOnKeyReleased(this::handleKeyRelease);
        gameOverPanel.setVisible(false);

        if (pauseMenuPanel != null) {
            pauseMenuPanel.setVisible(false);
            pauseMenuPanel.setOnResume(this::togglePause);
            pauseMenuPanel.setOnMainMenu(this::goToMainMenu);
        }
    }

    public void handleKeyPress(KeyEvent keyEvent) {
        if (gameState.isCountdownActive()) {
            return;
        }

        // FIX: Handle 'N' key for restart
        if (keyEvent.getCode() == KeyCode.N) {
            if (gameState.isChallengeCompleted() || gameState.isGameOver()) {
                // After challenge/game over - restart with countdown
                removeCompletionPanel();
                restartGameWithCountdown();
            } else {
                // During normal gameplay - instant restart
                restartGameInstantly();
            }
            keyEvent.consume();
            return;
        }

        // Block ALL input if game is over
        if (gameState.isGameOver()) {
            keyEvent.consume();
            return;
        }

        if (!gameState.isPaused()) {
            if (keyEvent.getCode() == KeyCode.LEFT || keyEvent.getCode() == KeyCode.A) {
                boolean moved = moveBrickHorizontally(-1);
                if (moved && gameState.isLockDelayActive()) {
                    resetLockDelay();
                }
                keyEvent.consume();
            }
            if (keyEvent.getCode() == KeyCode.RIGHT || keyEvent.getCode() == KeyCode.D) {
                boolean moved = moveBrickHorizontally(1);
                if (moved && gameState.isLockDelayActive()) {
                    resetLockDelay();
                }
                keyEvent.consume();
            }
            if ((keyEvent.getCode() == KeyCode.UP || keyEvent.getCode() == KeyCode.W) && !rotateKeyPressed) {
                rotateKeyPressed = true;
                boolean rotated = attemptRotation();
                if (rotated && gameState.isLockDelayActive()) {
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

        if (keyEvent.getCode() == KeyCode.P || keyEvent.getCode() == KeyCode.ESCAPE) {
            togglePause();
            keyEvent.consume();
        }
    }

    private boolean moveBrickHorizontally(int direction) {
        if (!(eventListener instanceof GameController)) return false;

        GameController gc = (GameController) eventListener;
        Board board = gc.getBoard();

        boolean moved = direction < 0 ? board.moveBrickLeft() : board.moveBrickRight();

        if (moved) {
            refreshBrick(board.getViewData());
            return true;
        }
        return false;
    }

    private boolean attemptRotation() {
        if (!(eventListener instanceof GameController)) return false;

        GameController gc = (GameController) eventListener;
        Board board = gc.getBoard();

        boolean rotated = board.rotateLeftBrick();

        if (rotated) {
            refreshBrick(board.getViewData());
            if (gameState.isLockDelayActive()) {
                resetLockDelay();
            }
            return true;
        }
        return false;
    }

    public void handleKeyRelease(KeyEvent keyEvent) {
        if (keyEvent.getCode() == KeyCode.UP || keyEvent.getCode() == KeyCode.W) {
            rotateKeyPressed = false;
        }
        if (keyEvent.getCode() == KeyCode.SPACE) {
            hardDropKeyPressed = false;
        }
    }

    private void handleHold() {
        if (eventListener instanceof GameController) {
            if (gameState.isLockDelayActive()) {
                cancelLockDelay();
            }

            GameController gc = (GameController) eventListener;
            boolean holdSuccess = gc.holdBrick();

            if (holdSuccess) {
                updateHoldDisplay();
                Board board = gc.getBoard();
                refreshGameBackground(board.getBoardMatrix());
                refreshBrick(board.getViewData());
                updateNextDisplay();
            }
        }
    }

    private void handleHardDrop() {
        if (eventListener instanceof GameController) {
            cancelLockDelay();

            GameController gc = (GameController) eventListener;
            Board board = gc.getBoard();

            ViewData currentBrickData = board.getViewData();
            int currentY = currentBrickData.getyPosition();
            int shadowY = calculateShadowPosition(currentBrickData);
            int dropDistance = shadowY - currentY;

            while (board.moveBrickDown()) {
            }

            if (dropDistance > 0) {
                scoringManager.addHardDropBonus(dropDistance);
                updateScoreDisplay();
            }

            board.mergeBrickToBackground();
            ClearRow clearRow = board.clearRows();

            handleLineClears(clearRow);

            gameState.setLockDelayActive(false);
            gameState.resetLockDelayCount();

            // CRITICAL FIX: Check if game over BEFORE spawning new brick
            if (board.createNewBrick()) {
                gameOver();
                return; // Don't update displays if game is over
            }

            refreshGameBackground(board.getBoardMatrix());
            refreshBrick(board.getViewData());
            updateNextDisplay();
            updateStatsDisplay();
        }
    }

    public void initGameView(int[][] boardMatrix, ViewData brick) {
        int height = boardMatrix.length;
        int width = boardMatrix[0].length;

        // Initialize timer manager after timeValue is available
        timerManager = new TimerManager(gameState, timeValue);
        scoringManager = new ScoringManager(gameState);

        displayMatrix = new Rectangle[height][width];
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                Rectangle rectangle = new Rectangle(BRICK_SIZE, BRICK_SIZE);
                rectangle.setFill(Color.TRANSPARENT);
                rectangle.setStroke(Color.rgb(40, 40, 40));
                rectangle.setStrokeWidth(0.5);
                displayMatrix[i][j] = rectangle;
                gamePanel.add(rectangle, j, i);
            }
        }

        rectangles = new Rectangle[brick.getBrickData().length][brick.getBrickData()[0].length];
        for (int i = 0; i < brick.getBrickData().length; i++) {
            for (int j = 0; j < brick.getBrickData()[i].length; j++) {
                Rectangle rectangle = new Rectangle(BRICK_SIZE, BRICK_SIZE);
                rectangle.setFill(getFillColor(brick.getBrickData()[i][j]));
                rectangles[i][j] = rectangle;
                brickPanel.add(rectangle, j, i);
            }
        }

        brickPanel.toFront();
        brickPanel.setLayoutX(brick.getxPosition() * BRICK_SIZE);
        brickPanel.setLayoutY(brick.getyPosition() * BRICK_SIZE);

        initializePreviewPanel(holdPanel, PREVIEW_BRICK_SIZE);
        holdRectangles = createPreviewRectangles(holdPanel, PREVIEW_BRICK_SIZE);

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

        brickPanel.setVisible(false);

        // Set initial drop speed
        if (gameState.getCurrentGameMode() == GameMode.NORMAL) {
            gameState.setCurrentDropSpeed(gameState.getBaseDropSpeed());
        } else {
            gameState.setCurrentDropSpeed(400);
        }

        javafx.application.Platform.runLater(() -> {
            if (gamePanel.getScene() != null) {
                gamePanel.getScene().addEventFilter(KeyEvent.KEY_PRESSED, this::handleSceneKeyPress);
            }
        });
        startGameWithCountdown();
    }

    private void initializePreviewPanel(GridPane panel, int size) {
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
                rectangles.setStroke(Color.BLACK);
                rectangles.setStrokeWidth(0.5);
                rects[i][j] = rectangles;
                panel.add(rectangles, j, i);
            }
        }
        return rects;
    }

    private void updatePreviewPanel(Rectangle[][] rects, int[][] brickData) {
        for (int i = 0; i < PREVIEW_GRID_SIZE; i++) {
            for (int j = 0; j < PREVIEW_GRID_SIZE; j++) {
                rects[i][j].setFill(Color.TRANSPARENT);
            }
        }

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

        int brickHeight = maxRow - minRow + 1;
        int brickWidth = maxCol - minCol + 1;
        int offsetRow = (4 - brickHeight) / 2;
        int offsetCol = (4 - brickWidth) / 2;

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
        return switch (i) {
            case 0 -> Color.TRANSPARENT;
            case 1 -> Color.CYAN;
            case 2 -> Color.BLUE;
            case 3 -> Color.ORANGE;
            case 4 -> Color.YELLOW;
            case 5 -> Color.GREEN;
            case 6 -> Color.PURPLE;
            case 7 -> Color.RED;
            default -> Color.WHITE;
        };
    }

    private void refreshBrick(ViewData brick) {
        if (!gameState.isPaused() && !gameState.isGameOver()) {
            for (Rectangle[] row : rectangles) {
                for (Rectangle r : row) {
                    gamePanel.getChildren().remove(r);
                }
            }

            for (int i = 0; i < brick.getBrickData().length; i++) {
                for (int j = 0; j < brick.getBrickData()[i].length; j++) {
                    if (brick.getBrickData()[i][j] != 0) {
                        int gridX = brick.getxPosition() + j;
                        int gridY = brick.getyPosition() + i;

                        rectangles[i][j].setFill(getFillColor(brick.getBrickData()[i][j]));
                        rectangles[i][j].setStroke(Color.BLACK);
                        rectangles[i][j].setStrokeWidth(0.5);
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
                displayMatrix[i][j].setFill(getFillColor(board[i][j]));
            }
        }
    }

    private void moveDown(MoveEvent event) {
        if (gameState.isPaused() || gameState.isGameOver()) return;

        GameController gc = (GameController) eventListener;
        Board board = gc.getBoard();

        boolean canMove = board.moveBrickDown();

        if (!canMove) {
            if (!gameState.isLockDelayActive()) {
                gameState.setLockDelayActive(true);
                gameState.resetLockDelayCount();
                startLockDelay();
            }
        } else {
            if (gameState.isLockDelayActive()) {
                cancelLockDelay();
            }

            if (event.getEventSource() == EventSource.USER) {
                scoringManager.addSoftDropBonus(1);
                updateScoreDisplay();
            }

            refreshBrick(board.getViewData());
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

            if (gameState.getCurrentGameMode() == GameMode.FORTY_LINES &&
                    linesCleared >= FORTY_LINES_GOAL &&
                    !gameState.isChallengeCompleted()) {
                gameState.setChallengeCompleted(true);
                completeFortyLinesChallenge();
            }

            if (gameState.getCurrentGameMode() == GameMode.TWO_MINUTES &&
                    !gameState.isChallengeCompleted()) {
                long elapsed = timerManager.getElapsedTime();
                if (elapsed >= TWO_MIN_GOAL) {
                    gameState.setChallengeCompleted(true);
                    completeTwoMinutesChallenge();
                }
            }
        }
    }

    private void updateScoreDisplay() {
        if (scoreValue != null) {
            scoreValue.setText(String.valueOf(scoringManager.getCurrentScore()));
        }
    }

    private void updateBestTimeDisplay() {
        if (gameState.getFortyLinesBestTime() == Long.MAX_VALUE) {
            bestTimeLabel.setText("--:--");
        } else {
            long time = gameState.getFortyLinesBestTime();
            int minutes = (int) (time / 60000);
            int seconds = (int) ((time % 60000) / 1000);
            int millis = (int) (time % 1000);
            bestTimeLabel.setText(String.format("%d:%02d.%03d", minutes, seconds, millis));
        }
    }

    private void updateBestScoreDisplay() {
        if (bestScoreValue != null) {
            int best = GameState.getTwoMinutesBestScore();
            if (best > 0) {
                bestScoreValue.setText(String.valueOf(best));
            } else {
                bestScoreValue.setText("--");
            }
        }
    }

    // Timing methods delegated to TimerManager
    private void startLockDelay() {
        timerManager.startLockDelay(this::executeLock);
    }

    private void resetLockDelay() {
        if (gameState.getLockDelayResetCount() < gameState.getMaxLockResets()) {
            gameState.incrementLockDelayResetCount();
            timerManager.stopLockDelay();
            startLockDelay();
        } else {
            executeLock();
        }
    }

    private void cancelLockDelay() {
        timerManager.stopLockDelay();
        gameState.setLockDelayActive(false);
        gameState.resetLockDelayCount();
    }

    private void executeLock() {
        if (gameState.isGameOver()) return; // CRITICAL: Don't lock if game is over

        if (eventListener instanceof GameController) {
            GameController gc = (GameController) eventListener;
            Board board = gc.getBoard();

            board.mergeBrickToBackground();
            ClearRow clearRow = board.clearRows();

            handleLineClears(clearRow);

            gameState.setLockDelayActive(false);
            gameState.resetLockDelayCount();
            timerManager.stopLockDelay();

            // CRITICAL FIX: Check if game over BEFORE spawning
            if (board.createNewBrick()) {
                gameOver();
                return;
            }

            refreshGameBackground(board.getBoardMatrix());
            refreshBrick(board.getViewData());
            updateNextDisplay();
        }
    }

    private void handleLineClears(ClearRow clearRow) {
        if (clearRow != null && clearRow.getLinesRemoved() > 0) {
            int linesCleared = clearRow.getLinesRemoved();
            int earnedScore = scoringManager.calculateTetrisScore(linesCleared);

            if (gameState.getCurrentGameMode() == GameMode.NORMAL) {
                gameState.setNormalModeScore(gameState.getNormalModeScore() + earnedScore);
            } else if (gameState.getCurrentGameMode() == GameMode.TWO_MINUTES) {
                gameState.setTwoMinutesScore(gameState.getTwoMinutesScore() + earnedScore);
            }

            updateScoreDisplay();
            updateStatsDisplay();
            updateGameSpeed();
        } else {
            scoringManager.resetCombo();
        }
    }

    private void updateGameSpeed() {
        if (gameState.getCurrentGameMode() == GameMode.NORMAL && eventListener instanceof GameController) {
            GameController gc = (GameController) eventListener;
            int linesCleared = gc.getLinesCleared();
            int level = linesCleared / 10;
            int newSpeed = Math.max(
                    gameState.getMinDropSpeed(),
                    gameState.getBaseDropSpeed() - (level * gameState.getSpeedDecreasePerLevel())
            );
            gameState.setCurrentDropSpeed(newSpeed);

            timerManager.stopDropTimer();
            timerManager.startDropTimer(newSpeed, () ->
                    moveDown(new MoveEvent(EventType.DOWN, EventSource.THREAD))
            );
        }
    }

    private void completeFortyLinesChallenge() {
        timerManager.stopAllTimers();
        gameState.setGameOver(true);

        long finalTime = timerManager.getElapsedTime();

        boolean isNewBest = finalTime < gameState.getFortyLinesBestTime();
        if (isNewBest) {
            gameState.setFortyLinesBestTime(finalTime);
            updateBestTimeDisplay();
        }

        int minutes = (int) (finalTime / 60000);
        int seconds = (int) ((finalTime % 60000) / 1000);
        int millis = (int) (finalTime % 1000);
        String timeString = String.format("%d:%02d.%03d", minutes, seconds, millis);

        showCompletionMessage(timeString, isNewBest);
    }

    private void showCompletionMessage(String timeString, boolean isNewBest) {
        String previousBest = null;
        if (gameState.getFortyLinesBestTime() != Long.MAX_VALUE && !isNewBest) {
            long time = gameState.getFortyLinesBestTime();
            int minutes = (int) (time / 60000);
            int seconds = (int) ((time % 60000) / 1000);
            int millis = (int) (time % 1000);
            previousBest = String.format("%d:%02d.%03d", minutes, seconds, millis);
        }

        CompletionPanel panel = new CompletionPanel(timeString, isNewBest, previousBest);
        currentFortyLinesPanel = panel;

        panel.setOnRetry(() -> {
            removeCompletionPanel();
            restartGameWithCountdown();
        });

        panel.setOnMainMenu(this::goToMainMenu);

        // Add as full-screen overlay
        StackPane root = getRootPane();
        root.getChildren().add(panel);

        // Ensure panel fills entire screen
        StackPane.setAlignment(panel, javafx.geometry.Pos.CENTER);
    }

    private void completeTwoMinutesChallenge() {
        timerManager.stopAllTimers();
        gameState.setGameOver(true);

        int finalScore = gameState.getTwoMinutesScore();
        boolean isNewBest = finalScore > GameState.getTwoMinutesBestScore();
        if (isNewBest) {
            GameState.setTwoMinutesBestScore(finalScore);
            updateBestScoreDisplay();
        }

        int linesCleared = 0;
        if (eventListener instanceof GameController) {
            linesCleared = ((GameController) eventListener).getLinesCleared();
        }

        showTwoMinutesCompletion(finalScore, linesCleared, isNewBest);
    }

    private void showTwoMinutesCompletion(int finalScore, int linesCleared, boolean isNewBest) {
        String previousBest = (GameState.getTwoMinutesBestScore() > 0 && !isNewBest) ?
                String.valueOf(GameState.getTwoMinutesBestScore()) : null;

        TwoMinutesCompletionPanel panel = new TwoMinutesCompletionPanel(
                finalScore, linesCleared, isNewBest, previousBest
        );
        currentCompletionPanel = panel;

        panel.setOnRetry(() -> {
            removeCompletionPanel();
            restartGameWithCountdown();
        });

        panel.setOnMainMenu(this::goToMainMenu);

        // Add as full-screen overlay
        StackPane root = getRootPane();
        root.getChildren().add(panel);

        // Ensure panel fills entire screen
        StackPane.setAlignment(panel, javafx.geometry.Pos.CENTER);
    }

    private void removeCompletionPanel() {
        StackPane root = getRootPane();

        if (currentCompletionPanel != null) {
            root.getChildren().remove(currentCompletionPanel);
            currentCompletionPanel = null;
        }
        if (currentFortyLinesPanel != null) {
            root.getChildren().remove(currentFortyLinesPanel);
            currentFortyLinesPanel = null;
        }
    }

    private void showCountdown(Runnable onComplete) {
        removeCompletionPanel();

        gameState.setCountdownActive(true);
        countdownPanel.setVisible(true);
        brickPanel.setVisible(false);

        timerManager.startCountdown(countdownLabel, () -> {
            countdownPanel.setVisible(false);
            brickPanel.setVisible(true);
            gameState.setCountdownActive(false);
            onComplete.run();
        });
    }

    private void restartGameWithCountdown() {
        timerManager.stopAllTimers();
        cancelLockDelay();

        gameState.setChallengeCompleted(false);
        gameState.resetScores();
        updateScoreDisplay();

        gameOverPanel.setVisible(false);
        countdownPanel.setVisible(false);
        brickPanel.setVisible(false);

        eventListener.createNewGame();

        if (eventListener instanceof GameController) {
            GameController gc = (GameController) eventListener;
            Board board = gc.getBoard();
            board.newGame();
            refreshGameBackground(board.getBoardMatrix());
            clearBrickDisplay();
        }

        gamePanel.requestFocus();
        timerManager.resetStartTime();
        piecesValue.setText("0");
        linesValue.setText("0");

        if (gameState.getCurrentGameMode() == GameMode.TWO_MINUTES) {
            timeValue.setText("2:00.000");
        } else {
            timeValue.setText("0:00.000");
        }

        updateHoldDisplay();
        updateNextDisplay();

        gameState.setPaused(false);
        gameState.setGameOver(false);

        if (gameState.getCurrentGameMode() == GameMode.NORMAL) {
            gameState.setCurrentDropSpeed(gameState.getBaseDropSpeed());
        } else {
            gameState.setCurrentDropSpeed(400);
        }

        showCountdown(() -> {
            if (eventListener instanceof GameController) {
                GameController gc = (GameController) eventListener;
                Board board = gc.getBoard();
                board.createNewBrick();
                refreshBrick(board.getViewData());
                updateNextDisplay();
            }

            timerManager.startGameTimer();
            timerManager.startDropTimer(gameState.getCurrentDropSpeed(), () ->
                    moveDown(new MoveEvent(EventType.DOWN, EventSource.THREAD))
            );
        });

        pauseMenuPanel.setVisible(false);
    }

    private void startGameWithCountdown() {
        brickPanel.setVisible(false);

        showCountdown(() -> {
            timerManager.startGameTimer();
            timerManager.resetStartTime();

            timerManager.startDropTimer(gameState.getCurrentDropSpeed(), () ->
                    moveDown(new MoveEvent(EventType.DOWN, EventSource.THREAD))
            );
        });
    }

    private void restartGameInstantly() {
        timerManager.stopAllTimers();
        cancelLockDelay();

        gameState.setChallengeCompleted(false);
        removeCompletionPanel();
        gameState.resetScores();
        updateScoreDisplay();

        gameOverPanel.setVisible(false);
        countdownPanel.setVisible(false);
        brickPanel.setVisible(true);

        clearBrickDisplay();
        eventListener.createNewGame();

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
        timerManager.resetStartTime();
        piecesValue.setText("0");
        linesValue.setText("0");

        if (gameState.getCurrentGameMode() == GameMode.TWO_MINUTES) {
            timeValue.setText("2:00.000");
        } else {
            timeValue.setText("0:00.000");
        }

        updateHoldDisplay();
        updateNextDisplay();

        gameState.setPaused(false);
        gameState.setGameOver(false);

        if (gameState.getCurrentGameMode() == GameMode.NORMAL) {
            gameState.setCurrentDropSpeed(gameState.getBaseDropSpeed());
        } else {
            gameState.setCurrentDropSpeed(400);
        }

        timerManager.startGameTimer();
        timerManager.startDropTimer(gameState.getCurrentDropSpeed(), () ->
                moveDown(new MoveEvent(EventType.DOWN, EventSource.THREAD))
        );

        pauseMenuPanel.setVisible(false);
    }

    public void setEventListener(InputEventListener eventListener) {
        this.eventListener = eventListener;
    }

    public void bindScore(IntegerProperty integerProperty) {
        // Score binding
    }

    public void gameOver() {
        // CRITICAL FIX: Set game over state FIRST
        gameState.setGameOver(true);

        // Then stop all timers
        timerManager.stopAllTimers();
        cancelLockDelay();

        long finalTime = timerManager.getElapsedTime();
        int minutes = (int) (finalTime / 60000);
        int seconds = (int) ((finalTime % 60000) / 1000);
        int millis = (int) (finalTime % 1000);
        String timeStr = String.format("%d:%02d.%03d", minutes, seconds, millis);

        int pieces = 0, lines = 0, score = 0;
        if (eventListener instanceof GameController) {
            GameController gc = (GameController) eventListener;
            pieces = gc.getPiecesPlaced();
            lines = gc.getLinesCleared();
        }

        if (gameState.getCurrentGameMode() == GameMode.NORMAL) {
            score = gameState.getNormalModeScore();
        } else if (gameState.getCurrentGameMode() == GameMode.TWO_MINUTES) {
            score = gameState.getTwoMinutesScore();
        }

        gameOverPanel.showGameOver(pieces, lines, finalTime, timeStr, score, gameState.getCurrentGameMode());
        gameOverPanel.setOnRetry(this::restartGameWithCountdown);
        gameOverPanel.setOnMainMenu(this::goToMainMenu);
    }

    private void clearBrickDisplay() {
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
        if (gameState.isGameOver()) return;
        if (gameState.isCountdownActive()) return;

        if (gameState.isPaused()) {
            gameState.setPaused(false);
            pauseMenuPanel.setVisible(false);
            timerManager.resumeDropTimer();
            timerManager.startGameTimer();
            gamePanel.requestFocus();
        } else {
            gameState.setPaused(true);
            pauseMenuPanel.setVisible(true);
            timerManager.pauseDropTimer();
            timerManager.stopGameTimer();
            if (gameState.isLockDelayActive()) {
                timerManager.stopLockDelay();
            }
        }
    }

    private void goToMainMenu() {
        try {
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
    }

    // Shadow and collision detection
    private void updateShadow(ViewData brick) {
        if (!(eventListener instanceof GameController)) return;

        GameController gc = (GameController) eventListener;
        Board board = gc.getBoard();
        int[][] boardMatrix = board.getBoardMatrix();

        refreshGameBackground(boardMatrix);

        int shadowY = calculateShadowPosition(brick);
        int shadowX = brick.getxPosition();
        int[][] brickData = brick.getBrickData();

        for (int i = 0; i < brickData.length; i++) {
            for (int j = 0; j < brickData[i].length; j++) {
                if (brickData[i][j] != 0) {
                    int boardRow = shadowY + i;
                    int boardCol = shadowX + j;

                    if (boardRow >= 0 && boardRow < displayMatrix.length &&
                            boardCol >= 0 && boardCol < displayMatrix[0].length) {
                        if (boardMatrix[boardRow][boardCol] == 0) {
                            Color shadowColor = Color.rgb(128, 128, 128, 0.3);
                            displayMatrix[boardRow][boardCol].setFill(shadowColor);
                        }
                    }
                }
            }
        }
    }

    private int calculateShadowPosition(ViewData brick) {
        if (!(eventListener instanceof GameController)) {
            return brick.getyPosition();
        }

        GameController gc = (GameController) eventListener;
        Board board = gc.getBoard();
        int[][] boardMatrix = board.getBoardMatrix();
        int[][] brickShape = brick.getBrickData();

        int currentX = brick.getxPosition();
        int currentY = brick.getyPosition();
        int dropY = currentY;

        while (!checkCollision(boardMatrix, brickShape, currentX, dropY + 1)) {
            dropY++;
        }

        return dropY;
    }

    private boolean checkCollision(int[][] board, int[][] brick, int x, int y) {
        for (int i = 0; i < brick.length; i++) {
            for (int j = 0; j < brick[i].length; j++) {
                if (brick[i][j] != 0) {
                    int boardRow = y + i;
                    int boardCol = x + j;

                    if (boardRow >= board.length) return true;
                    if (boardCol < 0 || boardCol >= board[0].length) return true;
                    if (boardRow >= 0 && board[boardRow][boardCol] != 0) return true;
                }
            }
        }
        return false;
    }


    private void handleSceneKeyPress(KeyEvent keyEvent) {
        if (keyEvent.getCode() == KeyCode.N) {
            if (gameState.isCountdownActive()) {
                return;
            }

            if (gameState.isChallengeCompleted() || gameState.isGameOver()) {
                // After challenge/game over - restart with countdown
                removeCompletionPanel();
                restartGameWithCountdown();
            } else {
                // During normal gameplay - instant restart
                restartGameInstantly();
            }
            keyEvent.consume();
        }
    }

    //For 40-line and 2 minute Mode overlay completion panel
    private StackPane getRootPane() {
        if (gamePanel.getScene() != null && gamePanel.getScene().getRoot() instanceof StackPane) {
            return (StackPane) gamePanel.getScene().getRoot();
        }
        // Fallback: traverse parent hierarchy
        javafx.scene.Parent parent = gamePanel.getParent();
        while (parent != null) {
            if (parent instanceof StackPane && parent.getParent() == null) {
                return (StackPane) parent;
            }
            if (parent.getParent() == null && parent instanceof javafx.scene.layout.Pane) {
                // Wrap in StackPane if needed
                break;
            }
            parent = parent.getParent();
        }
        return (StackPane) gamePanel.getScene().getRoot();
    }

}

