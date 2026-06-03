package net.quepierts.thatskyinteractions.feature.friendship;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import io.netty.buffer.ByteBuf;
import lombok.AccessLevel;
import lombok.Getter;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Player;
import net.quepierts.thatskyinteractions.ThatSkyInteractions;
import net.quepierts.thatskyinteractions.feature.registry.AttachmentTypes;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.*;

public final class PlayerFriendshipAttachment {

    public static final Codec<PlayerFriendshipAttachment> CODEC
            = FriendshipTreeData.CODEC.listOf().xmap(
                    PlayerFriendshipAttachment::new,
                    PlayerFriendshipAttachment::serialize
            );

    public static final MapCodec<PlayerFriendshipAttachment> MAP_CODEC
            = CODEC.fieldOf("player_friendship_attachment");

    public static final StreamCodec<ByteBuf, PlayerFriendshipAttachment> STREAM_CODEC
            = StreamCodec.composite(
                    ByteBufCodecs.collection(
                            ArrayList::new,
                            FriendshipTreeData.STREAM_CODEC
                    ),
                    PlayerFriendshipAttachment::getSerializable,
                    PlayerFriendshipAttachment::new
            );

    public static final Identifier FRIEND
            = ThatSkyInteractions.location("friend");

    public static PlayerFriendshipAttachment getAttachment(@NonNull final Player player) {
        return player.getData(AttachmentTypes.PLAYER_FRIENDSHIP);
    }

    @Getter(AccessLevel.PRIVATE)
    private final List<FriendshipTreeData>      serializable;
    private final Map<UUID, FriendshipTreeData> byUuid;

    public PlayerFriendshipAttachment() {
        this.serializable   = new ArrayList<>();
        this.byUuid         = new HashMap<>();
    }

    private PlayerFriendshipAttachment(
            final @NonNull List<FriendshipTreeData> serializable
    ) {
        this.serializable   = serializable;
        this.byUuid         = new HashMap<>(serializable.size());
        for (final var data : serializable) {
            this.byUuid.put(data.getFriend(), data);
        }
    }

    public @Nullable FriendshipTreeData get(
            final @NonNull UUID         uuid
    ) {
        return this.byUuid.get(uuid);
    }

    public @NonNull FriendshipTreeData get(
            final @NonNull UUID         uuid,
            final @NonNull Identifier   type
    ) {
        final var data = this.byUuid.get(uuid);
        if (data == null) {
            return new FriendshipTreeData(type, uuid);
        }

        final var fresh = new FriendshipTreeData(
                type,
                uuid
        );

        this.byUuid.put(uuid, fresh);
        this.serializable.add(fresh);

        return fresh;
    }

    public void clear() {
        this.byUuid.clear();
        this.serializable.clear();
    }

    public void compact() {
        final var iterator = this.serializable.iterator();
        while (iterator.hasNext()) {
            final var data = iterator.next();
            if (data.getStates().isEmpty()) {
                iterator.remove();
                this.byUuid.remove(data.getFriend());
            }
        }
    }

    private List<FriendshipTreeData> serialize() {
        this.compact();
        return this.serializable;
    }

}
