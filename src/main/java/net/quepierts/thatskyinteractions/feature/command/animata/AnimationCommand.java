package net.quepierts.thatskyinteractions.feature.command.animata;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import lombok.experimental.UtilityClass;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.IdentifierArgument;
import net.minecraft.resources.Identifier;
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
                        .then(Commands.argument("event", IdentifierArgument.id())
                                .executes(AnimationCommand::event)));
    }

    private static int play(CommandContext<CommandSourceStack> context) {
        final var name = IdentifierArgument.getId(context, "name");

        final var source = context.getSource();
        if (!source.isPlayer()) {
            source.sendFailure(
                    source.getDisplayName().copy()
                            .append(" is not a player")
            );
            return 0;
        }

        PlayerAnimationSystem.play(
                source.getPlayer(),
                name
        );

        return 1;
    }

    private static int abort(CommandContext<CommandSourceStack> context) {
        final var source = context.getSource();
        if (!source.isPlayer()) {
            source.sendFailure(
                    source.getDisplayName().copy()
                            .append(" is not a player")
            );
            return 0;
        }

        PlayerAnimationSystem.abort(
                source.getPlayer()
        );

        return 1;
    }

    private static int exit(CommandContext<CommandSourceStack> context) {
        final var source = context.getSource();
        if (!source.isPlayer()) {
            source.sendFailure(
                    source.getDisplayName().copy()
                            .append(" is not a player")
            );
        }

        PlayerAnimationSystem.exit(
                source.getPlayer()
        );

        return 0;
    }

    private static int event(CommandContext<CommandSourceStack> context) {

        final var eventArg  = IdentifierArgument.getId(context, "event");

        final var source    = context.getSource();
        if (!source.isPlayer()) {
            source.sendFailure(
                    source.getDisplayName().copy()
                            .append(" is not a player")
            );
            return 0;
        }

        final var event     = Identifier.fromNamespaceAndPath("e", eventArg.getPath());

        PlayerAnimationSystem.event(
                source.getPlayer(),
                event
        );

        return 1;
    }

}
