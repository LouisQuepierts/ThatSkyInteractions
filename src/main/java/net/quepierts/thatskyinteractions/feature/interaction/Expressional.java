package net.quepierts.thatskyinteractions.feature.interaction;

import net.minecraft.resources.Identifier;
import net.quepierts.thatskyinteractions.feature.expression.event.RegisterExpressionEvent;
import org.jspecify.annotations.NonNull;

// todo: delegate to PlayerExpressionSystem
public interface Expressional {

    void onRegisterExpression(
            final @NonNull RegisterExpressionEvent      event,
            final @NonNull Identifier                   identifier,
            final          int                          level
    );

}
