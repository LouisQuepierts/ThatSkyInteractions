package net.quepierts.thatskyinteractions.item;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.quepierts.thatskyinteractions.block.CloudBlock;
import net.quepierts.thatskyinteractions.block.entity.AbstractCloudBlockEntity;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class CloudItem extends BlockItem implements ICloudHighlight {
    private static final Component description0 = Component.translatable("cloud.description0").withStyle(ChatFormatting.GRAY);
    private static final Component description1 = Component.translatable("cloud.description1").withStyle(ChatFormatting.GRAY);

    public CloudItem(CloudBlock cloudBlock) {
        super(cloudBlock, new Properties());
    }

    @Override
    public int color(@NotNull ItemStack itemStack, @NotNull AbstractCloudBlockEntity cloud) {
        return 0xffffffff;
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @NotNull TooltipContext context, @NotNull List<Component> tooltipComponents, @NotNull TooltipFlag tooltipFlag) {
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
        tooltipComponents.add(description0);
        tooltipComponents.add(description1);
    }
}
