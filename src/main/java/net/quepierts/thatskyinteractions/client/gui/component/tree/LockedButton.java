package net.quepierts.thatskyinteractions.client.gui.component.tree;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.quepierts.thatskyinteractions.ThatSkyInteractions;
import net.quepierts.thatskyinteractions.client.gui.animate.ScreenAnimator;
import net.quepierts.thatskyinteractions.data.tree.NodeState;

@OnlyIn(Dist.CLIENT)
public class LockedButton extends TreeNodeButton {
    protected static final ResourceLocation ICON_LOCKED = ThatSkyInteractions.getLocation("textures/gui/locked.png");

    public LockedButton(String id, int x, int y, ScreenAnimator animator) {
        super(id, x, 0, Component.literal("locked"), y, ICON_LOCKED, animator, NodeState.LOCKED);
    }

    @Override
    protected void renderIcon(GuiGraphics guiGraphics, int begin) {
        super.renderIcon(guiGraphics, begin);
    }
}
