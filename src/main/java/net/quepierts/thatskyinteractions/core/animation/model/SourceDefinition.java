package net.quepierts.thatskyinteractions.core.animation.model;

import org.jspecify.annotations.NonNull;

import java.util.Optional;

public record SourceDefinition(
        String              source,
        float               fadeIn,
        float               fadeOut,
        Optional<String>    namespace
) {

    public static SourceDefinition of(@NonNull final String source) {
        return new SourceDefinition(source, 0.0f, 0.0f, Optional.empty());
    }

}
