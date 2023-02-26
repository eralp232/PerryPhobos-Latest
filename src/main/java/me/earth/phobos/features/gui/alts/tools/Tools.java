
/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.gui.Gui
 */
package me.earth.phobos.features.gui.alts.tools;

import net.minecraft.client.gui.Gui;

public class Tools {
    public static void drawBorderedRect(int x, int y, int x1, int y1, int size, int borderColor, int insideColor) {
        Gui.drawRect((int)(x + size), (int)(y + size), (int)(x1 - size), (int)(y1 - size), (int)insideColor);
        Gui.drawRect((int)(x + size), (int)(y + size), (int)x1, (int)y, (int)borderColor);
        Gui.drawRect((int)x, (int)y, (int)(x + size), (int)y1, (int)borderColor);
        Gui.drawRect((int)x1, (int)y1, (int)(x1 - size), (int)(y + size), (int)borderColor);
        Gui.drawRect((int)x, (int)(y1 - size), (int)x1, (int)y1, (int)borderColor);
    }
}

