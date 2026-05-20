package net.quepierts.thatskyinteractions.infra.animation.backend.skeleton.pass;

import net.quepierts.thatskyinteractions.infra.animation.backend.skeleton.pipeline.SkeletonContext;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.jspecify.annotations.NonNull;

public final class PivotPass extends SkeletonPass {

    private final int src;
    private final int dst;

    private final int bones;
    private final int ubo;

    public PivotPass(
            final String    name,
            final int       src,
            final int       dst,
            final int       bones,
            final int       ubo
    ) {
        super(name);

        this.src    = src;
        this.dst    = dst;

        this.bones  = bones;
        this.ubo    = ubo;
    }

    @Override
    public void execute(@NonNull final SkeletonContext context) {
        final var pivots    = context.getUniformBuffer(this.ubo);
        final var reader    = pivots.getRawReader();
        final var src       = context.getPoseBuffer(this.src);
        final var dst       = context.getPoseBuffer(this.dst);

        // vec3 + bool
        final var pivot     = new float[4];
        final var position  = new Vector3f();
        final var rotation  = new Quaternionf();
        final var scale     = new Vector3f();

        final var tmp       = new Vector3f();

        for (int i = 0; i < this.bones; i++) {
            reader              .readFloat(i * 4, 4, pivot);

            final var srcView   = src.get(i);
            final var dstView   = dst.get(i);

            srcView             .getPosition(position);
            srcView             .getRotation(rotation);
            srcView             .getScale(scale);

            if (pivot[3] != 0.0f) {

                // position += pivot - R * (S * pivot)
                tmp                 .set(pivot)
                                    .mul(scale);
                rotation            .transform(tmp);
                position            .add(pivot[0], pivot[1], pivot[2])
                                    .sub(tmp);
            }
            
            dstView             .setPosition(position);
            dstView             .setRotation(rotation);
            dstView             .setScale(scale);

        }
    }
}
