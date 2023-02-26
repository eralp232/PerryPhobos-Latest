/*
 * Decompiled with CFR 0.151.
 */
package me.earth.phobos.features.command.commands;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import me.earth.phobos.features.command.Command;

public class OpenFolderCommand
extends Command {
    public OpenFolderCommand() {
        super("openfolder", new String[0]);
    }

    @Override
    public void execute(String[] commands) {
        try {
            Desktop.getDesktop().open(new File("phobos/"));
            Command.sendMessage("Opened config folder!", false);
        }
        catch (IOException e) {
            Command.sendMessage("Could not open config folder!", false);
            e.printStackTrace();
        }
    }
}

