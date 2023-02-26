
/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  net.minecraft.command.ICommand
 *  net.minecraftforge.client.ClientCommandHandler
 *  net.minecraftforge.common.MinecraftForge
 *  net.minecraftforge.common.config.Configuration
 *  net.minecraftforge.fml.common.Mod
 *  net.minecraftforge.fml.common.Mod$EventHandler
 *  net.minecraftforge.fml.common.event.FMLInitializationEvent
 *  net.minecraftforge.fml.common.event.FMLPreInitializationEvent
 *  net.minecraftforge.fml.relauncher.Side
 *  net.minecraftforge.fml.relauncher.SideOnly
 */
package me.earth.phobos.features.gui.chat;

import me.earth.phobos.features.gui.chat.ChatSettings;
import me.earth.phobos.features.gui.chat.command.CommandConfig;
import me.earth.phobos.features.gui.chat.handlers.InjectHandler;
import net.minecraft.command.ICommand;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@Mod(modid="betterchat", name="Better Chat", version="1.5")
@SideOnly(value=Side.CLIENT)
public class BetterChat {
    public static final String MODID = "betterchat";
    public static final String NAME = "Better Chat";
    public static final String VERSION = "1.5";
    private static ChatSettings settings;

    public static ChatSettings getSettings() {
        return settings;
    }

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        settings = new ChatSettings(new Configuration(event.getSuggestedConfigurationFile()));
        settings.loadConfig();
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        MinecraftForge.EVENT_BUS.register((Object)new InjectHandler());
        ClientCommandHandler.instance.registerCommand((ICommand)new CommandConfig());
    }
}

