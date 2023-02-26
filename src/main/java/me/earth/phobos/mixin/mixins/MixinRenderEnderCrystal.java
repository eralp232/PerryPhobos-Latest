
/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.model.ModelBase
 *  net.minecraft.client.renderer.GlStateManager
 *  net.minecraft.client.renderer.entity.RenderEnderCrystal
 *  net.minecraft.entity.Entity
 *  net.minecraft.util.ResourceLocation
 *  org.lwjgl.opengl.GL11
 */
package me.earth.phobos.mixin.mixins;

import java.awt.Color;
import me.earth.phobos.event.events.RenderEntityModelEvent;
import me.earth.phobos.features.modules.client.Colors;
import me.earth.phobos.features.modules.render.CrystalModifier;
import me.earth.phobos.util.EntityUtil;
import me.earth.phobos.util.RenderUtil;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderEnderCrystal;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value={RenderEnderCrystal.class})
public class MixinRenderEnderCrystal {
    private static final ResourceLocation glint = new ResourceLocation("textures/glint.png");
    @Shadow
    @Final
    private static ResourceLocation ENDER_CRYSTAL_TEXTURES;

    @Redirect(method={"doRender"}, at=@At(value="INVOKE", target="Lnet/minecraft/client/model/ModelBase;render(Lnet/minecraft/entity/Entity;FFFFFF)V"))
    public void renderModelBaseHook(ModelBase model, Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        if (CrystalModifier.INSTANCE.isEnabled()) {
            if (CrystalModifier.INSTANCE.animateScale.getValue().booleanValue() && CrystalModifier.INSTANCE.scaleMap.containsKey(entity)) {
                GlStateManager.scale((float)CrystalModifier.INSTANCE.scaleMap.get(entity).floatValue(), (float)CrystalModifier.INSTANCE.scaleMap.get(entity).floatValue(), (float)CrystalModifier.INSTANCE.scaleMap.get(entity).floatValue());
            } else {
                GlStateManager.scale((float)CrystalModifier.INSTANCE.scale.getValue().floatValue(), (float)CrystalModifier.INSTANCE.scale.getValue().floatValue(), (float)CrystalModifier.INSTANCE.scale.getValue().floatValue());
            }
        }
        if (CrystalModifier.INSTANCE.isEnabled() && CrystalModifier.INSTANCE.wireframe.getValue().booleanValue()) {
            RenderEntityModelEvent event = new RenderEntityModelEvent(0, model, entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
            CrystalModifier.INSTANCE.onRenderModel(event);
        }
        if (CrystalModifier.INSTANCE.isEnabled() && CrystalModifier.INSTANCE.chams.getValue().booleanValue()) {
            Color visibleColor;
            GL11.glPushAttrib((int)1048575);
            GL11.glDisable((int)3008);
            GL11.glDisable((int)3553);
            GL11.glDisable((int)2896);
            GL11.glEnable((int)3042);
            GL11.glBlendFunc((int)770, (int)771);
            GL11.glLineWidth((float)1.5f);
            GL11.glEnable((int)2960);
            if (CrystalModifier.INSTANCE.rainbow.getValue().booleanValue()) {
                Color rainbowColor1 = CrystalModifier.INSTANCE.colorSync.getValue() != false ? Colors.INSTANCE.getCurrentColor() : new Color(RenderUtil.getRainbow(CrystalModifier.INSTANCE.speed.getValue() * 100, 0, (float)CrystalModifier.INSTANCE.saturation.getValue().intValue() / 100.0f, (float)CrystalModifier.INSTANCE.brightness.getValue().intValue() / 100.0f));
                Color rainbowColor = EntityUtil.getColor(entity, rainbowColor1.getRed(), rainbowColor1.getGreen(), rainbowColor1.getBlue(), CrystalModifier.INSTANCE.alpha.getValue(), true);
                if (CrystalModifier.INSTANCE.throughWalls.getValue().booleanValue()) {
                    GL11.glDisable((int)2929);
                    GL11.glDepthMask((boolean)false);
                }
                GL11.glEnable((int)10754);
                GL11.glColor4f((float)((float)rainbowColor.getRed() / 255.0f), (float)((float)rainbowColor.getGreen() / 255.0f), (float)((float)rainbowColor.getBlue() / 255.0f), (float)((float)CrystalModifier.INSTANCE.alpha.getValue().intValue() / 255.0f));
                model.render(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
                if (CrystalModifier.INSTANCE.throughWalls.getValue().booleanValue()) {
                    GL11.glEnable((int)2929);
                    GL11.glDepthMask((boolean)true);
                }
            } else if (CrystalModifier.INSTANCE.xqz.getValue().booleanValue() && CrystalModifier.INSTANCE.throughWalls.getValue().booleanValue()) {
                Color hiddenColor = EntityUtil.getColor(entity, CrystalModifier.INSTANCE.hiddenRed.getValue(), CrystalModifier.INSTANCE.hiddenGreen.getValue(), CrystalModifier.INSTANCE.hiddenBlue.getValue(), CrystalModifier.INSTANCE.hiddenAlpha.getValue(), true);
                visibleColor = EntityUtil.getColor(entity, CrystalModifier.INSTANCE.red.getValue(), CrystalModifier.INSTANCE.green.getValue(), CrystalModifier.INSTANCE.blue.getValue(), CrystalModifier.INSTANCE.alpha.getValue(), true);
                if (CrystalModifier.INSTANCE.throughWalls.getValue().booleanValue()) {
                    GL11.glDisable((int)2929);
                    GL11.glDepthMask((boolean)false);
                }
                GL11.glEnable((int)10754);
                GL11.glColor4f((float)((float)hiddenColor.getRed() / 255.0f), (float)((float)hiddenColor.getGreen() / 255.0f), (float)((float)hiddenColor.getBlue() / 255.0f), (float)((float)CrystalModifier.INSTANCE.alpha.getValue().intValue() / 255.0f));
                model.render(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
                if (CrystalModifier.INSTANCE.throughWalls.getValue().booleanValue()) {
                    GL11.glEnable((int)2929);
                    GL11.glDepthMask((boolean)true);
                }
                GL11.glColor4f((float)((float)visibleColor.getRed() / 255.0f), (float)((float)visibleColor.getGreen() / 255.0f), (float)((float)visibleColor.getBlue() / 255.0f), (float)((float)CrystalModifier.INSTANCE.alpha.getValue().intValue() / 255.0f));
                model.render(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
            } else {
                Color color = visibleColor = CrystalModifier.INSTANCE.colorSync.getValue() != false ? Colors.INSTANCE.getCurrentColor() : EntityUtil.getColor(entity, CrystalModifier.INSTANCE.red.getValue(), CrystalModifier.INSTANCE.green.getValue(), CrystalModifier.INSTANCE.blue.getValue(), CrystalModifier.INSTANCE.alpha.getValue(), true);
                if (CrystalModifier.INSTANCE.throughWalls.getValue().booleanValue()) {
                    GL11.glDisable((int)2929);
                    GL11.glDepthMask((boolean)false);
                }
                GL11.glEnable((int)10754);
                GL11.glColor4f((float)((float)visibleColor.getRed() / 255.0f), (float)((float)visibleColor.getGreen() / 255.0f), (float)((float)visibleColor.getBlue() / 255.0f), (float)((float)CrystalModifier.INSTANCE.alpha.getValue().intValue() / 255.0f));
                model.render(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
                if (CrystalModifier.INSTANCE.throughWalls.getValue().booleanValue()) {
                    GL11.glEnable((int)2929);
                    GL11.glDepthMask((boolean)true);
                }
            }
            GL11.glEnable((int)3042);
            GL11.glEnable((int)2896);
            GL11.glEnable((int)3553);
            GL11.glEnable((int)3008);
            GL11.glPopAttrib();
            if (CrystalModifier.INSTANCE.glint.getValue().booleanValue()) {
                GL11.glDisable((int)2929);
                GL11.glDepthMask((boolean)false);
                GlStateManager.enableAlpha();
                GlStateManager.color((float)1.0f, (float)0.0f, (float)0.0f, (float)0.13f);
                model.render(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
                GlStateManager.disableAlpha();
                GL11.glEnable((int)2929);
                GL11.glDepthMask((boolean)true);
            }
        } else {
            model.render(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
        }
        if (CrystalModifier.INSTANCE.isEnabled()) {
            if (CrystalModifier.INSTANCE.animateScale.getValue().booleanValue() && CrystalModifier.INSTANCE.scaleMap.containsKey(entity)) {
                GlStateManager.scale((float)(1.0f / CrystalModifier.INSTANCE.scaleMap.get(entity).floatValue()), (float)(1.0f / CrystalModifier.INSTANCE.scaleMap.get(entity).floatValue()), (float)(1.0f / CrystalModifier.INSTANCE.scaleMap.get(entity).floatValue()));
            } else {
                GlStateManager.scale((float)(1.0f / CrystalModifier.INSTANCE.scale.getValue().floatValue()), (float)(1.0f / CrystalModifier.INSTANCE.scale.getValue().floatValue()), (float)(1.0f / CrystalModifier.INSTANCE.scale.getValue().floatValue()));
            }
        }
    }
}

