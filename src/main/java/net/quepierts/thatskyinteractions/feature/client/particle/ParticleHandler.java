package net.quepierts.thatskyinteractions.feature.client.particle;

import lombok.experimental.UtilityClass;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent;
import net.quepierts.thatskyinteractions.ThatSkyInteractions;
import net.quepierts.thatskyinteractions.feature.registry.TsiParticleTypes;

@UtilityClass
@EventBusSubscriber(value = Dist.CLIENT, modid = ThatSkyInteractions.MODID)
public class ParticleHandler {

    @SubscribeEvent
    public static void onRegisterParticleProvider(final RegisterParticleProvidersEvent event) {
        event.registerSpriteSet(TsiParticleTypes.CALL.get(), CallParticleProvider::new);
    }

}
