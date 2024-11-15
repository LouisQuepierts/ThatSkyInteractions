package net.quepierts.thatskyinteractions.client.gui.component.tree;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.quepierts.thatskyinteractions.ThatSkyInteractions;
import net.quepierts.thatskyinteractions.client.gui.Palette;
import net.quepierts.thatskyinteractions.client.gui.animate.ScreenAnimator;
import net.quepierts.thatskyinteractions.client.gui.layer.AnimateScreenHolderLayer;
import net.quepierts.thatskyinteractions.client.gui.screen.InputScreen;
import net.quepierts.thatskyinteractions.common.data.FriendData;
import net.quepierts.thatskyinteractions.common.data.astrolabe.FriendAstrolabeInstance;
import net.quepierts.thatskyinteractions.common.data.attachment.UserDataAttachment;
import net.quepierts.thatskyinteractions.common.data.attachment.component.AstrolabeComponent;
import net.quepierts.thatskyinteractions.common.data.tree.NodeState;
import net.quepierts.thatskyinteractions.common.proxy.ClientProxy;

import java.util.Objects;
import java.util.UUID;

public class NicknameButton extends TreeNodeButton {
    public static final ResourceLocation ICON_NICKNAME = ThatSkyInteractions.getLocation("textures/gui/nickname.png");

    private final FriendData friendData;
    public NicknameButton(String id, int x, int y, ScreenAnimator animator) {
        super(id, x, 0, Component.empty(), y, ICON_NICKNAME, animator, NodeState.UNLOCKED);

        LocalPlayer player = Objects.requireNonNull(Minecraft.getInstance().player);
        AstrolabeComponent astrolabe = UserDataAttachment.getAttachment(player).getAstrolabe();

        ClientProxy client = ThatSkyInteractions.getInstance().getClient();
        UUID target = client.getTarget();
        FriendAstrolabeInstance.NodeData nodeData = astrolabe.getNodeData(target);
        if (nodeData == null) {
            this.friendData = null;
        } else {
            this.friendData = nodeData.getFriendData();
        }
    }

    @Override
    public void onClickUnlocked() {
        super.onClickUnlocked();
        AnimateScreenHolderLayer.INSTANCE.push(
            new InputScreen(
                    icon, 264, 176,
                    Component.translatable("gui.message.nickname.change").withColor(Palette.NORMAL_TEXT_COLOR),
                    this.friendData.getUsername(),
                    friendData::updateNickname
            )
        );
    }

    @Override
    protected void renderIcon(GuiGraphics guiGraphics, int begin) {
        super.renderIcon(guiGraphics, begin);

        String nickname = friendData == null ? "Unknown" : friendData.getNickname();
        guiGraphics.drawCenteredString(
                Minecraft.getInstance().font,
                nickname,
                0, 0, Palette.NORMAL_TEXT_COLOR
        );
    }
}
