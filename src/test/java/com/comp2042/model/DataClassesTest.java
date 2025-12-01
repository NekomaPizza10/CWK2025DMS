package com.comp2042.model;

import com.comp2042.event.EventSource;
import com.comp2042.event.EventType;
import com.comp2042.event.MoveEvent;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

// Tests for data transfer objects and enums
class DataClassesTest {

    // ========== ClearRow ==========

    @Test
    @DisplayName("ClearRow stores lines removed correctly")
    void clearRowStoresLinesRemoved() {
        // Given: ClearRow with 3 lines removed
        ClearRow clearRow = new ClearRow(3, new int[5][5], 450);
        // Then: Returns 3
        assertEquals(3, clearRow.getLinesRemoved(), "Should return 3 lines removed");
    }

    @Test
    @DisplayName("ClearRow returns copy of matrix")
    void clearRowReturnsMatrixCopy() {
        // Given: ClearRow with matrix
        int[][] original = {{1, 2}, {3, 4}};
        ClearRow clearRow = new ClearRow(1, original, 100);

        // When: Get matrix and modify it
        int[][] returned = clearRow.getNewMatrix();
        returned[0][0] = 999;

        // When: Get matrix again
        int[][] returned2 = clearRow.getNewMatrix();
        // Then: Should be unchanged (defensive copy)
        assertEquals(1, returned2[0][0], "Should return copy, not reference");
    }

    @Test
    @DisplayName("ClearRow stores score bonus correctly")
    void clearRowStoresScoreBonus() {
        // Given: ClearRow with bonus 800
        ClearRow clearRow = new ClearRow(4, new int[5][5], 800);
        // Then: Returns 800
        assertEquals(800, clearRow.getScoreBonus(), "Should return 800 score bonus");
    }

    // ========== ViewData ==========

    @Test
    @DisplayName("ViewData stores brick data correctly")
    void viewDataStoresBrickData() {
        // Given: ViewData with brick
        int[][] brickData = {{1, 1}, {1, 1}};
        int[][] nextData = {{2, 2}, {2, 2}};
        ViewData viewData = new ViewData(brickData, 5, 10, nextData);
        // Then: Returns brick data
        int[][] returned = viewData.getBrickData();
        assertNotNull(returned, "Brick data should not be null");
        assertEquals(2, returned.length, "Brick dimensions should match");
    }

    @Test
    @DisplayName("ViewData stores position correctly")
    void viewDataStoresPosition() {
        // Given: ViewData at position (3, 7)
        ViewData viewData = new ViewData(new int[2][2], 3, 7, new int[2][2]);
        // Then: Returns correct positions
        assertEquals(3, viewData.getxPosition(), "X should be 3");
        assertEquals(7, viewData.getyPosition(), "Y should be 7");
    }

    @Test
    @DisplayName("ViewData returns copy of next brick data")
    void viewDataReturnsNextBrickCopy() {
        // Given: ViewData with next brick
        int[][] nextData = {{5, 5}, {5, 5}};
        ViewData viewData = new ViewData(new int[2][2], 0, 0, nextData);
        // When: Get and modify next brick
        int[][] returned = viewData.getNextBrickData();
        returned[0][0] = 999;

        // When: Get again
        int[][] returned2 = viewData.getNextBrickData();
        // Then: Should be unchanged (defensive copy)
        assertEquals(5, returned2[0][0], "Should return copy of next brick");
    }

    // ========== NextShapeInfo ==========

    @Test
    @DisplayName("NextShapeInfo stores shape correctly")
    void nextShapeInfoStoresShape() {
        // Given: NextShapeInfo with shape
        int[][] shape = {{1, 2}, {3, 4}};
        NextShapeInfo info = new NextShapeInfo(shape, 2);
        // Then: Returns shape
        int[][] returned = info.getShape();
        assertNotNull(returned, "Shape should not be null");
        assertEquals(2, returned.length, "Shape dimensions should match");
    }

