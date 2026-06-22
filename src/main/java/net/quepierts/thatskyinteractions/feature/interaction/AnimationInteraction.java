package net.quepierts.thatskyinteractions.feature.interaction;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.quepierts.thatskyinteractions.ThatSkyInteractions;
import net.quepierts.thatskyinteractions.core.animation.model.PlayerAnimationDefinition;
import net.quepierts.thatskyinteractions.core.animation.model.PlayerMask;
import net.quepierts.thatskyinteractions.core.animation.model.SourceDefinition;
import net.quepierts.thatskyinteractions.core.interaction.DefaultInteractionFSM;
import net.quepierts.thatskyinteractions.feature.animation.PlayerAnimationSystem;
import net.quepierts.thatskyinteractions.feature.animation.event.PlayerAnimationControllerEvent;
import net.quepierts.thatskyinteractions.feature.animation.event.RegisterPlayerAnimationEvent;
import net.quepierts.thatskyinteractions.feature.control.PlayerControlSystem;
import net.quepierts.thatskyinteractions.feature.expression.PlayerExpressionSystem;
import net.quepierts.thatskyinteractions.feature.expression.event.RegisterExpressionEvent;
import net.quepierts.thatskyinteractions.feature.interaction.expression.AnimationInteractionExpression;
import net.quepierts.thatskyinteractions.feature.registry.AnimationLayerTypes;
import net.quepierts.thatskyinteractions.feature.registry.InteractionTypes;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.Map;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class AnimationInteraction implements Interaction, Expressional {

    public  static final String     AUTO    = "auto";
    private static final Identifier EMPTY   = ThatSkyInteractions.location("empty");

    public static final MapCodec<AnimationInteraction> MAP_CODEC
            = RecordCodecBuilder.mapCodec(instance -> instance.group(
                    Codec.STRING.fieldOf("requester").forGetter(AnimationInteraction::requester),
                    Codec.STRING.fieldOf("receiver").forGetter(AnimationInteraction::receiver)
            ).apply(instance, AnimationInteraction::new));

    public static final StreamCodec<ByteBuf, AnimationInteraction> STREAM_CODEC
            = StreamCodec.composite(
                    ByteBufCodecs.STRING_UTF8,
                    AnimationInteraction::requester,
                    ByteBufCodecs.STRING_UTF8,
                    AnimationInteraction::receiver,
                    AnimationInteraction::new
            );

    private final String        requester;
    private final String        receiver;

    @Getter
    private Identifier          requesterExpression = DEFAULT_EXPRESSION_REQUESTER;
    @Getter
    private Identifier          receiverExpression  = DEFAULT_EXPRESSION_RECEIVER;

    private Identifier          requesterAnimation  = EMPTY;
    private Identifier          receiverAnimation   = EMPTY;

    @Override
    public @NonNull InteractionType<? extends Interaction> getType() {
        return InteractionTypes.ANIMATION.get();
    }

    @Override
    public void onInvite(
            final @NonNull ServerPlayer                 requester,
            final @NonNull ServerPlayer                 receiver
    ) { }

    @Override
    public void onAccepted(
            final @NonNull ServerPlayer                 requester,
            final @NonNull ServerPlayer                 receiver
    ) {

        PlayerExpressionSystem.signal(
                requester,
                DefaultInteractionFSM.REQUESTER_ACCEPT
        );

    }

    @Override
    public void onCancel(
            final @NonNull  ServerPlayer                requester,
            final @Nullable ServerPlayer                receiver
    ) { }

    @Override // todo: delegate by expression in future
    public void onRegisterPlayerAnimation(
            final @NonNull RegisterPlayerAnimationEvent event,
            final @NonNull Identifier                   identifier,
            final          int                          level
    ) {

        final var typename  = level != 0 ? identifier.withSuffix("_" + level) : identifier;

        parseRequester(
                event,
                typename,
                this.requesterAnimation,
                AUTO.equalsIgnoreCase(this.requester)
        );

        parseReceiver(
                event,
                typename,
                this.receiverAnimation,
                AUTO.equalsIgnoreCase(this.receiver)
        );

    }

    @Override
    public void onGenerateData(
            final @NonNull Identifier                   identifier,
            final          int                          level
    ) {
        final var subfix = level != 0 ? ("_" + level) : "";

        this.requesterAnimation      = AUTO.equalsIgnoreCase(this.requester) ?
                                        identifier.withSuffix(subfix + ".requester") :
                                        identifier.withSuffix(".receiver");

        this.receiverAnimation      = AUTO.equalsIgnoreCase(this.receiver) ?
                                        identifier.withSuffix(subfix + ".receiver") :
                                        identifier.withSuffix(".receiver");

        final var path  = (identifier.getPath() + subfix);
        this.requesterExpression    = identifier.withPath("interaction/" + path + ".requester");
        this.receiverExpression     = identifier.withPath("interaction/" + path + ".receiver");

    }

    @Override
    public void onRegisterExpression(
            final @NonNull  RegisterExpressionEvent     event,
            final @NonNull  Identifier                  identifier,
            final           int                         level
    ) {
        if (this.requesterExpression != DEFAULT_EXPRESSION_REQUESTER) {
            event.register(this.requesterExpression, new AnimationInteractionExpression(
                    this.requesterAnimation,
                    true,
                    false
            ));
        }

        if (this.receiverExpression != DEFAULT_EXPRESSION_RECEIVER) {
            event.register(this.receiverExpression, new AnimationInteractionExpression(
                    this.receiverAnimation,
                    false,
                    true
            ));
        }
    }

    @Override
    public void onExpressionFinished(
            final @NonNull ServerPlayer     player,
            final @NonNull Identifier       expression
    ) {
        final var attachment                = PlayerInteractionSystem.getInteractionAttachment(player);
        final var ongoing                   = attachment.getOngoing();

        final var requester                 = ongoing.isRequester();
        final var match                     = requester
                                            ? this.requesterExpression
                                            : this.receiverExpression;
        
        if (!expression.equals(match)) {
            return;
        }

        ongoing.done();

    }

    @Override
    public boolean shouldFinish(final @NonNull Player player) {
        return false;
    }

    private static void parseRequester(
            RegisterPlayerAnimationEvent                event,
            Identifier                                  typename,
            Identifier                                  identifier,
            boolean                                     generate
    ) {

        if (!generate) {

            final var definition    = event.get(identifier);

            if (definition == null) {
                log.warn("Interaction source {} not found", identifier);
            }

            return;
        }

        final var namespace         = Optional.of("requester");
        final var prefix            = typename.toString();
        final var sources           = Map.of(
                "invite", new SourceDefinition(prefix + ".invite", 0.25f, 0.0f, namespace),
                "waiting", new SourceDefinition(prefix + ".waiting", 0.0f, 0.0f, namespace),
                "cancel", new SourceDefinition(prefix + ".cancel", 0.0f, 0.25f, namespace),
                "main", new SourceDefinition(prefix + ".main", 0.0f, 0.0f, namespace),
                "exit", new SourceDefinition(prefix + ".exit", 0.0f, 0.25f, namespace)
        );

        final var definition        = new PlayerAnimationDefinition(
                PlayerInteractionSystem.ANIMATION_TYPE_REQUESTER,
                "thatskyinteractions:modified",
                sources,
                PlayerMask.empty(),
                false,
                true,
                false,
                PlayerAnimationDefinition.DEFAULT_LAYER
        );

        event.register(identifier, definition);
    }

    private static void parseReceiver(
            RegisterPlayerAnimationEvent                event,
            Identifier                                  typename,
            Identifier                                  identifier,
            boolean                                     generate
    ) {
        if (!generate) {


            final var definition    = event.get(identifier);

            if (definition == null) {
                log.warn("Interaction source {} not found", identifier);
            }

            return;
        }

        final var namespace         = Optional.of("receiver");
        final var prefix            = typename.toString();
        final var sources           = Map.of(
                "accept", new SourceDefinition(prefix + ".accept", 0.25f, 0.0f, namespace),
                "main", new SourceDefinition(prefix + ".main", 0.0f, 0.0f, namespace),
                "exit", new SourceDefinition(prefix + ".exit", 0.0f, 0.25f, namespace)
        );

        final var definition        = new PlayerAnimationDefinition(
                PlayerInteractionSystem.ANIMATION_TYPE_RECEIVER,
                "thatskyinteractions:modified",
                sources,
                PlayerMask.empty(),
                false,
                true,
                false,
                PlayerAnimationDefinition.DEFAULT_LAYER
        );

        event.register(identifier, definition);
    }

    private String requester() {
        return this.requester;
    }

    private String receiver() {
        return this.receiver;
    }
}
