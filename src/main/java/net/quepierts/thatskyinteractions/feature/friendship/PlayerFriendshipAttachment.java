package net.quepierts.thatskyinteractions.feature.friendship;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import it.unimi.dsi.fastutil.ints.IntArraySet;
import it.unimi.dsi.fastutil.ints.IntSet;
import it.unimi.dsi.fastutil.objects.Object2IntArrayMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import lombok.AccessLevel;
import lombok.Getter;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.attachment.IAttachmentHolder;
import net.neoforged.neoforge.attachment.IAttachmentSerializer;
import net.quepierts.thatskyinteractions.ThatSkyInteractions;
import net.quepierts.thatskyinteractions.core.model.PlayerPair;
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

    public static final IAttachmentSerializer<PlayerFriendshipAttachment> SERIALIZER
            = new IAttachmentSerializer<>() {
        @Override
        public PlayerFriendshipAttachment read(
                final @NonNull IAttachmentHolder holder,
                final @NonNull ValueInput input
        ) {

            if (!(holder instanceof LivingEntity entity)) {
                return null;
            }

            final var result = input.read("friendship_attachment", CODEC);
            final var attachment = result.orElseThrow(() -> new IllegalArgumentException("Unable to read Friendship Attachment"));
            attachment.setup(entity.getUUID());
            return attachment;
        }

        @Override
        public boolean write(
                final PlayerFriendshipAttachment attachment,
                final @NonNull ValueOutput output
        ) {
            output.storeNullable("friendship_attachment", CODEC, attachment);
            return true;
        }
    };

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
        final var attachment = player.getData(AttachmentTypes.PLAYER_FRIENDSHIP);
        attachment.setup(player.getUUID());
        return attachment;
    }

    private transient       UUID                            me;
    private transient final Map<UUID, FriendshipTreeData>   byUuid;
    private transient final IntSet                          linked  = new IntArraySet();

    private transient final Object2IntMap<UUID>             invites = new Object2IntArrayMap<>();

    @Getter(AccessLevel.PRIVATE)
    private final List<FriendshipTreeData>                  serializable;

    public PlayerFriendshipAttachment() {
        this.serializable   = new ArrayList<>();
        this.byUuid         = new HashMap<>();
    }

    private PlayerFriendshipAttachment(
            final @NonNull List<FriendshipTreeData> serializable
    ) {
        this.serializable   = new ArrayList<>(new HashSet<>(serializable));
        this.byUuid         = new HashMap<>(serializable.size() + 1);

        this.tryExtract();
    }

    public static FriendshipTreeData union(
            @NonNull final Player a,
            @NonNull final Player b
    ) {
        final var aAttachment   = getAttachment(a);
        final var bAttachment   = getAttachment(b);

        if (aAttachment.linked.contains(b.getId()) && bAttachment.linked.contains(a.getId())) {
            return aAttachment.get(b);
        }

        aAttachment.linked.add(b.getId());
        bAttachment.linked.add(a.getId());

        final var aUuid     = a.getUUID();
        final var bUuid     = b.getUUID();

        final var aData     = aAttachment.get(b);
        bAttachment.byUuid.put(aUuid, aData);

        final var list      = bAttachment.serializable;
        final var size      = list.size();
        int i = 0;
        for (; i < size; i++) {
            final var bData = list.get(i);

            if (bData.getOther(bUuid).equals(aUuid)) {
                list.set(i, aData);
                break;
            }
        }

        if (i == size) {
            list.add(aData);
        }

        return aData;
    }

    public boolean has(
            final @NonNull Player       player
    ) {
        return this.byUuid.containsKey(player.getUUID());
    }

    public boolean has(
            final @NonNull UUID         uuid
    ) {
        return this.byUuid.containsKey(uuid);
    }

    public @NonNull FriendshipTreeData get(
            final @NonNull Player       player
    ) {
        return this.get(
                player.getUUID(),
                FRIEND
        );
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
        if (data != null && data.getType().equals(type)) {
            return data;
        }

        final var fresh = new FriendshipTreeData(
                type,
                PlayerPair.of(this.me, uuid)
        );

        this.byUuid.put(uuid, fresh);
        this.serializable.add(fresh);
        return fresh;
    }

    public void drop() {
        this.byUuid.clear();
        this.serializable.clear();
    }

    public void compact() {
        final var iterator = this.serializable.iterator();
        while (iterator.hasNext()) {
            final var data = iterator.next();
            if (data.isEmpty()) {
                iterator.remove();
                this.byUuid.remove(data.getOther(this.me));
            }
        }
    }

    private List<FriendshipTreeData> serialize() {
        this.compact();
        return this.serializable;
    }

    private void tryExtract() {

        final var list      = this.serializable;
        final var size      = list.size();
        if (size == 0) {
            return;
        }

        final var map       = this.byUuid;

        final var first     = list.getFirst();
        final var frl       = first.getRelation();
        final var l         = frl.getLeft();
        final var r         = frl.getRight();

        if (size == 1) {
            map.put(l, first);
            map.put(r, first);
            return; // do it later
        }

        for (final var data : list) {
            final var relation = data.getRelation();

            if (relation.equals(frl)) { // something goes wrong here
                continue;
            }

            if (this.me != null) {
                map.put(relation.getOther(this.me), data);
                continue;
            }

            if (relation.getLeft().equals(l) || relation.getRight().equals(l)) {
                this.me = l;
            } else {
                this.me = r;
            }

            map.put(relation.getOther(this.me), data);

        }
    }

    private void setup(final @NonNull UUID me) {
        if (this.me != null) {
            return;
        }

        this.me = me;
        this.byUuid.remove(me);
    }

    public void sendInvite(
            final @NonNull Player requester,
            final int node
    ) {
        this.invites.put(requester.getUUID(), node);
    }

    public boolean hasInvite(
            final @NonNull Player requester
    ) {
        return this.invites.containsKey(requester.getUUID());
    }

    public int getInvite(
            final @NonNull Player requester
    ) {
        return this.invites.getOrDefault(requester.getUUID(), -1);
    }

    public void removeInvite(
            final @NonNull Player requester
    ) {
        this.invites.removeInt(requester.getUUID());
    }
}
