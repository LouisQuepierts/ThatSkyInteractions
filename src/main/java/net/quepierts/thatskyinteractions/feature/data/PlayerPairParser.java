package net.quepierts.thatskyinteractions.feature.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import lombok.experimental.UtilityClass;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.codec.StreamCodec;
import net.quepierts.thatskyinteractions.core.model.PlayerPair;

@UtilityClass
public class PlayerPairParser {

    public static final Codec<PlayerPair> CODEC
            = RecordCodecBuilder.create(instance -> instance.group(
                    UUIDUtil.CODEC.fieldOf("left").forGetter(PlayerPair::getLeft),
                    UUIDUtil.CODEC.fieldOf("right").forGetter(PlayerPair::getRight)
            ).apply(instance, PlayerPair::new));

    public static final StreamCodec<ByteBuf, PlayerPair> STREAM_CODEC
            = StreamCodec.composite(
                    UUIDUtil.STREAM_CODEC,
                    PlayerPair::getLeft,
                    UUIDUtil.STREAM_CODEC,
                    PlayerPair::getRight,
                    PlayerPair::new
            );

}
