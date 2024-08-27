package net.quepierts.thatskyinteractions.client.gui.component.tree;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.quepierts.thatskyinteractions.ThatSkyInteractions;
import net.quepierts.thatskyinteractions.client.gui.Palette;
import net.quepierts.thatskyinteractions.client.gui.animate.ScreenAnimator;
import net.quepierts.thatskyinteractions.client.gui.screen.confirm.ConfirmMessageUtils;
import net.quepierts.thatskyinteractions.client.util.RenderUtils;
import net.quepierts.thatskyinteractions.data.Currency;
import net.quepierts.thatskyinteractions.data.tree.NodeState;

@OnlyIn(Dist.CLIENT)
public class LockButton extends TreeNodeButton {
    private static final Component MESSAGE_LEFT = Component.translatable("gui.message.unlock.lock.request.left").withColor(Palette.NORMAL_TEXT_COLOR);
    private static final Component MESSAGE_RIGHT = Component.translatable("gui.message.unlock.lock.request.right", Component.translatable("gui.message.unlock.lock.intimacy").withColor(Palette.HIGHLIGHT_TEXT_COLOR).withStyle(ChatFormatting.BOLD)).withColor(Palette.NORMAL_TEXT_COLOR);
    private static final Component MESSAGE_ACCEPT = Component.translatable("gui.message.unlock.lock.accept", Component.translatable("gui.message.unlock.lock.intimacy").withColor(Palette.HIGHLIGHT_TEXT_COLOR).withStyle(ChatFormatting.BOLD)).withColor(Palette.NORMAL_TEXT_COLOR);
    public static final ResourceLocation ICON_LOCK = ThatSkyInteractions.getLocation("textures/gui/lock.png");
    public LockButton(String id, int x, int y, int price, Component message, ScreenAnimator animator, NodeState state) {
        super(id, x, y, price, animator, ICON_LOCK, message, Currency.RED_CANDLE, state);
    }

    @Override
    public void renderUnlockMessageInvite(GuiGraphics guiGraphics, PoseStack pose, int width, int height) {
        Font font = Minecraft.getInstance().font;
        int leftWidth = font.width(MESSAGE_LEFT);
        int rightWidth = font.width(MESSAGE_RIGHT);

        int totalWidth = leftWidth + 12 + rightWidth;
        pose.translate(totalWidth / -2.0f, -4.0f, 0.0f);

        RenderUtils.blitIcon(guiGraphics, Currency.RED_CANDLE.icon, leftWidth + 1, 0, 8, 8);
        guiGraphics.drawString(font, MESSAGE_LEFT, 0, 0, 0xffffffff);
        guiGraphics.drawString(font, MESSAGE_RIGHT, leftWidth + 12, 0, 0xffffffff);
    }

    @Override
    public void renderUnlockMessageAccept(GuiGraphics guiGraphics, PoseStack pose, int width, int height) {
        ConfirmMessageUtils.renderUnlockAcceptMessage(guiGraphics, pose, -12, this.getCurrency().icon);
        guiGraphics.drawCenteredString(Minecraft.getInstance().font, MESSAGE_ACCEPT, 0, 0, 0xffffffff);
    }
}
