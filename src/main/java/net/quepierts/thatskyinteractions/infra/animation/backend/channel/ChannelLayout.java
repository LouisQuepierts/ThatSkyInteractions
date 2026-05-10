package net.quepierts.thatskyinteractions.infra.animation.backend.channel;

import lombok.RequiredArgsConstructor;
import net.quepierts.thatskyinteractions.infra.util.ArrayIterator;
import org.jspecify.annotations.NonNull;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@RequiredArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public final class ChannelLayout implements Iterable<String> {

    private final String[] names;

    public static Builder builder() {
        return new Builder();
    }

    public int getChannelId(String name) {
        for (int i = 0; i < names.length; i++) {
            if (names[i].equals(name)) {
                return i;
            }
        }
        return -1;
    }

    public int getChannelCount() {
        return this.names.length;
    }

    public int getBufferSize() {
        return this.names.length << 2;
    }

    @Override
    public @NonNull Iterator<String> iterator() {
        return new ArrayIterator<>(this.names);
    }

    public static final class Builder {

        private final List<String> names = new ArrayList<>();

        private Builder() { }

        public Builder add(String name) {
            this.names.add(name);
            return this;
        }

        public ChannelLayout build() {
            return new ChannelLayout(this.names.toArray(new String[0]));
        }

    }

}
