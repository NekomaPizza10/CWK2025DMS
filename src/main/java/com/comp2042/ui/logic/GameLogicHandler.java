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

    public void setGameController(GameController gc) {
        this.gameController = gc;
        movementHandler.setGameController(gc);
        lockHandler.setGameController(gc);
        progressHandler.setGameController(gc);
    }

    public void setOnGameOver(Runnable r) {
        this.onGameOver = r;
        lockHandler.setOnGameOver(r);
    }

    public void setOnChallengeComplete40Lines(Runnable r) {
        this.onChallengeComplete40Lines = r;
        progressHandler.setOnChallengeComplete40Lines(r);
    }

    public void setOnChallengeComplete2Minutes(Runnable r) {
        this.onChallengeComplete2Minutes = r;
        progressHandler.setOnChallengeComplete2Minutes(r);
    }

    public void setOnUpdateNextDisplay(Runnable r) {
        this.onUpdateNextDisplay = r;
        lockHandler.setOnUpdateNextDisplay(r);
        progressHandler.setOnUpdateNextDisplay(r);
    }

    public void setComboComponents(ComboAnimationManager anim, ComboMeterPanel meter, BoardGlowEffect glow) {
        comboHandler.setComponents(anim, meter, glow);
    }

    public boolean moveBrickHorizontally(int dir) {
        if (isDisposed) return false;
        return movementHandler.moveBrickHorizontally(dir);
    }

    public boolean attemptRotation() {
        if (isDisposed) return false;
        return movementHandler.attemptRotation();
    }

    public void moveDown(MoveEvent event) {
        if (isDisposed) return;
        movementHandler.moveDown(event);
    }

    public void handleHardDrop() {
        if (isDisposed) return;
        movementHandler.handleHardDrop();
    }

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

    public boolean isPieceJustSpawned() {
        return movementHandler.isPieceJustSpawned();
    }

}