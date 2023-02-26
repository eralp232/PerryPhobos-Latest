/*
 * Decompiled with CFR 0.151.
 */
package me.earth.phobos.event.events;

import me.earth.phobos.event.EventStage;

public class PerspectiveEvent
extends EventStage {
    private float aspect;

    public PerspectiveEvent(float f) {
        this.aspect = f;
    }

    public float getAspect() {
        return this.aspect;
    }

    public void setAspect(float f) {
        this.aspect = f;
    }
}

