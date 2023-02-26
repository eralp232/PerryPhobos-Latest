
/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.renderer.GlStateManager
 *  net.minecraft.client.renderer.GlStateManager$DestFactor
 *  net.minecraft.client.renderer.GlStateManager$SourceFactor
 *  net.minecraft.client.renderer.RenderHelper
 *  net.minecraft.init.SoundEvents
 *  net.minecraft.item.ItemStack
 *  net.minecraft.util.ResourceLocation
 *  net.minecraft.util.SoundCategory
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.RayTraceResult
 *  org.lwjgl.input.Keyboard
 */
package me.earth.phobos.features.modules.client;

import java.awt.Color;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import me.earth.phobos.Phobos;
import me.earth.phobos.event.events.Render2DEvent;
import me.earth.phobos.event.events.Render3DEvent;
import me.earth.phobos.features.Feature;
import me.earth.phobos.features.command.Command;
import me.earth.phobos.features.gui.PhobosGui;
import me.earth.phobos.features.modules.Module;
import me.earth.phobos.features.setting.Bind;
import me.earth.phobos.features.setting.Setting;
import me.earth.phobos.manager.WaypointManager;
import me.earth.phobos.util.ColorUtil;
import me.earth.phobos.util.RenderUtil;
import me.earth.phobos.util.TimerUtil;
import me.earth.phobos.util.Util;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import org.lwjgl.input.Keyboard;

