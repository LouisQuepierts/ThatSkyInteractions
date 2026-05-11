package net.quepierts.thatskyinteractions.infra.animation.backend.channel;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.quepierts.thatskyinteractions.infra.util.ArrayIterator;
import net.quepierts.thatskyinteractions.infra.util.LocationLookup;
import org.jspecify.annotations.NonNull;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@RequiredArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public final class ChannelLayout implements Iterable<String> {

    @Getter
    private final LocationLookup lookup;

    public static Builder builder() {
        return new Builder();
    }

    public int getChannelId(String name) {
        return this.lookup.find(name);
    }

    public int getChannelCount() {
        return this.lookup.size();
    }

    public int getBufferSize() {
        return this.lookup.size() << 2;
    }

    @Override
    public @NonNull Iterator<String> iterator() {
        return this.lookup.iterator();
    }

    public static final class Builder {

        private final List<String> names = new ArrayList<>();

        private Builder() { }

        public Builder add(String name) {
            this.names.add(name);
            return this;
        }

        public ChannelLayout build() {
            var array   = this.names.toArray(String[]::new);
            var lookup  = LocationLookup.of(array);
            return new ChannelLayout(lookup);
        }

    }

}
