package net.quepierts.thatskyinteractions.feature.friendship;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.quepierts.thatskyinteractions.core.friendship.model.Branch;
import net.quepierts.thatskyinteractions.core.friendship.model.Cost;
import net.quepierts.thatskyinteractions.feature.friendship.behaviour.FriendshipBehaviour;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

// todo: construct tree by separated files, like advancement
public record FriendshipTreeFile(

        Optional<Identifier>        parent,
        Branch branch,

        FriendshipBehaviour         behaviour,

        Cost                        cost,
        Map<String, String>         metadata,

        int                         priority

) {

    public static final Codec<FriendshipTreeFile> CODEC
            = RecordCodecBuilder.create(instance -> instance.group(
                    Identifier.CODEC.optionalFieldOf("parent").forGetter(FriendshipTreeFile::parent),
                    FriendshipTreeParser.BRANCH_CODEC.optionalFieldOf("branch", Branch.MIDDLE).forGetter(FriendshipTreeFile::branch),
                    FriendshipBehaviour.CODEC.fieldOf("behaviour").forGetter(FriendshipTreeFile::behaviour),
                    FriendshipTreeParser.COST_CODEC.optionalFieldOf("price", Cost.FREE).forGetter(FriendshipTreeFile::cost),
                    Codec.unboundedMap(Codec.STRING, Codec.STRING).optionalFieldOf("metadata", Map.of()).forGetter(FriendshipTreeFile::metadata),
                    Codec.INT.optionalFieldOf("priority", 0).forGetter(FriendshipTreeFile::priority)
            ).apply(instance, FriendshipTreeFile::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, FriendshipTreeFile> STREAM_CODEC
            = StreamCodec.composite(
                    ByteBufCodecs.optional(Identifier.STREAM_CODEC),
                    FriendshipTreeFile::parent,
            FriendshipTreeParser.BRANCH_STREAM_CODEC,
                    FriendshipTreeFile::branch,
                    FriendshipBehaviour.STREAM_CODEC,
                    FriendshipTreeFile::behaviour,
                    FriendshipTreeParser.COST_STREAM_CODEC,
                    FriendshipTreeFile::cost,
                    ByteBufCodecs.map(
                            HashMap::new,
                            ByteBufCodecs.STRING_UTF8,
                            ByteBufCodecs.STRING_UTF8
                    ),
                    FriendshipTreeFile::metadata,
                    ByteBufCodecs.INT,
                    FriendshipTreeFile::priority,
                    FriendshipTreeFile::new
            );

    public boolean root() {
        return this.parent.isEmpty();
    }

}
