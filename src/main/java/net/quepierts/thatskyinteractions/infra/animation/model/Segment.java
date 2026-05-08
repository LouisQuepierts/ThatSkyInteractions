package net.quepierts.thatskyinteractions.infra.animation.model;

import net.quepierts.thatskyinteractions.infra.animation.interpolator.Interpolator;

/**
 * 31:      type
 * 30~0:    addr
 */
public record Segment(
        float           start,
        float           end,
        float           length,
        float           invLength,
        int             fromRef,
        int             toRef,
        Interpolator    interpolator
) {

    public float getProgress(float progress) {
        return (progress - this.start) * this.invLength;
    }

    public static boolean useParameterBuffer(int ref) {
        return (ref & 0x80000000) != 0;
    }

    public static int getReferenceAddress(int ref) {
        return ref & 0x7FFFFFFF;
    }

}
