package net.quepierts.thatskyinteractions.core.friendship.model;

import java.util.Map;

public record TreeNodeDefinition(
        String              left,
        String              middle,
        String              right,
        String              type,
        Cost                cost,
        Map<String, String> metadata
) {
}
