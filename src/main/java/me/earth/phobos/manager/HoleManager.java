
/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.Block
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.init.Blocks
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.Vec3i
 */
package me.earth.phobos.manager;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import me.earth.phobos.features.Feature;
import me.earth.phobos.features.modules.client.Management;
import me.earth.phobos.features.modules.combat.HoleFiller;
import me.earth.phobos.features.modules.movement.HoleTP;
import me.earth.phobos.features.modules.render.HoleESP;
import me.earth.phobos.util.BlockUtil;
import me.earth.phobos.util.EntityUtil;
import me.earth.phobos.util.TimerUtil;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;

public class HoleManager
extends Feature
implements Runnable {
    private static final BlockPos[] surroundOffset = BlockUtil.toBlockPos(EntityUtil.getOffsets(0, true, true));
    private final List<BlockPos> midSafety = new ArrayList<BlockPos>();
    private final TimerUtil syncTimer = new TimerUtil();
    private final AtomicBoolean shouldInterrupt = new AtomicBoolean(false);
    private final TimerUtil holeTimer = new TimerUtil();
    private List<BlockPos> holes = new ArrayList<BlockPos>();
    private ScheduledExecutorService executorService;
    private int lastUpdates;
    private Thread thread;

    public void update() {
        if (Management.getInstance().holeThread.getValue() == Management.ThreadMode.WHILE) {
            if (this.thread == null || this.thread.isInterrupted() || !this.thread.isAlive() || this.syncTimer.passedMs(Management.getInstance().holeSync.getValue().intValue())) {
                if (this.thread == null) {
                    this.thread = new Thread(this);
                } else if (this.syncTimer.passedMs(Management.getInstance().holeSync.getValue().intValue()) && !this.shouldInterrupt.get()) {
                    this.shouldInterrupt.set(true);
                    this.syncTimer.reset();
                    return;
                }
                if (this.thread != null && (this.thread.isInterrupted() || !this.thread.isAlive())) {
                    this.thread = new Thread(this);
                }
                if (this.thread != null && this.thread.getState() == Thread.State.NEW) {
                    try {
                        this.thread.start();
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                    }
                    this.syncTimer.reset();
                }
            }
        } else if (Management.getInstance().holeThread.getValue() == Management.ThreadMode.WHILE) {
            if (this.executorService == null || this.executorService.isTerminated() || this.executorService.isShutdown() || this.syncTimer.passedMs(10000L) || this.lastUpdates != Management.getInstance().holeUpdates.getValue()) {
                this.lastUpdates = Management.getInstance().holeUpdates.getValue();
                if (this.executorService != null) {
                    this.executorService.shutdown();
                }
                this.executorService = this.getExecutor();
            }
        } else if (this.holeTimer.passedMs(Management.getInstance().holeUpdates.getValue().intValue()) && !HoleManager.fullNullCheck() && (HoleESP.getInstance().isOn() || HoleFiller.getInstance().isOn() || HoleTP.getInstance().isOn())) {
            this.holes = this.calcHoles();
            this.holeTimer.reset();
        }
    }

    public void settingChanged() {
        if (this.executorService != null) {
            this.executorService.shutdown();
        }
        if (this.thread != null) {
            this.shouldInterrupt.set(true);
        }
    }

    private ScheduledExecutorService getExecutor() {
        ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
        service.scheduleAtFixedRate(this, 0L, Management.getInstance().holeUpdates.getValue().intValue(), TimeUnit.MILLISECONDS);
        return service;
    }

    @Override
    public void run() {
        if (Management.getInstance().holeThread.getValue() == Management.ThreadMode.WHILE) {
            while (true) {
                if (this.shouldInterrupt.get()) {
                    this.shouldInterrupt.set(false);
                    this.syncTimer.reset();
                    Thread.currentThread().interrupt();
                    return;
                }
                if (!HoleManager.fullNullCheck() && (HoleESP.getInstance().isOn() || HoleFiller.getInstance().isOn() || HoleTP.getInstance().isOn())) {
                    this.holes = this.calcHoles();
                }
                try {
                    Thread.sleep(Management.getInstance().holeUpdates.getValue().intValue());
                }
                catch (InterruptedException e) {
                    this.thread.interrupt();
                    e.printStackTrace();
                }
            }
        }
        if (Management.getInstance().holeThread.getValue() == Management.ThreadMode.POOL && !HoleManager.fullNullCheck() && (HoleESP.getInstance().isOn() || HoleFiller.getInstance().isOn())) {
            this.holes = this.calcHoles();
        }
    }

    public List<BlockPos> getHoles() {
        return this.holes;
    }

    public List<BlockPos> getMidSafety() {
        return this.midSafety;
    }

    public List<BlockPos> getSortedHoles() {
        if (HoleManager.fullNullCheck()) {
            return null;
        }
        this.holes.sort(Comparator.comparingDouble(hole -> HoleManager.mc.player.getDistanceSq(hole)));
        return this.getHoles();
    }

    public List<BlockPos> calcHoles() {
        ArrayList<BlockPos> safeSpots = new ArrayList<BlockPos>();
        this.midSafety.clear();
        List<BlockPos> positions = BlockUtil.getSphere(EntityUtil.getPlayerPos((EntityPlayer)HoleManager.mc.player), Management.getInstance().holeRange.getValue().floatValue(), Management.getInstance().holeRange.getValue().intValue(), false, true, 0);
        for (BlockPos pos : positions) {
            if (HoleManager.fullNullCheck()) {
                return null;
            }
            if (!HoleManager.mc.world.getBlockState(pos).getBlock().equals(Blocks.AIR) || !HoleManager.mc.world.getBlockState(pos.add(0, 1, 0)).getBlock().equals(Blocks.AIR) || !HoleManager.mc.world.getBlockState(pos.add(0, 2, 0)).getBlock().equals(Blocks.AIR)) continue;
            boolean isSafe = true;
            boolean midSafe = true;
            for (BlockPos offset : surroundOffset) {
                Block block = HoleManager.mc.world.getBlockState(pos.add((Vec3i)offset)).getBlock();
                if (BlockUtil.isBlockUnSolid(block)) {
                    midSafe = false;
                }
                if (block == Blocks.BEDROCK || block == Blocks.OBSIDIAN || block == Blocks.ENDER_CHEST || block == Blocks.ANVIL) continue;
                isSafe = false;
            }
            if (isSafe) {
                safeSpots.add(pos);
            }
            if (!midSafe) continue;
            this.midSafety.add(pos);
        }
        return safeSpots;
    }

    public boolean isSafe(BlockPos pos) {
        boolean isSafe = true;
        for (BlockPos offset : surroundOffset) {
            Block block = HoleManager.mc.world.getBlockState(pos.add((Vec3i)offset)).getBlock();
            if (block == Blocks.BEDROCK) continue;
            isSafe = false;
            break;
        }
        return isSafe;
    }
}

