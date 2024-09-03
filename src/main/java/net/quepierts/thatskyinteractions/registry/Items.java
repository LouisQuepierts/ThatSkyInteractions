package net.quepierts.thatskyinteractions.registry;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.quepierts.thatskyinteractions.ThatSkyInteractions;
import net.quepierts.thatskyinteractions.item.CloudExpandItem;
import net.quepierts.thatskyinteractions.item.CloudItem;
import net.quepierts.thatskyinteractions.item.CloudReduceItem;
import net.quepierts.thatskyinteractions.item.WingOfLightItem;

public class Items {
    public static final DeferredRegister<Item> REGISTER = DeferredRegister.create(
            BuiltInRegistries.ITEM, ThatSkyInteractions.MODID
    );

    public static final DeferredHolder<Item, WingOfLightItem> WING_OF_LIGHT = REGISTER.register(
            "wing_of_light", WingOfLightItem::new
    );

    public static final DeferredHolder<Item, CloudItem> CLOUD = REGISTER.register(
            "cloud", CloudItem::new
    );

    public static final DeferredHolder<Item, CloudExpandItem> CLOUD_EXPAND = REGISTER.register(
            "cloud_expand", CloudExpandItem::new
    );

    public static final DeferredHolder<Item, CloudReduceItem> CLOUD_REDUCE = REGISTER.register(
            "cloud_reduce", CloudReduceItem::new
    );
}
