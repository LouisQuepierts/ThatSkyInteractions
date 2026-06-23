package net.quepierts.thatskyinteractions.feature.friendship;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.quepierts.thatskyinteractions.core.friendship.model.Cost;
import net.quepierts.thatskyinteractions.core.friendship.model.FriendshipTreeNode;
import net.quepierts.thatskyinteractions.feature.friendship.behaviour.FriendshipBehaviour;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

// todo: construct tree by separated files, like advancement
public record FriendshipAction(

        Optional<Identifier>        parent,
        FriendshipTreeNode.Branch   branch,

        FriendshipBehaviour         behaviour,

        Cost                        cost,
        Map<String, String>         metadata

) {

    public static final Codec<FriendshipTreeNode.Branch> BRANCH_CODEC
            = Codec.STRING.xmap(
                    FriendshipTreeNode.Branch::fromName,
                    FriendshipTreeNode.Branch::toName
            );

    public static final StreamCodec<ByteBuf, FriendshipTreeNode.Branch> BRANCH_STREAM_CODEC
            = ByteBufCodecs.BYTE.map(FriendshipTreeNode.Branch::fromByte, FriendshipTreeNode.Branch::toByte);

    public static final Codec<FriendshipAction> CODEC
            = RecordCodecBuilder.create(instance -> instance.group(
                    Identifier.CODEC.optionalFieldOf("parent").forGetter(FriendshipAction::parent),
                    BRANCH_CODEC.fieldOf("branch").forGetter(FriendshipAction::branch),
                    FriendshipBehaviour.CODEC.fieldOf("behaviour").forGetter(FriendshipAction::behaviour),
                    FriendshipTreeParser.COST_CODEC.optionalFieldOf("cost", Cost.FREE).forGetter(FriendshipAction::cost),
                    Codec.unboundedMap(Codec.STRING, Codec.STRING).optionalFieldOf("metadata", Map.of()).forGetter(FriendshipAction::metadata)
            ).apply(instance, FriendshipAction::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, FriendshipAction> STREAM_CODEC
            = StreamCodec.composite(
                    ByteBufCodecs.optional(Identifier.STREAM_CODEC),
                    FriendshipAction::parent,
                    BRANCH_STREAM_CODEC,
                    FriendshipAction::branch,
                    FriendshipBehaviour.STREAM_CODEC,
                    FriendshipAction::behaviour,
                    FriendshipTreeParser.COST_STREAM_CODEC,
                    FriendshipAction::cost,
                    ByteBufCodecs.map(
                            HashMap::new,
                            ByteBufCodecs.STRING_UTF8,
                            ByteBufCodecs.STRING_UTF8
                    ),
                    FriendshipAction::metadata,
                    FriendshipAction::new
            );

}
