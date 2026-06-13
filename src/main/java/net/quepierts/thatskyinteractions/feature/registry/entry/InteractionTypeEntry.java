package net.quepierts.thatskyinteractions.feature.registry.entry;

import dev.anvilcraft.lib.v2.registrum.AbstractRegistrum;
import dev.anvilcraft.lib.v2.registrum.util.entry.RegistryEntry;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.quepierts.thatskyinteractions.feature.interaction.Interaction;
import net.quepierts.thatskyinteractions.feature.interaction.InteractionType;

public final class InteractionTypeEntry<E extends Interaction>
        extends RegistryEntry<InteractionType<?>, InteractionType<E>> {
    public InteractionTypeEntry(
            final AbstractRegistrum<?>                                      owner,
            final DeferredHolder<InteractionType<?>, InteractionType<E>>    key
    ) {
        super(owner, key);
    }
}
