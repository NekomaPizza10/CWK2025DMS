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

    public void setGameMode(GameMode mode) {
        uiSetupHandler.configureGameMode(mode);
    }

    public void initGameView(int[][] boardMatrix, ViewData brick) {
        gameViewController.initGameView(boardMatrix, brick);
    }

    public void updateNextDisplay() {
        gameViewController.updateNextDisplay();
    }

    public void gameOver() {
        gameViewController.handleGameOver();
    }

    public void refreshGameBackground(int[][] boardMatrix) {
        renderer.refreshGameBackground(boardMatrix);
    }

    public void setEventListener(InputEventListener eventListener) {
        this.gameController = (GameController) eventListener;
        if (logicHandler != null) logicHandler.setGameController(gameController);
        if (flowManager != null) flowManager.setGameController(gameController);
        if (completionManager != null) completionManager.setGameController(gameController);
    }

    public void bindScore(IntegerProperty integerProperty) {
        // Reserved for score binding implementation
    }

    // ==================== PUBLIC GETTERS FOR FXML COMPONENTS ====================

    public GridPane getGamePanel() { return gamePanel; }
    public GridPane getBrickPanel() { return brickPanel; }
    public GridPane getHoldPanel() { return holdPanel; }
    public GridPane[] getNextPanels() {
        return new GridPane[]{nextPanel1, nextPanel2, nextPanel3, nextPanel4, nextPanel5};
    }
    public GameOverPanel getGameOverPanel() { return gameOverPanel; }
    public PauseMenuPanel getPauseMenuPanel() { return pauseMenuPanel; }
    public StackPane getCountdownPanel() { return countdownPanel; }
    public Label getCountdownLabel() { return countdownLabel; }
    public Label getTimeValue() { return timeValue; }
    public Label getScoreValue() { return scoreValue; }
    public Label getBestScoreValue() { return bestScoreValue; }
    public Label getBestTimeLabel() { return bestTimeLabel; }
    public Label getPiecesValue() { return piecesValue; }
    public Label getLinesValue() { return linesValue; }
    public Label getLinesLabel() { return linesLabel; }
    public Label getTimeLabel() { return timeLabel; }
    public VBox getScoreDisplayContainer() { return scoreDisplayContainer; }
    public VBox getScoreBox() { return scoreBox; }
    public VBox getBestScoreBox() { return bestScoreBox; }
    public VBox getBestTimeBox() { return bestTimeBox; }
    public Region getScoreSeparator() { return scoreSeparator; }
    public Pane getEffectsLayer() { return effectsLayer; }
    public ComboMeterPanel getComboMeterPanel() { return comboMeterPanel; }

    // ==================== PUBLIC GETTERS FOR CORE COMPONENTS ====================

    public GameState getGameState() { return gameState; }
    public GameRenderer getRenderer() { return renderer; }
    public InputHandler getInputHandler() { return inputHandler; }
    public UIUpdater getUiUpdater() { return uiUpdater; }
    public GameController getGameController() { return gameController; }

    // ==================== PUBLIC GETTERS FOR MANAGERS ====================

    public TimerManager getTimerManager() { return timerManager; }
    public ScoringManager getScoringManager() { return scoringManager; }
    public GameLogicHandler getLogicHandler() { return logicHandler; }
    public GameFlowManager getFlowManager() { return flowManager; }
    public ChallengeCompletionManager getCompletionManager() { return completionManager; }

    // ==================== PUBLIC GETTERS FOR HANDLERS ====================

    public NavigationHandler getNavigationHandler() { return navigationHandler; }
    public GameViewController getGameViewController() { return gameViewController; }

    // ==================== PUBLIC SETTERS FOR DEPENDENCY INJECTION ====================

    public void setGameState(GameState gameState) { this.gameState = gameState; }
    public void setRenderer(GameRenderer renderer) { this.renderer = renderer; }
    public void setInputHandler(InputHandler inputHandler) { this.inputHandler = inputHandler; }
    public void setUiUpdater(UIUpdater uiUpdater) { this.uiUpdater = uiUpdater; }
    public void setTimerManager(TimerManager timerManager) { this.timerManager = timerManager; }
    public void setScoringManager(ScoringManager scoringManager) { this.scoringManager = scoringManager; }
    public void setLogicHandler(GameLogicHandler logicHandler) { this.logicHandler = logicHandler; }
    public void setFlowManager(GameFlowManager flowManager) { this.flowManager = flowManager; }
    public void setCompletionManager(ChallengeCompletionManager completionManager) {
        this.completionManager = completionManager;
    }
}