package net.quepierts.thatskyinteractions.infra.animation.backend.uniform;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;

@Getter
@RequiredArgsConstructor(staticName = "of")
public final class UniformInstance<T extends UniformParameter> {

    private final T parameter;
    private final UniformBuffer buffer;

    public static <T extends UniformParameter> UniformInstance<T> of(@NonNull final T parameter) {
        return new UniformInstance<>(
                parameter,
                parameter.create()
        );
    }

    public void upload() {
        this.parameter.upload(this.buffer);
    }

}
