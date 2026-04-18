package net.quepierts.thatskyinteractions.client.gui.layer;

import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.LayeredDraw;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.quepierts.thatskyinteractions.ThatSkyInteractions;
import net.quepierts.thatskyinteractions.client.gui.SdfGraphics;
import net.quepierts.thatskyinteractions.client.gui.animate.AnimateUtils;
import net.quepierts.thatskyinteractions.client.gui.animate.LerpNumberAnimation;
import net.quepierts.thatskyinteractions.client.gui.animate.ScreenAnimator;
import net.quepierts.thatskyinteractions.client.gui.animate.WaitAnimation;
import net.quepierts.thatskyinteractions.client.gui.holder.FloatHolder;
import net.quepierts.thatskyinteractions.client.util.RenderUtils;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

public class PromptMessageLayer implements LayeredDraw.Layer {
    public static final int BG_COLOR = 0xc0101010;

    public static final PromptMessageLayer INSTANCE = new PromptMessageLayer();
    public static final ResourceLocation LOCATION = ThatSkyInteractions.getLocation("prompt");

    private static final float TRANSITION_TIME = 0.6f;

    private int time = 60;
    private int timer = 0;
    private int length = 0;
    private boolean started = false;
    private Component component;
    private String src = "";

    private final FloatHolder scale = new FloatHolder(0.0f);
    private final LerpNumberAnimation transition = new LerpNumberAnimation(scale, PromptMessageLayer::lerp, 0, 1, TRANSITION_TIME);
    private final WaitAnimation enter = new WaitAnimation(TRANSITION_TIME, this::begin);
    private final WaitAnimation clear = new WaitAnimation(TRANSITION_TIME, this::cleanup);

    PromptMessageLayer() {}

    @Override
    public void render(
            @NotNull GuiGraphics guiGraphics,
            @NotNull DeltaTracker deltaTracker
    ) {
        if (this.component == null) {
            return;
        }

        if (this.started) {
            this.timer ++;

            if (this.timer > time) {
                this.started = false;
                this.transition.reset(1, 0);
                ScreenAnimator.GLOBAL.play(this.transition);
                ScreenAnimator.GLOBAL.play(this.clear);
            }
        }

        Minecraft minecraft = Minecraft.getInstance();
        Window window = minecraft.getWindow();
        int width = window.getGuiScaledWidth();
        int halfWidth = width / 2;
        int height = window.getGuiScaledHeight();

        float scale = this.scale.getValue();

        PoseStack pose = guiGraphics.pose();
        pose.pushPose();
        pose.translate(halfWidth, height - 48, 0);
        pose.scale(scale, scale, scale);
        RenderSystem.enableBlend();
        int width1 = this.length * 2;

        SdfGraphics .getInstance()
                    .color(BG_COLOR)
                    .round(4)
                    .rectangle(-this.length, -16, width1, 20)
                    .fill(pose);

        guiGraphics.drawString(minecraft.font, this.component, -this.length + 12, -10, 0xffffffff);
        RenderSystem.disableBlend();
        pose.popPose();
    }

    public void setOrContinue(@NotNull Supplier<Component> supplier, @NotNull String src, int time) {
        if (!this.src.equals(src)) {
            this.setMessage(supplier.get(), src, time);
        } else {
            this.timer = 0;
        }
    }

    public void stop() {
        this.timer = this.time;
    }

    public void setMessage(@NotNull Component component, @NotNull String src, int time) {
        this.transition.reset(0, 1);
        ScreenAnimator.GLOBAL.remove(this.clear);
        ScreenAnimator.GLOBAL.play(this.transition);
        ScreenAnimator.GLOBAL.play(this.enter);
        this.src = src;
        this.component = component;
        this.length = Minecraft.getInstance().font.width(component) / 2 + 12;
        this.started = false;
        this.time = time;
        this.timer = 0;
    }

    private void begin() {
        this.started = true;
    }

    public void cleanup() {
        this.component = null;
        this.src = "";
        this.started = false;
        this.timer = 0;
    }

    private static double lerp(double src, double dest, float time) {
        float t = (float) AnimateUtils.Lerp.linear(src, dest, time);
        float t2 = t * t;
        return 8.4f * t -17.9f * t2 + 14.6f * t2 * t - 4.1f * t2 * t2;
    }
}
