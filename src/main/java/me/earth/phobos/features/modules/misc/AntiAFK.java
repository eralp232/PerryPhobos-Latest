
/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  net.minecraft.init.Blocks
 *  net.minecraft.inventory.ClickType
 *  net.minecraft.item.ItemStack
 *  net.minecraft.network.Packet
 *  net.minecraft.network.play.client.CPacketClickWindow
 *  net.minecraft.network.play.client.CPacketPlayerDigging
 *  net.minecraft.network.play.client.CPacketPlayerDigging$Action
 *  net.minecraft.network.play.client.CPacketTabComplete
 *  net.minecraft.util.EnumFacing
 *  net.minecraft.util.EnumHand
 *  net.minecraft.util.math.BlockPos
 */
package me.earth.phobos.features.modules.misc;

import java.util.Random;
import java.util.UUID;
import me.earth.phobos.features.modules.Module;
import me.earth.phobos.features.setting.Setting;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.ClickType;
import net.minecraft.item.ItemStack;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketClickWindow;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.network.play.client.CPacketTabComplete;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;

public class AntiAFK
extends Module {
    private final Random random;
    private final Setting<Boolean> swing = this.register(new Setting<Boolean>("Swing", true));
    private final Setting<Boolean> turn = this.register(new Setting<Boolean>("Turn", true));
    private final Setting<Boolean> jump = this.register(new Setting<Boolean>("Jump", true));
    private final Setting<Boolean> sneak = this.register(new Setting<Boolean>("Sneak", true));
    private final Setting<Boolean> interact = this.register(new Setting<Boolean>("InteractBlock", false));
    private final Setting<Boolean> tabcomplete = this.register(new Setting<Boolean>("TabComplete", true));
    private final Setting<Boolean> msgs = this.register(new Setting<Boolean>("ChatMsgs", true));
    private final Setting<Boolean> stats = this.register(new Setting<Boolean>("Stats", true));
    private final Setting<Boolean> window = this.register(new Setting<Boolean>("WindowClick", true));
    private final Setting<Boolean> swap = this.register(new Setting<Boolean>("ItemSwap", true));
    private final Setting<Boolean> dig = this.register(new Setting<Boolean>("HitBlock", true));
    private final Setting<Boolean> move = this.register(new Setting<Boolean>("Move", true));

    public AntiAFK() {
        super("AntiAFK", "Attempts to stop the server from kicking u when ur afk.", Module.Category.MISC, true, false, false);
        this.random = new Random();
    }

    @Override
    public void onUpdate() {
        BlockPos blockPos;
        if (AntiAFK.mc.player.ticksExisted % 45 == 0 && this.swing.getValue().booleanValue()) {
            AntiAFK.mc.player.swingArm(EnumHand.MAIN_HAND);
        }
        if (AntiAFK.mc.player.ticksExisted % 20 == 0 && this.turn.getValue().booleanValue()) {
            AntiAFK.mc.player.rotationYaw = this.random.nextInt(360) - 180;
        }
        if (AntiAFK.mc.player.ticksExisted % 60 == 0 && this.jump.getValue().booleanValue() && AntiAFK.mc.player.onGround) {
            AntiAFK.mc.player.jump();
        }
        if (AntiAFK.mc.player.ticksExisted % 50 == 0 && this.sneak.getValue().booleanValue() && !AntiAFK.mc.player.isSneaking()) {
            AntiAFK.mc.player.movementInput.sneak = true;
        }
        if ((double)AntiAFK.mc.player.ticksExisted % 52.5 == 0.0 && this.sneak.getValue().booleanValue() && AntiAFK.mc.player.isSneaking()) {
            AntiAFK.mc.player.movementInput.sneak = false;
        }
        if (AntiAFK.mc.player.ticksExisted % 30 == 0 && this.interact.getValue().booleanValue() && !AntiAFK.mc.world.isAirBlock(blockPos = AntiAFK.mc.objectMouseOver.getBlockPos())) {
            AntiAFK.mc.playerController.clickBlock(blockPos, AntiAFK.mc.objectMouseOver.sideHit);
        }
        if (AntiAFK.mc.player.ticksExisted % 80 == 0 && this.tabcomplete.getValue().booleanValue() && !AntiAFK.mc.player.isDead) {
            AntiAFK.mc.player.connection.sendPacket((Packet)new CPacketTabComplete("/" + UUID.randomUUID().toString().replace('-', 'v'), AntiAFK.mc.player.getPosition(), false));
        }
        if (AntiAFK.mc.player.ticksExisted % 200 == 0 && this.msgs.getValue().booleanValue() && !AntiAFK.mc.player.isDead) {
            AntiAFK.mc.player.sendChatMessage("Perry Phobos AntiAFK " + this.random.nextInt());
        }
        if (AntiAFK.mc.player.ticksExisted % 300 == 0 && this.stats.getValue().booleanValue() && !AntiAFK.mc.player.isDead) {
            AntiAFK.mc.player.sendChatMessage("/stats");
        }
        if (AntiAFK.mc.player.ticksExisted % 125 == 0 && this.window.getValue().booleanValue() && !AntiAFK.mc.player.isDead) {
            AntiAFK.mc.player.connection.sendPacket((Packet)new CPacketClickWindow(1, 1, 1, ClickType.CLONE, new ItemStack(Blocks.OBSIDIAN), (short) 1));
        }
        if (AntiAFK.mc.player.ticksExisted % 70 == 0 && this.swap.getValue().booleanValue() && !AntiAFK.mc.player.isDead) {
            AntiAFK.mc.player.connection.sendPacket((Packet)new CPacketPlayerDigging(CPacketPlayerDigging.Action.SWAP_HELD_ITEMS, AntiAFK.mc.player.getPosition(), EnumFacing.DOWN));
        }
        if (AntiAFK.mc.player.ticksExisted % 50 == 0 && this.dig.getValue().booleanValue()) {
            AntiAFK.mc.player.connection.sendPacket((Packet)new CPacketPlayerDigging(CPacketPlayerDigging.Action.START_DESTROY_BLOCK, AntiAFK.mc.player.getPosition(), EnumFacing.DOWN));
        }
        if (AntiAFK.mc.player.ticksExisted % 150 == 0 && this.move.getValue().booleanValue()) {
            AntiAFK.mc.gameSettings.keyBindForward.pressed = true;
            AntiAFK.mc.gameSettings.keyBindBack.pressed = true;
            AntiAFK.mc.gameSettings.keyBindRight.pressed = true;
            AntiAFK.mc.gameSettings.keyBindLeft.pressed = true;
        }
    }
}

