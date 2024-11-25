package net.quepierts.thatskyinteractions.common.item;

import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.quepierts.thatskyinteractions.client.gui.layer.AnimateScreenHolderLayer;
import net.quepierts.thatskyinteractions.client.gui.screen.blockentity.MuralEditScreen;
import net.quepierts.thatskyinteractions.common.block.entity.MuralBlockEntity;
import net.quepierts.thatskyinteractions.common.registry.Blocks;
import org.jetbrains.annotations.NotNull;

public class MuralItem extends BlockItem {
    private final boolean blooming;

    public MuralItem(boolean blooming) {
        super(Blocks.MURAL.get(), new Properties().stacksTo(1));
        this.blooming = blooming;
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

        InteractionResult result = super.useOn(context);


        if (context.getLevel().getBlockEntity(context.getClickedPos().above()) instanceof MuralBlockEntity mural) {
            mural.setBloom(this.blooming);
        }

        return result;
    }

    @OnlyIn(Dist.CLIENT)
    private void openEditorUI(MuralBlockEntity mural) {
        AnimateScreenHolderLayer.INSTANCE.push(new MuralEditScreen(mural));
    }

    @NotNull
    public String getDescriptionId() {
        String descriptionId = super.getDescriptionId();

        if (this.blooming) {
            descriptionId += "_blooming";
        }

        return descriptionId;
    }
}
