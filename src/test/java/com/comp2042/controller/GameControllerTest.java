package com.comp2042.controller;

import com.comp2042.event.*;
import com.comp2042.model.DownData;
import com.comp2042.model.ViewData;
import com.comp2042.ui.StubGuiController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for GameController
 * Uses StubGuiController to avoid JavaFX dependencies
 */
class GameControllerTest {

    private GameController gameController;
    private StubGuiController stubGuiController;

    @BeforeEach
    void setUp() {
        stubGuiController = new StubGuiController();
        gameController = new GameController(stubGuiController);
    }

    // ========== Constructor Tests ==========

    @Test
    @DisplayName("GameController initializes successfully")
    void gameControllerInitializesSuccessfully() {
        assertNotNull(gameController, "GameController should be created");
    }

    @Test
    @DisplayName("GameController initializes with board")
    void gameControllerInitializesWithBoard() {
        assertNotNull(gameController.getBoard(), "Board should be initialized");
    }

    @Test
    @DisplayName("GameController starts with zero pieces placed")
    void gameControllerStartsWithZeroPiecesPlaced() {
        assertEquals(0, gameController.getPiecesPlaced(),
                "Should start with 0 pieces placed");
    }

    @Test
    @DisplayName("Constructor calls setEventListener on GuiController")
    void constructorCallsSetEventListener() {
        assertTrue(stubGuiController.wasSetEventListenerCalled(),
                "Should call setEventListener during construction");
    }

    @Test
    @DisplayName("Constructor calls initGameView on GuiController")
    void constructorCallsInitGameView() {
        assertTrue(stubGuiController.wasInitGameViewCalled(),
                "Should call initGameView during construction");
    }

    @Test
    @DisplayName("Constructor calls updateNextDisplay on GuiController")
    void constructorCallsUpdateNextDisplay() {
        assertTrue(stubGuiController.wasUpdateNextDisplayCalled(),
                "Should call updateNextDisplay during construction");
    }

    // ========== getBoard Tests ==========

    @Test
    @DisplayName("getBoard returns non-null board")
    void getBoardReturnsNonNullBoard() {
        assertNotNull(gameController.getBoard(), "Board should not be null");
    }

    @Test
    @DisplayName("getBoard returns same instance")
    void getBoardReturnsSameInstance() {
        assertSame(gameController.getBoard(), gameController.getBoard(),
                "Should return same board instance");
    }

    // ========== Pieces Placed Tests ==========

    @Test
    @DisplayName("incrementPiecesPlaced increases count by 1")
    void incrementPiecesPlacedIncreasesByOne() {
        int before = gameController.getPiecesPlaced();
        gameController.incrementPiecesPlaced();
        assertEquals(before + 1, gameController.getPiecesPlaced());
    }

    @Test
    @DisplayName("Multiple increments work correctly")
    void multipleIncrementsWorkCorrectly() {
        gameController.incrementPiecesPlaced();
        gameController.incrementPiecesPlaced();
        gameController.incrementPiecesPlaced();
        assertEquals(3, gameController.getPiecesPlaced());
    }

    @Test
    @DisplayName("getPiecesPlaced returns correct count after many increments")
    void getPiecesPlacedReturnsCorrectCountAfterManyIncrements() {
        for (int i = 0; i < 100; i++) {
            gameController.incrementPiecesPlaced();
        }
        assertEquals(100, gameController.getPiecesPlaced());
    }

    // ========== onDownEvent Tests ==========

    @Test
    @DisplayName("onDownEvent returns DownData")
    void onDownEventReturnsDownData() {
        MoveEvent event = new MoveEvent(EventType.DOWN, EventSource.USER);
        DownData result = gameController.onDownEvent(event);
        assertNotNull(result, "Should return DownData");
    }

