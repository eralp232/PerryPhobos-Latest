
/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.gui.FontRenderer
 *  net.minecraft.client.gui.GuiButton
 *  net.minecraft.client.gui.GuiNewChat
 *  net.minecraft.client.gui.GuiScreen
 *  net.minecraft.client.gui.GuiUtilRenderComponents
 *  net.minecraft.client.gui.ScaledResolution
 *  net.minecraft.client.renderer.GlStateManager
 *  net.minecraft.client.resources.I18n
 *  net.minecraft.util.math.MathHelper
 *  net.minecraft.util.text.ITextComponent
 *  net.minecraft.util.text.TextComponentString
 *  net.minecraft.util.text.TextFormatting
 *  net.minecraftforge.fml.client.config.GuiSlider
 */
package me.earth.phobos.features.gui.chat.gui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import me.earth.phobos.features.gui.chat.BetterChat;
import me.earth.phobos.features.gui.chat.ChatSettings;
import me.earth.phobos.features.gui.chat.handlers.InjectHandler;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiNewChat;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiUtilRenderComponents;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.client.config.GuiSlider;

public class GuiConfig
extends GuiScreen {
    private final ChatSettings settings;
    private final List<ITextComponent> exampleChat = new ArrayList<ITextComponent>();
    private boolean dragging;
    private int chatLeft;
    private int chatRight;
    private int chatTop;
    private int chatBottom;
    private int dragStartX;
    private int dragStartY;
    private GuiButton clearButton;
    private GuiButton smoothButton;
    private GuiSlider scaleSlider;
    private GuiSlider widthSlider;

    public GuiConfig() {
        this.settings = BetterChat.getSettings();
        this.exampleChat.add((ITextComponent)new TextComponentString(I18n.format((String)"gui.betterchat.text.example3", (Object[])new Object[0])));
        this.exampleChat.add((ITextComponent)new TextComponentString(I18n.format((String)"gui.betterchat.text.example2", (Object[])new Object[0])));
        this.exampleChat.add((ITextComponent)new TextComponentString(I18n.format((String)"gui.betterchat.text.example1", (Object[])new Object[0])));
    }

    public void initGui() {
        InjectHandler.chatGUI.configuring = true;
        this.clearButton = new GuiButton(0, this.width / 2 - 120, this.height / 2 - 50, 240, 20, this.getPropName("clear") + " " + this.getColoredBool("clear", this.settings.clear));
        this.buttonList.add(this.clearButton);
        this.smoothButton = new GuiButton(1, this.width / 2 - 120, this.height / 2 - 25, 240, 20, this.getPropName("smooth") + " " + this.getColoredBool("smooth", this.settings.smooth));
        this.buttonList.add(this.smoothButton);
        this.scaleSlider = new GuiSlider(3, this.width / 2 - 120, this.height / 2, 240, 20, this.getPropName("scale") + " ", "%", 0.0, 100.0, (double)(this.mc.gameSettings.chatScale * 100.0f), false, true);
        this.buttonList.add(this.scaleSlider);
        this.widthSlider = new GuiSlider(4, this.width / 2 - 120, this.height / 2 + 25, 240, 20, this.getPropName("width") + " ", "px", 40.0, 320.0, (double)GuiNewChat.calculateChatboxWidth((float)this.mc.gameSettings.chatWidth), false, true);
        this.buttonList.add(this.widthSlider);
        this.buttonList.add(new GuiButton(2, this.width / 2 - 120, this.height / 2 + 50, 240, 20, this.getPropName("reset")));
    }

    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        super.drawScreen(mouseX, mouseY, partialTicks);
        this.drawCenteredString(this.mc.fontRenderer, I18n.format((String)"gui.betterchat.text.title", (Object[])new Object[]{TextFormatting.GREEN + TextFormatting.BOLD.toString() + "Better Chat" + TextFormatting.RESET, TextFormatting.AQUA + TextFormatting.BOLD.toString() + "LlamaLad7"}), this.width / 2, this.height / 2 - 75, 0xFFFFFF);
        this.drawCenteredString(this.mc.fontRenderer, I18n.format((String)"gui.betterchat.text.drag", (Object[])new Object[0]), this.width / 2, this.height / 2 - 63, 0xFFFFFF);
        if (this.dragging) {
            this.settings.xOffset += mouseX - this.dragStartX;
            this.settings.yOffset += mouseY - this.dragStartY;
            this.dragStartX = mouseX;
            this.dragStartY = mouseY;
        }
        this.mc.gameSettings.chatScale = (float)this.scaleSlider.getValueInt() / 100.0f;
        this.mc.gameSettings.chatWidth = ((float)this.widthSlider.getValueInt() - 40.0f) / 280.0f;
        this.drawExampleChat();
    }

    public void drawExampleChat() {
        ArrayList<ITextComponent> lines = new ArrayList();
        int i = MathHelper.floor((float)((float)InjectHandler.chatGUI.getChatWidth() / InjectHandler.chatGUI.getChatScale()));
        for (ITextComponent line : this.exampleChat) {
            lines.addAll(GuiUtilRenderComponents.splitText((ITextComponent)line, (int)i, (FontRenderer)this.mc.fontRenderer, (boolean)false, (boolean)false));
        }
        Collections.reverse(lines);
        GlStateManager.pushMatrix();
        ScaledResolution scaledresolution = new ScaledResolution(this.mc);
        GlStateManager.translate((float)(2.0f + (float)this.settings.xOffset), (float)(8.0f + (float)this.settings.yOffset + (float)scaledresolution.getScaledHeight() - 48.0f), (float)0.0f);
        float f = this.mc.gameSettings.chatOpacity * 0.9f + 0.1f;
        float f1 = this.mc.gameSettings.chatScale;
        int k = MathHelper.ceil((float)((float)InjectHandler.chatGUI.getChatWidth() / f1));
        GlStateManager.scale((float)f1, (float)f1, (float)1.0f);
        int i1 = 0;
        double d0 = 1.0;
        int l1 = (int)(255.0 * d0);
        l1 = (int)((float)l1 * f);
        GlStateManager.enableAlpha();
        GlStateManager.enableBlend();
        this.chatLeft = this.settings.xOffset;
        this.chatRight = (int)((float)this.settings.xOffset + (float)(k + 4) * f1);
        this.chatBottom = 8 + this.settings.yOffset + scaledresolution.getScaledHeight() - 48;
        for (ITextComponent message : lines) {
            int j2 = -i1 * 9;
            if (!this.settings.clear) {
                GuiConfig.drawRect((int)-2, (int)(j2 - 9), (int)(k + 4), (int)j2, (int)(l1 / 2 << 24));
            }
            this.mc.fontRenderer.drawStringWithShadow(message.getFormattedText(), 0.0f, (float)(j2 - 8), 0xFFFFFF + (l1 << 24));
            ++i1;
        }
        this.chatTop = (int)((float)(8 + this.settings.yOffset + scaledresolution.getScaledHeight() - 48) + (float)(-i1 * 9) * f1);
        GlStateManager.disableAlpha();
        GlStateManager.disableBlend();
        GlStateManager.popMatrix();
    }

    public void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        if (mouseButton == 0 && mouseX >= this.chatLeft && mouseX <= this.chatRight && mouseY >= this.chatTop && mouseY <= this.chatBottom) {
            this.dragging = true;
            this.dragStartX = mouseX;
            this.dragStartY = mouseY;
        }
    }

    public void mouseReleased(int mouseX, int mouseY, int mouseButton) {
        super.mouseReleased(mouseX, mouseY, mouseButton);
        this.dragging = false;
    }

    public void onGuiClosed() {
        this.settings.saveConfig();
        InjectHandler.chatGUI.configuring = false;
        this.mc.gameSettings.saveOptions();
    }

    protected void actionPerformed(GuiButton button) {
        switch (button.id) {
            case 0: {
                this.settings.clear = !this.settings.clear;
                button.displayString = this.getPropName("clear") + " " + this.getColoredBool("clear", this.settings.clear);
                break;
            }
            case 1: {
                this.settings.smooth = !this.settings.smooth;
                button.displayString = this.getPropName("smooth") + " " + this.getColoredBool("smooth", this.settings.smooth);
                break;
            }
            case 2: {
                this.settings.resetConfig();
                this.clearButton.displayString = this.getPropName("clear") + " " + this.getColoredBool("clear", this.settings.clear);
                this.smoothButton.displayString = this.getPropName("smooth") + " " + this.getColoredBool("smooth", this.settings.smooth);
                this.scaleSlider.setValue((double)(this.mc.gameSettings.chatScale * 100.0f));
                this.scaleSlider.updateSlider();
                this.widthSlider.setValue((double)GuiNewChat.calculateChatboxWidth((float)this.mc.gameSettings.chatWidth));
                this.widthSlider.updateSlider();
            }
        }
    }

    public boolean doesGuiPauseGame() {
        return false;
    }

    private String getColoredBool(String prop, boolean bool) {
        if (bool) {
            return TextFormatting.GREEN + I18n.format((String)("gui.betterchat.text." + prop + ".enabled"), (Object[])new Object[0]);
        }
        return TextFormatting.RED + I18n.format((String)("gui.betterchat.text." + prop + ".disabled"), (Object[])new Object[0]);
    }

    private String getPropName(String prop) {
        return I18n.format((String)("gui.betterchat.text." + prop + ".name"), (Object[])new Object[0]);
    }
}

