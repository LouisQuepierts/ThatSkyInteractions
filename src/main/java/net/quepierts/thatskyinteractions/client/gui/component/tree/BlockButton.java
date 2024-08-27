package net.quepierts.thatskyinteractions.client.gui.component.tree;

import com.mojang.blaze3d.vertex.PoseStack;
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
import net.quepierts.thatskyinteractions.client.gui.layer.AnimateScreenHolderLayer;
import net.quepierts.thatskyinteractions.client.gui.screen.AnimatableScreen;
import net.quepierts.thatskyinteractions.client.gui.screen.ConfirmScreen;
import net.quepierts.thatskyinteractions.data.tree.NodeState;
import net.quepierts.thatskyinteractions.proxy.ClientProxy;

import java.util.UUID;

@OnlyIn(Dist.CLIENT)
public class BlockButton extends TreeNodeButton {
    private static final ResourceLocation ICON_BLOCK = ThatSkyInteractions.getLocation("textures/gui/block.png");
    private static final Component MESSAGE_BLOCK_1 = Component.translatable("gui.message.block.line1").withColor(Palette.NORMAL_TEXT_COLOR);
    private static final Component MESSAGE_BLOCK_2 = Component.translatable("gui.message.block.line2").withColor(Palette.NORMAL_TEXT_COLOR);
    public BlockButton(String id, int x, int y, ScreenAnimator animator) {
        super(id, x, 0, Component.empty(), y, ICON_BLOCK, animator, NodeState.UNLOCKED);
    }

    @Override
    public void onClickUnlocked() {
        super.onClickUnlocked();
        Minecraft minecraft = Minecraft.getInstance();

        if (minecraft.screen instanceof AnimatableScreen screen) {
            screen.hide();
            AnimateScreenHolderLayer.INSTANCE.push(
                    new ConfirmScreen(
                            Component.empty(),
                            new ButtonConfirmProvider(screen, this) {
                                @Override
                                public void confirm() {
                                    ClientProxy client = ThatSkyInteractions.getInstance().getClient();
                                    UUID target = client.getTarget();
                                    if (target != null) {
                                        if (client.blocked(target)) {
                                            client.unblock(target);
                                        } else {
                                            client.block(target);
                                        }
                                    }
                                    screen.enter();
                                }

                                @Override
                                public void cancel() {
                                    screen.enter();
                                }
                            },
                            264, 176
                    )
            );
        }
    }

    @Override
    public void renderUnlockMessageInvite(GuiGraphics guiGraphics, PoseStack pose, int width, int height) {
        Font font = Minecraft.getInstance().font;
        guiGraphics.drawCenteredString(font, MESSAGE_BLOCK_1, 0, -12, 0xffffffff);
        guiGraphics.drawCenteredString(font, MESSAGE_BLOCK_2, 0, -4, 0xffffffff);
    }
}
