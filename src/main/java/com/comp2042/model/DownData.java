package com.comp2042.model;

public final class DownData {
    private final ClearRow clearRow;
    private final ViewData viewData;

    /**
     * Creates a new DownData instance.
     *
     * @param clearRow line clearing result (null if none)
     * @param viewData updated view information
     */
    public DownData(ClearRow clearRow, ViewData viewData) {
        this.clearRow = clearRow;
        this.viewData = viewData;
    }

    /**
     * Gets the line clearing result.
     *
     * @return ClearRow object, or null if no lines cleared
     */
    public ClearRow getClearRow() {
        return clearRow;
    }

    /**
     * Gets the updated view data.
     *
     * @return ViewData with current brick state
     */
    public ViewData getViewData() {
        return viewData;
    }
}
