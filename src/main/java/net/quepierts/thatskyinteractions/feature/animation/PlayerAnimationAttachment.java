package net.quepierts.thatskyinteractions.feature.animation;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import io.netty.buffer.ByteBuf;
import lombok.Getter;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.Avatar;
import net.quepierts.thatskyinteractions.feature.network.StreamCodecUtils;
import net.quepierts.thatskyinteractions.feature.registry.AttachmentTypes;
import org.jspecify.annotations.NonNull;

@Getter
public final class PlayerAnimationAttachment {

    public static final Codec<PlayerAnimationAttachment> CODEC
            = MapCodec.unitCodec(PlayerAnimationAttachment::new);

    public static final StreamCodec<ByteBuf, PlayerAnimationAttachment> STREAM_CODEC
            = StreamCodecUtils.unit(PlayerAnimationAttachment::new);

    private final PlayerAnimationController controller;

    public static PlayerAnimationAttachment getAttachment(@NonNull final Avatar avatar) {
        return avatar.getData(AttachmentTypes.PLAYER_ANIMATION);
    }

    public PlayerAnimationAttachment() {
        this.controller = new PlayerAnimationController();
    }

    public HumanoidAnimationState getAnimation() {
        return this.controller.getState();
    }

}
