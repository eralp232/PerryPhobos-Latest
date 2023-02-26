
/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  net.minecraft.network.Packet
 *  net.minecraft.network.play.client.CPacketPlayer$Position
 */
package me.earth.phobos.features.modules.player;

import java.util.Objects;
import me.earth.phobos.features.modules.Module;
import me.earth.phobos.features.setting.Setting;
import me.earth.phobos.util.Util;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketPlayer;

public class Rubberband
extends Module {
    private final Setting<RubberMode> mode = this.register(new Setting<RubberMode>("Mode", RubberMode.Motion));
    private final Setting<Integer> Ym = this.register(new Setting<Integer>("Motion", Integer.valueOf(1), Integer.valueOf(-15), Integer.valueOf(15), v -> this.mode.getValue() == RubberMode.Motion));

    public Rubberband() {
        super("Rubberband", "Teleports u to the latest ground pos.", Module.Category.PLAYER, true, false, false);
    }

    @Override
    public void onUpdate() {
        switch (this.mode.getValue()) {
            case Motion: {
                Util.mc.player.motionY = this.Ym.getValue().intValue();
                break;
            }
            case Packet: {
                Objects.requireNonNull(mc.getConnection()).sendPacket((Packet)new CPacketPlayer.Position(Rubberband.mc.player.posX, Rubberband.mc.player.posY + (double)this.Ym.getValue().intValue(), Rubberband.mc.player.posZ, true));
                break;
            }
            case Teleport: {
                Rubberband.mc.player.setPositionAndUpdate(Rubberband.mc.player.posX, Rubberband.mc.player.posY + (double)this.Ym.getValue().intValue(), Rubberband.mc.player.posZ);
            }
        }
        this.toggle();
    }

    public static enum RubberMode {
        Motion,
        Teleport,
        Packet;

    }
}

