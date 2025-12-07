package com.comp2042.ui.initialization;

import com.comp2042.controller.GameController;
import com.comp2042.model.GameMode;
import com.comp2042.model.ViewData;
import com.comp2042.state.*;
import com.comp2042.ui.effect.*;
import com.comp2042.ui.logic.GameLogicHandler;
import com.comp2042.ui.manager.*;
import com.comp2042.ui.render.*;
import com.comp2042.ui.panel.GameOverPanel;
import javafx.application.Platform;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;

import java.util.List;

/**
 * Manages game view initialization, updates, and game over handling.
 */
public class GameViewController {

    private final GuiController controller;

    /**
     * Creates a new controller for managing the game view.
     *
     * @param controller the main GUI controller used to access UI components
     */
    public GameViewController(GuiController controller) {
        this.controller = controller;
    }

    /**
     * Initializes the full game view, including managers, logic, renderer,
     * callbacks, and key-event handling, then starts the game countdown.
     *
     * @param boardMatrix the initial board state
     * @param brick       the currently falling brick to display
     */
    public void initGameView(int[][] boardMatrix, ViewData brick) {
        initializeManagers();
        initializeLogicHandler();
        setupComboComponents();
        configureManagers();
        setupTimerCallbacks();
        setupLogicCallbacks();
        initializeRenderer(boardMatrix, brick);
        setupSceneKeyFilter();
        startGame();
    }

    private void initializeManagers() {
        GameState gameState = controller.getGameState();

        TimerManager timerManager = new TimerManager(gameState, controller.getTimeValue());
        controller.setTimerManager(timerManager);

        ScoringManager scoringManager = new ScoringManager(gameState);
        controller.setScoringManager(scoringManager);
    }

    private void initializeLogicHandler() {
        GameLogicHandler logicHandler = new GameLogicHandler(
                controller.getGameState(),
                controller.getTimerManager(),
                controller.getScoringManager(),
                controller.getRenderer(),
                controller.getUiUpdater()
        );
        controller.setLogicHandler(logicHandler);

        GameFlowManager flowManager = new GameFlowManager(
                controller.getGameState(),
                controller.getTimerManager(),
                controller.getRenderer(),
                controller.getUiUpdater(),
                logicHandler
        );
        controller.setFlowManager(flowManager);

        ChallengeCompletionManager completionManager = new ChallengeCompletionManager(
                controller.getGameState(),
                controller.getTimerManager(),
                controller.getUiUpdater()
        );
        controller.setCompletionManager(completionManager);
    }

    private void setupComboComponents() {
        if (controller.getGamePanel() == null || controller.getEffectsLayer() == null) {
            return;
        }

        ComboAnimationManager animationManager = new ComboAnimationManager(
                controller.getGamePanel(),
                controller.getEffectsLayer()
        );
        BoardGlowEffect glowEffect = new BoardGlowEffect(controller.getGamePanel());

        controller.getLogicHandler().setComboComponents(
                animationManager,
                controller.getComboMeterPanel(),
                glowEffect
        );
    }

    private void configureManagers() {
        GameController gameController = controller.getGameController();

        controller.getLogicHandler().setGameController(gameController);
        controller.getFlowManager().setGameController(gameController);
        controller.getCompletionManager().setGameController(gameController);

        controller.getFlowManager().setCountdownComponents(
                controller.getCountdownPanel(),
                controller.getCountdownLabel()
        );

        controller.getFlowManager().setUpdateCallbacks(
                controller::updateNextDisplay,
                this::updateHoldDisplay
        );

        controller.getFlowManager().setHideGameOverPanelCallback(
                () -> controller.getGameOverPanel().setVisible(false)
        );

        controller.getFlowManager().setHidePausePanelCallback(
                () -> controller.getPauseMenuPanel().setVisible(false)
        );

        bindLabels();
    }

    private void bindLabels() {
        controller.getUiUpdater().bindLabels(
                controller.getScoreValue(),
                controller.getBestScoreValue(),
                controller.getBestTimeLabel(),
                controller.getScoreBox(),
                controller.getBestScoreBox(),
                controller.getBestTimeBox(),
                controller.getScoreSeparator(),
                controller.getScoreDisplayContainer(),
                controller.getPiecesValue(),
                controller.getLinesValue(),
                controller.getLinesLabel(),
                controller.getTimeValue(),
                controller.getTimeLabel()
        );
    }

    private void setupTimerCallbacks() {
        controller.getTimerManager().setOnTimeUp(() -> {
            controller.getCompletionManager().completeTwoMinutesChallenge(
                    getRootPane(),
                    this::restartWithCountdown,
                    controller.getNavigationHandler()::goToMainMenu
            );
        });
    }

    private void setupLogicCallbacks() {
        GameLogicHandler logicHandler = controller.getLogicHandler();

        logicHandler.setOnGameOver(controller::gameOver);

        logicHandler.setOnChallengeComplete40Lines(() -> {
            controller.getCompletionManager().completeFortyLinesChallenge(
                    getRootPane(),
                    this::restartWithCountdown,
                    controller.getNavigationHandler()::goToMainMenu
            );
        });

        logicHandler.setOnUpdateNextDisplay(controller::updateNextDisplay);

        logicHandler.setOnChallengeComplete2Minutes(() -> {
            controller.getCompletionManager().completeTwoMinutesChallenge(
                    getRootPane(),
                    this::restartWithCountdown,
                    controller.getNavigationHandler()::goToMainMenu
            );
        });
    }

