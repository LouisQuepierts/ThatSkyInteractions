package net.quepierts.thatskyinteractions.client.reference;

import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.quepierts.thatskyinteractions.ThatSkyInteractions;

@OnlyIn(Dist.CLIENT)
public class RenderTypes {
    public static final ResourceLocation TEXTURE;

    static {
        TEXTURE = ThatSkyInteractions.getLocation("textures/entity/wing_of_light.png");
    }
}
