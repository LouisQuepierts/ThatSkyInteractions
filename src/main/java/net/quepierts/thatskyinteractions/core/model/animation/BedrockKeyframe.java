package net.quepierts.thatskyinteractions.core.model.animation;

import org.joml.Vector3fc;

import java.util.Optional;

public record BedrockKeyframe(
        Vector3fc           post,
        Optional<Vector3fc> pre,
        String              interpolation
) {
    public static final String              LERP         = "lerp";
    public static final String              CATMULLROM   = "catmullrom";

    public static final BedrockKeyframe[]   EMPTY         = new BedrockKeyframe[0];

    public Vector3fc getPost() {
        return this.post;
    }

    public Vector3fc getPre() {
        return this.pre.orElse(this.post);
    }

    public static BedrockKeyframe of(Vector3fc pure) {
        return new BedrockKeyframe(pure, Optional.of(pure), LERP);
    }
}
