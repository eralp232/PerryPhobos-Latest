
/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.network.play.client.CPacketChatMessage
 *  net.minecraft.network.play.server.SPacketChat
 *  net.minecraft.util.math.Vec3i
 *  net.minecraft.util.text.TextComponentString
 *  net.minecraftforge.fml.common.eventhandler.SubscribeEvent
 */
package me.earth.phobos.features.modules.misc;

import java.text.SimpleDateFormat;
import java.util.Date;
import me.earth.phobos.event.events.PacketEvent;
import me.earth.phobos.features.command.Command;
import me.earth.phobos.features.modules.Module;
import me.earth.phobos.features.modules.client.Management;
import me.earth.phobos.features.setting.Setting;
import me.earth.phobos.util.TextUtil;
import me.earth.phobos.util.TimerUtil;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.play.client.CPacketChatMessage;
import net.minecraft.network.play.server.SPacketChat;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class ChatModifier
extends Module {
    private static ChatModifier INSTANCE = new ChatModifier();
    private final TimerUtil timer = new TimerUtil();
    public Setting<Suffix> suffix = this.register(new Setting<Suffix>("Suffix", Suffix.NONE, "Your Suffix."));
    public Setting<String> customSuffix = this.register(new Setting<String>("", " | Perrys Phobos", v -> this.suffix.getValue() == Suffix.CUSTOM));
    public Setting<Boolean> clean = this.register(new Setting<Boolean>("CleanChat", Boolean.valueOf(false), "Cleans your chat"));
    public Setting<Boolean> infinite = this.register(new Setting<Boolean>("Infinite", Boolean.valueOf(false), "Makes your chat infinite."));
    public Setting<Boolean> autoQMain = this.register(new Setting<Boolean>("AutoQMain", Boolean.valueOf(false), "Spams AutoQMain"));
    public Setting<Boolean> qNotification = this.register(new Setting<Object>("QNotification", Boolean.valueOf(false), v -> this.autoQMain.getValue()));
    public Setting<Integer> qDelay = this.register(new Setting<Object>("QDelay", Integer.valueOf(9), Integer.valueOf(1), Integer.valueOf(90), v -> this.autoQMain.getValue()));
    public Setting<TextUtil.Color> timeStamps = this.register(new Setting<TextUtil.Color>("Time", TextUtil.Color.NONE));
    public Setting<Boolean> rainbowTimeStamps = this.register(new Setting<Object>("RainbowTimeStamps", Boolean.valueOf(false), v -> this.timeStamps.getValue() != TextUtil.Color.NONE));
    public Setting<TextUtil.Color> bracket = this.register(new Setting<Object>("Bracket", (Object)TextUtil.Color.WHITE, v -> this.timeStamps.getValue() != TextUtil.Color.NONE));
    public Setting<Boolean> space = this.register(new Setting<Object>("Space", Boolean.valueOf(true), v -> this.timeStamps.getValue() != TextUtil.Color.NONE));
    public Setting<Boolean> all = this.register(new Setting<Object>("All", Boolean.valueOf(false), v -> this.timeStamps.getValue() != TextUtil.Color.NONE));
    public Setting<Boolean> shrug = this.register(new Setting<Boolean>("Shrug", false));
    public Setting<Boolean> disability = this.register(new Setting<Boolean>("Disability", false));

    public ChatModifier() {
        super("Chat", "Modifies your chat.", Module.Category.MISC, true, false, false);
        this.setInstance();
    }

    public static ChatModifier getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ChatModifier();
        }
        return INSTANCE;
    }

    private void setInstance() {
        INSTANCE = this;
    }

    @Override
    public void onUpdate() {
        if (this.shrug.getValue().booleanValue()) {
            ChatModifier.mc.player.sendChatMessage(TextUtil.shrug);
            this.shrug.setValue(false);
        }
        if (this.disability.getValue().booleanValue()) {
            ChatModifier.mc.player.sendChatMessage(TextUtil.disability);
            this.disability.setValue(false);
        }
        if (this.autoQMain.getValue().booleanValue()) {
            if (!this.shouldSendMessage((EntityPlayer)ChatModifier.mc.player)) {
                return;
            }
            if (this.qNotification.getValue().booleanValue()) {
                Command.sendMessage("<AutoQueueMain> Sending message: /queue main");
            }
            ChatModifier.mc.player.sendChatMessage("/queue main");
            this.timer.reset();
        }
    }

    @SubscribeEvent
    public void onPacketSend(PacketEvent.Send event) {
        if (event.getStage() == 0 && event.getPacket() instanceof CPacketChatMessage) {
            CPacketChatMessage packet = (CPacketChatMessage)event.getPacket();
            String s = packet.getMessage();
            if (s.startsWith("/")) {
                return;
            }
            switch (this.suffix.getValue()) {
                case EARTH: {
                    s = s + " \u23d0 3\u1d00\u0280\u1d1b\u029c\u029c4\u1d04\u1d0b";
                    break;
                }
                case PERRYPHOBOS: {
                    s = s + " \u23d0 \u1d29\u1d07\u0280\u0280\u1203\ua731 \u1d29\u029c\u1d0f\u0299\u1d0f\ua731";
                    break;
                }
                case PHOBOS: {
                    s = s + " \u23d0 \u1d18\u029c\u1d0f\u0299\u1d0f\ua731";
                    break;
                }
                case INSANE: {
                    s = s + " | \u028c\u0433\u1d07\u0455+ \u00ab \u1d0b\u1d00\u1d0d\u026a \u0299\u029f\u1d1c\u1d07 \u1d0f\u0274 \u1d1b\u1d0f\u1d18 \u00bb \u00bb \u02e2\u207f\u1d52\u02b7\u23d0 \u041d\u03b5\u13ae\u043d\u15e9\u03b5\u0455\u01ad\u03c5\u0455 \u00bb \u0299\u1d00\u1d04\u1d0b\u1d05\u1d0f\u1d0f\u0280\u1d07\u1d05 | \u1d0d\u1d07\u1d0f\u1d21 \u00bb \u1d1c\u0274\u026a\u1d04\u1d0f\u0280\u0274\u0262\u1d0f\u1d05.\u0262\u0262  \ua731\u1d07\u1d18\u1d18\u1d1c\u1d0b\u1d1c | \u029c\u1d1c\u1d22\u1d1c\u0274\u026a\u0262\u0280\u1d07\u1d07\u0274.\u0262\u0262tm \u00bb \u0299\u1d00\u1d04\u1d0b\u1d04\u029f\u026a\u1d07\u0274\u1d1btm \u00bb \u0274\u1d0f\u1d1c \u029f\u1d07\u1d00\u1d0b \u23d0 \u0493\u1d0f\u0280\u0262\u1d07\u0280\u1d00\u1d1b \u2661 | \u04e8B\u039bM\u039b \u1103\u1102I\u03a3\u041f\u01ac - \u1d07\u029f\u1d07\u1d0d\u1d07\u0274\u1d1b\u1d00\u0280\ua731.\u1d04\u1d0f\u1d0d \u300b\u1d0f\ua731\u026a\u0280\u026a\ua731 | W\u00d4\u00d4K\u00cf\u00ca \u00c7L\u00ee\u00eb\u00d1Ttm {\u0280\u1d00\u026a\u1d0f\u0274\u1d0b\u1d07\u1d0b} \u30c3 \uff32\uff10\uff10\uff34\uff5c \u1d20\u1d0f\u026a\u1d05 \u1d04\u029f\u026a\u1d07\u0274\u1d1b \uff5c \u1d0b\u1d00\u1d0d\u026a \uff5c \u02b3\u1d1c\u02e2\u02b0\u1d07\u02b3\u02b0\u1d00\u1d9c\u1d4f \uff5c \u24cc\u24cc\u24ba \uff5c \uff49\uff4d\uff50\uff41\uff43\uff54\uff5c \ua730\u1d1c\u1d1b\u1d1c\u0280\u1d07tm \u24d6\u24de\u24db\u24d3 \uff5c \u5342\u4e02\u3129\u51e0\u5342\u1d04\u029f\u026a\u1d07\u0274\u1d1b \uff5c\ua118\ua182\ua493\ua04d\ua35f\ua45b\ua2eb\ua1d3 \uff5c \u02b3\u1d00\uff54\u1d04\u029f\u026a\u1d07\u0274\u1d1b \uff5c \u1d00\u1d18\u1d0f\u029f\u029f\u028f\u1d0f\u0274 \uff5c \u1d0b\u1d00\u1d0d\u026a \u0299\u029f\u1d1c\u1d07 \u2758 \u1d3e\u1d3c\u1d3e\u1d2e\u1d3c\u1d2e \u1d9c\u1d38\u1d35\u1d31\u1d3a\u1d40 \u23d0 \u0262\u1d00\u028f \u23d0  c l i e n t |  B a L l C l i E n T\u00bb \u028c\u0433\u1d07\u0455+ \u00ab \u1d0b\u1d00\u1d0d\u026a \u0299\u029f\u1d1c\u1d07 \u1d0f\u0274 \u1d1b\u1d0f\u1d18 \u00bb \u00bb \u02e2\u207f\u1d52\u02b7\u23d0 \u041d\u03b5\u13ae\u043d\u15e9\u03b5\u0455\u01ad\u03c5\u0455 \u00bb \u0299\u1d00\u1d04\u1d0b\u1d05\u1d0f\u1d0f\u0280\u1d07\u1d05 | \u1d0d\u1d07\u1d0f\u1d21 \u00bb \u1d1c\u0274\u026a\u1d04\u1d0f\u0280\u0274\u0262\u1d0f\u1d05.\u0262\u0262  \ua731\u1d07\u1d18\u1d18\u1d1c\u1d0b\u1d1c | \u029c\u1d1c\u1d22\u1d1c\u0274\u026a\u0262\u0280\u1d07\u1d07\u0274.\u0262\u0262tm \u00bb \u0299\u1d00\u1d04\u1d0b\u1d04\u029f\u026a\u1d07\u0274\u1d1btm \u00bb \u0274\u1d0f\u1d1c \u029f\u1d07\u1d00\u1d0b  \u23d0 \u0493\u1d0f\u0280\u0262\u1d07\u0280\u1d00\u1d1b \u2661 | \u04e8B\u039bM\u039b \u1103\u1102I\u03a3\u041f\u01ac - \u1d07\u029f\u1d07\u1d0d\u1d07\u0274\u1d1b\u1d00\u0280\ua731.\u1d04\u1d0f\u1d0d \u300b\u1d0f\ua731\u026a\u0280\u026a\ua731 | W\u00d4\u00d4K\u00cf\u00ca \u00c7L\u00ee\u00eb\u00d1Ttm {\u0280\u1d00\u026a\u1d0f\u0274\u1d0b\u1d07\u1d0b} \u30c3 \uff32\uff10\uff10\uff34\uff5c \u1d20\u1d0f\u026a\u1d05 \u1d04\u029f\u026a\u1d07\u0274\u1d1b \uff5c \u1d0b\u1d00\u1d0d\u026a \uff5c \u02b3\u1d1c\u02e2\u02b0\u1d07\u02b3\u02b0\u1d00\u1d9c\u1d4f \uff5c \u24cc\u24cc\u24ba \uff5c \uff49\uff4d\uff50\uff41\uff43\uff54\uff5c \ua730\u1d1c\u1d1b\u1d1c\u0280\u1d07tm \u24d6\u24de\u24db\u24d3 \uff5c \u5342\u4e02\u3129\u51e0\u5342\u1d04\u029f\u026a\u1d07\u0274\u1d1b \uff5c\ua118\ua182\ua493\ua04d\ua35f\ua45b\ua2eb\ua1d3 \uff5c \u02b3\u1d00\uff54\u1d04\u029f\u026a\u1d07\u0274\u1d1b \uff5c \u1d00\u1d18\u1d0f\u029f\u029f\u028f\u1d0f\u0274 \uff5c \u1d0b\u1d00\u1d0d\u026a \u0299\u029f\u1d1c\u1d07 \u2758 \u1d3e\u1d3c\u1d3e\u1d2e\u1d3c\u1d2e \u1d9c\u1d38\u1d35\u1d31\u1d3a\u1d40 \u23d0 \u0262\u1d00\u028f \u23d0  c l i e n t |  B a L l C l i E n T\u00bb \u028c\u0433\u1d07\u0455+ \u00ab \u1d0b\u1d00\u1d0d\u026a \u0299\u029f\u1d1c\u1d07 \u1d0f\u0274 \u1d1b\u1d0f\u1d18 \u00bb \u00bb \u02e2\u207f\u1d52\u02b7\u23d0 \u041d\u03b5\u13ae\u043d\u15e9\u03b5\u0455\u01ad\u03c5\u0455 \u00bb \u0299\u1d00\u1d04\u1d0b\u1d05\u1d0f\u1d0f\u0280\u1d07\u1d05 | \u1d0d\u1d07\u1d0f\u1d21 \u00bb \u1d1c\u0274\u026a\u1d04\u1d0f\u0280\u0274\u0262\u1d0f\u1d05.\u0262\u0262  \ua731\u1d07\u1d18\u1d18\u1d1c\u1d0b\u1d1c | \u029c\u1d1c\u1d22\u1d1c\u0274\u026a\u0262\u0280\u1d07\u1d07\u0274.\u0262\u0262tm \u00bb \u0299\u1d00\u1d04\u1d0b\u1d04\u029f\u026a\u1d07\u0274\u1d1btm \u00bb \u0274\u1d0f\u1d1c \u029f\u1d07\u1d00\u1d0b  \u23d0 \u0493\u1d0f\u0280\u0262\u1d07\u0280\u1d00\u1d1b \u2661 | \u04e8B\u039bM\u039b \u1103\u1102I\u03a3\u041f\u01ac - \u1d07\u029f\u1d07\u1d0d\u1d07\u0274\u1d1b\u1d00\u0280\ua731.\u1d04\u1d0f\u1d0d \u300b\u1d0f\ua731\u026a\u0280\u026a\ua731 | W\u00d4\u00d4K\u00cf\u00ca \u00c7L\u00ee\u00eb\u00d1Ttm {\u0280\u1d00\u026a\u1d0f\u0274\u1d0b\u1d07\u1d0b} \u30c3 \uff32\uff10\uff10\uff34\uff5c \u1d20\u1d0f\u026a\u1d05 \u1d04\u029f\u026a\u1d07\u0274\u1d1b \uff5c \u1d0b\u1d00\u1d0d\u026a \uff5c \u02b3\u1d1c\u02e2\u02b0\u1d07\u02b3\u02b0\u1d00\u1d9c\u1d4f \uff5c \u24cc\u24cc\u24ba \uff5c \uff49\uff4d\uff50\uff41\uff43\uff54\uff5c \ua730\u1d1c\u1d1b\u1d1c\u0280\u1d07tm \u24d6\u24de\u24db\u24d3 \uff5c \u5342\u4e02\u3129\u51e0\u5342\u1d04\u029f\u026a\u1d07\u0274\u1d1b \uff5c\ua118\ua182\ua493\ua04d\ua35f\ua45b\ua2eb\ua1d3 \uff5c \u02b3\u1d00\uff54\u1d04\u029f\u026a\u1d07\u0274\u1d1b \uff5c \u1d00\u1d18\u1d0f\u029f\u029f\u028f\u1d0f\u0274 \uff5c \u1d0b\u1d00\u1d0d\u026a \u0299\u029f\u1d1c\u1d07 \u2758 \u1d3e\u1d3c\u1d3e\u1d2e\u1d3c\u1d2e \u1d9c\u1d38\u1d35\u1d31\u1d3a\u1d40 \u23d0 \u0262\u1d00\u028f \u23d0  c l i e n t |  B a L l C l i E n T\u00bb \u028c\u0433\u1d07\u0455+ \u00ab \u1d0b\u1d00\u1d0d\u026a \u0299\u029f\u1d1c\u1d07 \u1d0f\u0274 \u1d1b\u1d0f\u1d18 \u00bb \u00bb \u02e2\u207f\u1d52\u02b7\u23d0 \u041d\u03b5\u13ae\u043d\u15e9\u03b5\u0455\u01ad\u03c5\u0455 \u00bb \u0299\u1d00\u1d04\u1d0b\u1d05\u1d0f\u1d0f\u0280\u1d07\u1d05 | \u1d0d\u1d07\u1d0f\u1d21 \u00bb \u1d1c\u0274\u026a\u1d04\u1d0f\u0280\u0274\u0262\u1d0f\u1d05.\u0262\u0262  \ua731\u1d07\u1d18\u1d18\u1d1c\u1d0b\u1d1c | \u029c\u1d1c\u1d22\u1d1c\u0274\u026a\u0262\u0280\u1d07\u1d07\u0274.\u0262\u0262tm \u00bb \u0299\u1d00\u1d04\u1d0b\u1d04\u029f\u026a\u1d07\u0274\u1d1btm \u00bb \u0274\u1d0f\u1d1c \u029f\u1d07\u1d00\u1d0b  \u23d0 \u0493\u1d0f\u0280\u0262\u1d07\u0280\u1d00\u1d1b \u2661 | \u04e8B\u039bM\u039b \u1103\u1102I\u03a3\u041f\u01ac - \u1d07\u029f\u1d07\u1d0d\u1d07\u0274\u1d1b\u1d00\u0280\ua731.\u1d04\u1d0f\u1d0d \u300b\u1d0f\ua731\u026a\u0280\u026a\ua731 | W\u00d4\u00d4K\u00cf\u00ca \u00c7L\u00ee\u00eb\u00d1Ttm {\u0280\u1d00\u026a\u1d0f\u0274\u1d0b\u1d07\u1d0b} \u30c3 \uff32\uff10\uff10\uff34\uff5c \u1d20\u1d0f\u026a\u1d05 \u1d04\u029f\u026a\u1d07\u0274\u1d1b \uff5c \u1d0b\u1d00\u1d0d\u026a \uff5c \u02b3\u1d1c\u02e2\u02b0\u1d07\u02b3\u02b0\u1d00\u1d9c\u1d4f \uff5c \u24cc\u24cc\u24ba \uff5c \uff49\uff4d\uff50\uff41\uff43\uff54\uff5c \ua730\u1d1c\u1d1b\u1d1c\u0280\u1d07tm \u24d6\u24de\u24db\u24d3 \uff5c \u5342\u4e02\u3129\u51e0\u5342\u1d04\u029f\u026a\u1d07\u0274\u1d1b \uff5c\ua118\ua182\ua493\ua04d\ua35f\ua45b\ua2eb\ua1d3 \uff5c \u02b3\u1d00\uff54\u1d04\u029f\u026a\u1d07\u0274\u1d1b \uff5c \u1d00\u1d18\u1d0f\u029f\u029f\u028f\u1d0f\u0274 \uff5c \u1d0b\u1d00\u1d0d\u026a \u0299\u029f\u1d1c\u1d07 \u2758 \u1d3e\u1d3c\u1d3e\u1d2e\u1d3c\u1d2e \u1d9c\u1d38\u1d35\u1d31\u1d3a\u1d40 \u23d0 \u0262\u1d00\u028f \u23d0  c l i e n t |  B a L l C l i E n T | Phobos1.5.4.eu | popbobhack | .grabcoords | faxhax";
                    break;
                }
                case CUSTOM: {
                    s = s + this.customSuffix.getValue();
                }
            }
            if (s.length() >= 256) {
                s = s.substring(0, 256);
            }
            packet.message = s;
        }
    }

    @SubscribeEvent
    public void onChatPacketReceive(PacketEvent.Receive event) {
        if (event.getStage() == 0) {
            event.getPacket();
        }
    }

    @SubscribeEvent
    public void onPacketReceive(PacketEvent.Receive event) {
        if (event.getStage() == 0 && this.timeStamps.getValue() != TextUtil.Color.NONE && event.getPacket() instanceof SPacketChat) {
            if (!((SPacketChat)event.getPacket()).isSystem()) {
                return;
            }
            String originalMessage = ((SPacketChat)event.getPacket()).chatComponent.getFormattedText();
            String message = this.getTimeString(originalMessage) + originalMessage;
            ((SPacketChat)event.getPacket()).chatComponent = new TextComponentString(message);
        }
    }

    public String getTimeString(String message) {
        String date = new SimpleDateFormat("k:mm").format(new Date());
        if (this.rainbowTimeStamps.getValue().booleanValue()) {
            String timeString = "<" + date + ">" + (this.space.getValue() != false ? " " : "");
            StringBuilder builder = new StringBuilder(timeString);
            builder.insert(0, "\u00a7+");
            if (!message.contains(Management.getInstance().getRainbowCommandMessage())) {
                builder.append("\u00a7r");
            }
            return builder.toString();
        }
        return (this.bracket.getValue() == TextUtil.Color.NONE ? "" : TextUtil.coloredString("<", this.bracket.getValue())) + TextUtil.coloredString(date, this.timeStamps.getValue()) + (this.bracket.getValue() == TextUtil.Color.NONE ? "" : TextUtil.coloredString(">", this.bracket.getValue())) + (this.space.getValue() != false ? " " : "") + "\u00a7r";
    }

    private boolean shouldSendMessage(EntityPlayer player) {
        if (player.dimension != 1) {
            return false;
        }
        if (!this.timer.passedS(this.qDelay.getValue().intValue())) {
            return false;
        }
        return player.getPosition().equals((Object)new Vec3i(0, 240, 0));
    }

    public static enum Suffix {
        NONE,
        PHOBOS,
        EARTH,
        PERRYPHOBOS,
        CUSTOM,
        INSANE;

    }
}

