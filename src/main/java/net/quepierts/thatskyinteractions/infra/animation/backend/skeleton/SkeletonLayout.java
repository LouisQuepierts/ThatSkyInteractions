package net.quepierts.thatskyinteractions.infra.animation.backend.skeleton;

import it.unimi.dsi.fastutil.objects.ObjectArrayFIFOQueue;
import it.unimi.dsi.fastutil.objects.ObjectArraySet;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.quepierts.thatskyinteractions.infra.animation.backend.channel.ChannelLayout;
import net.quepierts.thatskyinteractions.infra.util.LocationLookup;
import org.jspecify.annotations.NonNull;

import java.util.*;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class SkeletonLayout {

    @Getter
    private final LocationLookup    bones;

    private final int[]             parents;
    private final int[]             childStarts;
    private final int[]             childFlat;

    public static Builder builder(@NonNull String root) {
        return new Builder(root);
    }

    public static Builder builder() {
        return new Builder("root");
    }

    public int id(String name) {
        return this.bones.find(name);
    }

    public String bone(int id) {
        return this.bones.name(id);
    }

    public int getParentId(int id) {
        return this.parents[id];
    }

    public int getChildCount(int id) {
        return this.childStarts[id + 1] - this.childStarts[id];
    }

    public int size() {
        return this.bones.size();
    }

    public void getChildrenIds(int parent, int[] out) {
        System.arraycopy(this.childFlat, this.childStarts[parent], out, 0, this.childStarts[parent + 1] - this.childStarts[parent]);
    }

    public @NonNull ChannelLayout toChannelLayout() {
        final var builder = ChannelLayout.builder();
        for (final var bone : this.bones) {
            builder.transform(bone);
        }
        return builder.build();
    }

    public static final class Builder {

        private final List<String>              bones       = new ArrayList<>();
        private final Map<String, String>       parents     = new HashMap<>();

        private final String                    root;

        private Builder(@NonNull String root) {
            this.root       = root;
            this.bones      .add(root);
        }

        public Builder bone(String name) {
            if (this.bones.contains(name)) {
                throw new IllegalArgumentException("Bone " + name + " already exists");
            }

            this.bones.add(name);
            this.parents.put(name, root);
            return this;
        }

        public Builder parent(String child, String parent) {
            if (root.equals(child)) {
                throw new IllegalArgumentException("Root bone cannot have parent");
            }

            if (!this.bones.contains(child)) {
                throw new IllegalArgumentException("Bone " + child + " does not exists");
            }

            if (!this.bones.contains(parent)) {
                throw new IllegalArgumentException("Bone " + parent + " does not exists");
            }

            this.parents.put(child, parent);
            return this;
        }

        public SkeletonLayout build() {

            final var children  = new HashMap<String, List<String>>();

            for (var entry : this.parents.entrySet()) {
                var child       = entry.getKey();
                var parent      = entry.getValue();

                children        .computeIfAbsent(parent, _ -> new ArrayList<>())
                                .add(child);
            }

            final var capacity  = this.bones.size() + 1;
            final var queue     = new ObjectArrayFIFOQueue<String>(capacity);
            final var order     = new ArrayList<String>(capacity);
            queue               .enqueue(root);

            while (!queue.isEmpty()) {
                final var bone  = queue.dequeue();
                order           .add(bone);

                children        .getOrDefault(bone, Collections.emptyList())
                                .forEach(queue::enqueue);
            }

            final var lookup    = LocationLookup.of(order);

            final var parents   = order.stream()
                                .map(this.parents::get)
                                .mapToInt(lookup::find)
                                .toArray();

            final var count     = new int[capacity];
            for (final var parent : parents) {
                if (parent != -1) {
                    count[parent]++;
                }
            }

            final var starts    = new int[capacity];
            var sum             = 0;
            for (var i = 0; i < capacity; i++) {
                starts[i] = sum;
                sum += count[i];
            }

            final var flat      = new int[sum];
            final var cursor    = new int[capacity];
            // starts -> cursor
            System.arraycopy(starts, 0, cursor, 0, capacity);
            for (var i = 0; i < sum; i++) {
                var parent      = parents[i];
                if (parent != -1) {
                    var pos     = cursor[parent]++;
                    flat[pos]   = i;
                }
            }

            return new SkeletonLayout(
                    lookup,
                    parents,
                    starts,
                    flat
            );
        }

    }

}
