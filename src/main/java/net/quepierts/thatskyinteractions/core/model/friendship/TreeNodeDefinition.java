package net.quepierts.thatskyinteractions.core.model.friendship;

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
