package net.quepierts.thatskyinteractions.feature.friendship;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import lombok.experimental.UtilityClass;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.quepierts.thatskyinteractions.core.friendship.model.Branch;
import net.quepierts.thatskyinteractions.core.friendship.model.Cost;
import net.quepierts.thatskyinteractions.core.friendship.model.FriendshipTreeDefinition;
import net.quepierts.thatskyinteractions.core.friendship.model.TreeNodeDefinition;
import net.quepierts.thatskyinteractions.core.model.Currency;

import java.util.HashMap;
import java.util.Map;

@UtilityClass
public class FriendshipTreeParser {

    public static final Codec<Cost> COST_CODEC
            = RecordCodecBuilder.<Cost>create(instance -> instance.group(
                    Codec.STRING.fieldOf("currency").xmap(
                            Currency::parse,
                            Currency::name
                    ).forGetter(Cost::currency),
                    Codec.INT.fieldOf("amount").forGetter(Cost::amount)
            ).apply(instance, Cost::new)).withAlternative(
                    Codec.INT.xmap(
                            i -> new Cost(Currency.WHITE_CANDLE, i),
                            Cost::amount
                    )
            );

    public static final StreamCodec<ByteBuf, Cost> COST_STREAM_CODEC
            = StreamCodec.composite(
                    ByteBufCodecs.VAR_INT.map(
                            Currency::parse,
                            Currency::ordinal
                    ),
                    Cost::currency,
                    ByteBufCodecs.VAR_INT,
                    Cost::amount,
                    Cost::new
            );

    public static final Codec<Branch> BRANCH_CODEC
            = Codec.STRING.xmap(
                    Branch::fromName,
                    Branch::toName
            );

    public static final StreamCodec<ByteBuf, Branch> BRANCH_STREAM_CODEC
            = ByteBufCodecs.BYTE.map(Branch::fromByte, Branch::toByte);

}
