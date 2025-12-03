package com.comp2042.ui;

import com.comp2042.controller.InputEventListener;
import com.comp2042.model.ViewData;
import com.comp2042.ui.initialization.GuiController;
import javafx.beans.property.IntegerProperty;

/**
 * Stub GuiController for testing without JavaFX dependencies.
 * All methods do nothing - just allows GameController to be instantiated.
 */
public class StubGuiController extends GuiController {

    private boolean gameOverCalled = false;
    private boolean refreshCalled = false;
    private boolean initGameViewCalled = false;
    private boolean updateNextDisplayCalled = false;
    private boolean setEventListenerCalled = false;
    private int[][] lastBoardMatrix = null;

    // ========== Override Methods ==========

    @Override
    public void initialize(java.net.URL location, java.util.ResourceBundle resources) {
        // Do nothing - avoid JavaFX initialization
    }

    @Override
    public void setEventListener(InputEventListener listener) {
        this.setEventListenerCalled = true;
        // Do nothing else - avoid JavaFX initialization
    }

    @Override
    public void initGameView(int[][] boardMatrix, ViewData brick) {
        this.initGameViewCalled = true;
        // Do nothing else - avoid JavaFX initialization
    }

    @Override
    public void bindScore(IntegerProperty prop) {
        // Do nothing
    }

    @Override
    public void updateNextDisplay() {
        this.updateNextDisplayCalled = true;
    }

    @Override
    public void refreshGameBackground(int[][] boardMatrix) {
        this.refreshCalled = true;
        this.lastBoardMatrix = boardMatrix;
    }

    @Override
    public void gameOver() {
        this.gameOverCalled = true;
    }

    // ========== Test Helper Methods ==========

    public boolean wasGameOverCalled() {
        return gameOverCalled;
    }

    public boolean wasRefreshCalled() {
        return refreshCalled;
    }

    public boolean wasInitGameViewCalled() {
        return initGameViewCalled;
    }

    public boolean wasUpdateNextDisplayCalled() {
        return updateNextDisplayCalled;
    }

    public boolean wasSetEventListenerCalled() {
        return setEventListenerCalled;
    }

    public int[][] getLastBoardMatrix() {
        return lastBoardMatrix;
    }

    public void reset() {
        gameOverCalled = false;
        refreshCalled = false;
        initGameViewCalled = false;
        updateNextDisplayCalled = false;
        setEventListenerCalled = false;
        lastBoardMatrix = null;
    }
}