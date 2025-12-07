package com.comp2042.ui.initialization;

import com.comp2042.controller.*;
import com.comp2042.model.GameMode;
import com.comp2042.model.ViewData;
import com.comp2042.state.*;
import com.comp2042.ui.effect.ComboMeterPanel;
import com.comp2042.ui.handlers.*;
import com.comp2042.ui.logic.GameLogicHandler;
import com.comp2042.ui.manager.ChallengeCompletionManager;
import com.comp2042.ui.manager.GameFlowManager;
import com.comp2042.ui.panel.GameOverPanel;
import com.comp2042.ui.panel.PauseMenuPanel;
import com.comp2042.ui.render.*;
import javafx.beans.property.IntegerProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.layout.*;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Main GUI Controller - Minimal coordinator for UI components.
 * Delegates all logic to specialized handlers.
 */
public class GuiController implements Initializable {

    // FXML Injected Fields
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

    // Core Components
    private GameState gameState;
    private GameRenderer renderer;
    private InputHandler inputHandler;
    private UIUpdater uiUpdater;
    private GameController gameController;

    // Managers
    private TimerManager timerManager;
    private ScoringManager scoringManager;
    private GameLogicHandler logicHandler;
    private GameFlowManager flowManager;
    private ChallengeCompletionManager completionManager;

    // Handlers
    private ComponentInitializer componentInitializer;
    private InputCallbackHandler inputCallbackHandler;
    private UISetupHandler uiSetupHandler;
    private NavigationHandler navigationHandler;
    private GameViewController gameViewController;

    /**
     * Called automatically by JavaFX when the FXML is loaded.
     * Initializes handlers and prepares UI components.
     *
     * @param location  the URL used to resolve relative paths (unused)
     * @param resources the resource bundle for localization (unused)
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initializeHandlers();
        componentInitializer.initializeComponents();
        inputCallbackHandler.setupInputCallbacks();
        uiSetupHandler.setupUI();
    }

    private void initializeHandlers() {
        componentInitializer = new ComponentInitializer(this);
        inputCallbackHandler = new InputCallbackHandler(this);
        uiSetupHandler = new UISetupHandler(this);
        navigationHandler = new NavigationHandler(this);
        gameViewController = new GameViewController(this);
    }

    /**
     * Applies the selected game mode to the UI and internal components.
     *
     * @param mode the selected game mode (Normal, 40 Lines, 2 Minutes)
     */
    public void setGameMode(GameMode mode) {
        uiSetupHandler.configureGameMode(mode);
    }

    /**
     * Initializes the complete game view, including board rendering,
     * preview panels, timers, and callbacks.
     *
     * @param boardMatrix the matrix representing the game board
     * @param brick       the currently active brick's view data
     */
    public void initGameView(int[][] boardMatrix, ViewData brick) {
        gameViewController.initGameView(boardMatrix, brick);
    }

    /**
     * Updates the preview panels that display the upcoming bricks.
     * Delegated to {@link GameViewController}.
     */
    public void updateNextDisplay() {
        gameViewController.updateNextDisplay();
    }

    /**
     * Triggers game over handling and displays the game over panel.
     * Delegated to {@link GameViewController}.
     */
    public void gameOver() {
        gameViewController.handleGameOver();
    }

    /**
     * Refreshes the game's background based on the current board matrix.
     *
     * @param boardMatrix the current game board state
     */
    public void refreshGameBackground(int[][] boardMatrix) {
        renderer.refreshGameBackground(boardMatrix);
    }

    /**
     * Registers the game controller as the input event listener and propagates it
     * to relevant handlers and managers.
     *
     * @param eventListener the event listener (expected to be a GameController)
     */
    public void setEventListener(InputEventListener eventListener) {
        this.gameController = (GameController) eventListener;
        if (logicHandler != null) logicHandler.setGameController(gameController);
        if (flowManager != null) flowManager.setGameController(gameController);
        if (completionManager != null) completionManager.setGameController(gameController);
    }

    /**
     * Binds a score property to the UI (reserved for future enhancement).
     *
     * @param integerProperty the score property to bind
     */
    public void bindScore(IntegerProperty integerProperty) {
        // Reserved for score binding implementation
    }

    // ==================== PUBLIC GETTERS FOR FXML COMPONENTS ====================

    /** @return the main game board panel */
    public GridPane getGamePanel() { return gamePanel; }

    /** @return the active brick display panel */
    public GridPane getBrickPanel() { return brickPanel; }

    /** @return the hold brick display panel */
    public GridPane getHoldPanel() { return holdPanel; }

    /**
     * Returns an array containing all "next" preview panels.
     *
     * @return array of preview panels for upcoming bricks
     */
    public GridPane[] getNextPanels() {
        return new GridPane[]{nextPanel1, nextPanel2, nextPanel3, nextPanel4, nextPanel5};
    }

    /** @return the game over panel */
    public GameOverPanel getGameOverPanel() { return gameOverPanel; }

    /** @return the pause menu panel */
    public PauseMenuPanel getPauseMenuPanel() { return pauseMenuPanel; }

    /** @return the countdown overlay panel */
    public StackPane getCountdownPanel() { return countdownPanel; }

