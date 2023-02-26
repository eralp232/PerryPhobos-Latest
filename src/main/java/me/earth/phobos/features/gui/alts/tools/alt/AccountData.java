/*
 * Decompiled with CFR 0.151.
 */
package me.earth.phobos.features.gui.alts.tools.alt;

import java.io.Serializable;
import me.earth.phobos.features.gui.alts.iasencrypt.EncryptionTools;

public class AccountData
implements Serializable {
    public static final long serialVersionUID = -147985492L;
    public final String user;
    public final String pass;
    public String alias;

    protected AccountData(String user, String pass, String alias) {
        this.user = EncryptionTools.encode(user);
        this.pass = EncryptionTools.encode(pass);
        this.alias = alias;
    }

    public boolean equalsBasic(AccountData obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (this.getClass() != obj.getClass()) {
            return false;
        }
        return this.user.equals(obj.user);
    }
}

