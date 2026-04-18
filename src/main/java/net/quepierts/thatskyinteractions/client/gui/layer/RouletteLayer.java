package net.quepierts.thatskyinteractions.client.gui.layer;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.LayeredDraw;
import net.minecraft.util.Mth;
import net.quepierts.thatskyinteractions.client.ClientHelper;
import net.quepierts.thatskyinteractions.client.gui.Palette;
import net.quepierts.thatskyinteractions.client.gui.SdfGraphics;
import net.quepierts.thatskyinteractions.client.util.RenderUtils;
import org.jetbrains.annotations.NotNull;

public class RouletteLayer implements LayeredDraw.Layer {
    public static final RouletteLayer INSTANCE = new RouletteLayer();
    RouletteLayer() {}

    private boolean wasEnabled = false;


    private float x;
    private float y;

    public void onMouseMove(float deltaX, float deltaY) {
        if (ClientHelper.isRouletteOpen()) {
            double sensitivity = Minecraft.getInstance().options.sensitivity().get() * 0.6000000238418579 + 0.20000000298023224;
            float s3 = (float) (sensitivity * sensitivity * sensitivity);
            this.x += deltaX * s3;
            this.y += deltaY * s3;
        }
    }

    @Override
    public void render(@NotNull GuiGraphics guiGraphics, @NotNull DeltaTracker deltaTracker) {
        boolean enabled = ClientHelper.isRouletteOpen();

        if (this.wasEnabled != enabled) {
            // TODO: animation
            this.x = 0;
            this.y = 0;
            this.wasEnabled = enabled;
        }

        if (!enabled) {
            return;
        }

        int left = 20;
        int top = 20;

        int centerX = left + 58;
        int centerY = top + 58;

        RenderSystem.enableBlend();
        //RenderUtils.drawRing(guiGraphics, left - 8, top - 8, 58, 0.5f, 0x44000000);

        var sdf     = SdfGraphics.getInstance();
        var pose    = guiGraphics.pose();

        sdf     .center(true)
                .color(0x44000000)
                .circle(centerX, centerY, 58)
                .fill(pose)

                .circle(centerX, centerY, 24)
                .fill(pose)

                .circle(centerX, centerY, 7)
                .color(0xbba0a0a0)
                .circle(centerX, centerY, 6)
                .stroke(pose, 1)

                .color(Palette.NORMAL_TEXT_COLOR)
                .circle(centerX + x, centerY + y, 4)
                .fill(pose);

        for (int i = 0; i < 8; i++) {
            float deg   = 45 * i * Mth.DEG_TO_RAD + Mth.HALF_PI;
            float x     = Mth.cos(deg) * 8;
            float y     = Mth.sin(deg) * 8;

            sdf     .rotate(45f * i)
                    .round(2)
                    .color(0x88000000)
                    .sector(centerX + x, centerY + y, 22.5f, 46, 26)
                    .fill(pose)

                    .round(1)
                    .color(0xbba0a0a0)
                    .stroke(pose, 0.5f)
                    ;
        }
        RenderSystem.disableBlend();

        sdf.reset();
    }
}
