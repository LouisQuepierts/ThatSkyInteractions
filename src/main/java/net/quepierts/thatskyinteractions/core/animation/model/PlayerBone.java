package net.quepierts.thatskyinteractions.core.animation.model;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Map;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public enum PlayerBone {

    ROOT(1, "root"),
    BODY(2, "body"),
    HEAD(4, "head"),
    LEFT_ARM(8, "left_arm"),
    RIGHT_ARM(16, "right_arm"),
    LEFT_LEG(32, "left_leg"),
    RIGHT_LEG(64, "right_leg"),
    LEFT_HAND(128, "left_hand"),
    RIGHT_HAND(256, "right_hand");

    private static final Map<String, PlayerBone> MAPPING;
    private static final PlayerBone[]            VALUES;

    @Getter
    private final int bit;

    @Getter
    private final String name;

    public static PlayerBone of(String name) {
        return MAPPING.get(name);
    }

    public static int size() {
        return VALUES.length;
    }

    public boolean in(int mask) {
        return (mask & bit) != 0;
    }

    static {
        MAPPING = Map.of(
                "root", ROOT,
                "body", BODY,
                "head", HEAD,
                "left_arm", LEFT_ARM,
                "right_arm", RIGHT_ARM,
                "left_leg", LEFT_LEG,
                "right_leg", RIGHT_LEG
        );
        VALUES = values();
    }
}
