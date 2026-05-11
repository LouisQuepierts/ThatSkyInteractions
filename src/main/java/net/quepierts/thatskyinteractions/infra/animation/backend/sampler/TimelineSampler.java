package net.quepierts.thatskyinteractions.infra.animation.backend.sampler;

import net.quepierts.thatskyinteractions.infra.animation.backend.buffer.WritableBuffer;
import net.quepierts.thatskyinteractions.infra.animation.backend.channel.ChannelFormatElement;
import net.quepierts.thatskyinteractions.infra.animation.backend.channel.ChannelLayout;
import net.quepierts.thatskyinteractions.infra.animation.backend.source.TimelineSource;
import net.quepierts.thatskyinteractions.infra.animation.backend.pipeline.AnimationContext;
import net.quepierts.thatskyinteractions.infra.animation.backend.model.Timeline;
import net.quepierts.thatskyinteractions.infra.animation.interpolator.Interpolator4f;
import org.jspecify.annotations.NonNull;

public final class TimelineSampler extends AnimationSampler<TimelineSource> {

    private static final Interpolator4f[]   BUILTIN_INTERPOLATIONS  = new Interpolator4f[]{
            Interpolator4f.LINEAR,
            Interpolator4f.CATMULL_ROM,
            Interpolator4f.CONSTANT
    };

    public static TimelineSampler of(
            @NonNull TimelineSource  source,
            @NonNull ChannelLayout   layout
    ) {
        var channels        = source.getChannels();
        var mapping         = new int[channels.size()];

        var i               = 0;
        for (var channel    : channels) {
            mapping[i]      = layout.getChannelId(channel);

            i               ++;
        }

        return              new TimelineSampler(
                                source,
                                mapping
                            );
    }

    private final int[] mapping;

    private TimelineSampler(
            TimelineSource  source,
            int[]           mapping
    ) {
        super(source);
        this.mapping = mapping;
    }

    @Override
    public void sample(
            AnimationContext        context,
            WritableBuffer          target
    ) {
        var time        = context.getProgress();
        var source      = this.getSource();
        var localTime   = source.isLoop() ?
                        time % source.getDuration() :
                        time;

        var state       = context.getAnimationState();
        var rewind      = time < state.lastProgress;
        state.lastProgress = time;

        var format      = context.getChannelFormat();
        var cursorOff   = format.getOffset(ChannelFormatElement.CURSOR);
        var attrSize    = format.getAttributeSize();
        var attributes  = state.getChannelAttribute();

        var attrBase    = 0;

        for (int i = 0; i < this.mapping.length; i++) {
            var channel = this.mapping[i];

            if (!context.getSamplerMask(channel)) {
                continue;
            }

            var cursorAddr = attrBase + cursorOff;
            if (rewind) {
                attributes.write(cursorAddr, 0);
            }

            this.sampleTimeline(
                    context,
                    source.getTimeline(i),
                    localTime,
                    cursorAddr,
                    channel << 2,
                    target
            );

            attrBase += attrSize;
        }
    }

    private void sampleTimeline(
            AnimationContext        context,
            Timeline                timeline,
            float                   time,
            int                     cursorAddr,
            int                     offset,
            WritableBuffer          target
    ) {
        var state           = context.getAnimationState();
        var attribute       = state.getChannelAttribute();

        var cursor          = attribute.readInt(cursorAddr);
        var ends            = timeline.ends();

        if (time < ends[cursor]) {

            this.sampleSegment(
                    context,
                    time,
                    cursor,
                    offset,
                    target
            );
            return;
        }

        cursor              = binarySearch(timeline, time);
        attribute           .write(cursorAddr, cursor);

        this.sampleSegment(
                context,
                time,
                cursor,
                offset,
                target
        );
    }

    private void sampleSegment(
            AnimationContext        context,
            float                   time,
            int                     cursor,
            int                     offset,
            WritableBuffer          target
    ) {

        var source          = this.getSource();
        var timeline        = source.getTimeline(cursor);
        var constants       = source.getConstants();
        var parameters      = context.getParameterBuffer();

        var a0              = timeline.addr0()[cursor];
        var a1              = timeline.addr1()[cursor];

        var b0              = Timeline.Address.isParameter(a0) ?
                            parameters : constants;

        var b1              = Timeline.Address.isParameter(a1) ?
                            parameters : constants;

        var o0              = Timeline.Address.offset(a0);
        var o1              = Timeline.Address.offset(a1);

        var lerp            = timeline.interpolation()[cursor];
        BUILTIN_INTERPOLATIONS[lerp].interpolate(
                time,
                o0,
                o1,
                b0,
                b1,
                offset,
                target
        );
    }

    private static int binarySearch(Timeline timeline, float time) {
        int left            = 0;
        int right           = timeline.size() - 1;

        var starts          = timeline.starts();
        var ends            = timeline.ends();

        while (left         <= right) {
            int mid         = (left + right) / 2;
            if (time        < starts[mid]) {
                right       = mid - 1;
            } else if (time > ends[mid]) {
                left        = mid + 1;
            } else {
                return      mid;
            }
        }

        return              left;
    }
}
