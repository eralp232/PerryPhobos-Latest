
/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.Block
 *  net.minecraft.client.entity.EntityPlayerSP
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.init.MobEffects
 *  net.minecraft.network.Packet
 *  net.minecraft.network.play.client.CPacketPlayer$Position
 *  net.minecraft.network.play.server.SPacketPlayerPosLook
 *  net.minecraft.util.MovementInput
 *  net.minecraft.util.math.AxisAlignedBB
 *  net.minecraft.util.math.BlockPos
 *  net.minecraftforge.fml.common.eventhandler.SubscribeEvent
 *  net.minecraftforge.fml.common.gameevent.TickEvent$ClientTickEvent
 *  net.minecraftforge.fml.common.gameevent.TickEvent$Phase
 */
package me.earth.phobos.features.modules.movement;

import java.util.List;
import java.util.Objects;
import me.earth.phobos.Phobos;
import me.earth.phobos.event.events.MoveEvent;
import me.earth.phobos.event.events.PacketEvent;
import me.earth.phobos.event.events.UpdateWalkingPlayerEvent;
import me.earth.phobos.features.Feature;
import me.earth.phobos.features.modules.Module;
import me.earth.phobos.features.modules.movement.Strafe;
import me.earth.phobos.features.setting.Setting;
import me.earth.phobos.util.EntityUtil;
import me.earth.phobos.util.TimerUtil;
import net.minecraft.block.Block;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.server.SPacketPlayerPosLook;
import net.minecraft.util.MovementInput;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class LongJump
extends Module {
    private final Setting<Integer> timeout = this.register(new Setting<Integer>("TimeOut", 2000, 0, 5000));
    private final Setting<Float> boost = this.register(new Setting<Float>("Boost", Float.valueOf(4.48f), Float.valueOf(1.0f), Float.valueOf(20.0f)));
    private final Setting<Mode> mode = this.register(new Setting<Mode>("Mode", Mode.DIRECT));
    private final Setting<Boolean> lagOff = this.register(new Setting<Boolean>("LagOff", false));
    private final Setting<Boolean> autoOff = this.register(new Setting<Boolean>("AutoOff", false));
    private final Setting<Boolean> disableStrafe = this.register(new Setting<Boolean>("DisableStrafe", false));
    private final Setting<Boolean> strafeOff = this.register(new Setting<Boolean>("StrafeOff", false));
    private final Setting<Boolean> step = this.register(new Setting<Boolean>("SetStep", false));
    private final TimerUtil timer = new TimerUtil();
    private int stage;
    private int airTicks;
    private int groundTicks;
    private double moveSpeed;
    private double lastDist;
    private boolean beganJump;

    public LongJump() {
        super("LongJump", "Jumps long.", Module.Category.MOVEMENT, true, false, false);
    }

    @Override
    public void onEnable() {
        this.timer.reset();
        this.groundTicks = 0;
        this.stage = 0;
        this.beganJump = false;
        if (Strafe.getInstance().isOn() && this.disableStrafe.getValue().booleanValue()) {
            Strafe.getInstance().disable();
        }
    }

    @Override
    public void onDisable() {
        Phobos.timerManager.setTimer(1.0f);
    }

    @SubscribeEvent
    public void onPacketReceive(PacketEvent.Receive event) {
        if (this.lagOff.getValue().booleanValue() && event.getPacket() instanceof SPacketPlayerPosLook) {
            this.disable();
        }
    }

    @SubscribeEvent
    public void onMove(MoveEvent event) {
        if (event.getStage() != 0) {
            return;
        }
        if (!this.timer.passedMs(this.timeout.getValue().intValue())) {
            event.setX(0.0);
            event.setY(0.0);
            event.setZ(0.0);
            return;
        }
        if (this.step.getValue().booleanValue()) {
            LongJump.mc.player.stepHeight = 0.6f;
        }
        this.doVirtue(event);
    }

    @SubscribeEvent
    public void onTickEvent(TickEvent.ClientTickEvent event) {
        if (Feature.fullNullCheck() || event.phase != TickEvent.Phase.START) {
            return;
        }
        if (Strafe.getInstance().isOn() && this.strafeOff.getValue().booleanValue()) {
            this.disable();
            return;
        }
        if (this.mode.getValue() == Mode.TICK) {
            this.doNormal(null);
        }
    }

    @SubscribeEvent
    public void onUpdateWalkingPlayer(UpdateWalkingPlayerEvent event) {
        if (event.getStage() != 0) {
            return;
        }
        if (!this.timer.passedMs(this.timeout.getValue().intValue())) {
            event.setCanceled(true);
            return;
        }
        this.doNormal(event);
    }

    private void doNormal(UpdateWalkingPlayerEvent event) {
        if (this.autoOff.getValue().booleanValue() && this.beganJump && LongJump.mc.player.onGround) {
            this.disable();
            return;
        }
        switch (this.mode.getValue()) {
            case VIRTUE: {
                if (LongJump.mc.player.moveForward != 0.0f || LongJump.mc.player.moveStrafing != 0.0f) {
                    double xDist = LongJump.mc.player.posX - LongJump.mc.player.prevPosX;
                    double zDist = LongJump.mc.player.posZ - LongJump.mc.player.prevPosZ;
                    this.lastDist = Math.sqrt(xDist * xDist + zDist * zDist);
                    break;
                }
                event.setCanceled(true);
                break;
            }
            case TICK: {
                if (event != null) {
                    return;
                }
            }
            case DIRECT: {
                if (EntityUtil.isInLiquid() || EntityUtil.isOnLiquid()) break;
                float direction = LongJump.mc.player.rotationYaw + (float)(LongJump.mc.player.moveForward < 0.0f ? 180 : 0) + (LongJump.mc.player.moveStrafing > 0.0f ? -90.0f * (LongJump.mc.player.moveForward < 0.0f ? -0.5f : (LongJump.mc.player.moveForward > 0.0f ? 0.5f : 1.0f)) : 0.0f) - (LongJump.mc.player.moveStrafing < 0.0f ? -90.0f * (LongJump.mc.player.moveForward < 0.0f ? -0.5f : (LongJump.mc.player.moveForward > 0.0f ? 0.5f : 1.0f)) : 0.0f);
                float xDir = (float)Math.cos((double)(direction + 90.0f) * Math.PI / 180.0);
                float zDir = (float)Math.sin((double)(direction + 90.0f) * Math.PI / 180.0);
                if (!LongJump.mc.player.collidedVertically) {
                    ++this.airTicks;
                    if (LongJump.mc.gameSettings.keyBindSneak.isKeyDown()) {
                        LongJump.mc.player.connection.sendPacket((Packet)new CPacketPlayer.Position(0.0, 2.147483647E9, 0.0, false));
                    }
                    this.groundTicks = 0;
                    if (!LongJump.mc.player.collidedVertically) {
                        if (LongJump.mc.player.motionY == -0.07190068807140403) {
                            EntityPlayerSP player = LongJump.mc.player;
                            player.motionY *= (double)0.35f;
                        } else if (LongJump.mc.player.motionY == -0.10306193759436909) {
                            EntityPlayerSP player2 = LongJump.mc.player;
                            player2.motionY *= (double)0.55f;
                        } else if (LongJump.mc.player.motionY == -0.13395038817442878) {
                            EntityPlayerSP player3 = LongJump.mc.player;
                            player3.motionY *= (double)0.67f;
                        } else if (LongJump.mc.player.motionY == -0.16635183030382) {
                            EntityPlayerSP player4 = LongJump.mc.player;
                            player4.motionY *= (double)0.69f;
                        } else if (LongJump.mc.player.motionY == -0.19088711097794803) {
                            EntityPlayerSP player5 = LongJump.mc.player;
                            player5.motionY *= (double)0.71f;
                        } else if (LongJump.mc.player.motionY == -0.21121925191528862) {
                            EntityPlayerSP player6 = LongJump.mc.player;
                            player6.motionY *= (double)0.2f;
                        } else if (LongJump.mc.player.motionY == -0.11979897632390576) {
                            EntityPlayerSP player7 = LongJump.mc.player;
                            player7.motionY *= (double)0.93f;
                        } else if (LongJump.mc.player.motionY == -0.18758479151225355) {
                            EntityPlayerSP player8 = LongJump.mc.player;
                            player8.motionY *= (double)0.72f;
                        } else if (LongJump.mc.player.motionY == -0.21075983825251726) {
                            EntityPlayerSP player9 = LongJump.mc.player;
                            player9.motionY *= (double)0.76f;
                        }
                        if (LongJump.mc.player.motionY < -0.2 && LongJump.mc.player.motionY > -0.24) {
                            EntityPlayerSP player10 = LongJump.mc.player;
                            player10.motionY *= 0.7;
                        }
                        if (LongJump.mc.player.motionY < -0.25 && LongJump.mc.player.motionY > -0.32) {
                            EntityPlayerSP player11 = LongJump.mc.player;
                            player11.motionY *= 0.8;
                        }
                        if (LongJump.mc.player.motionY < -0.35 && LongJump.mc.player.motionY > -0.8) {
                            EntityPlayerSP player12 = LongJump.mc.player;
                            player12.motionY *= 0.98;
                        }
                        if (LongJump.mc.player.motionY < -0.8 && LongJump.mc.player.motionY > -1.6) {
                            EntityPlayerSP player13 = LongJump.mc.player;
                            player13.motionY *= 0.99;
                        }
                    }
                    Phobos.timerManager.setTimer(0.85f);
                    double[] speedVals = new double[]{0.420606, 0.417924, 0.415258, 0.412609, 0.409977, 0.407361, 0.404761, 0.402178, 0.399611, 0.39706, 0.394525, 0.392, 0.3894, 0.38644, 0.383655, 0.381105, 0.37867, 0.37625, 0.37384, 0.37145, 0.369, 0.3666, 0.3642, 0.3618, 0.35945, 0.357, 0.354, 0.351, 0.348, 0.345, 0.342, 0.339, 0.336, 0.333, 0.33, 0.327, 0.324, 0.321, 0.318, 0.315, 0.312, 0.309, 0.307, 0.305, 0.303, 0.3, 0.297, 0.295, 0.293, 0.291, 0.289, 0.287, 0.285, 0.283, 0.281, 0.279, 0.277, 0.275, 0.273, 0.271, 0.269, 0.267, 0.265, 0.263, 0.261, 0.259, 0.257, 0.255, 0.253, 0.251, 0.249, 0.247, 0.245, 0.243, 0.241, 0.239, 0.237};
                    if (LongJump.mc.gameSettings.keyBindForward.pressed) {
                        try {
                            LongJump.mc.player.motionX = (double)xDir * speedVals[this.airTicks - 1] * 3.0;
                            LongJump.mc.player.motionZ = (double)zDir * speedVals[this.airTicks - 1] * 3.0;
                            break;
                        }
                        catch (ArrayIndexOutOfBoundsException e) {
                            return;
                        }
                    }
                    LongJump.mc.player.motionX = 0.0;
                    LongJump.mc.player.motionZ = 0.0;
                    break;
                }
                Phobos.timerManager.setTimer(1.0f);
                this.airTicks = 0;
                ++this.groundTicks;
                EntityPlayerSP player14 = LongJump.mc.player;
                player14.motionX /= 13.0;
                EntityPlayerSP player15 = LongJump.mc.player;
                player15.motionZ /= 13.0;
                if (this.groundTicks == 1) {
                    this.updatePosition(LongJump.mc.player.posX, LongJump.mc.player.posY, LongJump.mc.player.posZ);
                    this.updatePosition(LongJump.mc.player.posX + 0.0624, LongJump.mc.player.posY, LongJump.mc.player.posZ);
                    this.updatePosition(LongJump.mc.player.posX, LongJump.mc.player.posY + 0.419, LongJump.mc.player.posZ);
                    this.updatePosition(LongJump.mc.player.posX + 0.0624, LongJump.mc.player.posY, LongJump.mc.player.posZ);
                    this.updatePosition(LongJump.mc.player.posX, LongJump.mc.player.posY + 0.419, LongJump.mc.player.posZ);
                    break;
                }
                if (this.groundTicks <= 2) break;
                this.groundTicks = 0;
                LongJump.mc.player.motionX = (double)xDir * 0.3;
                LongJump.mc.player.motionZ = (double)zDir * 0.3;
                LongJump.mc.player.motionY = 0.424f;
                this.beganJump = true;
                break;
            }
        }
    }

    private void doVirtue(MoveEvent event) {
        if (this.mode.getValue() == Mode.VIRTUE && (LongJump.mc.player.moveForward != 0.0f || LongJump.mc.player.moveStrafing != 0.0f && !EntityUtil.isOnLiquid() && !EntityUtil.isInLiquid())) {
            if (this.stage == 0) {
                this.moveSpeed = (double)this.boost.getValue().floatValue() * this.getBaseMoveSpeed();
            } else if (this.stage == 1) {
                LongJump.mc.player.motionY = 0.42;
                event.setY(0.42);
                this.moveSpeed *= 2.149;
            } else if (this.stage == 2) {
                double difference = 0.66 * (this.lastDist - this.getBaseMoveSpeed());
                this.moveSpeed = this.lastDist - difference;
            } else {
                this.moveSpeed = this.lastDist - this.lastDist / 159.0;
            }
            this.moveSpeed = Math.max(this.getBaseMoveSpeed(), this.moveSpeed);
            this.setMoveSpeed(event, this.moveSpeed);
            List collidingList = LongJump.mc.world.getCollisionBoxes((Entity)LongJump.mc.player, LongJump.mc.player.getEntityBoundingBox().offset(0.0, LongJump.mc.player.motionY, 0.0));
            List collidingList2 = LongJump.mc.world.getCollisionBoxes((Entity)LongJump.mc.player, LongJump.mc.player.getEntityBoundingBox().offset(0.0, -0.4, 0.0));
            if (!(LongJump.mc.player.collidedVertically || collidingList.size() <= 0 && collidingList2.size() <= 0)) {
                LongJump.mc.player.motionY = -0.001;
                event.setY(-0.001);
            }
            ++this.stage;
        } else if (this.stage > 0) {
            this.disable();
        }
    }

    private void updatePosition(double x, double y, double z) {
        LongJump.mc.player.connection.sendPacket((Packet)new CPacketPlayer.Position(x, y, z, LongJump.mc.player.onGround));
    }

    private Block getBlock(BlockPos pos) {
        return LongJump.mc.world.getBlockState(pos).getBlock();
    }

    private double getDistance(EntityPlayer player, double distance) {
        List<AxisAlignedBB> boundingBoxes = player.world.getCollisionBoxes((Entity)player, player.getEntityBoundingBox().offset(0.0, -distance, 0.0));
        if (boundingBoxes.isEmpty()) {
            return 0.0;
        }
        double y = 0.0;
        for (AxisAlignedBB boundingBox : boundingBoxes) {
            if (!(boundingBox.maxY > y)) continue;
            y = boundingBox.maxY;
        }
        return player.posY - y;
    }

    private void setMoveSpeed(MoveEvent event, double speed) {
        MovementInput movementInput = LongJump.mc.player.movementInput;
        double forward = movementInput.moveForward;
        double strafe = movementInput.moveStrafe;
        float yaw = LongJump.mc.player.rotationYaw;
        if (forward == 0.0 && strafe == 0.0) {
            event.setX(0.0);
            event.setZ(0.0);
        } else {
            if (forward != 0.0) {
                if (strafe > 0.0) {
                    yaw += (float)(forward > 0.0 ? -45 : 45);
                } else if (strafe < 0.0) {
                    yaw += (float)(forward > 0.0 ? 45 : -45);
                }
                strafe = 0.0;
                if (forward > 0.0) {
                    forward = 1.0;
                } else if (forward < 0.0) {
                    forward = -1.0;
                }
            }
            double cos = Math.cos(Math.toRadians(yaw + 90.0f));
            double sin = Math.sin(Math.toRadians(yaw + 90.0f));
            event.setX(forward * speed * cos + strafe * speed * sin);
            event.setZ(forward * speed * sin - strafe * speed * cos);
        }
    }

    private double getBaseMoveSpeed() {
        double baseSpeed = 0.2873;
        if (LongJump.mc.player != null && LongJump.mc.player.isPotionActive(MobEffects.SPEED)) {
            int amplifier = Objects.requireNonNull(LongJump.mc.player.getActivePotionEffect(MobEffects.SPEED)).getAmplifier();
            baseSpeed *= 1.0 + 0.2 * (double)(amplifier + 1);
        }
        return baseSpeed;
    }

    public static enum Mode {
        VIRTUE,
        DIRECT,
        TICK;

    }
}

