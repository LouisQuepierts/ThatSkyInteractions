package net.quepierts.thatskyinteractions.feature.command.veynir;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.Suggestions;
import lombok.experimental.UtilityClass;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.IdentifierArgument;
import net.quepierts.thatskyinteractions.feature.command.TsiSuggestions;
import net.quepierts.thatskyinteractions.feature.interaction.PlayerInteractionAttachment;
import net.quepierts.thatskyinteractions.feature.interaction.PlayerInteractionManager;
import net.quepierts.thatskyinteractions.feature.interaction.PlayerInteractionSystem;

import java.util.ArrayList;

@UtilityClass
public final class InteractionCommand {

    static final SuggestionProvider<CommandSourceStack> WAITING
            = (context, builder) -> {
                    final var source    = context.getSource();
                    if (source == null || !source.isPlayer()) {
                        return Suggestions.empty();
                    }

                    final var player        = source.getPlayerOrException();
                    final var level         = source.getLevel();
                    final var data          = PlayerInteractionAttachment.getAttachment(player);

                    final var requests      = data.getReceivedRequests();
                    final var names         = new ArrayList<String>(requests.size());
                    for (final var request : requests) {
                        final var uuid      = request.getOther();
                        final var other     = level.getPlayerInAnyDimension(uuid);

                        if (other != null) {
                            names.add(other.getGameProfile().name());
                        }
                    }

                    return SharedSuggestionProvider.suggest(names, builder);
            };

    static final SuggestionProvider<CommandSourceStack> INTERACTIONS
            = (_, builder)
            -> SharedSuggestionProvider.suggestResource(
                    PlayerInteractionManager.getInstance().identifiers(), builder
            );

    static LiteralArgumentBuilder<CommandSourceStack> command() {
        return Commands.literal("interact").requires(Commands.hasPermission(Commands.LEVEL_GAMEMASTERS))
                .then(Commands.literal("invite")
                        .then(Commands.argument("receiver", EntityArgument.player()).suggests(TsiSuggestions.OTHERS)
                                .then(Commands.argument("interaction", IdentifierArgument.id()).suggests(INTERACTIONS)
                                        .executes(InteractionCommand::invite))))
                .then(Commands.literal("accept")
                        .then(Commands.argument("requester", EntityArgument.player()).suggests(WAITING)
                                .executes(InteractionCommand::accept)))
                .then(Commands.literal("cancel")
                        .executes(InteractionCommand::cancel));
    }

    private static int invite(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        final var source        = context.getSource();
        final var player        = source.getPlayerOrException();

        final var receiver      = EntityArgument.getPlayer(context, "receiver");
        final var interaction   = IdentifierArgument.getId(context, "interaction");

        PlayerInteractionSystem.invite(
                player,
                receiver,
                interaction
        );

        return 1;
    }

    private static int accept(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        final var source        = context.getSource();
        final var player        = source.getPlayerOrException();

        final var requester     = EntityArgument.getPlayer(context, "requester");

        PlayerInteractionSystem.accept(
                requester,
                player,
                true
        );

        return 1;
    }

    private static int cancel(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        final var source        = context.getSource();
        final var player        = source.getPlayerOrException();

        PlayerInteractionSystem.cancel(player);
        return 1;
    }

}
