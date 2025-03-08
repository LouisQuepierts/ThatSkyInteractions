package net.quepierts.thatskyinteractions.client.gui.layer;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.LayeredDraw;
import net.minecraft.util.Mth;
import net.quepierts.thatskyinteractions.client.ClientHelper;
import net.quepierts.thatskyinteractions.client.gui.Palette;
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

        int innerX = left + 26;
        int innerY = top + 26;

        RenderSystem.enableBlend();
        //RenderUtils.drawRing(guiGraphics, left - 8, top - 8, 58, 0.5f, 0x44000000);

        RenderUtils.fillCircle(guiGraphics, left - 8, top - 8, 58, 0x44000000);
        RenderUtils.fillCircle(guiGraphics, innerX, innerY, 24, 0x44000000);
        RenderUtils.fillCircle(guiGraphics, innerX + 17f, innerY + 17f, 7, 0x44000000);
        RenderUtils.drawRing(guiGraphics, innerX + 18, innerY + 18, 6, 0.1f, 0xbba0a0a0);

        RenderUtils.fillCircle(guiGraphics, innerX + 20 + x, innerY + 20 + y, 4, Palette.NORMAL_TEXT_COLOR);
        for (int i = 0; i < 8; i++) {
            float deg = 45 * i * Mth.DEG_TO_RAD;
            float x = Mth.cos(deg) * 8;
            float y = Mth.sin(deg) * 8;

            RenderUtils.fillSector(guiGraphics, left + x, top + y, 100, 45, 45 * i, 34, 28, 2, 0x88000000);
            RenderUtils.drawSectorStroke(guiGraphics, left + x, top + y, 100, 45, 45 * i, 34, 28, 1.8f, 0.5f, 0xbba0a0a0);
        }
        RenderSystem.disableBlend();
    }
}
