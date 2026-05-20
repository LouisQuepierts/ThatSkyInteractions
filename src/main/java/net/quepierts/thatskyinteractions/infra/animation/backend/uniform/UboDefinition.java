package net.quepierts.thatskyinteractions.infra.animation.backend.uniform;

import com.google.common.collect.ImmutableList;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.quepierts.thatskyinteractions.infra.util.LocationLookup;

import java.util.*;
import java.util.regex.Pattern;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class UboDefinition {

    private final List<Entry>                       entries;
    private final LocationLookup                    lookup;
    private final int                               size;

    public int getUniformOffset(int location) {
        return this.entries.get(location).offset();
    }

    public int getUniformOffset(int location, int index) {
        var entry   = this.entries.get(location);
        return entry.offset() + index * entry.type().getSize();
    }

    public int getUniformOffset(String name) {
        var index = 0;

        if (name.endsWith("]")) {
            // extract n in [n]
            var shift   = name.substring(name.lastIndexOf('[') + 1, name.length() - 1);
            index = Integer.parseInt(shift);
        }

        for (var entry : this.entries) {
            if (entry.name().equals(name)) {
                if (index >= entry.length()) {
                    throw new IllegalArgumentException("Index out of bounds: " + index);
                }
                return entry.offset() + index * entry.type().getSize();
            }
        }

        return -1;
    }

    public int getUniformLocation(String name) {
        return this.lookup.find(name);
    }

    public Entry getEntry(int location) {
        return this.entries.get(location);
    }

    public static Builder builder() {
        return new Builder();
    }

    public record Entry (
            String      name,
            UniformType type,
            int         offset,
            int         length
    ) { }

    public static final class Builder {
        private static final Pattern                NAME_PATTERN    = Pattern.compile("[a-zA-Z_][a-zA-Z0-9_]*");

        private final   List<UniformDescription>    descriptions    = new ArrayList<>();
        private final   Set<String>                 names           = new HashSet<>();
        private         boolean                     optimize        = false;

        private Builder() { }

        public Builder withUniform(String name, UniformType type) {
            this.validate(name);

            this.descriptions.add(new UniformDescription(name, type, 1));
            this.names      .add(name);
            return          this;
        }

        public Builder withArray(String name, UniformType type, int length) {
            this.validate(name);

            if (length < 1) {
                throw new IllegalArgumentException("Array length must be greater than 0");
            }

            this.descriptions.add(new UniformDescription(name, type, length));
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
                final var array = description.length() > 1;
                offset      = align(offset, Math.max(description.type().getAlign(), array ? 4 : 1));
                entries     .add(new Entry(description.name(), description.type(), offset, description.length()));
                names       .add(description.name());

                offset      += description.type().getSize() * description.length();
            }

            return new UboDefinition(
                    entries.build(),
                    LocationLookup.of(names),
                    offset
            );
        }

        private void validate(String name) {
            if (name == null) {
                throw new IllegalArgumentException("Uniform name cannot be null");
            }

            if (this.names.contains(name)) {
                throw new IllegalArgumentException("Duplicated uniform name: " + name);
            }

            if (!NAME_PATTERN.matcher(name).matches()) {
                throw new IllegalArgumentException("Invalid uniform name: " + name);
            }
        }

        private int align(int size, int alignment) {
            return (size + alignment - 1) & -alignment;
        }
    }
}
