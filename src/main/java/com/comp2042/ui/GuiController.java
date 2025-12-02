package com.comp2042.ui;

import com.comp2042.controller.GameController;
import com.comp2042.controller.InputEventListener;
import com.comp2042.event.*;
import com.comp2042.model.GameMode;
import com.comp2042.model.ViewData;
import com.comp2042.state.*;
import javafx.application.Platform;
import javafx.beans.property.IntegerProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Main GUI Controller - Minimal coordinator for UI components.
 * Delegates all logic to specialized managers.
 */
public class GuiController implements Initializable {

    @FXML private GridPane gamePanel, brickPanel, holdPanel;
    @FXML private GridPane nextPanel1, nextPanel2, nextPanel3, nextPanel4, nextPanel5;
    @FXML private GameOverPanel gameOverPanel;
    @FXML private PauseMenuPanel pauseMenuPanel;
    @FXML private StackPane countdownPanel;
    @FXML private Label countdownLabel, timeValue;
    @FXML private Label scoreValue, bestScoreValue, bestTimeLabel;
    @FXML private Label piecesValue, linesValue, linesLabel, timeLabel;
    @FXML private VBox scoreDisplayContainer, scoreBox, bestScoreBox, bestTimeBox;
    @FXML private Region scoreSeparator;
    @FXML private Pane effectsLayer;
    @FXML private ComboMeterPanel comboMeterPanel;