    @Test
    @DisplayName("NextShapeInfo stores position correctly")
    void nextShapeInfoStoresPosition() {
        // Given: NextShapeInfo at position 3
        NextShapeInfo info = new NextShapeInfo(new int[2][2], 3);
        // Then: Returns 3
        assertEquals(3, info.getPosition(), "Position should be 3");
    }

    @Test
    @DisplayName("NextShapeInfo returns copy of shape")
    void nextShapeInfoReturnsShapeCopy() {
        // Given: NextShapeInfo with shape
        int[][] original = {{7, 8}, {9, 10}};
        NextShapeInfo info = new NextShapeInfo(original, 1);

        // When: Get and modify shape
        int[][] returned = info.getShape();
        returned[0][0] = 999;
        // When: Get again
        int[][] returned2 = info.getShape();
        // Then: Should be unchanged (defensive copy)
        assertEquals(7, returned2[0][0], "Should return copy of shape");
    }

    // ========== DownData ==========

    @Test
    @DisplayName("DownData stores ClearRow correctly")
    void downDataStoresClearRow() {
        // Given: DownData with ClearRow
        ClearRow clearRow = new ClearRow(2, new int[5][5], 200);
        ViewData viewData = new ViewData(new int[2][2], 0, 0, new int[2][2]);
        DownData downData = new DownData(clearRow, viewData);
        // Then: Returns same ClearRow
        assertEquals(clearRow, downData.getClearRow(), "Should return ClearRow");
    }

    @Test
    @DisplayName("DownData stores ViewData correctly")
    void downDataStoresViewData() {
        // Given: DownData with ViewData
        ClearRow clearRow = new ClearRow(1, new int[5][5], 100);
        ViewData viewData = new ViewData(new int[2][2], 3, 5, new int[2][2]);
        DownData downData = new DownData(clearRow, viewData);
        // Then: Returns same ViewData
        assertEquals(viewData, downData.getViewData(), "Should return ViewData");
    }

    @Test
    @DisplayName("DownData handles null ClearRow")
    void downDataHandlesNullClearRow() {
        // Given: DownData with null ClearRow (no lines cleared)
        ViewData viewData = new ViewData(new int[2][2], 0, 0, new int[2][2]);
        DownData downData = new DownData(null, viewData);
        // Then: Allows null
        assertNull(downData.getClearRow(), "ClearRow can be null");
        assertNotNull(downData.getViewData(), "ViewData should exist");
    }

    // ========== MoveEvent ==========

    @Test
    @DisplayName("MoveEvent stores event type correctly")
    void moveEventStoresEventType() {
        // Given: MoveEvent with DOWN type
        MoveEvent event = new MoveEvent(EventType.DOWN, EventSource.USER);
        // Then: Returns DOWN
        assertEquals(EventType.DOWN, event.getEventType(), "Should return DOWN");
    }

    @Test
    @DisplayName("MoveEvent stores event source correctly")
    void moveEventStoresEventSource() {
        // Given: MoveEvent with THREAD source
        MoveEvent event = new MoveEvent(EventType.ROTATE, EventSource.THREAD);
        // Then: Returns THREAD
        assertEquals(EventSource.THREAD, event.getEventSource(), "Should return THREAD");
    }

    @Test
    @DisplayName("MoveEvent supports all event types")
    void moveEventSupportsAllEventTypes() {
        // Test all types
        MoveEvent down = new MoveEvent(EventType.DOWN, EventSource.USER);
        MoveEvent left = new MoveEvent(EventType.LEFT, EventSource.USER);
        MoveEvent right = new MoveEvent(EventType.RIGHT, EventSource.USER);
        MoveEvent rotate = new MoveEvent(EventType.ROTATE, EventSource.USER);

        assertEquals(EventType.DOWN, down.getEventType());
        assertEquals(EventType.LEFT, left.getEventType());
        assertEquals(EventType.RIGHT, right.getEventType());
        assertEquals(EventType.ROTATE, rotate.getEventType());
    }

