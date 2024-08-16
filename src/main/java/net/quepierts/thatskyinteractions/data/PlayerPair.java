package net.quepierts.thatskyinteractions.data;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class PlayerPair {
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

    public CompoundTag serializeNBT(CompoundTag tag) {
        tag.putUUID("a", this.left);
        tag.putUUID("b", this.right);
        return tag;
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
