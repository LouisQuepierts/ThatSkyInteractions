package net.quepierts.thatskyinteractions.feature.expression;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.quepierts.thatskyinteractions.ThatSkyInteractions;
import net.quepierts.thatskyinteractions.feature.animation.PlayerAnimationSystem;
import net.quepierts.thatskyinteractions.feature.animation.event.PlayerAnimationControllerEvent;
import net.quepierts.thatskyinteractions.feature.control.PlayerControlSystem;
import net.quepierts.thatskyinteractions.feature.expression.runtime.AnimationExpressionState;
import net.quepierts.thatskyinteractions.feature.expression.runtime.ExpressionState;
import net.quepierts.thatskyinteractions.feature.registry.AnimationLayerTypes;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.function.Function;

@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class AbstractAnimationExpression implements Expression {
    public  static  final   String      AUTO        = "auto";
    public  static  final   Identifier  EMPTY       = ThatSkyInteractions.location("empty");

    protected       final   String      animation;
    protected               Identifier  animationId = EMPTY;

    protected static <T extends AbstractAnimationExpression> MapCodec<T> codec(
            final @NonNull Function<String, T> constructor
    ) {

        return  RecordCodecBuilder.mapCodec(instance -> instance.group(
                    Codec.STRING.fieldOf("animation").forGetter(AbstractAnimationExpression::animation)
                ).apply(instance, constructor));

    }

    protected static <T extends AbstractAnimationExpression> StreamCodec<ByteBuf, T> streamCodec(
            final @NonNull Function<String, T> constructor
    ) {

        return  StreamCodec.composite(
                    ByteBufCodecs.STRING_UTF8,
                    AbstractAnimationExpression::animation,
                    constructor
                );

    }

    @Override
    public void onPerform(final @NonNull ServerPlayer player) {
        PlayerAnimationSystem.play(player, this.animationId);
        PlayerControlSystem.align(player);
    }

    @Override
    public void onInterrupt(@NonNull ServerPlayer player) {
        PlayerAnimationSystem.exit(player, AnimationLayerTypes.DEFAULT.getId());
    }

    @Override
    public boolean isInterruptible(
            final @NonNull Player           player,
            final @Nullable ExpressionState state
    ) {
        return false;
    }

    @Override
    public boolean isFinished(
            final @NonNull Player player,
            final @Nullable ExpressionState state
    ) {
        return (state != null)
                && ((AnimationExpressionState) state).getStatus() == AnimationExpressionState.Status.FINISHED;
    }

    @Override
    public void onGenerateData(@NonNull Identifier identifier, int level) {

        if (AUTO.equalsIgnoreCase(this.animation)) {
            this.animationId    = level != 0 ? identifier.withSuffix("_" + level) : identifier;
        } else {
            this.animationId    = Identifier.tryParse(this.animation);
        }

    }

    @Override
    public void onAnimationFinished(
            final @NonNull  Player                          player,
            final @Nullable ExpressionState                 state,
            final PlayerAnimationControllerEvent.Finished   event
    ) {

        if (state == null) {
            return;
        }

        if (event.getLayer() != AnimationLayerTypes.DEFAULT.get()) {
            return;
        }

        if (event.getAnimation().equals(this.animationId)) {

            ((AnimationExpressionState) state).setStatus(AnimationExpressionState.Status.FINISHED);

        }

    }

    @Override
    public @NonNull ExpressionState createRuntimeData() {
        return new AnimationExpressionState();
    }

    @Override
    public boolean immediate() {
        return false;
    }

    protected String animation() {
        return this.animation;
    }

}
