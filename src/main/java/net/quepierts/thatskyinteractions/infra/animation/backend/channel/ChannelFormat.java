package net.quepierts.thatskyinteractions.infra.animation.backend.channel;

import com.google.common.collect.ImmutableMap;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.quepierts.thatskyinteractions.infra.util.LocationLookup;
import org.jspecify.annotations.NonNull;

import java.util.Iterator;
import java.util.List;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class ChannelFormat implements Iterable<ChannelFormatElement> {

    @Getter
    private final LocationLookup                lookup;

    @Getter
    private final List<ChannelFormatElement>    elements;

    private final int[]                         offsetByLocation;
    private final int[]                         offsetByElement;

    @Getter
    private final int                           attributeSize;

    public int getOffset(ChannelFormatElement element) {
        return this.offsetByElement[element.id()];
    }

    public int getOffset(int location) {
        return this.offsetByLocation[location];
    }

    public ChannelFormatElement getElement(int location) {
        return this.elements.get(location);
    }

    public static Builder builder() {
        return new Builder();
    }

    @Override
    public @NonNull Iterator<ChannelFormatElement> iterator() {
        return this.elements.iterator();
    }

    public static final class Builder {

        private final   ImmutableMap.Builder<String, ChannelFormatElement> elements = ImmutableMap.builder();

        private final   IntList     offsets = new IntArrayList();
        private         int         offset = 0;

        private Builder() { }

        public Builder add(String name, ChannelFormatElement element) {
            this.elements   .put(name, element);
            this.offsets    .add(this.offset);
            this.offset     += element.size();
            return this;
        }

        public ChannelFormat build() {
            var map             = this.elements.build();
            var names           = LocationLookup.of(map.keySet());
            var elements        = map.values().asList();

            var byLocation      = new int[elements.size()];
            var byElement       = new int[32];

            for (int i = 0; i < elements.size(); i++) {
                var element     = elements.get(i);
                var id          = element.id();
                var offset      = this.offsets.getInt(i);

                byElement[id]     = offset;
                byLocation[i]   = offset;
            }

            return new ChannelFormat(
                    names,
                    elements,
                    byLocation,
                    byElement,
                    this.offset
            );
        }

    }

}
