package net.quepierts.thatskyinteractions.common.item;

import net.minecraft.world.item.ItemStack;
import net.quepierts.thatskyinteractions.common.block.entity.AbstractCloudBlockEntity;
import org.jetbrains.annotations.NotNull;

public interface ICloudHighlight {
    int color(@NotNull ItemStack itemStack, @NotNull AbstractCloudBlockEntity cloud);
}
