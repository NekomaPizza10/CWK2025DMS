package com.comp2042.controller;

import com.comp2042.core.Board;
import com.comp2042.core.SimpleBoard;
import com.comp2042.model.*;
import com.comp2042.event.*;
import com.comp2042.ui.GuiController;

import java.util.List;

public class GameController implements InputEventListener {

    private Board board = new SimpleBoard(10, 25);
    private int piecesPlaced = 0;
    private final GuiController viewGuiController;

    public GameController(GuiController c) {
        viewGuiController = c;
        board.createNewBrick();
        viewGuiController.setEventListener(this);
        viewGuiController.initGameView(board.getBoardMatrix(), board.getViewData());
        viewGuiController.bindScore(board.getScore().scoreProperty());
        viewGuiController.updateNextDisplay();
    }

    public Board getBoard() { return this.board; }

    @Override
    public DownData onDownEvent(MoveEvent event) {
        boolean canMove = board.moveBrickDown();
        if (canMove && event.getEventSource() == EventSource.USER) { board.getScore().add(1); }
        return new DownData(null, board.getViewData());
    }

    @Override
    public ViewData onLeftEvent(MoveEvent event) {
        board.moveBrickLeft();
        return board.getViewData();
    }

    @Override
    public ViewData onRightEvent(MoveEvent event) {
        board.moveBrickRight();
        return board.getViewData();
    }

    @Override
    public ViewData onRotateEvent(MoveEvent event) {
        board.rotateLeftBrick();
        return board.getViewData();
    }

    @Override
    public void createNewGame() {
        piecesPlaced = 0;
        board.newGame();
        viewGuiController.refreshGameBackground(board.getBoardMatrix());
    }

    public boolean holdBrick() {
        board.holdCurrentBrick();
        viewGuiController.refreshGameBackground(board.getBoardMatrix());
        return true;
    }

    public int[][] getHoldBrickData() { return board.getHoldBrickData(); }

    public List<int[][]> getNextBricksData() { return board.getNextBricksData(5); }

    public int getPiecesPlaced() { return piecesPlaced; }

    public void incrementPiecesPlaced() { piecesPlaced++; }

    public int getLinesCleared() { return board.getLinesCleared(); }

    public boolean hardDrop() {
        while (board.moveBrickDown()) { }
        board.mergeBrickToBackground();
        ClearRow clearRow = board.clearRows();
        if (board.createNewBrick()) {
            viewGuiController.gameOver();
            return false;
        }
        viewGuiController.refreshGameBackground(board.getBoardMatrix());
        return true;
    }
}