package net.quepierts.thatskyinteractions.infra.animation.backend.skeleton.pass;

import net.quepierts.thatskyinteractions.infra.animation.backend.skeleton.pipeline.SkeletonContext;
import org.jspecify.annotations.NonNull;

public final class FetchPass extends SkeletonPass {

    private final int provider;
    private final int target;

    public FetchPass(
            final String name,
            final int provider,
            final int target
    ) {
        super(name);
        this.provider = provider;
        this.target = target;
    }

    @Override
    public void execute(@NonNull final SkeletonContext context) {
        final var provider  = context.getProvider(this.provider);
        final var target    = context.getPoseBuffer(this.target);

        provider.fetch(context, target);
    }

}
