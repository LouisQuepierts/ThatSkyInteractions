package net.quepierts.thatskyinteractions.infra.animation.backend.model;

import lombok.experimental.UtilityClass;

public record Timeline(
        float[]             starts,
        float[]             ends,
        int[]               addr0,
        int[]               addr1,
        byte[]              interpolation,
        int                 size
) {

    public static final byte INTERPOLATION_LINER = (byte) 0;
    public static final byte INTERPOLATION_CATMULLROM = (byte) 1;
    public static final byte INTERPOLATION_CONSTANT         = (byte) 2;

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
