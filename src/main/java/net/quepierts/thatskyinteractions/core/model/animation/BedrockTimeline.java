package net.quepierts.thatskyinteractions.core.model.animation;

import it.unimi.dsi.fastutil.floats.Float2ObjectMap;
import org.joml.Vector3f;
import org.joml.Vector3fc;

import java.util.Map;

public record BedrockTimeline(
        Map<Float, BedrockKeyframe> keyframes
) {

    public static BedrockTimeline of(float value) {
        var vector      = new Vector3f(value);
        return          BedrockTimeline.of(vector);
    }

    public static BedrockTimeline of(Vector3fc vector) {
        var keyframe    = BedrockKeyframe.of(vector);
        var entry       = Float2ObjectMap.entry(0.0f, keyframe);

        return          new BedrockTimeline(Float2ObjectMap.ofEntries(entry));
    }

}
