package net.quepierts.thatskyinteractions.client.util;

import com.mojang.authlib.GameProfile;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.client.resources.PlayerSkin;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

@OnlyIn(Dist.CLIENT)
public class FakeClientPlayer extends AbstractClientPlayer {
    public static final String PLACEHOLDER_NAME = "";
    public static final UUID PLACEHOLDER_UUID = new UUID(42, -42);
    private final FakePlayerDisplayHandler handler;
    private PlayerInfo displayPlayerInfo;
    private UUID displayUUID;
    private boolean changed = false;
    public FakeClientPlayer(ClientLevel clientLevel, FakePlayerDisplayHandler handler) {
        super(clientLevel, new GameProfile(PLACEHOLDER_UUID, PLACEHOLDER_NAME));
        this.handler = handler;
        this.displayPlayerInfo = Minecraft.getInstance().getConnection().getPlayerInfo(PLACEHOLDER_UUID);
        clientLevel.addEntity(this);
        this.setPos(128, 70, 3);
        this.noCulling = true;
        this.displayUUID = PLACEHOLDER_UUID;
    }

    @Override
    public boolean shouldRender(double x, double y, double z) {
        return this.handler.isVisible();
    }

    public void setPlayerSkin(@NotNull UUID uuid) {
        if (uuid.equals(this.displayUUID))
            return;

        this.changed = true;
        this.displayUUID = uuid;
        this.getDisplayPlayerInfo();
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

    @Override
    public PlayerSkin getSkin() {
        PlayerInfo playerinfo = this.getDisplayPlayerInfo();
        return playerinfo == null ? DefaultPlayerSkin.get(this.getUUID()) : playerinfo.getSkin();
    }

    public UUID getDisplayUUID() {
        return displayUUID;
    }

    @Nullable
    protected PlayerInfo getDisplayPlayerInfo() {
        if (changed || this.displayPlayerInfo == null) {
            this.displayPlayerInfo = Minecraft.getInstance().getConnection().getPlayerInfo(this.displayUUID);
        }
        return this.displayPlayerInfo;
    }
}
