package net.quepierts.thatskyinteractions.feature.command;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import lombok.experimental.UtilityClass;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.IdentifierArgument;
import net.quepierts.thatskyinteractions.feature.animation.PlayerAnimationManager;
import net.quepierts.thatskyinteractions.feature.animation.PlayerAnimationSystem;
import net.quepierts.thatskyinteractions.feature.data.DataSyncSystem;

@UtilityClass
public final class AnimationCommand {

    static final SuggestionProvider<CommandSourceStack> ANIMATIONS
            = (_, builder)
            -> SharedSuggestionProvider.suggestResource(
                    DataSyncSystem.PLAYER_ANIMATION.identifiers(), builder
            );

    static LiteralArgumentBuilder<CommandSourceStack> command() {
        return Commands.literal("animation")
                .then(Commands.literal("play")
                        .then(Commands.argument("name", IdentifierArgument.id()).suggests(ANIMATIONS)
                                .executes(AnimationCommand::play)))
                .then(Commands.literal("stop")
                        .executes(AnimationCommand::stop))
                .then(Commands.literal("terminate")
                        .executes(AnimationCommand::terminate));
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

        return 0;
    }

    private static int stop(CommandContext<CommandSourceStack> context) {
        return 0;
    }

    private static int terminate(CommandContext<CommandSourceStack> context) {
        return 0;
    }

}
