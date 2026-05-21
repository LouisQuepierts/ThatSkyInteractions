package net.quepierts.thatskyinteractions.core.model.animation.bedrock;

import java.util.Optional;

public record BedrockBoneAnimation(
        Optional<BedrockTimeline>   position,
        Optional<BedrockTimeline>   rotation,
        Optional<BedrockTimeline>   scale
) { }
