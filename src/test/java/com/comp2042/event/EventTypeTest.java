package com.comp2042.event;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

class EventTypeTest {

    @Test
    @DisplayName("All event types should exist")
    void allEventTypesShouldExist() {
        EventType[] types = EventType.values();
        assertEquals(4, types.length);
    }

    @Test
    @DisplayName("DOWN event type exists")
    void downEventTypeExists() {
        assertEquals(EventType.DOWN, EventType.valueOf("DOWN"));
    }

    @Test
    @DisplayName("LEFT event type exists")
    void leftEventTypeExists() {
        assertEquals(EventType.LEFT, EventType.valueOf("LEFT"));
    }

    @Test
    @DisplayName("RIGHT event type exists")
    void rightEventTypeExists() {
        assertEquals(EventType.RIGHT, EventType.valueOf("RIGHT"));
    }

    @Test
    @DisplayName("ROTATE event type exists")
    void rotateEventTypeExists() {
        assertEquals(EventType.ROTATE, EventType.valueOf("ROTATE"));
    }

    @Test
    @DisplayName("Invalid event type throws exception")
    void invalidEventTypeThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> {
            EventType.valueOf("INVALID");
        });
    }
}