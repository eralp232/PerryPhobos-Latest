
/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.init.Items
 *  net.minecraft.init.PotionTypes
 *  net.minecraft.inventory.ClickType
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemAir
 *  net.minecraft.item.ItemBow
 *  net.minecraft.item.ItemStack
 *  net.minecraft.item.ItemTippedArrow
 *  net.minecraft.network.Packet
 *  net.minecraft.network.play.client.CPacketPlayerDigging
 *  net.minecraft.network.play.client.CPacketPlayerDigging$Action
 *  net.minecraft.potion.PotionUtils
 *  net.minecraft.util.math.BlockPos
 */
package me.earth.phobos.features.modules.combat;

import java.util.ArrayList;
import me.earth.phobos.features.command.Command;
import me.earth.phobos.features.modules.Module;
import me.earth.phobos.features.setting.Setting;
import me.earth.phobos.util.EntityUtil;
import me.earth.phobos.util.InventoryUtil;
import me.earth.phobos.util.RotationUtil;
import me.earth.phobos.util.TimerUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.init.PotionTypes;
import net.minecraft.inventory.ClickType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemAir;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemTippedArrow;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.potion.PotionUtils;
import net.minecraft.util.math.BlockPos;

public class Quiver
extends Module {
    private final Setting<Integer> delay = this.register(new Setting<Integer>("Delay", 200, 0, 500));
    private final Setting<Integer> holdLength = this.register(new Setting<Integer>("Hold Length", 350, 100, 1000));
    private final Setting<mainEnum> main = this.register(new Setting<mainEnum>("Main", mainEnum.SPEED));
    private final Setting<mainEnum> secondary = this.register(new Setting<mainEnum>("Secondary", mainEnum.STRENGTH));
    private final TimerUtil delayTimer = new TimerUtil();
    private final TimerUtil holdTimer = new TimerUtil();
    private int stage;
    private ArrayList<Integer> map;
    private int strSlot = -1;
    private int speedSlot = -1;
    private int oldSlot = 1;

    public Quiver() {
        super("Quiver", "Automatically shoots yourself with good effects.", Module.Category.COMBAT, true, false, false);
    }

    @Override
    public void onEnable() {
        if (Quiver.nullCheck()) {
            return;
        }
        InventoryUtil.switchToHotbarSlot(ItemBow.class, false);
        this.clean();
        this.oldSlot = Quiver.mc.player.inventory.currentItem;
        Quiver.mc.gameSettings.keyBindUseItem.pressed = false;
    }

    @Override
    public void onDisable() {
        if (Quiver.nullCheck()) {
            return;
        }
        InventoryUtil.switchToHotbarSlot(this.oldSlot, false);
        Quiver.mc.gameSettings.keyBindUseItem.pressed = false;
        this.clean();
    }

    @Override
    public void onUpdate() {
        if (Quiver.nullCheck()) {
            return;
        }
        if (Quiver.mc.currentScreen != null) {
            return;
        }
        if (InventoryUtil.findItemInventorySlot((Item)Items.BOW, true) == -1) {
            Command.sendMessage("Couldn't find bow in inventory! Toggling!");
            this.toggle();
        }
        RotationUtil.faceVector(EntityUtil.getInterpolatedPos((Entity)Quiver.mc.player, Quiver.mc.timer.elapsedPartialTicks).add(0.0, 3.0, 0.0), false);
        if (this.stage == 0) {
            this.map = this.mapArrows();
            for (int a : this.map) {
                ItemStack arrow = (ItemStack)Quiver.mc.player.inventoryContainer.getInventory().get(a);
                if ((PotionUtils.getPotionFromItem((ItemStack)arrow).equals(PotionTypes.STRENGTH) || PotionUtils.getPotionFromItem((ItemStack)arrow).equals(PotionTypes.STRONG_STRENGTH) || PotionUtils.getPotionFromItem((ItemStack)arrow).equals(PotionTypes.LONG_STRENGTH)) && this.strSlot == -1) {
                    this.strSlot = a;
                }
                if (!PotionUtils.getPotionFromItem((ItemStack)arrow).equals(PotionTypes.SWIFTNESS) && !PotionUtils.getPotionFromItem((ItemStack)arrow).equals(PotionTypes.LONG_SWIFTNESS) && !PotionUtils.getPotionFromItem((ItemStack)arrow).equals(PotionTypes.STRONG_SWIFTNESS) || this.speedSlot != -1) continue;
                this.speedSlot = a;
            }
            ++this.stage;
        } else if (this.stage == 1) {
            if (!this.delayTimer.passedMs(this.delay.getValue().intValue())) {
                return;
            }
            this.delayTimer.reset();
            ++this.stage;
        } else if (this.stage == 2) {
            this.switchTo(this.main.getValue());
            ++this.stage;
        } else if (this.stage == 3) {
            if (!this.delayTimer.passedMs(this.delay.getValue().intValue())) {
                return;
            }
            this.delayTimer.reset();
            ++this.stage;
        } else if (this.stage == 4) {
            Quiver.mc.gameSettings.keyBindUseItem.pressed = true;
            this.holdTimer.reset();
            ++this.stage;
        } else if (this.stage == 5) {
            if (!this.holdTimer.passedMs(this.holdLength.getValue().intValue())) {
                return;
            }
            this.holdTimer.reset();
            ++this.stage;
        } else if (this.stage == 6) {
            Quiver.mc.player.connection.sendPacket((Packet)new CPacketPlayerDigging(CPacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, Quiver.mc.player.getHorizontalFacing()));
            Quiver.mc.player.resetActiveHand();
            Quiver.mc.gameSettings.keyBindUseItem.pressed = false;
            ++this.stage;
        } else if (this.stage == 7) {
            if (!this.delayTimer.passedMs(this.delay.getValue().intValue())) {
                return;
            }
            this.delayTimer.reset();
            ++this.stage;
        } else if (this.stage == 8) {
            this.map = this.mapArrows();
            this.strSlot = -1;
            this.speedSlot = -1;
            for (int a : this.map) {
                ItemStack arrow = (ItemStack)Quiver.mc.player.inventoryContainer.getInventory().get(a);
                if ((PotionUtils.getPotionFromItem((ItemStack)arrow).equals(PotionTypes.STRENGTH) || PotionUtils.getPotionFromItem((ItemStack)arrow).equals(PotionTypes.STRONG_STRENGTH) || PotionUtils.getPotionFromItem((ItemStack)arrow).equals(PotionTypes.LONG_STRENGTH)) && this.strSlot == -1) {
                    this.strSlot = a;
                }
                if (!PotionUtils.getPotionFromItem((ItemStack)arrow).equals(PotionTypes.SWIFTNESS) && !PotionUtils.getPotionFromItem((ItemStack)arrow).equals(PotionTypes.LONG_SWIFTNESS) && !PotionUtils.getPotionFromItem((ItemStack)arrow).equals(PotionTypes.STRONG_SWIFTNESS) || this.speedSlot != -1) continue;
                this.speedSlot = a;
            }
            ++this.stage;
        }
        if (this.stage == 9) {
            this.switchTo(this.secondary.getValue());
            ++this.stage;
        } else if (this.stage == 10) {
            if (!this.delayTimer.passedMs(this.delay.getValue().intValue())) {
                return;
            }
            ++this.stage;
        } else if (this.stage == 11) {
            Quiver.mc.gameSettings.keyBindUseItem.pressed = true;
            this.holdTimer.reset();
            ++this.stage;
        } else if (this.stage == 12) {
            if (!this.holdTimer.passedMs(this.holdLength.getValue().intValue())) {
                return;
            }
            this.holdTimer.reset();
            ++this.stage;
        } else if (this.stage == 13) {
            Quiver.mc.player.connection.sendPacket((Packet)new CPacketPlayerDigging(CPacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, Quiver.mc.player.getHorizontalFacing()));
            Quiver.mc.player.resetActiveHand();
            Quiver.mc.gameSettings.keyBindUseItem.pressed = false;
            ++this.stage;
        } else if (this.stage == 14) {
            ArrayList<Integer> map = this.mapEmpty();
            if (!map.isEmpty()) {
                int a;
                a = map.get(0);
                Quiver.mc.playerController.windowClick(Quiver.mc.player.inventoryContainer.windowId, a, 0, ClickType.PICKUP, (EntityPlayer)Quiver.mc.player);
            }
            ++this.stage;
        } else if (this.stage == 15) {
            this.setEnabled(false);
        }
    }

    private void switchTo(Enum<mainEnum> mode) {
        if (mode.toString().equalsIgnoreCase("STRENGTH") && this.strSlot != -1) {
            this.switchTo(this.strSlot);
        }
        if (mode.toString().equalsIgnoreCase("SPEED") && this.speedSlot != -1) {
            this.switchTo(this.speedSlot);
        }
    }

    private ArrayList<Integer> mapArrows() {
        ArrayList<Integer> map = new ArrayList<Integer>();
        for (int a = 9; a < 45; ++a) {
            if (!(((ItemStack)Quiver.mc.player.inventoryContainer.getInventory().get(a)).getItem() instanceof ItemTippedArrow)) continue;
            ItemStack arrow = (ItemStack)Quiver.mc.player.inventoryContainer.getInventory().get(a);
            if (PotionUtils.getPotionFromItem((ItemStack)arrow).equals(PotionTypes.STRENGTH) || PotionUtils.getPotionFromItem((ItemStack)arrow).equals(PotionTypes.STRONG_STRENGTH) || PotionUtils.getPotionFromItem((ItemStack)arrow).equals(PotionTypes.LONG_STRENGTH)) {
                map.add(a);
            }
            if (!PotionUtils.getPotionFromItem((ItemStack)arrow).equals(PotionTypes.SWIFTNESS) && !PotionUtils.getPotionFromItem((ItemStack)arrow).equals(PotionTypes.LONG_SWIFTNESS) && !PotionUtils.getPotionFromItem((ItemStack)arrow).equals(PotionTypes.STRONG_SWIFTNESS)) continue;
            map.add(a);
        }
        return map;
    }

    private ArrayList<Integer> mapEmpty() {
        ArrayList<Integer> map = new ArrayList<Integer>();
        for (int a = 9; a < 45; ++a) {
            if (!(((ItemStack)Quiver.mc.player.inventoryContainer.getInventory().get(a)).getItem() instanceof ItemAir) && Quiver.mc.player.inventoryContainer.getInventory().get(a) != ItemStack.EMPTY) continue;
            map.add(a);
        }
        return map;
    }

    private void switchTo(int from) {
        if (from == 9) {
            return;
        }
        Quiver.mc.playerController.windowClick(Quiver.mc.player.inventoryContainer.windowId, from, 0, ClickType.PICKUP, (EntityPlayer)Quiver.mc.player);
        Quiver.mc.playerController.windowClick(Quiver.mc.player.inventoryContainer.windowId, 9, 0, ClickType.PICKUP, (EntityPlayer)Quiver.mc.player);
        Quiver.mc.playerController.windowClick(Quiver.mc.player.inventoryContainer.windowId, from, 0, ClickType.PICKUP, (EntityPlayer)Quiver.mc.player);
        Quiver.mc.playerController.updateController();
    }

    private void clean() {
        this.holdTimer.reset();
        this.delayTimer.reset();
        this.map = null;
        this.speedSlot = -1;
        this.strSlot = -1;
        this.stage = 0;
    }

    private static enum mainEnum {
        STRENGTH,
        SPEED;

    }
}

