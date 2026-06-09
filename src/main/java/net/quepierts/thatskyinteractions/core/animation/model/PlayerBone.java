package net.quepierts.thatskyinteractions.core.animation.model;

import lombok.Getter;
import net.quepierts.thatskyinteractions.core.animation.DefaultMinecraftSkeletonLayout;

import java.util.Map;

public enum PlayerBone {

    ROOT("root", 1),
    BODY("body", 2),
    HEAD("head", 4),
    LEFT_ARM("left_arm", 8),
    RIGHT_ARM("right_arm", 16),
    LEFT_LEG("left_leg", 32),
    RIGHT_LEG("right_leg", 64),
    LEFT_HAND("left_hand", 128),
    RIGHT_HAND("right_hand", 256);

    private static final Map<String, PlayerBone> MAPPING;
    private static final PlayerBone[]            VALUES;

    @Getter private final String    name;
    @Getter private final int       bit;
    @Getter private final int       mapped;

    PlayerBone(String name, int bit) {
        this.bit    = bit;
        this.name   = name;

        this.mapped = DefaultMinecraftSkeletonLayout.HUMANOID.id(name);
    }

    public static PlayerBone of(String name) {
        return MAPPING.get(name);
    }

    public static PlayerBone ordinal(int ordinal) {
        return VALUES[ordinal];
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
