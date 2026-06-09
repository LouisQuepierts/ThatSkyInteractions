package net.quepierts.thatskyinteractions.feature.animation;

import com.google.common.collect.ImmutableMap;
import net.minecraft.resources.Identifier;
import net.quepierts.thatskyinteractions.ThatSkyInteractions;
import net.quepierts.thatskyinteractions.core.animation.model.PlayerBone;
import net.quepierts.thatskyinteractions.core.animation.model.PlayerMask;

import java.util.HashMap;
import java.util.Map;

public record AnimationLayerType(
        Identifier id,
        PlayerMask defaultMask,
        int defaultPriority,
        boolean exclusive
) {

    // todo: use NeoForge registry
    private static final Map<Identifier, AnimationLayerType> REGISTRY = new HashMap<>();

    public static final AnimationLayerType MAIN = register(
            ThatSkyInteractions.location("main"),
            0,
            PlayerMask.ALL
    );

    public static final AnimationLayerType FULL_BODY = register(
            ThatSkyInteractions.location("full_body"),
            50,
            PlayerMask.ALL
    );

    public static final AnimationLayerType UPPER_BODY = register(
            ThatSkyInteractions.location("upper_body"),
            100,
            PlayerMask.of(PlayerBone.HEAD, PlayerBone.LEFT_ARM, PlayerBone.RIGHT_ARM)
    );

    public static final AnimationLayerType LOWER_BODY = register(
            ThatSkyInteractions.location("lower_body"),
            100,
            PlayerMask.of(PlayerBone.BODY, PlayerBone.LEFT_LEG, PlayerBone.RIGHT_LEG)
    );

    public static AnimationLayerType register(Identifier id, int priority, PlayerMask mask) {
        var type = new AnimationLayerType(id, mask, priority, false);
        REGISTRY.put(id, type);
        return type;
    }

    public static AnimationLayerType get(Identifier id) {
        return REGISTRY.getOrDefault(id, MAIN);
    }

    public static Map<Identifier, AnimationLayerType> registry() {
        return ImmutableMap.copyOf(REGISTRY);
    }

    @Override
    public boolean equals(final Object obj) {
        return obj == this
                || obj.getClass() == AnimationLayerType.class && (((AnimationLayerType) obj)).id.equals(this.id);
    }

    @Override
    public int hashCode() {
        return this.id().hashCode();
    }
}
