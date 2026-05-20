package net.quepierts.thatskyinteractions.infra.animation.backend.skeleton.pass;

import net.quepierts.thatskyinteractions.infra.animation.backend.skeleton.pipeline.SkeletonContext;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.jspecify.annotations.NonNull;

public final class ParentOverridePass extends SkeletonPass {

    private final int src;
    private final int dst;

    private final int bones;

    // ubo format:
    // fint  parent[]       0
    // bool  enable         1
    private final int ubo;

    public ParentOverridePass(
            final String name,
            final int src,
            final int dst,
            final int bones,
            final int ubo
    ) {
        super(name);
        this.src        = src;
        this.dst        = dst;
        this.bones      = bones;
        this.ubo        = ubo;
    }

    @Override
    public void execute(@NonNull final SkeletonContext context) {
        final var uniform   = context.getUniformBuffer(this.ubo);
        final var enable    = uniform.readBool(1);

        final var src       = context.getPoseBuffer(this.src);
        final var dst       = context.getPoseBuffer(this.dst);

        if (!enable) {
            dst.copy(src);
            return;
        }

        final var reader    = uniform.getRawReader();

        final var pParent   = 0;

        final var size      = this.bones;
        final var parents   = new float[size];
        final var matrices  = new Matrix4f[size];

        reader.readFloat(pParent, size, parents);

        final var position  = new Vector3f();
        final var rotation  = new Quaternionf();
        final var scale     = new Vector3f();

        final var local = new Matrix4f();

        for (int i = 0; i < size; i++) {
            final var parent    = (int) parents[i];

            final var srcView   = src.get(i);
            final var dstView   = dst.get(i);

            srcView             .getPosition(position);
            srcView             .getRotation(rotation);
            srcView             .getScale(scale);

            local               .identity()
                                .translate(position)
                                .rotate(rotation)
                                .scale(scale);

            var mat             = new Matrix4f(local);
            if (parent != -1) {
                mat             .set(matrices[parent])
                                .mul(local);

                mat             .getTranslation(position);
                mat             .getNormalizedRotation(rotation);
                mat             .getScale(scale);
            }

            matrices[i]         = mat;

            dstView             .setPosition(position);
            dstView             .setRotation(rotation);
            dstView             .setScale(scale);

        }
    }
}
