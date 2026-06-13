package net.quepierts.thatskyinteractions.feature.registry.builer;

import com.mojang.serialization.MapCodec;
import dev.anvilcraft.lib.v2.registrum.AbstractRegistrum;
import dev.anvilcraft.lib.v2.registrum.builders.AbstractBuilder;
import dev.anvilcraft.lib.v2.registrum.builders.BuilderCallback;
import dev.anvilcraft.lib.v2.registrum.util.entry.RegistryEntry;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.quepierts.thatskyinteractions.feature.interaction.Interaction;
import net.quepierts.thatskyinteractions.feature.interaction.InteractionType;
import net.quepierts.thatskyinteractions.feature.registry.TsiRegistries;
import net.quepierts.thatskyinteractions.feature.registry.entry.InteractionTypeEntry;
import org.jspecify.annotations.NonNull;

public final class InteractionTypeBuilder<E extends Interaction>
        extends AbstractBuilder<
            InteractionType<?>,
            InteractionType<E>,
            AbstractRegistrum<?>,
            InteractionTypeBuilder<E>
        > {

    private MapCodec<E>                         codec;
    private StreamCodec<ByteBuf, E>             streamCodec;

    public InteractionTypeBuilder(
            final AbstractRegistrum<?>          owner,
            final AbstractRegistrum<?>          parent,
            final String                        name,
            final BuilderCallback               callback
    ) {
        super(owner, parent, name, callback, TsiRegistries.Keys.INTERACTION_TYPE);
    }

    public @NonNull InteractionTypeBuilder<E> codec(
            final @NonNull MapCodec<E> codec
    ) {
        this.codec = codec;
        return this;
    }

    public @NonNull InteractionTypeBuilder<E> streamCodec(
            final @NonNull StreamCodec<ByteBuf, E> streamCodec
    ) {
        this.streamCodec = streamCodec;
        return this;
    }

    @Override
    public @NonNull InteractionTypeEntry<E> register() {
        return (InteractionTypeEntry<E>) super.register();
    }

    @Override
    protected @NonNull RegistryEntry<InteractionType<?>, InteractionType<E>> createEntryWrapper(
            final @NonNull DeferredHolder<InteractionType<?>, InteractionType<E>> delegate
    ) {
        return new InteractionTypeEntry<>(this.getOwner(), delegate);
    }

    @Override
    protected InteractionType<E> createEntry() {

        if (this.codec == null) {
            throw new IllegalStateException("No codec provided");
        }

        if (this.streamCodec == null) {
            throw new IllegalStateException("No stream codec provided");
        }

        return new InteractionType<>(
                this.codec,
                this.streamCodec
        );

    }
}
