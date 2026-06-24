package net.quepierts.thatskyinteractions.feature.command;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import lombok.experimental.UtilityClass;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.quepierts.thatskyinteractions.feature.bond.PlayerBondSystem;

@UtilityClass
public class HandholdingCommand {

    static LiteralArgumentBuilder<CommandSourceStack> command() {
        return Commands.literal("handholding")
                .then(Commands.literal("lead")
                        .then(Commands.argument("carrier", EntityArgument.player()).suggests(TsiSuggestions.OTHERS)
                                .executes(HandholdingCommand::lead)))
                .then(Commands.literal("follow")
                        .then(Commands.argument("carrier", EntityArgument.player()).suggests(TsiSuggestions.OTHERS)
                                .executes(HandholdingCommand::follow)))
                .then(Commands.literal("unhold")
                        .executes(HandholdingCommand::unholdAll)
                        .then(Commands.argument("carrier", EntityArgument.player()).suggests(TsiSuggestions.OTHERS)
                                .executes(HandholdingCommand::unhold)));
    }

    private static int lead(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {

        final var source        = context.getSource();
        final var player        = source.getPlayerOrException();
        final var other         = EntityArgument.getPlayer(context, "carrier");

        if (other.is(player)) {
            source.sendFailure(Component.translatable("command.thatskyinteractions.handholding.lead.self"));
            return 0;
        }

        if (PlayerBondSystem.hold(player, other)) {
            return 1;
        }

        source.sendFailure(Component.translatable("command.thatskyinteractions.handholding.lead.fail"));
        return 0;
    }

    private static int follow(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {

        final var source        = context.getSource();
        final var player        = source.getPlayerOrException();
        final var other         = EntityArgument.getPlayer(context, "carrier");

        if (other.is(player)) {
            source.sendFailure(Component.translatable("command.thatskyinteractions.handholding.follow.self"));
            return 0;
        }

        if (PlayerBondSystem.hold(other, player)) {
            return 1;
        }

        source.sendFailure(Component.translatable("command.thatskyinteractions.handholding.follow.fail"));
        return 0;

    }

    private static int unhold(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {

        final var source        = context.getSource();
        final var player        = source.getPlayerOrException();
        final var other         = EntityArgument.getPlayer(context, "carrier");

        if (other.is(player)) {
            source.sendFailure(Component.translatable("command.thatskyinteractions.handholding.unhold.self"));
            return 0;
        }

        PlayerBondSystem.unhold(player, other);

        return 1;
    }

    private static int unholdAll(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {

        final var source        = context.getSource();
        final var player        = source.getPlayerOrException();

        PlayerBondSystem.unholdAll(player);

        return 1;
    }
}
