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
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.IdentifierArgument;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.Avatar;
import net.minecraft.world.entity.Entity;
import net.quepierts.thatskyinteractions.feature.animation.PlayerAnimationManager;
import net.quepierts.thatskyinteractions.feature.animation.PlayerAnimationSystem;
import net.quepierts.thatskyinteractions.feature.registry.TsiRegistries;

import java.util.Collection;
import java.util.Collections;

@UtilityClass
public final class AnimationCommand {

    static final SuggestionProvider<CommandSourceStack> ANIMATIONS
            = (_, builder)
            -> SharedSuggestionProvider.suggestResource(
                    PlayerAnimationManager.getInstance().identifiers(), builder
            );

    static final SuggestionProvider<CommandSourceStack> LAYERS = (context, builder) -> {

        final var source = context.getSource();
        source.suggestRegistryElements(
                TsiRegistries.Keys.ANIMATION_LAYER_TYPE,
                SharedSuggestionProvider.ElementSuggestionType.ELEMENTS,
                builder,
                context
        );

        return builder.buildFuture();
    };


    static LiteralArgumentBuilder<CommandSourceStack> command() {

        return Commands.literal("animation")
                // /animation play <name> [targets] [layer]
                .then(Commands.literal("play")
                        .then(Commands.argument("animation", IdentifierArgument.id()).suggests(ANIMATIONS)
                                .executes(ctx -> play(ctx, null, null))
                                .then(Commands.argument("targets", EntityArgument.entities())
                                        .executes(ctx -> play(ctx, EntityArgument.getOptionalEntities(ctx, "targets"), null))
                                        .then(Commands.argument("layer", IdentifierArgument.id()).suggests(LAYERS)
                                                .executes(ctx -> play(ctx, EntityArgument.getOptionalEntities(ctx, "targets"), IdentifierArgument.getId(ctx, "layer"))))
                                )
                        )
                )
                // /animation stop [targets] [layer]
                .then(Commands.literal("stop")
                        .executes(ctx -> stop(ctx, null, null))
                        .then(Commands.argument("targets", EntityArgument.entities())
                                .executes(ctx -> stop(ctx, EntityArgument.getOptionalEntities(ctx, "targets"), null))
                                .then(Commands.argument("layer", IdentifierArgument.id()).suggests(LAYERS)
                                        .executes(ctx -> stop(ctx, EntityArgument.getOptionalEntities(ctx, "targets"), IdentifierArgument.getId(ctx, "layer"))))
                        )
                )
                // /animation exit [targets] [layer]
                .then(Commands.literal("exit")
                        .executes(ctx -> exit(ctx, null, null))
                        .then(Commands.argument("targets", EntityArgument.entities())
                                .executes(ctx -> exit(ctx, EntityArgument.getOptionalEntities(ctx, "targets"), null))
                                .then(Commands.argument("layer", IdentifierArgument.id()).suggests(LAYERS)
                                        .executes(ctx -> exit(ctx, EntityArgument.getOptionalEntities(ctx, "targets"), IdentifierArgument.getId(ctx, "layer"))))
                        )
                )
                // /animation pause [targets] [layer]
                .then(Commands.literal("pause")
                        .executes(ctx -> pause(ctx, null, null))
                        .then(Commands.argument("targets", EntityArgument.entities())
                                .executes(ctx -> pause(ctx, EntityArgument.getOptionalEntities(ctx, "targets"), null))
                                .then(Commands.argument("layer", IdentifierArgument.id()).suggests(LAYERS)
                                        .executes(ctx -> pause(ctx, EntityArgument.getOptionalEntities(ctx, "targets"), IdentifierArgument.getId(ctx, "layer"))))
                        )
                )
                // /animation resume [targets] [layer]
                .then(Commands.literal("resume")
                        .executes(ctx -> resume(ctx, null, null))
                        .then(Commands.argument("targets", EntityArgument.entities())
                                .executes(ctx -> resume(ctx, EntityArgument.getOptionalEntities(ctx, "targets"), null))
                                .then(Commands.argument("layer", IdentifierArgument.id()).suggests(LAYERS)
                                        .executes(ctx -> resume(ctx, EntityArgument.getOptionalEntities(ctx, "targets"), IdentifierArgument.getId(ctx, "layer"))))
                        )
                )
                // /animation event <event> [targets] [layer]
                .then(Commands.literal("event")
                        .then(Commands.argument("event", StringArgumentType.word())
                                .executes(ctx -> event(ctx, StringArgumentType.getString(ctx, "event"), null, null))
                                .then(Commands.argument("targets", EntityArgument.entities())
                                        .executes(ctx -> event(ctx, StringArgumentType.getString(ctx, "event"), EntityArgument.getOptionalEntities(ctx, "targets"), null))
                                        .then(Commands.argument("layer", IdentifierArgument.id()).suggests(LAYERS)
                                                .executes(ctx -> event(ctx, StringArgumentType.getString(ctx, "event"), EntityArgument.getOptionalEntities(ctx, "targets"), IdentifierArgument.getId(ctx, "layer"))))
                                )
                        )
                );
    }

