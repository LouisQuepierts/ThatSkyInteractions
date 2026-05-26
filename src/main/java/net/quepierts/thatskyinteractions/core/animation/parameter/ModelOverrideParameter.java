package net.quepierts.thatskyinteractions.core.animation.parameter;

import net.quepierts.thatskyinteractions.infra.animation.backend.skeleton.SkeletonLayout;
import net.quepierts.thatskyinteractions.infra.animation.backend.uniform.UboDefinition;
import net.quepierts.thatskyinteractions.infra.animation.backend.uniform.UniformBuffer;
import net.quepierts.thatskyinteractions.infra.animation.backend.uniform.UniformParameter;
import net.quepierts.thatskyinteractions.infra.animation.backend.uniform.UniformType;
import org.jspecify.annotations.NonNull;

public final class ModelOverrideParameter extends UniformParameter {

    private ModelOverrideParameter(final UboDefinition definition, final SkeletonLayout layout, final float[] override) {
        super(definition);
        this.layout = layout;
        this.override = override;
    }

    private final SkeletonLayout layout;
    private final float[] override;

    public static ModelOverrideParameter of(final @NonNull SkeletonLayout layout) {
        final var definition    = UboDefinition.builder()
                .withArray("overrides", UniformType.BOOL, layout.size())
                .build();
        final var override      = new float[layout.size()];
        return new ModelOverrideParameter(definition, layout, override);
    }

    public void override(
            @NonNull    final String    name,
                        final boolean   override
    ) {
        final var id = this.layout.id(name);

        if (id != -1) {
            this.override[id] = override ? 1.0f : 0.0f;
        }
    }

    @Override
    public void upload(final @NonNull UniformBuffer buffer) {
        final var writer = buffer.getRawWriter();
        writer.write(0, this.override);
    }
}
