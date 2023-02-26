/*
 * Decompiled with CFR 0.151.
 */
package me.earth.phobos.features.gui.chat.utils;

public class AnimationTools {
    public static float clamp(float number, float min, float max) {
        return number < min ? min : Math.min(number, max);
    }
}

