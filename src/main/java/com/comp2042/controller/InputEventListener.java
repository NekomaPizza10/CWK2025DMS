package com.comp2042.controller;

import com.comp2042.event.MoveEvent;
import com.comp2042.model.DownData;
import com.comp2042.model.ViewData;

public interface InputEventListener {

    /**
     * Handles downward movement event.
     * @param event movement event with source info
     * @return DownData containing clear and view information
     */
    DownData onDownEvent(MoveEvent event);

    /**
     * Handles left movement event.
     * @param event movement event
     * @return ViewData with updated position
     */
    ViewData onLeftEvent(MoveEvent event);

    /**
     * Handles right movement event.
     *
     * @param event movement event
     * @return ViewData with updated position
     */
    ViewData onRightEvent(MoveEvent event);

    ViewData onRotateEvent(MoveEvent event);

    void createNewGame();
}
