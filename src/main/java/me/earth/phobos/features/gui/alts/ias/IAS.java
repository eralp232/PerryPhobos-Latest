
/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.resources.I18n
 *  net.minecraftforge.common.MinecraftForge
 *  net.minecraftforge.common.config.Configuration
 *  net.minecraftforge.common.config.Property
 *  net.minecraftforge.fml.common.Mod
 *  net.minecraftforge.fml.common.Mod$EventHandler
 *  net.minecraftforge.fml.common.event.FMLInitializationEvent
 *  net.minecraftforge.fml.common.event.FMLPostInitializationEvent
 *  net.minecraftforge.fml.common.event.FMLPreInitializationEvent
 */
package me.earth.phobos.features.gui.alts.ias;

import me.earth.phobos.features.gui.alts.MR;
import me.earth.phobos.features.gui.alts.ias.config.ConfigValues;
import me.earth.phobos.features.gui.alts.ias.events.ClientEvents;
import me.earth.phobos.features.gui.alts.ias.tools.SkinTools;
import me.earth.phobos.features.gui.alts.iasencrypt.Standards;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

@Mod(modid="ias", name="In-Game Account Switcher", clientSideOnly=true, guiFactory="me.earth.phobos.features.gui.alts.ias.config.IASGuiFactory", updateJSON="https://thefireplace.bitnamiapp.com/jsons/ias.json", acceptedMinecraftVersions="[1.11,)")
public class IAS {
    public static Configuration config;
    private static Property CASESENSITIVE_PROPERTY;
    private static Property ENABLERELOG_PROPERTY;

    public static void syncConfig() {
        ConfigValues.CASESENSITIVE = CASESENSITIVE_PROPERTY.getBoolean();
        ConfigValues.ENABLERELOG = ENABLERELOG_PROPERTY.getBoolean();
        if (config.hasChanged()) {
            config.save();
        }
    }

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        config = new Configuration(event.getSuggestedConfigurationFile());
        config.load();
        CASESENSITIVE_PROPERTY = config.get("general", "ias.cfg.casesensitive", false, I18n.format((String)"ias.cfg.casesensitive.tooltip", (Object[])new Object[0]));
        ENABLERELOG_PROPERTY = config.get("general", "ias.cfg.enablerelog", false, I18n.format((String)"ias.cfg.enablerelog.tooltip", (Object[])new Object[0]));
        IAS.syncConfig();
        if (!event.getModMetadata().version.equals("${version}")) {
            Standards.updateFolder();
        } else {
            System.out.println("Dev environment detected!");
        }
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        MR.init();
        MinecraftForge.EVENT_BUS.register((Object)new ClientEvents());
        Standards.importAccounts();
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        SkinTools.cacheSkins();
    }
}