public class PhobosChat
extends Module {
    private static final ResourceLocation SHULKER_GUI_TEXTURE = null;
    public static PhobosChat INSTANCE;
    public static IRCHandler handler;
    public static List<String> phobosUsers;
    public Setting<String> ip = this.register(new Setting<String>("IP", "127.0.0.1"));
    public Setting<Boolean> waypoints = this.register(new Setting<Boolean>("Waypoints", false));
    public Setting<Boolean> ding = this.register(new Setting<Boolean>("Ding", Boolean.valueOf(false), v -> this.waypoints.getValue()));
    public Setting<Integer> red = this.register(new Setting<Object>("Red", Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(255), v -> this.waypoints.getValue()));
    public Setting<Integer> green = this.register(new Setting<Object>("Green", Integer.valueOf(255), Integer.valueOf(0), Integer.valueOf(255), v -> this.waypoints.getValue()));
    public Setting<Integer> blue = this.register(new Setting<Object>("Blue", Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(255), v -> this.waypoints.getValue()));
    public Setting<Integer> alpha = this.register(new Setting<Object>("Alpha", Integer.valueOf(255), Integer.valueOf(0), Integer.valueOf(255), v -> this.waypoints.getValue()));
    public Setting<Boolean> inventories = this.register(new Setting<Boolean>("Inventories", false));
    public Setting<Boolean> render = this.register(new Setting<Object>("Render", Boolean.valueOf(true), v -> this.inventories.getValue()));
    public Setting<Integer> cooldown = this.register(new Setting<Object>("ShowForS", Integer.valueOf(2), Integer.valueOf(0), Integer.valueOf(5), v -> this.inventories.getValue()));
    public Setting<Boolean> offsets = this.register(new Setting<Boolean>("Offsets", false));
    private final Setting<Integer> yPerPlayer = this.register(new Setting<Object>("Y/Player", Integer.valueOf(18), v -> this.offsets.getValue()));
    private final Setting<Integer> xOffset = this.register(new Setting<Object>("XOffset", Integer.valueOf(4), v -> this.offsets.getValue()));
    private final Setting<Integer> yOffset = this.register(new Setting<Object>("YOffset", Integer.valueOf(2), v -> this.offsets.getValue()));
    public Setting<Integer> invH = this.register(new Setting<Object>("InvH", Integer.valueOf(3), v -> this.inventories.getValue()));
    public Setting<Bind> pingBind = this.register(new Setting<Bind>("Ping", new Bind(-1)));
    public boolean status;
    public TimerUtil updateTimer = new TimerUtil();
    public TimerUtil downTimer = new TimerUtil();
    public BlockPos waypointTarget;
    private boolean down;
    private boolean pressed;

    public PhobosChat() {
        super("PhobosChat", "Phobos chat server.", Module.Category.CLIENT, true, false, true);
        INSTANCE = this;
    }

    public static void updateInventory() throws IOException {
        PhobosChat.handler.outputStream.writeUTF("updateinventory");
        PhobosChat.handler.outputStream.writeUTF(PhobosChat.mc.player.getName());
        PhobosChat.writeByteArray(PhobosChat.serializeInventory(), PhobosChat.handler.outputStream);
    }

    public static void updateWaypoint(BlockPos pos, String server, String dimension, Color color) throws IOException {
        PhobosChat.send("waypoint", server + ":" + dimension + ":" + pos.getX() + ":" + pos.getY() + ":" + pos.getZ(), color.getRed() + ":" + color.getGreen() + ":" + color.getBlue() + ":" + color.getAlpha());
    }

    public static void removeWaypoint() throws IOException {
        PhobosChat.handler.outputStream.writeUTF("removewaypoint");
        PhobosChat.handler.outputStream.writeUTF(PhobosChat.mc.player.getName());
        PhobosChat.handler.outputStream.flush();
    }

    public static void send(String command, String data, String data1) throws IOException {
        PhobosChat.handler.outputStream.writeUTF(command);
        PhobosChat.handler.outputStream.writeUTF(PhobosChat.mc.player.getName());
        PhobosChat.handler.outputStream.writeUTF(data);
        PhobosChat.handler.outputStream.writeUTF(data1);
        PhobosChat.handler.outputStream.flush();
    }

    public static void send(String command, String data) throws IOException {
        PhobosChat.handler.outputStream.writeUTF(command);
        PhobosChat.handler.outputStream.writeUTF(PhobosChat.mc.player.getName());
        PhobosChat.handler.outputStream.writeUTF(data);
        PhobosChat.handler.outputStream.flush();
    }

    private static byte[] readByteArrayLWithLength(DataInputStream reader) throws IOException {
        int length = reader.readInt();
        if (length > 0) {
            byte[] cifrato = new byte[length];
            reader.readFully(cifrato, 0, cifrato.length);
            return cifrato;
        }
        return null;
    }

    public static void writeByteArray(byte[] data, DataOutputStream writer) throws IOException {
        writer.writeInt(data.length);
        writer.write(data);
        writer.flush();
    }

    public static List<ItemStack> deserializeInventory(byte[] inventory) throws IOException, ClassNotFoundException {
        ObjectInputStream stream = new ObjectInputStream(new ByteArrayInputStream(inventory));
        return (ArrayList)stream.readObject();
    }

    public static byte[] serializeInventory() throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(bos);
        oos.writeObject(new ArrayList(PhobosChat.mc.player.inventory.mainInventory));
        return bos.toByteArray();
    }

    public static void say(String message) throws IOException {
        PhobosChat.handler.outputStream.writeUTF("message");
        PhobosChat.handler.outputStream.writeUTF(PhobosChat.mc.player.getName());
        PhobosChat.handler.outputStream.writeUTF(message);
        PhobosChat.handler.outputStream.flush();
    }

    public static void cockt(int id) throws IOException {
        PhobosChat.handler.outputStream.writeUTF("cockt");
        PhobosChat.handler.outputStream.writeInt(id);
        PhobosChat.handler.outputStream.flush();
    }

    public static String getDimension(int dim) {
        switch (dim) {
            case 0: {
                return "Overworld";
            }
            case -1: {
                return "Nether";
            }
            case 1: {
                return "End";
            }
        }
        return "";
    }

    @Override
    public void onUpdate() {
        this.status = handler != null && handler.isAlive() && !handler.isInterrupted() ? !PhobosChat.handler.socket.isClosed() : false;
        if (this.updateTimer.passedMs(5000L) && handler != null && handler.isAlive() && !PhobosChat.handler.socket.isClosed()) {
            try {
                PhobosChat.handler.outputStream.writeUTF("update");
                PhobosChat.handler.outputStream.writeUTF(PhobosChat.mc.player.getName());
                PhobosChat.handler.outputStream.flush();
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            this.updateTimer.reset();
        }
        if (!mc.isSingleplayer() && !(PhobosChat.mc.currentScreen instanceof PhobosGui) && handler != null && !PhobosChat.handler.socket.isClosed() && this.status) {
            if (this.down) {
                if (this.downTimer.passedMs(2000L)) {
                    try {
                        PhobosChat.removeWaypoint();
                    }
                    catch (IOException e2) {
                        e2.printStackTrace();
                    }
                    this.down = false;
                    this.downTimer.reset();
                }
                if (!Keyboard.isKeyDown((int)this.pingBind.getValue().getKey())) {
                    try {
                        PhobosChat.updateWaypoint(this.waypointTarget, PhobosChat.mc.currentServerData.serverIP, String.valueOf(PhobosChat.mc.player.dimension), new Color(this.red.getValue(), this.green.getValue(), this.blue.getValue(), this.alpha.getValue()));
                    }
                    catch (IOException e2) {
                        e2.printStackTrace();
                    }
                }
            }
            if (Keyboard.isKeyDown((int)this.pingBind.getValue().getKey())) {
                if (!this.pressed) {
                    this.down = true;
                    this.pressed = true;
                }
            } else {
                this.down = false;
                this.pressed = false;
                this.downTimer.reset();
            }
        }
    }

    @Override
    public void onRender3D(Render3DEvent event) {
        if (Feature.fullNullCheck() || mc.isSingleplayer()) {
            return;
        }
        RayTraceResult result = PhobosChat.mc.player.rayTrace(2000.0, event.getPartialTicks());
        if (result != null) {
            this.waypointTarget = new BlockPos(result.hitVec);
        }
        if (this.waypoints.getValue().booleanValue()) {
            for (WaypointManager.Waypoint waypoint : Phobos.waypointManager.waypoints.values()) {
                if (PhobosChat.mc.player.dimension != waypoint.dimension || !PhobosChat.mc.currentServerData.serverIP.equals(waypoint.server)) continue;
                waypoint.renderBox();
                waypoint.render();
                GlStateManager.enableDepth();
                GlStateManager.depthMask((boolean)true);
                GlStateManager.enableLighting();
                GlStateManager.disableBlend();
                GlStateManager.color((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
                RenderHelper.disableStandardItemLighting();
            }
        }
    }

    @Override
    public void onRender2D(Render2DEvent event) {
        if (PhobosChat.fullNullCheck()) {
            return;
        }
        if (this.inventories.getValue().booleanValue()) {
            int x = -4 + this.xOffset.getValue();
            int y = 10 + this.yOffset.getValue();
            for (String player : phobosUsers) {
                if (Phobos.inventoryManager.inventories.get(player) != null) continue;
                List<ItemStack> stacks = Phobos.inventoryManager.inventories.get(player);
                this.renderShulkerToolTip(stacks, x, y, player);
                y += this.yPerPlayer.getValue() + 60;
            }
        }
    }

    public void connect() throws IOException {
        if (!PhobosChat.INSTANCE.status) {
            Socket socket = new Socket(this.ip.getValue(), 1488);
            handler = new IRCHandler(socket);
            handler.start();
            PhobosChat.handler.outputStream.writeUTF("update");
            PhobosChat.handler.outputStream.writeUTF(PhobosChat.mc.player.getName());
            PhobosChat.handler.outputStream.flush();
            PhobosChat.INSTANCE.status = true;
            Command.sendMessage("\u00a7aIRC connected successfully!");
        } else {
            Command.sendMessage("\u00a7cIRC is already connected!");
        }
    }

    public void disconnect() throws IOException {
        if (PhobosChat.INSTANCE.status) {
            PhobosChat.handler.socket.close();
            if (!handler.isInterrupted()) {
                handler.interrupt();
            }
        } else {
            Command.sendMessage("\u00a7cIRC is not connected!");
        }
    }

    public void friendAll() throws IOException {
        PhobosChat.handler.outputStream.writeUTF("friendall");
        PhobosChat.handler.outputStream.flush();
    }

    public void list() throws IOException {
        PhobosChat.handler.outputStream.writeUTF("list");
        PhobosChat.handler.outputStream.flush();
    }

    public void renderShulkerToolTip(List<ItemStack> stacks, int x, int y, String name) {
        GlStateManager.enableTexture2D();
        GlStateManager.disableLighting();
        GlStateManager.color((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate((GlStateManager.SourceFactor)GlStateManager.SourceFactor.SRC_ALPHA, (GlStateManager.DestFactor)GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, (GlStateManager.SourceFactor)GlStateManager.SourceFactor.ONE, (GlStateManager.DestFactor)GlStateManager.DestFactor.ZERO);
        assert (false);
        mc.getTextureManager().bindTexture(SHULKER_GUI_TEXTURE);
        RenderUtil.drawTexturedRect(x, y, 0, 0, 176, 16, 500);
        RenderUtil.drawTexturedRect(x, y + 16, 0, 16, 176, 54 + this.invH.getValue(), 500);
        RenderUtil.drawTexturedRect(x, y + 16 + 54, 0, 160, 176, 8, 500);
        GlStateManager.disableDepth();
        Color color = new Color(0, 0, 0, 255);
        this.renderer.drawStringWithShadow(name, x + 8, y + 6, ColorUtil.toRGBA(color));
        GlStateManager.enableDepth();
        RenderHelper.enableGUIStandardItemLighting();
        GlStateManager.enableRescaleNormal();
        GlStateManager.enableColorMaterial();
        GlStateManager.enableLighting();
        for (int i = 0; i < stacks.size(); ++i) {
            int iX = x + i % 9 * 18 + 8;
            int iY = y + i / 9 * 18 + 18;
            ItemStack itemStack = stacks.get(i);
            PhobosChat.mc.getRenderItem().zLevel = 501.0f;
            RenderUtil.itemRender.renderItemAndEffectIntoGUI(itemStack, iX, iY);
            RenderUtil.itemRender.renderItemOverlayIntoGUI(PhobosChat.mc.fontRenderer, itemStack, iX, iY, null);
            PhobosChat.mc.getRenderItem().zLevel = 0.0f;
        }
        GlStateManager.disableLighting();
        GlStateManager.disableBlend();
        GlStateManager.color((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
    }

    private static class IRCHandler
    extends Thread {
        public Socket socket;
        public DataInputStream inputStream;
        public DataOutputStream outputStream;

        public IRCHandler(Socket socket) {
            super(Util.mc.player.getName());
            this.socket = socket;
            try {
                this.inputStream = new DataInputStream(socket.getInputStream());
                this.outputStream = new DataOutputStream(socket.getOutputStream());
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void run() {
            Command.sendMessage("\u00a7aSocket thread starting!");
            while (true) {
                try {
                    while (true) {
                        String f;
                        String name;
                        String input;
                        if ((input = this.inputStream.readUTF()).equalsIgnoreCase("message")) {
                            name = this.inputStream.readUTF();
                            String message = this.inputStream.readUTF();
                            Command.sendMessage("\u00a7c[PhobosChat] \u00a7r<" + name + ">: " + message);
                        }
                        if (input.equalsIgnoreCase("list")) {
                            String[] split;
                            f = this.inputStream.readUTF();
                            for (String friend : split = f.split("%%%")) {
                                Command.sendMessage("\u00a7b" + friend.replace("_&_", " ID: "));
                            }
                        } else if (input.equalsIgnoreCase("friendall")) {
                            String[] split2;
                            f = this.inputStream.readUTF();
                            for (String friend : split2 = f.split("%%%")) {
                                if (friend.equals(Util.mc.player.getName())) continue;
                                Phobos.friendManager.addFriend(friend);
                                Command.sendMessage("\u00a7b" + friend + " has been friended");
                            }
                        } else if (input.equalsIgnoreCase("waypoint")) {
                            name = this.inputStream.readUTF();
                            String[] inputs = this.inputStream.readUTF().split(":");
                            String[] colors = this.inputStream.readUTF().split(":");
                            String server = inputs[0];
                            String dimension = inputs[1];
                            Color color = new Color(Integer.parseInt((String)colors[0]), Integer.parseInt((String)colors[1]), Integer.parseInt((String)colors[2]), Integer.parseInt((String)colors[3]));
                            Phobos.waypointManager.waypoints.put(name, new WaypointManager.Waypoint(name, server, Integer.parseInt(dimension), Integer.parseInt(inputs[2]), Integer.parseInt(inputs[3]), Integer.parseInt(inputs[4]), color));
                            Command.sendMessage("\u00a7c[PhobosChat] \u00a7r" + name + " has set a waypoint at \u00a7c(" + Integer.parseInt(inputs[2]) + "," + Integer.parseInt(inputs[3]) + "," + Integer.parseInt(inputs[4]) + ")\u00a7r on the server \u00a7c" + server + "\u00a7r in the dimension \u00a7c" + PhobosChat.getDimension(Integer.parseInt(dimension)));
                            if (PhobosChat.INSTANCE.ding.getValue().booleanValue()) {
                                Util.mc.world.playSound(Util.mc.player.posX, Util.mc.player.posY, Util.mc.player.posZ, SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, SoundCategory.PLAYERS, 1.0f, 0.7f, false);
                            }
                        } else if (input.equalsIgnoreCase("removewaypoint")) {
                            name = this.inputStream.readUTF();
                            Phobos.waypointManager.waypoints.remove(name);
                            Command.sendMessage("\u00a7c[PhobosChat] \u00a7r" + name + " has removed their waypoint");
                            if (PhobosChat.INSTANCE.ding.getValue().booleanValue()) {
                                Util.mc.world.playSound(Util.mc.player.posX, Util.mc.player.posY, Util.mc.player.posZ, SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, SoundCategory.PLAYERS, 1.0f, -0.7f, false);
                            }
                        } else if (input.equalsIgnoreCase("inventory")) {
                            name = this.inputStream.readUTF();
                            byte[] inventory = PhobosChat.readByteArrayLWithLength(this.inputStream);
                            for (String player : phobosUsers) {
                                if (!player.equalsIgnoreCase(name)) continue;
                                Phobos.inventoryManager.inventories.put(player, PhobosChat.deserializeInventory(inventory));
                            }
                        } else if (input.equalsIgnoreCase("users")) {
                            byte[] inputBytes = PhobosChat.readByteArrayLWithLength(this.inputStream);
                            assert (inputBytes != null);
                            ObjectInputStream stream = new ObjectInputStream(new ByteArrayInputStream(inputBytes));
                            List<String> players = (List)stream.readObject();
                            Command.sendMessage("\u00a7c[PhobosChat]\u00a7r Active Users:");
                            for (String name2 : players) {
                                Command.sendMessage(name2);
                                if (phobosUsers.contains(name2)) continue;
                                phobosUsers.add(name2);
                            }
                        }
                        PhobosChat.INSTANCE.status = !this.socket.isClosed();
                    }
                }
                catch (IOException | ClassNotFoundException e) {
                    e.printStackTrace();
                    continue;
                }
            }
        }
    }
}

