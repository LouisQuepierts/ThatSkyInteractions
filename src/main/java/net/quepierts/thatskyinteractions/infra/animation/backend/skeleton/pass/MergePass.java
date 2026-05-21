package net.quepierts.thatskyinteractions.infra.animation.backend.skeleton.pass;

import net.quepierts.thatskyinteractions.infra.animation.backend.skeleton.pipeline.SkeletonContext;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.jspecify.annotations.NonNull;

public final class MergePass extends SkeletonPass {

    private final int src0;
    private final int src1;
    private final int dst;

    private final int bones;

    public MergePass(
            final String name,
            final int src0,
            final int src1,
            final int dst,
            final int bones
    ) {
        super(name);
        this.src0   = src0;
        this.src1   = src1;
        this.dst    = dst;
        this.bones = bones;
    }

    @Override
    public void execute(@NonNull final SkeletonContext context) {
        final var src0      = context.getPoseBuffer(this.src0);
        final var src1      = context.getPoseBuffer(this.src1);
        final var dst       = context.getPoseBuffer(this.dst);

        final var position0 = new Vector3f();
        final var rotation0 = new Quaternionf();
        final var scale0    = new Vector3f();
        final var position1 = new Vector3f();
        final var rotation1 = new Quaternionf();
        final var scale1    = new Vector3f();

        for (int i = 0; i < this.bones; i++) {
            final var view0 = src0.get(i);
            final var view1 = src1.get(i);
            final var viewD = dst.get(i);

            view0.getPosition(position0);
            view0.getRotation(rotation0);
            view0.getScale(scale0);

            view1.getPosition(position1);
            view1.getRotation(rotation1);
            view1.getScale(scale1);

            viewD.setPosition(position0.add(position1));
            viewD.setRotation(rotation0.mul(rotation1));
            viewD.setScale(scale0.mul(scale1));
        }
    }
}
