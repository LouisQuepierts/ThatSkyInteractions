package net.quepierts.thatskyinteractions.feature.animation;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import io.netty.buffer.ByteBuf;
import lombok.Getter;
import net.minecraft.network.codec.StreamCodec;
import net.quepierts.thatskyinteractions.feature.network.StreamCodecUtils;

@Getter
public final class PlayerAnimationAttachment {

    public static final Codec<PlayerAnimationAttachment> CODEC
            = MapCodec.unitCodec(PlayerAnimationAttachment::new);

    public static final StreamCodec<ByteBuf, PlayerAnimationAttachment> STREAM_CODEC
            = StreamCodecUtils.unit(PlayerAnimationAttachment::new);

    private final PlayerAnimationController controller;

    public PlayerAnimationAttachment() {
        this.controller = new PlayerAnimationController();
    }

    public HumanoidAnimationState getAnimation() {
        return this.controller.getState();
    }

}
