package net.quepierts.thatskyinteractions.feature.command;

import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.Suggestions;
import lombok.experimental.UtilityClass;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.SharedSuggestionProvider;

@UtilityClass
public class TsiSuggestions {

    public static final SuggestionProvider<CommandSourceStack> OTHERS
            = (context, builder) -> {
                    final var source    = context.getSource();
                    if (source == null || !source.isPlayer()) {
                        return Suggestions.empty();
                    }
                    final var player    = source.getPlayerOrException();
                    final var name      = player.getGameProfile().name();
                    final var names     = source.getOnlinePlayerNames();
                    final var removed   = names.stream()
                            .filter(n -> !n.equals(name))
                            .toList();
                    return SharedSuggestionProvider.suggest(removed, builder);
            };

}
