package net.quepierts.thatskyinteractions.registry;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.quepierts.thatskyinteractions.ThatSkyInteractions;

public class Items {
    public static final DeferredRegister<Item> REGISTER = DeferredRegister.create(
            BuiltInRegistries.ITEM, ThatSkyInteractions.MODID
    );

    public static final DeferredHolder<Item, BlockItem> WING_OF_LIGHT = REGISTER.register(
            "wing_of_light", () -> new BlockItem(Blocks.WING_OF_LIGHT.get(), new Item.Properties())
    );
}
