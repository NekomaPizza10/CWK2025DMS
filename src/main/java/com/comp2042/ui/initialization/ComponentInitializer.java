package com.comp2042.ui.initialization;

import com.comp2042.state.GameState;
import com.comp2042.ui.render.GameRenderer;
import com.comp2042.ui.handlers.InputHandler;
import com.comp2042.ui.handlers.UIUpdater;

/**
 * Handles initialization of core game components.
 */
public class ComponentInitializer {

    private final GuiController controller;

    public ComponentInitializer(GuiController controller) {
        this.controller = controller;
    }

    // Initializes all core game components.
    public void initializeComponents() {
        GameState gameState = new GameState();
        controller.setGameState(gameState);

        GameRenderer renderer = new GameRenderer(
                controller.getGamePanel(),
                controller.getBrickPanel()
        );
        controller.setRenderer(renderer);

        InputHandler inputHandler = new InputHandler(gameState);
        controller.setInputHandler(inputHandler);

        UIUpdater uiUpdater = new UIUpdater(gameState);
        controller.setUiUpdater(uiUpdater);
    }
}