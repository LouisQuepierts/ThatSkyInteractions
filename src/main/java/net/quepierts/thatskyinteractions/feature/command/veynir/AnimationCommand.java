package net.quepierts.thatskyinteractions.feature.command.veynir;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import lombok.experimental.UtilityClass;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.IdentifierArgument;
import net.quepierts.thatskyinteractions.feature.animation.PlayerAnimationManager;
import net.quepierts.thatskyinteractions.feature.animation.PlayerAnimationSystem;

@UtilityClass
public final class AnimationCommand {

    static final SuggestionProvider<CommandSourceStack> ANIMATIONS
            = (_, builder)
            -> SharedSuggestionProvider.suggestResource(
                    PlayerAnimationManager.getInstance().identifiers(), builder
            );

    static LiteralArgumentBuilder<CommandSourceStack> command() {
        return Commands.literal("animation")
                .then(Commands.literal("play")
                        .then(Commands.argument("name", IdentifierArgument.id()).suggests(ANIMATIONS)
                                .executes(AnimationCommand::play)))
                .then(Commands.literal("stop")
                        .executes(AnimationCommand::abort))
                .then(Commands.literal("exit")
                        .executes(AnimationCommand::exit))
                .then(Commands.literal("event")
                        .then(Commands.argument("event", StringArgumentType.word())
                                .executes(AnimationCommand::event)));
    }

    private static int play(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        final var name      = IdentifierArgument.getId(context, "name");

        final var source    = context.getSource();
        final var player    = source.getPlayerOrException();

        PlayerAnimationSystem.play(
                player,
                name
        );

        return 1;
    }

    private static int abort(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        final var source    = context.getSource();
        final var player   = source.getPlayerOrException();

        PlayerAnimationSystem.abort(player);

        return 1;
    }

    private static int exit(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        final var source    = context.getSource();
        final var player   = source.getPlayerOrException();

        PlayerAnimationSystem.exit(player);

        return 0;
    }

    private static int event(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {

        final var eventArg  = StringArgumentType.getString(context, "event");

        final var source    = context.getSource();
        final var player    = source.getPlayerOrException();

        PlayerAnimationSystem.event(
                player,
                eventArg
        );

        return 1;
    }

}
