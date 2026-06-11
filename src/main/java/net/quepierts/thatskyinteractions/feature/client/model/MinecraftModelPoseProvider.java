package net.quepierts.thatskyinteractions.feature.client.model;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import net.quepierts.veynir.backend.skeleton.pipeline.*;
import net.quepierts.veynir.core.SkeletonState;
import org.joml.Quaternionf;
import org.jspecify.annotations.NonNull;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class MinecraftModelPoseProvider implements SkeletonPoseProvider {

    public static final String REQUIRED_UBO = "OverrideMask";

    private final MinecraftModelSkeleton skeleton;
    private final Quaternionf quaternion;
    private final float[] override;
    private final int ubo;

    public static MinecraftModelPoseProvider of(
            @NonNull MinecraftModelSkeleton skeleton,
            @NonNull SkeletonPipeline       pipeline
    ) {
        return new MinecraftModelPoseProvider(
                skeleton,
                new Quaternionf(),
                new float[skeleton.getLayout().size()],
                pipeline.getReflection().location("ubo." + REQUIRED_UBO)
        );
    }

    @Override
    public void fetch(
            @NonNull final SkeletonContext      context,
            @NonNull final SkeletonPoseBuffer   target
    ) {
        final var buffer        = context.getUniformBuffer(this.ubo);
        final var reader        = buffer.getRawReader();

        final var quaternion    = this.quaternion;
        final var overrides     = this.override;
        reader.readFloat(0, overrides.length, overrides);

        final var root          = target.get(0);
        root.setPosition(0, 0, 0);
        root.setRotation(quaternion.identity());
        root.setScale(1, 1, 1);

        final var state = context.getState();
        for (final var entry : this.skeleton.getEntries()) {
            final var loc       = entry.id();

            if (!state.getMask(loc)) {
                continue;
            }

            final var part      = entry.part();
            final var view      = target.get(loc);

            if (overrides[loc] != 0.0f) {
                view.setPosition(part.x, part.y, part.z);
                view.setRotation(quaternion.identity()
                        .rotateZYX(part.zRot, part.yRot, part.xRot)
                );
                view.setScale(part.xScale, part.yScale, part.zScale);
            } else {
                final var pose  = part.getInitialPose();
                view.setPosition(pose.x(), pose.y(), pose.z());
                view.setRotation(quaternion.identity()
                        .rotateZYX(pose.zRot(), pose.yRot(), pose.xRot())
                );
                view.setScale(pose.xScale(), pose.yScale(), pose.zScale());
            }
        }
    }
}
