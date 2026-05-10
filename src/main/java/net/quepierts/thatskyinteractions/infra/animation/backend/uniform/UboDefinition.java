package net.quepierts.thatskyinteractions.infra.animation.backend.uniform;

import com.google.common.collect.ImmutableList;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.quepierts.thatskyinteractions.infra.util.LocationLookup;

import java.util.*;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class UboDefinition {

    private final List<Entry>                       entries;
    private final LocationLookup                    lookup;
    private final int                               size;

    public int getUniformOffset(int location) {
        return this.entries.get(location).offset();
    }

    public int getUniformOffset(String name) {
        for (var entry : this.entries) {
            if (entry.name().equals(name)) {
                return entry.offset();
            }
        }

        return -1;
    }

    public int getUniformLocation(String name) {
        return this.lookup.find(name);
    }

    public static Builder builder() {
        return new Builder();
    }

    public record Entry (
            String      name,
            UniformType type,
            int         offset
    ) { }

    public static final class Builder {
        private final   List<UniformDescription>    descriptions    = new ArrayList<>();
        private final   Set<String>                 names           = new HashSet<>();
        private         boolean                     optimize        = false;

        private Builder() { }

        public Builder withUniform(String name, UniformType type) {
            if (this.names.contains(name)) {
                throw new IllegalArgumentException("Duplicated uniform name: " + name);
            }

            this.descriptions.add(new UniformDescription(name, type));
            this.names      .add(name);
            return          this;
        }

        public Builder optimize() {
            this.optimize = true;
            return this;
        }

        public UboDefinition build() {
            var offset      = 0;
            var entries     = ImmutableList.<Entry>builder();
            var names       = new ArrayList<String>();

            if (this.optimize) {
                this.descriptions.sort(Comparator.comparingInt(v -> v.type().getSize()));
            }

            for (var description : this.descriptions) {
                offset      = align(offset, description.type().getAlign());
                entries     .add(new Entry(description.name(), description.type(), offset));
                names       .add(description.name());

                offset      += description.type().getSize();
            }

            return new UboDefinition(
                    entries.build(),
                    LocationLookup.of(names),
                    offset
            );
        }

        private int align(int size, int alignment) {
            return (size + alignment - 1) & -alignment;
        }
    }
}
