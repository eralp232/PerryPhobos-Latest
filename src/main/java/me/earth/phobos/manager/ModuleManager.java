
/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  net.minecraftforge.common.MinecraftForge
 *  net.minecraftforge.fml.common.eventhandler.EventBus
 *  org.lwjgl.input.Keyboard
 */
package me.earth.phobos.manager;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import me.earth.phobos.event.events.Render2DEvent;
import me.earth.phobos.event.events.Render3DEvent;
import me.earth.phobos.features.Feature;
import me.earth.phobos.features.gui.PhobosGui;
import me.earth.phobos.features.modules.Module;
import me.earth.phobos.features.modules.client.Capes;
import me.earth.phobos.features.modules.client.ClickGui;
import me.earth.phobos.features.modules.client.Colors;
import me.earth.phobos.features.modules.client.Components;
import me.earth.phobos.features.modules.client.Cosmetics;
import me.earth.phobos.features.modules.client.CustomFont;
import me.earth.phobos.features.modules.client.HUD;
import me.earth.phobos.features.modules.client.Management;
import me.earth.phobos.features.modules.client.Media;
import me.earth.phobos.features.modules.client.Notifications;
import me.earth.phobos.features.modules.client.PhobosChat;
import me.earth.phobos.features.modules.client.PhysicsCapes;
import me.earth.phobos.features.modules.client.PingBypass;
import me.earth.phobos.features.modules.client.Screens;
import me.earth.phobos.features.modules.client.ShoulderEntity;
import me.earth.phobos.features.modules.client.StreamerMode;
import me.earth.phobos.features.modules.combat.AntiCrystal;
import me.earth.phobos.features.modules.combat.AntiTrap;
import me.earth.phobos.features.modules.combat.AnvilAura;
import me.earth.phobos.features.modules.combat.ArmorMessage;
import me.earth.phobos.features.modules.combat.Auto32k;
import me.earth.phobos.features.modules.combat.AutoArmor;
import me.earth.phobos.features.modules.combat.AutoCity;
import me.earth.phobos.features.modules.combat.AutoCrystal;
import me.earth.phobos.features.modules.combat.AutoMinecart;
import me.earth.phobos.features.modules.combat.AutoTrap;
import me.earth.phobos.features.modules.combat.BedBomb;
import me.earth.phobos.features.modules.combat.BowAim;
import me.earth.phobos.features.modules.combat.BowSpam;
import me.earth.phobos.features.modules.combat.Criticals;
import me.earth.phobos.features.modules.combat.CrystalCrash;
import me.earth.phobos.features.modules.combat.Flatten;
import me.earth.phobos.features.modules.combat.GodModule;
import me.earth.phobos.features.modules.combat.HoleFiller;
import me.earth.phobos.features.modules.combat.Killaura;
import me.earth.phobos.features.modules.combat.Offhand;
import me.earth.phobos.features.modules.combat.OffhandRewrite;
import me.earth.phobos.features.modules.combat.Quiver;
import me.earth.phobos.features.modules.combat.SelfAnvil;
import me.earth.phobos.features.modules.combat.SelfCrystal;
import me.earth.phobos.features.modules.combat.Selftrap;
import me.earth.phobos.features.modules.combat.Surround;
import me.earth.phobos.features.modules.combat.Webaura;
import me.earth.phobos.features.modules.misc.Announcer;
import me.earth.phobos.features.modules.misc.AntiAFK;
import me.earth.phobos.features.modules.misc.AntiPackets;
import me.earth.phobos.features.modules.misc.AntiVanish;
import me.earth.phobos.features.modules.misc.AutoGG;
import me.earth.phobos.features.modules.misc.AutoLog;
import me.earth.phobos.features.modules.misc.AutoReconnect;
import me.earth.phobos.features.modules.misc.AutoRespawn;
import me.earth.phobos.features.modules.misc.BetterPortals;
import me.earth.phobos.features.modules.misc.BuildHeight;
import me.earth.phobos.features.modules.misc.Bypass;
import me.earth.phobos.features.modules.misc.ChatModifier;
import me.earth.phobos.features.modules.misc.Companion;
import me.earth.phobos.features.modules.misc.Exploits;
import me.earth.phobos.features.modules.misc.ExtraTab;
import me.earth.phobos.features.modules.misc.KitDelete;
import me.earth.phobos.features.modules.misc.Logger;
import me.earth.phobos.features.modules.misc.MCF;
import me.earth.phobos.features.modules.misc.MobOwner;
import me.earth.phobos.features.modules.misc.NoHandShake;
import me.earth.phobos.features.modules.misc.NoRotate;
import me.earth.phobos.features.modules.misc.NoSoundLag;
import me.earth.phobos.features.modules.misc.NoteBot;
import me.earth.phobos.features.modules.misc.Nuker;
import me.earth.phobos.features.modules.misc.PackReload;
import me.earth.phobos.features.modules.misc.PingSpoof;
import me.earth.phobos.features.modules.misc.RPC;
import me.earth.phobos.features.modules.misc.Spammer;
import me.earth.phobos.features.modules.misc.ToolTips;
import me.earth.phobos.features.modules.misc.Tracker;
import me.earth.phobos.features.modules.misc.Translator;
import me.earth.phobos.features.modules.movement.AirJump;
import me.earth.phobos.features.modules.movement.Anchor;
import me.earth.phobos.features.modules.movement.AntiLevitate;
import me.earth.phobos.features.modules.movement.AutoWalk;
import me.earth.phobos.features.modules.movement.BlockLag;
import me.earth.phobos.features.modules.movement.BoatFly;
import me.earth.phobos.features.modules.movement.ElytraFlight;
import me.earth.phobos.features.modules.movement.EntityControl;
import me.earth.phobos.features.modules.movement.FastSwim;
import me.earth.phobos.features.modules.movement.Flight;
import me.earth.phobos.features.modules.movement.HoleTP;
import me.earth.phobos.features.modules.movement.IceSpeed;
import me.earth.phobos.features.modules.movement.LongJump;
import me.earth.phobos.features.modules.movement.NoFall;
import me.earth.phobos.features.modules.movement.NoSlowDown;
import me.earth.phobos.features.modules.movement.PacketFlyNew;
import me.earth.phobos.features.modules.movement.Packetfly;
import me.earth.phobos.features.modules.movement.Phase;
import me.earth.phobos.features.modules.movement.ReverseStep;
import me.earth.phobos.features.modules.movement.SafeWalk;
import me.earth.phobos.features.modules.movement.Speed;
import me.earth.phobos.features.modules.movement.Sprint;
import me.earth.phobos.features.modules.movement.StairSpeed;
import me.earth.phobos.features.modules.movement.Static;
import me.earth.phobos.features.modules.movement.Step;
import me.earth.phobos.features.modules.movement.StepOld;
import me.earth.phobos.features.modules.movement.Strafe;
import me.earth.phobos.features.modules.movement.TPSpeed;
import me.earth.phobos.features.modules.movement.VanillaSpeed;
import me.earth.phobos.features.modules.movement.Velocity;
import me.earth.phobos.features.modules.player.AirPlace;
import me.earth.phobos.features.modules.player.AntiDDoS;
import me.earth.phobos.features.modules.player.AntiHunger;
import me.earth.phobos.features.modules.player.AutoBuilder;
import me.earth.phobos.features.modules.player.Blink;
import me.earth.phobos.features.modules.player.BlockTweaks;
import me.earth.phobos.features.modules.player.EchestBP;
import me.earth.phobos.features.modules.player.FakePlayer;
import me.earth.phobos.features.modules.player.FastPlace;
import me.earth.phobos.features.modules.player.Freecam;
import me.earth.phobos.features.modules.player.Godmode;
import me.earth.phobos.features.modules.player.Jesus;
import me.earth.phobos.features.modules.player.LiquidInteract;
import me.earth.phobos.features.modules.player.MCP;
import me.earth.phobos.features.modules.player.MultiTask;
import me.earth.phobos.features.modules.player.NoEntityTrace;
import me.earth.phobos.features.modules.player.PacketMend;
import me.earth.phobos.features.modules.player.PluginsGrabber;
import me.earth.phobos.features.modules.player.Reach;
import me.earth.phobos.features.modules.player.Replenish;
import me.earth.phobos.features.modules.player.Rubberband;
import me.earth.phobos.features.modules.player.Scaffold;
import me.earth.phobos.features.modules.player.SilentXP;
import me.earth.phobos.features.modules.player.Speedmine;
import me.earth.phobos.features.modules.player.Timer;
import me.earth.phobos.features.modules.player.TpsSync;
import me.earth.phobos.features.modules.player.TrueDurability;
import me.earth.phobos.features.modules.player.XCarry;
import me.earth.phobos.features.modules.player.Yaw;
import me.earth.phobos.features.modules.render.Animations;
import me.earth.phobos.features.modules.render.ArrowESP;
import me.earth.phobos.features.modules.render.Aspect;
import me.earth.phobos.features.modules.render.BlockHighlight;
import me.earth.phobos.features.modules.render.CameraClip;
import me.earth.phobos.features.modules.render.Chams;
import me.earth.phobos.features.modules.render.ChorusESP;
import me.earth.phobos.features.modules.render.CrystalModifier;
import me.earth.phobos.features.modules.render.ESP;
import me.earth.phobos.features.modules.render.Fullbright;
import me.earth.phobos.features.modules.render.GlintModify;
import me.earth.phobos.features.modules.render.HandColor;
import me.earth.phobos.features.modules.render.HoleESP;
import me.earth.phobos.features.modules.render.ItemPhysics;
import me.earth.phobos.features.modules.render.LogoutSpots;
import me.earth.phobos.features.modules.render.Nametags;
import me.earth.phobos.features.modules.render.NoRender;
import me.earth.phobos.features.modules.render.PopChams;
import me.earth.phobos.features.modules.render.PortalESP;
import me.earth.phobos.features.modules.render.Ranges;
import me.earth.phobos.features.modules.render.Shaders;
import me.earth.phobos.features.modules.render.Skeleton;
import me.earth.phobos.features.modules.render.SkyColor;
import me.earth.phobos.features.modules.render.SmallShield;
import me.earth.phobos.features.modules.render.StorageESP;
import me.earth.phobos.features.modules.render.TestNametags;
import me.earth.phobos.features.modules.render.Tracers;
import me.earth.phobos.features.modules.render.Trails;
import me.earth.phobos.features.modules.render.Trajectories;
import me.earth.phobos.features.modules.render.ViewModel;
import me.earth.phobos.features.modules.render.VoidESP;
import me.earth.phobos.features.modules.render.XRay;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.EventBus;
import org.lwjgl.input.Keyboard;

