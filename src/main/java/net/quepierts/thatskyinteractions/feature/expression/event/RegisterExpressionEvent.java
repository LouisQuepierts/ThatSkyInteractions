package net.quepierts.thatskyinteractions.feature.expression.event;

import lombok.RequiredArgsConstructor;
import net.minecraft.resources.Identifier;
import net.neoforged.bus.api.Event;
import net.quepierts.thatskyinteractions.feature.expression.Expression;
import org.jspecify.annotations.NonNull;

import java.util.function.BiConsumer;

@RequiredArgsConstructor
public final class RegisterExpressionEvent extends Event {

    private final BiConsumer<Identifier, Expression> registrar;

    public void register(
            final @NonNull Identifier   identifier,
            final @NonNull Expression   expression
    ) {
        this.registrar.accept(identifier, expression);
    }

}
