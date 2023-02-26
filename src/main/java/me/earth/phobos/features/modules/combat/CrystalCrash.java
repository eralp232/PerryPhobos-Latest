
/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.item.EntityEnderCrystal
 *  net.minecraft.init.Items
 *  net.minecraft.network.Packet
 *  net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock
 *  net.minecraft.util.EnumFacing
 *  net.minecraft.util.EnumHand
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.RayTraceResult
 *  net.minecraft.util.math.Vec3d
 *  net.minecraft.world.World
 *  net.minecraftforge.fml.common.eventhandler.SubscribeEvent
 *  net.minecraftforge.fml.common.gameevent.TickEvent$ClientTickEvent
 *  net.minecraftforge.fml.common.gameevent.TickEvent$Phase
 */
package me.earth.phobos.features.modules.combat;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import me.earth.phobos.features.modules.Module;
import me.earth.phobos.features.setting.Setting;
import me.earth.phobos.util.BlockUtil;
import me.earth.phobos.util.InventoryUtil;
import me.earth.phobos.util.TimerUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.init.Items;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class CrystalCrash
extends Module {
    private final Setting<Boolean> oneDot15 = this.register(new Setting<Boolean>("1.15", false));
    private final Setting<Float> placeRange = this.register(new Setting<Float>("PlaceRange", Float.valueOf(6.0f), Float.valueOf(0.0f), Float.valueOf(10.0f)));
    private final Setting<Integer> crystals = this.register(new Setting<Integer>("Packets", 25, 0, 100));
    private final Setting<Integer> coolDown = this.register(new Setting<Integer>("CoolDown", 400, 0, 1000));
    private final Setting<InventoryUtil.Switch> switchMode = this.register(new Setting<InventoryUtil.Switch>("Switch", InventoryUtil.Switch.NORMAL));
    private final TimerUtil timer = new TimerUtil();
    private final List<Integer> entityIDs = new ArrayList<Integer>();
    public Setting<Integer> sort = this.register(new Setting<Integer>("Sort", 0, 0, 2));
    private boolean offhand;
    private boolean mainhand;
    private int lastHotbarSlot = -1;
    private boolean switchedItem;
    private boolean chinese;
    private int currentID = -1000;

    public CrystalCrash() {
        super("CrystalCrash", "Attempts to crash chinese AutoCrystals.", Module.Category.COMBAT, false, false, true);
    }

    @Override
    public void onEnable() {
        this.chinese = false;
        if (CrystalCrash.fullNullCheck() || !this.timer.passedMs(this.coolDown.getValue().intValue())) {
            this.disable();
            return;
        }
        this.lastHotbarSlot = CrystalCrash.mc.player.inventory.currentItem;
        this.placeCrystals();
        this.disable();
    }

    @Override
    public void onDisable() {
        if (!CrystalCrash.fullNullCheck()) {
            for (int i : this.entityIDs) {
                CrystalCrash.mc.world.removeEntityFromWorld(i);
            }
        }
        this.entityIDs.clear();
        this.currentID = -1000;
        this.timer.reset();
    }

    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent event) {
        if (CrystalCrash.fullNullCheck() || event.phase == TickEvent.Phase.START || this.isOff() && this.timer.passedMs(10L)) {
            return;
        }
        this.switchItem(true);
    }

    private void placeCrystals() {
        this.offhand = CrystalCrash.mc.player.getHeldItemOffhand().getItem() == Items.END_CRYSTAL;
        this.mainhand = CrystalCrash.mc.player.getHeldItemMainhand().getItem() == Items.END_CRYSTAL;
        int crystalcount = 0;
        List<BlockPos> blocks = BlockUtil.possiblePlacePositions(this.placeRange.getValue().floatValue(), false, this.oneDot15.getValue(), false);
        if (this.sort.getValue() == 1) {
            blocks.sort(Comparator.comparingDouble(hole -> CrystalCrash.mc.player.getDistanceSq(hole)));
        } else if (this.sort.getValue() == 2) {
            blocks.sort(Comparator.comparingDouble(hole -> -CrystalCrash.mc.player.getDistanceSq(hole)));
        }
        for (BlockPos pos : blocks) {
            if (this.isOff() || crystalcount >= this.crystals.getValue()) break;
            if (!BlockUtil.canPlaceCrystal(pos, false, this.oneDot15.getValue(), false)) continue;
            this.placeCrystal(pos);
            ++crystalcount;
        }
    }

    private void placeCrystal(BlockPos pos) {
        if (!(this.chinese || this.mainhand || this.offhand || this.switchItem(false))) {
            this.disable();
            return;
        }
        RayTraceResult result = CrystalCrash.mc.world.rayTraceBlocks(new Vec3d(CrystalCrash.mc.player.posX, CrystalCrash.mc.player.posY + (double)CrystalCrash.mc.player.getEyeHeight(), CrystalCrash.mc.player.posZ), new Vec3d((double)pos.getX() + 0.5, (double)pos.getY() - 0.5, (double)pos.getZ() + 0.5));
        EnumFacing facing = result == null || result.sideHit == null ? EnumFacing.UP : result.sideHit;
        CrystalCrash.mc.player.connection.sendPacket((Packet)new CPacketPlayerTryUseItemOnBlock(pos, facing, this.offhand ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND, 0.0f, 0.0f, 0.0f));
        CrystalCrash.mc.player.swingArm(EnumHand.MAIN_HAND);
        EntityEnderCrystal fakeCrystal = new EntityEnderCrystal((World)CrystalCrash.mc.world, (double)((float)pos.getX() + 0.5f), (double)(pos.getY() + 1), (double)((float)pos.getZ() + 0.5f));
        int newID = this.currentID--;
        this.entityIDs.add(newID);
        CrystalCrash.mc.world.addEntityToWorld(newID, (Entity)fakeCrystal);
    }

    private boolean switchItem(boolean back) {
        this.chinese = true;
        if (this.offhand) {
            return true;
        }
        boolean[] value = InventoryUtil.switchItemToItem(back, this.lastHotbarSlot, this.switchedItem, this.switchMode.getValue(), Items.END_CRYSTAL);
        this.switchedItem = value[0];
        return value[1];
    }
}

