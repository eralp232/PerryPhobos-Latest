/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  net.minecraftforge.fml.common.eventhandler.Cancelable
 */
package me.earth.phobos.event.events;

import me.earth.phobos.event.EventStage;
import net.minecraftforge.fml.common.eventhandler.Cancelable;

@Cancelable
public class UpdateWalkingPlayerEvent
extends EventStage {
    public UpdateWalkingPlayerEvent(int stage) {
        super(stage);
    }
}

