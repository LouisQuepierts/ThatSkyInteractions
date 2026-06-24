package net.quepierts.thatskyinteractions.feature.registry;

import lombok.experimental.UtilityClass;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.registries.NewRegistryEvent;
import net.neoforged.neoforge.registries.RegistryBuilder;
import net.quepierts.thatskyinteractions.ThatSkyInteractions;
import net.quepierts.thatskyinteractions.feature.animation.AnimationLayerType;
import net.quepierts.thatskyinteractions.feature.expression.ExpressionType;
import net.quepierts.thatskyinteractions.feature.friendship.behaviour.FriendshipBehaviour;
import net.quepierts.thatskyinteractions.feature.interaction.InteractionType;

@UtilityClass
public class TsiRegistries {

    public static final Registry<AnimationLayerType> ANIMATION_LAYER_TYPE
            = new RegistryBuilder<>(Keys.ANIMATION_LAYER_TYPE)
            .create();

    public static final Registry<InteractionType<?>> INTERACTION_TYPE
            = new RegistryBuilder<>(Keys.INTERACTION_TYPE)
            .sync(true)
            .create();

    public static final Registry<ExpressionType<?>> EXPRESSION_TYPE
            = new RegistryBuilder<>(Keys.EXPRESSION_TYPE)
            .sync(true)
            .create();

    public static final Registry<FriendshipBehaviour> FRIENDSHIP_BEHAVIOUR
            = new RegistryBuilder<>(Keys.FRIENDSHIP_BEHAVIOUR)
            .sync(true)
            .create();

    public static void register() { }

    @UtilityClass
    public static final class Keys {
        public static final ResourceKey<Registry<AnimationLayerType>> ANIMATION_LAYER_TYPE
                = ResourceKey.createRegistryKey(ThatSkyInteractions.location("animation_layer_type"));

        public static final ResourceKey<Registry<InteractionType<?>>> INTERACTION_TYPE
                = ResourceKey.createRegistryKey(ThatSkyInteractions.location("interaction_type"));

        public static final ResourceKey<Registry<ExpressionType<?>>> EXPRESSION_TYPE
                = ResourceKey.createRegistryKey(ThatSkyInteractions.location("expression_type"));

        public static final ResourceKey<Registry<FriendshipBehaviour>> FRIENDSHIP_BEHAVIOUR
                = ResourceKey.createRegistryKey(ThatSkyInteractions.location("friendship_behaviour"));

    }

    @EventBusSubscriber(modid = ThatSkyInteractions.MODID)
    private static final class Handler {

        @SubscribeEvent
        public static void onNewRegistry(final NewRegistryEvent event) {

            event.register(ANIMATION_LAYER_TYPE);
            event.register(INTERACTION_TYPE);
            event.register(EXPRESSION_TYPE);
            event.register(FRIENDSHIP_BEHAVIOUR);

        }

    }

}
