package net.quepierts.thatskyinteractions.infra.animation.backend.skeleton.pass.definition;

import net.quepierts.thatskyinteractions.infra.animation.backend.skeleton.SkeletonLayout;
import net.quepierts.thatskyinteractions.infra.animation.backend.skeleton.pass.PivotPass;
import net.quepierts.thatskyinteractions.infra.animation.backend.skeleton.pass.SkeletonPass;
import net.quepierts.thatskyinteractions.infra.animation.backend.skeleton.pipeline.SkeletonPipelineCompileContext;
import org.jspecify.annotations.NonNull;

public class PivotPassDefinition extends SkeletonPassDefinition {

    private String src;
    private String dst;

    public PivotPassDefinition(final String name) {
        super(name);
    }

    public PivotPassDefinition src(final String src) {
        this.src = src;
        return this;
    }

    public PivotPassDefinition dst(final String dst) {
        this.dst = dst;
        return this;
    }

    @Override
    public SkeletonPass compile(@NonNull final SkeletonPipelineCompileContext context) {
        final var layout    = context.getLayout();
        final var bones     = layout.size();
        final var location  = context.getUboLocation("SkeletonPivots");

        final var src       = context.getBufferLocation(this.src);
        final var dst       = context.getBufferLocation(this.dst);

        return new PivotPass(this.getName(), src, dst, bones, location);
    }
}
