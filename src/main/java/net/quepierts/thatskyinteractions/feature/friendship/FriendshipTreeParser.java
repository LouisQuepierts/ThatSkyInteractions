package net.quepierts.thatskyinteractions.feature.friendship;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import lombok.experimental.UtilityClass;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.quepierts.thatskyinteractions.core.model.Currency;
import net.quepierts.thatskyinteractions.core.model.friendship.Cost;
import net.quepierts.thatskyinteractions.core.model.friendship.FriendshipTreeDefinition;
import net.quepierts.thatskyinteractions.core.model.friendship.TreeNodeDefinition;

import java.util.HashMap;
import java.util.Map;

@UtilityClass
public class FriendshipTreeParser {

    public static final Codec<Cost> COST_CODEC
            = Codec.INT.xmap(
                    i -> new Cost(Currency.WHITE_CANDLE, i),
                    Cost::price
            )
            .withAlternative(RecordCodecBuilder.create(instance -> instance.group(
                    Codec.STRING.fieldOf("currency").xmap(
                            Currency::parse,
                            Currency::name
                    ).forGetter(Cost::currency),
                    Codec.INT.fieldOf("price").forGetter(Cost::price)
            ).apply(instance, Cost::new)));

    public static final Codec<TreeNodeDefinition> NODE_CODEC
            = RecordCodecBuilder.create(instance -> instance.group(
                    Codec.STRING.optionalFieldOf("left", "").forGetter(TreeNodeDefinition::left),
                    Codec.STRING.optionalFieldOf("middle", "").forGetter(TreeNodeDefinition::middle),
                    Codec.STRING.optionalFieldOf("right", "").forGetter(TreeNodeDefinition::right),
                    Codec.STRING.fieldOf("type").forGetter(TreeNodeDefinition::type),
                    COST_CODEC.optionalFieldOf("price", Cost.FREE).forGetter(TreeNodeDefinition::cost),
                    Codec.unboundedMap(
                            Codec.STRING, Codec.STRING
                    ).optionalFieldOf("metadata", Map.of()).forGetter(TreeNodeDefinition::metadata)
            ).apply(instance, TreeNodeDefinition::new));

    public static final Codec<FriendshipTreeDefinition> TREE_CODEC
            = RecordCodecBuilder.create(instance -> instance.group(
                    Codec.unboundedMap(Codec.STRING, NODE_CODEC).fieldOf("nodes").forGetter(FriendshipTreeDefinition::nodes),
                    Codec.STRING.fieldOf("root").forGetter(FriendshipTreeDefinition::root)
            ).apply(instance, FriendshipTreeDefinition::new));

    public static final StreamCodec<ByteBuf, Cost> COST_STREAM_CODEC
            = StreamCodec.composite(
                    ByteBufCodecs.VAR_INT.map(
                            Currency::parse,
                            Currency::ordinal
                    ),
                    Cost::currency,
                    ByteBufCodecs.VAR_INT,
                    Cost::price,
                    Cost::new
            );

    public static final StreamCodec<ByteBuf, TreeNodeDefinition> NODE_STREAM_CODEC
            = StreamCodec.composite(
                    ByteBufCodecs.STRING_UTF8,
                    TreeNodeDefinition::left,
                    ByteBufCodecs.STRING_UTF8,
                    TreeNodeDefinition::middle,
                    ByteBufCodecs.STRING_UTF8,
                    TreeNodeDefinition::right,
                    ByteBufCodecs.STRING_UTF8,
                    TreeNodeDefinition::type,
                    COST_STREAM_CODEC,
                    TreeNodeDefinition::cost,
                    ByteBufCodecs.map(
                            HashMap::new,
                            ByteBufCodecs.STRING_UTF8,
                            ByteBufCodecs.STRING_UTF8
                    ),
                    TreeNodeDefinition::metadata,
                    TreeNodeDefinition::new
            );

    public static final StreamCodec<ByteBuf, FriendshipTreeDefinition> TREE_STREAM_CODEC
            = StreamCodec.composite(
                    ByteBufCodecs.map(
                            HashMap::new,
                            ByteBufCodecs.STRING_UTF8,
                            NODE_STREAM_CODEC
                    ),
                    FriendshipTreeDefinition::nodes,
                    ByteBufCodecs.STRING_UTF8,
                    FriendshipTreeDefinition::root,
                    FriendshipTreeDefinition::new
            );

}
