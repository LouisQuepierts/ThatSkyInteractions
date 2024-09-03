package net.quepierts.thatskyinteractions.client.registry;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.quepierts.thatskyinteractions.client.render.ter.CloudBlockRenderer;
import net.quepierts.thatskyinteractions.client.render.ter.WingOfLightBlockRenderer;
import net.quepierts.thatskyinteractions.registry.BlockEntities;

@OnlyIn(Dist.CLIENT)
public class BlockEntityRenderers {
    public static void onRegisterBER(final EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(BlockEntities.WING_OF_LIGHT.get(), WingOfLightBlockRenderer::new);
        event.registerBlockEntityRenderer(BlockEntities.CLOUD.get(), CloudBlockRenderer::new);
    }
}
