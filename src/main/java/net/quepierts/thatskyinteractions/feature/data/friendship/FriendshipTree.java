package net.quepierts.thatskyinteractions.feature.data.friendship;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import net.quepierts.thatskyinteractions.core.model.friendship.FriendshipTreeDefinition;
import net.quepierts.thatskyinteractions.infra.util.LocationLookup;
import org.jspecify.annotations.NonNull;

import java.util.Arrays;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class FriendshipTree {

    public static FriendshipTree of(@NonNull final FriendshipTreeDefinition definition) {

        final var nodes     = definition.nodes();
        final var size      = nodes.size();
        final var lookup    = LocationLookup.of(nodes.keySet());
        final var ordinal   = new FriendshipTreeNode[size];

        final var parents   = new int[size];
        final var branches  = new FriendshipTreeNode.Branch[size];
        Arrays.fill(parents, -1);
        Arrays.fill(branches, FriendshipTreeNode.Branch.MIDDLE);

        var i = 0;
        for (final var name : lookup) {
            final var node      = nodes.get(name);
            final var location  = i;

            final var left      = lookup.find(node.left());
            final var middle    = lookup.find(node.middle());
            final var right     = lookup.find(node.right());

            final var branch    = branches[location];

            if (branch != FriendshipTreeNode.Branch.MIDDLE) {
                if (left != -1 || right != -1) {
                    throw new IllegalArgumentException("Invalid friendship tree definition: " + name);
                }
            }

            if (left != -1) {
                parents[left]   = location;
                branches[left]  = FriendshipTreeNode.Branch.LEFT;
            }

            if (middle != -1) {
                parents[middle]  = location;
                branches[middle] = FriendshipTreeNode.Branch.MIDDLE;
            }

            if (right != -1) {
                parents[right]   = location;
                branches[right]  = FriendshipTreeNode.Branch.RIGHT;
            }

            ordinal[i] = new FriendshipTreeNode(
                    name,
                    node.type(),
                    left,
                    middle,
                    right,
                    parents[location],
                    node.metadata(),
                    node.cost(),
                    branch
            );

            i ++;
        }

        return new FriendshipTree(lookup, ordinal);
    }

    private final LocationLookup lookup;
    private final FriendshipTreeNode[] ordinal;

}
