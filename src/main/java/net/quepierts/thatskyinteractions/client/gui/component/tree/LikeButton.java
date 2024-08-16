package net.quepierts.thatskyinteractions.client.gui.component.tree;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.quepierts.thatskyinteractions.ThatSkyInteractions;
import net.quepierts.thatskyinteractions.client.gui.animate.ScreenAnimator;
import net.quepierts.thatskyinteractions.data.tree.NodeState;
import org.jetbrains.annotations.NotNull;

@OnlyIn(Dist.CLIENT)
public class LikeButton extends TreeNodeButton {
    public static final ResourceLocation ICON_LIKE_OFF = ThatSkyInteractions.getLocation("textures/icon/like_off.png");
    public static final ResourceLocation ICON_LIKE_ON = ThatSkyInteractions.getLocation("textures/icon/like_on.png");

    private boolean on = false;
    public LikeButton(String id, int x, int y, ScreenAnimator animator, NodeState state) {
        super(id, x, 0, Component.literal("like"), y, ICON_LIKE_OFF, animator, state);
    }

    @Override
    public void onClick(double mouseX, double mouseY) {
        this.animator.play(this.clickAnimation);
        this.on = !this.on;
        Minecraft.getInstance().getSoundManager().play(
                SimpleSoundInstance.forUI(
                        SoundEvents.PLAYER_LEVELUP,
                        1.0f
                )
        );
    }

    @Override
    @NotNull
    public ResourceLocation getIcon() {
        return this.on ? ICON_LIKE_ON : ICON_LIKE_OFF;
    }
}
