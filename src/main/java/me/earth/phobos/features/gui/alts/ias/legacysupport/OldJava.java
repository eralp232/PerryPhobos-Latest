
/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.resources.I18n
 */
package me.earth.phobos.features.gui.alts.ias.legacysupport;

import me.earth.phobos.features.gui.alts.ias.legacysupport.ILegacyCompat;
import net.minecraft.client.resources.I18n;

public class OldJava
implements ILegacyCompat {
    @Override
    public int[] getDate() {
        return new int[3];
    }

    @Override
    public String getFormattedDate() {
        return I18n.format((String)"ias.updatejava", (Object[])new Object[0]);
    }
}

