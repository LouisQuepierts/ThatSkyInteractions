package net.quepierts.thatskyinteractions.infra.animation.backend.skeleton.pass.definition;

import net.quepierts.thatskyinteractions.infra.animation.backend.skeleton.pass.ParentOverridePass;
import net.quepierts.thatskyinteractions.infra.animation.backend.skeleton.pass.SkeletonPass;
import net.quepierts.thatskyinteractions.infra.animation.backend.skeleton.pipeline.SkeletonPipelineCompileContext;
import org.jspecify.annotations.NonNull;

public final class ParentOverridePassDefinition extends SkeletonPassDefinition {

    public static final String REQUIRED_UBO = "ParentOverrides";

    private String src;
    private String dst;

    public ParentOverridePassDefinition(final String name) {
        super(name);
    }

    public ParentOverridePassDefinition src1(final String src) {
        this.src = src;
        return this;
    }

    public ParentOverridePassDefinition dst(final String dst) {
        this.dst = dst;
        return this;
    }

    @Override
    public SkeletonPass compile(@NonNull final SkeletonPipelineCompileContext context) {

        final var layout    = context.getLayout();
        final var bones     = layout.size();
        final var location  = context.getUboLocation(REQUIRED_UBO);

        final var src       = context.getBufferLocation(this.src);
        final var dst       = context.getBufferLocation(this.dst);

        return new ParentOverridePass(this.getName(), src, dst, bones, location);
    }
}
