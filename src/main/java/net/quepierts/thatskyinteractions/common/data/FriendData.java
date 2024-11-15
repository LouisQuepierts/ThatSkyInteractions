package net.quepierts.thatskyinteractions.common.data;

import com.mojang.authlib.properties.PropertyMap;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.UUIDUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.component.ResolvableProfile;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.quepierts.simpleanimator.core.SimpleAnimator;
import net.quepierts.thatskyinteractions.common.network.packet.astrolabe.AstrolabeModifyPacket;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.UUID;

public class FriendData {
    public static final Codec<FriendData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            UUIDUtil.CODEC.fieldOf("uuid").forGetter(FriendData::getUuid),
            Codec.STRING.fieldOf("username").forGetter(FriendData::getUsername),
            Codec.STRING.fieldOf("nickname").forGetter(FriendData::getNickname)
    ).apply(instance, FriendData::new));

    public static final StreamCodec<ByteBuf, FriendData> STREAM_CODEC = StreamCodec.composite(
            UUIDUtil.STREAM_CODEC,
            FriendData::getUuid,
            ByteBufCodecs.STRING_UTF8,
            FriendData::getUsername,
            ByteBufCodecs.STRING_UTF8,
            FriendData::getNickname,
            FriendData::new
    );

    @NotNull private final UUID uuid;
    @NotNull private final String username;
    @NotNull private String nickname;

    @NotNull private ResolvableProfile profile;

    public FriendData(@NotNull Player player) {
        this(player.getUUID(), player.getName().getString(), player.getName().getString());
    }

    public FriendData(@NotNull UUID uuid, @NotNull String username, @NotNull String nickname) {
        this.uuid = uuid;
        this.username = username;
        this.nickname = nickname;

        this.profile = new ResolvableProfile(Optional.of(username), Optional.empty(), new PropertyMap());
        this.profile.resolve()
                .thenAcceptAsync(profile -> this.profile = profile);
    }

    public @NotNull UUID getUuid() {
        return uuid;
    }

    public @NotNull ResolvableProfile getProfile() {
        return profile;
    }

    public void setNickname(@NotNull String nickname) {
        this.nickname = nickname;
    }

    @OnlyIn(Dist.CLIENT)
    public void updateNickname(@NotNull String nickname) {
        this.nickname = nickname;
        SimpleAnimator.getNetwork().update(new AstrolabeModifyPacket.Nickname(this.uuid, this.nickname));
    }

    public @NotNull String getNickname() {
        return nickname;
    }

    public @NotNull String getUsername() {
        return this.username;
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
