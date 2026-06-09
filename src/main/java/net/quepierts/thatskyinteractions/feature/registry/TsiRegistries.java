package net.quepierts.thatskyinteractions.feature.registry;

import lombok.experimental.UtilityClass;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.neoforged.neoforge.registries.RegistryBuilder;
import net.quepierts.thatskyinteractions.ThatSkyInteractions;
import net.quepierts.thatskyinteractions.feature.animation.AnimationLayerType;

@UtilityClass
public class TsiRegistries {

    public static final ResourceKey<Registry<AnimationLayerType>> ANIMATION_LAYER_TYPES
            = ThatSkyInteractions.REGISTRUM.makeRegistry(
                    "animation_layer_types",
                    RegistryBuilder::new
            );

    public static void register() { }

}
