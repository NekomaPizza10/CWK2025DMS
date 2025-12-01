package com.comp2042.event;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

class MoveEventTest {

    // ===== Constructor and Getter Tests =====

    @Test
    @DisplayName("MoveEvent stores correct event type")
    void moveEventStoresCorrectEventType() {
        MoveEvent event = new MoveEvent(EventType.DOWN, EventSource.USER);
        assertEquals(EventType.DOWN, event.getEventType());
    }

    @Test
    @DisplayName("MoveEvent stores correct event source")
    void moveEventStoresCorrectEventSource() {
        MoveEvent event = new MoveEvent(EventType.LEFT, EventSource.USER);
        assertEquals(EventSource.USER, event.getEventSource());
    }

    // ===== All EventType Combinations =====

    @Test
    @DisplayName("MoveEvent with DOWN type and USER source")
    void moveEventDownUser() {
        MoveEvent event = new MoveEvent(EventType.DOWN, EventSource.USER);
        assertEquals(EventType.DOWN, event.getEventType());
        assertEquals(EventSource.USER, event.getEventSource());
    }

    @Test
    @DisplayName("MoveEvent with LEFT type and THREAD source")
    void moveEventLeftThread() {
        MoveEvent event = new MoveEvent(EventType.LEFT, EventSource.THREAD);
        assertEquals(EventType.LEFT, event.getEventType());
        assertEquals(EventSource.THREAD, event.getEventSource());
    }

    @Test
    @DisplayName("MoveEvent with RIGHT type and LOCK_DELAY source")
    void moveEventRightLockDelay() {
        MoveEvent event = new MoveEvent(EventType.RIGHT, EventSource.LOCK_DELAY);
        assertEquals(EventType.RIGHT, event.getEventType());
        assertEquals(EventSource.LOCK_DELAY, event.getEventSource());
    }

    @Test
    @DisplayName("MoveEvent with ROTATE type and USER source")
    void moveEventRotateUser() {
        MoveEvent event = new MoveEvent(EventType.ROTATE, EventSource.USER);
        assertEquals(EventType.ROTATE, event.getEventType());
        assertEquals(EventSource.USER, event.getEventSource());
    }

    // ===== Independence Tests =====

    @Test
    @DisplayName("Two MoveEvents are independent objects")
    void twoMoveEventsAreIndependent() {
        MoveEvent event1 = new MoveEvent(EventType.DOWN, EventSource.USER);
        MoveEvent event2 = new MoveEvent(EventType.DOWN, EventSource.USER);

        assertNotSame(event1, event2);
    }

    @Test
    @DisplayName("MoveEvents with different types are not equal")
    void moveEventsWithDifferentTypesAreNotEqual() {
        MoveEvent event1 = new MoveEvent(EventType.DOWN, EventSource.USER);
        MoveEvent event2 = new MoveEvent(EventType.LEFT, EventSource.USER);

        assertNotEquals(event1.getEventType(), event2.getEventType());
    }

    @Test
    @DisplayName("MoveEvents with different sources are not equal")
    void moveEventsWithDifferentSourcesAreNotEqual() {
        MoveEvent event1 = new MoveEvent(EventType.DOWN, EventSource.USER);
        MoveEvent event2 = new MoveEvent(EventType.DOWN, EventSource.THREAD);

        assertNotEquals(event1.getEventSource(), event2.getEventSource());
    }
}