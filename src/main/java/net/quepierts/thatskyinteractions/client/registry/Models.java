package net.quepierts.thatskyinteractions.client.registry;


import net.minecraft.client.resources.model.ModelResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ModelEvent;
import net.quepierts.thatskyinteractions.ThatSkyInteractions;
import net.quepierts.thatskyinteractions.block.CandleType;
import net.quepierts.thatskyinteractions.client.util.CandleModels;

@OnlyIn(Dist.CLIENT)
@EventBusSubscriber(value = Dist.CLIENT, modid = ThatSkyInteractions.MODID, bus = EventBusSubscriber.Bus.MOD)
public class Models {
    @SubscribeEvent
    public static void onRegisterAdditionalModel(ModelEvent.RegisterAdditional event) {
        for (CandleType value : CandleType.values()) {
            event.register(CandleModels.get(value));
        }
    }
}
