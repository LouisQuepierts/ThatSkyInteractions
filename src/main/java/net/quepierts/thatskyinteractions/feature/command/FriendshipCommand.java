package net.quepierts.thatskyinteractions.feature.command;

import com.mojang.brigadier.arguments.StringArgumentType;
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
import net.minecraft.network.chat.Component;
import net.quepierts.thatskyinteractions.feature.friendship.FriendshipTreeManager;
import net.quepierts.thatskyinteractions.feature.friendship.PlayerFriendshipAttachment;
import net.quepierts.thatskyinteractions.feature.friendship.PlayerFriendshipSystem;

@UtilityClass
public class FriendshipCommand {

    static final SuggestionProvider<CommandSourceStack> NODES
            = (context, builder) -> {
        final var source    = context.getSource();
        if (source == null || !source.isPlayer()) {
            return Suggestions.empty();
        }
        final var player    = source.getPlayerOrException();
        final var other     = EntityArgument.getPlayer(context, "other");

        final var attachment = PlayerFriendshipAttachment.getAttachment(player);
        if (!attachment.has(other)) {
            return Suggestions.empty();
        }

        final var data      = attachment.get(other);
        return SharedSuggestionProvider.suggest(data.getUnlockableNames(), builder);
    };

    static LiteralArgumentBuilder<CommandSourceStack> command() {
        return Commands.literal("friendship")
                .then(Commands.literal("unlock")
                        .then(Commands.argument("other", EntityArgument.player()).suggests(TsiSuggestions.OTHERS)
                                .then(Commands.argument("node", StringArgumentType.word()).suggests(NODES)
                                        .executes(FriendshipCommand::unlock))))
                .then(Commands.literal("reset")
                        .then(Commands.argument("other", EntityArgument.player()).suggests(TsiSuggestions.OTHERS)
                                .executes(FriendshipCommand::reset)))
                .then(Commands.literal("complete")
                        .then(Commands.argument("other", EntityArgument.player()).suggests(TsiSuggestions.OTHERS)
                                .executes(FriendshipCommand::complete)));
    }

    private static int unlock(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {

        final var source        = context.getSource();
        final var player        = source.getPlayerOrException();

        final var other         = EntityArgument.getPlayer(context, "other");

        if (player.is(other)) {
            source.sendFailure(Component.translatable("command.thatskyinteractions.friendship.self"));
            return 0;
        }

        final var node          = StringArgumentType.getString(context, "node");
        final var structure     = FriendshipTreeManager.getInstance().get(PlayerFriendshipAttachment.FRIEND);

        if (structure == null) {
            source.sendFailure(Component.translatable("command.thatskyinteractions.friendship.structure.fail"));
            return 0;
        }

        final var id            = structure.getLookup().find(node);
        if (id == -1) {
            source.sendFailure(Component.translatable("command.thatskyinteractions.friendship.node.fail"));
            return 0;
        }

        if (!PlayerFriendshipSystem.unlock(player, other, id)) {
            source.sendFailure(Component.translatable("command.thatskyinteractions.friendship.unlock.fail"));
            return 0;
        }

        return 1;
    }

    private static int reset(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {

        final var source        = context.getSource();
        final var player        = source.getPlayerOrException();

        final var other         = EntityArgument.getPlayer(context, "other");

        if (player.is(other)) {
            source.sendFailure(Component.translatable("command.thatskyinteractions.friendship.self"));
            return 0;
        }

        if (!PlayerFriendshipSystem.reset(player, other)) {
            source.sendFailure(Component.translatable("command.thatskyinteractions.friendship.reset.fail"));
            return 0;
        }

        return 1;
    }

    private static int complete(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {

        final var source        = context.getSource();
        final var player        = source.getPlayerOrException();

        final var other         = EntityArgument.getPlayer(context, "other");

        if (player.is(other)) {
            source.sendFailure(Component.translatable("command.thatskyinteractions.friendship.self"));
            return 0;
        }

        if (!PlayerFriendshipSystem.complete(player, other)) {
            source.sendFailure(Component.translatable("command.thatskyinteractions.friendship.complete.fail"));
            return 0;
        }

        return 1;
    }

}
