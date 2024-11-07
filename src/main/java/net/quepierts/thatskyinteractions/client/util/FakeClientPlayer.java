package net.quepierts.thatskyinteractions.client.util;

import com.mojang.authlib.GameProfile;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.resources.PlayerSkin;
import net.minecraft.client.resources.SkinManager;
import net.minecraft.world.item.component.ResolvableProfile;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.quepierts.thatskyinteractions.common.data.FriendData;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

@OnlyIn(Dist.CLIENT)
public class FakeClientPlayer extends AbstractClientPlayer {
    public static final String PLACEHOLDER_NAME = "";
    public static final UUID PLACEHOLDER_UUID = new UUID(42, -42);

    private final FakePlayerDisplayHandler handler;
    private final ResolvableProfile profile;
    public FakeClientPlayer(ClientLevel clientLevel, FakePlayerDisplayHandler handler, FriendData friendData) {
        super(clientLevel, new GameProfile(PLACEHOLDER_UUID, PLACEHOLDER_NAME));
        this.handler = handler;
        this.profile = friendData.getProfile();
        clientLevel.addEntity(this);

        this.getEntityData().set(DATA_PLAYER_MODE_CUSTOMISATION, (byte) 0b11111111);
        this.noCulling = true;
    }

    @Override
    public boolean shouldRender(double x, double y, double z) {
        return this.handler.isVisible();
    }

    @Override
    public boolean canBeCollidedWith() {
        return false;
    }

    @Override
    public boolean shouldShowName() {
        return false;
    }

    @Override
    public boolean hasCustomName() {
        return false;
    }

    @Override
    public void aiStep() {

    }

    public UUID getDisplayUUID() {
        return this.profile.gameProfile().getId();
    }

    @NotNull
    @Override
    public PlayerSkin getSkin() {
        SkinManager skinManager = Minecraft.getInstance().getSkinManager();
        return skinManager.getInsecureSkin(this.profile.gameProfile());
    }
}
