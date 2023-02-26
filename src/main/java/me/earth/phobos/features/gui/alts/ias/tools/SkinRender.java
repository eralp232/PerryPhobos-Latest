
/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.gui.Gui
 *  net.minecraft.client.renderer.texture.DynamicTexture
 *  net.minecraft.client.renderer.texture.TextureManager
 *  net.minecraft.util.ResourceLocation
 */
package me.earth.phobos.features.gui.alts.ias.tools;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.util.ResourceLocation;

public class SkinRender {
    private final File file;
    private final TextureManager textureManager;
    private DynamicTexture previewTexture;
    private ResourceLocation resourceLocation;

    public SkinRender(TextureManager textureManager, File file) {
        this.textureManager = textureManager;
        this.file = file;
    }

    private boolean loadPreview() {
        try {
            BufferedImage image = ImageIO.read(this.file);
            this.previewTexture = new DynamicTexture(image);
            this.resourceLocation = this.textureManager.getDynamicTextureLocation("ias", this.previewTexture);
            return true;
        }
        catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public void drawImage(int xPos, int yPos, int width, int height) {
        boolean successful;
        if (this.previewTexture == null && !(successful = this.loadPreview())) {
            System.out.println("Failure to load preview.");
            return;
        }
        this.previewTexture.updateDynamicTexture();
        this.textureManager.bindTexture(this.resourceLocation);
        Gui.drawModalRectWithCustomSizedTexture((int)xPos, (int)yPos, (float)0.0f, (float)0.0f, (int)width, (int)height, (float)64.0f, (float)128.0f);
    }
}