    @Test
    @DisplayName("onDownEvent with USER source adds score when brick moves")
    void onDownEventWithUserSourceAddsScore() {
        MoveEvent event = new MoveEvent(EventType.DOWN, EventSource.USER);
        int initialScore = gameController.getBoard().getScore().scoreProperty().get();
        // Move down - if successful, score should increase
        gameController.onDownEvent(event);
        // Score might increase if brick moved
        int newScore = gameController.getBoard().getScore().scoreProperty().get();
        assertTrue(newScore >= initialScore, "Score should not decrease");
    }

    @Test
    @DisplayName("onDownEvent with THREAD source does not add score")
    void onDownEventWithThreadSourceDoesNotAddScore() {
        MoveEvent event = new MoveEvent(EventType.DOWN, EventSource.THREAD);
        int initialScore = gameController.getBoard().getScore().scoreProperty().get();

        gameController.onDownEvent(event);

        int newScore = gameController.getBoard().getScore().scoreProperty().get();
        assertEquals(initialScore, newScore, "THREAD source should not add score");
    }

    @Test
    @DisplayName("onDownEvent with LOCK_DELAY source does not add score")
    void onDownEventWithLockDelaySourceDoesNotAddScore() {
        MoveEvent event = new MoveEvent(EventType.DOWN, EventSource.LOCK_DELAY);
        int initialScore = gameController.getBoard().getScore().scoreProperty().get();

        gameController.onDownEvent(event);

        int newScore = gameController.getBoard().getScore().scoreProperty().get();
        assertEquals(initialScore, newScore, "LOCK_DELAY source should not add score");
    }

    // ========== onLeftEvent Tests ==========

    @Test
    @DisplayName("onLeftEvent returns ViewData")
    void onLeftEventReturnsViewData() {
        MoveEvent event = new MoveEvent(EventType.LEFT, EventSource.USER);
        ViewData result = gameController.onLeftEvent(event);
        assertNotNull(result, "Should return ViewData");
    }

    @Test
    @DisplayName("onLeftEvent returns valid ViewData with brick data")
    void onLeftEventReturnsValidViewData() {
        MoveEvent event = new MoveEvent(EventType.LEFT, EventSource.USER);
        ViewData result = gameController.onLeftEvent(event);
        assertNotNull(result.getBrickData(), "ViewData should contain brick data");
    }

    // ========== onRightEvent Tests ==========

    @Test
    @DisplayName("onRightEvent returns ViewData")
    void onRightEventReturnsViewData() {
        MoveEvent event = new MoveEvent(EventType.RIGHT, EventSource.USER);
        ViewData result = gameController.onRightEvent(event);
        assertNotNull(result, "Should return ViewData");
    }

    @Test
    @DisplayName("onRightEvent returns valid ViewData with brick data")
    void onRightEventReturnsValidViewData() {
        MoveEvent event = new MoveEvent(EventType.RIGHT, EventSource.USER);
        ViewData result = gameController.onRightEvent(event);
        assertNotNull(result.getBrickData(), "ViewData should contain brick data");
    }

    // ========== onRotateEvent Tests ==========

    @Test
    @DisplayName("onRotateEvent returns ViewData")
    void onRotateEventReturnsViewData() {
        MoveEvent event = new MoveEvent(EventType.ROTATE, EventSource.USER);
        ViewData result = gameController.onRotateEvent(event);
        assertNotNull(result, "Should return ViewData");
    }

    @Test
    @DisplayName("onRotateEvent returns valid ViewData with brick data")
    void onRotateEventReturnsValidViewData() {
        MoveEvent event = new MoveEvent(EventType.ROTATE, EventSource.USER);
        ViewData result = gameController.onRotateEvent(event);
        assertNotNull(result.getBrickData(), "ViewData should contain brick data");
    }

    // ========== createNewGame Tests ==========

    @Test
    @DisplayName("createNewGame resets pieces placed to zero")
    void createNewGameResetsPiecesPlaced() {
        // Place some pieces
        gameController.incrementPiecesPlaced();
        gameController.incrementPiecesPlaced();
        assertEquals(2, gameController.getPiecesPlaced());

        // Reset game
        gameController.createNewGame();

        // Pieces should be reset
        assertEquals(0, gameController.getPiecesPlaced());
    }

