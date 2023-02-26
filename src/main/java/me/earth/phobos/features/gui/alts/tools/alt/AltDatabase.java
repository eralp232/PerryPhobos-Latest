/*
 * Decompiled with CFR 0.151.
 */
package me.earth.phobos.features.gui.alts.tools.alt;

import java.io.Serializable;
import java.util.ArrayList;
import me.earth.phobos.features.gui.alts.tools.Config;
import me.earth.phobos.features.gui.alts.tools.alt.AccountData;

public class AltDatabase
implements Serializable {
    public static final long serialVersionUID = -1585600597L;
    private static AltDatabase instance;
    private final ArrayList<AccountData> altList = new ArrayList();

    private AltDatabase() {
    }

    private static void loadFromConfig() {
        if (instance == null) {
            instance = (AltDatabase)Config.getInstance().getKey("altaccounts");
        }
    }

    private static void saveToConfig() {
        Config.getInstance().setKey("altaccounts", instance);
    }

    public static AltDatabase getInstance() {
        AltDatabase.loadFromConfig();
        if (instance == null) {
            instance = new AltDatabase();
            AltDatabase.saveToConfig();
        }
        return instance;
    }

    public ArrayList<AccountData> getAlts() {
        return this.altList;
    }
}

