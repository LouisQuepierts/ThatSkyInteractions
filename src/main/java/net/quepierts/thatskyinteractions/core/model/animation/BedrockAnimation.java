package net.quepierts.thatskyinteractions.core.model.animation;

import java.util.Map;

public record BedrockAnimation(
        boolean                             loop,
        float                               length,
        Map<String, BedrockBoneAnimation>   bones
) {
}
