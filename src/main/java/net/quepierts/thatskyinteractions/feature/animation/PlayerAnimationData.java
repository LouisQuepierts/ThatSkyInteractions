package net.quepierts.thatskyinteractions.feature.animation;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import io.netty.buffer.ByteBuf;
import lombok.Getter;
import net.minecraft.network.codec.StreamCodec;
import net.quepierts.thatskyinteractions.feature.network.StreamCodecUtils;

@Getter
public final class PlayerAnimationData {

    public static final Codec<PlayerAnimationData> CODEC
            = MapCodec.unitCodec(PlayerAnimationData::new);

    public static final StreamCodec<ByteBuf, PlayerAnimationData> STREAM_CODEC
            = StreamCodecUtils.unit(PlayerAnimationData::new);

    private final HumanoidAnimationState animation;

    public PlayerAnimationData() {
        this.animation = HumanoidAnimationState._default();
    }

}
