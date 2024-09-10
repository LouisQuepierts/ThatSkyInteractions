package net.quepierts.thatskyinteractions.item;

import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.quepierts.thatskyinteractions.block.entity.MuralBlockEntity;
import net.quepierts.thatskyinteractions.client.gui.layer.AnimateScreenHolderLayer;
import net.quepierts.thatskyinteractions.client.gui.screen.blockentity.MuralEditScreen;
import net.quepierts.thatskyinteractions.registry.Blocks;
import org.jetbrains.annotations.NotNull;

public class MuralItem extends BlockItem {
    public MuralItem() {
        super(Blocks.MURAL.get(), new Properties().stacksTo(1));
    }

    @NotNull
    @Override
    public InteractionResult useOn(UseOnContext context) {
        BlockEntity entity = context.getLevel().getBlockEntity(context.getClickedPos());
        if (entity instanceof MuralBlockEntity mural) {
            if (context.getLevel().isClientSide()) {
                this.openEditorUI(mural);
            }
            return InteractionResult.SUCCESS;
        }

        return super.useOn(context);
    }

    @OnlyIn(Dist.CLIENT)
    private void openEditorUI(MuralBlockEntity mural) {
        AnimateScreenHolderLayer.INSTANCE.push(new MuralEditScreen(mural));
    }
}
