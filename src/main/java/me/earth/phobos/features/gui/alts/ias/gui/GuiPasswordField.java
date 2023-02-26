
/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  joptsimple.internal.Strings
 *  net.minecraft.client.gui.FontRenderer
 *  net.minecraft.client.gui.GuiScreen
 *  net.minecraft.client.gui.GuiTextField
 */
package me.earth.phobos.features.gui.alts.ias.gui;

import joptsimple.internal.Strings;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;

public class GuiPasswordField
extends GuiTextField {
    public GuiPasswordField(int componentId, FontRenderer fontrendererObj, int x, int y, int par5Width, int par6Height) {
        super(componentId, fontrendererObj, x, y, par5Width, par6Height);
    }

    public void drawTextBox() {
        String password = this.getText();
        this.replaceText(Strings.repeat((char)'*', (int)this.getText().length()));
        super.drawTextBox();
        this.replaceText(password);
    }

    public boolean textboxKeyTyped(char typedChar, int keyCode) {
        return !GuiScreen.isKeyComboCtrlC((int)keyCode) && !GuiScreen.isKeyComboCtrlX((int)keyCode) && super.textboxKeyTyped(typedChar, keyCode);
    }

    public boolean mouseClicked(int mouseX, int mouseY, int mouseButton) {
        String password = this.getText();
        this.replaceText(Strings.repeat((char)'*', (int)this.getText().length()));
        super.mouseClicked(mouseX, mouseY, mouseButton);
        this.replaceText(password);
        return true;
    }

    private void replaceText(String newText) {
        int cursorPosition = this.getCursorPosition();
        int selectionEnd = this.getSelectionEnd();
        this.setText(newText);
        this.setCursorPosition(cursorPosition);
        this.setSelectionPos(selectionEnd);
    }
}

