package net.quepierts.thatskyinteractions.feature.command.veynir;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.IdentifierArgument;
import net.minecraft.network.chat.Component;
import net.quepierts.thatskyinteractions.feature.animation.bedrock.BedrockAnimationCompiler;
import net.quepierts.thatskyinteractions.feature.animation.bedrock.BedrockAnimationManager;
import net.quepierts.thatskyinteractions.feature.animation.binary.SourceParser;
import net.quepierts.veynir.backend.source.TimelineSource;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Slf4j
@UtilityClass
public class CompileCommand {

    static final SuggestionProvider<CommandSourceStack> BR_ANIMATIONS
            = (_, builder)
            -> SharedSuggestionProvider.suggestResource(
            BedrockAnimationManager.getInstance().identifiers(), builder
    );

    static LiteralArgumentBuilder<CommandSourceStack> command() {

        return Commands.literal("compile")
                .then(Commands.literal("bedrock")
                        .then(Commands.argument("animation", IdentifierArgument.id()).suggests(BR_ANIMATIONS)
                                .then(Commands.argument("args", StringArgumentType.greedyString())
                                        .executes(CompileCommand::compileBedrockAnimation))
                                .executes(CompileCommand::compileBedrockAnimation)));

    }

    private static int compileBedrockAnimation(CommandContext<CommandSourceStack> context) {

        final var animationId   = IdentifierArgument.getId(context, "animation");
        final var animation     = BedrockAnimationManager.getInstance().getAnimation(animationId);

        if (animation == null) {
            // send literal
            context.getSource().sendFailure(
                    Component.literal("Animation " + animationId + " not found")
            );
            return 0;
        }

        final var source        = BedrockAnimationCompiler.compile(animation);

        // use java file system to write binary
        final var filename      = animationId.toDebugFileName() + ".anim.bin";
        final var file          = new File("target", filename);
        file.getParentFile().mkdirs();

        try (final var fout = Files.newOutputStream(file.toPath());
             final var out = new ZipOutputStream(fout)
        ) {
            out.putNextEntry(new ZipEntry("content"));
            final var buffer = Unpooled.buffer();
            SourceParser.SOURCE.encode(buffer, (TimelineSource) source);
            out.write(buffer.array());
            out.finish();

            buffer.release();

            context.getSource().sendSuccess(
                    () -> Component.literal("Animation " + animationId + " compiled to " + filename),
                    false
            );
        } catch (IOException e) {

            context.getSource().sendFailure(
                    Component.literal("Failed to compile animation " + animationId)
            );

            log.error("Failed to compile animation {}", animationId, e);
        }

        return 1;
    }

}
