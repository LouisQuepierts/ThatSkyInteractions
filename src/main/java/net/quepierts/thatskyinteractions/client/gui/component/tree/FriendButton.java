package net.quepierts.thatskyinteractions.client.gui.component.tree;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.quepierts.thatskyinteractions.ThatSkyInteractions;
import net.quepierts.thatskyinteractions.client.util.RenderUtils;
import net.quepierts.thatskyinteractions.client.gui.Palette;
import net.quepierts.thatskyinteractions.client.gui.animate.ScreenAnimator;
import net.quepierts.thatskyinteractions.client.gui.screen.confirm.ConfirmMessageUtils;
import net.quepierts.thatskyinteractions.data.tree.NodeState;
import org.jetbrains.annotations.NotNull;

@OnlyIn(Dist.CLIENT)
public class FriendButton extends TreeNodeButton {
    public static final ResourceLocation ICON_FRIEND = ThatSkyInteractions.getLocation("textures/gui/be_friend.png");
    private static final FormattedCharSequence INVITE_MESSAGE_LEFT = Component.translatable("gui.message.unlock.friend.request.left").withColor(Palette.NORMAL_TEXT_COLOR).getVisualOrderText();
    private static final FormattedCharSequence INVITE_MESSAGE_RIGHT = Component.translatable("gui.message.unlock.friend.request.right").withColor(Palette.NORMAL_TEXT_COLOR).getVisualOrderText();
    private static final Component ACCEPT_MESSAGE = Component.translatable("gui.message.unlock.friend.accept").withColor(Palette.NORMAL_TEXT_COLOR);

    public FriendButton(String id, int x, int y, ScreenAnimator animator, NodeState state) {
        super(id, x, 1, Component.literal("friend"), y, ICON_FRIEND, animator, state);
    }

    @Override
    protected void renderIcon(GuiGraphics guiGraphics, int begin) {
        super.renderIcon(guiGraphics, begin);
    }

    @Override
    public void renderUnlockMessageInvite(GuiGraphics guiGraphics, PoseStack pose, int width, int height) {
        Font font = Minecraft.getInstance().font;

        int leftWidth = font.width(INVITE_MESSAGE_LEFT);
        int rightWidth = font.width(INVITE_MESSAGE_RIGHT);

        int totalWidth = leftWidth + 12 + rightWidth;
        pose.translate(totalWidth / -2.0f, -4.0f, 0.0f);

        RenderUtils.blitIcon(guiGraphics, this.getCurrency().icon, leftWidth + 1, 0, 8, 8);
        guiGraphics.drawString(font, INVITE_MESSAGE_LEFT, 0, 0, 0xffffffff);
        guiGraphics.drawString(font, INVITE_MESSAGE_RIGHT, leftWidth + 12, 0, 0xffffffff);
    }

    @Override
    public void renderUnlockMessageAccept(GuiGraphics guiGraphics, PoseStack pose, int width, int height) {
        ConfirmMessageUtils.renderUnlockAcceptMessage(guiGraphics, pose, -12, this.getCurrency().icon);
        guiGraphics.drawCenteredString(Minecraft.getInstance().font, ACCEPT_MESSAGE, 0, 0, 0xffffffff);
    }

    @Override
    public void onClickUnlocked() {
        super.onClickUnlocked();
    }

    @Override
    public @NotNull ResourceLocation getIcon() {
        return ICON_FRIEND;
    }
}
