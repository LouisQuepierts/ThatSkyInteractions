package net.quepierts.thatskyinteractions.infra.animation.backend.skeleton.pass.definition;

import net.quepierts.thatskyinteractions.infra.animation.backend.skeleton.pass.FetchPass;
import net.quepierts.thatskyinteractions.infra.animation.backend.skeleton.pass.SkeletonPass;
import net.quepierts.thatskyinteractions.infra.animation.backend.skeleton.pipeline.SkeletonPipelineCompileContext;
import org.jspecify.annotations.NonNull;

public final class FetchPassDefinition extends SkeletonPassDefinition {

    private String provider;
    private String target;

    public FetchPassDefinition(final String name) {
        super(name);
    }

    public FetchPassDefinition src(final String provider) {
        this.provider = provider;
        return this;
    }

    public FetchPassDefinition dst(final String target) {
        this.target = target;
        return this;
    }

    @Override
    public SkeletonPass compile(@NonNull final SkeletonPipelineCompileContext context) {
        final var provider = context.getProviderLocation(this.provider);
        final var target   = context.getBufferLocation(this.target);

        return new FetchPass(this.getName(), provider, target);
    }
}
