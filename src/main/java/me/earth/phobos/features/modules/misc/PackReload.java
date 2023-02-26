
/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  net.minecraftforge.fml.common.eventhandler.SubscribeEvent
 */
package me.earth.phobos.features.modules.misc;

import me.earth.phobos.features.modules.Module;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class PackReload
extends Module {
    public PackReload() {
        super("PackReload", "Automatically reloads ur pack.", Module.Category.MISC, false, false, false);
    }

    @Override
    @SubscribeEvent
    public void onTick() {
        mc.refreshResources();
        this.disable();
    }
}

