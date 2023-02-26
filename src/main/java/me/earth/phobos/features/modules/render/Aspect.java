/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  net.minecraftforge.fml.common.eventhandler.SubscribeEvent
 */
package me.earth.phobos.features.modules.render;

import me.earth.phobos.event.events.PerspectiveEvent;
import me.earth.phobos.features.modules.Module;
import me.earth.phobos.features.setting.Setting;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class Aspect
extends Module {
    public Setting<Float> aspect = this.register(new Setting<Float>("Alpha", Float.valueOf(1.0f), Float.valueOf(0.1f), Float.valueOf(5.0f)));

    public Aspect() {
        super("Aspect", "Change ur screen aspect like fortnite.", Module.Category.RENDER, true, false, false);
    }

    @SubscribeEvent
    public void onPerspectiveEvent(PerspectiveEvent perspectiveEvent) {
        perspectiveEvent.setAspect(this.aspect.getValue().floatValue());
    }
}

