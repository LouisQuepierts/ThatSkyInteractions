package net.quepierts.thatskyinteractions.feature.command;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import lombok.experimental.UtilityClass;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.IdentifierArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.quepierts.thatskyinteractions.feature.expression.PlayerPreferenceSystem;
import net.quepierts.thatskyinteractions.feature.expression.call.PlayerVoiceTypeManager;

@UtilityClass
public class PreferenceCommand {

    static final SuggestionProvider<CommandSourceStack> VOICES
            = (_, builder)
            -> SharedSuggestionProvider.suggestResource(
                    PlayerVoiceTypeManager.getInstance().identifiers(), builder
            );

    static LiteralArgumentBuilder<CommandSourceStack> command() {
        return Commands.literal("preference")
                .then(Commands.literal("voice")
                        .then(Commands.literal("set")
                                .then(Commands.argument("voice", IdentifierArgument.id()).suggests(VOICES)
                                        .executes(c -> setVoice(c, c.getSource().getPlayerOrException())))
                                .then(Commands.argument("target", EntityArgument.player())
                                        .then(Commands.argument("voice", IdentifierArgument.id()).suggests(VOICES)
                                                .executes(c -> setVoice(c, EntityArgument.getPlayer(c, "target"))))))
                        .then(Commands.literal("get")
                                .then(Commands.argument("target", EntityArgument.player())
                                        .executes(c -> getVoice(c, EntityArgument.getPlayer(c, "target"))))
                                .then(Commands.argument("target", EntityArgument.player()))));
    }

    private static int getVoice(
            final CommandContext<CommandSourceStack>    context,
            final ServerPlayer                          target
    ) {
        final var source = context.getSource();
        source.sendSuccess(
                () -> Component.translatable("command.thatskyinteractions.preference.voice.get",
                        target.getDisplayName(),
                        PlayerPreferenceSystem.getVoice(target).toString()
                ),
                true
        );
        return 0;
    }

    private static int setVoice(
            final CommandContext<CommandSourceStack>    context,
            final ServerPlayer                          target
    ) throws CommandSyntaxException {

        final var source    = context.getSource();
        final var type      = IdentifierArgument.getId(context, "voice");

        if (!PlayerVoiceTypeManager.getInstance().has(type)) {
            source.sendFailure(Component.translatable("command.thatskyinteractions.preference.voice.invalid", type));
            return 0;
        }

        PlayerPreferenceSystem.setVoice(target, type);

        source.sendSuccess(
                () -> Component.translatable("command.thatskyinteractions.preference.voice.set",
                        target.getDisplayName(),
                        type.toString()
                ),
                true
        );

        return 1;
    }
}
