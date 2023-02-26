
/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.item.EntityEnderCrystal
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.init.Items
 *  net.minecraft.network.Packet
 *  net.minecraft.network.play.client.CPacketAnimation
 *  net.minecraft.network.play.client.CPacketPlayer
 *  net.minecraft.network.play.client.CPacketUseEntity
 *  net.minecraft.network.play.client.CPacketUseEntity$Action
 *  net.minecraft.network.play.server.SPacketSpawnObject
 *  net.minecraft.util.EnumHand
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.Vec3d
 *  net.minecraftforge.fml.common.eventhandler.EventPriority
 *  net.minecraftforge.fml.common.eventhandler.SubscribeEvent
 */
package me.earth.phobos.features.modules.combat;

import java.util.ArrayList;
import java.util.List;
import me.earth.phobos.event.events.PacketEvent;
import me.earth.phobos.event.events.UpdateWalkingPlayerEvent;
import me.earth.phobos.features.modules.Module;
import me.earth.phobos.features.modules.combat.AutoCrystal;
import me.earth.phobos.features.setting.Setting;
import me.earth.phobos.util.BlockUtil;
import me.earth.phobos.util.DamageUtil;
import me.earth.phobos.util.EntityUtil;
import me.earth.phobos.util.MathUtil;
import me.earth.phobos.util.TimerUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketAnimation;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.client.CPacketUseEntity;
import net.minecraft.network.play.server.SPacketSpawnObject;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class AntiCrystal
extends Module {
    private final List<BlockPos> targets = new ArrayList<BlockPos>();
    private final TimerUtil timer = new TimerUtil();
    private final TimerUtil breakTimer = new TimerUtil();
    private final TimerUtil checkTimer = new TimerUtil();
    public Setting<Float> range = this.register(new Setting<Float>("Range", Float.valueOf(6.0f), Float.valueOf(0.0f), Float.valueOf(10.0f)));
    public Setting<Float> wallsRange = this.register(new Setting<Float>("WallsRange", Float.valueOf(3.5f), Float.valueOf(0.0f), Float.valueOf(10.0f)));
    public Setting<Float> minDmg = this.register(new Setting<Float>("MinDmg", Float.valueOf(6.0f), Float.valueOf(0.0f), Float.valueOf(40.0f)));
    public Setting<Float> selfDmg = this.register(new Setting<Float>("SelfDmg", Float.valueOf(2.0f), Float.valueOf(0.0f), Float.valueOf(10.0f)));
    public Setting<Integer> placeDelay = this.register(new Setting<Integer>("PlaceDelay", 0, 0, 500));
    public Setting<Integer> breakDelay = this.register(new Setting<Integer>("BreakDelay", 0, 0, 500));
    public Setting<Integer> checkDelay = this.register(new Setting<Integer>("CheckDelay", 0, 0, 500));
    public Setting<Integer> wasteAmount = this.register(new Setting<Integer>("WasteAmount", 1, 1, 5));
    public Setting<Switch> switcher = this.register(new Setting<Switch>("Switch", Switch.NONE));
    public Setting<Updates> mode = this.register(new Setting<Updates>("Updates", Updates.TICK));
    public Setting<Boolean> rotate = this.register(new Setting<Boolean>("Rotate", true));
    public Setting<Boolean> packet = this.register(new Setting<Boolean>("Packet", true));
    public Setting<Boolean> instant = this.register(new Setting<Boolean>("Predict", false));
    public Setting<Boolean> resetBreakTimer = this.register(new Setting<Boolean>("ResetBreak", true));
    public Setting<Integer> rotations = this.register(new Setting<Integer>("Spoofs", 1, 1, 20));
    private float yaw;
    private float pitch;
    private boolean rotating;
    private int rotationPacketsSpoofed;
    private Entity breakTarget;

    public AntiCrystal() {
        super("AntiCrystal", "Depending on you and ur opponents ping if urs is lower u can out place their ca to have essentially a godmode.", Module.Category.COMBAT, true, false, false);
    }

    @Override
    public void onToggle() {
        this.rotating = false;
    }

    private Entity getDeadlyCrystal() {
        Entity bestcrystal = null;
        float highestDamage = 0.0f;
        for (Entity crystal : AntiCrystal.mc.world.loadedEntityList) {
            float damage;
            if ( ! ( crystal instanceof EntityEnderCrystal ) || AntiCrystal.mc.player.getDistanceSq ( crystal ) > 169.0 || ( damage = DamageUtil.calculateDamage ( crystal , AntiCrystal.mc.player ) ) < this.minDmg.getValue ( ) )
                continue;
            if ( bestcrystal == null ) {
                bestcrystal = crystal;
                highestDamage = damage;
                continue;
            }
            if ( ! ( damage > highestDamage ) ) continue;
            bestcrystal = crystal;
            highestDamage = damage;
        }
        return bestcrystal;
    }

    private int getSafetyCrystals(Entity deadlyCrystal) {
        int count = 0;
        for (Entity entity : AntiCrystal.mc.world.loadedEntityList) {
            if (entity instanceof EntityEnderCrystal || DamageUtil.calculateDamage(entity, (Entity)AntiCrystal.mc.player) > 2.0f || deadlyCrystal.getDistanceSq(entity) > 144.0) continue;
            ++count;
        }
        return count;
    }

    private BlockPos getPlaceTarget(Entity deadlyCrystal) {
        BlockPos closestPos = null;
        float smallestDamage = 10.0f;
        for (BlockPos pos : BlockUtil.possiblePlacePositions(this.range.getValue().floatValue())) {
            float damage = DamageUtil.calculateDamage(pos, (Entity)AntiCrystal.mc.player);
            if (damage > 2.0f || deadlyCrystal.getDistanceSq(pos) > 144.0 || AntiCrystal.mc.player.getDistanceSq(pos) >= MathUtil.square(this.wallsRange.getValue().floatValue()) && BlockUtil.rayTracePlaceCheck(pos, true, 1.0f)) continue;
            if (closestPos == null) {
                smallestDamage = damage;
                closestPos = pos;
                continue;
            }
            if (!(damage < smallestDamage || damage == smallestDamage && AntiCrystal.mc.player.getDistanceSq(pos) < AntiCrystal.mc.player.getDistanceSq(closestPos))) continue;
            smallestDamage = damage;
            closestPos = pos;
        }
        return closestPos;
    }

    @SubscribeEvent
    public void onPacketSend(PacketEvent.Send event) {
        if (event.getStage() == 0 && this.rotate.getValue().booleanValue() && this.rotating) {
            if (event.getPacket() instanceof CPacketPlayer) {
                CPacketPlayer packet = (CPacketPlayer)event.getPacket();
                packet.yaw = this.yaw;
                packet.pitch = this.pitch;
            }
            ++this.rotationPacketsSpoofed;
            if (this.rotationPacketsSpoofed >= this.rotations.getValue()) {
                this.rotating = false;
                this.rotationPacketsSpoofed = 0;
            }
        }
    }

    @SubscribeEvent(priority=EventPriority.HIGH, receiveCanceled=true)
    public void onPacketReceive(PacketEvent.Receive event) {
        if (AntiCrystal.fullNullCheck()) {
            return;
        }
        if (event.getPacket() instanceof SPacketSpawnObject && this.instant.getValue().booleanValue()) {
            SPacketSpawnObject packet2 = (SPacketSpawnObject)event.getPacket();
            BlockPos pos = new BlockPos(packet2.getX(), packet2.getY(), packet2.getZ());
            if (packet2.getType() == 51 && this.targets.contains(pos.down()) && AutoCrystal.mc.player.getDistanceSq(pos) <= MathUtil.square(this.range.getValue().floatValue())) {
                this.attackCrystalPredict(packet2.getEntityID(), pos);
                this.targets.clear();
            }
        }
    }

    @Override
    public void onTick() {
        if (this.mode.getValue() == Updates.TICK) {
            this.doAntiCrystal();
        }
    }

    @Override
    public void onUpdate() {
        if (this.mode.getValue() == Updates.UPDATE) {
            this.doAntiCrystal();
        }
    }

    @SubscribeEvent
    public void onUpdateWalkingPlayer(UpdateWalkingPlayerEvent event) {
        if (this.mode.getValue() == Updates.WALKING) {
            this.doAntiCrystal();
        }
    }

    private void doAntiCrystal() {
        if (!AntiCrystal.fullNullCheck() && this.checkTimer.passedMs(this.checkDelay.getValue().intValue())) {
            Entity deadlyCrystal = this.getDeadlyCrystal();
            if (deadlyCrystal != null) {
                if (this.getSafetyCrystals(deadlyCrystal) < this.wasteAmount.getValue()) {
                    BlockPos placeTarget = this.getPlaceTarget(deadlyCrystal);
                    if (placeTarget != null) {
                        this.targets.add(placeTarget);
                    }
                    this.placeCrystal(deadlyCrystal);
                }
                this.breakTarget = this.getBreakTarget(deadlyCrystal);
                this.breakCrystal();
            }
            this.checkTimer.reset();
        }
    }

    public Entity getBreakTarget(Entity deadlyCrystal) {
        Entity smallestCrystal = null;
        float smallestDamage = 10.0f;
        for (Entity entity : AntiCrystal.mc.world.loadedEntityList) {
            float damage;
            if (!(entity instanceof EntityEnderCrystal) || (damage = DamageUtil.calculateDamage(entity, (Entity)AntiCrystal.mc.player)) > this.selfDmg.getValue().floatValue() || entity.getDistanceSq(deadlyCrystal) > 144.0 || AntiCrystal.mc.player.getDistanceSq(entity) > MathUtil.square(this.wallsRange.getValue().floatValue()) && EntityUtil.rayTraceHitCheck(entity, true)) continue;
            if (smallestCrystal == null) {
                smallestCrystal = entity;
                smallestDamage = damage;
                continue;
            }
            if (!(damage < smallestDamage || smallestDamage == damage && AntiCrystal.mc.player.getDistanceSq(entity) < AntiCrystal.mc.player.getDistanceSq(smallestCrystal))) continue;
            smallestCrystal = entity;
            smallestDamage = damage;
        }
        return smallestCrystal;
    }

    private void placeCrystal(Entity deadlyCrystal) {
        boolean offhand;
        boolean bl = offhand = AntiCrystal.mc.player.getHeldItemOffhand().getItem() == Items.END_CRYSTAL;
        if (this.timer.passedMs(this.placeDelay.getValue().intValue()) && (this.switcher.getValue() == Switch.NORMAL || this.switcher.getValue() == Switch.SILENT || AntiCrystal.mc.player.getHeldItemMainhand().getItem() == Items.END_CRYSTAL || offhand) && !this.targets.isEmpty() && this.getSafetyCrystals(deadlyCrystal) <= this.wasteAmount.getValue()) {
            if (this.switcher.getValue() == Switch.NORMAL && AntiCrystal.mc.player.getHeldItemMainhand().getItem() != Items.END_CRYSTAL && !offhand) {
                this.doSwitch();
            }
            if (!this.targets.isEmpty()) {
                this.rotateToPos(this.targets.get(this.targets.size() - 1));
                BlockUtil.placeCrystalOnBlock(this.targets.get(this.targets.size() - 1), offhand ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND, true, true, this.switcher.getValue() == Switch.SILENT);
            }
            this.timer.reset();
        }
    }

    private void doSwitch() {
        int crystalSlot;
        int n = crystalSlot = AntiCrystal.mc.player.getHeldItemMainhand().getItem() == Items.END_CRYSTAL ? AntiCrystal.mc.player.inventory.currentItem : -1;
        if (crystalSlot == -1) {
            for (int l = 0; l < 9; ++l) {
                if (AntiCrystal.mc.player.inventory.getStackInSlot(l).getItem() != Items.END_CRYSTAL) continue;
                crystalSlot = l;
                break;
            }
        }
        if (crystalSlot != -1) {
            AntiCrystal.mc.player.inventory.currentItem = crystalSlot;
        }
    }

    private void breakCrystal() {
        if (this.breakTimer.passedMs(this.breakDelay.getValue().intValue()) && this.breakTarget != null && DamageUtil.canBreakWeakness((EntityPlayer)AntiCrystal.mc.player)) {
            this.rotateTo(this.breakTarget);
            EntityUtil.attackEntity(this.breakTarget, this.packet.getValue(), true);
            this.breakTimer.reset();
            this.targets.clear();
        }
    }

    private void attackCrystalPredict(int entityID, BlockPos pos) {
        CPacketUseEntity attackPacket = new CPacketUseEntity();
        attackPacket.entityId = entityID;
        attackPacket.action = CPacketUseEntity.Action.ATTACK;
        AutoCrystal.mc.player.connection.sendPacket((Packet)attackPacket);
        AutoCrystal.mc.player.connection.sendPacket((Packet)new CPacketAnimation(EnumHand.MAIN_HAND));
        if (this.resetBreakTimer.getValue().booleanValue()) {
            this.breakTimer.reset();
        }
    }

    private void rotateTo(Entity entity) {
        if (this.rotate.getValue().booleanValue()) {
            float[] angle = MathUtil.calcAngle(AntiCrystal.mc.player.getPositionEyes(mc.getRenderPartialTicks()), entity.getPositionVector());
            this.yaw = angle[0];
            this.pitch = angle[1];
            this.rotating = true;
        }
    }

    private void rotateToPos(BlockPos pos) {
        if (this.rotate.getValue().booleanValue()) {
            float[] angle = MathUtil.calcAngle(AntiCrystal.mc.player.getPositionEyes(mc.getRenderPartialTicks()), new Vec3d((double)((float)pos.getX() + 0.5f), (double)((float)pos.getY() - 0.5f), (double)((float)pos.getZ() + 0.5f)));
            this.yaw = angle[0];
            this.pitch = angle[1];
            this.rotating = true;
        }
    }

    public static enum Updates {
        TICK,
        UPDATE,
        WALKING;

    }

    public static enum Switch {
        NONE,
        SILENT,
        NORMAL;

    }
}

