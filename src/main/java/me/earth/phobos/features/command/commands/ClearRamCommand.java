/*
 * Decompiled with CFR 0.151.
 */
package me.earth.phobos.features.command.commands;

import me.earth.phobos.features.command.Command;

public class ClearRamCommand
extends Command {
    public ClearRamCommand() {
        super("clearram");
    }

    @Override
    public void execute(String[] commands) {
        System.gc();
        Command.sendMessage("Finished clearing the ram.", false);
    }
}

