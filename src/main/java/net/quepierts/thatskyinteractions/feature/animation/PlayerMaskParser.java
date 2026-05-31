package net.quepierts.thatskyinteractions.feature.animation;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import lombok.experimental.UtilityClass;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.quepierts.thatskyinteractions.core.animation.model.PlayerBone;
import net.quepierts.thatskyinteractions.core.animation.model.PlayerMask;

@UtilityClass
public class PlayerMaskParser {

    public static final Codec<PlayerMask> CODEC
            = Codec.INT.xmap(
                    PlayerMask::direct,
                    PlayerMask::getMask
            ).withAlternative(
                    Codec.STRING.xmap(
                            PlayerBone::of,
                            PlayerBone::getName
                    ).listOf(
                            0,
                            PlayerBone.size()
                    ).xmap(
                            PlayerMask::of,
                            PlayerMask::toList
                    )
            );

    public static final StreamCodec<ByteBuf, PlayerMask> STREAM_CODEC
            = StreamCodec.composite(
                    ByteBufCodecs.INT,
                    PlayerMask::getMask,
                    PlayerMask::direct
            );

}
