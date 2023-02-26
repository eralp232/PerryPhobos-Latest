
/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.init.Items
 *  net.minecraft.item.ItemEnderPearl
 *  net.minecraft.util.EnumHand
 *  net.minecraft.util.math.RayTraceResult
 *  net.minecraft.util.math.RayTraceResult$Type
 *  net.minecraft.world.World
 *  org.lwjgl.input.Mouse
 */
package me.earth.phobos.features.modules.player;

import me.earth.phobos.features.modules.Module;
import me.earth.phobos.features.setting.Setting;
import me.earth.phobos.util.InventoryUtil;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemEnderPearl;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import org.lwjgl.input.Mouse;

public class MCP
extends Module {
    private final Setting<Mode> mode = this.register(new Setting<Mode>("Mode", Mode.MIDDLECLICK));
    private final Setting<Boolean> antiFriend = this.register(new Setting<Boolean>("AntiFriend", true));
    private final Setting<Boolean> skyonly = this.register(new Setting<Boolean>("AboveHorizon", false));
    private final Setting<Boolean> skyonly2 = this.register(new Setting<Boolean>("Skyonly", false));
    private boolean clicked;

    public MCP() {
        super("MCP", "Throws a pearl.", Module.Category.PLAYER, false, false, false);
    }

    @Override
    public void onEnable() {
        if (!MCP.fullNullCheck() && this.mode.getValue() == Mode.TOGGLE) {
            this.throwPearl();
            this.disable();
        }
    }

    @Override
    public void onTick() {
        if (this.mode.getValue() == Mode.MIDDLECLICK) {
            if (Mouse.isButtonDown((int)2)) {
                if (!this.clicked) {
                    this.throwPearl();
                }
                this.clicked = true;
            } else {
                this.clicked = false;
            }
        }
    }

    private void throwPearl() {
        boolean offhand;
        RayTraceResult result;
        if (this.antiFriend.getValue().booleanValue() && (result = MCP.mc.objectMouseOver) != null && result.typeOfHit == RayTraceResult.Type.ENTITY && result.entityHit instanceof EntityPlayer) {
            return;
        }
        if (this.skyonly.getValue().booleanValue() && MCP.mc.player.rotationPitch > 0.0f) {
            return;
        }
        if (this.skyonly2.getValue().booleanValue() && (result = MCP.mc.objectMouseOver) != null && result.typeOfHit == RayTraceResult.Type.BLOCK) {
            return;
        }
        int pearlSlot = InventoryUtil.findHotbarBlock(ItemEnderPearl.class);
        boolean bl = offhand = MCP.mc.player.getHeldItemOffhand().getItem() == Items.ENDER_PEARL;
        if (pearlSlot != -1 || offhand) {
            int oldslot = MCP.mc.player.inventory.currentItem;
            if (!offhand) {
                InventoryUtil.switchToHotbarSlot(pearlSlot, false);
            }
            MCP.mc.playerController.processRightClick((EntityPlayer)MCP.mc.player, (World)MCP.mc.world, offhand ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND);
            if (!offhand) {
                InventoryUtil.switchToHotbarSlot(oldslot, false);
            }
        }
    }

    public static enum Mode {
        TOGGLE,
        MIDDLECLICK;

    }
}

