package net.quepierts.thatskyinteractions.feature.registry;

import lombok.experimental.UtilityClass;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.registries.NewRegistryEvent;
import net.neoforged.neoforge.registries.RegistryBuilder;
import net.quepierts.thatskyinteractions.ThatSkyInteractions;
import net.quepierts.thatskyinteractions.feature.animation.AnimationLayerType;
import net.quepierts.thatskyinteractions.feature.expression.behaviour.ExpressionBehaviour;
import net.quepierts.thatskyinteractions.feature.friendship.behaviour.FriendshipBehaviour;
import net.quepierts.thatskyinteractions.feature.interaction.behaviour.InteractionBehaviour;

@UtilityClass
public class TsiRegistries {

    public static final Registry<AnimationLayerType> ANIMATION_LAYER_TYPE
            = new RegistryBuilder<>(Keys.ANIMATION_LAYER_TYPE)
            .create();

    public static final Registry<InteractionBehaviour> INTERACTION_BEHAVIOUR
            = new RegistryBuilder<>(Keys.INTERACTION_BEHAVIOUR)
            .create();

    public static final Registry<FriendshipBehaviour> FRIENDSHIP_BEHAVIOUR
            = new RegistryBuilder<>(Keys.FRIENDSHIP_BEHAVIOUR)
            .create();

    public static void register() { }

    @UtilityClass
    public static final class Keys {
        public static final ResourceKey<Registry<AnimationLayerType>> ANIMATION_LAYER_TYPE
                = ResourceKey.createRegistryKey(ThatSkyInteractions.location("animation_layer_types"));

        public static final ResourceKey<Registry<InteractionBehaviour>> INTERACTION_BEHAVIOUR
                = ResourceKey.createRegistryKey(ThatSkyInteractions.location("interaction_behaviours"));

        public static final ResourceKey<Registry<FriendshipBehaviour>> FRIENDSHIP_BEHAVIOUR
                = ResourceKey.createRegistryKey(ThatSkyInteractions.location("friendship_behaviours"));

    }

    @EventBusSubscriber(modid = ThatSkyInteractions.MODID)
    private static final class Handler {

        @SubscribeEvent
        public static void onNewRegistry(final NewRegistryEvent event) {

            event.register(ANIMATION_LAYER_TYPE);

            event.register(INTERACTION_BEHAVIOUR);
            event.register(FRIENDSHIP_BEHAVIOUR);

        }

    }

}
