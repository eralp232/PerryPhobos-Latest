/*
 * Decompiled with CFR 0.151.
 */
package me.earth.phobos.features.gui.alts.ias.tools;

import me.earth.phobos.features.gui.alts.ias.legacysupport.ILegacyCompat;
import me.earth.phobos.features.gui.alts.ias.legacysupport.NewJava;
import me.earth.phobos.features.gui.alts.ias.legacysupport.OldJava;

public class JavaTools {
    private static double getJavaVersion() {
        String version = System.getProperty("java.version");
        int pos = version.indexOf(46);
        pos = version.indexOf(46, pos + 1);
        return Double.parseDouble(version.substring(0, pos));
    }

    public static ILegacyCompat getJavaCompat() {
        if (JavaTools.getJavaVersion() >= 1.8) {
            return new NewJava();
        }
        return new OldJava();
    }
}

