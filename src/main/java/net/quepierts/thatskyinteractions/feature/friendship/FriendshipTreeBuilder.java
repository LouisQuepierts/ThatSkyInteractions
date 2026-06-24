package net.quepierts.thatskyinteractions.feature.friendship;

import com.google.common.collect.ImmutableMap;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.objects.*;
import lombok.extern.slf4j.Slf4j;
import net.minecraft.resources.Identifier;
import net.quepierts.thatskyinteractions.core.friendship.model.Branch;
import net.quepierts.veynir.core.util.LocationLookup;
import org.jspecify.annotations.NonNull;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Slf4j
public final class FriendshipTreeBuilder {

    private final Map<Identifier, Node>   actions       = new Object2ObjectOpenHashMap<>();
    private final Map<Identifier, Node>   roots         = new Object2ObjectOpenHashMap<>();

    public void addAll(
            final @NonNull Map<Identifier, FriendshipTreeFile>  actions
    ) {
        actions.forEach(this::insert);
    }


    /*
    * rule:
    * */
    public void insert(
            final @NonNull Identifier                               identifier,
            final @NonNull FriendshipTreeFile action
    ) {

        final var root          = action.root();
        final var middle        = action.branch() == Branch.MIDDLE;
        if (root) {

            if (!middle) {
                // error with identifier
                log.error("Friendship action {} is not a root action", identifier);
                return;
            }
            final var id        = identifier.withPath(s -> {
                // remove contents after last '/'
                return s.substring(0, s.lastIndexOf('/'));
            });

            if (this.roots.containsKey(id)) {
                // duplicated root in same directory ${id}
                log.error("Directory {} has duplicated roots",  id);
                return;
            }

            final var node      = Node.of(identifier, action);
            this.roots          .put(id, node);
            this.actions        .put(identifier, node);

        } else {

            this.actions        .put(identifier, Node.of(identifier, action));

        }
    }

    /*
     * rule:
     * same branch: ok
     * different branch:
     * - parent.branch == MIDDLE -> ok
     * - parent.branch != MIDDLE -> discard and error
     *
     * if no parent: self.branch == MIDDLE, or else discard and error
     * */
    public @NonNull Map<Identifier, FriendshipTree> build() {

        for (final var node : this.actions.values()) {
            if (node.root) {
                continue;
            }

            final var identifier    = node.identifier;
            final var parentId      = node.action.parent().orElseThrow();
            final var parent        = this.actions.get(parentId); // normally exists

            if (parent == null) {
                log.error("Cound not find parent {} for {}, cuz it's non-existent", parentId, identifier);
                continue;
            }

            parent                  .insert(node);
        }

        final var builder   = ImmutableMap.<Identifier, FriendshipTree>builderWithExpectedSize(this.roots.size());

        for (final var entry : this.roots.entrySet()) {
            final var key   = entry.getKey();
            final var root  = entry.getValue();

            builder         .put(key, this.build(root));
        }

        return builder.build();

    }

    private FriendshipTree build(final @NonNull Node root) {

        final var order             = new ObjectArrayList<String>();
        final var parents           = new IntArrayList();
        final var levels            = new IntArrayList();
        final var ordinal           = new ObjectArrayList<FriendshipTreeNode>();

        parents                     .add(-1);
        levels                      .add(0);

        final var queue             = new ObjectArrayFIFOQueue<Node>();
        queue                       .enqueue(root);

        var read                    = 0;
        var write                   = 0;

        while (!queue.isEmpty()) {

            final var node          = queue.dequeue();
            final var identifier    = node.identifier;

            var left                = -1;
            var middle              = -1;
            var right               = -1;

            if (node.hasLeft()) {
                left                = ++ write;
                parents             .add(read);
                levels              .add(levels.getInt(read) + 1);
                queue               .enqueue(node.children[0]);
            }

            if (node.hasMiddle()) {
                middle              = ++ write;
                parents             .add(read);
                levels              .add(levels.getInt(read) + 1);
                queue               .enqueue(node.children[1]);
            }

            if (node.hasRight()) {
                right               = ++ write;
                parents             .add(read);
                levels              .add(levels.getInt(read) + 1);
                queue               .enqueue(node.children[2]);
            }

            final var name          = identifier.toString();
            order                   .add(name);
            ordinal                 .add(new FriendshipTreeNode(
                                            name,
                                            node.action.behaviour(),
                                            left,
                                            middle,
                                            right,
                                            parents.getInt(read),
                                            levels.getInt(read),
                                            node.action.metadata(),
                                            node.action.cost(),
                                            node.action.branch()
                                    ));

            read ++;
        }

        final var lookup        = LocationLookup.of(order);
        return                  new FriendshipTree(
                                        lookup,
                                        ordinal.toArray(FriendshipTreeNode[]::new),
                                        levels.getLast() + 1
                                );

    }

    private static final class Node implements Comparable<Node> {

        private final Node[]                    children    = new Node[3];
        private final Identifier                identifier;
        private final FriendshipTreeFile        action;
        private final boolean                   root;

        private       int                       priority;

        static Node of(
                final @NonNull Identifier       identifier,
                final @NonNull FriendshipTreeFile action
        ) {
            return new Node(
                    identifier,
                    action
            );
        }

        private Node(
                final @NonNull Identifier       identifier,
                final @NonNull FriendshipTreeFile action
        ) {
            this.identifier    = identifier;
            this.action        = action;
            this.root          = action.parent().isEmpty();
            this.priority      = action.priority();
        }

        /*
        * rule:
        * self.branch == MIDDLE -> ok
        * self.branch != MIDDLE -> self.branch == node.branch
        * */
        void insert(final @NonNull Node node) {

            this.insert(node, node.action.branch());

        }

        void insert(
                final @NonNull Node     node,
                final @NonNull Branch   branch
        ) {
            insert(node, this, branch);
        }

        static void insert(
                final @NonNull Node     node,
                final @NonNull Node     parent,
                final @NonNull Branch   branch
        ) {

            final var selfBranch    = parent.action.branch();

            if (selfBranch != Branch.MIDDLE && selfBranch != branch) {
                log.error("Friendship action {} is not allowed to be inserted into {}", node.identifier, parent.identifier);
                return;
            }

            final int ordinal   = branch.ordinal();
            Node current        = parent;
            Node insert         = node;
            Set<Node> visited   = new ObjectOpenHashSet<>();

            while (true) {
                if (!visited.add(current)) {
                    log.error("Circular dependency detected");
                    return; // skip
                }

                Node child                      = current.children[ordinal];
                if (child                       == null) {
                    current.children[ordinal]   = insert;
                    return;
                }

                if (insert.compareTo(child)     < 0) {
                    insert.priority             -= 100;
                    current.children[ordinal]   = insert;
                    current                     = insert;
                    insert                      = child;
                } else {
                    current                     = child;
                }
            }

        }

        boolean hasLeft() {
            return this.children[0] != null;
        }

        boolean hasMiddle() {
            return this.children[1] != null;
        }

        boolean hasRight() {
            return this.children[2] != null;
        }

        @Override
        public int hashCode() {
            return this.identifier.hashCode();
        }

        @Override
        public boolean equals(final Object obj) {
            // only check identifier
            return obj.getClass() == Node.class
                    && (((Node) obj)).identifier.equals(this.identifier);
        }

        @Override
        public int compareTo(final FriendshipTreeBuilder.@NonNull Node o) {
            return Integer.compare(this.priority, o.priority);
        }
    }

}
