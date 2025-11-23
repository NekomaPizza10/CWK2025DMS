package com.comp2042;

public enum GameMode {
    NORMAL("Normal Mode", "Practice mode with increasing speed. Press N to restart anytime."),
    FORTY_LINES("40 Lines Challenge", "Clear 40 lines as fast as possible!"),
    TWO_MINUTES("2 Minutes Challenge", "Clear as many lines as you can in 2 minutes!");

    private final String displayName;
    private final String description;

    GameMode(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDescription() {
        return description;
    }
}


