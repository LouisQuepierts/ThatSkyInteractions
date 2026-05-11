package net.quepierts.thatskyinteractions.core.model.animation;

import java.util.Optional;

public record BedrockBoneAnimation(
        Optional<BedrockTimeline>   position,
        Optional<BedrockTimeline>   rotation,
        Optional<BedrockTimeline>   scale
) { }
