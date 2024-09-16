package net.quepierts.thatskyinteractions.datagen;

import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.client.model.generators.BlockModelProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.quepierts.thatskyinteractions.ThatSkyInteractions;
import net.quepierts.thatskyinteractions.block.CandleType;

public class LitCandleModelProvider extends BlockModelProvider {
    public LitCandleModelProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, ThatSkyInteractions.MODID, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        ResourceLocation texture = ThatSkyInteractions.getLocation("block/candles_lit");
        for (CandleType type : CandleType.values()) {
            String typename = type.name().toLowerCase();

            ResourceLocation parent = ThatSkyInteractions.getLocation("block/candle/" + typename);
            ResourceLocation candle = ThatSkyInteractions.getLocation("block/candle/" + typename + "_lit");
            this.getBuilder(candle.toString())
                    .parent(this.getExistingFile(parent))
                    .texture("0", texture)
                    .texture("particle", texture);
        }
    }
}
