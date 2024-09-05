package net.quepierts.thatskyinteractions.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.quepierts.thatskyinteractions.data.tree.NodeState;

@OnlyIn(Dist.CLIENT)
public class Palette {
    public static final int NORMAL_TEXT_COLOR = 0xfff4f5e3;
    public static final int HIGHLIGHT_TEXT_COLOR = 0xfff67e1e;
    public static final int HIGHLIGHT_COLOR = 0xfffffee0;
    public static void useLockedIconColor() {
        RenderSystem.setShaderColor(
                82f / 255f,
               103f / 255f,
               122f / 255f,
               getShaderAlpha()
        );
    }

    public static void useUnlockableIconColor() {
        RenderSystem.setShaderColor(
                200f / 255f,
                249f / 255f,
                253f / 255f,
                getShaderAlpha()
        );
    }

    public static void useUnlockedIconColor() {
        RenderSystem.setShaderColor(
                1.0f,
                254f / 255f,
                224f / 255f,
                getShaderAlpha()
        );
    }

    public static void mulLockedIconColor() {
        RenderSystem.setShaderColor(
                0.3215f,
                0.4055f,
                0.5446f,
                getShaderAlpha()
        );
    }

    public static void mulUnlockableIconColor() {
        RenderSystem.setShaderColor(
                0.7843f,
                0.9803f,
                1.1294f,
                getShaderAlpha()
        );
    }

    public static void mulUnlockedIconColor() {
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, getShaderAlpha());
    }

    public static void useColor(NodeState state) {
        switch (state) {
            case LOCKED:
                useLockedIconColor();
                break;
            case UNLOCKED:
                useUnlockedIconColor();
                break;
            case UNLOCKABLE:
                useUnlockableIconColor();
                break;
        }
    }

    public static void mulColor(NodeState state) {
        switch (state) {
            case LOCKED:
                mulLockedIconColor();
                break;
            case UNLOCKED:
                mulUnlockedIconColor();
                break;
            case UNLOCKABLE:
                mulUnlockableIconColor();
                break;
        }
    }

    public static void reset() {
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, getShaderAlpha());
    }

    public static float getShaderAlpha() {
        return RenderSystem.getShaderColor()[3];
    }

    public static void setShaderAlpha(float alpha) {
        float[] color = RenderSystem.getShaderColor();
        RenderSystem.setShaderColor(color[0], color[1], color[2], alpha);
    }

    public static void mulShaderAlpha(float alpha) {
        float[] color = RenderSystem.getShaderColor();
        RenderSystem.setShaderColor(color[0], color[1], color[2], color[3] * alpha);
    }
}
