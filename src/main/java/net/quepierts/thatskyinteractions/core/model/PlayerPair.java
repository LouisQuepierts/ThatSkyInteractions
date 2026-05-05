package net.quepierts.thatskyinteractions.core.model;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.UUIDUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class PlayerPair {
    public static final Codec<PlayerPair> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            UUIDUtil.CODEC.fieldOf("left").forGetter(PlayerPair::getLeft),
            UUIDUtil.CODEC.fieldOf("right").forGetter(PlayerPair::getRight)
    ).apply(instance, PlayerPair::new));

    public static final StreamCodec<ByteBuf, PlayerPair> STREAM_CODEC = StreamCodec.composite(
            UUIDUtil.STREAM_CODEC,
            PlayerPair::getLeft,
            UUIDUtil.STREAM_CODEC,
            PlayerPair::getRight,
            PlayerPair::new
    );

    @NotNull private final UUID left;
    @NotNull private final UUID right;

    public PlayerPair(@NotNull UUID a, @NotNull UUID b) {
        if (a.compareTo(b) > 0) {
            this.left = a;
            this.right = b;
        } else {
            this.left = b;
            this.right = a;
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof PlayerPair other) {
            return this.left.equals(other.left) && this.right.equals(other.right);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return left.hashCode() ^ right.hashCode();
    }

    public void serializeNBT(CompoundTag tag) {
        tag.putUUID("a", this.left);
        tag.putUUID("b", this.right);
    }

    public static PlayerPair deserializeNBT(CompoundTag tag) {
        UUID a1 = tag.getUUID("a");
        UUID b1 = tag.getUUID("b");
        return new PlayerPair(a1, b1);
    }

    public static PlayerPair fromNetwork(FriendlyByteBuf friendlyByteBuf) {
        return new PlayerPair(friendlyByteBuf.readUUID(), friendlyByteBuf.readUUID());
    }

    public @NotNull UUID getLeft() {
        return left;
    }

    public @NotNull UUID getRight() {
        return right;
    }

    public UUID getOther(UUID uuid) {
        return left.equals(uuid) ? right : left;
    }

    public void toNetwork(FriendlyByteBuf friendlyByteBuf) {
        friendlyByteBuf.writeUUID(this.left);
        friendlyByteBuf.writeUUID(this.right);
    }
}
