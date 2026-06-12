package net.quepierts.thatskyinteractions.feature.registry.builer;

import dev.anvilcraft.lib.v2.registrum.AbstractRegistrum;
import dev.anvilcraft.lib.v2.registrum.builders.AbstractBuilder;
import dev.anvilcraft.lib.v2.registrum.builders.BuilderCallback;
import dev.anvilcraft.lib.v2.registrum.util.entry.RegistryEntry;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.quepierts.thatskyinteractions.feature.friendship.behaviour.FriendshipBehaviour;
import net.quepierts.thatskyinteractions.feature.registry.TsiRegistries;
import net.quepierts.thatskyinteractions.feature.registry.entry.FriendshipBehaviourEntry;
import org.jspecify.annotations.NonNull;

import java.util.function.Supplier;

public final class FriendshipBehaviourBuilder<T extends FriendshipBehaviour>
        extends AbstractBuilder<
            FriendshipBehaviour,
            T,
            AbstractRegistrum<?>,
            FriendshipBehaviourBuilder<T>
        > {

    private final Supplier<T> supplier;

    public FriendshipBehaviourBuilder(
            final AbstractRegistrum<?>  owner,
            final AbstractRegistrum<?>  parent,
            final String                name,
            final BuilderCallback       callback,
            final Supplier<T>           supplier
    ) {
        super(owner, parent, name, callback, TsiRegistries.Keys.FRIENDSHIP_BEHAVIOUR);
        this.supplier = supplier;
    }

    @Override
    public @NonNull FriendshipBehaviourEntry<T> register() {
        return (FriendshipBehaviourEntry<T>) super.register();
    }

    @Override
    protected @NonNull RegistryEntry<FriendshipBehaviour, T> createEntryWrapper(
            final @NonNull DeferredHolder<FriendshipBehaviour, T> delegate
    ) {
        return new FriendshipBehaviourEntry<>(this.getOwner(), delegate);
    }

    @Override
    protected T createEntry() {
        return this.supplier.get();
    }
}
