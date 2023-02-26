/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  net.minecraft.item.ItemStack
 *  net.minecraft.util.EnumHand
 *  net.minecraft.util.math.BlockPos
 *  net.minecraftforge.fml.common.eventhandler.Cancelable
 */
package me.earth.phobos.event.events;

import me.earth.phobos.event.EventStage;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.eventhandler.Cancelable;

@Cancelable
public class ProcessRightClickBlockEvent
extends EventStage {
    public BlockPos pos;
    public EnumHand hand;
    public ItemStack stack;

    public ProcessRightClickBlockEvent(BlockPos pos, EnumHand hand, ItemStack stack) {
        this.pos = pos;
        this.hand = hand;
        this.stack = stack;
    }
}

