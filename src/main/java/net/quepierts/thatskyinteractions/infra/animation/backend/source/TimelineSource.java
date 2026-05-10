package net.quepierts.thatskyinteractions.infra.animation.backend.source;

import lombok.Getter;
import net.quepierts.thatskyinteractions.infra.animation.backend.buffer.AnimationBuffer;
import net.quepierts.thatskyinteractions.infra.animation.backend.model.Timeline;
import net.quepierts.thatskyinteractions.infra.util.LocationLookup;

@Getter
public final class TimelineSource extends AnimationSource {

    private final Timeline[]        timelines;
    private final AnimationBuffer   constants;
    private final boolean           loop;
    private final float             duration;

    public TimelineSource(
            String[]            channels,
            Timeline[]          timelines,
            AnimationBuffer     constants,
            boolean             loop,
            float               duration
    ) {
        super(new LocationLookup(channels));

        this.timelines          = timelines;
        this.constants          = constants;
        this.loop               = loop;
        this.duration           = duration;
    }

    public Timeline getTimeline(int channel) {
        return this.timelines[channel];
    }
}
