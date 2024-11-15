package net.quepierts.thatskyinteractions.common.data.attachment.component;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.quepierts.thatskyinteractions.ThatSkyInteractions;
import net.quepierts.thatskyinteractions.common.data.Codecs;
import net.quepierts.thatskyinteractions.common.data.PlayerPair;
import net.quepierts.thatskyinteractions.common.data.attachment.UserDataAttachment;
import net.quepierts.thatskyinteractions.common.data.tree.InteractTreeInstance;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public record RelationshipComponent(
        Map<UUID, InteractTreeInstance> friends,
        Set<UUID> blackList
) implements IComponent<RelationshipComponent> {
    public static final ResourceLocation FRIEND_INTERACT_TREE = ThatSkyInteractions.getLocation("friend");

    public static final Codec<RelationshipComponent> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codecs.map(UUIDUtil.CODEC, InteractTreeInstance.CODEC).fieldOf("friends").forGetter(RelationshipComponent::friends),
            Codecs.set(UUIDUtil.CODEC).fieldOf("blackList").forGetter(RelationshipComponent::blackList)
    ).apply(instance, RelationshipComponent::new));

    public static final StreamCodec<ByteBuf, RelationshipComponent> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.map(HashMap::new, UUIDUtil.STREAM_CODEC, InteractTreeInstance.STREAM_CODEC),
            RelationshipComponent::friends,
            ByteBufCodecs.collection(HashSet::new, UUIDUtil.STREAM_CODEC),
            RelationshipComponent::blackList,
            RelationshipComponent::new
    );

    @NotNull
    public static RelationshipComponent createInstance() {
        return new RelationshipComponent(new HashMap<>(), new HashSet<>());
    }

    @Nullable
    public static Pair<InteractTreeInstance, InteractTreeInstance> getRelevantInstance(PlayerPair pair, ServerLevel level) {
        Player left = level.getPlayerByUUID(pair.getLeft());
        Player right = level.getPlayerByUUID(pair.getRight());

        if (left == null || right == null) {
            return null;
        }

        return Pair.of(
                UserDataAttachment.getAttachment(left).getRelationship().get(left, pair.getRight()),
                UserDataAttachment.getAttachment(right).getRelationship().get(right, pair.getLeft())
        );
    }

    public boolean isBlocked(UUID player) {
        return this.blackList.contains(player);
    }

    public void block(UUID player) {
        this.blackList.add(player);
    }

    public void unblock(UUID player) {
        this.blackList.remove(player);
    }

    public boolean isFriend(UUID player) {
        return this.friends.containsKey(player);
    }

    public InteractTreeInstance get(Player player, UUID other) {
        PlayerPair pair = new PlayerPair(other, player.getUUID());
        return friends.computeIfAbsent(other, key -> new InteractTreeInstance(pair, FRIEND_INTERACT_TREE));
    }

    private List<Pair<UUID, InteractTreeInstance>> toList() {
        return this.friends().entrySet().stream()
                .map(entry -> Pair.of(entry.getKey(), entry.getValue()))
                .toList();
    }

    public void setInfo(@NotNull RelationshipComponent relationship) {
        this.friends.clear();
        this.friends.putAll(relationship.friends);
    }

    @Override
    public void update() {

    }
}
