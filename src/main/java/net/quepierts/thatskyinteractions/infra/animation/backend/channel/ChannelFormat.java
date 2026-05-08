package net.quepierts.thatskyinteractions.infra.animation.backend.channel;

import com.google.common.collect.ImmutableMap;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class ChannelFormat {

    private final List<String>                  names;
    private final List<ChannelFormatElement>    elements;
    private final int[]                         offsetsByElements;

    private final int                           attributeSize;

    public static Builder builder() {
        return new Builder();
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
            var map         = this.elements.build();
            var names       = map.keySet().asList();
            var elements    = map.values().asList();
            var offsets     = new int[32];

            for (int i = 0; i < elements.size(); i++) {
                var element = elements.get(i);
                var id      = element.id();
                var offset  = this.offsets.getInt(i);

                offsets[id] = offset;
            }

            return new ChannelFormat(names, elements, offsets, this.offset);
        }

    }

}
