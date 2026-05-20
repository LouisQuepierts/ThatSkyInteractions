package net.quepierts.thatskyinteractions.infra.animation.backend.uniform;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class UniformParameter {

    private final UboDefinition definition;

    public abstract void upload(@NonNull final UniformBuffer buffer);

    public UniformBuffer create() {
        return new UniformBuffer(this.definition);
    }

}
