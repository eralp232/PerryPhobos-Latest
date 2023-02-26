
/*
 * Decompiled with CFR 0.151.
 */
package me.earth.phobos.features.gui.alts.ias.gui;

import me.earth.phobos.features.gui.alts.ias.account.ExtendedAccountData;
import me.earth.phobos.features.gui.alts.ias.enums.EnumBool;
import me.earth.phobos.features.gui.alts.ias.gui.AbstractAccountGui;
import me.earth.phobos.features.gui.alts.ias.tools.JavaTools;
import me.earth.phobos.features.gui.alts.iasencrypt.EncryptionTools;
import me.earth.phobos.features.gui.alts.tools.alt.AccountData;
import me.earth.phobos.features.gui.alts.tools.alt.AltDatabase;

class GuiEditAccount
extends AbstractAccountGui {
    private final ExtendedAccountData data;
    private final int selectedIndex;

    public GuiEditAccount(int index) {
        super("ias.editaccount");
        this.selectedIndex = index;
        AccountData data = AltDatabase.getInstance().getAlts().get(index);
        this.data = data instanceof ExtendedAccountData ? (ExtendedAccountData)data : new ExtendedAccountData(data.user, data.pass, data.alias, 0, JavaTools.getJavaCompat().getDate(), EnumBool.UNKNOWN);
    }

    @Override
    public void initGui() {
        super.initGui();
        this.setUsername(EncryptionTools.decode(this.data.user));
        this.setPassword(EncryptionTools.decode(this.data.pass));
    }

    @Override
    public void complete() {
        AltDatabase.getInstance().getAlts().set(this.selectedIndex, new ExtendedAccountData(this.getUsername(), this.getPassword(), this.hasUserChanged ? this.getUsername() : this.data.alias, this.data.useCount, this.data.lastused, this.data.premium));
    }
}

