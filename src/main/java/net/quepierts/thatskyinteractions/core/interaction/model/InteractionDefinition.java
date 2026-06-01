package net.quepierts.thatskyinteractions.core.interaction.model;

import java.util.List;

public record InteractionDefinition(
        int                                 levels,
        String                              icon,
        List<InteractionDefinitionEntry>    interactions
) {
}
