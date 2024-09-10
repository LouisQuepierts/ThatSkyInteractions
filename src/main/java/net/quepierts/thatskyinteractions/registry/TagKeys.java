package net.quepierts.thatskyinteractions.registry;

import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.quepierts.thatskyinteractions.ThatSkyInteractions;

public class TagKeys {
    public static final TagKey<Item> CLOUD_EDIT = TagKey.create(
            Registries.ITEM,
            ThatSkyInteractions.getLocation("cloud_edits")
    );
}
