package net.quepierts.thatskyinteractions.client.gui.screen.confirm;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.quepierts.thatskyinteractions.client.RenderUtils;
import net.quepierts.thatskyinteractions.client.gui.Palette;

public class ConfirmMessageUtils {
    private static final Component MESSAGE_UNLOCK_ACCEPT_LEFT = Component.translatable("gui.message.unlock.accept.left").withColor(Palette.NORMAL_TEXT_COLOR);
    private static final Component MESSAGE_UNLOCK_ACCEPT_RIGHT = Component.translatable("gui.message.unlock.accept.right").withColor(Palette.NORMAL_TEXT_COLOR);

    public static void renderUnlockAcceptMessage(GuiGraphics guiGraphics, PoseStack pose, int y, ResourceLocation currency) {
        pose.pushPose();
        Font font = Minecraft.getInstance().font;

        int leftWidth = font.width(MESSAGE_UNLOCK_ACCEPT_LEFT);
        int rightWidth = font.width(MESSAGE_UNLOCK_ACCEPT_RIGHT);

        int totalWidth = leftWidth + 12 + rightWidth;
        pose.translate(totalWidth / -2.0f, y, 0.0f);

        RenderUtils.blitIcon(guiGraphics, currency, leftWidth + 1, 0, 8, 8);
        guiGraphics.drawString(font, MESSAGE_UNLOCK_ACCEPT_LEFT, 0, 0, 0xffffffff);
        guiGraphics.drawString(font, MESSAGE_UNLOCK_ACCEPT_RIGHT, leftWidth + 12, 0, 0xffffffff);
        pose.popPose();
    }

}
