/*
 * Decompiled with CFR 0.151.
 */
package me.earth.phobos.features.modules.movement;

import me.earth.phobos.features.modules.Module;

public class EntityControl
extends Module {
    public static EntityControl INSTANCE;

    public EntityControl() {
        super("EntityControl", "Control non saddled entities.", Module.Category.MOVEMENT, false, false, false);
        INSTANCE = this;
    }
}

