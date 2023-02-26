
/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.model.ModelBase
 *  net.minecraft.client.model.ModelRenderer
 *  net.minecraft.client.renderer.GlStateManager
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.player.EnumPlayerModelParts
 *  net.minecraft.util.ResourceLocation
 *  net.minecraft.util.math.MathHelper
 *  net.minecraftforge.client.event.RenderPlayerEvent$Post
 *  net.minecraftforge.fml.common.eventhandler.SubscribeEvent
 *  org.lwjgl.opengl.GL11
 */
package me.earth.phobos.features.modules.client;

import me.earth.phobos.Phobos;
import me.earth.phobos.features.modules.Module;
import me.earth.phobos.util.EntityUtil;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EnumPlayerModelParts;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.opengl.GL11;

public class Cosmetics
extends Module {
    public static Cosmetics INSTANCE;
    public final ModelCloutGoggles cloutGoggles = new ModelCloutGoggles();
    public final ModelSquidFlag flag = new ModelSquidFlag();
    public final TopHatModel hatModel = new TopHatModel();
    public final GlassesModel glassesModel = new GlassesModel();
    public final SantaHatModel santaHatModel = new SantaHatModel();
    public final ModelSquidLauncher squidLauncher = new ModelSquidLauncher();
    private final HatGlassesModel hatGlassesModel = new HatGlassesModel();
    private final ResourceLocation hatTexture = new ResourceLocation("textures/tophat.png");
    private final ResourceLocation glassesTexture = new ResourceLocation("textures/sunglasses.png");
    private final ResourceLocation santaHatTexture = new ResourceLocation("textures/santahat.png");
    private final ResourceLocation squidTexture = new ResourceLocation("textures/squid.png");
    private final ResourceLocation cloutGoggleTexture = new ResourceLocation("textures/cloutgoggles.png");
    private final ResourceLocation squidLauncherTexture = new ResourceLocation("textures/squidlauncher.png");

    public Cosmetics() {
        super("Cosmetics", "Shows Cosmetics on phobos users.", Module.Category.CLIENT, true, false, false);
        INSTANCE = this;
    }

    @SubscribeEvent
    public void onRenderPlayer(RenderPlayerEvent.Post event) {
        if (Phobos.cosmeticsManager.hasCosmetics(event.getEntityPlayer()) || EntityUtil.isFakePlayer(event.getEntityPlayer())) {
            return;
        }
        for (ModelBase model : Phobos.cosmeticsManager.getRenderModels(event.getEntityPlayer())) {
            GlStateManager.pushMatrix();
            mc.getRenderManager();
            GlStateManager.translate((double)event.getX(), (double)event.getY(), (double)event.getZ());
            double scale = 1.0;
            double rotate = this.interpolate(event.getEntityPlayer().prevRotationYawHead, event.getEntityPlayer().rotationYawHead, event.getPartialRenderTick());
            double rotate1 = this.interpolate(event.getEntityPlayer().prevRotationPitch, event.getEntityPlayer().rotationPitch, event.getPartialRenderTick());
            double rotate3 = event.getEntityPlayer().isSneaking() ? 22.0 : 0.0;
            float limbSwingAmount = this.interpolate(event.getEntityPlayer().prevLimbSwingAmount, event.getEntityPlayer().limbSwingAmount, event.getPartialRenderTick());
            float rotate2 = MathHelper.cos((float)(event.getEntityPlayer().limbSwing * 0.6662f + (float)Math.PI)) * 1.4f * limbSwingAmount;
            GL11.glScaled((double)(-scale), (double)(-scale), (double)scale);
            GL11.glTranslated((double)0.0, (double)(-((double)event.getEntityPlayer().height - (event.getEntityPlayer().isSneaking() ? 0.25 : 0.0) - 0.38) / scale), (double)0.0);
            GL11.glRotated((double)(180.0 + rotate), (double)0.0, (double)1.0, (double)0.0);
            if (!(model instanceof ModelSquidLauncher)) {
                GL11.glRotated((double)rotate1, (double)1.0, (double)0.0, (double)0.0);
            }
            if (model instanceof ModelSquidLauncher) {
                GL11.glRotated((double)rotate3, (double)1.0, (double)0.0, (double)0.0);
            }
            GlStateManager.translate((double)0.0, (double)-0.45, (double)0.0);
            if (model instanceof ModelSquidLauncher) {
                GlStateManager.translate((double)0.15, (double)1.3, (double)0.0);
                for (ModelRenderer renderer : this.squidLauncher.boxList) {
                    renderer.rotateAngleX = rotate2;
                }
            }
            if (model instanceof TopHatModel) {
                mc.getTextureManager().bindTexture(this.hatTexture);
                this.hatModel.render(event.getEntity(), 0.0f, 0.0f, -0.1f, 0.0f, 0.0f, 0.0625f);
                mc.getTextureManager().deleteTexture(this.hatTexture);
            } else if (model instanceof GlassesModel) {
                if (event.getEntityPlayer().isWearing(EnumPlayerModelParts.HAT)) {
                    mc.getTextureManager().bindTexture(this.glassesTexture);
                    this.hatGlassesModel.render(event.getEntity(), 0.0f, 0.0f, -0.1f, 0.0f, 0.0f, 0.0625f);
                    mc.getTextureManager().deleteTexture(this.glassesTexture);
                } else {
                    mc.getTextureManager().bindTexture(this.glassesTexture);
                    this.glassesModel.render(event.getEntity(), 0.0f, 0.0f, -0.1f, 0.0f, 0.0f, 0.0625f);
                    mc.getTextureManager().deleteTexture(this.glassesTexture);
                }
            } else if (model instanceof SantaHatModel) {
                mc.getTextureManager().bindTexture(this.santaHatTexture);
                this.santaHatModel.render(event.getEntity(), 0.0f, 0.0f, -0.1f, 0.0f, 0.0f, 0.0625f);
                mc.getTextureManager().deleteTexture(this.santaHatTexture);
            } else if (model instanceof ModelCloutGoggles) {
                mc.getTextureManager().bindTexture(this.cloutGoggleTexture);
                this.cloutGoggles.render(event.getEntity(), 0.0f, 0.0f, -0.1f, 0.0f, 0.0f, 0.0625f);
                mc.getTextureManager().deleteTexture(this.cloutGoggleTexture);
            } else if (model instanceof ModelSquidFlag) {
                mc.getTextureManager().bindTexture(this.squidTexture);
                this.flag.render(event.getEntity(), 0.0f, 0.0f, -0.1f, 0.0f, 0.0f, 0.0625f);
                mc.getTextureManager().deleteTexture(this.squidTexture);
            } else if (model instanceof ModelSquidLauncher) {
                mc.getTextureManager().bindTexture(this.squidLauncherTexture);
                this.squidLauncher.render(event.getEntity(), 0.0f, 0.0f, -0.1f, 0.0f, 0.0f, 0.0325f);
                mc.getTextureManager().deleteTexture(this.squidLauncherTexture);
            }
            GlStateManager.popMatrix();
        }
    }

    public float interpolate(float yaw1, float yaw2, float percent) {
        float rotation = (yaw1 + (yaw2 - yaw1) * percent) % 360.0f;
        if (rotation < 0.0f) {
            rotation += 360.0f;
        }
        return rotation;
    }

    public static class TopHatModel
    extends ModelBase {
        public ModelRenderer bottom;
        public ModelRenderer top;

        public TopHatModel() {
            this.textureWidth = 64;
            this.textureHeight = 32;
            this.top = new ModelRenderer((ModelBase)this, 0, 10);
            this.top.addBox(0.0f, 0.0f, 0.0f, 4, 10, 4, 0.0f);
            this.top.setRotationPoint(-2.0f, -11.0f, -2.0f);
            this.bottom = new ModelRenderer((ModelBase)this, 0, 0);
            this.bottom.addBox(0.0f, 0.0f, 0.0f, 8, 1, 8, 0.0f);
            this.bottom.setRotationPoint(-4.0f, -1.0f, -4.0f);
        }

        public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
            this.top.render(f5);
            this.bottom.render(f5);
        }

        public void setRotateAngle(ModelRenderer modelRenderer, float x, float y, float z) {
            modelRenderer.rotateAngleX = x;
            modelRenderer.rotateAngleY = y;
            modelRenderer.rotateAngleZ = z;
        }
    }

    public static class GlassesModel
    extends ModelBase {
        public ModelRenderer firstLeftFrame;
        public ModelRenderer firstRightFrame;
        public ModelRenderer centerBar;
        public ModelRenderer farLeftBar;
        public ModelRenderer farRightBar;
        public ModelRenderer leftEar;
        public ModelRenderer rightEar;

        public GlassesModel() {
            this.textureWidth = 64;
            this.textureHeight = 64;
            this.farLeftBar = new ModelRenderer((ModelBase)this, 0, 13);
            this.farLeftBar.setRotationPoint(-4.0f, 3.5f, -4.0f);
            this.farLeftBar.addBox(0.0f, 0.0f, 0.0f, 1, 1, 1, 0.0f);
            this.rightEar = new ModelRenderer((ModelBase)this, 10, 0);
            this.rightEar.setRotationPoint(3.2f, 3.5f, -4.0f);
            this.rightEar.addBox(0.0f, 0.0f, 0.0f, 1, 1, 3, 0.0f);
            this.centerBar = new ModelRenderer((ModelBase)this, 0, 9);
            this.centerBar.setRotationPoint(-1.0f, 3.5f, -4.0f);
            this.centerBar.addBox(0.0f, 0.0f, 0.0f, 2, 1, 1, 0.0f);
            this.firstLeftFrame = new ModelRenderer((ModelBase)this, 0, 0);
            this.firstLeftFrame.setRotationPoint(-3.0f, 3.0f, -4.0f);
            this.firstLeftFrame.addBox(0.0f, 0.0f, 0.0f, 2, 2, 1, 0.0f);
            this.firstRightFrame = new ModelRenderer((ModelBase)this, 0, 5);
            this.firstRightFrame.setRotationPoint(1.0f, 3.0f, -4.0f);
            this.firstRightFrame.addBox(0.0f, 0.0f, 0.0f, 2, 2, 1, 0.0f);
            this.leftEar = new ModelRenderer((ModelBase)this, 20, 0);
            this.leftEar.setRotationPoint(-4.2f, 3.5f, -4.0f);
            this.leftEar.addBox(0.0f, 0.0f, 0.0f, 1, 1, 3, 0.0f);
            this.farRightBar = new ModelRenderer((ModelBase)this, 0, 17);
            this.farRightBar.setRotationPoint(3.0f, 3.5f, -4.0f);
            this.farRightBar.addBox(0.0f, 0.0f, 0.0f, 1, 1, 1, 0.0f);
        }

        public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
            this.farLeftBar.render(f5);
            this.rightEar.render(f5);
            this.centerBar.render(f5);
            this.firstLeftFrame.render(f5);
            this.firstRightFrame.render(f5);
            this.leftEar.render(f5);
            this.farRightBar.render(f5);
        }

        public void setRotateAngle(ModelRenderer modelRenderer, float x, float y, float z) {
            modelRenderer.rotateAngleX = x;
            modelRenderer.rotateAngleY = y;
            modelRenderer.rotateAngleZ = z;
        }
    }

    public static class HatGlassesModel
    extends ModelBase {
        public ModelRenderer firstLeftFrame;
        public ModelRenderer firstRightFrame;
        public ModelRenderer centerBar;
        public ModelRenderer farLeftBar;
        public ModelRenderer farRightBar;
        public ModelRenderer leftEar;
        public ModelRenderer rightEar;

        public HatGlassesModel() {
            this.textureWidth = 64;
            this.textureHeight = 64;
            this.farLeftBar = new ModelRenderer((ModelBase)this, 0, 13);
            this.farLeftBar.setRotationPoint(-4.0f, 3.5f, -5.0f);
            this.farLeftBar.addBox(0.0f, 0.0f, 0.0f, 1, 1, 1, 0.0f);
            this.rightEar = new ModelRenderer((ModelBase)this, 10, 0);
            this.rightEar.setRotationPoint(3.2f, 3.5f, -5.0f);
            this.rightEar.addBox(0.0f, 0.0f, 0.0f, 1, 1, 3, 0.0f);
            this.centerBar = new ModelRenderer((ModelBase)this, 0, 9);
            this.centerBar.setRotationPoint(-1.0f, 3.5f, -5.0f);
            this.centerBar.addBox(0.0f, 0.0f, 0.0f, 2, 1, 1, 0.0f);
            this.firstLeftFrame = new ModelRenderer((ModelBase)this, 0, 0);
            this.firstLeftFrame.setRotationPoint(-3.0f, 3.0f, -5.0f);
            this.firstLeftFrame.addBox(0.0f, 0.0f, 0.0f, 2, 2, 1, 0.0f);
            this.firstRightFrame = new ModelRenderer((ModelBase)this, 0, 5);
            this.firstRightFrame.setRotationPoint(1.0f, 3.0f, -5.0f);
            this.firstRightFrame.addBox(0.0f, 0.0f, 0.0f, 2, 2, 1, 0.0f);
            this.leftEar = new ModelRenderer((ModelBase)this, 20, 0);
            this.leftEar.setRotationPoint(-4.2f, 3.5f, -5.0f);
            this.leftEar.addBox(0.0f, 0.0f, 0.0f, 1, 1, 3, 0.0f);
            this.farRightBar = new ModelRenderer((ModelBase)this, 0, 17);
            this.farRightBar.setRotationPoint(3.0f, 3.5f, -5.0f);
            this.farRightBar.addBox(0.0f, 0.0f, 0.0f, 1, 1, 1, 0.0f);
        }

        public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
            this.farLeftBar.render(f5);
            this.rightEar.render(f5);
            this.centerBar.render(f5);
            this.firstLeftFrame.render(f5);
            this.firstRightFrame.render(f5);
            this.leftEar.render(f5);
            this.farRightBar.render(f5);
        }

        public void setRotateAngle(ModelRenderer modelRenderer, float x, float y, float z) {
            modelRenderer.rotateAngleX = x;
            modelRenderer.rotateAngleY = y;
            modelRenderer.rotateAngleZ = z;
        }
    }

    public static class SantaHatModel
    extends ModelBase {
        public ModelRenderer baseLayer;
        public ModelRenderer baseRedLayer;
        public ModelRenderer midRedLayer;
        public ModelRenderer topRedLayer;
        public ModelRenderer lastRedLayer;
        public ModelRenderer realFinalLastLayer;
        public ModelRenderer whiteLayer;

        public SantaHatModel() {
            this.textureWidth = 64;
            this.textureHeight = 32;
            this.topRedLayer = new ModelRenderer((ModelBase)this, 46, 0);
            this.topRedLayer.setRotationPoint(0.5f, -8.4f, -1.5f);
            this.topRedLayer.addBox(0.0f, 0.0f, 0.0f, 3, 2, 3, 0.0f);
            this.setRotateAngle(this.topRedLayer, 0.0f, 0.0f, 0.5009095f);
            this.baseLayer = new ModelRenderer((ModelBase)this, 0, 0);
            this.baseLayer.setRotationPoint(-4.0f, -1.0f, -4.0f);
            this.baseLayer.addBox(0.0f, 0.0f, 0.0f, 8, 2, 8, 0.0f);
            this.midRedLayer = new ModelRenderer((ModelBase)this, 28, 0);
            this.midRedLayer.setRotationPoint(-1.2f, -6.8f, -2.0f);
            this.midRedLayer.addBox(0.0f, 0.0f, 0.0f, 4, 3, 4, 0.0f);
            this.setRotateAngle(this.midRedLayer, 0.0f, 0.0f, 0.22759093f);
            this.realFinalLastLayer = new ModelRenderer((ModelBase)this, 46, 8);
            this.realFinalLastLayer.setRotationPoint(4.0f, -10.4f, 0.0f);
            this.realFinalLastLayer.addBox(0.0f, 0.0f, 0.0f, 1, 3, 1, 0.0f);
            this.setRotateAngle(this.realFinalLastLayer, 0.0f, 0.0f, 1.0016445f);
            this.lastRedLayer = new ModelRenderer((ModelBase)this, 34, 8);
            this.lastRedLayer.setRotationPoint(2.0f, -9.4f, 0.0f);
            this.lastRedLayer.addBox(0.0f, 0.0f, 0.0f, 2, 2, 2, 0.0f);
            this.setRotateAngle(this.lastRedLayer, 0.0f, 0.0f, 0.8196066f);
            this.whiteLayer = new ModelRenderer((ModelBase)this, 0, 22);
            this.whiteLayer.setRotationPoint(4.1f, -9.7f, -0.5f);
            this.whiteLayer.addBox(0.0f, 0.0f, 0.0f, 2, 2, 2, 0.0f);
            this.setRotateAngle(this.whiteLayer, -0.091106184f, 0.0f, 0.18203785f);
            this.baseRedLayer = new ModelRenderer((ModelBase)this, 0, 11);
            this.baseRedLayer.setRotationPoint(-3.0f, -4.0f, -3.0f);
            this.baseRedLayer.addBox(0.0f, 0.0f, 0.0f, 6, 3, 6, 0.0f);
            this.setRotateAngle(this.baseRedLayer, 0.0f, 0.0f, 0.045553092f);
        }

        public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
            this.topRedLayer.render(f5);
            this.baseLayer.render(f5);
            this.midRedLayer.render(f5);
            this.realFinalLastLayer.render(f5);
            this.lastRedLayer.render(f5);
            this.whiteLayer.render(f5);
            this.baseRedLayer.render(f5);
        }

        public void setRotateAngle(ModelRenderer modelRenderer, float x, float y, float z) {
            modelRenderer.rotateAngleX = x;
            modelRenderer.rotateAngleY = y;
            modelRenderer.rotateAngleZ = z;
        }
    }

    public static class ModelSquidFlag
    extends ModelBase {
        public ModelRenderer flag;

        public ModelSquidFlag() {
            this.textureWidth = 64;
            this.textureHeight = 32;
            this.flag = new ModelRenderer((ModelBase)this, 0, 0);
            this.flag.setRotationPoint(0.0f, 0.0f, 0.0f);
            this.flag.addBox(-5.0f, -16.0f, 0.0f, 10, 16, 1, 0.0f);
        }

        public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
            this.flag.render(f5);
        }

        public void setRotateAngle(ModelRenderer modelRenderer, float x, float y, float z) {
            modelRenderer.rotateAngleX = x;
            modelRenderer.rotateAngleY = y;
            modelRenderer.rotateAngleZ = z;
        }
    }

    public static class ModelSquidLauncher
    extends ModelBase {
        public ModelRenderer barrel;
        public ModelRenderer squid;
        public ModelRenderer secondBarrel;
        public ModelRenderer barrelSide1;
        public ModelRenderer barrelSide2;
        public ModelRenderer barrelSide3;
        public ModelRenderer barrelSide4;
        public ModelRenderer stock;
        public ModelRenderer stockEnd;
        public ModelRenderer trigger;

        public ModelSquidLauncher() {
            this.textureWidth = 64;
            this.textureHeight = 32;
            this.barrelSide4 = new ModelRenderer((ModelBase)this, 0, 0);
            this.barrelSide4.setRotationPoint(0.5f, 0.0f, 0.0f);
            this.barrelSide4.addBox(0.0f, -2.0f, 0.2f, 4, 5, 1, 0.0f);
            this.setRotateAngle(this.barrelSide4, 0.091106184f, 0.0f, 0.0f);
            this.stock = new ModelRenderer((ModelBase)this, 0, 24);
            this.stock.setRotationPoint(0.0f, 0.0f, 0.0f);
            this.stock.addBox(1.5f, 3.0f, 1.5f, 2, 4, 2, 0.0f);
            this.squid = new ModelRenderer((ModelBase)this, 0, 16);
            this.squid.setRotationPoint(0.0f, 0.0f, 0.0f);
            this.squid.addBox(1.2f, -11.5f, 0.8f, 3, 4, 3, 0.0f);
            this.setRotateAngle(this.squid, 0.0f, -0.091106184f, 0.0f);
            this.barrelSide2 = new ModelRenderer((ModelBase)this, 18, 14);
            this.barrelSide2.setRotationPoint(0.0f, 0.0f, 0.0f);
            this.barrelSide2.addBox(3.8f, -2.5f, 0.5f, 1, 5, 4, 0.0f);
            this.setRotateAngle(this.barrelSide2, 0.0f, 0.0f, 0.091106184f);
            this.secondBarrel = new ModelRenderer((ModelBase)this, 32, 14);
            this.secondBarrel.setRotationPoint(0.0f, 0.0f, 0.0f);
            this.secondBarrel.addBox(0.5f, -2.0f, 0.5f, 4, 5, 4, 0.0f);
            this.stockEnd = new ModelRenderer((ModelBase)this, 18, 26);
            this.stockEnd.setRotationPoint(0.0f, 0.0f, 0.0f);
            this.stockEnd.addBox(2.0f, 7.0f, 1.5f, 1, 1, 4, 0.0f);
            this.barrelSide1 = new ModelRenderer((ModelBase)this, 18, 14);
            this.barrelSide1.setRotationPoint(0.0f, 0.0f, 0.0f);
            this.barrelSide1.addBox(0.2f, -2.0f, 0.5f, 1, 5, 4, 0.0f);
            this.setRotateAngle(this.barrelSide1, 0.0f, 0.0f, -0.091106184f);
            this.barrelSide3 = new ModelRenderer((ModelBase)this, 0, 0);
            this.barrelSide3.setRotationPoint(0.0f, 0.0f, 0.0f);
            this.barrelSide3.addBox(0.5f, -2.5f, 3.8f, 4, 5, 1, 0.0f);
            this.setRotateAngle(this.barrelSide3, -0.091106184f, 0.0f, 0.0f);
            this.trigger = new ModelRenderer((ModelBase)this, 40, 0);
            this.trigger.setRotationPoint(0.0f, 0.0f, 0.0f);
            this.trigger.addBox(12.0f, 6.6f, 5.4f, 1, 1, 1, 0.0f);
            this.barrel = new ModelRenderer((ModelBase)this, 18, 0);
            this.barrel.setRotationPoint(0.0f, 0.0f, 0.0f);
            this.barrel.addBox(0.0f, -8.0f, 0.0f, 5, 6, 5, 0.0f);
            this.boxList.add(this.barrel);
            this.boxList.add(this.squid);
            this.boxList.add(this.secondBarrel);
            this.boxList.add(this.barrelSide1);
            this.boxList.add(this.barrelSide2);
            this.boxList.add(this.barrelSide3);
            this.boxList.add(this.barrelSide4);
            this.boxList.add(this.stock);
            this.boxList.add(this.stockEnd);
            this.boxList.add(this.trigger);
        }

        public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
            this.stock.render(f5);
            this.barrelSide1.render(f5);
            this.stockEnd.render(f5);
            this.secondBarrel.render(f5);
            this.barrelSide3.render(f5);
            this.squid.render(f5);
            this.barrelSide4.render(f5);
            this.barrel.render(f5);
            this.barrelSide2.render(f5);
            GlStateManager.pushMatrix();
            GlStateManager.translate((float)this.trigger.offsetX, (float)this.trigger.offsetY, (float)this.trigger.offsetZ);
            GlStateManager.translate((float)(this.trigger.rotationPointX * f5), (float)(this.trigger.rotationPointY * f5), (float)(this.trigger.rotationPointZ * f5));
            GlStateManager.scale((double)0.2, (double)1.0, (double)0.8);
            GlStateManager.translate((float)(-this.trigger.offsetX), (float)(-this.trigger.offsetY), (float)(-this.trigger.offsetZ));
            GlStateManager.translate((float)(-this.trigger.rotationPointX * f5), (float)(-this.trigger.rotationPointY * f5), (float)(-this.trigger.rotationPointZ * f5));
            this.trigger.render(f5);
            GlStateManager.popMatrix();
        }

        public void setRotateAngle(ModelRenderer modelRenderer, float x, float y, float z) {
            modelRenderer.rotateAngleX = x;
            modelRenderer.rotateAngleY = y;
            modelRenderer.rotateAngleZ = z;
        }
    }

    public static class ModelCloutGoggles
    extends ModelBase {
        public ModelRenderer leftGlass;
        public ModelRenderer topLeftFrame;
        public ModelRenderer bottomLeftFrame;
        public ModelRenderer leftLeftFrame;
        public ModelRenderer rightLeftFrame;
        public ModelRenderer rightGlass;
        public ModelRenderer topRightFrame;
        public ModelRenderer bottomLeftFrame_1;
        public ModelRenderer leftRightFrame;
        public ModelRenderer rightRightFrame;
        public ModelRenderer leftEar;
        public ModelRenderer rightEar;

        public ModelCloutGoggles() {
            this.textureWidth = 64;
            this.textureHeight = 32;
            this.rightLeftFrame = new ModelRenderer((ModelBase)this, 18, 0);
            this.rightLeftFrame.setRotationPoint(-3.0f, 3.0f, -4.0f);
            this.rightLeftFrame.addBox(0.0f, 2.0f, 0.0f, 2, 1, 1, 0.0f);
            this.bottomLeftFrame_1 = new ModelRenderer((ModelBase)this, 26, 5);
            this.bottomLeftFrame_1.setRotationPoint(-3.0f, 3.0f, -4.0f);
            this.bottomLeftFrame_1.addBox(4.0f, 2.0f, 0.0f, 2, 1, 1, 0.0f);
            this.leftLeftFrame = new ModelRenderer((ModelBase)this, 10, 5);
            this.leftLeftFrame.setRotationPoint(-3.0f, 3.0f, -4.0f);
            this.leftLeftFrame.addBox(2.0f, 0.0f, 0.0f, 1, 2, 1, 0.0f);
            this.rightGlass = new ModelRenderer((ModelBase)this, 18, 5);
            this.rightGlass.setRotationPoint(-3.0f, 3.0f, -4.0f);
            this.rightGlass.addBox(4.0f, 0.0f, 0.0f, 2, 2, 1, 0.0f);
            this.rightRightFrame = new ModelRenderer((ModelBase)this, 10, 11);
            this.rightRightFrame.setRotationPoint(3.0f, 3.0f, -4.0f);
            this.rightRightFrame.addBox(0.0f, 0.0f, 0.0f, 1, 2, 1, 0.0f);
            this.leftEar = new ModelRenderer((ModelBase)this, 18, 11);
            this.leftEar.setRotationPoint(-3.0f, 3.0f, -4.0f);
            this.leftEar.addBox(-1.2f, 0.0f, 0.0f, 1, 1, 3, 0.0f);
            this.topRightFrame = new ModelRenderer((ModelBase)this, 26, 0);
            this.topRightFrame.setRotationPoint(1.0f, 3.0f, -4.0f);
            this.topRightFrame.addBox(0.0f, -1.0f, 0.0f, 2, 1, 1, 0.0f);
            this.topLeftFrame = new ModelRenderer((ModelBase)this, 0, 5);
            this.topLeftFrame.setRotationPoint(-3.0f, 3.0f, -4.0f);
            this.topLeftFrame.addBox(-1.0f, 0.0f, 0.0f, 1, 2, 1, 0.0f);
            this.rightEar = new ModelRenderer((ModelBase)this, 28, 11);
            this.rightEar.setRotationPoint(-3.0f, 3.0f, -4.0f);
            this.rightEar.addBox(6.2f, 0.0f, 0.0f, 1, 1, 3, 0.0f);
            this.leftGlass = new ModelRenderer((ModelBase)this, 0, 0);
            this.leftGlass.setRotationPoint(-3.0f, 3.0f, -4.0f);
            this.leftGlass.addBox(0.0f, 0.0f, 0.0f, 2, 2, 1, 0.0f);
            this.bottomLeftFrame = new ModelRenderer((ModelBase)this, 10, 0);
            this.bottomLeftFrame.setRotationPoint(-3.0f, 3.0f, -4.0f);
            this.bottomLeftFrame.addBox(0.0f, -1.0f, 0.0f, 2, 1, 1, 0.0f);
            this.leftRightFrame = new ModelRenderer((ModelBase)this, 0, 11);
            this.leftRightFrame.setRotationPoint(-3.0f, 3.0f, -4.0f);
            this.leftRightFrame.addBox(3.0f, 0.0f, 0.0f, 1, 2, 1, 0.0f);
        }

        public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
            this.rightLeftFrame.render(f5);
            this.bottomLeftFrame_1.render(f5);
            this.leftLeftFrame.render(f5);
            this.rightGlass.render(f5);
            this.rightRightFrame.render(f5);
            this.leftEar.render(f5);
            this.topRightFrame.render(f5);
            this.topLeftFrame.render(f5);
            this.rightEar.render(f5);
            this.leftGlass.render(f5);
            this.bottomLeftFrame.render(f5);
            this.leftRightFrame.render(f5);
        }

        public void setRotateAngle(ModelRenderer modelRenderer, float x, float y, float z) {
            modelRenderer.rotateAngleX = x;
            modelRenderer.rotateAngleY = y;
            modelRenderer.rotateAngleZ = z;
        }
    }
}

