package net.quepierts.thatskyinteractions.core.model.animation;

import java.util.Map;

public record PlayerAnimationDefinition(
        String                          type,
        Map<String, SourceDefinition>   sources
) { }