    /** @return the label used to display countdown values */
    public Label getCountdownLabel() { return countdownLabel; }

    /** @return the label showing elapsed time */
    public Label getTimeValue() { return timeValue; }

    /** @return the label showing the player's score */
    public Label getScoreValue() { return scoreValue; }

    /** @return the label showing the best score */
    public Label getBestScoreValue() { return bestScoreValue; }

    /** @return the label showing the best recorded time */
    public Label getBestTimeLabel() { return bestTimeLabel; }

    /** @return the label showing pieces placed */
    public Label getPiecesValue() { return piecesValue; }

    /** @return the label showing lines cleared */
    public Label getLinesValue() { return linesValue; }

    /** @return the label for "Lines" title */
    public Label getLinesLabel() { return linesLabel; }

    /** @return the label for "Time" title */
    public Label getTimeLabel() { return timeLabel; }

    /** @return container holding score panels */
    public VBox getScoreDisplayContainer() { return scoreDisplayContainer; }

    /** @return score box panel */
    public VBox getScoreBox() { return scoreBox; }

    /** @return best score box panel */
    public VBox getBestScoreBox() { return bestScoreBox; }

    /** @return best time box panel */
    public VBox getBestTimeBox() { return bestTimeBox; }

    /** @return separator between score sections */
    public Region getScoreSeparator() { return scoreSeparator; }

    /** @return the effects layer used for animations (combo, glow, etc) */
    public Pane getEffectsLayer() { return effectsLayer; }

    /** @return the combo meter panel */
    public ComboMeterPanel getComboMeterPanel() { return comboMeterPanel; }

    // ==================== PUBLIC GETTERS FOR CORE COMPONENTS ====================

    /** @return current game state instance */
    public GameState getGameState() { return gameState; }

    /** @return renderer instance used for drawing tiles and panels */
    public GameRenderer getRenderer() { return renderer; }

    /** @return input handler instance */
    public InputHandler getInputHandler() { return inputHandler; }

    /** @return UI updater used for binding and updating label values */
    public UIUpdater getUiUpdater() { return uiUpdater; }

    /** @return game controller providing gameplay logic */
    public GameController getGameController() { return gameController; }


    // ==================== PUBLIC GETTERS FOR MANAGERS ====================

    /** @return timer manager for timing and countdown functions */
    public TimerManager getTimerManager() { return timerManager; }

    /** @return scoring manager for score calculations */
    public ScoringManager getScoringManager() { return scoringManager; }

    /** @return logic handler for gameplay processing */
    public GameLogicHandler getLogicHandler() { return logicHandler; }

    /** @return game flow manager controlling pauses, countdowns, restarts */
    public GameFlowManager getFlowManager() { return flowManager; }

    /** @return challenge completion manager for 40-line and 2-minute modes */
    public ChallengeCompletionManager getCompletionManager() { return completionManager; }

    // ==================== PUBLIC GETTERS FOR HANDLERS ====================

    /** @return navigation handler for scene transitions */
    public NavigationHandler getNavigationHandler() { return navigationHandler; }

    /** @return game view controller for setting up the game screen */
    public GameViewController getGameViewController() { return gameViewController; }


    // ==================== PUBLIC SETTERS FOR DEPENDENCY INJECTION ====================

    /**
     * Sets the game state object.
     *
     * @param gameState the game state instance
     */
    public void setGameState(GameState gameState) { this.gameState = gameState; }

    /**
     * Sets the renderer used to draw the game board and UI components.
     *
     * @param renderer the game renderer
     */
    public void setRenderer(GameRenderer renderer) { this.renderer = renderer; }

    /**
     * Sets the input handler responsible for mapping key events.
     *
     * @param inputHandler the input handler
     */
    public void setInputHandler(InputHandler inputHandler) { this.inputHandler = inputHandler; }

    /**
     * Sets the UI updater responsible for binding and updating values.
     *
     * @param uiUpdater the UI updater instance
     */
    public void setUiUpdater(UIUpdater uiUpdater) { this.uiUpdater = uiUpdater; }

    /**
     * Sets the timer manager responsible for game countdowns and time tracking.
     *
     * @param timerManager the timer manager instance
     */
    public void setTimerManager(TimerManager timerManager) { this.timerManager = timerManager; }

    /**
     * Sets the scoring manager for score calculations.
     *
     * @param scoringManager the scoring manager instance
     */
    public void setScoringManager(ScoringManager scoringManager) { this.scoringManager = scoringManager; }

    /**
     * Sets the logic handler for gameplay logic operations.
     *
     * @param logicHandler the logic handler
     */
    public void setLogicHandler(GameLogicHandler logicHandler) { this.logicHandler = logicHandler; }

    /**
     * Sets the game flow manager controlling pauses, countdowns, and restarts.
     *
     * @param flowManager the game flow manager
     */
    public void setFlowManager(GameFlowManager flowManager) { this.flowManager = flowManager; }

    /**
     * Sets the completion manager for challenge modes.
     *
     * @param completionManager the challenge completion manager
     */
    public void setCompletionManager(ChallengeCompletionManager completionManager) {
        this.completionManager = completionManager;
    }
}