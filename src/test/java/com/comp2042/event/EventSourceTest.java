package com.comp2042.event;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

class EventSourceTest {

    @Test
    @DisplayName("All event sources should exist")
    void allEventSourcesShouldExist() {
        EventSource[] sources = EventSource.values();
        assertEquals(3, sources.length);
    }

    @Test
    @DisplayName("USER event source exists")
    void userEventSourceExists() {
        assertEquals(EventSource.USER, EventSource.valueOf("USER"));
    }

    @Test
    @DisplayName("THREAD event source exists")
    void threadEventSourceExists() {
        assertEquals(EventSource.THREAD, EventSource.valueOf("THREAD"));
    }

    @Test
    @DisplayName("LOCK_DELAY event source exists")
    void lockDelayEventSourceExists() {
        assertEquals(EventSource.LOCK_DELAY, EventSource.valueOf("LOCK_DELAY"));
    }

    @Test
    @DisplayName("Invalid event source throws exception")
    void invalidEventSourceThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> {
            EventSource.valueOf("INVALID");
        });
    }
}