
/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.Entity
 *  net.minecraft.network.Packet
 *  net.minecraft.network.play.INetHandlerPlayServer
 *  net.minecraft.network.play.client.CPacketConfirmTeleport
 *  net.minecraft.network.play.client.CPacketPlayer
 *  net.minecraft.network.play.client.CPacketPlayer$Position
 *  net.minecraft.network.play.client.CPacketPlayer$PositionRotation
 *  net.minecraft.network.play.client.CPacketPlayer$Rotation
 *  net.minecraft.network.play.server.SPacketEntityVelocity
 *  net.minecraft.network.play.server.SPacketPlayerPosLook
 *  net.minecraftforge.fml.common.eventhandler.SubscribeEvent
 */
package me.earth.phobos.features.modules.movement;

import java.util.ArrayList;
import java.util.Objects;
import java.util.Random;
import me.earth.phobos.event.events.MoveEvent;
import me.earth.phobos.event.events.PacketEvent;
import me.earth.phobos.event.events.PushEvent;
import me.earth.phobos.event.events.UpdateWalkingPlayerEvent;
import me.earth.phobos.features.modules.Module;
import me.earth.phobos.features.setting.Setting;
import me.earth.phobos.util.MathUtil;
import net.minecraft.entity.Entity;
import net.minecraft.network.Packet;
import net.minecraft.network.play.INetHandlerPlayServer;
import net.minecraft.network.play.client.CPacketConfirmTeleport;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.server.SPacketEntityVelocity;
import net.minecraft.network.play.server.SPacketPlayerPosLook;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class PacketFlyNew
extends Module {
    private final Setting<types> type = this.register(new Setting<types>("Type", types.DOWN));
    private final Setting<modes> mode = this.register(new Setting<modes>("Mode", modes.FAST));
    private final ArrayList<Packet<INetHandlerPlayServer>> packets;
    public Setting<Integer> factorAmount = this.register(new Setting<Float>("Factor", Float.valueOf(1.0f), Float.valueOf(0.1f), Float.valueOf(10.0f)));
    public Setting<Boolean> limit = this.register(new Setting<Boolean>("Limit", true));
    private int teleportID = -1;
    private int frequency = 1;
    private boolean frequencyUp = true;
    private float rotationYaw = -1.0f;
    private float rotationPitch = -1.0f;

    public PacketFlyNew() {
        super("PacketFlyNew", "Uses packets to allow you to fly and move.", Module.Category.MOVEMENT, true, false, false);
        this.packets = new ArrayList();
    }

    public void sendOffsets(double x, double y, double z) {
        CPacketPlayer.PositionRotation spoofPos = null;
        switch (this.type.getValue()) {
            case UP: {
                spoofPos = new CPacketPlayer.PositionRotation(x, y + 1337.0, z, this.rotationYaw, this.rotationPitch, PacketFlyNew.mc.player.onGround);
                break;
            }
            case DOWN: {
                spoofPos = new CPacketPlayer.PositionRotation(x, y - 1337.0, z, this.rotationYaw, this.rotationPitch, PacketFlyNew.mc.player.onGround);
                break;
            }
            case BOUNDED: {
                spoofPos = new CPacketPlayer.PositionRotation(x, 256.0, z, this.rotationYaw, this.rotationPitch, PacketFlyNew.mc.player.onGround);
                break;
            }
            case CONCEAL: {
                spoofPos = new CPacketPlayer.PositionRotation(x + (double)new Random().nextInt(2000000) - 1000000.0, y + (double)new Random().nextInt(2000000) - 1000000.0, z + (double)new Random().nextInt(2000000) - 1000000.0, this.rotationYaw, this.rotationPitch, PacketFlyNew.mc.player.onGround);
                break;
            }
            case LIMITJITTER: {
                spoofPos = new CPacketPlayer.PositionRotation(x, y + (double)new Random().nextInt(512) - 256.0, z, this.rotationYaw, this.rotationPitch, PacketFlyNew.mc.player.onGround);
                break;
            }
            case PRESERVE: {
                spoofPos = new CPacketPlayer.PositionRotation(x + (double)new Random().nextInt(2000000) - 1000000.0, y, z + (double)new Random().nextInt(2000000) - 1000000.0, this.rotationYaw, this.rotationPitch, PacketFlyNew.mc.player.onGround);
            }
        }
        if (spoofPos == null) {
            return;
        }
        this.packets.add((Packet<INetHandlerPlayServer>)spoofPos);
        Objects.requireNonNull(mc.getConnection()).sendPacket(spoofPos);
    }

    @Override
    public void onEnable() {
        this.packets.clear();
        this.rotationYaw = PacketFlyNew.mc.player.rotationYaw;
        this.rotationPitch = PacketFlyNew.mc.player.rotationPitch;
        this.sendOffsets(PacketFlyNew.mc.player.posX, PacketFlyNew.mc.player.posY, PacketFlyNew.mc.player.posZ);
    }

    private boolean isInsideBlock() {
        return !PacketFlyNew.mc.world.getCollisionBoxes((Entity)PacketFlyNew.mc.player, PacketFlyNew.mc.player.getEntityBoundingBox().expand(-0.0625, -0.0625, -0.0625)).isEmpty();
    }

    @SubscribeEvent
    public void onMove(MoveEvent event) {
        PacketFlyNew.mc.player.motionX = 0.0;
        PacketFlyNew.mc.player.motionY = 0.0;
        PacketFlyNew.mc.player.motionZ = 0.0;
        if (event.getStage() == 1) {
            event.setCanceled(true);
            return;
        }
        double motionY = 0.0;
        if (PacketFlyNew.mc.player.movementInput.jump) {
            motionY = this.isInsideBlock() ? 0.016 : 0.032;
            if (!PacketFlyNew.mc.player.onGround && !this.isInsideBlock() && PacketFlyNew.mc.player.ticksExisted % 15 == 0) {
                motionY = -0.032;
            }
        } else if (PacketFlyNew.mc.player.movementInput.sneak) {
            motionY = this.isInsideBlock() ? -0.032 : -0.062;
        } else if (!PacketFlyNew.mc.player.onGround && !this.isInsideBlock() && PacketFlyNew.mc.player.ticksExisted % 15 == 0) {
            motionY = -0.032;
        }
        int currentFactor = 1;
        int clock = 0;
        while (currentFactor <= (this.mode.getValue().equals((Object)modes.FACTOR) ? this.factorAmount.getValue() : 1)) {
            if (clock++ <= (this.limit.getValue() != false ? 5 : 1)) continue;
            double vSpeed = this.isInsideBlock() ? (PacketFlyNew.mc.player.movementInput.jump || PacketFlyNew.mc.player.movementInput.sneak ? 0.016 : 0.032) : (PacketFlyNew.mc.player.movementInput.jump ? 0.0032 : (PacketFlyNew.mc.player.movementInput.sneak ? 0.032 : 0.062));
            double[] strafing = MathUtil.directionSpeed(vSpeed);
            double motionX = strafing[0] * (double)currentFactor;
            double motionZ = strafing[1] * (double)currentFactor;
            event.setX(motionX);
            event.setY(motionY);
            event.setZ(motionZ);
            this.doMovement(motionX, motionY, motionZ);
            ++currentFactor;
            if (motionX == 0.0 && motionY == 0.0 && motionZ == 0.0) {
                return;
            }
            if (this.frequencyUp) {
                ++this.frequency;
                if (this.frequency != 3) continue;
                this.frequencyUp = false;
                continue;
            }
            --this.frequency;
            if (this.frequency != 1) continue;
            this.frequencyUp = true;
        }
    }

    @SubscribeEvent
    public void onUpdateWalkingPlayer(UpdateWalkingPlayerEvent event) {
        if (event.getStage() == 1) {
            return;
        }
        PacketFlyNew.mc.player.motionX = 0.0;
        PacketFlyNew.mc.player.motionY = 0.0;
        PacketFlyNew.mc.player.motionZ = 0.0;
        PacketFlyNew.mc.player.setVelocity(0.0, 0.0, 0.0);
    }

    @SubscribeEvent
    public void onPushOutOfBlocks(PushEvent event) {
        if (event.getStage() == 1) {
            event.setCanceled(true);
        }
    }

    private void doMovement(double x, double y, double z) {
        CPacketPlayer.PositionRotation newPos = new CPacketPlayer.PositionRotation(PacketFlyNew.mc.player.posX + x, PacketFlyNew.mc.player.posY + y, PacketFlyNew.mc.player.posZ + z, this.rotationYaw, this.rotationPitch, PacketFlyNew.mc.player.onGround);
        this.packets.add((Packet<INetHandlerPlayServer>)newPos);
        Objects.requireNonNull(mc.getConnection()).sendPacket((Packet)newPos);
        for (int i = 0; i < this.frequency; ++i) {
            this.sendOffsets(PacketFlyNew.mc.player.posX, PacketFlyNew.mc.player.posY, PacketFlyNew.mc.player.posZ);
        }
    }

    @SubscribeEvent
    public void onPacketSend(PacketEvent.Send event) {
        if (event.getPacket() instanceof CPacketPlayer.Position || event.getPacket() instanceof CPacketPlayer.Rotation) {
            event.setCanceled(true);
        }
        if (event.getPacket() instanceof CPacketPlayer) {
            CPacketPlayer packetPlayer = (CPacketPlayer)event.getPacket();
            if (this.packets.contains(packetPlayer)) {
                this.packets.remove(packetPlayer);
            } else {
                event.setCanceled(true);
            }
        }
    }

    @SubscribeEvent
    public void onPacketReceive(PacketEvent.Receive event) {
        if (PacketFlyNew.mc.player == null || PacketFlyNew.mc.world == null) {
            return;
        }
        if (event.getPacket() instanceof SPacketPlayerPosLook) {
            SPacketPlayerPosLook flag = (SPacketPlayerPosLook)event.getPacket();
            this.teleportID = flag.getTeleportId();
            PacketFlyNew.mc.player.setPosition(flag.getX(), flag.getY(), flag.getZ());
            PacketFlyNew.mc.player.connection.sendPacket((Packet)new CPacketConfirmTeleport(this.teleportID++));
            event.setCanceled(true);
        }
        if (event.getPacket() instanceof SPacketEntityVelocity) {
            SPacketEntityVelocity packet = (SPacketEntityVelocity)event.getPacket();
            if (packet.entityID == PacketFlyNew.mc.player.entityId) {
                packet.motionX = 0;
                packet.motionY = 0;
                packet.motionZ = 0;
            }
        }
    }

    private static enum types {
        UP,
        DOWN,
        PRESERVE,
        LIMITJITTER,
        BOUNDED,
        CONCEAL;

    }

    private static enum modes {
        FAST,
        FACTOR;

    }
}

