package net.quepierts.thatskyinteractions.client.gui.layer.interact;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.quepierts.thatskyinteractions.ThatSkyInteractions;
import net.quepierts.thatskyinteractions.client.gui.Palette;
import net.quepierts.thatskyinteractions.client.RenderUtils;
import net.quepierts.thatskyinteractions.client.gui.component.tree.TreeNodeButton;
import net.quepierts.thatskyinteractions.client.gui.screen.confirm.ConfirmProvider;
import net.quepierts.thatskyinteractions.client.gui.screen.confirm.ConfirmScreen;
import net.quepierts.thatskyinteractions.data.PlayerPair;
import org.joml.Vector3f;

public class UnlockRequestW2SButton extends World2ScreenButton {
    private final PlayerPair pair;
    private final String node;
    private final TreeNodeButton parent;
    private final Vector3f position;
    public UnlockRequestW2SButton(TreeNodeButton button, PlayerPair pair, String node, Player player) {
        super(button.getIcon());
        this.pair = pair;
        this.node = node;
        this.position = player.position().toVector3f().add(0, 2.4f, 0);
        this.parent = button;
    }

    @Override
    public void invoke() {
        Minecraft.getInstance().setScreen(new ConfirmScreen(
                Component.empty(),
                new UnlockNodeAcceptConfirmProvider(this),
                264, 176
        ));
    }

    @Override
    public Vector3f getWorldPos() {
        return this.position;
    }

    private static class UnlockNodeAcceptConfirmProvider implements ConfirmProvider {
        private final UnlockRequestW2SButton button;

        private UnlockNodeAcceptConfirmProvider(UnlockRequestW2SButton button) {
            this.button = button;
        }

        @Override
        public void render(GuiGraphics guiGraphics, int width, int height) {
            RenderSystem.enableBlend();
            Palette.useUnlockedIconColor();
            RenderUtils.blitIcon(guiGraphics, this.button.parent.getIcon(), -20, 20 - height / 2, 40, 40);
            Palette.reset();

            PoseStack pose = guiGraphics.pose();
            pose.pushPose();
            pose.scale(1.25f, 1.25f, 1.25f);
            this.button.parent.renderUnlockMessageAccept(guiGraphics, pose, width, height);
            pose.popPose();

            Palette.reset();
        }

        @Override
        public void confirm() {
            ThatSkyInteractions.getInstance().getClient().getUnlockRelationshipHandler().accept(this.button.pair, this.button.node);
        }

        @Override
        public void cancel() {

        }
    }
}
