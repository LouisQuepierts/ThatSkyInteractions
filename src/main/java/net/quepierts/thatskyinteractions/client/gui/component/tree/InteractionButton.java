package net.quepierts.thatskyinteractions.client.gui.component.tree;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.quepierts.simpleanimator.api.IInteractHandler;
import net.quepierts.simpleanimator.core.SimpleAnimator;
import net.quepierts.thatskyinteractions.ThatSkyInteractions;
import net.quepierts.thatskyinteractions.client.gui.Palette;
import net.quepierts.thatskyinteractions.client.gui.animate.ScreenAnimator;
import net.quepierts.thatskyinteractions.client.gui.screen.confirm.ConfirmMessageUtils;
import net.quepierts.thatskyinteractions.client.util.RenderUtils;
import net.quepierts.thatskyinteractions.data.tree.NodeState;
import net.quepierts.thatskyinteractions.network.packet.InteractButtonPacket;

import java.util.UUID;

@OnlyIn(Dist.CLIENT)
public class InteractionButton extends TreeNodeButton {
    private final ResourceLocation interaction;
    private final ResourceLocation leveledInteraction;
    private final Component levelComponent;
    private final Component left;
    private final Component right;
    private final Component accept;
    public InteractionButton(String id, int x, int y, int price, Component message, ScreenAnimator animator, ResourceLocation interaction, int level, NodeState state) {
        super(id, x, price, message, y, RenderUtils.getInteractionIcon(interaction), animator, state);
        this.interaction = interaction;
        this.leveledInteraction = interaction.withSuffix("_" + level);

        this.levelComponent = level > 1 ? Component.translatable("gui.tree.node.interaction.level", level) : Component.empty();
        this.left = Component.translatable("gui.message.unlock.interaction.request.left", this.getPrice()).withColor(Palette.NORMAL_TEXT_COLOR);
        MutableComponent name = Component.translatable(ThatSkyInteractions.getInteractionTranslateKey(interaction)).withColor(Palette.HIGHLIGHT_TEXT_COLOR).withStyle(ChatFormatting.BOLD);
        this.right = Component.translatable("gui.message.unlock.interaction.request.right", name).withColor(Palette.NORMAL_TEXT_COLOR);
        this.accept = Component.translatable("gui.message.unlock.interaction.accept", name).withColor(Palette.NORMAL_TEXT_COLOR);
    }

    @Override
    public void renderUnlockMessageInvite(GuiGraphics guiGraphics, PoseStack pose, int width, int height) {
        Font font = Minecraft.getInstance().font;

        int leftWidth = font.width(this.left);
        int rightWidth = font.width(this.right);

        int totalWidth = leftWidth + 12 + rightWidth;
        pose.translate(totalWidth / -2.0f, -4.0f, 0.0f);

        RenderUtils.blitIcon(guiGraphics, this.getCurrency().icon, leftWidth + 1, 0, 8, 8);
        guiGraphics.drawString(font, this.left, 0, 0, 0xffffffff);
        guiGraphics.drawString(font, this.right, leftWidth + 12, 0, 0xffffffff);
    }

    @Override
    public void renderUnlockMessageAccept(GuiGraphics guiGraphics, PoseStack pose, int width, int height) {
        ConfirmMessageUtils.renderUnlockAcceptMessage(guiGraphics, pose, -12, this.getCurrency().icon);
        guiGraphics.drawCenteredString(Minecraft.getInstance().font, this.accept, 0, 0, 0xffffffff);
    }

    @Override
    public void onClickUnlocked() {
        if (SimpleAnimator.getProxy().getAnimationManager().getInteraction(this.leveledInteraction) == null) {
            super.onClickLocked();
            return;
        }

        super.onClickUnlocked();
        Minecraft minecraft = Minecraft.getInstance();
        UUID target = ThatSkyInteractions.getInstance().getClient().getTarget();

        if (target == null)
            return;

        Player player = minecraft.level.getPlayerByUUID(target);

        if (player == null)
            return;

        if (((IInteractHandler) minecraft.player).simpleanimator$inviteInteract(player, this.leveledInteraction, true)) {
            SimpleAnimator.getNetwork().update(new InteractButtonPacket.Invite(target, this.interaction));
        }
    }

    @Override
    protected void renderIcon(GuiGraphics guiGraphics, int begin) {
        Font font = Minecraft.getInstance().font;
        double v = shake.get();
        guiGraphics.pose().translate(v, 0.0, 0.0);

        Palette.useColor(state);
        RenderUtils.blitIcon(guiGraphics, this.getIcon(), begin, begin, this.getWidth(), this.getHeight());
        guiGraphics.pose().scale(0.8f, 0.8f, 1.0f);
        guiGraphics.drawString(font, this.levelComponent, begin + 20, begin - 8, 0xffffffff);
        guiGraphics.pose().scale(1.25f, 1.25f, 1.0f);

        Palette.reset();

        guiGraphics.pose().translate(-v, 0.0, 0.0);

        if (this.state == NodeState.UNLOCKABLE && this.price > 0) {
            guiGraphics.blit(this.currency.icon, begin + 16, begin + 28, 0, 0, 16, 16, 16, 16);

            guiGraphics.drawString(font, String.valueOf(this.price), begin + 28, begin + 40, Palette.NORMAL_TEXT_COLOR);
        }
    }
}
