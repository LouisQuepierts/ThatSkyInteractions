package net.quepierts.thatskyinteractions.infra.animation.interpolator;

import net.quepierts.thatskyinteractions.infra.animation.backend.buffer.AnimationBuffer;
import net.quepierts.thatskyinteractions.infra.animation.backend.buffer.WritableBuffer;
import net.quepierts.thatskyinteractions.infra.util.Mth;

@FunctionalInterface
public interface Interpolator4f {

    Interpolator4f LINEAR = (
            progress,
            address0, address1,
            buffer0, buffer1,
            offset, target
    ) -> {
        var r0      = buffer0.getBuffer();
        var r1      = buffer1.getBuffer();

        target.write(
                offset,
                Mth.lerp(progress, r0[address0], r1[address1]),
                Mth.lerp(progress, r0[address0 + 1], r1[address1 + 1]),
                Mth.lerp(progress, r0[address0 + 2], r1[address1 + 2]),
                Mth.lerp(progress, r0[address0 + 3], r1[address1 + 3])
        );
    };

    Interpolator4f CONSTANT = (
            progress,
            address0, address1,
            buffer0, buffer1,
            offset, target
    ) -> {
        var raw     = buffer0.getBuffer();

        target.write(
                offset,
                raw[address0],
                raw[address0 + 1],
                raw[address0 + 2],
                raw[address0 + 3]
        );
    };

    Interpolator4f CATMULL_ROM = (
            progress,
            address0, address1,
            buffer0, buffer1,
            offset, target
    ) -> {
        var r0      = buffer0.getBuffer();
        var r1      = buffer1.getBuffer();

        target.write(
                offset,
                Mth.catmullrom(progress, r0[address0], r0[address0 + 4], r1[address1], r1[address1 + 4]),
                Mth.catmullrom(progress, r0[address0 + 1], r0[address0 + 5], r1[address1 + 1], r1[address1 + 5]),
                Mth.catmullrom(progress, r0[address0 + 2], r0[address0 + 6], r1[address1 + 2], r1[address1 + 6]),
                Mth.catmullrom(progress, r0[address0 + 3], r0[address0 + 7], r1[address1 + 3], r1[address1 + 7])
        );
    };

    void interpolate(
            float           progress,
            int             address0,
            int             address1,
            AnimationBuffer buffer0,
            AnimationBuffer buffer1,
            int             offset,
            WritableBuffer  target
    );

}
