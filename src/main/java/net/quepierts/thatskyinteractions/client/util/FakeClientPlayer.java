package net.quepierts.thatskyinteractions.client.util;

import com.mojang.authlib.GameProfile;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.client.player.AbstractClientPlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class FakeClientPlayer extends AbstractClientPlayer {
    private static final String PLACEHOLDER_NAME = "";
    private static final UUID PLACEHOLDER_UUID = new UUID(42, -42);
    private final FakePlayerDisplayHandler handler;
    private PlayerInfo displayPlayerInfo;
    private UUID displayUUID;
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

        this.displayUUID = uuid;
        this.getPlayerInfo();
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

    @Nullable
    @Override
    protected PlayerInfo getPlayerInfo() {
        if (this.displayPlayerInfo == null) {
            this.displayPlayerInfo = Minecraft.getInstance().getConnection().getPlayerInfo(this.displayUUID);
        }
        return this.displayPlayerInfo;
    }
}
