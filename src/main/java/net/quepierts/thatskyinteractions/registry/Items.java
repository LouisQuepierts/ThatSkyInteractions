package net.quepierts.thatskyinteractions.registry;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.quepierts.thatskyinteractions.ThatSkyInteractions;
import net.quepierts.thatskyinteractions.block.CandleType;
import net.quepierts.thatskyinteractions.item.*;

@SuppressWarnings("unused")
public class Items {
    public static final DeferredRegister<Item> REGISTER = DeferredRegister.create(
            BuiltInRegistries.ITEM, ThatSkyInteractions.MODID
    );

    public static final DeferredHolder<Item, WingOfLightItem> WING_OF_LIGHT = REGISTER.register(
            "wing_of_light", WingOfLightItem::new
    );

    public static final DeferredHolder<Item, CloudItem> SIMPLE_CLOUD = REGISTER.register(
            "simple_cloud", () -> new CloudItem(Blocks.CLOUD.get())
    );

    public static final DeferredHolder<Item, CloudItem> COLORED_CLOUD = REGISTER.register(
            "colored_cloud", () -> new CloudItem(Blocks.COLORED_CLOUD.get())
    );

    public static final DeferredHolder<Item, CloudExpandItem> CLOUD_EXPAND = REGISTER.register(
            "cloud_expand", CloudExpandItem::new
    );

    public static final DeferredHolder<Item, CloudReduceItem> CLOUD_REDUCE = REGISTER.register(
            "cloud_reduce", CloudReduceItem::new
    );

    public static final DeferredHolder<Item, CloudEditorItem> CLOUD_EDITOR = REGISTER.register(
            "cloud_editor", CloudEditorItem::new
    );

    public static final DeferredHolder<Item, MuralItem> MURAL = REGISTER.register(
            "mural", MuralItem::new
    );

    @SuppressWarnings("unchecked")
    public static final DeferredHolder<Item, CandleClusterItem>[] CANDLES = new DeferredHolder[CandleType.values().length];

    static {
        CandleType[] values = CandleType.values();
        for (int i = 0; i < values.length; i++) {
            CandleType type = values[i];
            CANDLES[i] = REGISTER.register(
                    "candle_cluster_" + type.name().toLowerCase(),
                    () -> new CandleClusterItem(type)
            );
        }
    }
}