public class ModuleManager
extends Feature {
    public ArrayList<Module> modules = new ArrayList();
    public List<Module> sortedModules = new ArrayList<Module>();
    public List<Module> alphabeticallySortedModules = new ArrayList<Module>();
    public Map<Module, Color> moduleColorMap = new HashMap<Module, Color>();

    public void init() {
        this.modules.add(new Offhand());
        this.modules.add(new Surround());
        this.modules.add(new AutoTrap());
        this.modules.add(new AutoCrystal());
        this.modules.add(new Criticals());
        this.modules.add(new BowSpam());
        this.modules.add(new Killaura());
        this.modules.add(new HoleFiller());
        this.modules.add(new Selftrap());
        this.modules.add(new Webaura());
        this.modules.add(new AutoArmor());
        this.modules.add(new AntiTrap());
        this.modules.add(new BedBomb());
        this.modules.add(new ArmorMessage());
        this.modules.add(new CrystalCrash());
        this.modules.add(new Auto32k());
        this.modules.add(new AntiCrystal());
        this.modules.add(new AnvilAura());
        this.modules.add(new GodModule());
        this.modules.add(new NoteBot());
        this.modules.add(new ChatModifier());
        this.modules.add(new BetterPortals());
        this.modules.add(new BuildHeight());
        this.modules.add(new NoHandShake());
        this.modules.add(new AutoRespawn());
        this.modules.add(new NoRotate());
        this.modules.add(new MCF());
        this.modules.add(new PingSpoof());
        this.modules.add(new NoSoundLag());
        this.modules.add(new AutoLog());
        this.modules.add(new KitDelete());
        this.modules.add(new Exploits());
        this.modules.add(new Spammer());
        this.modules.add(new AntiVanish());
        this.modules.add(new ExtraTab());
        this.modules.add(new MobOwner());
        this.modules.add(new Nuker());
        this.modules.add(new AutoReconnect());
        this.modules.add(new AntiAFK());
        this.modules.add(new Tracker());
        this.modules.add(new AntiPackets());
        this.modules.add(new Logger());
        this.modules.add(new RPC());
        this.modules.add(new AutoGG());
        this.modules.add(new Godmode());
        this.modules.add(new Companion());
        this.modules.add(new EntityControl());
        this.modules.add(new ReverseStep());
        this.modules.add(new Bypass());
        this.modules.add(new Strafe());
        this.modules.add(new Velocity());
        this.modules.add(new Speed());
        this.modules.add(new Step());
        this.modules.add(new StepOld());
        this.modules.add(new Sprint());
        this.modules.add(new AntiLevitate());
        this.modules.add(new Phase());
        this.modules.add(new Static());
        this.modules.add(new TPSpeed());
        this.modules.add(new Flight());
        this.modules.add(new ElytraFlight());
        this.modules.add(new NoSlowDown());
        this.modules.add(new HoleTP());
        this.modules.add(new NoFall());
        this.modules.add(new IceSpeed());
        this.modules.add(new AutoWalk());
        this.modules.add(new Packetfly());
        this.modules.add(new LongJump());
        this.modules.add(new BlockLag());
        this.modules.add(new FastSwim());
        this.modules.add(new StairSpeed());
        this.modules.add(new BoatFly());
        this.modules.add(new VanillaSpeed());
        this.modules.add(new Reach());
        this.modules.add(new LiquidInteract());
        this.modules.add(new FakePlayer());
        this.modules.add(new Timer());
        this.modules.add(new FastPlace());
        this.modules.add(new Freecam());
        this.modules.add(new Speedmine());
        this.modules.add(new SafeWalk());
        this.modules.add(new Blink());
        this.modules.add(new MultiTask());
        this.modules.add(new BlockTweaks());
        this.modules.add(new XCarry());
        this.modules.add(new Replenish());
        this.modules.add(new AntiHunger());
        this.modules.add(new Jesus());
        this.modules.add(new Scaffold());
        this.modules.add(new EchestBP());
        this.modules.add(new TpsSync());
        this.modules.add(new MCP());
        this.modules.add(new TrueDurability());
        this.modules.add(new Yaw());
        this.modules.add(new AntiDDoS());
        this.modules.add(new StorageESP());
        this.modules.add(new NoRender());
        this.modules.add(new SmallShield());
        this.modules.add(new Fullbright());
        this.modules.add(new CameraClip());
        this.modules.add(new Chams());
        this.modules.add(new Skeleton());
        this.modules.add(new ESP());
        this.modules.add(new HoleESP());
        this.modules.add(new BlockHighlight());
        this.modules.add(new Trajectories());
        this.modules.add(new Tracers());
        this.modules.add(new LogoutSpots());
        this.modules.add(new XRay());
        this.modules.add(new PortalESP());
        this.modules.add(new Ranges());
        this.modules.add(new ArrowESP());
        this.modules.add(new HandColor());
        this.modules.add(new VoidESP());
        this.modules.add(new Cosmetics());
        this.modules.add(new CrystalModifier());
        this.modules.add(new Notifications());
        this.modules.add(new HUD());
        this.modules.add(new ToolTips());
        this.modules.add(new CustomFont());
        this.modules.add(new ClickGui());
        this.modules.add(new Management());
        this.modules.add(new Components());
        this.modules.add(new StreamerMode());
        this.modules.add(new Capes());
        this.modules.add(new Colors());
        this.modules.add(new PingBypass());
        this.modules.add(new Screens());
        this.modules.add(new Media());
        this.modules.add(new PhobosChat());
        this.modules.add(new SelfCrystal());
        this.modules.add(new PhysicsCapes());
        this.modules.add(new ShoulderEntity());
        this.modules.add(new Announcer());
        this.modules.add(new Translator());
        this.modules.add(new me.earth.phobos.features.modules.render.Cosmetics());
        this.modules.add(new Trails());
        this.modules.add(new Nametags());
        this.modules.add(new TestNametags());
        this.modules.add(new Aspect());
        this.modules.add(new Anchor());
        this.modules.add(new Shaders());
        this.modules.add(new Animations());
        this.modules.add(new ViewModel());
        this.modules.add(new GlintModify());
        this.modules.add(new AutoCity());
        this.modules.add(new AirJump());
        this.modules.add(new AirPlace());
        this.modules.add(new SelfAnvil());
        this.modules.add(new SilentXP());
        this.modules.add(new SkyColor());
        this.modules.add(new Quiver());
        this.modules.add(new OffhandRewrite());
        this.modules.add(new AutoMinecart());
        this.modules.add(new Rubberband());
        this.modules.add(new PackReload());
        this.modules.add(new NoEntityTrace());
        this.modules.add(new PacketMend());
        this.modules.add(new Flatten());
        this.modules.add(new BowAim());
        this.modules.add(new ItemPhysics());
        this.modules.add(new ChorusESP());
        this.modules.add(new AutoBuilder());
        this.modules.add(new PopChams());
        this.modules.add(new PacketFlyNew());
        this.modules.add(new PluginsGrabber());
        this.moduleColorMap.put(this.getModuleByClass(AntiTrap.class), new Color(128, 53, 69));
        this.moduleColorMap.put(this.getModuleByClass(AnvilAura.class), new Color(90, 227, 96));
        this.moduleColorMap.put(this.getModuleByClass(ArmorMessage.class), new Color(255, 51, 51));
        this.moduleColorMap.put(this.getModuleByClass(Auto32k.class), new Color(185, 212, 144));
        this.moduleColorMap.put(this.getModuleByClass(AutoArmor.class), new Color(74, 227, 206));
        this.moduleColorMap.put(this.getModuleByClass(AutoCrystal.class), new Color(255, 15, 43));
        this.moduleColorMap.put(this.getModuleByClass(AutoTrap.class), new Color(193, 49, 244));
        this.moduleColorMap.put(this.getModuleByClass(BedBomb.class), new Color(185, 80, 195));
        this.moduleColorMap.put(this.getModuleByClass(BowSpam.class), new Color(204, 191, 153));
        this.moduleColorMap.put(this.getModuleByClass(CrystalCrash.class), new Color(208, 66, 9));
        this.moduleColorMap.put(this.getModuleByClass(Criticals.class), new Color(204, 151, 184));
        this.moduleColorMap.put(this.getModuleByClass(HoleFiller.class), new Color(166, 55, 110));
        this.moduleColorMap.put(this.getModuleByClass(Killaura.class), new Color(255, 37, 0));
        this.moduleColorMap.put(this.getModuleByClass(Offhand.class), new Color(185, 212, 144));
        this.moduleColorMap.put(this.getModuleByClass(Selftrap.class), new Color(22, 127, 145));
        this.moduleColorMap.put(this.getModuleByClass(Surround.class), new Color(100, 0, 150));
        this.moduleColorMap.put(this.getModuleByClass(Webaura.class), new Color(11, 161, 121));
        this.moduleColorMap.put(this.getModuleByClass(AntiCrystal.class), new Color(255, 161, 121));
        this.moduleColorMap.put(this.getModuleByClass(AntiPackets.class), new Color(155, 186, 115));
        this.moduleColorMap.put(this.getModuleByClass(AntiVanish.class), new Color(25, 209, 135));
        this.moduleColorMap.put(this.getModuleByClass(AutoGG.class), new Color(240, 49, 110));
        this.moduleColorMap.put(this.getModuleByClass(AutoLog.class), new Color(176, 176, 176));
        this.moduleColorMap.put(this.getModuleByClass(AutoReconnect.class), new Color(17, 85, 153));
        this.moduleColorMap.put(this.getModuleByClass(BetterPortals.class), new Color(71, 214, 187));
        this.moduleColorMap.put(this.getModuleByClass(BuildHeight.class), new Color(64, 136, 199));
        this.moduleColorMap.put(this.getModuleByClass(Bypass.class), new Color(194, 214, 81));
        this.moduleColorMap.put(this.getModuleByClass(Companion.class), new Color(140, 252, 146));
        this.moduleColorMap.put(this.getModuleByClass(ChatModifier.class), new Color(255, 59, 216));
        this.moduleColorMap.put(this.getModuleByClass(Exploits.class), new Color(255, 0, 0));
        this.moduleColorMap.put(this.getModuleByClass(ExtraTab.class), new Color(161, 113, 173));
        this.moduleColorMap.put(this.getModuleByClass(Godmode.class), new Color(1, 35, 95));
        this.moduleColorMap.put(this.getModuleByClass(KitDelete.class), new Color(229, 194, 255));
        this.moduleColorMap.put(this.getModuleByClass(Logger.class), new Color(186, 0, 109));
        this.moduleColorMap.put(this.getModuleByClass(MCF.class), new Color(17, 85, 255));
        this.moduleColorMap.put(this.getModuleByClass(MobOwner.class), new Color(255, 254, 204));
        this.moduleColorMap.put(this.getModuleByClass(AntiAFK.class), new Color(80, 5, 98));
        this.moduleColorMap.put(this.getModuleByClass(NoHandShake.class), new Color(173, 232, 139));
        this.moduleColorMap.put(this.getModuleByClass(NoRotate.class), new Color(69, 81, 223));
        this.moduleColorMap.put(this.getModuleByClass(NoSoundLag.class), new Color(255, 56, 0));
        this.moduleColorMap.put(this.getModuleByClass(Nuker.class), new Color(152, 169, 17));
        this.moduleColorMap.put(this.getModuleByClass(PingSpoof.class), new Color(23, 214, 187));
        this.moduleColorMap.put(this.getModuleByClass(RPC.class), new Color(0, 64, 255));
        this.moduleColorMap.put(this.getModuleByClass(Spammer.class), new Color(140, 87, 166));
        this.moduleColorMap.put(this.getModuleByClass(ToolTips.class), new Color(209, 125, 156));
        this.moduleColorMap.put(this.getModuleByClass(Translator.class), new Color(74, 82, 15));
        this.moduleColorMap.put(this.getModuleByClass(Tracker.class), new Color(0, 255, 225));
        this.moduleColorMap.put(this.getModuleByClass(ArrowESP.class), new Color(193, 219, 20));
        this.moduleColorMap.put(this.getModuleByClass(BlockHighlight.class), new Color(103, 182, 224));
        this.moduleColorMap.put(this.getModuleByClass(CameraClip.class), new Color(247, 169, 107));
        this.moduleColorMap.put(this.getModuleByClass(Chams.class), new Color(34, 152, 34));
        this.moduleColorMap.put(this.getModuleByClass(ESP.class), new Color(255, 27, 155));
        this.moduleColorMap.put(this.getModuleByClass(Fullbright.class), new Color(255, 164, 107));
        this.moduleColorMap.put(this.getModuleByClass(HandColor.class), new Color(96, 138, 92));
        this.moduleColorMap.put(this.getModuleByClass(HoleESP.class), new Color(95, 83, 130));
        this.moduleColorMap.put(this.getModuleByClass(LogoutSpots.class), new Color(2, 135, 134));
        this.moduleColorMap.put(this.getModuleByClass(Nametags.class), new Color(98, 82, 223));
        this.moduleColorMap.put(this.getModuleByClass(NoRender.class), new Color(255, 164, 107));
        this.moduleColorMap.put(this.getModuleByClass(PortalESP.class), new Color(26, 242, 62));
        this.moduleColorMap.put(this.getModuleByClass(Ranges.class), new Color(144, 212, 196));
        this.moduleColorMap.put(this.getModuleByClass(Skeleton.class), new Color(219, 219, 219));
        this.moduleColorMap.put(this.getModuleByClass(SmallShield.class), new Color(145, 223, 187));
        this.moduleColorMap.put(this.getModuleByClass(StorageESP.class), new Color(97, 81, 223));
        this.moduleColorMap.put(this.getModuleByClass(Tracers.class), new Color(255, 107, 107));
        this.moduleColorMap.put(this.getModuleByClass(Trajectories.class), new Color(98, 18, 223));
        this.moduleColorMap.put(this.getModuleByClass(VoidESP.class), new Color(68, 178, 142));
        this.moduleColorMap.put(this.getModuleByClass(XRay.class), new Color(217, 118, 37));
        this.moduleColorMap.put(this.getModuleByClass(AntiLevitate.class), new Color(206, 255, 255));
        this.moduleColorMap.put(this.getModuleByClass(AutoWalk.class), new Color(153, 153, 170));
        this.moduleColorMap.put(this.getModuleByClass(ElytraFlight.class), new Color(55, 161, 201));
        this.moduleColorMap.put(this.getModuleByClass(Flight.class), new Color(186, 164, 178));
        this.moduleColorMap.put(this.getModuleByClass(HoleTP.class), new Color(68, 178, 142));
        this.moduleColorMap.put(this.getModuleByClass(IceSpeed.class), new Color(33, 193, 247));
        this.moduleColorMap.put(this.getModuleByClass(LongJump.class), new Color(228, 27, 213));
        this.moduleColorMap.put(this.getModuleByClass(NoFall.class), new Color(61, 204, 78));
        this.moduleColorMap.put(this.getModuleByClass(NoSlowDown.class), new Color(61, 204, 78));
        this.moduleColorMap.put(this.getModuleByClass(Packetfly.class), new Color(238, 59, 27));
        this.moduleColorMap.put(this.getModuleByClass(Phase.class), new Color(186, 144, 212));
        this.moduleColorMap.put(this.getModuleByClass(SafeWalk.class), new Color(182, 186, 164));
        this.moduleColorMap.put(this.getModuleByClass(Speed.class), new Color(55, 161, 196));
        this.moduleColorMap.put(this.getModuleByClass(Sprint.class), new Color(148, 184, 142));
        this.moduleColorMap.put(this.getModuleByClass(Static.class), new Color(86, 53, 98));
        this.moduleColorMap.put(this.getModuleByClass(Step.class), new Color(144, 212, 203));
        this.moduleColorMap.put(this.getModuleByClass(StepOld.class), new Color(144, 212, 203));
        this.moduleColorMap.put(this.getModuleByClass(Strafe.class), new Color(0, 204, 255));
        this.moduleColorMap.put(this.getModuleByClass(TPSpeed.class), new Color(20, 177, 142));
        this.moduleColorMap.put(this.getModuleByClass(Velocity.class), new Color(115, 134, 140));
        this.moduleColorMap.put(this.getModuleByClass(ReverseStep.class), new Color(1, 134, 140));
        this.moduleColorMap.put(this.getModuleByClass(AntiDDoS.class), new Color(67, 191, 181));
        this.moduleColorMap.put(this.getModuleByClass(Blink.class), new Color(144, 184, 141));
        this.moduleColorMap.put(this.getModuleByClass(BlockTweaks.class), new Color(89, 223, 235));
        this.moduleColorMap.put(this.getModuleByClass(EchestBP.class), new Color(255, 243, 30));
        this.moduleColorMap.put(this.getModuleByClass(FakePlayer.class), new Color(37, 192, 170));
        this.moduleColorMap.put(this.getModuleByClass(FastPlace.class), new Color(217, 118, 37));
        this.moduleColorMap.put(this.getModuleByClass(Freecam.class), new Color(206, 232, 128));
        this.moduleColorMap.put(this.getModuleByClass(Jesus.class), new Color(136, 221, 235));
        this.moduleColorMap.put(this.getModuleByClass(LiquidInteract.class), new Color(85, 223, 235));
        this.moduleColorMap.put(this.getModuleByClass(MCP.class), new Color(153, 68, 170));
        this.moduleColorMap.put(this.getModuleByClass(MultiTask.class), new Color(17, 223, 235));
        this.moduleColorMap.put(this.getModuleByClass(AntiHunger.class), new Color(86, 53, 98));
        this.moduleColorMap.put(this.getModuleByClass(Reach.class), new Color(9, 223, 187));
        this.moduleColorMap.put(this.getModuleByClass(Replenish.class), new Color(153, 223, 235));
        this.moduleColorMap.put(this.getModuleByClass(Scaffold.class), new Color(152, 166, 113));
        this.moduleColorMap.put(this.getModuleByClass(Speedmine.class), new Color(152, 166, 113));
        this.moduleColorMap.put(this.getModuleByClass(Timer.class), new Color(255, 133, 18));
        this.moduleColorMap.put(this.getModuleByClass(TpsSync.class), new Color(93, 144, 153));
        this.moduleColorMap.put(this.getModuleByClass(TrueDurability.class), new Color(254, 161, 51));
        this.moduleColorMap.put(this.getModuleByClass(XCarry.class), new Color(254, 161, 51));
        this.moduleColorMap.put(this.getModuleByClass(Yaw.class), new Color(115, 39, 141));
        this.moduleColorMap.put(this.getModuleByClass(Capes.class), new Color(26, 135, 104));
        this.moduleColorMap.put(this.getModuleByClass(ClickGui.class), new Color(26, 81, 135));
        this.moduleColorMap.put(this.getModuleByClass(Colors.class), new Color(135, 133, 26));
        this.moduleColorMap.put(this.getModuleByClass(Components.class), new Color(135, 26, 26));
        this.moduleColorMap.put(this.getModuleByClass(CustomFont.class), new Color(135, 26, 88));
        this.moduleColorMap.put(this.getModuleByClass(HUD.class), new Color(110, 26, 135));
        this.moduleColorMap.put(this.getModuleByClass(Management.class), new Color(26, 90, 135));
        this.moduleColorMap.put(this.getModuleByClass(Notifications.class), new Color(170, 153, 255));
        this.moduleColorMap.put(this.getModuleByClass(PingBypass.class), new Color(60, 110, 175));
        this.moduleColorMap.put(this.getModuleByClass(Media.class), new Color(138, 45, 13));
        this.moduleColorMap.put(this.getModuleByClass(Screens.class), new Color(165, 89, 101));
        this.moduleColorMap.put(this.getModuleByClass(StreamerMode.class), new Color(0, 0, 0));
        for (Module module : this.modules) {
            module.animation.start();
        }
    }

    public Module getModuleByName(String name) {
        for (Module module : this.modules) {
            if (!module.getName().equalsIgnoreCase(name)) continue;
            return module;
        }
        return null;
    }

    public <T extends Module> T getModuleByClass(Class<T> clazz) {
        for (Module module : this.modules) {
            if (!clazz.isInstance(module)) continue;
            return (T)module;
        }
        return null;
    }

    public void enableModule(Class<Module> clazz) {
        Module module = this.getModuleByClass(clazz);
        if (module != null) {
            module.enable();
        }
    }

    public void disableModule(Class<Module> clazz) {
        Module module = this.getModuleByClass(clazz);
        if (module != null) {
            module.disable();
        }
    }

    public void enableModule(String name) {
        Module module = this.getModuleByName(name);
        if (module != null) {
            module.enable();
        }
    }

    public void disableModule(String name) {
        Module module = this.getModuleByName(name);
        if (module != null) {
            module.disable();
        }
    }

    public boolean isModuleEnabled(String name) {
        Module module = this.getModuleByName(name);
        return module != null && module.isOn();
    }

    public boolean isModuleEnabled(Class<? extends Module> clazz) {
        Module module = this.getModuleByClass(clazz);
        return module != null && module.isOn();
    }

    public Module getModuleByDisplayName(String displayName) {
        for (Module module : this.modules) {
            if (!module.getDisplayName().equalsIgnoreCase(displayName)) continue;
            return module;
        }
        return null;
    }

    public ArrayList<Module> getEnabledModules() {
        ArrayList<Module> enabledModules = new ArrayList<Module>();
        for (Module module : this.modules) {
            if (!module.isEnabled() && !module.isSliding()) continue;
            enabledModules.add(module);
        }
        return enabledModules;
    }

    public ArrayList<Module> getModulesByCategory(Module.Category category) {
        ArrayList<Module> modulesCategory = new ArrayList<Module>();
        this.modules.forEach(module -> {
            if (module.getCategory() == category) {
                modulesCategory.add((Module)module);
            }
        });
        return modulesCategory;
    }

    public List<Module.Category> getCategories() {
        return Arrays.asList(Module.Category.values());
    }

    public void onLoad() {
        this.modules.stream().filter(Module::listening).forEach(arg_0 -> ((EventBus)MinecraftForge.EVENT_BUS).register(arg_0));
        this.modules.forEach(Module::onLoad);
    }

    public void onUpdate() {
        this.modules.stream().filter(Feature::isEnabled).forEach(Module::onUpdate);
    }

    public void onTick() {
        this.modules.stream().filter(Feature::isEnabled).forEach(Module::onTick);
    }

    public void onRender2D(Render2DEvent event) {
        this.modules.stream().filter(Feature::isEnabled).forEach(module -> module.onRender2D(event));
    }

    public void onRender3D(Render3DEvent event) {
        this.modules.stream().filter(Feature::isEnabled).forEach(module -> module.onRender3D(event));
    }

    public void sortModules(boolean reverse) {
        this.sortedModules = this.getEnabledModules().stream().filter(Module::isDrawn).sorted(Comparator.comparing(module -> this.renderer.getStringWidth(module.getFullArrayString()) * (reverse ? -1 : 1))).collect(Collectors.toList());
    }

    public void alphabeticallySortModules() {
        this.alphabeticallySortedModules = this.getEnabledModules().stream().filter(Module::isDrawn).sorted(Comparator.comparing(Module::getDisplayName)).collect(Collectors.toList());
    }

    public void onLogout() {
        this.modules.forEach(Module::onLogout);
    }

    public void onLogin() {
        this.modules.forEach(Module::onLogin);
    }

    public void onUnload() {
        this.modules.forEach(arg_0 -> ((EventBus)MinecraftForge.EVENT_BUS).unregister(arg_0));
        this.modules.forEach(Module::onUnload);
    }

    public void onUnloadPost() {
        for (Module module : this.modules) {
            module.enabled.setValue(false);
        }
    }

    public void onKeyPressed(int eventKey) {
        if (eventKey == 0 || !Keyboard.getEventKeyState() || ModuleManager.mc.currentScreen instanceof PhobosGui) {
            return;
        }
        this.modules.forEach(module -> {
            if (module.getBind().getKey() == eventKey) {
                module.toggle();
            }
        });
    }

    public List<Module> getAnimationModules(Module.Category category) {
        ArrayList<Module> animationModules = new ArrayList<Module>();
        for (Module module : this.getEnabledModules()) {
            if (module.getCategory() != category || module.isDisabled() || !module.isSliding() || !module.isDrawn()) continue;
            animationModules.add(module);
        }
        return animationModules;
    }
}