    // ---- Play ----
    private static int play(
            CommandContext<CommandSourceStack>  ctx,
            Collection<? extends Entity>        targets,
            Identifier                          layer
    ) throws CommandSyntaxException {
        
        var name        = IdentifierArgument.getId(ctx, "animation");
        var entities    = resolveTargets(ctx, targets);
        for (var e : entities) {
            if (e instanceof Avatar avatar) {
                PlayerAnimationSystem.play(avatar, name, layer);
            }
        }
        
        return 1;
        
    }

    // ---- Stop ----
    private static int stop(
            CommandContext<CommandSourceStack>  ctx,
            Collection<? extends Entity>        targets,
            Identifier                          layer
    ) throws CommandSyntaxException {
        
        var entities    = resolveTargets(ctx, targets);
        for (var e : entities) {
            if (e instanceof Avatar avatar) {
                PlayerAnimationSystem.abort(avatar, layer);
            }
        }
        
        return 1;
    }

    // ---- Exit ----
    private static int exit(
            CommandContext<CommandSourceStack>  ctx,
            Collection<? extends Entity>        targets,
            Identifier                          layer
    ) throws CommandSyntaxException {
        
        var entities    = resolveTargets(ctx, targets);
        
        for (var e : entities) {
            if (e instanceof Avatar avatar) {
                PlayerAnimationSystem.exit(avatar, layer);
            }
        }
        
        return 1;
    }

    // ---- Pause ----
    private static int pause(
            CommandContext<CommandSourceStack>  ctx,
            Collection<? extends Entity>        targets,
            Identifier                          layer
    ) throws CommandSyntaxException {
        
        var entities    = resolveTargets(ctx, targets);
        for (var e : entities) {
            if (e instanceof Avatar avatar) {
                PlayerAnimationSystem.pause(avatar, layer);
            }
        }
        
        return 1;
    }

    // ---- Resume ----
    private static int resume(
            CommandContext<CommandSourceStack>  ctx,
            Collection<? extends Entity>        targets,
            Identifier                          layer
    ) throws CommandSyntaxException {
        
        var entities = resolveTargets(ctx, targets);
        for (var e : entities) {
            if (e instanceof Avatar avatar) {
                PlayerAnimationSystem.resume(avatar, layer);
            }
        }
        
        return 1;
    }

    // ---- Event ----
    private static int event(
            CommandContext<CommandSourceStack>  ctx,
            String                              eventArg, 
            Collection<? extends Entity>        targets,
            Identifier                          layer
    ) throws CommandSyntaxException {
        
        var entities = resolveTargets(ctx, targets);
        for (var e : entities) {
            if (e instanceof Avatar avatar) {
                PlayerAnimationSystem.event(avatar, eventArg, layer);
            }
        }
        
        return 1;
    }

    // ---- Helper ----
    private static Collection<? extends Entity> resolveTargets(
            CommandContext<CommandSourceStack>  ctx,
            Collection<? extends Entity>        targets
    ) throws CommandSyntaxException {
        return targets == null
                ? Collections.singletonList(ctx.getSource().getEntityOrException())
                : targets;
    }
}
