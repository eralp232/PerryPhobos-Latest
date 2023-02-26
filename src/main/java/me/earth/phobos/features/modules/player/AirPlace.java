
/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  net.minecraft.util.EnumFacing
 *  net.minecraft.util.math.BlockPos
 */
package me.earth.phobos.features.modules.player;

import me.earth.phobos.features.modules.Module;
import me.earth.phobos.features.setting.Setting;
import me.earth.phobos.util.BlockUtil;
import me.earth.phobos.util.Util;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;

public class AirPlace
extends Module {
    private final Setting<Mode> mode = this.register(new Setting<Mode>("Mode", Mode.UP));

    public AirPlace() {
        super("AirPlace", "Place blocks in the air for 1.13+ servers.", Module.Category.PLAYER, false, false, false);
    }

    @Override
    public void onEnable() {
        switch (this.mode.getValue()) {
            case UP: {
                BlockPos pos = Util.mc.player.getPosition().add(0, 1, 0);
                BlockUtil.placeBlock(pos, EnumFacing.UP, false);
                this.disable();
            }
            case DOWN: {
                BlockPos pos = Util.mc.player.getPosition().add(0, 0, 0);
                BlockUtil.placeBlock(pos, EnumFacing.DOWN, false);
                this.disable();
            }
        }
    }

    public static enum Mode {
        UP,
        DOWN;

    }
}

