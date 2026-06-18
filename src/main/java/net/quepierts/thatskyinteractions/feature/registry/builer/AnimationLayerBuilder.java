package net.quepierts.thatskyinteractions.feature.registry.builer;

import dev.anvilcraft.lib.v2.registrum.AbstractRegistrum;
import dev.anvilcraft.lib.v2.registrum.builders.AbstractBuilder;
import dev.anvilcraft.lib.v2.registrum.builders.BuilderCallback;
import dev.anvilcraft.lib.v2.registrum.util.entry.RegistryEntry;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.quepierts.thatskyinteractions.core.animation.model.PlayerBone;
import net.quepierts.thatskyinteractions.core.animation.model.PlayerMask;
import net.quepierts.thatskyinteractions.feature.animation.AnimationLayerType;
import net.quepierts.thatskyinteractions.feature.registry.TsiRegistries;
import net.quepierts.thatskyinteractions.feature.registry.entry.AnimationLayerEntry;
import org.jspecify.annotations.NonNull;

public final class AnimationLayerBuilder extends AbstractBuilder<
        AnimationLayerType,
        AnimationLayerType,
        AbstractRegistrum<?>,
        AnimationLayerBuilder> {

    private PlayerMask mask;
    private int priority        = 0;
    private boolean exclusive   = false;

    public AnimationLayerBuilder(
            final AbstractRegistrum<?>  owner,
            final AbstractRegistrum<?>  parent,
            final String                name,
            final BuilderCallback       callback
    ) {
        super(owner, parent, name, callback, TsiRegistries.Keys.ANIMATION_LAYER_TYPE);
    }

    public @NonNull AnimationLayerBuilder mask(
            final @NonNull PlayerMask mask
    ) {
        this.mask = mask;
        return this;
    }

    public @NonNull AnimationLayerBuilder mask(
            final @NonNull PlayerBone... bones
    ) {
        this.mask = PlayerMask.of(bones);
        return this;
    }

    public @NonNull AnimationLayerBuilder priority(
            final int priority
    ) {
        this.priority = priority;
        return this;
    }

    public @NonNull AnimationLayerBuilder exclusive(
            final boolean exclusive
    ) {
        this.exclusive = exclusive;
        return this;
    }

    @Override
    public @NonNull AnimationLayerEntry register() {
        return (AnimationLayerEntry) super.register();
    }

    @Override
    protected @NonNull RegistryEntry<AnimationLayerType, AnimationLayerType> createEntryWrapper(
            final @NonNull DeferredHolder<AnimationLayerType, AnimationLayerType> delegate
    ) {
        return new AnimationLayerEntry(this.getOwner(), delegate);
    }

    @Override
    protected AnimationLayerType createEntry() {
        return new AnimationLayerType(
                this.getResourceKey().identifier(),
                this.mask == null ? PlayerMask.empty() : this.mask,
                this.priority,
                this.exclusive
        );
    }
}
