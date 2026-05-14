package net.quepierts.thatskyinteractions.infra.animation.adapter;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import net.quepierts.thatskyinteractions.infra.animation.backend.pipeline.AnimationResultView;
import org.jspecify.annotations.NonNull;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class ChannelBinding {

    private final Entry[] entries;

    public static @NonNull Builder builder() {
        return new Builder();
    }

    public void apply(AnimationResultView view) {
        for (var entry : this.entries) {
            view.read(entry.channel(), entry.consumer());
        }
    }

    record Entry(
            int         channel,
            Consumer4f  consumer
    ) { }

    public static final class Builder {
        private final List<Entry> entries = new ArrayList<>();

        public @NonNull Builder bind(int channel, @NonNull Consumer4f consumer) {
            if (channel > 0) {
                this.entries.add(new Entry(channel, consumer));
            }
            return this;
        }

        public @NonNull ChannelBinding build() {
            return new ChannelBinding(entries.toArray(Entry[]::new));
        }
    }

}
