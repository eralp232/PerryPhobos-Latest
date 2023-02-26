/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  net.minecraftforge.fml.common.Mod
 *  net.minecraftforge.fml.common.Mod$EventHandler
 *  net.minecraftforge.fml.common.Mod$Instance
 *  net.minecraftforge.fml.common.event.FMLInitializationEvent
 *  net.minecraftforge.fml.common.event.FMLPreInitializationEvent
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 *  org.lwjgl.opengl.Display
 */
package me.earth.phobos;

import java.io.IOException;

import me.earth.phobos.features.gui.custom.GuiCustomMainScreen;
import me.earth.phobos.features.modules.client.PhobosChat;
import me.earth.phobos.features.modules.misc.RPC;
import me.earth.phobos.manager.ColorManager;
import me.earth.phobos.manager.CommandManager;
import me.earth.phobos.manager.ConfigManager;
import me.earth.phobos.manager.CosmeticsManager;
import me.earth.phobos.manager.EventManager;
import me.earth.phobos.manager.FileManager;
import me.earth.phobos.manager.FriendManager;
import me.earth.phobos.manager.HoleManager;
import me.earth.phobos.manager.InventoryManager;
import me.earth.phobos.manager.ModuleManager;
import me.earth.phobos.manager.NoStopManager;
import me.earth.phobos.manager.NotificationManager;
import me.earth.phobos.manager.PacketManager;
import me.earth.phobos.manager.PositionManager;
import me.earth.phobos.manager.PotionManager;
import me.earth.phobos.manager.ReloadManager;
import me.earth.phobos.manager.RotationManager;
import me.earth.phobos.manager.SafetyManager;
import me.earth.phobos.manager.ServerManager;
import me.earth.phobos.manager.SpeedManager;
import me.earth.phobos.manager.TextManager;
import me.earth.phobos.manager.TimerManager;
import me.earth.phobos.manager.TotemPopManager;
import me.earth.phobos.manager.WaypointManager;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.opengl.Display;

@Mod(modid="phobos", name="Phobos", version="1.9.0")
public class Phobos {
    public static final String MODID = "phobos";
    public static final String MODNAME = "Phobos";
    public static final String MODVER = "1.9.0";
    public static final String NAME_UNICODE = "3\u1d00\u0280\u1d1b\u029c\u029c4\u1d04\u1d0b";
    public static final String PHOBOS_UNICODE = "\u1d18\u029c\u1d0f\u0299\u1d0f\ua731";
    public static final String CHAT_SUFFIX = " \u23d0 3\u1d00\u0280\u1d1b\u029c\u029c4\u1d04\u1d0b";
    public static final String PHOBOS_SUFFIX = " \u23d0 \u1d18\u029c\u1d0f\u0299\u1d0f\ua731";
    public static final Logger LOGGER = LogManager.getLogger((String)"3arthh4ck");
    public static ModuleManager moduleManager;
    public static SpeedManager speedManager;
    public static PositionManager positionManager;
    public static RotationManager rotationManager;
    public static CommandManager commandManager;
    public static EventManager eventManager;
    public static ConfigManager configManager;
    public static FileManager fileManager;
    public static FriendManager friendManager;
    public static TextManager textManager;
    public static ColorManager colorManager;
    public static ServerManager serverManager;
    public static PotionManager potionManager;
    public static InventoryManager inventoryManager;
    public static TimerManager timerManager;
    public static PacketManager packetManager;
    public static ReloadManager reloadManager;
    public static TotemPopManager totemPopManager;
    public static HoleManager holeManager;
    public static NotificationManager notificationManager;
    public static SafetyManager safetyManager;
    public static GuiCustomMainScreen customMainScreen;
    public static CosmeticsManager cosmeticsManager;
    public static NoStopManager baritoneManager;
    public static WaypointManager waypointManager;
    @Mod.Instance
    public static Phobos INSTANCE;
    private static boolean unloaded;

    public static void load() {
        LOGGER.info("\n\nLoading 3arthh4ck 1.9.0");
        unloaded = false;
        if (reloadManager != null) {
            reloadManager.unload();
            reloadManager = null;
        }
//        capeManager = new CapeManager();
        baritoneManager = new NoStopManager();
        totemPopManager = new TotemPopManager();
        timerManager = new TimerManager();
        packetManager = new PacketManager();
        serverManager = new ServerManager();
        colorManager = new ColorManager();
        textManager = new TextManager();
        moduleManager = new ModuleManager();
        speedManager = new SpeedManager();
        rotationManager = new RotationManager();
        positionManager = new PositionManager();
        commandManager = new CommandManager();
        eventManager = new EventManager();
        configManager = new ConfigManager();
        fileManager = new FileManager();
        friendManager = new FriendManager();
        potionManager = new PotionManager();
        inventoryManager = new InventoryManager();
        holeManager = new HoleManager();
        notificationManager = new NotificationManager();
        safetyManager = new SafetyManager();
        waypointManager = new WaypointManager();
        LOGGER.info("Initialized Management");
        moduleManager.init();
        LOGGER.info("Modules loaded.");
        configManager.init();
        eventManager.init();
        LOGGER.info("EventManager loaded.");
        textManager.init(true);
        moduleManager.onLoad();
        totemPopManager.init();
        timerManager.init();
        if (moduleManager.getModuleByClass(RPC.class).isEnabled()) {
            DiscordPresence.start();
        }
        cosmeticsManager = new CosmeticsManager();
        LOGGER.info("3arthh4ck initialized!\n");
    }

    public static void unload(boolean unload) {
        LOGGER.info("\n\nUnloading 3arthh4ck 1.9.0");
        if (unload) {
            reloadManager = new ReloadManager();
            reloadManager.init(commandManager != null ? commandManager.getPrefix() : ".");
        }
        if (baritoneManager != null) {
            baritoneManager.stop();
        }
        Phobos.onUnload();
//        capeManager = null;
        eventManager = null;
        holeManager = null;
        timerManager = null;
        moduleManager = null;
        totemPopManager = null;
        serverManager = null;
        colorManager = null;
        textManager = null;
        speedManager = null;
        rotationManager = null;
        positionManager = null;
        commandManager = null;
        configManager = null;
        fileManager = null;
        friendManager = null;
        potionManager = null;
        inventoryManager = null;
        notificationManager = null;
        safetyManager = null;
        LOGGER.info("3arthh4ck unloaded!\n");
    }

    public static void reload() {
        Phobos.unload(false);
        Phobos.load();
    }

    public static void onUnload() {
        if (!unloaded) {
            try {
                PhobosChat.INSTANCE.disconnect();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
            eventManager.onUnload();
            moduleManager.onUnload();
            configManager.saveConfig(Phobos.configManager.config.replaceFirst("phobos/", ""));
            moduleManager.onUnloadPost();
            timerManager.unload();
//            new TrackerID();
            unloaded = true;
        }
    }

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        LOGGER.info("ohare is cute!!!");
        LOGGER.info("faggot above - 3vt");
        LOGGER.info("megyn wins again");
        LOGGER.info("gtfo my logs - 3arth");
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        customMainScreen = new GuiCustomMainScreen();
        Display.setTitle((String)"3arthh4ck - v1.9.0");
        Phobos.load();
    }

    static {
        unloaded = false;
    }
}

