package net.quepierts.thatskyinteractions.feature.registry.entry;

import dev.anvilcraft.lib.v2.registrum.AbstractRegistrum;
import dev.anvilcraft.lib.v2.registrum.util.entry.RegistryEntry;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.quepierts.thatskyinteractions.feature.animation.AnimationLayerType;

public class AnimationLayerEntry extends RegistryEntry<AnimationLayerType, AnimationLayerType> {
    public AnimationLayerEntry(
            final AbstractRegistrum<?>                                      owner,
            final DeferredHolder<AnimationLayerType, AnimationLayerType>    key
    ) {
        super(owner, key);
    }
}
