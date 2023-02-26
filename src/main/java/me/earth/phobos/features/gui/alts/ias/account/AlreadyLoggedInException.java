
/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.resources.I18n
 */
package me.earth.phobos.features.gui.alts.ias.account;

import net.minecraft.client.resources.I18n;

public class AlreadyLoggedInException
extends Exception {
    private static final long serialVersionUID = -7572892045698003265L;

    @Override
    public String getLocalizedMessage() {
        return I18n.format((String)"ias.alreadyloggedin", (Object[])new Object[0]);
    }
}