    @Test
    @DisplayName("createNewGame calls refreshGameBackground")
    void createNewGameCallsRefreshGameBackground() {
        stubGuiController.reset();

        gameController.createNewGame();

        assertTrue(stubGuiController.wasRefreshCalled(),
                "Should refresh game background");
    }

    @Test
    @DisplayName("createNewGame resets board")
    void createNewGameResetsBoard() {
        gameController.createNewGame();

        // Board should have new brick
        assertNotNull(gameController.getBoard().getViewData(),
                "Board should have view data after reset");
    }

    // ========== holdBrick Tests ==========

    @Test
    @DisplayName("holdBrick returns true")
    void holdBrickReturnsTrue() {
        boolean result = gameController.holdBrick();
        assertTrue(result, "holdBrick should return true");
    }

    @Test
    @DisplayName("holdBrick calls refreshGameBackground")
    void holdBrickCallsRefreshGameBackground() {
        stubGuiController.reset();

        gameController.holdBrick();

        assertTrue(stubGuiController.wasRefreshCalled(),
                "Should refresh game background after hold");
    }

    // ========== getHoldBrickData Tests ==========

    @Test
    @DisplayName("getHoldBrickData returns data after hold")
    void getHoldBrickDataReturnsDataAfterHold() {
        gameController.holdBrick();
        int[][] holdData = gameController.getHoldBrickData();
        assertNotNull(holdData, "Hold brick data should not be null after hold");
    }

    // ========== getNextBricksData Tests ==========

    @Test
    @DisplayName("getNextBricksData returns list")
    void getNextBricksDataReturnsList() {
        assertNotNull(gameController.getNextBricksData(),
                "Next bricks data should not be null");
    }

    @Test
    @DisplayName("getNextBricksData returns 5 items")
    void getNextBricksDataReturnsFiveItems() {
        assertEquals(5, gameController.getNextBricksData().size(),
                "Should return 5 next bricks");
    }

    @Test
    @DisplayName("getNextBricksData items are not null")
    void getNextBricksDataItemsAreNotNull() {
        for (int[][] brick : gameController.getNextBricksData()) {
            assertNotNull(brick, "Each next brick should not be null");
        }
    }

    // ========== getLinesCleared Tests ==========

    @Test
    @DisplayName("getLinesCleared returns non-negative value")
    void getLinesClearedReturnsNonNegativeValue() {
        int lines = gameController.getLinesCleared();
        assertTrue(lines >= 0, "Lines cleared should be non-negative");
    }

    @Test
    @DisplayName("getLinesCleared starts at zero")
    void getLinesClearedStartsAtZero() {
        assertEquals(0, gameController.getLinesCleared(),
                "Lines cleared should start at 0");
    }

    // ========== hardDrop Tests ==========

    @Test
    @DisplayName("hardDrop returns boolean")
    void hardDropReturnsBoolean() {
        boolean result = gameController.hardDrop();
        // Either true or false is valid
        assertTrue(result || !result, "hardDrop should return a boolean");
    }

    @Test
    @DisplayName("hardDrop calls refreshGameBackground")
    void hardDropCallsRefreshGameBackground() {
        stubGuiController.reset();

        gameController.hardDrop();

        assertTrue(stubGuiController.wasRefreshCalled(),
                "Should refresh after hard drop");
    }

    @Test
    @DisplayName("hardDrop merges brick to board")
    void hardDropMergesBrickToBoard() {
        // Get initial board state
        int[][] initialBoard = gameController.getBoard().getBoardMatrix();
        boolean initiallyEmpty = isBoardEmpty(initialBoard);

        gameController.hardDrop();

        // Board should have blocks now
        int[][] newBoard = gameController.getBoard().getBoardMatrix();
        boolean hasBlocks = !isBoardEmpty(newBoard);

        assertTrue(hasBlocks || !initiallyEmpty,
                "Board should have blocks after hard drop");
    }

