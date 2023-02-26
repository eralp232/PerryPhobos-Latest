
/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  com.mojang.authlib.Agent
 *  com.mojang.authlib.UserAuthentication
 *  com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService
 *  com.mojang.util.UUIDTypeAdapter
 *  net.minecraft.client.Minecraft
 *  net.minecraft.util.Session
 */
package me.earth.phobos.features.gui.alts.tools.alt;

import com.mojang.authlib.Agent;
import com.mojang.authlib.UserAuthentication;
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import com.mojang.util.UUIDTypeAdapter;
import java.util.UUID;
import me.earth.phobos.features.gui.alts.MR;
import me.earth.phobos.features.gui.alts.ias.account.AlreadyLoggedInException;
import me.earth.phobos.features.gui.alts.ias.config.ConfigValues;
import me.earth.phobos.features.gui.alts.iasencrypt.EncryptionTools;
import me.earth.phobos.features.gui.alts.tools.alt.AccountData;
import me.earth.phobos.features.gui.alts.tools.alt.AltDatabase;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Session;

public class AltManager {
    private static AltManager manager;
    private final UserAuthentication auth;

    private AltManager() {
        UUID uuid = UUID.randomUUID();
        YggdrasilAuthenticationService authService = new YggdrasilAuthenticationService(Minecraft.getMinecraft().getProxy(), uuid.toString());
        this.auth = authService.createUserAuthentication(Agent.MINECRAFT);
        authService.createMinecraftSessionService();
    }

    public static AltManager getInstance() {
        if (manager == null) {
            manager = new AltManager();
        }
        return manager;
    }

    public Throwable setUser(String username, String password) {
        Exception throwable = null;
        if (!Minecraft.getMinecraft().getSession().getUsername().equals(EncryptionTools.decode(username)) || Minecraft.getMinecraft().getSession().getToken().equals("0")) {
            if (!Minecraft.getMinecraft().getSession().getToken().equals("0")) {
                for (AccountData data : AltDatabase.getInstance().getAlts()) {
                    if (!data.alias.equals(Minecraft.getMinecraft().getSession().getUsername()) || !data.user.equals(username)) continue;
                    throwable = new AlreadyLoggedInException();
                    return throwable;
                }
            }
            this.auth.logOut();
            this.auth.setUsername(EncryptionTools.decode(username));
            this.auth.setPassword(EncryptionTools.decode(password));
            try {
                this.auth.logIn();
                Session session = new Session(this.auth.getSelectedProfile().getName(), UUIDTypeAdapter.fromUUID((UUID)this.auth.getSelectedProfile().getId()), this.auth.getAuthenticatedToken(), this.auth.getUserType().getName());
                MR.setSession(session);
                for (int i = 0; i < AltDatabase.getInstance().getAlts().size(); ++i) {
                    AccountData data = AltDatabase.getInstance().getAlts().get(i);
                    if (!data.user.equals(username) || !data.pass.equals(password)) continue;
                    data.alias = session.getUsername();
                }
            }
            catch (Exception e) {
                throwable = e;
            }
        } else if (!ConfigValues.ENABLERELOG) {
            throwable = new AlreadyLoggedInException();
        }
        return throwable;
    }

    public void setUserOffline(String username) {
        this.auth.logOut();
        Session session = new Session(username, username, "0", "legacy");
        try {
            MR.setSession(session);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}

