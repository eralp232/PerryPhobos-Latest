/*
 * Decompiled with CFR 0.151.
 */
package me.earth.phobos.features.gui.alts.ias.account;

import java.util.Arrays;
import me.earth.phobos.features.gui.alts.ias.enums.EnumBool;
import me.earth.phobos.features.gui.alts.ias.tools.JavaTools;
import me.earth.phobos.features.gui.alts.tools.alt.AccountData;

public class ExtendedAccountData
extends AccountData {
    private static final long serialVersionUID = -909128662161235160L;
    public EnumBool premium;
    public int[] lastused;
    public int useCount;

    public ExtendedAccountData(String user, String pass, String alias) {
        super(user, pass, alias);
        this.useCount = 0;
        this.lastused = JavaTools.getJavaCompat().getDate();
        this.premium = EnumBool.UNKNOWN;
    }

    public ExtendedAccountData(String user, String pass, String alias, int useCount, int[] lastused, EnumBool premium) {
        super(user, pass, alias);
        this.useCount = useCount;
        this.lastused = lastused;
        this.premium = premium;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (this.getClass() != obj.getClass()) {
            return false;
        }
        ExtendedAccountData other = (ExtendedAccountData)obj;
        if (!Arrays.equals(this.lastused, other.lastused)) {
            return false;
        }
        if (this.premium != other.premium) {
            return false;
        }
        if (this.useCount != other.useCount) {
            return false;
        }
        return this.user.equals(other.user) && this.pass.equals(other.pass);
    }
}

