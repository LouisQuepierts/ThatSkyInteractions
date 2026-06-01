package net.quepierts.thatskyinteractions.core.model.interaction;

import java.util.List;

public record InteractionDefinition(
        int                                 levels,
        String                              icon,
        List<InteractionDefinitionEntry>    interactions
) {
}
