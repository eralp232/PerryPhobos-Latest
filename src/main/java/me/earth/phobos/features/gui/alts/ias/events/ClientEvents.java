
/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.gui.GuiMainMenu
 *  net.minecraft.client.gui.GuiMultiplayer
 *  net.minecraft.client.gui.GuiScreen
 *  net.minecraft.client.resources.I18n
 *  net.minecraftforge.client.event.GuiScreenEvent$ActionPerformedEvent
 *  net.minecraftforge.client.event.GuiScreenEvent$InitGuiEvent$Post
 *  net.minecraftforge.fml.client.event.ConfigChangedEvent
 *  net.minecraftforge.fml.common.eventhandler.SubscribeEvent
 *  net.minecraftforge.fml.common.gameevent.TickEvent$RenderTickEvent
 */
package me.earth.phobos.features.gui.alts.ias.events;

import me.earth.phobos.features.gui.alts.ias.IAS;
import me.earth.phobos.features.gui.alts.ias.gui.GuiAccountSelector;
import me.earth.phobos.features.gui.alts.ias.gui.GuiButtonWithImage;
import me.earth.phobos.features.gui.alts.tools.Config;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.gui.GuiMultiplayer;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class ClientEvents {
    @SubscribeEvent
    public void guiEvent(GuiScreenEvent.InitGuiEvent.Post event) {
        GuiScreen gui = event.getGui();
        if (gui instanceof GuiMainMenu) {
            event.getButtonList().add(new GuiButtonWithImage(20, gui.width / 2 + 104, gui.height / 4 + 48 + 72 + 12, 20, 20, ""));
        }
    }

    @SubscribeEvent
    public void onClick(GuiScreenEvent.ActionPerformedEvent event) {
        if (event.getGui() instanceof GuiMainMenu && event.getButton().id == 20) {
            if (Config.getInstance() == null) {
                Config.load();
            }
            Minecraft.getMinecraft().displayGuiScreen((GuiScreen)new GuiAccountSelector());
        }
    }

    @SubscribeEvent
    public void onTick(TickEvent.RenderTickEvent t) {
        GuiScreen screen = Minecraft.getMinecraft().currentScreen;
        if (screen instanceof GuiMainMenu) {
            screen.drawCenteredString(Minecraft.getMinecraft().fontRenderer, I18n.format((String)"ias.loggedinas", (Object[])new Object[0]) + Minecraft.getMinecraft().getSession().getUsername() + ".", screen.width / 2, screen.height / 4 + 48 + 72 + 12 + 22, -3372920);
        } else if (screen instanceof GuiMultiplayer && Minecraft.getMinecraft().getSession().getToken().equals("0")) {
            screen.drawCenteredString(Minecraft.getMinecraft().fontRenderer, I18n.format((String)"ias.offlinemode", (Object[])new Object[0]), screen.width / 2, 10, 0xFF6464);
        }
    }

    @SubscribeEvent
    public void configChanged(ConfigChangedEvent event) {
        if (event.getModID().equals("ias")) {
            IAS.syncConfig();
        }
    }
}

