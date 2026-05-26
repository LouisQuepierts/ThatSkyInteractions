package net.quepierts.thatskyinteractions.infra.animation.backend.sampler;

public enum SamplingMode {
    DEFAULT,
    FREEZE_START,
    FREEZE_END;

    private static final SamplingMode[] VALUES = values();

    public static SamplingMode byId(int id) {
        return VALUES[id];
    }

}
