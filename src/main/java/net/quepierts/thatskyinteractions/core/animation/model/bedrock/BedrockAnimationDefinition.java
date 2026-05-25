package net.quepierts.thatskyinteractions.core.animation.model.bedrock;

import java.util.Map;
import java.util.Optional;

public record BedrockAnimationDefinition(
        String                          formatVersion,
        Map<String, BedrockAnimation>   animations,
        Optional<Map<String, String>>   metadata
) {
}
