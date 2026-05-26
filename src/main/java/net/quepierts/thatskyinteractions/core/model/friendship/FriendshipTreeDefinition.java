package net.quepierts.thatskyinteractions.core.model.friendship;

import java.util.Map;

public record FriendshipTreeDefinition(
        Map<String, TreeNodeDefinition> nodes,
        String                          root
) { }
