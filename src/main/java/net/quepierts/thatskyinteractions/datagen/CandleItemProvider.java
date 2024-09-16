package net.quepierts.thatskyinteractions.datagen;

import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.quepierts.thatskyinteractions.ThatSkyInteractions;
import net.quepierts.thatskyinteractions.block.CandleType;

public class CandleItemProvider extends ItemModelProvider {
    public CandleItemProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, ThatSkyInteractions.MODID, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        ResourceLocation normal = ThatSkyInteractions.getLocation("item/candle_normal");
        ResourceLocation framed = ThatSkyInteractions.getLocation("item/candle_framed");
        for (CandleType type : CandleType.values()) {
            String typename = type.name().toLowerCase();
            ResourceLocation item = ThatSkyInteractions.getLocation("candle_cluster_" + typename);
            this.getBuilder(item.toString())
                    .parent(this.getExistingFile(ThatSkyInteractions.getLocation("block/candle/" + typename)));
        }
    }
}
