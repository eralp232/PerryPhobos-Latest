
/*
 * Decompiled with CFR 0.151.
 */
package me.earth.phobos.features.modules.movement;

import me.earth.phobos.Phobos;
import me.earth.phobos.features.modules.Module;
import me.earth.phobos.features.setting.Setting;

public class ReverseStep
extends Module {
    private final Setting<Mode> mode = this.register(new Setting<Mode>("Mode", Mode.NEW));
    int delay;

    public ReverseStep() {
        super("ReverseStep", "Makes u fall faster.", Module.Category.MOVEMENT, true, false, false);
    }

    @Override
    public void onUpdate() {
        if (this.delay > 0) {
            --this.delay;
        }
        if (this.mode.getValue() == Mode.NEW) {
            if (ReverseStep.mc.player.motionY > (double)-0.06f) {
                this.delay = 10;
            }
            if (ReverseStep.mc.player.fallDistance > 0.0f && ReverseStep.mc.player.fallDistance < 1.0f && this.delay == 0 && !Phobos.moduleManager.isModuleEnabled("Strafe") && !ReverseStep.mc.player.isInWater() && !ReverseStep.mc.player.isInLava()) {
                ReverseStep.mc.player.motionY = -3.9200038147008747;
            }
        }
        if (!(this.mode.getValue() != Mode.OLD || ReverseStep.mc.player == null || ReverseStep.mc.world == null || !ReverseStep.mc.player.onGround || ReverseStep.mc.player.isSneaking() || ReverseStep.mc.player.isInWater() || ReverseStep.mc.player.isDead || ReverseStep.mc.player.isInLava() || ReverseStep.mc.player.isOnLadder() || ReverseStep.mc.player.noClip || ReverseStep.mc.gameSettings.keyBindSneak.isKeyDown() || ReverseStep.mc.gameSettings.keyBindJump.isKeyDown() || Phobos.moduleManager.isModuleEnabled("Strafe") || !ReverseStep.mc.player.onGround)) {
            ReverseStep.mc.player.motionY -= 1.0;
        }
    }

    private static enum Mode {
        OLD,
        NEW;

    }
}

