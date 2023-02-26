
/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.renderer.culling.ICamera
 *  net.minecraft.client.renderer.entity.Render
 *  net.minecraft.entity.Entity
 *  net.minecraft.util.math.AxisAlignedBB
 */
package me.earth.phobos.mixin.mixins;

import net.minecraft.client.renderer.culling.ICamera;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.AxisAlignedBB;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(value={Render.class})
public class MixinRender<T extends Entity> {
    @Overwrite
    public boolean shouldRender(T livingEntity, ICamera camera, double camX, double camY, double camZ) {
        try {
            AxisAlignedBB axisalignedbb = livingEntity.getRenderBoundingBox().grow(0.5);
            if ((axisalignedbb.hasNaN() || axisalignedbb.getAverageEdgeLength() == 0.0) && livingEntity != null) {
                axisalignedbb = new AxisAlignedBB(((Entity)livingEntity).posX - 2.0, ((Entity)livingEntity).posY - 2.0, ((Entity)livingEntity).posZ - 2.0, ((Entity)livingEntity).posX + 2.0, ((Entity)livingEntity).posY + 2.0, ((Entity)livingEntity).posZ + 2.0);
            }
            return livingEntity.isInRangeToRender3d(camX, camY, camZ) && (((Entity)livingEntity).ignoreFrustumCheck || camera.isBoundingBoxInFrustum(axisalignedbb));
        }
        catch (Exception ignored) {
            return false;
        }
    }
}

