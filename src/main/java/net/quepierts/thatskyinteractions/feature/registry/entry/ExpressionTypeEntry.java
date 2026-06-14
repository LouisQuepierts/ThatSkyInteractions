package net.quepierts.thatskyinteractions.feature.registry.entry;

import dev.anvilcraft.lib.v2.registrum.AbstractRegistrum;
import dev.anvilcraft.lib.v2.registrum.util.entry.RegistryEntry;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.quepierts.thatskyinteractions.feature.expression.Expression;
import net.quepierts.thatskyinteractions.feature.expression.ExpressionType;

public final class ExpressionTypeEntry<E extends Expression>
        extends RegistryEntry<ExpressionType<?>, ExpressionType<E>> {
    public ExpressionTypeEntry(
            final AbstractRegistrum<?> owner,
            final DeferredHolder<ExpressionType<?>, ExpressionType<E>> key
    ) {
        super(owner, key);
    }
}