    private GameState gameState;
    private TimerManager timerManager;
    private ScoringManager scoringManager;
    private GameRenderer renderer;
    private InputHandler inputHandler;
    private UIUpdater uiUpdater;
    private GameLogicHandler logicHandler;
    private GameFlowManager flowManager;
    private ChallengeCompletionManager completionManager;
    private GameController gameController;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initializeComponents();
        setupInputCallbacks();
        setupUI();
    }

    private void initializeComponents() {
        gameState = new GameState();
        renderer = new GameRenderer(gamePanel, brickPanel);
        inputHandler = new InputHandler(gameState);
        uiUpdater = new UIUpdater(gameState);
    }

    private void setupInputCallbacks() {
        inputHandler.setCallback(new InputHandler.InputCallback() {
            public void onMoveLeft() {
                if (logicHandler.moveBrickHorizontally(-1) && gameState.isLockDelayActive()) {logicHandler.resetLockDelay();}
            }

            public void onMoveRight() {
                if (logicHandler.moveBrickHorizontally(1) && gameState.isLockDelayActive()) {logicHandler.resetLockDelay();}
            }

            public void onRotate() {
                if (logicHandler.attemptRotation() && gameState.isLockDelayActive()) {logicHandler.resetLockDelay();}
            }

            public void onSoftDrop() {logicHandler.moveDown(new MoveEvent(EventType.DOWN, EventSource.USER));}
            public void onHardDrop() {logicHandler.handleHardDrop();}

            public void onHold() {
                logicHandler.handleHold(() -> {
                    updateHoldDisplay();
                    updateNextDisplay();
                });
            }

            public void onTogglePause() {
                flowManager.togglePause();
                pauseMenuPanel.setVisible(gameState.isPaused());
                if (gameState.isPaused()) {logicHandler.pauseComboDecay();}
                else {
                    logicHandler.resumeComboDecay();
                    gamePanel.requestFocus();
                }
            }

            public void onRestartInstant() {
                flowManager.restartInstantly(() -> {
                    completionManager.removeCompletionPanels(getRootPane());
                    if (comboMeterPanel != null) {comboMeterPanel.reset();}
                });
            }

            public void onRestartWithCountdown() {
                flowManager.restartWithCountdown(() -> {
                    completionManager.removeCompletionPanels(getRootPane());
                    if (comboMeterPanel != null) {comboMeterPanel.reset();}
                });
            }
        });

        gamePanel.setOnKeyPressed(inputHandler::handleKeyPress);
        gamePanel.setOnKeyReleased(inputHandler::handleKeyRelease);
    }

    private void setupUI() {
        gamePanel.setFocusTraversable(true);
        gamePanel.requestFocus();
        gameOverPanel.setVisible(false);

        if (pauseMenuPanel != null) {
            pauseMenuPanel.setVisible(false);
            pauseMenuPanel.setOnResume(() -> {
                flowManager.togglePause();
                pauseMenuPanel.setVisible(false);
                gamePanel.requestFocus();
            });
            pauseMenuPanel.setOnRetry(() -> flowManager.restartInstantly(() -> {
                completionManager.removeCompletionPanels(getRootPane());
                if (comboMeterPanel != null) {
                    comboMeterPanel.reset();
                }
            }));
            pauseMenuPanel.setOnMainMenu(this::goToMainMenu);
        }
    }

    public void setGameMode(GameMode mode) {
        gameState.setCurrentGameMode(mode);
        gameState.setChallengeCompleted(false);
        gameState.resetScores();
        uiUpdater.configureForGameMode(mode);

        if (comboMeterPanel != null) {
            if (mode == GameMode.FORTY_LINES) {
                comboMeterPanel.setVisible(false);
                comboMeterPanel.setManaged(false);
            } else {
                comboMeterPanel.setVisible(true);
                comboMeterPanel.setManaged(true);
            }
        }
    }

    public void initGameView(int[][] boardMatrix, ViewData brick) {
        uiUpdater.bindLabels(scoreValue, bestScoreValue, bestTimeLabel,
                scoreBox, bestScoreBox, bestTimeBox, scoreSeparator, scoreDisplayContainer,
                piecesValue, linesValue, linesLabel, timeValue, timeLabel);

        timerManager = new TimerManager(gameState, timeValue);
        scoringManager = new ScoringManager(gameState);
        logicHandler = new GameLogicHandler(gameState, timerManager, scoringManager, renderer, uiUpdater);
        flowManager = new GameFlowManager(gameState, timerManager, renderer, uiUpdater, logicHandler);
        completionManager = new ChallengeCompletionManager(gameState, timerManager, uiUpdater);

        if (gamePanel != null && effectsLayer != null) {
            ComboAnimationManager animationManager = new ComboAnimationManager(gamePanel, effectsLayer);
            BoardGlowEffect glowEffect = new BoardGlowEffect(gamePanel);
            logicHandler.setComboComponents(animationManager, comboMeterPanel, glowEffect);
        }

        logicHandler.setGameController(gameController);
        flowManager.setGameController(gameController);
        completionManager.setGameController(gameController);

        flowManager.setCountdownComponents(countdownPanel, countdownLabel);
        flowManager.setUpdateCallbacks(this::updateNextDisplay, this::updateHoldDisplay);
        flowManager.setHideGameOverPanelCallback(() -> gameOverPanel.setVisible(false));
        flowManager.setHidePausePanelCallback(() -> pauseMenuPanel.setVisible(false));

        timerManager.setOnTimeUp(() -> completionManager.completeTwoMinutesChallenge(
                getRootPane(),
                () -> flowManager.restartWithCountdown(() -> {
                    completionManager.removeCompletionPanels(getRootPane());
                    if (comboMeterPanel != null) {
                        comboMeterPanel.reset();
                    }
                }),
                this::goToMainMenu));

        logicHandler.setOnGameOver(this::gameOver);

        logicHandler.setOnChallengeComplete40Lines(() -> completionManager.completeFortyLinesChallenge(
                getRootPane(),
                () -> flowManager.restartWithCountdown(() -> {
                    completionManager.removeCompletionPanels(getRootPane());
                    if (comboMeterPanel != null) {
                        comboMeterPanel.reset();
                    }
                }),
                this::goToMainMenu));

        logicHandler.setOnUpdateNextDisplay(this::updateNextDisplay);

        logicHandler.setOnChallengeComplete2Minutes(() -> completionManager.completeTwoMinutesChallenge(
                getRootPane(),
                () -> flowManager.restartWithCountdown(() -> {
                    completionManager.removeCompletionPanels(getRootPane());
                    if (comboMeterPanel != null) {
                        comboMeterPanel.reset();
                    }
                }),
                this::goToMainMenu));

        renderer.initializeGameBoard(boardMatrix.length, boardMatrix[0].length);
        renderer.initializeBrickPanel(brick.getBrickData());
        renderer.setHoldRectangles(renderer.initializePreviewPanel(holdPanel));
        renderer.setNextRectangles(
                renderer.initializePreviewPanel(nextPanel1),
                renderer.initializePreviewPanel(nextPanel2),
                renderer.initializePreviewPanel(nextPanel3),
                renderer.initializePreviewPanel(nextPanel4),
                renderer.initializePreviewPanel(nextPanel5));

        Platform.runLater(() -> {
            if (gamePanel.getScene() != null) {
                gamePanel.getScene().addEventFilter(KeyEvent.KEY_PRESSED, inputHandler::handleKeyPress);
            }
        });

        flowManager.startGameWithCountdown();
    }

    private void updateHoldDisplay() {
        renderer.updatePreviewPanel(renderer.getHoldRectangles(), gameController.getHoldBrickData());
    }

    public void updateNextDisplay() {
        List<int[][]> nextBricks = gameController.getNextBricksData();
        if (nextBricks.size() > 0) renderer.updatePreviewPanel(renderer.getNextRectangles1(), nextBricks.get(0));
        if (nextBricks.size() > 1) renderer.updatePreviewPanel(renderer.getNextRectangles2(), nextBricks.get(1));
        if (nextBricks.size() > 2) renderer.updatePreviewPanel(renderer.getNextRectangles3(), nextBricks.get(2));
        if (nextBricks.size() > 3) renderer.updatePreviewPanel(renderer.getNextRectangles4(), nextBricks.get(3));
        if (nextBricks.size() > 4) renderer.updatePreviewPanel(renderer.getNextRectangles5(), nextBricks.get(4));
    }

    public void gameOver() {
        gameState.setGameOver(true);
        timerManager.stopAllTimers();
        logicHandler.cancelLockDelay();

        long finalTime = timerManager.getElapsedTime();
        int minutes = (int) (finalTime / 60000);
        int seconds = (int) ((finalTime % 60000) / 1000);
        int millis = (int) (finalTime % 1000);
        String timeStr = String.format("%d:%02d.%03d", minutes, seconds, millis);

        int pieces = gameController.getPiecesPlaced();
        int lines = gameController.getLinesCleared();
        int score;
        if (gameState.getCurrentGameMode() == GameMode.NORMAL) {
            score = gameState.getNormalModeScore();
        } else {
            score = gameState.getTwoMinutesScore();
        }

        gameOverPanel.showGameOver(pieces, lines, finalTime, timeStr, score, gameState.getCurrentGameMode());
        gameOverPanel.setOnRetry(() -> flowManager.restartWithCountdown(() -> {
            completionManager.removeCompletionPanels(getRootPane());
            if (comboMeterPanel != null) {
                comboMeterPanel.reset();
            }
        }));
        gameOverPanel.setOnMainMenu(this::goToMainMenu);
    }

    private StackPane getRootPane() {
        if (gamePanel.getScene() == null) {return null;}
        if (gamePanel.getScene().getRoot() instanceof StackPane) {return (StackPane) gamePanel.getScene().getRoot();}
        Parent parent = gamePanel.getParent();
        while (parent != null) {
            if (parent instanceof StackPane && parent.getParent() == null) {return (StackPane) parent;}
            parent = parent.getParent();
        }
        return null;
    }

    private void goToMainMenu() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/MainMenu.fxml"));
            Parent menuRoot = loader.load();
            Stage stage = (Stage) gamePanel.getScene().getWindow();
            stage.setScene(new Scene(menuRoot, 900, 700));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void setEventListener(InputEventListener eventListener) {
        this.gameController = (GameController) eventListener;
        if (logicHandler != null) {logicHandler.setGameController((GameController) eventListener);}
        if (flowManager != null) {flowManager.setGameController((GameController) eventListener);}
        if (completionManager != null) {completionManager.setGameController((GameController) eventListener);}
    }

    public void bindScore(IntegerProperty integerProperty) {}

    public void refreshGameBackground(int[][] boardMatrix) {
        renderer.refreshGameBackground(boardMatrix);
    }
}