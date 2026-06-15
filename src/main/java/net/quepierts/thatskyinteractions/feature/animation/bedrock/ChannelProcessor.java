package net.quepierts.thatskyinteractions.feature.animation.bedrock;

import org.jspecify.annotations.NonNull;

public interface ChannelProcessor {

    static ChannelProcessor namespaced(
            @NonNull String namespace,
            @NonNull String separator
    ) {

        final var prefix = namespace + separator;
        return new ChannelProcessor() {
            @Override
            public boolean discard(final @NonNull String name) {
                return !name.startsWith(prefix);
            }

            @Override
            public @NonNull String process(final @NonNull String name) {
                return name.substring(prefix.length());
            }
        };
    }

    static ChannelProcessor none() {
        return new Default();
    }

    boolean discard(final @NonNull String name);

    @NonNull String process(final @NonNull String name);

    class Default implements ChannelProcessor {
        @Override
        public boolean discard(final @NonNull String name) {
            return false;
        }

        @Override
        public @NonNull String process(final @NonNull String name) {
            return name;
        }
    }
}
