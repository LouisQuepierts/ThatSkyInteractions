package net.quepierts.thatskyinteractions.infra.animation.model;

import lombok.Getter;
import net.quepierts.thatskyinteractions.infra.util.ArrayIterator;
import org.jspecify.annotations.NonNull;

import java.util.Iterator;

public final class Timeline implements Iterable<Segment> {
    private final Segment[] segments;

    @Getter
    private final int max;

    public Timeline(
            Segment[] segments,
            int max
    ) {
        this.segments = segments;
        this.max = max;
    }

    public Segment get(int index) {
        return this.segments[index];
    }

    public Segment next(int index) {
        return this.segments[Math.min(index + 1, this.max)];
    }

    public int find(float time) {
        int left = 0;
        int right = this.max - 1;

        while (left <= right) {
            int mid = (left + right) / 2;
            if (time < this.segments[mid].start()) {
                right = mid - 1;
            } else if (time > this.segments[mid].end()) {
                left = mid + 1;
            } else {
                return mid;
            }
        }

        return left;
    }

    public boolean valid(int index) {
        return index >= 0 && index < this.max;
    }

    @Override
    public @NonNull Iterator<Segment> iterator() {
        return new ArrayIterator<>(this.segments);
    }
}
