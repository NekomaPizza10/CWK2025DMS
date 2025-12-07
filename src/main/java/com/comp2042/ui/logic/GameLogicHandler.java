package com.comp2042.ui.logic;

import com.comp2042.controller.GameController;
import com.comp2042.event.*;
import com.comp2042.state.*;
import com.comp2042.ui.render.GameRenderer;
import com.comp2042.ui.handlers.UIUpdater;
import com.comp2042.ui.effect.BoardGlowEffect;
import com.comp2042.ui.effect.ComboAnimationManager;
import com.comp2042.ui.effect.ComboMeterPanel;

/**
 * Main coordinator for game logic operations.
 * Delegates specific functionality to specialized handlers.
 */
public class GameLogicHandler {

    private final GameState gameState;
    private final TimerManager timerManager;
    private final ScoringManager scoringManager;
    private final GameRenderer renderer;
    private final UIUpdater uiUpdater;

    private GameController gameController;
    private boolean isDisposed = false;

    private BrickMovementHandler movementHandler;
    private BrickLockHandler lockHandler;
    private ComboEffectHandler comboHandler;
    private GameProgressHandler progressHandler;
    private ShadowCalculator shadowCalculator;

    private Runnable onGameOver;
    private Runnable onChallengeComplete40Lines;
    private Runnable onChallengeComplete2Minutes;
    private Runnable onUpdateNextDisplay;

    /**
     * Creates a new {@code GameLogicHandler} and initializes all sub-handlers required
     * for gameplay, including movement, locking, scoring, progress tracking, and combo effects.
     *
     * @param gameState     the active game state containing board, brick, and gameplay data
     * @param timerManager  manages game timers and tick intervals
     * @param scoringManager handles score calculation and combo logic
     * @param renderer      responsible for rendering the game board and bricks
     * @param uiUpdater     handles UI updates related to the current game state
     */
    public GameLogicHandler(GameState gameState, TimerManager timerManager,
                            ScoringManager scoringManager, GameRenderer renderer, UIUpdater uiUpdater) {
        this.gameState = gameState;
        this.timerManager = timerManager;
        this.scoringManager = scoringManager;
        this.renderer = renderer;
        this.uiUpdater = uiUpdater;

        initializeHandlers();
    }

    private void initializeHandlers() {
        shadowCalculator = new ShadowCalculator();
        comboHandler = new ComboEffectHandler();
        progressHandler = new GameProgressHandler(gameState, timerManager, scoringManager, uiUpdater);
        movementHandler = new BrickMovementHandler(gameState, scoringManager, renderer, uiUpdater, shadowCalculator);
        lockHandler = new BrickLockHandler(gameState, timerManager, renderer, uiUpdater,
                scoringManager, progressHandler, comboHandler, shadowCalculator);

        // Set cross-references
        movementHandler.setLockHandler(lockHandler);
        lockHandler.setMovementHandler(movementHandler);
    }

    /**
     * Associates the logic handler with the main {@link GameController}, enabling callbacks
     * and communication between UI and logic.
     *
     * @param gc the main game controller
     */
    public void setGameController(GameController gc) {
        this.gameController = gc;
        movementHandler.setGameController(gc);
        lockHandler.setGameController(gc);
        progressHandler.setGameController(gc);
    }

    /**
     * Sets the callback to be executed when the game reaches a game-over state.
     *
     * @param r the runnable to execute on game over
     */
    public void setOnGameOver(Runnable r) {
        this.onGameOver = r;
        lockHandler.setOnGameOver(r);
    }

    /**
     * Sets the callback executed when the 40-line challenge is completed.
     *
     * @param r the runnable triggered upon challenge completion
     */
    public void setOnChallengeComplete40Lines(Runnable r) {
        this.onChallengeComplete40Lines = r;
        progressHandler.setOnChallengeComplete40Lines(r);
    }

    /**
     * Sets the callback executed when the 2-minute challenge timer expires.
     *
     * @param r the runnable triggered at challenge completion
     */
    public void setOnChallengeComplete2Minutes(Runnable r) {
        this.onChallengeComplete2Minutes = r;
        progressHandler.setOnChallengeComplete2Minutes(r);
    }

    /**
     * Sets the callback responsible for updating the "next piece" display.
     *
     * @param r the runnable to execute when the next preview needs updating
     */
    public void setOnUpdateNextDisplay(Runnable r) {
        this.onUpdateNextDisplay = r;
        lockHandler.setOnUpdateNextDisplay(r);
        progressHandler.setOnUpdateNextDisplay(r);
    }

    /**
     * Assigns combo-related visual components to the internal combo handler.
     *
     * @param anim the animation manager for combo text and screen effects
     * @param meter the combo meter panel
     * @param glow the board glow effect for high combos
     */
    public void setComboComponents(ComboAnimationManager anim, ComboMeterPanel meter, BoardGlowEffect glow) {
        comboHandler.setComponents(anim, meter, glow);
    }

    /**
     * Attempts to move the active brick horizontally.
     *
     * @param dir direction of movement: -1 for left, +1 for right
     * @return {@code true} if the brick successfully moved, {@code false} otherwise
     */
    public boolean moveBrickHorizontally(int dir) {
        if (isDisposed) return false;
        return movementHandler.moveBrickHorizontally(dir);
    }

    /**
     * Attempts to rotate the active brick if space permits.
     *
     * @return {@code true} if rotation succeeds, {@code false} otherwise
     */
    public boolean attemptRotation() {
        if (isDisposed) return false;
        return movementHandler.attemptRotation();
    }

    /**
     * Handles downward movement of the active brick in response to a timed or user-triggered event.
     *
     * @param event the movement event containing timing and context information
     */
    public void moveDown(MoveEvent event) {
        if (isDisposed) return;
        movementHandler.moveDown(event);
    }

    public void handleHardDrop() {
        if (isDisposed) return;
        movementHandler.handleHardDrop();
    }

    /**
     * Handles hold input, swapping the current brick with the hold slot.
     *
     * @param callback a runnable executed after the hold action completes
     */
    public void handleHold(Runnable callback) {
        if (isDisposed) return;
        movementHandler.handleHold(callback);
    }

    public void resetLockDelay() {
        lockHandler.resetLockDelay();
    }

    public void cancelLockDelay() {
        lockHandler.cancelLockDelay();
    }

    public void pauseComboDecay() {
        comboHandler.pauseDecay();
    }

    public void resumeComboDecay() {
        comboHandler.resumeDecay();
    }

    /**
     * Checks whether the active brick was just spawned and has not yet moved.
     *
     * @return {@code true} if the brick is in its initial spawned state; {@code false} otherwise
     */
    public boolean isPieceJustSpawned() {
        return movementHandler.isPieceJustSpawned();
    }

}