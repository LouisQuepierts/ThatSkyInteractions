package net.quepierts.thatskyinteractions.feature.data.event;

import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayFIFOQueue;
import lombok.RequiredArgsConstructor;
import net.minecraft.resources.Identifier;
import net.neoforged.bus.api.Event;
import net.quepierts.thatskyinteractions.feature.data.DataSyncManager;
import org.jspecify.annotations.NonNull;

import java.util.*;

@RequiredArgsConstructor
public final class RegisterSyncManagerEvent extends Event {

    private final Map<Identifier, DataSyncManager<?>>   pending              = new HashMap<>();
    private final Map<Identifier, Set<Identifier>>      afterDependencies    = new HashMap<>();
    private final Map<Identifier, Set<Identifier>>      beforeDependencies   = new HashMap<>();
    private final List<DataSyncManager<?>>              managers;

    public void register(
            @NonNull DataSyncManager<?> manager
    ) {
        if (this.pending.put(manager.getIdentifier(), manager) != null) {
            throw new IllegalArgumentException("Duplicate manager: " + manager.getIdentifier());
        }
    }

    public void registerAfter(
            @NonNull DataSyncManager<?> manager,
            @NonNull Identifier         identifier
    ) {
        final var key = manager.getIdentifier();
        if (this.pending.put(key, manager) != null) {
            throw new IllegalArgumentException("Duplicate manager: " + key);
        }

        this.afterDependencies.computeIfAbsent(key, _ -> new HashSet<>()).add(identifier);
    }

    public void registerBefore(
            @NonNull DataSyncManager<?> manager,
            @NonNull Identifier         identifier
    ) {
        final var key = manager.getIdentifier();
        if (this.pending.put(key, manager) != null) {
            throw new IllegalArgumentException("Duplicate manager: " + key);
        }

        this.beforeDependencies.computeIfAbsent(key, _ -> new HashSet<>()).add(identifier);
    }

    public void register() {

        final var size      = this.pending.size();
        final var inDeg     = new Object2IntOpenHashMap<Identifier>(size);
        final var graph     = new HashMap<Identifier, List<DataSyncManager<?>>>(size);
        final var allIds    = this.pending.keySet();

        for (final var id : allIds) {
            inDeg.put(id, 0);
            graph.put(id, new ArrayList<>());
        }

        for (final var id : allIds) {
            for (final var other : this.afterDependencies.getOrDefault(id, Collections.emptySet())) {
                inDeg.addTo(id, 1);
                graph.get(other).add(this.pending.get(id));
            }
        }

        for (final var id : allIds) {
            for (final var other : this.beforeDependencies.getOrDefault(id, Collections.emptySet())) {
                inDeg.addTo(other, 1);
                graph.get(id).add(this.pending.get(other));
            }
        }

        final var queue     = new ObjectArrayFIFOQueue<Identifier>();
        for (final var entry : inDeg.object2IntEntrySet()) {
            if (entry.getIntValue() == 0) {
                queue.enqueue(entry.getKey());
            }
        }

        this.managers       .clear();

        var progressed      = 0;
        while (!queue.isEmpty()) {
            final var id    = queue.dequeue();
            this.managers.add(this.pending.get(id));
            progressed      ++;

            for (final var neighbor : graph.get(id)) {
                var degree  = inDeg.mergeInt(neighbor.getIdentifier(), -1, Integer::sum);
                if (degree == 0) {
                    queue.enqueue(neighbor.getIdentifier());
                }
            }
        }

        if (progressed != size) {
            throw new IllegalStateException("Cyclic dependency detected");
        }

    }

    private record Entry(
            DataSyncManager<?>      manager,
            Identifier              other
    ) { }
}
