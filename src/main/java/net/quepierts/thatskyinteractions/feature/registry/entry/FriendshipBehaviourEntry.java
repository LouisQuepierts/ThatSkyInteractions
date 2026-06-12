package net.quepierts.thatskyinteractions.feature.registry.entry;

import dev.anvilcraft.lib.v2.registrum.AbstractRegistrum;
import dev.anvilcraft.lib.v2.registrum.util.entry.RegistryEntry;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.quepierts.thatskyinteractions.feature.friendship.behaviour.FriendshipBehaviour;

public final class FriendshipBehaviourEntry<T extends FriendshipBehaviour>
        extends RegistryEntry<FriendshipBehaviour, T> {
    public FriendshipBehaviourEntry(
            final AbstractRegistrum<?>                      owner,
            final DeferredHolder<FriendshipBehaviour, T>    key
    ) {
        super(owner, key);
    }
}
