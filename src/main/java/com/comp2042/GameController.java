package com.comp2042;

import java.util.List;

public class GameController implements InputEventListener {

    private Board board = new SimpleBoard(10, 27);

    private final GuiController viewGuiController;

    public GameController(GuiController c) {
        viewGuiController = c;
        board.createNewBrick();
        viewGuiController.setEventListener(this);
        viewGuiController.initGameView(board.getBoardMatrix(), board.getViewData());
        viewGuiController.bindScore(board.getScore().scoreProperty());
        viewGuiController.updateNextDisplay();
    }


    public Board getBoard() {
        return this.board;
    }

    @Override
    public DownData onDownEvent(MoveEvent event) {
        boolean canMove = board.moveBrickDown();

        if (canMove && event.getEventSource() == EventSource.USER) {
            board.getScore().add(1);
        }

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
        board.newGame();
        viewGuiController.refreshGameBackground(board.getBoardMatrix());
    }

    //HOLD Function
    public boolean holdBrick() {
        board.holdCurrentBrick();
        viewGuiController.refreshGameBackground(board.getBoardMatrix());
        return true;
    }

    public int[][] getHoldBrickData() {
        return board.getHoldBrickData();
    }

    //NEXT PREVIEW Function
    public List<int[][]> getNextBricksData() {
        return board.getNextBricksData(5);
    }

    public int getPiecesPlaced() {
        return board.getPiecesPlaced();
    }

    public int getLinesCleared() {
        return board.getLinesCleared();
    }

    // Hard Drop function
    public boolean hardDrop() {
        // Drop brick to bottom instantly
        while (board.moveBrickDown()) {
            // Keep moving down until it can't move anymore
        }

        // Lock the brick
        board.mergeBrickToBackground();
        ClearRow clearRow = board.clearRows();

        // Spawn next brick
        if (board.createNewBrick()) {
            viewGuiController.gameOver();
            return false;
        }

        viewGuiController.refreshGameBackground(board.getBoardMatrix());
        return true;
    }
}
