package net.quepierts.thatskyinteractions.item;

import net.minecraft.world.item.BlockItem;
import net.quepierts.thatskyinteractions.block.CloudBlock;
import net.quepierts.thatskyinteractions.registry.Blocks;

public class CloudItem extends BlockItem {
    public CloudItem(CloudBlock cloudBlock) {
        super(cloudBlock, new Properties());
    }
}
