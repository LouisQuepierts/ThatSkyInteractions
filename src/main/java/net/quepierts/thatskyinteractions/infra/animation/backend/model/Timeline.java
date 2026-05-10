package net.quepierts.thatskyinteractions.infra.animation.backend.model;

import lombok.experimental.UtilityClass;
import net.quepierts.thatskyinteractions.infra.animation.interpolator.Interpolator4f;

public record Timeline(
        float[]             starts,
        float[]             ends,
        int[]               addr0,
        int[]               addr1,
        Interpolator4f[]    interpolation,
        int                 size
) {

    @UtilityClass
    public static class Address {
        static final int PARAMETER_BIT = 0x80000000;

        public static boolean isParameter(int address) {
            return (address & PARAMETER_BIT) != 0;
        }

        public static int offset(int address) {
            return address & ~PARAMETER_BIT;
        }
    }

}
