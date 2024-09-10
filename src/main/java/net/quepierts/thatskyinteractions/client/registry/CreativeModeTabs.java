package net.quepierts.thatskyinteractions.client.registry;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.quepierts.thatskyinteractions.ThatSkyInteractions;
import net.quepierts.thatskyinteractions.registry.Items;

@OnlyIn(Dist.CLIENT)
public class CreativeModeTabs {
    public static final DeferredRegister<CreativeModeTab> REGISTER = DeferredRegister.create(
            BuiltInRegistries.CREATIVE_MODE_TAB, ThatSkyInteractions.MODID
    );

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> TSI = REGISTER.register(
            "tsi", () -> CreativeModeTab.builder()
                    .title(Component.translatable("tab.tsi"))
                    .icon(() -> new ItemStack(Items.SIMPLE_CLOUD))
                    .build()
    );
}
