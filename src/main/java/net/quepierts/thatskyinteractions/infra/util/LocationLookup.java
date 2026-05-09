package net.quepierts.thatskyinteractions.infra.util;

import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;

import java.util.Collection;
import java.util.Iterator;

@RequiredArgsConstructor
public final class LocationLookup implements Iterable<String> {

    public static LocationLookup of(@NotNull Collection<String> collection) {
        return new LocationLookup(collection.toArray(String[]::new));
    }

    private final String[] names;

    public int find(String name) {
        for (int i = 0; i < names.length; i++) {
            if (names[i].equals(name)) {
                return i;
            }
        }

        return -1;
    }

    public boolean has(String name) {
        return this.find(name) != -1;
    }

    public String name(int location) {
        return this.names[location];
    }

    @Override
    public @NonNull Iterator<String> iterator() {
        return new ArrayIterator<>(this.names);
    }

    public int size() {
        return this.names.length;
    }
}
