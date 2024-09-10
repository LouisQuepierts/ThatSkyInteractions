package net.quepierts.thatskyinteractions.item;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.quepierts.thatskyinteractions.block.CloudBlock;
import net.quepierts.thatskyinteractions.block.entity.AbstractCloudBlockEntity;
import org.jetbrains.annotations.NotNull;

public class CloudItem extends BlockItem implements ICloudHighlight {
    public CloudItem(CloudBlock cloudBlock) {
        super(cloudBlock, new Properties());
    }

    @Override
    public int color(@NotNull ItemStack itemStack, @NotNull AbstractCloudBlockEntity cloud) {
        return 0xffffffff;
    }
}