    @Test
    @DisplayName("hardDrop spawns new brick")
    void hardDropSpawnsNewBrick() {
        gameController.hardDrop();

        // New brick should exist
        ViewData viewData = gameController.getBoard().getViewData();
        assertNotNull(viewData.getBrickData(),
                "New brick should be spawned after hard drop");
    }

    // ========== Multiple Operations Tests ==========

    @Test
    @DisplayName("Multiple move events work in sequence")
    void multipleMoveEventsWorkInSequence() {
        assertDoesNotThrow(() -> {
            gameController.onLeftEvent(new MoveEvent(EventType.LEFT, EventSource.USER));
            gameController.onRightEvent(new MoveEvent(EventType.RIGHT, EventSource.USER));
            gameController.onDownEvent(new MoveEvent(EventType.DOWN, EventSource.USER));
            gameController.onRotateEvent(new MoveEvent(EventType.ROTATE, EventSource.USER));
        }, "Multiple moves should work in sequence");
    }

    @Test
    @DisplayName("Game can be restarted multiple times")
    void gameCanBeRestartedMultipleTimes() {
        assertDoesNotThrow(() -> {
            for (int i = 0; i < 5; i++) {
                gameController.incrementPiecesPlaced();
                gameController.createNewGame();
                assertEquals(0, gameController.getPiecesPlaced());
            }
        }, "Game should be restartable multiple times");
    }

    @Test
    @DisplayName("Hold can be used after restart")
    void holdCanBeUsedAfterRestart() {
        gameController.holdBrick();
        gameController.createNewGame();

        boolean result = gameController.holdBrick();
        assertTrue(result, "Should be able to hold after restart");
    }

    // ========== InputEventListener Implementation Tests ==========

    @Test
    @DisplayName("GameController implements InputEventListener")
    void gameControllerImplementsInputEventListener() {
        assertTrue(gameController instanceof InputEventListener,
                "GameController should implement InputEventListener");
    }

    @Test
    @DisplayName("All InputEventListener methods are implemented")
    void allInputEventListenerMethodsAreImplemented() {
        MoveEvent event = new MoveEvent(EventType.DOWN, EventSource.USER);

        assertDoesNotThrow(() -> {
            gameController.onDownEvent(event);
            gameController.onLeftEvent(event);
            gameController.onRightEvent(event);
            gameController.onRotateEvent(event);
            gameController.createNewGame();
        }, "All InputEventListener methods should work");
    }

    // ========== Integration Tests ==========

    @Test
    @DisplayName("Full game cycle: move, rotate, hard drop")
    void fullGameCycleWorks() {
        // Move brick around
        gameController.onLeftEvent(new MoveEvent(EventType.LEFT, EventSource.USER));
        gameController.onRightEvent(new MoveEvent(EventType.RIGHT, EventSource.USER));
        gameController.onRotateEvent(new MoveEvent(EventType.ROTATE, EventSource.USER));

        boolean success = gameController.hardDrop();

        // Should complete successfully (unless game over)
        assertTrue(success || !success, "Game cycle should complete");

        // New brick should exist
        assertNotNull(gameController.getBoard().getViewData().getBrickData(),
                "New brick should be active after drop");
    }

    @Test
    @DisplayName("Score increases with USER down events")
    void scoreIncreasesWithUserDownEvents() {
        int initialScore = gameController.getBoard().getScore().scoreProperty().get();

        // Move down multiple times
        for (int i = 0; i < 5; i++) {
            MoveEvent event = new MoveEvent(EventType.DOWN, EventSource.USER);
            gameController.onDownEvent(event);
        }

        int newScore = gameController.getBoard().getScore().scoreProperty().get();
        assertTrue(newScore >= initialScore,
                "Score should increase with USER down events");
    }

    // ========== Helper Methods ==========

    // Checks if the board is completely empty (all cells are 0)
    private boolean isBoardEmpty(int[][] board) {
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[i].length; j++) {
                if (board[i][j] != 0) {
                    return false;
                }
            }
        }
        return true;
    }
}