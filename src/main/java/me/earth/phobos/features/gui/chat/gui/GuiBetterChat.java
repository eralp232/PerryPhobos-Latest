
/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  javax.annotation.Nullable
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.gui.ChatLine
 *  net.minecraft.client.gui.FontRenderer
 *  net.minecraft.client.gui.GuiChat
 *  net.minecraft.client.gui.GuiNewChat
 *  net.minecraft.client.gui.GuiUtilRenderComponents
 *  net.minecraft.client.gui.ScaledResolution
 *  net.minecraft.client.renderer.GlStateManager
 *  net.minecraft.entity.player.EntityPlayer$EnumChatVisibility
 *  net.minecraft.util.math.MathHelper
 *  net.minecraft.util.text.ITextComponent
 *  net.minecraft.util.text.TextComponentString
 *  net.minecraftforge.fml.relauncher.Side
 *  net.minecraftforge.fml.relauncher.SideOnly
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package me.earth.phobos.features.gui.chat.gui;

import com.google.common.collect.Lists;
import java.util.Iterator;
import java.util.List;
import javax.annotation.Nullable;
import me.earth.phobos.features.gui.chat.BetterChat;
import me.earth.phobos.features.gui.chat.utils.AnimationTools;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ChatLine;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.GuiNewChat;
import net.minecraft.client.gui.GuiUtilRenderComponents;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@SideOnly(value=Side.CLIENT)
public class GuiBetterChat
extends GuiNewChat {
    private static final Logger LOGGER = LogManager.getLogger();
    public static float percentComplete;
    public static int newLines;
    public static long prevMillis;
    private final Minecraft mc;
    private final List<String> sentMessages = Lists.newArrayList();
    private final List<ChatLine> chatLines = Lists.newArrayList();
    private final List<ChatLine> drawnChatLines = Lists.newArrayList();
    public boolean configuring;
    private int scrollPos;
    private boolean isScrolled;

    public GuiBetterChat(Minecraft mcIn) {
        super(mcIn);
        this.mc = mcIn;
    }

    public static void updatePercentage(long diff) {
        if (percentComplete < 1.0f) {
            percentComplete += 0.004f * (float)diff;
        }
        percentComplete = AnimationTools.clamp(percentComplete, 0.0f, 1.0f);
    }

    public static int calculateChatboxWidth(float scale) {
        return MathHelper.floor((float)(scale * 280.0f + 40.0f));
    }

    public static int calculateChatboxHeight(float scale) {
        return MathHelper.floor((float)(scale * 160.0f + 20.0f));
    }

    public void drawChat(int updateCounter) {
        if (this.configuring) {
            return;
        }
        if (prevMillis == -1L) {
            prevMillis = System.currentTimeMillis();
            return;
        }
        long current = System.currentTimeMillis();
        long diff = current - prevMillis;
        prevMillis = current;
        GuiBetterChat.updatePercentage(diff);
        float t = percentComplete;
        float percent = 1.0f - (t -= 1.0f) * t * t * t;
        percent = AnimationTools.clamp(percent, 0.0f, 1.0f);
        if (this.mc.gameSettings.chatVisibility != EntityPlayer.EnumChatVisibility.HIDDEN) {
            int i = this.getLineCount();
            int j = this.drawnChatLines.size();
            float f = this.mc.gameSettings.chatOpacity * 0.9f + 0.1f;
            if (j > 0) {
                boolean flag = this.getChatOpen();
                float f1 = this.getChatScale();
                int k = MathHelper.ceil((float)((float)this.getChatWidth() / f1));
                GlStateManager.pushMatrix();
                if (BetterChat.getSettings().smooth && !this.isScrolled) {
                    GlStateManager.translate((float)(2.0f + (float)BetterChat.getSettings().xOffset), (float)(8.0f + (float)BetterChat.getSettings().yOffset + (9.0f - 9.0f * percent) * f1), (float)0.0f);
                } else {
                    GlStateManager.translate((float)(2.0f + (float)BetterChat.getSettings().xOffset), (float)(8.0f + (float)BetterChat.getSettings().yOffset), (float)0.0f);
                }
                GlStateManager.scale((float)f1, (float)f1, (float)1.0f);
                int l = 0;
                for (int i1 = 0; i1 + this.scrollPos < this.drawnChatLines.size() && i1 < i; ++i1) {
                    int j1;
                    ChatLine chatline = this.drawnChatLines.get(i1 + this.scrollPos);
                    if (chatline == null || (j1 = updateCounter - chatline.getUpdatedCounter()) >= 200 && !flag) continue;
                    double d0 = (double)j1 / 200.0;
                    d0 = 1.0 - d0;
                    d0 *= 10.0;
                    d0 = MathHelper.clamp((double)d0, (double)0.0, (double)1.0);
                    d0 *= d0;
                    int l1 = (int)(255.0 * d0);
                    if (flag) {
                        l1 = 255;
                    }
                    l1 = (int)((float)l1 * f);
                    ++l;
                    if (l1 <= 3) continue;
                    int i2 = 0;
                    int j2 = -i1 * 9;
                    if (!BetterChat.getSettings().clear) {
                        GuiBetterChat.drawRect((int)-2, (int)(j2 - 9), (int)(i2 + k + 4), (int)j2, (int)(l1 / 2 << 24));
                    }
                    String s = chatline.getChatComponent().getFormattedText();
                    GlStateManager.enableBlend();
                    if (BetterChat.getSettings().smooth && i1 <= newLines) {
                        this.mc.fontRenderer.drawStringWithShadow(s, 0.0f, (float)(j2 - 8), 0xFFFFFF + ((int)((float)l1 * percent) << 24));
                    } else {
                        this.mc.fontRenderer.drawStringWithShadow(s, (float)i2, (float)(j2 - 8), 0xFFFFFF + (l1 << 24));
                    }
                    GlStateManager.disableAlpha();
                    GlStateManager.disableBlend();
                }
                if (flag) {
                    int k2 = this.mc.fontRenderer.FONT_HEIGHT;
                    GlStateManager.translate((float)-3.0f, (float)0.0f, (float)0.0f);
                    int l2 = j * k2 + j;
                    int i3 = l * k2 + l;
                    int j3 = this.scrollPos * i3 / j;
                    int k1 = i3 * i3 / l2;
                    if (l2 != i3) {
                        int k3 = j3 > 0 ? 170 : 96;
                        int l3 = this.isScrolled ? 0xCC3333 : 0x3333AA;
                        GuiBetterChat.drawRect((int)0, (int)(-j3), (int)2, (int)(-j3 - k1), (int)(l3 + (k3 << 24)));
                        GuiBetterChat.drawRect((int)2, (int)(-j3), (int)1, (int)(-j3 - k1), (int)(0xCCCCCC + (k3 << 24)));
                    }
                }
                GlStateManager.popMatrix();
            }
        }
    }

    public void clearChatMessages(boolean p_146231_1_) {
        this.drawnChatLines.clear();
        this.chatLines.clear();
        if (p_146231_1_) {
            this.sentMessages.clear();
        }
    }

    public void printChatMessage(ITextComponent chatComponent) {
        this.printChatMessageWithOptionalDeletion(chatComponent, 0);
    }

    public void printChatMessageWithOptionalDeletion(ITextComponent chatComponent, int chatLineId) {
        percentComplete = 0.0f;
        this.setChatLine(chatComponent, chatLineId, this.mc.ingameGUI.getUpdateCounter(), false);
        LOGGER.info("[CHAT] {}", (Object)chatComponent.getUnformattedText().replaceAll("\r", "\\\\r").replaceAll("\n", "\\\\n"));
    }

    private void setChatLine(ITextComponent chatComponent, int chatLineId, int updateCounter, boolean displayOnly) {
        if (chatLineId != 0) {
            this.deleteChatLine(chatLineId);
        }
        int i = MathHelper.floor((float)((float)this.getChatWidth() / this.getChatScale()));
        List<ITextComponent> list = GuiUtilRenderComponents.splitText((ITextComponent)chatComponent, (int)i, (FontRenderer)this.mc.fontRenderer, (boolean)false, (boolean)false);
        boolean flag = this.getChatOpen();
        newLines = list.size() - 1;
        for (ITextComponent itextcomponent : list) {
            if (flag && this.scrollPos > 0) {
                this.isScrolled = true;
                this.scroll(1);
            }
            this.drawnChatLines.add(0, new ChatLine(updateCounter, itextcomponent, chatLineId));
        }
        while (this.drawnChatLines.size() > 100) {
            this.drawnChatLines.remove(this.drawnChatLines.size() - 1);
        }
        if (!displayOnly) {
            this.chatLines.add(0, new ChatLine(updateCounter, chatComponent, chatLineId));
            while (this.chatLines.size() > 100) {
                this.chatLines.remove(this.chatLines.size() - 1);
            }
        }
    }

    public void refreshChat() {
        this.drawnChatLines.clear();
        this.resetScroll();
        for (int i = this.chatLines.size() - 1; i >= 0; --i) {
            ChatLine chatline = this.chatLines.get(i);
            this.setChatLine(chatline.getChatComponent(), chatline.getChatLineID(), chatline.getUpdatedCounter(), true);
        }
    }

    public List<String> getSentMessages() {
        return this.sentMessages;
    }

    public void addToSentMessages(String message) {
        if (this.sentMessages.isEmpty() || !this.sentMessages.get(this.sentMessages.size() - 1).equals(message)) {
            this.sentMessages.add(message);
        }
    }

    public void resetScroll() {
        this.scrollPos = 0;
        this.isScrolled = false;
    }

    public void scroll(int amount) {
        this.scrollPos += amount;
        int i = this.drawnChatLines.size();
        if (this.scrollPos > i - this.getLineCount()) {
            this.scrollPos = i - this.getLineCount();
        }
        if (this.scrollPos <= 0) {
            this.scrollPos = 0;
            this.isScrolled = false;
        }
    }

    @Nullable
    public ITextComponent getChatComponent(int mouseX, int mouseY) {
        if (!this.getChatOpen()) {
            return null;
        }
        ScaledResolution scaledresolution = new ScaledResolution(this.mc);
        int i = scaledresolution.getScaleFactor();
        float f = this.getChatScale();
        int j = mouseX / i - 2 - BetterChat.getSettings().xOffset;
        int k = mouseY / i - 40 + BetterChat.getSettings().yOffset;
        j = MathHelper.floor((float)((float)j / f));
        k = MathHelper.floor((float)((float)k / f));
        if (j >= 0 && k >= 0) {
            int l = Math.min(this.getLineCount(), this.drawnChatLines.size());
            if (j <= MathHelper.floor((float)((float)this.getChatWidth() / this.getChatScale())) && k < this.mc.fontRenderer.FONT_HEIGHT * l + l) {
                int i1 = k / this.mc.fontRenderer.FONT_HEIGHT + this.scrollPos;
                if (i1 >= 0 && i1 < this.drawnChatLines.size()) {
                    ChatLine chatline = this.drawnChatLines.get(i1);
                    int j1 = 0;
                    for (ITextComponent itextcomponent : chatline.getChatComponent()) {
                        if (!(itextcomponent instanceof TextComponentString) || (j1 += this.mc.fontRenderer.getStringWidth(GuiUtilRenderComponents.removeTextColorsIfConfigured((String)((TextComponentString)itextcomponent).getText(), (boolean)false))) <= j) continue;
                        return itextcomponent;
                    }
                }
                return null;
            }
            return null;
        }
        return null;
    }

    public boolean getChatOpen() {
        return this.mc.currentScreen instanceof GuiChat;
    }

    public void deleteChatLine(int id) {
        Iterator<ChatLine> iterator = this.drawnChatLines.iterator();
        while (iterator.hasNext()) {
            ChatLine chatline = iterator.next();
            if (chatline.getChatLineID() != id) continue;
            iterator.remove();
        }
        iterator = this.chatLines.iterator();
        while (iterator.hasNext()) {
            ChatLine chatline1 = iterator.next();
            if (chatline1.getChatLineID() != id) continue;
            iterator.remove();
            break;
        }
    }

    public int getChatWidth() {
        return GuiBetterChat.calculateChatboxWidth(this.mc.gameSettings.chatWidth);
    }

    public int getChatHeight() {
        return GuiBetterChat.calculateChatboxHeight(this.getChatOpen() ? this.mc.gameSettings.chatHeightFocused : this.mc.gameSettings.chatHeightUnfocused);
    }

    public float getChatScale() {
        return this.mc.gameSettings.chatScale;
    }

    public int getLineCount() {
        return this.getChatHeight() / 9;
    }

    static {
        prevMillis = -1L;
    }
}

