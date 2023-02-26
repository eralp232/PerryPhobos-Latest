
/*
 * Decompiled with CFR 0.151.
 */
package me.earth.phobos.features.modules.combat;

import me.earth.phobos.features.modules.Module;
import me.earth.phobos.features.modules.combat.AutoCrystal;

public class SelfCrystal
extends Module {
    public SelfCrystal() {
        super("SelfCrystal", "Makes CA target urself.", Module.Category.COMBAT, true, false, false);
    }

    @Override
    public void onTick() {
        if (AutoCrystal.getInstance().isEnabled()) {
            AutoCrystal.target = SelfCrystal.mc.player;
        }
    }
}

