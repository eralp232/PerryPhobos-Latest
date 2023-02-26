
/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  joptsimple.internal.Strings
 *  net.minecraft.network.Packet
 *  net.minecraft.network.play.client.CPacketTabComplete
 *  net.minecraft.network.play.server.SPacketTabComplete
 *  net.minecraftforge.fml.common.eventhandler.SubscribeEvent
 */
package me.earth.phobos.features.modules.player;

import java.util.ArrayList;
import java.util.Collections;
import joptsimple.internal.Strings;
import me.earth.phobos.event.events.PacketEvent;
import me.earth.phobos.features.command.Command;
import me.earth.phobos.features.modules.Module;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketTabComplete;
import net.minecraft.network.play.server.SPacketTabComplete;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class PluginsGrabber
extends Module {
    public PluginsGrabber() {
        super("PluginsGrabber", "Attempts to use TabComplete packets to get the plugins list.", Module.Category.PLAYER, true, false, false);
    }

    @Override
    public void onEnable() {
        super.onEnable();
        Command.sendMessage("Attempting to obtain the plugins");
        CPacketTabComplete packet = new CPacketTabComplete("/", null, false);
        PluginsGrabber.mc.player.connection.sendPacket((Packet)packet);
    }

    @SubscribeEvent
    public void onReceivePacket(PacketEvent event) {
        if (event.getPacket() instanceof SPacketTabComplete) {
            String[] commands;
            SPacketTabComplete s3APacketTabComplete = (SPacketTabComplete)event.getPacket();
            ArrayList<String> plugins = new ArrayList<String>();
            for (String s : commands = s3APacketTabComplete.getMatches()) {
                String pluginName;
                String[] command = s.split(":");
                if (command.length <= 1 || plugins.contains(pluginName = command[0].replace("/", ""))) continue;
                plugins.add(pluginName);
            }
            Collections.sort(plugins);
            if (!plugins.isEmpty()) {
                Command.sendMessage("Plugins \u00a77(\u00a78" + plugins.size() + "\u00a77): \u00a79" + Strings.join((String[])plugins.toArray(new String[0]), (String)"\u00a77, \u00a79"));
            } else {
                Command.sendMessage("No plugins found");
            }
            this.disable();
        }
    }
}

