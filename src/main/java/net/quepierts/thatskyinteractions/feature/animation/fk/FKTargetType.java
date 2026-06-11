package net.quepierts.thatskyinteractions.feature.animation.fk;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.quepierts.thatskyinteractions.core.animation.model.PlayerBone;
import org.joml.Vector3f;
import org.joml.Vector3fc;
import org.jspecify.annotations.NonNull;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public enum FKTargetType {
    LEFT_ARM(PlayerBone.LEFT_ARM),
    RIGHT_ARM(PlayerBone.RIGHT_ARM),
    LEFT_LEG(PlayerBone.LEFT_LEG),
    RIGHT_LEG(PlayerBone.RIGHT_LEG);

    private final PlayerBone bone;

    public static @NonNull FKTargetType of(
            final @NonNull PlayerBone bone
    ) {
        return switch (bone) {
            case LEFT_ARM -> LEFT_ARM;
            case RIGHT_ARM -> RIGHT_ARM;
            case LEFT_LEG -> LEFT_LEG;
            case RIGHT_LEG -> RIGHT_LEG;
            default -> throw new IllegalArgumentException("Invalid bone: " + bone);
        };
    }
}
