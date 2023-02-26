
/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraft.entity.Entity
 *  net.minecraft.network.Packet
 *  net.minecraft.network.play.client.CPacketEntityAction
 *  net.minecraft.network.play.client.CPacketEntityAction$Action
 *  net.minecraft.network.play.client.CPacketInput
 *  net.minecraft.network.play.client.CPacketPlayer$Position
 *  net.minecraft.network.play.client.CPacketPlayer$PositionRotation
 *  net.minecraft.network.play.client.CPacketPlayer$Rotation
 *  net.minecraft.network.play.client.CPacketVehicleMove
 *  net.minecraftforge.fml.common.eventhandler.SubscribeEvent
 */
package me.earth.phobos.features.modules.player;

import java.util.Objects;
import me.earth.phobos.Phobos;
import me.earth.phobos.event.events.PacketEvent;
import me.earth.phobos.event.events.UpdateWalkingPlayerEvent;
import me.earth.phobos.features.modules.Module;
import me.earth.phobos.features.setting.Setting;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.client.CPacketInput;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.client.CPacketVehicleMove;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class Godmode
extends Module {
    private final Setting<Boolean> remount = this.register(new Setting<Boolean>("Remount", false));
    public Minecraft mc = Minecraft.getMinecraft();
    public Entity entity;

    public Godmode() {
        super("Godmode", "Makes u able to be in godmode via riding entities and can dupe with it.", Module.Category.PLAYER, true, false, false);
    }

    @Override
    public void onEnable() {
        super.onEnable();
        if (this.mc.world != null && this.mc.player.getRidingEntity() != null) {
            this.entity = this.mc.player.getRidingEntity();
            this.mc.renderGlobal.loadRenderers();
            this.hideEntity();
            this.mc.player.setPosition((double)Minecraft.getMinecraft().player.getPosition().getX(), (double)(Minecraft.getMinecraft().player.getPosition().getY() - 1), (double)Minecraft.getMinecraft().player.getPosition().getZ());
        }
        if (this.mc.world != null && this.remount.getValue().booleanValue()) {
            this.remount.setValue(false);
        }
    }

    @Override
    public void onDisable() {
        super.onDisable();
        if (this.remount.getValue().booleanValue()) {
            this.remount.setValue(false);
        }
        this.mc.player.dismountRidingEntity();
        Objects.requireNonNull(this.mc.getConnection()).sendPacket((Packet)new CPacketEntityAction((Entity)this.mc.player, CPacketEntityAction.Action.START_SNEAKING));
        this.mc.getConnection().sendPacket((Packet)new CPacketEntityAction((Entity)this.mc.player, CPacketEntityAction.Action.STOP_SNEAKING));
    }

    @SubscribeEvent
    public void onPacketSend(PacketEvent.Send event) {
        if (event.getPacket() instanceof CPacketPlayer.Position || event.getPacket() instanceof CPacketPlayer.PositionRotation) {
            event.setCanceled(true);
        }
    }

    private void hideEntity() {
        if (this.mc.player.getRidingEntity() != null) {
            this.mc.player.dismountRidingEntity();
            this.mc.world.removeEntity(this.entity);
        }
    }

    private void showEntity(Entity entity2) {
        entity2.isDead = false;
        this.mc.world.loadedEntityList.add(entity2);
        this.mc.player.startRiding(entity2, true);
    }

    @SubscribeEvent
    public void onPlayerWalkingUpdate(UpdateWalkingPlayerEvent event) {
        if (this.entity == null) {
            return;
        }
        if (event.getStage() == 0) {
            if (this.remount.getValue().booleanValue() && Objects.requireNonNull(Phobos.moduleManager.getModuleByClass(Godmode.class)).isEnabled()) {
                this.showEntity(this.entity);
            }
            this.entity.setPositionAndRotation(Minecraft.getMinecraft().player.posX, Minecraft.getMinecraft().player.posY, Minecraft.getMinecraft().player.posZ, Minecraft.getMinecraft().player.rotationYaw, Minecraft.getMinecraft().player.rotationPitch);
            this.mc.player.connection.sendPacket((Packet)new CPacketPlayer.Rotation(this.mc.player.rotationYaw, this.mc.player.rotationPitch, true));
            this.mc.player.connection.sendPacket((Packet)new CPacketInput(this.mc.player.movementInput.moveForward, this.mc.player.movementInput.moveStrafe, false, false));
            this.mc.player.connection.sendPacket((Packet)new CPacketVehicleMove(this.entity));
        }
    }
}

