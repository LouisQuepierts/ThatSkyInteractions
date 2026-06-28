package net.quepierts.thatskyinteractions.feature.client.renderstate;

import lombok.experimental.UtilityClass;
import net.minecraft.util.context.ContextKey;
import net.quepierts.thatskyinteractions.ThatSkyInteractions;
import org.jspecify.annotations.NonNull;

@UtilityClass
public class RenderStateModifiers {

    public static <T> ContextKey<T> create(final @NonNull String name) {
        return new ContextKey<>(ThatSkyInteractions.location(name));
    }

}
