
/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.passive.AbstractHorse
 *  net.minecraft.entity.passive.EntityTameable
 *  net.minecraft.entity.player.EntityPlayer
 */
package me.earth.phobos.features.modules.misc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import me.earth.phobos.features.modules.Module;
import me.earth.phobos.util.PlayerUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.AbstractHorse;
import net.minecraft.entity.passive.EntityTameable;
import net.minecraft.entity.player.EntityPlayer;

public class MobOwner
extends Module {
    private final Map<Entity, String> owners = new HashMap<Entity, String>();
    private final Map<Entity, UUID> toLookUp = new ConcurrentHashMap<Entity, UUID>();
    private final List<Entity> lookedUp = new ArrayList<Entity>();

    public MobOwner() {
        super("MobOwner", "Shows you who owns mobs.", Module.Category.MISC, false, false, false);
    }

    @Override
    public void onUpdate() {
        if (MobOwner.fullNullCheck()) {
            return;
        }
        if (PlayerUtil.timer.passedS(5.0)) {
            for (Map.Entry<Entity, UUID> entry : this.toLookUp.entrySet()) {
                Entity entity = entry.getKey();
                UUID uuid = entry.getValue();
                if (uuid != null) {
                    EntityPlayer owner = MobOwner.mc.world.getPlayerEntityByUUID(uuid);
                    if (owner == null) {
                        try {
                            String name = PlayerUtil.getNameFromUUID(uuid);
                            if (name != null) {
                                this.owners.put(entity, name);
                                this.lookedUp.add(entity);
                            }
                        }
                        catch (Exception e) {
                            this.lookedUp.add(entity);
                            this.toLookUp.remove(entry);
                        }
                        PlayerUtil.timer.reset();
                        break;
                    }
                    this.owners.put(entity, owner.getName());
                    this.lookedUp.add(entity);
                    continue;
                }
                this.lookedUp.add(entity);
                this.toLookUp.remove(entry);
            }
        }
        for (Entity entity : MobOwner.mc.world.getLoadedEntityList()) {
            AbstractHorse tameableEntity2;
            if (entity.getAlwaysRenderNameTag()) continue;
            if (entity instanceof EntityTameable) {
                EntityTameable tameableEntity = (EntityTameable)entity;
                if (!tameableEntity.isTamed() || tameableEntity.getOwnerId() == null) continue;
                if (this.owners.get(tameableEntity) != null) {
                    tameableEntity.setAlwaysRenderNameTag(true);
                    tameableEntity.setCustomNameTag(this.owners.get(tameableEntity));
                    continue;
                }
                if (this.lookedUp.contains(entity)) continue;
                this.toLookUp.put((Entity)tameableEntity, tameableEntity.getOwnerId());
                continue;
            }
            if (!(entity instanceof AbstractHorse) || !(tameableEntity2 = (AbstractHorse)entity).isTame() || tameableEntity2.getOwnerUniqueId() == null) continue;
            if (this.owners.get(tameableEntity2) != null) {
                tameableEntity2.setAlwaysRenderNameTag(true);
                tameableEntity2.setCustomNameTag(this.owners.get(tameableEntity2));
                continue;
            }
            if (this.lookedUp.contains(entity)) continue;
            this.toLookUp.put((Entity)tameableEntity2, tameableEntity2.getOwnerUniqueId());
        }
    }

    @Override
    public void onDisable() {
        for (Entity entity : MobOwner.mc.world.loadedEntityList) {
            if (!(entity instanceof EntityTameable) && !(entity instanceof AbstractHorse)) continue;
            try {
                entity.setAlwaysRenderNameTag(false);
            }
            catch (Exception exception) {}
        }
    }
}

