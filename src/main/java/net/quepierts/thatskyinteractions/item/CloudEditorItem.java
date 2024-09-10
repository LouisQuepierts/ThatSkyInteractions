package net.quepierts.thatskyinteractions.item;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.quepierts.thatskyinteractions.block.CloudBlock;
import net.quepierts.thatskyinteractions.block.entity.AbstractCloudBlockEntity;
import net.quepierts.thatskyinteractions.client.gui.layer.AnimateScreenHolderLayer;
import net.quepierts.thatskyinteractions.client.gui.screen.blockentity.CloudEditScreen;
import net.quepierts.thatskyinteractions.registry.DataComponents;
import org.jetbrains.annotations.NotNull;

public class CloudEditorItem extends Item implements ICloudHighlight {
    public CloudEditorItem() {
        super(new Properties().stacksTo(1));
    }

    @NotNull
    @Override
    public InteractionResult useOn(UseOnContext context) {
        if (context.getHand() == InteractionHand.OFF_HAND) {
            return InteractionResult.FAIL;
        }

        Player player = context.getPlayer();
        if (player != null && player.isCreative()) {
            BlockPos pos = context.getClickedPos();
            Level level = context.getLevel();

            ItemStack item = context.getItemInHand();
            if (level.getBlockState(pos).getBlock() instanceof CloudBlock) {
                item.set(DataComponents.VEC3I.get(), pos);
                item.set(net.minecraft.core.component.DataComponents.ENCHANTMENT_GLINT_OVERRIDE, true);
                return InteractionResult.SUCCESS;
            }
        }
        return super.useOn(context);
    }

    @NotNull
    @Override
    public InteractionResultHolder<ItemStack> use(@NotNull Level level, @NotNull Player player, @NotNull InteractionHand usedHand) {
        ItemStack item = player.getItemInHand(usedHand);
        if (player.isCreative()) {
            if (player.isShiftKeyDown()) {
                item.remove(DataComponents.VEC3I.get());
                item.remove(net.minecraft.core.component.DataComponents.ENCHANTMENT_GLINT_OVERRIDE);
                return InteractionResultHolder.success(item);
            } else if (level.isClientSide) {
                Vec3i vec3i = item.get(DataComponents.VEC3I.get());

                if (vec3i != null) {
                    BlockEntity entity = level.getBlockEntity(new BlockPos(vec3i));
                    if (entity instanceof AbstractCloudBlockEntity cloud) {
                        this.openEditorUI(cloud);
                        return InteractionResultHolder.success(item);
                    }
                }
            }
        }

        return InteractionResultHolder.fail(item);
    }

    @OnlyIn(Dist.CLIENT)
    private void openEditorUI(AbstractCloudBlockEntity cloud) {
        AnimateScreenHolderLayer.INSTANCE.push(new CloudEditScreen(cloud));
    }

    @Override
    public int color(@NotNull ItemStack itemStack, @NotNull AbstractCloudBlockEntity cloud) {
        Vec3i pos = itemStack.get(DataComponents.VEC3I);

        if (pos != null && cloud.getBlockPos().equals(pos)) {
            return 0xff0000ff;
        }

        return 0xffffffff;
    }
}
