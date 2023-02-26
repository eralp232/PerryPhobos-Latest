
/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.gui.GuiChat
 *  net.minecraft.client.gui.ScaledResolution
 *  net.minecraft.client.renderer.GlStateManager
 *  net.minecraft.entity.Entity
 *  net.minecraft.init.Items
 *  net.minecraft.init.MobEffects
 *  net.minecraft.item.ItemStack
 *  net.minecraft.potion.Potion
 *  net.minecraft.potion.PotionEffect
 *  net.minecraft.util.ResourceLocation
 *  net.minecraft.util.SoundEvent
 *  net.minecraftforge.fml.common.eventhandler.SubscribeEvent
 */
package me.earth.phobos.features.modules.client;

import java.awt.Color;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import me.earth.phobos.Phobos;
import me.earth.phobos.event.events.ClientEvent;
import me.earth.phobos.event.events.Render2DEvent;
import me.earth.phobos.features.modules.Module;
import me.earth.phobos.features.modules.client.Colors;
import me.earth.phobos.features.modules.client.Management;
import me.earth.phobos.features.modules.client.PingBypass;
import me.earth.phobos.features.modules.misc.ToolTips;
import me.earth.phobos.features.setting.Setting;
import me.earth.phobos.manager.TextManager;
import me.earth.phobos.util.ColorUtil;
import me.earth.phobos.util.EntityUtil;
import me.earth.phobos.util.MathUtil;
import me.earth.phobos.util.RenderUtil;
import me.earth.phobos.util.TimerUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class HUD
extends Module {
    private static final ResourceLocation box = new ResourceLocation("textures/gui/container/shulker_box.png");
    private static final ItemStack totem = new ItemStack(Items.TOTEM_OF_UNDYING);
    private static final ResourceLocation codHitmarker = new ResourceLocation("earthhack", "cod_hitmarker");
    public static final SoundEvent COD_EVENT = new SoundEvent(codHitmarker);
    private static final ResourceLocation csgoHitmarker = new ResourceLocation("earthhack", "csgo_hitmarker");
    public static final SoundEvent CSGO_EVENT = new SoundEvent(csgoHitmarker);
    private static HUD INSTANCE = new HUD();
    private final Setting<Boolean> renderingUp = this.register(new Setting<Boolean>("RenderingUp", Boolean.valueOf(false), "Orientation of the HUD-Elements."));
    private final Setting<WaterMark> watermark = this.register(new Setting<WaterMark>("Logo", WaterMark.NONE, "WaterMark"));
    private final Setting<String> customWatermark = this.register(new Setting<String>("WatermarkName", "megyn.club b1"));
    private final Setting<Boolean> modeVer = this.register(new Setting<Object>("Version", Boolean.valueOf(false), v -> this.watermark.getValue() != WaterMark.NONE));
    private final Setting<Boolean> arrayList = this.register(new Setting<Boolean>("ActiveModules", Boolean.valueOf(false), "Lists the active modules."));
    private final Setting<Boolean> moduleColors = this.register(new Setting<Object>("ModuleColors", Boolean.valueOf(false), v -> this.arrayList.getValue()));
    private final Setting<Boolean> alphabeticalSorting = this.register(new Setting<Object>("AlphabeticalSorting", Boolean.valueOf(false), v -> this.arrayList.getValue()));
    private final Setting<Boolean> serverBrand = this.register(new Setting<Boolean>("ServerBrand", Boolean.valueOf(false), "Brand of the server you are on."));
    private final Setting<Boolean> ping = this.register(new Setting<Boolean>("Ping", Boolean.valueOf(false), "Your response time to the server."));
    private final Setting<Boolean> tps = this.register(new Setting<Boolean>("TPS", Boolean.valueOf(false), "Ticks per second of the server."));
    private final Setting<Boolean> fps = this.register(new Setting<Boolean>("FPS", Boolean.valueOf(false), "Your frames per second."));
    private final Setting<Boolean> coords = this.register(new Setting<Boolean>("Coords", Boolean.valueOf(false), "Your current coordinates"));
    private final Setting<Boolean> direction = this.register(new Setting<Boolean>("Direction", Boolean.valueOf(false), "The Direction you are facing."));
    private final Setting<Boolean> speed = this.register(new Setting<Boolean>("Speed", Boolean.valueOf(false), "Your Speed"));
    private final Setting<Boolean> potions = this.register(new Setting<Boolean>("Potions", Boolean.valueOf(false), "Active potion effects"));
    private final Setting<Boolean> altPotionsColors = this.register(new Setting<Object>("AltPotionColors", Boolean.valueOf(false), v -> this.potions.getValue()));
    private final Setting<Boolean> armor = this.register(new Setting<Boolean>("Armor", Boolean.valueOf(false), "ArmorHUD"));
    private final Setting<Boolean> durability = this.register(new Setting<Boolean>("Durability", Boolean.valueOf(false), "Durability"));
    private final Setting<Boolean> percent = this.register(new Setting<Object>("Percent", Boolean.valueOf(true), v -> this.armor.getValue()));
    private final Setting<Boolean> totems = this.register(new Setting<Boolean>("Totems", Boolean.valueOf(false), "TotemHUD"));
    private final Setting<Boolean> queue = this.register(new Setting<Boolean>("2b2tQueue", Boolean.valueOf(false), "Shows the 2b2t queue."));
    private final Setting<Greeter> greeter = this.register(new Setting<Greeter>("Greeter", Greeter.NONE, "Greets you."));
    private final Setting<String> spoofGreeter = this.register(new Setting<Object>("GreeterName", "3arthqu4ke", v -> this.greeter.getValue() == Greeter.CUSTOM));
    private final Setting<LagNotify> lag = this.register(new Setting<LagNotify>("Lag", LagNotify.GRAY, "Lag Notifier"));
    private final Setting<Boolean> hitMarkers = this.register(new Setting<Boolean>("HitMarkers", true));
    private final Setting<Sound> sound = this.register(new Setting<Object>("Sound", (Object)Sound.NONE, v -> this.hitMarkers.getValue()));
    private final Setting<Boolean> grayNess = this.register(new Setting<Boolean>("FutureColour", true));
    private final Setting<Boolean> friendslist = this.register(new Setting<Boolean>("Friends", false));
    private final TimerUtil timer = new TimerUtil();
    private final TimerUtil moduleTimer = new TimerUtil();
    private final Map<Potion, Color> potionColorMap = new HashMap<Potion, Color>();
    public Setting<Boolean> colorSync = this.register(new Setting<Boolean>("Sync", Boolean.valueOf(false), "Universal colors for hud."));
    public Setting<Boolean> rainbow = this.register(new Setting<Boolean>("Rainbow", Boolean.valueOf(false), "Rainbow hud."));
    public Setting<Integer> factor = this.register(new Setting<Object>("Factor", Integer.valueOf(1), Integer.valueOf(0), Integer.valueOf(20), v -> this.rainbow.getValue()));
    public Setting<Boolean> rolling = this.register(new Setting<Object>("Rolling", Boolean.valueOf(false), v -> this.rainbow.getValue()));
    public Setting<Integer> rainbowSpeed = this.register(new Setting<Object>("RSpeed", Integer.valueOf(20), Integer.valueOf(0), Integer.valueOf(100), v -> this.rainbow.getValue()));
    public Setting<Integer> rainbowSaturation = this.register(new Setting<Object>("Saturation", Integer.valueOf(255), Integer.valueOf(0), Integer.valueOf(255), v -> this.rainbow.getValue()));
    public Setting<Integer> rainbowBrightness = this.register(new Setting<Object>("Brightness", Integer.valueOf(255), Integer.valueOf(0), Integer.valueOf(255), v -> this.rainbow.getValue()));
    public Setting<Boolean> potionIcons = this.register(new Setting<Boolean>("PotionIcons", Boolean.valueOf(true), "Draws Potion Icons."));
    public Setting<Boolean> shadow = this.register(new Setting<Boolean>("Shadow", Boolean.valueOf(false), "Draws the text with a shadow."));
    public Setting<Integer> animationHorizontalTime = this.register(new Setting<Object>("AnimationHTime", Integer.valueOf(500), Integer.valueOf(1), Integer.valueOf(1000), v -> this.arrayList.getValue()));
    public Setting<Integer> animationVerticalTime = this.register(new Setting<Object>("AnimationVTime", Integer.valueOf(50), Integer.valueOf(1), Integer.valueOf(500), v -> this.arrayList.getValue()));
    public Setting<Boolean> textRadar = this.register(new Setting<Boolean>("TextRadar", Boolean.valueOf(false), "A TextRadar"));
    public Setting<Integer> playerCount = this.register(new Setting<Integer>("MaxPlayers", Integer.valueOf(8), Integer.valueOf(1), Integer.valueOf(30), v -> this.textRadar.getValue()));
    public Setting<Boolean> time = this.register(new Setting<Boolean>("Time", Boolean.valueOf(false), "The time"));
    public Setting<Integer> hudRed = this.register(new Setting<Object>("Red", Integer.valueOf(255), Integer.valueOf(0), Integer.valueOf(255), v -> this.rainbow.getValue() == false));
    public Setting<Integer> hudGreen = this.register(new Setting<Object>("Green", Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(255), v -> this.rainbow.getValue() == false));
    public Setting<Integer> hudBlue = this.register(new Setting<Object>("Blue", Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(255), v -> this.rainbow.getValue() == false));
    public Setting<Boolean> potions1 = this.register(new Setting<Object>("LevelPotions", Boolean.valueOf(false), v -> this.potions.getValue()));
    public Setting<Boolean> MS = this.register(new Setting<Object>("ms", Boolean.valueOf(false), v -> this.ping.getValue()));
    public Map<Module, Float> moduleProgressMap = new HashMap<Module, Float>();
    public Map<Integer, Integer> colorMap = new HashMap<Integer, Integer>();
    private Map<String, Integer> players = new HashMap<String, Integer>();
    private int color;
    private boolean shouldIncrement;
    private int hitMarkerTimer;

    public HUD() {
        super("HUD", "HUD Elements rendered on your screen.", Module.Category.CLIENT, true, false, false);
        this.setInstance();
        this.potionColorMap.put(MobEffects.SPEED, new Color(124, 175, 198));
        this.potionColorMap.put(MobEffects.SLOWNESS, new Color(90, 108, 129));
        this.potionColorMap.put(MobEffects.HASTE, new Color(217, 192, 67));
        this.potionColorMap.put(MobEffects.MINING_FATIGUE, new Color(74, 66, 23));
        this.potionColorMap.put(MobEffects.STRENGTH, new Color(147, 36, 35));
        this.potionColorMap.put(MobEffects.INSTANT_HEALTH, new Color(67, 10, 9));
        this.potionColorMap.put(MobEffects.INSTANT_DAMAGE, new Color(67, 10, 9));
        this.potionColorMap.put(MobEffects.JUMP_BOOST, new Color(34, 255, 76));
        this.potionColorMap.put(MobEffects.NAUSEA, new Color(85, 29, 74));
        this.potionColorMap.put(MobEffects.REGENERATION, new Color(205, 92, 171));
        this.potionColorMap.put(MobEffects.RESISTANCE, new Color(153, 69, 58));
        this.potionColorMap.put(MobEffects.FIRE_RESISTANCE, new Color(228, 154, 58));
        this.potionColorMap.put(MobEffects.WATER_BREATHING, new Color(46, 82, 153));
        this.potionColorMap.put(MobEffects.INVISIBILITY, new Color(127, 131, 146));
        this.potionColorMap.put(MobEffects.BLINDNESS, new Color(31, 31, 35));
        this.potionColorMap.put(MobEffects.NIGHT_VISION, new Color(31, 31, 161));
        this.potionColorMap.put(MobEffects.HUNGER, new Color(88, 118, 83));
        this.potionColorMap.put(MobEffects.WEAKNESS, new Color(72, 77, 72));
        this.potionColorMap.put(MobEffects.POISON, new Color(78, 147, 49));
        this.potionColorMap.put(MobEffects.WITHER, new Color(53, 42, 39));
        this.potionColorMap.put(MobEffects.HEALTH_BOOST, new Color(248, 125, 35));
        this.potionColorMap.put(MobEffects.ABSORPTION, new Color(37, 82, 165));
        this.potionColorMap.put(MobEffects.SATURATION, new Color(248, 36, 35));
        this.potionColorMap.put(MobEffects.GLOWING, new Color(148, 160, 97));
        this.potionColorMap.put(MobEffects.LEVITATION, new Color(206, 255, 255));
        this.potionColorMap.put(MobEffects.LUCK, new Color(51, 153, 0));
        this.potionColorMap.put(MobEffects.UNLUCK, new Color(192, 164, 77));
    }

    public static HUD getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new HUD();
        }
        return INSTANCE;
    }

    private void setInstance() {
        INSTANCE = this;
    }

    @Override
    public void onUpdate() {
        for (Module module : Phobos.moduleManager.sortedModules) {
            if (!module.isDisabled() || module.arrayListOffset != 0.0f) continue;
            module.sliding = true;
        }
        if (this.timer.passedMs(Management.getInstance().textRadarUpdates.getValue().intValue())) {
            this.players = this.getTextRadarPlayers();
            this.timer.reset();
        }
        if (this.shouldIncrement) {
            ++this.hitMarkerTimer;
        }
        if (this.hitMarkerTimer == 10) {
            this.hitMarkerTimer = 0;
            this.shouldIncrement = false;
        }
    }

    @SubscribeEvent
    public void onModuleToggle(ClientEvent event) {
        block4: {
            block5: {
                if (!(event.getFeature() instanceof Module)) break block4;
                if (event.getStage() != 0) break block5;
                for (float i = 0.0f; i <= (float)this.renderer.getStringWidth(((Module)event.getFeature()).getDisplayName()); i += (float)this.renderer.getStringWidth(((Module)event.getFeature()).getDisplayName()) / 500.0f) {
                    if (this.moduleTimer.passedMs(1L)) {
                        this.moduleProgressMap.put((Module)event.getFeature(), Float.valueOf((float)this.renderer.getStringWidth(((Module)event.getFeature()).getDisplayName()) - i));
                    }
                    this.timer.reset();
                }
                break block4;
            }
            if (event.getStage() != 1) break block4;
            for (float i = 0.0f; i <= (float)this.renderer.getStringWidth(((Module)event.getFeature()).getDisplayName()); i += (float)this.renderer.getStringWidth(((Module)event.getFeature()).getDisplayName()) / 500.0f) {
                if (this.moduleTimer.passedMs(1L)) {
                    this.moduleProgressMap.put((Module)event.getFeature(), Float.valueOf((float)this.renderer.getStringWidth(((Module)event.getFeature()).getDisplayName()) - i));
                }
                this.timer.reset();
            }
        }
    }

    @Override
    public void onRender2D(Render2DEvent event) {
        int color;
        String fpsText;
        Object text2;
        String text;
        int k;
        int j = this.renderingUp.getValue ( ) ? 0 : ( ( HUD.mc.currentScreen instanceof GuiChat ) ? 14 : 0 );
        if (HUD.fullNullCheck()) {
            return;
        }
        int colorSpeed = 101 - this.rainbowSpeed.getValue();
        float hue = this.colorSync.getValue() != false ? Colors.INSTANCE.hue : (float)(System.currentTimeMillis() % (long)(360 * colorSpeed)) / (360.0f * (float)colorSpeed);
        int width = this.renderer.scaledWidth;
        int height = this.renderer.scaledHeight;
        float tempHue = hue;
        for (int i = 0; i <= height; ++i) {
            if (this.colorSync.getValue().booleanValue()) {
                this.colorMap.put(i, Color.HSBtoRGB(tempHue, (float)Colors.INSTANCE.rainbowSaturation.getValue().intValue() / 255.0f, (float)Colors.INSTANCE.rainbowBrightness.getValue().intValue() / 255.0f));
            } else {
                this.colorMap.put(i, Color.HSBtoRGB(tempHue, (float)this.rainbowSaturation.getValue().intValue() / 255.0f, (float)this.rainbowBrightness.getValue().intValue() / 255.0f));
            }
            tempHue += 1.0f / (float)height * (float)this.factor.getValue().intValue();
        }
        GlStateManager.pushMatrix();
        if (this.rainbow.getValue().booleanValue() && !this.rolling.getValue().booleanValue()) {
            this.color = this.colorSync.getValue() != false ? Colors.INSTANCE.getCurrentColorHex() : Color.HSBtoRGB(hue, (float)this.rainbowSaturation.getValue().intValue() / 255.0f, (float)this.rainbowBrightness.getValue().intValue() / 255.0f);
        } else if (!this.rainbow.getValue().booleanValue()) {
            this.color = this.colorSync.getValue() != false ? Colors.INSTANCE.getCurrentColorHex() : ColorUtil.toRGBA(this.hudRed.getValue(), this.hudGreen.getValue(), this.hudBlue.getValue());
        }
        String grayString = this.grayNess.getValue() != false ? "\u00a77" : "";
        switch (this.watermark.getValue()) {
            case PHOBOS: {
                this.renderer.drawString("Phobos" + (this.modeVer.getValue() != false ? " v1.9.0" : ""), 2.0f, 2.0f, this.rolling.getValue() != false && this.rainbow.getValue() != false ? this.colorMap.get(2) : this.color, true);
                break;
            }
            case EARTH: {
                this.renderer.drawString("3arthh4ck" + (this.modeVer.getValue() != false ? " v1.9.0" : ""), 2.0f, 2.0f, this.rolling.getValue() != false && this.rainbow.getValue() != false ? this.colorMap.get(2) : this.color, true);
                break;
            }
            case CUSTOM: {
                this.renderer.drawString(this.customWatermark.getValue() + (this.modeVer.getValue() != false ? " v1.9.0" : ""), 2.0f, 2.0f, this.rolling.getValue() != false && this.rainbow.getValue() != false ? this.colorMap.get(2) : this.color, true);
            }
        }
        if (this.textRadar.getValue().booleanValue()) {
            this.drawTextRadar(ToolTips.getInstance().isOff() || ToolTips.getInstance().shulkerSpy.getValue() == false || ToolTips.getInstance().render.getValue() == false ? 0 : ToolTips.getInstance().getTextRadarY());
        }
        int n = this.renderingUp.getValue() != false ? 0 : (j = HUD.mc.currentScreen instanceof GuiChat ? 14 : 0);
        if (this.arrayList.getValue().booleanValue()) {
            Color moduleColor;
            Module module;
            if (this.renderingUp.getValue().booleanValue()) {
                for (k = 0; k < (this.alphabeticalSorting.getValue() != false ? Phobos.moduleManager.alphabeticallySortedModules.size() : Phobos.moduleManager.sortedModules.size()); ++k) {
                    module = this.alphabeticalSorting.getValue() != false ? Phobos.moduleManager.alphabeticallySortedModules.get(k) : Phobos.moduleManager.sortedModules.get(k);
                    text = module.getDisplayName() + "\u00a77" + (module.getDisplayInfo() != null ? " [\u00a7f" + module.getDisplayInfo() + "\u00a77]" : "");
                    moduleColor = Phobos.moduleManager.moduleColorMap.get(module);
                    this.renderer.drawString(text, (float)(width - 2 - this.renderer.getStringWidth(text)) + (this.animationHorizontalTime.getValue() == 1 ? 0.0f : module.arrayListOffset), 2 + j * 10, this.rolling.getValue() != false && this.rainbow.getValue() != false ? this.colorMap.get(MathUtil.clamp(2 + j * 10, 0, height)) : (this.moduleColors.getValue() != false && moduleColor != null ? moduleColor.getRGB() : this.color), true);
                    ++j;
                }
            } else {
                for (k = 0; k < (this.alphabeticalSorting.getValue() != false ? Phobos.moduleManager.alphabeticallySortedModules.size() : Phobos.moduleManager.sortedModules.size()); ++k) {
                    module = this.alphabeticalSorting.getValue() != false ? Phobos.moduleManager.alphabeticallySortedModules.get(Phobos.moduleManager.alphabeticallySortedModules.size() - 1 - k) : Phobos.moduleManager.sortedModules.get(k);
                    text = module.getDisplayName() + "\u00a77" + (module.getDisplayInfo() != null ? " [\u00a7f" + module.getDisplayInfo() + "\u00a77]" : "");
                    moduleColor = Phobos.moduleManager.moduleColorMap.get(module);
                    TextManager renderer = this.renderer;
                    float x = (float)(width - 2 - this.renderer.getStringWidth(text)) + (this.animationHorizontalTime.getValue() == 1 ? 0.0f : module.arrayListOffset);
                    renderer.drawString(text, x, height - (j += 10), this.rolling.getValue() != false && this.rainbow.getValue() != false ? this.colorMap.get(MathUtil.clamp(height - j, 0, height)) : (this.moduleColors.getValue() != false && moduleColor != null ? moduleColor.getRGB() : this.color), true);
                }
            }
        }
        k = 0;
        if (this.renderingUp.getValue().booleanValue()) {
            int itemDamage;
            if (this.serverBrand.getValue().booleanValue()) {
                text2 = grayString + "Server brand \u00a7f" + Phobos.serverManager.getServerBrand();
                TextManager renderer2 = this.renderer;
                float x2 = width - (this.renderer.getStringWidth((String)text2) + 2);
                int n2 = height - 2;
                renderer2.drawString((String)text2, x2, n2 - (k += 10), this.rolling.getValue() != false && this.rainbow.getValue() != false ? this.colorMap.get(height - k) : this.color, true);
            }
            if (this.potions.getValue().booleanValue()) {
                for (PotionEffect effect : Phobos.potionManager.getOwnPotions()) {
                    String text3 = this.altPotionsColors.getValue() != false ? Phobos.potionManager.getPotionString(effect) : Phobos.potionManager.getColoredPotionString(effect);
                    TextManager renderer3 = this.renderer;
                    float x3 = width - (this.renderer.getStringWidth(text3) + 2);
                    int n3 = height - 2;
                    renderer3.drawString(text3, x3, n3 - (k += 10), this.rolling.getValue() != false && this.rainbow.getValue() != false ? this.colorMap.get(height - k) : (this.altPotionsColors.getValue() != false ? this.potionColorMap.get(effect.getPotion()).getRGB() : this.color), true);
                }
            }
            if (this.speed.getValue().booleanValue()) {
                text2 = grayString + "Speed \u00a7f" + Phobos.speedManager.getSpeedKpH() + " km/h";
                TextManager renderer4 = this.renderer;
                float x4 = width - (this.renderer.getStringWidth((String)text2) + 2);
                int n4 = height - 2;
                renderer4.drawString((String)text2, x4, n4 - (k += 10), this.rolling.getValue() != false && this.rainbow.getValue() != false ? this.colorMap.get(height - k) : this.color, true);
            }
            if (this.time.getValue().booleanValue()) {
                text2 = grayString + "Time \u00a7f" + new SimpleDateFormat("h:mm a").format(new Date());
                TextManager renderer5 = this.renderer;
                float x5 = width - (this.renderer.getStringWidth((String)text2) + 2);
                int n5 = height - 2;
                renderer5.drawString((String)text2, x5, n5 - (k += 10), this.rolling.getValue() != false && this.rainbow.getValue() != false ? this.colorMap.get(height - k) : this.color, true);
            }
            if (this.durability.getValue().booleanValue() && (itemDamage = HUD.mc.player.getHeldItemMainhand().getMaxDamage() - HUD.mc.player.getHeldItemMainhand().getItemDamage()) > 0) {
                text = grayString + "Durability \u00a7a" + itemDamage;
                TextManager renderer6 = this.renderer;
                float x6 = width - (this.renderer.getStringWidth(text) + 2);
                int n6 = height - 2;
                renderer6.drawString(text, x6, n6 - (k += 10), this.rolling.getValue() != false && this.rainbow.getValue() != false ? this.colorMap.get(height - k) : this.color, true);
            }
            if (this.tps.getValue().booleanValue()) {
                String text22 = grayString + "TPS \u00a7f" + Phobos.serverManager.getTPS();
                TextManager renderer7 = this.renderer;
                float x7 = width - (this.renderer.getStringWidth(text22) + 2);
                int n7 = height - 2;
                renderer7.drawString(text22, x7, n7 - (k += 10), this.rolling.getValue() != false && this.rainbow.getValue() != false ? this.colorMap.get(height - k) : this.color, true);
            }
            fpsText = grayString + "FPS \u00a7f" + Minecraft.debugFPS;
            text = grayString + "Ping \u00a7f" + (PingBypass.getInstance().isConnected() ? PingBypass.getInstance().getServerPing() : (long)Phobos.serverManager.getPing()) + (this.MS.getValue() != false ? "ms" : "");
            if (this.renderer.getStringWidth(text) > this.renderer.getStringWidth(fpsText)) {
                if (this.ping.getValue().booleanValue()) {
                    TextManager renderer8 = this.renderer;
                    float x8 = width - (this.renderer.getStringWidth(text) + 2);
                    int n8 = height - 2;
                    renderer8.drawString(text, x8, n8 - (k += 10), this.rolling.getValue() != false && this.rainbow.getValue() != false ? this.colorMap.get(height - k) : this.color, true);
                }
                if (this.fps.getValue().booleanValue()) {
                    TextManager renderer9 = this.renderer;
                    float x9 = width - (this.renderer.getStringWidth(fpsText) + 2);
                    int n9 = height - 2;
                    renderer9.drawString(fpsText, x9, n9 - (k += 10), this.rolling.getValue() != false && this.rainbow.getValue() != false ? this.colorMap.get(height - k) : this.color, true);
                }
            } else {
                if (this.fps.getValue().booleanValue()) {
                    TextManager renderer10 = this.renderer;
                    float x10 = width - (this.renderer.getStringWidth(fpsText) + 2);
                    int n10 = height - 2;
                    renderer10.drawString(fpsText, x10, n10 - (k += 10), this.rolling.getValue() != false && this.rainbow.getValue() != false ? this.colorMap.get(height - k) : this.color, true);
                }
                if (this.ping.getValue().booleanValue()) {
                    TextManager renderer11 = this.renderer;
                    float x11 = width - (this.renderer.getStringWidth(text) + 2);
                    int n11 = height - 2;
                    renderer11.drawString(text, x11, n11 - (k += 10), this.rolling.getValue() != false && this.rainbow.getValue() != false ? this.colorMap.get(height - k) : this.color, true);
                }
            }
        } else {
            int itemDamage;
            if (this.serverBrand.getValue().booleanValue()) {
                text2 = grayString + "Server brand \u00a7f" + Phobos.serverManager.getServerBrand();
                this.renderer.drawString((String)text2, width - (this.renderer.getStringWidth((String)text2) + 2), 2.0f, this.rolling.getValue() != false && this.rainbow.getValue() != false ? this.colorMap.get(2) : this.color, true);
            }
            if (this.potions.getValue().booleanValue()) {
                for (PotionEffect effect : Phobos.potionManager.getOwnPotions()) {
                    String text3 = this.altPotionsColors.getValue() != false ? Phobos.potionManager.getPotionString(effect) : Phobos.potionManager.getColoredPotionString(effect);
                    this.renderer.drawString(text3, width - (this.renderer.getStringWidth(text3) + 2), 2 + k++ * 10, this.rolling.getValue() != false && this.rainbow.getValue() != false ? this.colorMap.get(2 + k * 10) : (this.altPotionsColors.getValue() != false ? this.potionColorMap.get(effect.getPotion()).getRGB() : this.color), true);
                }
            }
            if (this.speed.getValue().booleanValue()) {
                text2 = grayString + "Speed \u00a7f" + Phobos.speedManager.getSpeedKpH() + " km/h";
                this.renderer.drawString((String)text2, width - (this.renderer.getStringWidth((String)text2) + 2), 2 + k++ * 10, this.rolling.getValue() != false && this.rainbow.getValue() != false ? this.colorMap.get(2 + k * 10) : this.color, true);
            }
            if (this.time.getValue().booleanValue()) {
                text2 = grayString + "Time \u00a7f" + new SimpleDateFormat("h:mm a").format(new Date());
                this.renderer.drawString((String)text2, width - (this.renderer.getStringWidth((String)text2) + 2), 2 + k++ * 10, this.rolling.getValue() != false && this.rainbow.getValue() != false ? this.colorMap.get(2 + k * 10) : this.color, true);
            }
            if (this.durability.getValue().booleanValue() && (itemDamage = HUD.mc.player.getHeldItemMainhand().getMaxDamage() - HUD.mc.player.getHeldItemMainhand().getItemDamage()) > 0) {
                text = grayString + "Durability \u00a7a" + itemDamage;
                this.renderer.drawString(text, width - (this.renderer.getStringWidth(text) + 2), 2 + k++ * 10, this.rolling.getValue() != false && this.rainbow.getValue() != false ? this.colorMap.get(2 + k * 10) : this.color, true);
            }
            if (this.tps.getValue().booleanValue()) {
                String text23 = grayString + "TPS \u00a7f" + Phobos.serverManager.getTPS();
                this.renderer.drawString(text23, width - (this.renderer.getStringWidth(text23) + 2), 2 + k++ * 10, this.rolling.getValue() != false && this.rainbow.getValue() != false ? this.colorMap.get(2 + k * 10) : this.color, true);
            }
            fpsText = grayString + "FPS \u00a7f" + Minecraft.debugFPS;
            text = grayString + "Ping \u00a7f" + Phobos.serverManager.getPing();
            if (this.renderer.getStringWidth(text) > this.renderer.getStringWidth(fpsText)) {
                if (this.ping.getValue().booleanValue()) {
                    this.renderer.drawString(text, width - (this.renderer.getStringWidth(text) + 2), 2 + k++ * 10, this.rolling.getValue() != false && this.rainbow.getValue() != false ? this.colorMap.get(2 + k * 10) : this.color, true);
                }
                if (this.fps.getValue().booleanValue()) {
                    this.renderer.drawString(fpsText, width - (this.renderer.getStringWidth(fpsText) + 2), 2 + k++ * 10, this.rolling.getValue() != false && this.rainbow.getValue() != false ? this.colorMap.get(2 + k * 10) : this.color, true);
                }
            } else {
                if (this.fps.getValue().booleanValue()) {
                    this.renderer.drawString(fpsText, width - (this.renderer.getStringWidth(fpsText) + 2), 2 + k++ * 10, this.rolling.getValue() != false && this.rainbow.getValue() != false ? this.colorMap.get(2 + k * 10) : this.color, true);
                }
                if (this.ping.getValue().booleanValue()) {
                    this.renderer.drawString(text, width - (this.renderer.getStringWidth(text) + 2), 2 + k++ * 10, this.rolling.getValue() != false && this.rainbow.getValue() != false ? this.colorMap.get(2 + k * 10) : this.color, true);
                }
            }
        }
        boolean inHell = HUD.mc.world.getBiome(HUD.mc.player.getPosition()).getBiomeName().equals("Hell");
        int posX = (int)HUD.mc.player.posX;
        int posY = (int)HUD.mc.player.posY;
        int posZ = (int)HUD.mc.player.posZ;
        float nether = inHell ? 8.0f : 0.125f;
        int hposX = (int)(HUD.mc.player.posX * (double)nether);
        int hposZ = (int)(HUD.mc.player.posZ * (double)nether);
        if (this.renderingUp.getValue().booleanValue()) {
            Phobos.notificationManager.handleNotifications(height - (k + 16));
        } else {
            Phobos.notificationManager.handleNotifications(height - (j + 16));
        }
        k = HUD.mc.currentScreen instanceof GuiChat ? 14 : 0;
        String coordinates = grayString + "XYZ \u00a7f" + posX + ", " + posY + ", " + posZ + " \u00a7r" + grayString + "[\u00a7f" + hposX + ", " + hposZ + "\u00a7r" + grayString + "]";
        String text4 = (this.direction.getValue() != false ? Phobos.rotationManager.getDirection4D(false) + " " : "") + (this.coords.getValue() != false ? coordinates : "") + "";
        TextManager renderer12 = this.renderer;
        float x12 = 2.0f;
        float y = height - (k += 10);
        if (this.rolling.getValue().booleanValue() && this.rainbow.getValue().booleanValue()) {
            Map<Integer, Integer> colorMap = this.colorMap;
            color = colorMap.get(height - (k += 10));
        } else {
            color = this.color;
        }
        renderer12.drawString(text4, 2.0f, y, color, true);
        if (this.armor.getValue().booleanValue()) {
            this.renderArmorHUD(this.percent.getValue());
        }
        if (this.totems.getValue().booleanValue()) {
            this.renderTotemHUD();
        }
        if (this.greeter.getValue() != Greeter.NONE) {
            this.renderGreeter();
        }
        if (this.lag.getValue() != LagNotify.NONE) {
            this.renderLag();
        }
        if (this.hitMarkers.getValue().booleanValue() && this.hitMarkerTimer > 0) {
            this.drawHitMarkers();
        }
        GlStateManager.popMatrix();
        if (this.friendslist.getValue().booleanValue()) {
            Color object = new Color(Colors.INSTANCE.getCurrentColor().getRed(), Colors.INSTANCE.getCurrentColor().getGreen(), Colors.INSTANCE.getCurrentColor().getBlue(), 255);
            int n3 = 30;
            for (Entity entity : HUD.mc.world.playerEntities) {
                if (!Phobos.friendManager.isFriend(entity.getName())) continue;
                this.renderer.drawString("Friends", 1.0f, 30.0f, object.getRGB(), true);
                this.renderer.drawString(entity.getName(), 1.0f, n3 += this.renderer.getFontHeight() + 1, object.getRGB(), true);
            }
        }
    }

    public Map<String, Integer> getTextRadarPlayers() {
        return EntityUtil.getTextRadarPlayers();
    }

    public void renderGreeter() {
        int width = this.renderer.scaledWidth;
        String text = "";
        switch (this.greeter.getValue()) {
            case TIME: {
                text = text + MathUtil.getTimeOfDay() + HUD.mc.player.getDisplayNameString();
                break;
            }
            case CHRISTMAS: {
                text = text + "Merry Christmas " + HUD.mc.player.getDisplayNameString() + " :^)";
                break;
            }
            case LONG: {
                text = text + "Welcome to Phobos.eu " + HUD.mc.player.getDisplayNameString() + " :^)";
                break;
            }
            case CUSTOM: {
                text = text + this.spoofGreeter.getValue();
                break;
            }
            default: {
                text = text + "Welcome " + HUD.mc.player.getDisplayNameString();
            }
        }
        this.renderer.drawString(text, (float)width / 2.0f - (float)this.renderer.getStringWidth(text) / 2.0f + 2.0f, 2.0f, this.rolling.getValue() != false && this.rainbow.getValue() != false ? this.colorMap.get(2) : this.color, true);
    }

    public void renderLag() {
        int width = this.renderer.scaledWidth;
        if (Phobos.serverManager.isServerNotResponding()) {
            String text = (this.lag.getValue() == LagNotify.GRAY ? "\u00a77" : "\u00a7c") + "Server not responding: " + MathUtil.round((float)Phobos.serverManager.serverRespondingTime() / 1000.0f, 1) + "s.";
            this.renderer.drawString(text, (float)width / 2.0f - (float)this.renderer.getStringWidth(text) / 2.0f + 2.0f, 20.0f, this.rolling.getValue() != false && this.rainbow.getValue() != false ? this.colorMap.get(20) : this.color, true);
        }
    }

    public void renderTotemHUD() {
        int width = this.renderer.scaledWidth;
        int height = this.renderer.scaledHeight;
        int totems = HUD.mc.player.inventory.mainInventory.stream().filter(itemStack -> itemStack.getItem() == Items.TOTEM_OF_UNDYING).mapToInt(ItemStack::getCount).sum();
        if (HUD.mc.player.getHeldItemOffhand().getItem() == Items.TOTEM_OF_UNDYING) {
            totems += HUD.mc.player.getHeldItemOffhand().getCount();
        }
        if (totems > 0) {
            GlStateManager.enableTexture2D();
            int i = width / 2;
            int y = height - 55 - (HUD.mc.player.isInWater() && HUD.mc.playerController.gameIsSurvivalOrAdventure() ? 10 : 0);
            int x = i - 189 + 180 + 2;
            GlStateManager.enableDepth();
            RenderUtil.itemRender.zLevel = 200.0f;
            RenderUtil.itemRender.renderItemAndEffectIntoGUI(totem, x, y);
            RenderUtil.itemRender.renderItemOverlayIntoGUI(HUD.mc.fontRenderer, totem, x, y, "");
            RenderUtil.itemRender.zLevel = 0.0f;
            GlStateManager.enableTexture2D();
            GlStateManager.disableLighting();
            GlStateManager.disableDepth();
            this.renderer.drawStringWithShadow(totems + "", x + 19 - 2 - this.renderer.getStringWidth(totems + ""), y + 9, 0xFFFFFF);
            GlStateManager.enableDepth();
            GlStateManager.disableLighting();
        }
    }

    public void renderArmorHUD(boolean percent) {
        int width = this.renderer.scaledWidth;
        int height = this.renderer.scaledHeight;
        GlStateManager.enableTexture2D();
        int i = width / 2;
        int iteration = 0;
        int y = height - 55 - (HUD.mc.player.isInWater() && HUD.mc.playerController.gameIsSurvivalOrAdventure() ? 10 : 0);
        for (ItemStack is : HUD.mc.player.inventory.armorInventory) {
            ++iteration;
            if (is.isEmpty()) continue;
            int x = i - 90 + (9 - iteration) * 20 + 2;
            GlStateManager.enableDepth();
            RenderUtil.itemRender.zLevel = 200.0f;
            RenderUtil.itemRender.renderItemAndEffectIntoGUI(is, x, y);
            RenderUtil.itemRender.renderItemOverlayIntoGUI(HUD.mc.fontRenderer, is, x, y, "");
            RenderUtil.itemRender.zLevel = 0.0f;
            GlStateManager.enableTexture2D();
            GlStateManager.disableLighting();
            GlStateManager.disableDepth();
            String s = is.getCount() > 1 ? is.getCount() + "" : "";
            this.renderer.drawStringWithShadow(s, x + 19 - 2 - this.renderer.getStringWidth(s), y + 9, 0xFFFFFF);
            if (!percent) continue;
            float green = ((float)is.getMaxDamage() - (float)is.getItemDamage()) / (float)is.getMaxDamage();
            float red = 1.0f - green;
            int dmg = 100 - (int)(red * 100.0f);
            this.renderer.drawStringWithShadow(dmg + "", x + 8 - this.renderer.getStringWidth(dmg + "") / 2, y - 11, ColorUtil.toRGBA((int)(red * 255.0f), (int)(green * 255.0f), 0));
        }
        GlStateManager.enableDepth();
        GlStateManager.disableLighting();
    }

    public void drawHitMarkers() {
        ScaledResolution resolution = new ScaledResolution(mc);
        RenderUtil.drawLine((float)resolution.getScaledWidth() / 2.0f - 4.0f, (float)resolution.getScaledHeight() / 2.0f - 4.0f, (float)resolution.getScaledWidth() / 2.0f - 8.0f, (float)resolution.getScaledHeight() / 2.0f - 8.0f, 1.0f, ColorUtil.toRGBA(255, 255, 255, 255));
        RenderUtil.drawLine((float)resolution.getScaledWidth() / 2.0f + 4.0f, (float)resolution.getScaledHeight() / 2.0f - 4.0f, (float)resolution.getScaledWidth() / 2.0f + 8.0f, (float)resolution.getScaledHeight() / 2.0f - 8.0f, 1.0f, ColorUtil.toRGBA(255, 255, 255, 255));
        RenderUtil.drawLine((float)resolution.getScaledWidth() / 2.0f - 4.0f, (float)resolution.getScaledHeight() / 2.0f + 4.0f, (float)resolution.getScaledWidth() / 2.0f - 8.0f, (float)resolution.getScaledHeight() / 2.0f + 8.0f, 1.0f, ColorUtil.toRGBA(255, 255, 255, 255));
        RenderUtil.drawLine((float)resolution.getScaledWidth() / 2.0f + 4.0f, (float)resolution.getScaledHeight() / 2.0f + 4.0f, (float)resolution.getScaledWidth() / 2.0f + 8.0f, (float)resolution.getScaledHeight() / 2.0f + 8.0f, 1.0f, ColorUtil.toRGBA(255, 255, 255, 255));
    }

    public void drawTextRadar(int yOffset) {
        if (!this.players.isEmpty()) {
            int y = this.renderer.getFontHeight() + 7 + yOffset;
            int n3 = 0;
            for (Map.Entry<String, Integer> entry : this.players.entrySet()) {
                if (n3 >= this.playerCount.getValue()) continue;
                String string = entry.getKey() + " ";
                int n4 = this.renderer.getFontHeight() + 1;
                this.renderer.drawString(string, 2.0f, y, this.rolling.getValue() != false && this.rainbow.getValue() != false ? this.colorMap.get(y) : this.color, true);
                y += n4;
            }
            ++n3;
        }
    }

    public static enum Sound {
        NONE,
        COD,
        CSGO;

    }

    public static enum WaterMark {
        NONE,
        PHOBOS,
        EARTH,
        CUSTOM;

    }

    public static enum LagNotify {
        NONE,
        RED,
        GRAY;

    }

    public static enum Greeter {
        NONE,
        NAME,
        TIME,
        CHRISTMAS,
        LONG,
        CUSTOM;

    }
}

