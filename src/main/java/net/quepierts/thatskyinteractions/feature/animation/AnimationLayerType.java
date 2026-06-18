package net.quepierts.thatskyinteractions.feature.animation;

import lombok.Getter;
import net.minecraft.resources.Identifier;
import net.quepierts.thatskyinteractions.core.animation.model.PlayerMask;
import org.jspecify.annotations.NonNull;

@Getter
public final class AnimationLayerType {

    private final Identifier            identifier;
    private final PlayerMask            mask;
    private final int                   priority;
    private final boolean               exclusive;

    public AnimationLayerType(
            final @NonNull Identifier   identifier,
            final @NonNull PlayerMask   mask,
            final          int          priority,
            final          boolean      exclusive
    ) {
        this.identifier                 = identifier;
        this.mask                       = mask;
        this.priority                   = priority;
        this.exclusive                  = exclusive;
    }

}
