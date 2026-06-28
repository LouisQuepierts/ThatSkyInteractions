package net.quepierts.thatskyinteractions.feature.animation;

import lombok.Getter;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.quepierts.thatskyinteractions.core.scene.Scene;
import net.quepierts.thatskyinteractions.feature.registry.AttachmentTypes;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

@Getter
public final class PlayerAnimationAttachment {

    private final PlayerAnimationController controller;
    private final Scene                     scene;

    public static PlayerAnimationAttachment getAttachment(
            final @NonNull LivingEntity avatar
    ) {
        return avatar.getData(AttachmentTypes.PLAYER_ANIMATION);
    }

    public static @Nullable PlayerAnimationAttachment getExistingAttachment(
            final @NonNull Entity       entity
    ) {
        return entity.getExistingDataOrNull(AttachmentTypes.PLAYER_ANIMATION);
    }

    public PlayerAnimationAttachment(final @NonNull LivingEntity avatar) {
        this.controller = new PlayerAnimationController(avatar);
        this.scene      = new Scene();
    }

    public void setupScene(
            final @NonNull LivingEntity   avatar
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
