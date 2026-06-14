package net.quepierts.thatskyinteractions.feature.registry.builer;

import com.mojang.serialization.MapCodec;
import dev.anvilcraft.lib.v2.registrum.AbstractRegistrum;
import dev.anvilcraft.lib.v2.registrum.builders.AbstractBuilder;
import dev.anvilcraft.lib.v2.registrum.builders.BuilderCallback;
import dev.anvilcraft.lib.v2.registrum.util.entry.RegistryEntry;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.quepierts.thatskyinteractions.feature.expression.Expression;
import net.quepierts.thatskyinteractions.feature.expression.ExpressionType;
import net.quepierts.thatskyinteractions.feature.registry.TsiRegistries;
import net.quepierts.thatskyinteractions.feature.registry.entry.ExpressionTypeEntry;
import org.jspecify.annotations.NonNull;

public final class ExpressionTypeBuilder<E extends Expression>
        extends AbstractBuilder<
                ExpressionType<?>,
                ExpressionType<E>,
                AbstractRegistrum<?>,
                ExpressionTypeBuilder<E>
        > {

    private MapCodec<E> codec;
    private StreamCodec<ByteBuf, E> streamCodec;

    public ExpressionTypeBuilder(
            final AbstractRegistrum<?> owner,
            final AbstractRegistrum<?> parent,
            final String name,
            final BuilderCallback callback
    ) {
        super(owner, parent, name, callback, TsiRegistries.Keys.EXPRESSION_TYPE);
    }

    public @NonNull ExpressionTypeBuilder<E> codec(@NonNull MapCodec<E> codec) {
        this.codec = codec;
        return this;
    }

    public @NonNull ExpressionTypeBuilder<E> streamCodec(@NonNull StreamCodec<ByteBuf, E> streamCodec) {
        this.streamCodec = streamCodec;
        return this;
    }

    @Override
    public @NonNull ExpressionTypeEntry<E> register() {
        return (ExpressionTypeEntry<E>) super.register();
    }

    @Override
    protected @NonNull RegistryEntry<ExpressionType<?>, ExpressionType<E>> createEntryWrapper(
            @NonNull DeferredHolder<ExpressionType<?>, ExpressionType<E>> delegate
    ) {
        return new ExpressionTypeEntry<>(this.getOwner(), delegate);
    }

    @Override
    protected ExpressionType<E> createEntry() {
        if (this.codec == null) {
            throw new IllegalStateException("No codec provided");
        }
        if (this.streamCodec == null) {
            throw new IllegalStateException("No stream codec provided");
        }
        return new ExpressionType<>(this.codec, this.streamCodec);
    }
}
