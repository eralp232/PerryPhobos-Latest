
/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  com.mojang.realmsclient.gui.ChatFormatting
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.item.EntityEnderPearl
 *  net.minecraft.entity.monster.EntityGhast
 *  net.minecraft.entity.passive.EntityDonkey
 *  net.minecraft.entity.passive.EntityLlama
 *  net.minecraft.entity.passive.EntityMule
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.init.Blocks
 *  net.minecraft.init.MobEffects
 *  net.minecraft.init.SoundEvents
 *  net.minecraft.item.ItemStack
 *  net.minecraft.item.ItemSword
 *  net.minecraft.nbt.NBTTagList
 *  net.minecraft.network.play.server.SPacketPlayerPosLook
 *  net.minecraft.potion.Potion
 *  net.minecraft.util.EnumHand
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.text.ITextComponent
 *  net.minecraft.util.text.TextComponentString
 *  net.minecraftforge.fml.common.eventhandler.SubscribeEvent
 */
package me.earth.phobos.features.modules.client;

import com.mojang.realmsclient.gui.ChatFormatting;
import java.awt.Image;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.stream.Collectors;
import me.earth.phobos.Phobos;
import me.earth.phobos.event.events.ChorusEvent;
import me.earth.phobos.event.events.ClientEvent;
import me.earth.phobos.event.events.PacketEvent;
import me.earth.phobos.features.command.Command;
import me.earth.phobos.features.modules.Module;
import me.earth.phobos.features.setting.Setting;
import me.earth.phobos.manager.FileManager;
import me.earth.phobos.util.MathUtil;
import me.earth.phobos.util.TimerUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderPearl;
import net.minecraft.entity.monster.EntityGhast;
import net.minecraft.entity.passive.EntityDonkey;
import net.minecraft.entity.passive.EntityLlama;
import net.minecraft.entity.passive.EntityMule;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.play.server.SPacketPlayerPosLook;
import net.minecraft.potion.Potion;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class Notifications
extends Module {
    private static final String fileName = "phobos/util/ModuleMessage_List.txt";
    private static final List<String> modules = new ArrayList<String>();
    private static Notifications INSTANCE = new Notifications();
    private final TimerUtil timer = new TimerUtil();
    private final List<EntityPlayer> burrowedPlayers = new ArrayList<EntityPlayer>();
    private final Set<EntityPlayer> sword = Collections.newSetFromMap(new WeakHashMap());
    private final TimerUtil weak = new TimerUtil();
    private final TimerUtil strgh = new TimerUtil();
    private final TimerUtil voids = new TimerUtil();
    private final Set<EntityPlayer> str = Collections.newSetFromMap(new WeakHashMap());
    private final Set<Entity> ghasts = new HashSet<Entity>();
    private final Set<Entity> donkeys = new HashSet<Entity>();
    private final Set<Entity> mules = new HashSet<Entity>();
    private final Set<Entity> llamas = new HashSet<Entity>();
    public Setting<Boolean> totemPops = this.register(new Setting<Boolean>("TotemPops", true));
    public Setting<Boolean> totemNoti = this.register(new Setting<Object>("TotemNoti", Boolean.valueOf(false), v -> this.totemPops.getValue()));
    public Setting<Integer> delay = this.register(new Setting<Object>("Delay", 0, 0, 5000, v -> this.totemPops.getValue(), "Delays messages."));
    public Setting<Boolean> clearOnLogout = this.register(new Setting<Boolean>("LogoutClear", false));
    public Setting<Boolean> moduleMessage = this.register(new Setting<Boolean>("ModuleMessage", true));
    private final Setting<Boolean> bold = this.register(new Setting<Object>("BoldMsgs", Boolean.valueOf(false), v -> this.moduleMessage.getValue()));
    private final Setting<Boolean> readfile = this.register(new Setting<Object>("LoadFile", Boolean.valueOf(false), v -> this.moduleMessage.getValue()));
    public Setting<Boolean> list = this.register(new Setting<Object>("List", Boolean.valueOf(false), v -> this.moduleMessage.getValue()));
    public Setting<Boolean> watermark = this.register(new Setting<Object>("Watermark", Boolean.valueOf(true), v -> this.moduleMessage.getValue()));
    public Setting<Boolean> visualRange = this.register(new Setting<Boolean>("VisualRange", false));
    public Setting<Boolean> VisualRangeSound = this.register(new Setting<Boolean>("VisualRangeSound", false));
    public Setting<Boolean> coords = this.register(new Setting<Object>("Coords", Boolean.valueOf(true), v -> this.visualRange.getValue()));
    public Setting<Boolean> leaving = this.register(new Setting<Object>("Leaving", Boolean.valueOf(true), v -> this.visualRange.getValue()));
    public Setting<Boolean> pearls = this.register(new Setting<Boolean>("PearlNotifs", true));
    public Setting<Boolean> crash = this.register(new Setting<Boolean>("Crash", true));
    public Setting<Boolean> popUp = this.register(new Setting<Boolean>("PopUpVisualRange", false));
    public Setting<Boolean> burrow = this.register(new Setting<Boolean>("Burrow", false));
    public Setting<Boolean> strength = this.register(new Setting<Boolean>("Strength", false));
    public Setting<Integer> strcheckrate = this.register(new Setting<Integer>("CheckRate", Integer.valueOf(500), Integer.valueOf(0), Integer.valueOf(5000), v -> this.strength.getValue()));
    public Setting<Boolean> weakness = this.register(new Setting<Boolean>("Weakness", false));
    public Setting<Boolean> strongness = this.register(new Setting<Boolean>("Strongness", Boolean.valueOf(false), v -> this.weakness.getValue()));
    public Setting<Integer> checkrate = this.register(new Setting<Integer>("CheckRate", Integer.valueOf(500), Integer.valueOf(0), Integer.valueOf(5000), v -> this.weakness.getValue()));
    public Setting<Boolean> thirtytwokay = this.register(new Setting<Boolean>("32k", false));
    public Setting<Boolean> voider = this.register(new Setting<Boolean>("Void", false));
    public Setting<Integer> voidcheckrate = this.register(new Setting<Integer>("CheckRate", Integer.valueOf(500), Integer.valueOf(0), Integer.valueOf(5000), v -> this.voider.getValue()));
    public Setting<Boolean> tp = this.register(new Setting<Boolean>("Chorus", false));
    public Setting<Boolean> rb = this.register(new Setting<Boolean>("Rubberband", false));
    public Setting<Boolean> entities = this.register(new Setting<Boolean>("Entities", false));
    public Setting<Boolean> Desktop = this.register(new Setting<Boolean>("DesktopNotifs", Boolean.valueOf(false), v -> this.entities.getValue()));
    public Setting<Boolean> Ghasts = this.register(new Setting<Boolean>("Ghasts", Boolean.valueOf(false), v -> this.entities.getValue()));
    public Setting<Boolean> Donkeys = this.register(new Setting<Boolean>("Donkeys", Boolean.valueOf(false), v -> this.entities.getValue()));
    public Setting<Boolean> Mules = this.register(new Setting<Boolean>("Mules", Boolean.valueOf(false), v -> this.entities.getValue()));
    public Setting<Boolean> Llamas = this.register(new Setting<Boolean>("Llamas", Boolean.valueOf(false), v -> this.entities.getValue()));
    public Setting<Boolean> Chat = this.register(new Setting<Boolean>("Chat", Boolean.valueOf(false), v -> this.entities.getValue()));
    public Setting<Boolean> Sound = this.register(new Setting<Boolean>("Sound", Boolean.valueOf(false), v -> this.entities.getValue()));
    public TimerUtil totemAnnounce = new TimerUtil();
    Image image = Toolkit.getDefaultToolkit().getImage("phobos.png");
    private final TrayIcon icon = new TrayIcon(this.image, "Perry Phobos");
    private boolean flag;
    private List<EntityPlayer> knownPlayers = new ArrayList<EntityPlayer>();
    private boolean check;
    private boolean last;

    public Notifications() {
        super("Notifications", "Sends Messages.", Module.Category.CLIENT, true, false, false);
        this.setInstance();
        this.icon.setImageAutoSize(true);
        try {
            SystemTray tray = SystemTray.getSystemTray();
            tray.add(this.icon);
        }
        catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    public static Notifications getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new Notifications();
        }
        return INSTANCE;
    }

    public static void displayCrash(Exception e) {
        Command.sendMessage("\u00a7cException caught: " + e.getMessage());
    }

    @SubscribeEvent
    public void onPacketReceive(PacketEvent.Receive event) {
        if (event.getPacket() instanceof SPacketPlayerPosLook && this.rb.getValue().booleanValue()) {
            Command.sendMessage(ChatFormatting.RED + "Rubberband Detected!");
        }
    }

    private void setInstance() {
        INSTANCE = this;
    }

    private boolean is32k(ItemStack stack) {
        if (stack.getItem() instanceof ItemSword) {
            NBTTagList enchants = stack.getEnchantmentTagList();
            for (int i = 0; i < enchants.tagCount(); ++i) {
                if (enchants.getCompoundTagAt(i).getShort("lvl") < Short.MAX_VALUE) continue;
                return true;
            }
        }
        return false;
    }

    @Override
    public void onLoad() {
        this.check = true;
        this.loadFile();
        this.check = false;
    }

    @Override
    public void onEnable() {
        this.knownPlayers = new ArrayList<EntityPlayer>();
        if (!this.check) {
            this.loadFile();
            this.flag = true;
        }
        this.ghasts.clear();
        this.donkeys.clear();
        this.mules.clear();
        this.llamas.clear();
    }

    @SubscribeEvent
    public void onChorus(ChorusEvent event) {
        if (this.tp.getValue().booleanValue()) {
            Command.sendMessage(ChatFormatting.DARK_PURPLE + "A player teleported " + MathUtil.getDirectionFromPlayer(event.getChorusX(), event.getChorusZ()) + "!");
        }
    }

    @Override
    public void onUpdate() {
        if (this.entities.getValue().booleanValue() && this.Ghasts.getValue().booleanValue()) {
            for (Entity entity : Notifications.mc.world.getLoadedEntityList()) {
                if (!(entity instanceof EntityGhast) || this.ghasts.contains(entity)) continue;
                if (this.Chat.getValue().booleanValue()) {
                    Command.sendMessage("Ghast Detected at: " + Math.round(entity.getPosition().getX()) + ", " + Math.round(entity.getPosition().getY()) + ", " + Math.round(entity.getPosition().getZ()) + ".");
                }
                this.ghasts.add((Entity)entity);
                if (this.Desktop.getValue().booleanValue()) {
                    this.icon.displayMessage("Perry Phobos", "I found a ghast at: " + Math.round(entity.getPosition().getX()) + ", " + Math.round(entity.getPosition().getY()) + ", " + Math.round(entity.getPosition().getZ()) + ".", TrayIcon.MessageType.INFO);
                }
                if (!this.Sound.getValue().booleanValue()) continue;
                Notifications.mc.player.playSound(SoundEvents.BLOCK_ANVIL_DESTROY, 1.0f, 1.0f);
            }
        }
        if (this.entities.getValue().booleanValue() && this.Donkeys.getValue().booleanValue()) {
            for (Entity entity : Notifications.mc.world.getLoadedEntityList()) {
                if (!(entity instanceof EntityDonkey) || this.donkeys.contains(entity)) continue;
                if (this.Chat.getValue().booleanValue()) {
                    Command.sendMessage("Donkey Detected at: " + Math.round(entity.getPosition().getX()) + ", " + Math.round(entity.getPosition().getY()) + ", " + Math.round(entity.getPosition().getZ()) + ".");
                }
                this.donkeys.add((Entity)entity);
                if (this.Desktop.getValue().booleanValue()) {
                    this.icon.displayMessage("Perry Phobos", "I found a donkey at: " + Math.round(entity.getPosition().getX()) + ", " + Math.round(entity.getPosition().getY()) + ", " + Math.round(entity.getPosition().getZ()) + ".", TrayIcon.MessageType.INFO);
                }
                if (!this.Sound.getValue().booleanValue()) continue;
                Notifications.mc.player.playSound(SoundEvents.BLOCK_ANVIL_DESTROY, 1.0f, 1.0f);
            }
        }
        if (this.entities.getValue().booleanValue() && this.Mules.getValue().booleanValue()) {
            for (Entity entity : Notifications.mc.world.getLoadedEntityList()) {
                if (!(entity instanceof EntityMule) || this.mules.contains(entity)) continue;
                if (this.Chat.getValue().booleanValue()) {
                    Command.sendMessage("Mule Detected at: " + Math.round(entity.getPosition().getX()) + ", " + Math.round(entity.getPosition().getY()) + ", " + Math.round(entity.getPosition().getZ()) + ".");
                }
                this.mules.add((Entity)entity);
                if (this.Desktop.getValue().booleanValue()) {
                    this.icon.displayMessage("Perry Phobos", "I found a mule at: " + Math.round(entity.getPosition().getX()) + ", " + Math.round(entity.getPosition().getY()) + ", " + Math.round(entity.getPosition().getZ()) + ".", TrayIcon.MessageType.INFO);
                }
                if (!this.Sound.getValue().booleanValue()) continue;
                Notifications.mc.player.playSound(SoundEvents.BLOCK_ANVIL_DESTROY, 1.0f, 1.0f);
            }
        }
        if (this.entities.getValue().booleanValue() && this.Llamas.getValue().booleanValue()) {
            for (Entity entity : Notifications.mc.world.getLoadedEntityList()) {
                if (!(entity instanceof EntityLlama) || this.llamas.contains(entity)) continue;
                if (this.Chat.getValue().booleanValue()) {
                    Command.sendMessage("Llama Detected at: " + Math.round(entity.getPosition().getX()) + ", " + Math.round(entity.getPosition().getY()) + ", " + Math.round(entity.getPosition().getZ()) + ".");
                }
                this.llamas.add((Entity)entity);
                if (this.Desktop.getValue().booleanValue()) {
                    this.icon.displayMessage("Perry Phobos", "I found a llama at: " + Math.round(entity.getPosition().getX()) + ", " + Math.round(entity.getPosition().getY()) + ", " + Math.round(entity.getPosition().getZ()) + ".", TrayIcon.MessageType.INFO);
                }
                if (!this.Sound.getValue().booleanValue()) continue;
                Notifications.mc.player.playSound(SoundEvents.BLOCK_ANVIL_DESTROY, 1.0f, 1.0f);
            }
        }
        if (this.readfile.getValue().booleanValue()) {
            if (!this.check) {
                Command.sendMessage("Loading File...");
                this.timer.reset();
                this.loadFile();
            }
            this.check = true;
        }
        if (this.check && this.timer.passedMs(750L)) {
            this.readfile.setValue(false);
            this.check = false;
        }
        if (this.voider.getValue().booleanValue()) {
            if (!this.voids.passedMs(this.voidcheckrate.getValue().intValue())) {
                return;
            }
            for (Entity entity : Notifications.mc.world.loadedEntityList) {
                if (!(entity instanceof EntityPlayer) || !(((Entity)entity).posY < 0.0) || ((EntityPlayer)entity).isSpectator()) continue;
                DecimalFormat format = new DecimalFormat("#");
                Command.sendMessage(ChatFormatting.RED + entity.getName() + ChatFormatting.RESET + " Is in the void at Y " + format.format(((Entity)entity).posY), false);
            }
            this.voids.reset();
        }
        if (this.visualRange.getValue().booleanValue()) {
            ArrayList<EntityPlayer> tickPlayerList = new ArrayList(Notifications.mc.world.playerEntities);
            if (tickPlayerList.size() > 0) {
                for (EntityPlayer player : tickPlayerList) {
                    if (player.getName().equals(Notifications.mc.player.getName()) || this.knownPlayers.contains(player)) continue;
                    this.knownPlayers.add(player);
                    if (Phobos.friendManager.isFriend(player)) {
                        Command.sendMessage("Player \u00a7a" + player.getName() + "\u00a7r entered your visual range" + (this.coords.getValue() != false ? " at (" + (int)player.posX + ", " + (int)player.posY + ", " + (int)player.posZ + ")!" : "!"), this.popUp.getValue());
                    } else {
                        Command.sendMessage("Player \u00a7c" + player.getName() + "\u00a7r entered your visual range" + (this.coords.getValue() != false ? " at (" + (int)player.posX + ", " + (int)player.posY + ", " + (int)player.posZ + ")!" : "!"), this.popUp.getValue());
                    }
                    if (this.VisualRangeSound.getValue().booleanValue()) {
                        Notifications.mc.player.playSound(SoundEvents.BLOCK_ANVIL_LAND, 1.0f, 1.0f);
                    }
                    return;
                }
            }
            if (this.knownPlayers.size() > 0) {
                for (EntityPlayer player : this.knownPlayers) {
                    if (tickPlayerList.contains(player)) continue;
                    this.knownPlayers.remove(player);
                    if (this.leaving.getValue().booleanValue()) {
                        if (Phobos.friendManager.isFriend(player)) {
                            Command.sendMessage("Player \u00a7a" + player.getName() + "\u00a7r left your visual range" + (this.coords.getValue() != false ? " at (" + (int)player.posX + ", " + (int)player.posY + ", " + (int)player.posZ + ")!" : "!"), this.popUp.getValue());
                        } else {
                            Command.sendMessage("Player \u00a7c" + player.getName() + "\u00a7r left your visual range" + (this.coords.getValue() != false ? " at (" + (int)player.posX + ", " + (int)player.posY + ", " + (int)player.posZ + ")!" : "!"), this.popUp.getValue());
                        }
                    }
                    return;
                }
            }
        }
        if (this.pearls.getValue().booleanValue()) {
            if (Notifications.mc.world == null || Notifications.mc.player == null) {
                return;
            }
            Entity enderPearl = null;
            for (Entity e : Notifications.mc.world.loadedEntityList) {
                if (!(e instanceof EntityEnderPearl)) continue;
                enderPearl = e;
                break;
            }
            if (enderPearl == null) {
                this.flag = true;
                return;
            }
            EntityPlayer closestPlayer = null;
            for (EntityPlayer entity : Notifications.mc.world.playerEntities) {
                if (closestPlayer == null) {
                    closestPlayer = entity;
                    continue;
                }
                if (closestPlayer.getDistance(enderPearl) <= entity.getDistance(enderPearl)) continue;
                closestPlayer = entity;
            }
            if (closestPlayer == Notifications.mc.player) {
                this.flag = false;
            }
            if (closestPlayer != null && this.flag) {
                String facing = enderPearl.getHorizontalFacing().toString();
                if (facing.equals("west")) {
                    facing = "east";
                } else if (facing.equals("east")) {
                    facing = "west";
                }
                Command.sendSilentMessage(Phobos.friendManager.isFriend(closestPlayer.getName()) ? ChatFormatting.AQUA + closestPlayer.getName() + ChatFormatting.DARK_GRAY + " has just thrown a pearl heading " + facing + "!" : ChatFormatting.RED + closestPlayer.getName() + ChatFormatting.DARK_GRAY + " has just thrown a pearl heading " + facing + "!");
                this.flag = false;
            }
        }
        if (this.weakness.getValue().booleanValue()) {
            if (!this.weak.passedMs(this.checkrate.getValue().intValue())) {
                return;
            }
            if (Notifications.mc.player.isPotionActive(Objects.requireNonNull(Potion.getPotionFromResourceLocation((String)"weakness"))) && !this.last) {
                Command.sendMessage(ChatFormatting.RED + "You have been weaknessed.");
                this.last = true;
            }
            if (!Notifications.mc.player.isPotionActive(Objects.requireNonNull(Potion.getPotionFromResourceLocation((String)"weakness"))) && this.last) {
                if (this.strongness.getValue().booleanValue()) {
                    Command.sendMessage(ChatFormatting.GREEN + "You no longer have weakness.");
                }
                this.last = false;
            }
            this.weak.reset();
        }
        if (this.strength.getValue().booleanValue()) {
            for (EntityPlayer entityPlayer : Notifications.mc.world.playerEntities) {
                if (!this.strgh.passedMs(this.strcheckrate.getValue().intValue())) {
                    return;
                }
                if (entityPlayer.equals((Object)Notifications.mc.player)) continue;
                if (entityPlayer.isPotionActive(MobEffects.STRENGTH) && !this.str.contains(entityPlayer)) {
                    Command.sendMessage(entityPlayer.getDisplayNameString() + ChatFormatting.RED + " has strength");
                    this.str.add(entityPlayer);
                }
                if (!this.str.contains(entityPlayer) || entityPlayer.isPotionActive(MobEffects.STRENGTH)) continue;
                Command.sendMessage(entityPlayer.getDisplayNameString() + ChatFormatting.GREEN + " no longer has strength");
                this.str.remove(entityPlayer);
            }
        }
    }

    @Override
    public void onTick() {
        if (Notifications.fullNullCheck()) {
            return;
        }
        if (this.burrow.getValue().booleanValue()) {
            for (EntityPlayer entityPlayer2 : Notifications.mc.world.playerEntities.stream().filter(entityPlayer -> entityPlayer != Notifications.mc.player).collect(Collectors.toList())) {
                if (this.burrowedPlayers.contains(entityPlayer2) || !this.isInBurrow(entityPlayer2)) continue;
                Command.sendMessage(ChatFormatting.RED + entityPlayer2.getDisplayNameString() + ChatFormatting.GREEN + " has burrowed.");
                this.burrowedPlayers.add(entityPlayer2);
            }
        }
        if (this.thirtytwokay.getValue().booleanValue()) {
            int once = 0;
            for (EntityPlayer player : Notifications.mc.world.playerEntities) {
                if (player.equals((Object)Notifications.mc.player)) continue;
                if (this.is32k(player.getHeldItem(EnumHand.MAIN_HAND)) && !this.sword.contains(player)) {
                    Command.sendMessage(ChatFormatting.RED + player.getDisplayNameString() + " is holding a 32k");
                    this.sword.add(player);
                }
                if (this.is32k(player.getHeldItem(EnumHand.MAIN_HAND))) {
                    if (once > 0) {
                        return;
                    }
                    ++once;
                }
                if (!this.sword.contains(player) || this.is32k(player.getHeldItem(EnumHand.MAIN_HAND))) continue;
                Command.sendMessage(ChatFormatting.GREEN + player.getDisplayNameString() + " is no longer holding a 32k");
                this.sword.remove(player);
            }
        }
    }

    private boolean isInBurrow(EntityPlayer entityPlayer) {
        BlockPos playerPos = new BlockPos(this.getMiddlePosition(entityPlayer.posX), entityPlayer.posY, this.getMiddlePosition(entityPlayer.posZ));
        return Notifications.mc.world.getBlockState(playerPos).getBlock() == Blocks.OBSIDIAN || Notifications.mc.world.getBlockState(playerPos).getBlock() == Blocks.ENDER_CHEST || Notifications.mc.world.getBlockState(playerPos).getBlock() == Blocks.ANVIL;
    }

    private double getMiddlePosition(double positionIn) {
        double positionFinal = Math.round(positionIn);
        if ((double)Math.round(positionIn) > positionIn) {
            positionFinal -= 0.5;
        } else if ((double)Math.round(positionIn) <= positionIn) {
            positionFinal += 0.5;
        }
        return positionFinal;
    }

    public void loadFile() {
        List<String> fileInput = FileManager.readTextFileAllLines(fileName);
        Iterator<String> i = fileInput.iterator();
        modules.clear();
        while (i.hasNext()) {
            String s = i.next();
            if (s.replaceAll("\\s", "").isEmpty()) continue;
            modules.add(s);
        }
    }

    @SubscribeEvent
    public void onToggleModule(ClientEvent event) {
        int moduleNumber;
        Module module;
        if (!this.moduleMessage.getValue().booleanValue()) {
            return;
        }
        if (!(event.getStage() != 0 || (module = (Module)event.getFeature()).equals(this) || !modules.contains(module.getDisplayName()) && this.list.getValue().booleanValue())) {
            moduleNumber = 0;
            char[] cArray = module.getDisplayName().toCharArray();
            int n = cArray.length;
            for (int i = 0; i < n; ++i) {
                char character = cArray[i];
                moduleNumber += character;
                moduleNumber *= 10;
            }
            if (this.watermark.getValue().booleanValue()) {
                TextComponentString textComponentString = new TextComponentString(Phobos.commandManager.getClientMessage() + " \u00a7r\u00a7c" + module.getDisplayName() + " disabled.");
                Notifications.mc.ingameGUI.getChatGUI().printChatMessageWithOptionalDeletion((ITextComponent)textComponentString, moduleNumber);
                if (this.bold.getValue().booleanValue()) {
                    TextComponentString textComponentString2 = new TextComponentString(Phobos.commandManager.getClientMessage() + " " + ChatFormatting.BOLD + module.getDisplayName() + ChatFormatting.RED + " disabled.");
                    Notifications.mc.ingameGUI.getChatGUI().printChatMessageWithOptionalDeletion((ITextComponent)textComponentString2, moduleNumber);
                }
            } else {
                TextComponentString textComponentString = new TextComponentString("\u00a7c" + module.getDisplayName() + " disabled.");
                Notifications.mc.ingameGUI.getChatGUI().printChatMessageWithOptionalDeletion((ITextComponent)textComponentString, moduleNumber);
                if (this.bold.getValue().booleanValue()) {
                    TextComponentString textComponentString2 = new TextComponentString(ChatFormatting.BOLD + module.getDisplayName() + ChatFormatting.RED + " disabled.");
                    Notifications.mc.ingameGUI.getChatGUI().printChatMessageWithOptionalDeletion((ITextComponent)textComponentString2, moduleNumber);
                }
            }
        }
        if (event.getStage() == 1 && (modules.contains((module = (Module)event.getFeature()).getDisplayName()) || !this.list.getValue().booleanValue())) {
            moduleNumber = 0;
            for (char character : module.getDisplayName().toCharArray()) {
                moduleNumber += character;
                moduleNumber *= 10;
            }
            if (this.watermark.getValue().booleanValue()) {
                TextComponentString textComponentString = new TextComponentString(Phobos.commandManager.getClientMessage() + " \u00a7r\u00a7a" + module.getDisplayName() + " enabled.");
                Notifications.mc.ingameGUI.getChatGUI().printChatMessageWithOptionalDeletion((ITextComponent)textComponentString, moduleNumber);
                if (this.bold.getValue().booleanValue()) {
                    TextComponentString textComponentString2 = new TextComponentString(Phobos.commandManager.getClientMessage() + " " + ChatFormatting.BOLD + module.getDisplayName() + ChatFormatting.GREEN + " enabled.");
                    Notifications.mc.ingameGUI.getChatGUI().printChatMessageWithOptionalDeletion((ITextComponent)textComponentString2, moduleNumber);
                }
            } else {
                TextComponentString textComponentString = new TextComponentString("\u00a7a" + module.getDisplayName() + " enabled.");
                Notifications.mc.ingameGUI.getChatGUI().printChatMessageWithOptionalDeletion((ITextComponent)textComponentString, moduleNumber);
                if (this.bold.getValue().booleanValue()) {
                    TextComponentString textComponentString2 = new TextComponentString(ChatFormatting.BOLD + module.getDisplayName() + ChatFormatting.GREEN + " enabled.");
                    Notifications.mc.ingameGUI.getChatGUI().printChatMessageWithOptionalDeletion((ITextComponent)textComponentString2, moduleNumber);
                }
            }
        }
    }
}

