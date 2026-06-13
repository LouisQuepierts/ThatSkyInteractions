package net.quepierts.thatskyinteractions.feature.registry;

import lombok.experimental.UtilityClass;
import net.quepierts.thatskyinteractions.ThatSkyInteractions;
import net.quepierts.thatskyinteractions.feature.interaction.AnimationInteraction;
import net.quepierts.thatskyinteractions.feature.interaction.HoldingHandsInteraction;
import net.quepierts.thatskyinteractions.feature.interaction.Interaction;
import net.quepierts.thatskyinteractions.feature.interaction.UnlockInteraction;
import net.quepierts.thatskyinteractions.feature.registry.builer.InteractionTypeBuilder;
import net.quepierts.thatskyinteractions.feature.registry.entry.InteractionTypeEntry;

@UtilityClass
public class InteractionTypes {

    public static final InteractionTypeEntry<AnimationInteraction> ANIMATION
            = InteractionTypes.<AnimationInteraction>type("animation")
            .codec(AnimationInteraction.MAP_CODEC)
            .streamCodec(AnimationInteraction.STREAM_CODEC)
            .register();

    public static final InteractionTypeEntry<UnlockInteraction> UNLOCK
            = InteractionTypes.<UnlockInteraction>type("unlock")
            .codec(UnlockInteraction.MAP_CODEC)
            .streamCodec(UnlockInteraction.STREAM_CODEC)
            .register();

    public static final InteractionTypeEntry<HoldingHandsInteraction> HANDHOLD
            = InteractionTypes.<HoldingHandsInteraction>type("holding_hands")
            .codec(HoldingHandsInteraction.MAP_CODEC)
            .streamCodec(HoldingHandsInteraction.STREAM_CODEC)
            .register();

    public static void register() { }

    private static <E extends Interaction> InteractionTypeBuilder<E> type(String name) {
        return ThatSkyInteractions.REGISTRUM.entry(
                name,
                callback -> new InteractionTypeBuilder<>(
                        ThatSkyInteractions.REGISTRUM,
                        ThatSkyInteractions.REGISTRUM,
                        name,
                        callback
                )
        );
    }
}
