
/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  com.google.common.eventbus.Subscribe
 *  net.minecraft.entity.Entity
 *  net.minecraft.init.Blocks
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.Vec3d
 */
package me.earth.phobos.features.modules.movement;

import com.google.common.eventbus.Subscribe;
import me.earth.phobos.features.modules.Module;
import me.earth.phobos.features.setting.Setting;
import me.earth.phobos.util.EntityUtil;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class Anchor
extends Module {
    public static boolean Anchoring;
    private final Setting<Integer> pitch = this.register(new Setting<Integer>("Pitch", 60, 0, 90));
    private final Setting<Boolean> disable = this.register(new Setting<Boolean>("AutoDisable", true));
    private final Setting<Boolean> pull = this.register(new Setting<Boolean>("Pull", true));
    int holeblocks;

    public Anchor() {
        super("Anchor", "Automatically makes u go into holes.", Module.Category.MOVEMENT, false, false, false);
    }

    public boolean isBlockHole(BlockPos blockPos) {
        block28: {
            block27: {
                block26: {
                    block25: {
                        block24: {
                            block23: {
                                block22: {
                                    block21: {
                                        block20: {
                                            block19: {
                                                this.holeblocks = 0;
                                                if (Anchor.mc.world.getBlockState(blockPos.add(0, 3, 0)).getBlock() == Blocks.AIR) {
                                                    ++this.holeblocks;
                                                }
                                                if (Anchor.mc.world.getBlockState(blockPos.add(0, 2, 0)).getBlock() == Blocks.AIR) {
                                                    ++this.holeblocks;
                                                }
                                                if (Anchor.mc.world.getBlockState(blockPos.add(0, 1, 0)).getBlock() == Blocks.AIR) {
                                                    ++this.holeblocks;
                                                }
                                                if (Anchor.mc.world.getBlockState(blockPos.add(0, 0, 0)).getBlock() == Blocks.AIR) {
                                                    ++this.holeblocks;
                                                }
                                                if (Anchor.mc.world.getBlockState(blockPos.add(0, -1, 0)).getBlock() == Blocks.OBSIDIAN) break block19;
                                                if (Anchor.mc.world.getBlockState(blockPos.add(0, -1, 0)).getBlock() != Blocks.BEDROCK) break block20;
                                            }
                                            ++this.holeblocks;
                                        }
                                        if (Anchor.mc.world.getBlockState(blockPos.add(1, 0, 0)).getBlock() == Blocks.OBSIDIAN) break block21;
                                        if (Anchor.mc.world.getBlockState(blockPos.add(1, 0, 0)).getBlock() != Blocks.BEDROCK) break block22;
                                    }
                                    ++this.holeblocks;
                                }
                                if (Anchor.mc.world.getBlockState(blockPos.add(-1, 0, 0)).getBlock() == Blocks.OBSIDIAN) break block23;
                                if (Anchor.mc.world.getBlockState(blockPos.add(-1, 0, 0)).getBlock() != Blocks.BEDROCK) break block24;
                            }
                            ++this.holeblocks;
                        }
                        if (Anchor.mc.world.getBlockState(blockPos.add(0, 0, 1)).getBlock() == Blocks.OBSIDIAN) break block25;
                        if (Anchor.mc.world.getBlockState(blockPos.add(0, 0, 1)).getBlock() != Blocks.BEDROCK) break block26;
                    }
                    ++this.holeblocks;
                }
                if (Anchor.mc.world.getBlockState(blockPos.add(0, 0, -1)).getBlock() == Blocks.OBSIDIAN) break block27;
                if (Anchor.mc.world.getBlockState(blockPos.add(0, 0, -1)).getBlock() != Blocks.BEDROCK) break block28;
            }
            ++this.holeblocks;
        }
        return this.holeblocks >= 9;
    }

    public Vec3d GetCenter(double d, double d2, double d3) {
        double d4 = Math.floor(d) + 0.5;
        double d5 = Math.floor(d2);
        double d6 = Math.floor(d3) + 0.5;
        return new Vec3d(d4, d5, d6);
    }

    @Override
    @Subscribe
    public void onUpdate() {
        if (Anchor.mc.world == null) {
            return;
        }
        if (Anchor.mc.player.rotationPitch >= (float)this.pitch.getValue().intValue()) {
            if (this.isBlockHole(this.getPlayerPos().down(1)) || this.isBlockHole(this.getPlayerPos().down(2)) || this.isBlockHole(this.getPlayerPos().down(3)) || this.isBlockHole(this.getPlayerPos().down(4))) {
                Anchoring = true;
                if (!this.pull.getValue().booleanValue()) {
                    Anchor.mc.player.motionX = 0.0;
                    Anchor.mc.player.motionZ = 0.0;
                } else {
                    Vec3d center = this.GetCenter(Anchor.mc.player.posX, Anchor.mc.player.posY, Anchor.mc.player.posZ);
                    double d = Math.abs(center.x - Anchor.mc.player.posX);
                    double d2 = Math.abs(center.z - Anchor.mc.player.posZ);
                    if (!(d <= 0.1) || !(d2 <= 0.1)) {
                        double d3 = center.x - Anchor.mc.player.posX;
                        double d4 = center.z - Anchor.mc.player.posZ;
                        Anchor.mc.player.motionX = d3 / 2.0;
                        Anchor.mc.player.motionZ = d4 / 2.0;
                    }
                }
            } else {
                Anchoring = false;
            }
        }
        if (this.disable.getValue().booleanValue() && EntityUtil.isSafe((Entity)Anchor.mc.player)) {
            this.disable();
        }
    }

    @Override
    public void onDisable() {
        Anchoring = false;
        this.holeblocks = 0;
    }

    public BlockPos getPlayerPos() {
        return new BlockPos(Math.floor(Anchor.mc.player.posX), Math.floor(Anchor.mc.player.posY), Math.floor(Anchor.mc.player.posZ));
    }
}

