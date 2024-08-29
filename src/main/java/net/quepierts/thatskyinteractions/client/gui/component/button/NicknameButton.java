package net.quepierts.thatskyinteractions.client.gui.component.button;

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
import net.quepierts.thatskyinteractions.client.gui.screen.InputScreen;
import net.quepierts.thatskyinteractions.data.FriendData;

@OnlyIn(Dist.CLIENT)
public class NicknameButton extends SqueezeButton {
    public static final ResourceLocation TEXTURE = ThatSkyInteractions.getLocation("textures/gui/nickname.png");
    private final FriendData friendData;
    private final Font font;

    public NicknameButton(int x, int y, ScreenAnimator animator, FriendData friendData) {
        super(x, y, 32, Component.empty(), animator, TEXTURE);

        this.friendData = friendData;
        this.font = Minecraft.getInstance().font;
    }

    @Override
    public void onPress() {
        AnimateScreenHolderLayer.INSTANCE.push(
                new InputScreen(
                        icon, 264, 176,
                        Component.translatable("gui.message.nickname.change").withColor(Palette.NORMAL_TEXT_COLOR),
                        friendData::updateNickname
                )
        );
    }

    @Override
    protected void renderIcon(GuiGraphics guiGraphics, int begin) {
        super.renderIcon(guiGraphics, begin);

        guiGraphics.drawCenteredString(this.font, friendData.getNickname(), begin + 16, begin + 40, Palette.NORMAL_TEXT_COLOR);
    }
}
