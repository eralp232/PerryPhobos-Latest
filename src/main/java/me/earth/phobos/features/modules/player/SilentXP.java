
/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.init.Items
 *  net.minecraft.item.ItemExpBottle
 *  net.minecraft.util.EnumHand
 *  net.minecraft.util.math.RayTraceResult
 *  net.minecraft.util.math.RayTraceResult$Type
 *  net.minecraft.world.World
 *  org.lwjgl.input.Keyboard
 *  org.lwjgl.input.Mouse
 */
package me.earth.phobos.features.modules.player;

import me.earth.phobos.features.modules.Module;
import me.earth.phobos.features.setting.Bind;
import me.earth.phobos.features.setting.Setting;
import me.earth.phobos.util.InventoryUtil;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemExpBottle;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

public class SilentXP
extends Module {
    public Setting<Mode> mode = this.register(new Setting<Mode>("Mode", Mode.MIDDLECLICK));
    public Setting<Boolean> antiFriend = this.register(new Setting<Boolean>("AntiFriend", true));
    public Setting<Bind> key = this.register(new Setting<Bind>("Key", new Bind(-1), v -> this.mode.getValue() != Mode.MIDDLECLICK));
    public Setting<Boolean> groundOnly = this.register(new Setting<Boolean>("BelowHorizon", false));
    private boolean last;
    private boolean on;

    public SilentXP() {
        super("SilentXP", "Silent XP.", Module.Category.PLAYER, false, false, false);
    }

    @Override
    public void onUpdate() {
        if (SilentXP.fullNullCheck()) {
            return;
        }
        switch (this.mode.getValue()) {
            case PRESS: {
                if (!this.key.getValue().isDown()) break;
                this.throwXP(false);
                break;
            }
            case TOGGLE: {
                if (!this.toggled()) break;
                this.throwXP(false);
                break;
            }
            default: {
                if (this.groundOnly.getValue().booleanValue() && SilentXP.mc.player.rotationPitch < 0.0f) {
                    return;
                }
                if (!Mouse.isButtonDown((int)2)) break;
                this.throwXP(true);
            }
        }
    }

    private boolean toggled() {
        if (this.key.getValue().getKey() == -1) {
            return false;
        }
        if (!Keyboard.isKeyDown((int)this.key.getValue().getKey())) {
            this.last = true;
        } else {
            if (Keyboard.isKeyDown((int)this.key.getValue().getKey()) && this.last && !this.on) {
                this.last = false;
                this.on = true;
                return true;
            }
            if (Keyboard.isKeyDown((int)this.key.getValue().getKey()) && this.last && this.on) {
                this.last = false;
                this.on = false;
                return false;
            }
        }
        return this.on;
    }

    private void throwXP(boolean mcf) {
        boolean offhand;
        RayTraceResult result;
        if (mcf && this.antiFriend.getValue().booleanValue() && (result = SilentXP.mc.objectMouseOver) != null && result.typeOfHit == RayTraceResult.Type.ENTITY && result.entityHit instanceof EntityPlayer) {
            return;
        }
        int xpSlot = InventoryUtil.findHotbarBlock(ItemExpBottle.class);
        boolean bl = offhand = SilentXP.mc.player.getHeldItemOffhand().getItem() == Items.EXPERIENCE_BOTTLE;
        if (xpSlot != -1 || offhand) {
            int oldslot = SilentXP.mc.player.inventory.currentItem;
            if (!offhand) {
                InventoryUtil.switchToHotbarSlot(xpSlot, false);
            }
            SilentXP.mc.playerController.processRightClick((EntityPlayer)SilentXP.mc.player, (World)SilentXP.mc.world, offhand ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND);
            if (!offhand) {
                InventoryUtil.switchToHotbarSlot(oldslot, false);
            }
        }
    }

    public static enum Mode {
        MIDDLECLICK,
        TOGGLE,
        PRESS;

    }
}

