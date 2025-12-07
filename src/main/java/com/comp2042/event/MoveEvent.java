package com.comp2042.event;

/**
 * Immutable data class representing a movement event in the game.
 * A MoveEvent combines an {@link EventType} (what action to perform) with an
 * {@link EventSource} (who initiated the action). This distinction allows the
 * game to treat user-initiated moves differently from automatic game events,
 * such as awarding bonus points for soft drops.
 *
 *
 * @see EventType
 * @see EventSource
 */
public final class MoveEvent {
    private final EventType eventType;
    private final EventSource eventSource;

    /**
     * Creates a new MoveEvent.
     *
     * @param eventType the type of movement
     * @param eventSource the source of the event
     */
    public MoveEvent(EventType eventType, EventSource eventSource) {
        this.eventType = eventType;
        this.eventSource = eventSource;
    }

    /**
     * Gets the event type.
     * @return the EventType (DOWN, LEFT, RIGHT, ROTATE)
     */
    public EventType getEventType() {
        return eventType;
    }

    /**
     * Gets the event source.
     * @return the EventSource (USER, THREAD, LOCK_DELAY)
     */
    public EventSource getEventSource() {
        return eventSource;
    }
}
