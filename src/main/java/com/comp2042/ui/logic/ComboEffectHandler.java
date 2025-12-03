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

    public ComboEffectHandler() {
        // Components are set after construction
    }

    public void setComponents(ComboAnimationManager animation,
                              ComboMeterPanel meter,
                              BoardGlowEffect glow) {
        this.comboAnimation = animation;
        this.comboMeter = meter;
        this.boardGlow = glow;
    }

    public void triggerComboEffects(int combo, int linesCleared) {
        triggerComboAnimation(combo);
        triggerLineClearShake(linesCleared);
        updateComboMeter(combo);
        updateBoardGlow(combo);
    }

    private void triggerComboAnimation(int combo) {
        if (comboAnimation != null) {
            comboAnimation.triggerComboEffects(combo);
        }
    }

    private void triggerLineClearShake(int lines) {
        if (comboAnimation != null) {
            comboAnimation.shakeOnLineClear(lines);
        }
    }

    private void updateComboMeter(int combo) {
        if (comboMeter != null) {
            comboMeter.updateCombo(combo);
        }
    }

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