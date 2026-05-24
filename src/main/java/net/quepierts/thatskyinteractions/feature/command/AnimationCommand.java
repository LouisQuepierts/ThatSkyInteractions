package net.quepierts.thatskyinteractions.feature.command;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import lombok.experimental.UtilityClass;
import net.minecraft.client.Minecraft;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.IdentifierArgument;
import net.neoforged.neoforge.network.PacketDistributor;
import net.quepierts.thatskyinteractions.feature.animation.PlayerAnimationManager;
import net.quepierts.thatskyinteractions.feature.entity.AvatarExtension;
import net.quepierts.thatskyinteractions.feature.network.PlayAnimationPacket;

import java.util.UUID;

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

        final var id = source.getPlayer().getId();


        PacketDistributor.sendToPlayersInDimension(
                source.getLevel(),
                new PlayAnimationPacket(name, id)
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
