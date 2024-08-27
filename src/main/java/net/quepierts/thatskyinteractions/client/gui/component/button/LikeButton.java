package net.quepierts.thatskyinteractions.client.gui.component.button;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.quepierts.thatskyinteractions.ThatSkyInteractions;
import net.quepierts.thatskyinteractions.client.data.ClientTSIDataCache;
import net.quepierts.thatskyinteractions.client.gui.animate.ScreenAnimator;
import net.quepierts.thatskyinteractions.data.FriendData;
import net.quepierts.thatskyinteractions.proxy.ClientProxy;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class LikeButton extends SqueezeButton {
    public static final ResourceLocation TEXTURE_ON = ThatSkyInteractions.getLocation("textures/gui/like_on.png");
    public static final ResourceLocation TEXTURE_OFF = ThatSkyInteractions.getLocation("textures/gui/like_off.png");

    private final FriendData friendData;
    private boolean on;
    public LikeButton(int x, int y, ScreenAnimator animator, FriendData friendData) {
        super(x, y, 32, Component.empty(), animator, TEXTURE_OFF);
        ClientProxy client = ThatSkyInteractions.getInstance().getClient();
        this.friendData = friendData;
        this.on = client.getCache().getUserData().isLiked(this.friendData.getUuid());
    }

    @Override
    public void onPress() {
        this.on = !this.on;
        ClientProxy client = ThatSkyInteractions.getInstance().getClient();
        ClientTSIDataCache cache = client.getCache();
        UUID friendUUID = this.friendData.getUuid();
        if (this.on) {
            cache.likeFriend(friendUUID, true);
        } else {
            cache.unlikeFriend(friendUUID, true);
        }
    }

    @Override
    public @NotNull ResourceLocation getIcon() {
        return this.on ? TEXTURE_ON : TEXTURE_OFF;
    }
}
