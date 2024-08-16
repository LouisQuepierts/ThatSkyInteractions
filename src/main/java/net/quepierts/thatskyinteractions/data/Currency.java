package net.quepierts.thatskyinteractions.data;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;

public enum Currency {
    NORMAL_CANDLE(Items.CANDLE, ResourceLocation.withDefaultNamespace("textures/item/candle.png")),
    RED_CANDLE(Items.RED_CANDLE, ResourceLocation.withDefaultNamespace("textures/item/red_candle.png"));
    public final Item item;
    public final ResourceLocation icon;

    Currency(Item item, ResourceLocation icon) {
        this.item = item;
        this.icon = icon;
    }
}
