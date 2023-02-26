/*
 * Decompiled with CFR 0.151.
 */
package me.earth.phobos.features.gui.alts.ias.gui;

import me.earth.phobos.features.gui.alts.ias.account.ExtendedAccountData;
import me.earth.phobos.features.gui.alts.ias.gui.AbstractAccountGui;
import me.earth.phobos.features.gui.alts.tools.alt.AltDatabase;

public class GuiAddAccount
extends AbstractAccountGui {
    public GuiAddAccount() {
        super("ias.addaccount");
    }

    @Override
    public void complete() {
        AltDatabase.getInstance().getAlts().add(new ExtendedAccountData(this.getUsername(), this.getPassword(), this.getUsername()));
    }
}

