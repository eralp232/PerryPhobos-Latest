
/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.audio.SoundHandler
 *  net.minecraft.client.audio.SoundManager
 *  net.minecraftforge.fml.common.ObfuscationReflectionHelper
 */
package me.earth.phobos.features.command.commands;

import me.earth.phobos.features.command.Command;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.client.audio.SoundManager;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;

public class ReloadSoundCommand
extends Command {
    public ReloadSoundCommand() {
        super("reloadsound", new String[0]);
    }

    @Override
    public void execute(String[] commands) {
        try {
            SoundManager sndManager = (SoundManager)ObfuscationReflectionHelper.getPrivateValue(SoundHandler.class, mc.getSoundHandler(), (String[])new String[]{"sndManager", "sndManager"});
            sndManager.reloadSoundSystem();
            ReloadSoundCommand.sendMessage("\u00a7aReloaded Sound System.");
        }
        catch (Exception e) {
            System.out.println("Could not restart sound manager: " + e);
            e.printStackTrace();
            ReloadSoundCommand.sendMessage("\u00a7cCouldn't Reload Sound System!");
        }
    }
}

