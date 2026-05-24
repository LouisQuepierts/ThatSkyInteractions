package net.quepierts.thatskyinteractions.feature.network;

import dev.anvilcraft.lib.v2.network.register.NetworkRegistrar;
import lombok.experimental.UtilityClass;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import net.quepierts.thatskyinteractions.ThatSkyInteractions;

@UtilityClass
@EventBusSubscriber(modid = ThatSkyInteractions.MODID)
public class NetworkModule {

    @SubscribeEvent
    public static void onNetwork(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar("1");
        NetworkRegistrar.register(registrar, ThatSkyInteractions.MODID);
    }

}
