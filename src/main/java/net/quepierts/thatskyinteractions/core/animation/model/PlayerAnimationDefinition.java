package net.quepierts.thatskyinteractions.core.animation.model;

import java.util.Map;

public record PlayerAnimationDefinition(
        String                          type,
        String                          override,
        Map<String, SourceDefinition>   sources,
        PlayerMask                      unlock,
        boolean                         abortable,
        boolean                         restrictMotion,
        boolean                         rootMotion,
        String                          layer
) {
    public static final String DEFAULT_LAYER = "thatskyinteractions:main";
}