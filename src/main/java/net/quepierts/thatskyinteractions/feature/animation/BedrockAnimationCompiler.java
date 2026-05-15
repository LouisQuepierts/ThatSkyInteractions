package net.quepierts.thatskyinteractions.feature.animation;

import it.unimi.dsi.fastutil.bytes.ByteArrayList;
import it.unimi.dsi.fastutil.floats.FloatArrayList;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.UtilityClass;
import net.minecraft.util.Mth;
import net.quepierts.thatskyinteractions.core.model.animation.BedrockAnimation;
import net.quepierts.thatskyinteractions.core.model.animation.BedrockKeyframe;
import net.quepierts.thatskyinteractions.core.model.animation.BedrockTimeline;
import net.quepierts.thatskyinteractions.infra.animation.backend.buffer.AnimationBuffer;
import net.quepierts.thatskyinteractions.infra.animation.backend.model.Timeline;
import net.quepierts.thatskyinteractions.infra.animation.backend.source.AnimationSource;
import net.quepierts.thatskyinteractions.infra.animation.backend.source.TimelineSource;
import org.joml.Vector3fc;
import org.jspecify.annotations.NonNull;

import java.util.ArrayList;

@UtilityClass
public class BedrockAnimationCompiler {

    public static @NonNull AnimationSource compile(@NonNull BedrockAnimation animation) {
        var bones               = animation.bones();
        var duration            = animation.length();

        var channels            = new ArrayList<String>();
        var timelines           = new ArrayList<Timeline>();

        var constants           = new FloatArrayList();

        for (var entry : bones.entrySet()) {
            var name            = entry.getKey();
            var bone            = entry.getValue();

            if (bone            .position()
                                .isPresent()) {

                channels        .add(name + ".position");
                var timeline    = compile(bone.position().get(), constants, duration, Channel.POSITION);
                timelines       .add(timeline);

            }

            if (bone            .rotation()
                                .isPresent()) {

                channels        .add(name + ".rotation");
                var timeline    = compile(bone.rotation().get(), constants, duration, Channel.ROTATION);
                timelines       .add(timeline);
            }

            if (bone            .scale()
                                .isPresent()) {

                channels        .add(name + ".scale");
                var timeline    = compile(bone.scale().get(), constants, duration, Channel.SCALE);
                timelines       .add(timeline);
            }
        }

        var size                = constants.size();
        var buffer              = new AnimationBuffer(size);
        System                  .arraycopy(
                                    constants.elements(), 0,
                                    buffer.getBuffer(), 0,
                                    size
                                );

        return                  new TimelineSource(
                                    channels.toArray(String[]::new),
                                    timelines.toArray(Timeline[]::new),
                                    buffer,
                                    /*animation.loop()*/ true,
                                    animation.length()
                                );
    }


    private static @NonNull Timeline compile(
            @NonNull BedrockTimeline    timeline,
            @NonNull FloatArrayList     constants,
            float                       duration,
            final Channel               channel
    ) {
        var tmp                 = new float[16];
        var keyframes           = timeline.keyframes();

        if (keyframes.size()    == 1) {

            throw new UnsupportedOperationException();
        }

        var starts              = new FloatArrayList();
        var ends                = new FloatArrayList();
        var addr0               = new IntArrayList();
        var addr1               = new IntArrayList();
        var interpolations      = new ByteArrayList();

        var array               = new ArrayList<>(keyframes.entrySet());

        var n                   = array.size();
        var t                   = n - 1;

        var factor              = channel.getFactor();

        for (int i = 0; i < t; i++) {
            var e0              = array.get(i);
            var e1              = array.get(i + 1);

            var t0              = e0.getKey();
            var t1              = e1.getKey();

            starts              .add(t0.floatValue());
            ends                .add(t1.floatValue());

            var k0              = e0.getValue();
            var k1              = e1.getValue();

            var smooth          =   (BedrockKeyframe.CATMULLROM.equals(k0.interpolation())
                                ||   BedrockKeyframe.CATMULLROM.equals(k1.interpolation()))
                                &&  (i > 0 && i < t - 1);

            var addr             = constants.size();

            if (smooth) {

                var i0          = Mth.clamp(i - 1, 0, n - 1);
                var i3          = Mth.clamp(i + 2, 0, n - 1);

                var kp          = array.get(i0).getValue();
                var kn          = array.get(i3).getValue();

                get(kp.getPost  (), factor, tmp, 0);
                get(k0.getPost  (), factor, tmp, 4);
                get(k1.getPre   (), factor, tmp, 8);
                get(kn.getPre   (), factor, tmp, 12);

                addr0           .add(addr);
                addr1           .add(addr + 8);

                interpolations  .add(Timeline.INTERPOLATION_CATMULLROM);

                constants       .addElements(addr, tmp, 0, 16);

            } else {

                get(k0.getPost  (), factor, tmp, 0);
                addr0           .add(addr);

                if (k0.getPost().equals(k1.getPre())) {
                    addr1       .add(addr);
                    constants   .addElements(addr, tmp, 0, 4);

                    interpolations.add(Timeline.INTERPOLATION_CONSTANT);
                } else {
                    get(k1.getPre(), factor, tmp, 4);
                    addr1        .add(addr + 4);
                    constants    .addElements(addr, tmp, 0, 8);

                    interpolations.add(Timeline.INTERPOLATION_LINER);
                }

            }
        }


        var last                = starts.size() - 1;
        var end                 = ends.getFloat(last);

        if (end < duration) {
            starts.add(end);
            ends.add(duration);
            addr0.add(addr0.getInt(last));
            addr1.add(addr1.getInt(last));
            interpolations.add(Timeline.INTERPOLATION_CONSTANT);
        }


        return                  new Timeline(
                                    starts          .toFloatArray(),
                                    ends            .toFloatArray(),
                                    addr0           .toIntArray(),
                                    addr1           .toIntArray(),
                                    interpolations  .toByteArray(),
                                    starts          .size()
                                );
    }

    private static void get(
            @NonNull Vector3fc  vector,
            final float         factor,
            float[]             array,
            int                 offset
    ) {
        array[offset]       = vector.x() * factor;
        array[offset + 1]   = vector.y() * factor;
        array[offset + 2]   = vector.z() * factor;
    }

    @Getter
    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    private enum Channel {
        POSITION(0.0625f),
        ROTATION(Mth.DEG_TO_RAD),
        SCALE(1.0f);

        final float factor;
    }
}
