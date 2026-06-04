package net.quepierts.thatskyinteractions.core.friendship.model;

import java.util.Map;

public record FriendshipTreeDefinition(
        Map<String, TreeNodeDefinition> nodes,
        String                          root
) { }
