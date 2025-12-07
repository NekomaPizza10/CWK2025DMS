package com.comp2042.ui.logic;

import com.comp2042.ui.effect.BoardGlowEffect;
import com.comp2042.ui.effect.ComboAnimationManager;
import com.comp2042.ui.effect.ComboMeterPanel;

/**
 * Handles all combo-related visual effects including
 * animations, meter updates, and board glow.
 */
public class ComboEffectHandler {

    private ComboAnimationManager comboAnimation;
    private ComboMeterPanel comboMeter;
    private BoardGlowEffect boardGlow;

    /**
     * Creates a new {@code ComboEffectHandler}.
     * Component references are assigned later using {@link #setComponents}.
     */
    public ComboEffectHandler() {
        // Components are set after construction
    }

    /**
     * Sets the visual effect components used for combo animations, combo meter,
     * and board glow effects.
     *
     * @param animation the combo animation manager responsible for visual combo effects
     * @param meter     the combo meter panel that displays the player's combo level
     * @param glow      the board glow effect that reacts to combo strength
     */
    public void setComponents(ComboAnimationManager animation,
                              ComboMeterPanel meter,
                              BoardGlowEffect glow) {
        this.comboAnimation = animation;
        this.comboMeter = meter;
        this.boardGlow = glow;
    }

    /**
     * Triggers all visual combo effects based on the current combo count
     * and the number of lines cleared.
     *
     * @param combo        the player's current combo count
     * @param linesCleared the number of lines cleared in the recent action
     */
    public void triggerComboEffects(int combo, int linesCleared) {
        triggerComboAnimation(combo);
        triggerLineClearShake(linesCleared);
        updateComboMeter(combo);
        updateBoardGlow(combo);
    }

    /**
     * Triggers the combo animation if the animation component is available.
     *
     * @param combo the combo count used to determine animation intensity
     */
    private void triggerComboAnimation(int combo) {
        if (comboAnimation != null) {
            comboAnimation.triggerComboEffects(combo);
        }
    }

    /**
     * Triggers a shake animation based on the number of cleared lines.
     *
     * @param lines number of lines cleared
     */
    private void triggerLineClearShake(int lines) {
        if (comboAnimation != null) {
            comboAnimation.shakeOnLineClear(lines);
        }
    }

    /**
     * Updates the combo meter visual to match the current combo value.
     *
     * @param combo the current combo count
     */
    private void updateComboMeter(int combo) {
        if (comboMeter != null) {
            comboMeter.updateCombo(combo);
        }
    }

    /**
     * Updates the board glow effect intensity based on the current combo value.
     *
     * @param combo the combo count that determines the glow level
     */
    private void updateBoardGlow(int combo) {
        if (boardGlow != null) {
            boardGlow.updateGlow(combo);
        }
    }

    public void triggerHardDropEffect() {
        if (comboAnimation != null) {
            comboAnimation.shakeOnHardDrop();
        }
    }

    public void pauseDecay() {
        if (comboMeter != null) {
            comboMeter.pauseDecay();
        }
        if (boardGlow != null) {
            boardGlow.pauseDecay();
        }
    }

    public void resumeDecay() {
        if (comboMeter != null) {
            comboMeter.resumeDecay();
        }
        if (boardGlow != null) {
            boardGlow.resumeDecay();
        }
    }

    public void reset() {
        if (comboMeter != null) {
            comboMeter.reset();
        }
        if (boardGlow != null) {
            boardGlow.reset();
        }
    }
}