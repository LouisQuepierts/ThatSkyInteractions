package net.quepierts.thatskyinteractions.client.util;

import net.minecraft.client.resources.model.ModelResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.quepierts.thatskyinteractions.ThatSkyInteractions;
import net.quepierts.thatskyinteractions.block.CandleType;

@OnlyIn(Dist.CLIENT)
public class CandleModels {
    private static final ModelResourceLocation[] normal;
    private static final ModelResourceLocation[] lit;

    public static ModelResourceLocation get(CandleType type) {
        return normal[type.ordinal()];
    }

    static {
        CandleType[] values = CandleType.values();
        normal = new ModelResourceLocation[values.length];
        lit = new ModelResourceLocation[values.length];

        for (int i = 0; i < values.length; i++) {
            normal[i] = ThatSkyInteractions.getStandaloneModel("block/" + values[i].name().toLowerCase());
            lit[i] = ThatSkyInteractions.getStandaloneModel("block/" + values[i].name().toLowerCase() + "_lit");
        }
    }
}
