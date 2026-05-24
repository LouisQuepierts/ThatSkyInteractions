package net.quepierts.thatskyinteractions.feature.client.model;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import net.quepierts.thatskyinteractions.infra.animation.backend.skeleton.pipeline.SkeletonContext;
import net.quepierts.thatskyinteractions.infra.animation.backend.skeleton.pipeline.SkeletonPipeline;
import net.quepierts.thatskyinteractions.infra.animation.backend.skeleton.pipeline.SkeletonPoseBuffer;
import net.quepierts.thatskyinteractions.infra.animation.backend.skeleton.pipeline.SkeletonPoseProvider;
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

        for (final var entry : this.skeleton.getEntries()) {
            final var part      = entry.part();
            final var loc       = entry.id();
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
