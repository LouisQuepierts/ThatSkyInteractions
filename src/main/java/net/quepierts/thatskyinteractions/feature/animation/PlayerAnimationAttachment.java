package net.quepierts.thatskyinteractions.feature.animation;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import io.netty.buffer.ByteBuf;
import lombok.Getter;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.Avatar;
import net.neoforged.neoforge.attachment.AttachmentSyncHandler;
import net.neoforged.neoforge.attachment.IAttachmentHolder;
import net.quepierts.thatskyinteractions.core.scene.Scene;
import net.quepierts.thatskyinteractions.feature.network.StreamCodecUtils;
import net.quepierts.thatskyinteractions.feature.registry.AttachmentTypes;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

@Getter
public final class PlayerAnimationAttachment {

    public static final Codec<PlayerAnimationAttachment> CODEC
            = MapCodec.unitCodec(PlayerAnimationAttachment::new);

    public static final StreamCodec<ByteBuf, PlayerAnimationAttachment> STREAM_CODEC
            = StreamCodecUtils.unit(PlayerAnimationAttachment::new);

    private final PlayerAnimationController controller;
    private final Scene                     scene;

    public static PlayerAnimationAttachment getAttachment(
            final @NonNull Avatar   avatar
    ) {
        return avatar.getData(AttachmentTypes.PLAYER_ANIMATION);
    }

    public PlayerAnimationAttachment() {
        this.controller = new PlayerAnimationController();
        this.scene      = new Scene();
    }

    public void setupScene(
            final @NonNull Avatar   avatar
    ) {
        final var position = new Vector3f(
                (float) avatar.getX(),
                (float) avatar.getY(),
                (float) avatar.getZ()
        );
        final var rotation = new Quaternionf().rotateY(avatar.yHeadRot);
        this.scene.fromObjectTransform(position, rotation);
    }

}
