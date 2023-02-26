/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.gui.GuiScreen
 *  net.minecraftforge.common.config.ConfigElement
 *  net.minecraftforge.fml.client.config.GuiConfig
 */
package me.earth.phobos.features.gui.alts.ias.config;

import me.earth.phobos.features.gui.alts.ias.IAS;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.fml.client.config.GuiConfig;

public class IASConfigGui
extends GuiConfig {
    public IASConfigGui(GuiScreen parentScreen) {
        super(parentScreen, new ConfigElement(IAS.config.getCategory("general")).getChildElements(), "ias", false, false, GuiConfig.getAbridgedConfigPath((String)IAS.config.toString()));
    }
}

