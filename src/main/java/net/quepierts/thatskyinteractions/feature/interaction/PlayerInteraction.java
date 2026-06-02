package net.quepierts.thatskyinteractions.feature.interaction;

import net.minecraft.resources.Identifier;
import net.quepierts.thatskyinteractions.core.interaction.model.InteractionDefinitionEntry;
import org.jspecify.annotations.NonNull;

public record PlayerInteraction(
        Identifier  requester,
        Identifier  receiver,
        int         level
) {

    public static PlayerInteraction parse(
            @NonNull Identifier                 identifier,
            @NonNull InteractionDefinitionEntry definition,
            int level
    ) {
        return new PlayerInteraction(
                PlayerInteractionManager.AUTO.equals(definition.requester()) ?
                    identifier.withSuffix(".requester") :
                    Identifier.parse(definition.requester()),
                PlayerInteractionManager.AUTO.equals(definition.receiver()) ?
                    identifier.withSuffix(".receiver") :
                    Identifier.parse(definition.receiver()),
                level
        );
    }

}