    @Test
    @DisplayName("MoveEvent supports all event sources")
    void moveEventSupportsAllEventSources() {
        MoveEvent user = new MoveEvent(EventType.DOWN, EventSource.USER);
        MoveEvent thread = new MoveEvent(EventType.DOWN, EventSource.THREAD);
        MoveEvent lockDelay = new MoveEvent(EventType.DOWN, EventSource.LOCK_DELAY);

        assertEquals(EventSource.USER, user.getEventSource());
        assertEquals(EventSource.THREAD, thread.getEventSource());
        assertEquals(EventSource.LOCK_DELAY, lockDelay.getEventSource());
    }

    // ========== GameMode Enum ==========

    @Test
    @DisplayName("GameMode has correct display names")
    void gameModeHasCorrectDisplayNames() {
        assertEquals("Normal Mode", GameMode.NORMAL.getDisplayName());
        assertEquals("40 Lines Challenge", GameMode.FORTY_LINES.getDisplayName());
        assertEquals("2 Minutes Challenge", GameMode.TWO_MINUTES.getDisplayName());
    }

    @Test
    @DisplayName("GameMode has descriptions")
    void gameModeHasDescriptions() {
        assertNotNull(GameMode.NORMAL.getDescription(), "Normal should have description");
        assertNotNull(GameMode.FORTY_LINES.getDescription(), "40 Lines should have description");
        assertNotNull(GameMode.TWO_MINUTES.getDescription(), "2 Minutes should have description");

        // Verify they contain relevant info
        assertTrue(GameMode.FORTY_LINES.getDescription().contains("40"),
                "40 Lines description should mention 40");
        assertTrue(GameMode.TWO_MINUTES.getDescription().contains("2"),
                "2 Minutes description should mention 2");
    }

    // ========== EventType Enum ==========

    @Test
    @DisplayName("EventType has all expected values")
    void eventTypeHasAllExpectedValues() {
        // Verify count
        EventType[] types = EventType.values();
        assertEquals(4, types.length, "Should have 4 event types");

        // Verify each exists
        assertNotNull(EventType.valueOf("DOWN"));
        assertNotNull(EventType.valueOf("LEFT"));
        assertNotNull(EventType.valueOf("RIGHT"));
        assertNotNull(EventType.valueOf("ROTATE"));
    }

    // ========== EventSource Enum ==========

    @Test
    @DisplayName("EventSource has all expected values")
    void eventSourceHasAllExpectedValues() {
        // Verify count
        EventSource[] sources = EventSource.values();
        assertEquals(3, sources.length, "Should have 3 event sources");

        // Verify each exists
        assertNotNull(EventSource.valueOf("USER"));
        assertNotNull(EventSource.valueOf("THREAD"));
        assertNotNull(EventSource.valueOf("LOCK_DELAY"));
    }

    // ========== Integration ==========

    @Test
    @DisplayName("Data objects work together in game flow")
    void dataObjectsWorkTogetherInGameFlow() {
        // Simulate typical game flow

        // 1. Create brick and view
        int[][] brickData = {{1, 1}, {1, 1}};
        int[][] nextBrick = {{2, 2}, {2, 2}};
        ViewData viewData = new ViewData(brickData, 5, 10, nextBrick);

        // 2. Create move event
        MoveEvent moveEvent = new MoveEvent(EventType.DOWN, EventSource.THREAD);

        // 3. Create clear result
        int[][] clearedMatrix = new int[5][5];
        ClearRow clearRow = new ClearRow(2, clearedMatrix, 200);

        // 4. Combine into down data
        DownData downData = new DownData(clearRow, viewData);

        // 5. Create next shape info
        NextShapeInfo nextShape = new NextShapeInfo(nextBrick, 1);

        // Verify everything accessible
        assertNotNull(downData.getClearRow());
        assertNotNull(downData.getViewData());
        assertEquals(2, downData.getClearRow().getLinesRemoved());
        assertEquals(5, downData.getViewData().getxPosition());
        assertEquals(EventType.DOWN, moveEvent.getEventType());
        assertEquals(1, nextShape.getPosition());
    }
}