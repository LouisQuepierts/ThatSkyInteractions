package net.quepierts.thatskyinteractions.feature.friendship;

import it.unimi.dsi.fastutil.objects.ObjectArrayFIFOQueue;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.minecraft.resources.Identifier;
import net.quepierts.thatskyinteractions.core.friendship.model.FriendshipTreeDefinition;
import net.quepierts.thatskyinteractions.core.friendship.model.FriendshipTreeNode;
import net.quepierts.veynir.core.util.ArrayIterator;
import net.quepierts.veynir.core.util.LocationLookup;
import org.jspecify.annotations.NonNull;

import java.util.Arrays;
import java.util.Iterator;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class FriendshipTree implements Iterable<FriendshipTreeNode> {

    public static FriendshipTree of(final @NonNull FriendshipTreeDefinition definition) {

        final var nodes             = definition.nodes();
        final var size              = nodes.size();

        final var order             = new String[size];
        final var parents           = new int[size];
        final var levels            = new int[size];
        final var ordinal           = new FriendshipTreeNode[size];
        final var branches          = new FriendshipTreeNode.Branch[size];

        Arrays.fill(parents, -1);
        Arrays.fill(levels, 0);
        Arrays.fill(branches, FriendshipTreeNode.Branch.MIDDLE);

        final var queue             = new ObjectArrayFIFOQueue<String>();
        queue                       .enqueue(definition.root());

        var read                    = 0;
        var write                   = 0;

        while (!queue.isEmpty()) {
            final var name          = queue.dequeue();
            final var node          = nodes.get(name);

            if (node == null) {
                throw new IllegalArgumentException("Node " + name + " does not exist.");
            }

            final var hasLeft       = !node.left().isEmpty();
            final var hasRight      = !node.right().isEmpty();

            if (branches[read] != FriendshipTreeNode.Branch.MIDDLE && (hasLeft || hasRight)) {
                throw new IllegalArgumentException("Node " + name + " is not on middle branch.");
            }

            var left                = -1;
            var middle              = -1;
            var right               = -1;

            if (hasLeft) {
                left                = ++ write;
                parents[left]       = read;
                levels[left]        = levels[read] + 1;
                branches[left]      = FriendshipTreeNode.Branch.LEFT;
                queue.enqueue(node.left());
            }

            if (!node.middle().isEmpty()) {
                middle              = ++ write;
                parents[middle]     = read;
                levels[middle]      = levels[read] + 1;
                queue.enqueue(node.middle());
            }

            if (hasRight) {
                right               = ++ write;
                parents[right]      = read;
                levels[right]       = levels[read] + 1;
                branches[right]     = FriendshipTreeNode.Branch.RIGHT;
                queue.enqueue(node.right());
            }

            order[read]       = name;
            ordinal[read]     = new FriendshipTreeNode(
                                name,
                                Identifier.parse(node.type()),
                                left, right, middle,
                                parents[read],
                                levels[read],
                                node.metadata(),
                                node.cost(),
                                branches[read]
            );
            read ++;
        }

        final var lookup        = LocationLookup.of(order);

        return new FriendshipTree(
                lookup,
                ordinal,
                levels[size - 1] + 1
        );
    }

    @Getter
    private final LocationLookup        lookup;

    private final FriendshipTreeNode[]  ordinal;

    private final int                   height;

    @Override
    public @NonNull Iterator<FriendshipTreeNode> iterator() {
        return new ArrayIterator<>(this.ordinal);
    }

    public FriendshipTreeNode getRoot() {
        return this.ordinal[0];
    }

    public FriendshipTreeNode get(int location) {
        return this.ordinal[location];
    }

    public int size() {
        return this.ordinal.length;
    }

    public int height() {
        return this.height;
    }
}
