
/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  net.minecraft.network.play.client.CPacketEntityAction
 *  net.minecraft.network.play.client.CPacketEntityAction$Action
 *  net.minecraft.network.play.client.CPacketPlayer
 *  net.minecraftforge.fml.common.eventhandler.SubscribeEvent
 */
package me.earth.phobos.features.modules.player;

import me.earth.phobos.event.events.PacketEvent;
import me.earth.phobos.features.modules.Module;
import me.earth.phobos.features.setting.Setting;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class AntiHunger
extends Module {
    public Setting<Boolean> cancelSprint = this.register(new Setting<Boolean>("Sprint", true));
    public Setting<Boolean> ground = this.register(new Setting<Boolean>("Ground", true));

    public AntiHunger() {
        super("AntiHunger", "Prevents you from getting hungry as fast.", Module.Category.PLAYER, true, false, false);
    }

    @SubscribeEvent
    public void onPacketSend(PacketEvent.Send event) {
        if (this.ground.getValue().booleanValue() && event.getPacket() instanceof CPacketPlayer) {
            CPacketPlayer packet = event.getPacket();
            boolean bl = packet.onGround = AntiHunger.mc.player.fallDistance >= 0.0f || AntiHunger.mc.playerController.isHittingBlock;
        }
        if (this.cancelSprint.getValue().booleanValue() && event.getPacket() instanceof CPacketEntityAction && (((CPacketEntityAction) event.getPacket()).getAction() == CPacketEntityAction.Action.START_SPRINTING || ((CPacketEntityAction) event.getPacket()).getAction() == CPacketEntityAction.Action.STOP_SPRINTING)) {
            event.setCanceled(true);
        }
    }
}

