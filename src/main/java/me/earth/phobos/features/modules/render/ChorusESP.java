/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  net.minecraft.util.math.AxisAlignedBB
 *  net.minecraftforge.fml.common.eventhandler.SubscribeEvent
 */
package me.earth.phobos.features.modules.render;

import java.awt.Color;
import me.earth.phobos.event.events.ChorusEvent;
import me.earth.phobos.event.events.Render3DEvent;
import me.earth.phobos.features.modules.Module;
import me.earth.phobos.features.setting.Setting;
import me.earth.phobos.util.ColorUtil;
import me.earth.phobos.util.RenderUtil;
import me.earth.phobos.util.TimerUtil;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class ChorusESP
extends Module {
    private final Setting<Integer> time = this.register(new Setting<Integer>("Duration", 500, 50, 3000));
    private final Setting<Boolean> box = this.register(new Setting<Boolean>("Box", true));
    private final Setting<Boolean> outline = this.register(new Setting<Boolean>("Outline", true));
    private final Setting<Integer> boxR = this.register(new Setting<Integer>("BoxR", Integer.valueOf(180), Integer.valueOf(0), Integer.valueOf(255), v -> this.box.getValue()));
    private final Setting<Integer> boxG = this.register(new Setting<Integer>("BoxG", Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(255), v -> this.box.getValue()));
    private final Setting<Integer> boxB = this.register(new Setting<Integer>("BoxB", Integer.valueOf(180), Integer.valueOf(0), Integer.valueOf(255), v -> this.box.getValue()));
    private final Setting<Integer> boxA = this.register(new Setting<Integer>("BoxA", Integer.valueOf(180), Integer.valueOf(0), Integer.valueOf(255), v -> this.box.getValue()));
    private final Setting<Float> lineWidth = this.register(new Setting<Float>("LineWidth", Float.valueOf(1.0f), Float.valueOf(0.1f), Float.valueOf(5.0f), v -> this.outline.getValue()));
    private final Setting<Integer> outlineR = this.register(new Setting<Integer>("OutlineR", Integer.valueOf(255), Integer.valueOf(0), Integer.valueOf(255), v -> this.outline.getValue()));
    private final Setting<Integer> outlineG = this.register(new Setting<Integer>("OutlineG", Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(255), v -> this.outline.getValue()));
    private final Setting<Integer> outlineB = this.register(new Setting<Integer>("OutlineB", Integer.valueOf(255), Integer.valueOf(0), Integer.valueOf(255), v -> this.outline.getValue()));
    private final Setting<Integer> outlineA = this.register(new Setting<Integer>("OutlineA", Integer.valueOf(255), Integer.valueOf(0), Integer.valueOf(255), v -> this.outline.getValue()));
    private final TimerUtil timer = new TimerUtil();
    private double x;
    private double y;
    private double z;

    public ChorusESP() {
        super("ChorusESP", "Renders a chorus sound packet.", Module.Category.RENDER, true, false, false);
    }

    @SubscribeEvent
    public void onChorus(ChorusEvent event) {
        this.x = event.getChorusX();
        this.y = event.getChorusY();
        this.z = event.getChorusZ();
        this.timer.reset();
    }

    @Override
    public void onRender3D(Render3DEvent render3DEvent) {
        if (this.timer.passedMs(this.time.getValue().intValue())) {
            return;
        }
        AxisAlignedBB pos = RenderUtil.interpolateAxis(new AxisAlignedBB(this.x - 0.3, this.y, this.z - 0.3, this.x + 0.3, this.y + 1.8, this.z + 0.3));
        if (this.outline.getValue().booleanValue()) {
            RenderUtil.drawBlockOutline(pos, new Color(this.outlineR.getValue(), this.outlineG.getValue(), this.outlineB.getValue(), this.outlineA.getValue()), this.lineWidth.getValue().floatValue());
        }
        if (this.box.getValue().booleanValue()) {
            RenderUtil.drawFilledBox(pos, ColorUtil.toRGBA(this.boxR.getValue(), this.boxG.getValue(), this.boxB.getValue(), this.boxA.getValue()));
        }
    }
}

