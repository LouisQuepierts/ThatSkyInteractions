package net.quepierts.thatskyinteractions.core.animation.model;

import java.util.Map;

public record PlayerAnimationDefinition(
        String                          type,
        Map<String, SourceDefinition>   sources
) { }