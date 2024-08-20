package net.quepierts.thatskyinteractions.client.gui.component.tree;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.GuiGraphics;
import net.quepierts.thatskyinteractions.client.RenderUtils;
import net.quepierts.thatskyinteractions.client.gui.Palette;
import net.quepierts.thatskyinteractions.client.gui.screen.AnimatableScreen;
import net.quepierts.thatskyinteractions.client.gui.screen.confirm.ConfirmProvider;

public abstract class ButtonConfirmProvider implements ConfirmProvider {
    protected final AnimatableScreen screen;
    protected final TreeNodeButton button;

    public ButtonConfirmProvider(AnimatableScreen screen, TreeNodeButton button) {
        this.screen = screen;
        this.button = button;
    }

    @Override
    public void render(GuiGraphics guiGraphics, int width, int height) {
        RenderSystem.enableBlend();
        Palette.useUnlockedIconColor();
        RenderUtils.blitIcon(guiGraphics, this.button.getIcon(), -20, 20 - height / 2, 40, 40);
        Palette.reset();

        PoseStack pose = guiGraphics.pose();
        pose.pushPose();
        pose.scale(1.25f, 1.25f, 1.25f);
        this.button.renderUnlockMessageInvite(guiGraphics, pose, width, height);
        pose.popPose();

        Palette.reset();
    }
}
