package net.quepierts.thatskyinteractions.feature.registry;

import lombok.experimental.UtilityClass;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.quepierts.thatskyinteractions.ThatSkyInteractions;

@UtilityClass
public class TsiEntityTags {

    public static final TagKey<EntityType<?>> ANIMATABLE_HUMANOID
            = TagKey.create(Registries.ENTITY_TYPE, ThatSkyInteractions.location("animatable_humanoid"));


}
