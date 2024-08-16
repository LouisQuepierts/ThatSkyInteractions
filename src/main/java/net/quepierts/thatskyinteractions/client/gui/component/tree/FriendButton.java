package net.quepierts.thatskyinteractions.client.gui.component.tree;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.quepierts.thatskyinteractions.ThatSkyInteractions;
import net.quepierts.thatskyinteractions.client.gui.Palette;
import net.quepierts.thatskyinteractions.client.gui.RenderUtils;
import net.quepierts.thatskyinteractions.client.gui.animate.ScreenAnimator;
import net.quepierts.thatskyinteractions.data.tree.NodeState;

public class FriendButton extends TreeNodeButton {
    public static final ResourceLocation ICON_FRIEND = ThatSkyInteractions.getLocation("textures/gui/be_friend.png");
    private static final FormattedCharSequence MESSAGE_LEFT = Component.translatable("gui.message.unlock.friend.request.left").withColor(Palette.NORMAL_TEXT_COLOR).getVisualOrderText();
    private static final FormattedCharSequence MESSAGE_RIGHT = Component.translatable("gui.message.unlock.friend.request.right").withColor(Palette.NORMAL_TEXT_COLOR).getVisualOrderText();

    public FriendButton(String id, int x, int y, ScreenAnimator animator, NodeState state) {
        super(id, x, 1, Component.literal("friend"), y, ICON_FRIEND, animator, state);
    }

    @Override
    public void renderUnlockMessage(GuiGraphics guiGraphics, PoseStack pose, int width, int height) {
        Font font = Minecraft.getInstance().font;


        int leftWidth = font.width(MESSAGE_LEFT);
        int rightWidth = font.width(MESSAGE_RIGHT);

        int totalWidth = leftWidth + 12 + rightWidth;
        pose.translate(totalWidth / -2.0f, -4.0f, 0.0f);

        RenderUtils.blitIcon(guiGraphics, this.getCurrency().icon, leftWidth + 1, 0, 8, 8);
        guiGraphics.drawString(font, MESSAGE_LEFT, 0, 0, 0xffffffff);
        guiGraphics.drawString(font, MESSAGE_RIGHT, leftWidth + 12, 0, 0xffffffff);
    }
}
