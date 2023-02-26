
/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.item.EntityXPOrb
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.init.Items
 *  net.minecraft.item.ItemStack
 *  net.minecraft.network.Packet
 *  net.minecraft.network.play.client.CPacketHeldItemChange
 *  net.minecraft.network.play.client.CPacketPlayer$Rotation
 *  net.minecraft.network.play.client.CPacketPlayerTryUseItem
 *  net.minecraft.util.EnumHand
 *  net.minecraft.util.NonNullList
 */
package me.earth.phobos.features.modules.player;

import me.earth.phobos.features.modules.Module;
import me.earth.phobos.features.setting.Setting;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketHeldItemChange;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.client.CPacketPlayerTryUseItem;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;

public class PacketMend
extends Module {
    public Setting<Boolean> sneakOnly = this.register(new Setting<Boolean>("SneakOnly", false));
    public Setting<Boolean> noEntityCollision = this.register(new Setting<Boolean>("No Collision", true));
    public Setting<Boolean> silentSwitch = this.register(new Setting<Boolean>("Silent Switch", true));
    public Setting<Integer> minDamage = this.register(new Setting<Integer>("Min Damage", 100, 1, 100));
    public Setting<Integer> maxHeal = this.register(new Setting<Integer>("Repair To", 90, 1, 100));
    public Setting<Boolean> predict = this.register(new Setting<Boolean>("Predict", false));
    public Setting<Boolean> DisableWhenDone = this.register(new Setting<Boolean>("AutoDisable", true));
    public Setting<Boolean> rotate = this.register(new Setting<Boolean>("Rotate", true));
    char toMend;

    public PacketMend() {
        super("PacketMend", "Automatically mends.", Module.Category.PLAYER, true, false, false);
    }

    @Override
    public void onUpdate() {
        if (PacketMend.mc.player == null || PacketMend.mc.world == null) {
            return;
        }
        int sumOfDamage = 0;
        NonNullList armour = PacketMend.mc.player.inventory.armorInventory;
        for (int i = 0; i < armour.size(); ++i) {
            ItemStack itemStack = (ItemStack)armour.get(i);
            if (itemStack.isEmpty) continue;
            float damageOnArmor = itemStack.getMaxDamage() - itemStack.getItemDamage();
            float damagePercent = 100.0f - 100.0f * (1.0f - damageOnArmor / (float)itemStack.getMaxDamage());
            if (!(damagePercent >= (float)this.maxHeal.getValue().intValue()) && this.DisableWhenDone.getValue().booleanValue()) {
                this.toggle();
            }
            if (damagePercent <= (float)this.maxHeal.getValue().intValue()) {
                if (damagePercent <= (float)this.minDamage.getValue().intValue()) {
                    this.toMend = (char)(this.toMend | 1 << i);
                }
                if (!this.predict.getValue().booleanValue()) continue;
                sumOfDamage = (int)((float)sumOfDamage + ((float)(itemStack.getMaxDamage() * this.maxHeal.getValue()) / 100.0f - (float)(itemStack.getMaxDamage() - itemStack.getItemDamage())));
                continue;
            }
            this.toMend = (char)(this.toMend & ~(1 << i));
        }
        if (this.toMend > '\u0000') {
            if (this.predict.getValue().booleanValue()) {
                int totalXp = PacketMend.mc.world.loadedEntityList.stream().filter(entity -> entity instanceof EntityXPOrb).filter(entity -> entity.getDistanceSq((Entity)PacketMend.mc.player) <= 1.0).mapToInt(entity -> ((EntityXPOrb)entity).xpValue).sum();
                if (totalXp * 2 < sumOfDamage) {
                    this.mendArmor(PacketMend.mc.player.inventory.currentItem);
                }
            } else {
                this.mendArmor(PacketMend.mc.player.inventory.currentItem);
            }
        }
    }

    private void mendArmor(int oldSlot) {
        if (this.noEntityCollision.getValue().booleanValue()) {
            for (EntityPlayer entityPlayer : PacketMend.mc.world.playerEntities) {
                if (!(entityPlayer.getDistance((Entity)PacketMend.mc.player) < 1.0f) || entityPlayer == PacketMend.mc.player) continue;
                return;
            }
        }
        if (this.sneakOnly.getValue().booleanValue() && !PacketMend.mc.player.isSneaking()) {
            return;
        }
        int newSlot = this.findXPSlot();
        if (newSlot == -1) {
            return;
        }
        if (oldSlot != newSlot) {
            if (this.silentSwitch.getValue().booleanValue()) {
                PacketMend.mc.player.connection.sendPacket((Packet)new CPacketHeldItemChange(newSlot));
            } else {
                PacketMend.mc.player.inventory.currentItem = newSlot;
            }
            PacketMend.mc.playerController.syncCurrentPlayItem();
        }
        if (this.rotate.getValue().booleanValue()) {
            PacketMend.mc.player.connection.sendPacket((Packet)new CPacketPlayer.Rotation(0.0f, 90.0f, true));
        }
        PacketMend.mc.player.connection.sendPacket((Packet)new CPacketPlayerTryUseItem(EnumHand.MAIN_HAND));
        if (this.silentSwitch.getValue().booleanValue()) {
            PacketMend.mc.player.connection.sendPacket((Packet)new CPacketHeldItemChange(oldSlot));
        } else {
            PacketMend.mc.player.inventory.currentItem = oldSlot;
        }
        PacketMend.mc.playerController.syncCurrentPlayItem();
    }

    private int findXPSlot() {
        int slot = -1;
        for (int i = 0; i < 9; ++i) {
            if (PacketMend.mc.player.inventory.getStackInSlot(i).getItem() != Items.EXPERIENCE_BOTTLE) continue;
            slot = i;
            break;
        }
        return slot;
    }
}

