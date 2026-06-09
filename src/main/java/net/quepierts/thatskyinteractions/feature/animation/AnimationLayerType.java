package net.quepierts.thatskyinteractions.feature.animation;

import lombok.Getter;
import net.quepierts.thatskyinteractions.core.animation.model.PlayerMask;

public final class AnimationLayerType {

    @Getter private final PlayerMask    mask;
    @Getter private final int           priority;
    @Getter private final boolean       exclusive;

    public AnimationLayerType(
            PlayerMask                  mask,
            int                         priority,
            boolean                     exclusive
    ) {
        this.mask                       = mask;
        this.priority                   = priority;
        this.exclusive                  = exclusive;
    }

}