    /**
     * Initializes the game renderer for the board, hold piece, and next piece previews.
     *
     * @param boardMatrix the initial board matrix
     * @param brick       the initial brick to display
     */
    private void initializeRenderer(int[][] boardMatrix, ViewData brick) {
        GameRenderer renderer = controller.getRenderer();
        GridPane[] nextPanels = controller.getNextPanels();

        renderer.initializeGameBoard(boardMatrix.length, boardMatrix[0].length);
        renderer.initializeBrickPanel(brick.getBrickData());
        renderer.setHoldRectangles(renderer.initializePreviewPanel(controller.getHoldPanel()));
        renderer.setNextRectangles(
                renderer.initializePreviewPanel(nextPanels[0]),
                renderer.initializePreviewPanel(nextPanels[1]),
                renderer.initializePreviewPanel(nextPanels[2]),
                renderer.initializePreviewPanel(nextPanels[3]),
                renderer.initializePreviewPanel(nextPanels[4])
        );
    }

    private void setupSceneKeyFilter() {
        Platform.runLater(() -> {
            if (controller.getGamePanel().getScene() != null) {
                controller.getGamePanel().getScene().addEventFilter(
                        KeyEvent.KEY_PRESSED,
                        controller.getInputHandler()::handleKeyPress
                );
            }
        });
    }

    private void startGame() {
        controller.getFlowManager().startGameWithCountdown();
    }

    public void updateHoldDisplay() {
        controller.getRenderer().updatePreviewPanel(
                controller.getRenderer().getHoldRectangles(),
                controller.getGameController().getHoldBrickData()
        );
    }

    public void updateNextDisplay() {
        List<int[][]> nextBricks = controller.getGameController().getNextBricksData();
        GameRenderer renderer = controller.getRenderer();

        if (nextBricks.size() > 0) renderer.updatePreviewPanel(renderer.getNextRectangles1(), nextBricks.get(0));
        if (nextBricks.size() > 1) renderer.updatePreviewPanel(renderer.getNextRectangles2(), nextBricks.get(1));
        if (nextBricks.size() > 2) renderer.updatePreviewPanel(renderer.getNextRectangles3(), nextBricks.get(2));
        if (nextBricks.size() > 3) renderer.updatePreviewPanel(renderer.getNextRectangles4(), nextBricks.get(3));
        if (nextBricks.size() > 4) renderer.updatePreviewPanel(renderer.getNextRectangles5(), nextBricks.get(4));
    }

    public void handleGameOver() {
        GameState gameState = controller.getGameState();
        TimerManager timerManager = controller.getTimerManager();
        GameController gameController = controller.getGameController();

        gameState.setGameOver(true);
        timerManager.stopAllTimers();
        controller.getLogicHandler().cancelLockDelay();

        GameOverData data = collectGameOverData(timerManager, gameController, gameState);
        showGameOverPanel(data);
    }

    /**
     * Collects relevant data for a game-over summary.
     *
     * @param timerManager   the timer manager tracking elapsed time
     * @param gameController the game controller providing gameplay statistics
     * @param gameState      the active game state
     * @return a data object containing pieces, lines, time, score, and mode
     */
    private GameOverData collectGameOverData(TimerManager timerManager,
                                             GameController gameController,
                                             GameState gameState) {
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

        return new GameOverData(pieces, lines, finalTime, timeStr, score, gameState.getCurrentGameMode());
    }

    /**
     * Displays the game-over panel using the supplied data and assigns retry
     * and main-menu actions.
     *
     * @param data the data to display on the game-over screen
     */
    private void showGameOverPanel(GameOverData data) {
        GameOverPanel gameOverPanel = controller.getGameOverPanel();

        gameOverPanel.showGameOver(
                data.pieces, data.lines, data.finalTime,
                data.timeStr, data.score, data.gameMode
        );

        gameOverPanel.setOnRetry(this::restartWithCountdown);
        gameOverPanel.setOnMainMenu(controller.getNavigationHandler()::goToMainMenu);
    }

    private void restartWithCountdown() {
        controller.getFlowManager().restartWithCountdown(() -> {
            controller.getCompletionManager().removeCompletionPanels(getRootPane());
            ComboMeterPanel comboMeterPanel = controller.getComboMeterPanel();
            if (comboMeterPanel != null) {
                comboMeterPanel.reset();
            }
        });
    }

    /**
     * Returns the application's root container.
     *
     * @return the root {@link StackPane}
     */
    private StackPane getRootPane() {
        return controller.getNavigationHandler().getRootPane();
    }

    private static class GameOverData {
        final int pieces;
        final int lines;
        final long finalTime;
        final String timeStr;
        final int score;
        final GameMode gameMode;

        /**
         * Creates a new container for game-over results.
         *
         * @param pieces   number of pieces placed
         * @param lines    number of lines cleared
         * @param finalTime elapsed time in milliseconds
         * @param timeStr  formatted time string
         * @param score    final score
         * @param gameMode the mode that was played
         */
        GameOverData(int pieces, int lines, long finalTime, String timeStr, int score, GameMode gameMode) {
            this.pieces = pieces;
            this.lines = lines;
            this.finalTime = finalTime;
            this.timeStr = timeStr;
            this.score = score;
            this.gameMode = gameMode;
        }
    }
}