package net.quepierts.thatskyinteractions.data;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class FriendData {
    @NotNull private final UUID uuid;
    @NotNull private final String username;
    @NotNull private String nickname;

    public FriendData(@NotNull UUID uuid, @NotNull String username, @NotNull String nickname) {
        this.uuid = uuid;
        this.username = username;
        this.nickname = nickname;
    }

    public @NotNull UUID getUuid() {
        return uuid;
    }

    public void setNickname(@NotNull String nickname) {
        this.nickname = nickname;
    }

    public @NotNull String getNickname() {
        return nickname;
    }

    public static void toNetwork(FriendlyByteBuf byteBuf, FriendData data) {
        byteBuf.writeUUID(data.uuid);
        byteBuf.writeUtf(data.username);
        byteBuf.writeUtf(data.nickname);
    }

    public static FriendData fromNetwork(FriendlyByteBuf byteBuf) {
        UUID uuid = byteBuf.readUUID();
        String username = byteBuf.readUtf();
        String nickname = byteBuf.readUtf();
        return new FriendData(uuid, username, nickname);
    }

    public static void toNBT(CompoundTag tag, FriendData data) {
        tag.putUUID("uuid", data.uuid);
        tag.putString("username", data.username);
        tag.putString("nickname", data.nickname);
    }

    public static FriendData fromNBT(CompoundTag tag) {
        UUID uuid = tag.getUUID("uuid");
        String username = tag.getString("username");
        String nickname = tag.getString("nickname");
        return new FriendData(uuid, username, nickname);
    }
}
