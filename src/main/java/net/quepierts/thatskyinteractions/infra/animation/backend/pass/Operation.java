package net.quepierts.thatskyinteractions.infra.animation.backend.pass;

public record Operation(
        Type type,

        int dst,
        int src0,
        int src1,

        int arg0,
        int arg1,

        float param0,
        float param1
) {

    public static Operation sample(
            int sampler,
            int buffer
    ) {
        return new Operation(
                Type.SAMPLE,
                buffer,
                sampler,
                -1,
                -1,
                -1,
                0.0f,
                0.0f
        );
    }

    public static Operation blend(
            int src0,
            int src1,
            int dst,
            float weight
    ) {
        return new Operation(
                Type.BLEND_P,
                dst,
                src0,
                src1,
                -1,
                -1,
                weight,
                0.0f
        );
    }

    public static Operation blend(
            int src0,
            int src1,
            int dst,
            int weightAddress
    ) {
        return new Operation(
                Type.BLEND_A,
                dst,
                src0,
                src1,
                weightAddress,
                -1,
                0.0f,
                0.0f
        );
    }

    public static Operation assign(
            int src,
            int dst
    ) {
        return new Operation(
                Type.ASSIGN,
                dst,
                src,
                -1,
                -1,
                -1,
                0.0f,
                0.0f
        );
    }

    public enum Type {
        SAMPLE,
        BLEND_P,
        BLEND_A,
        ASSIGN,
        CUSTOM
    }

}
