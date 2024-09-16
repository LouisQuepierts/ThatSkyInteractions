package net.quepierts.thatskyinteractions.datagen;

import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import net.quepierts.thatskyinteractions.ThatSkyInteractions;

@EventBusSubscriber(modid = ThatSkyInteractions.MODID, bus = EventBusSubscriber.Bus.MOD)
public class DataGenerationHandler {
    @SubscribeEvent
    public static void onGatherData(final GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
        PackOutput output = generator.getPackOutput();
        ExistingFileHelper existingFileHelper = event.getExistingFileHelper();

        generator.addProvider(
                event.includeClient(),
                new CandleItemProvider(output, existingFileHelper)
        );

        generator.addProvider(
                event.includeClient(),
                new LitCandleModelProvider(output, existingFileHelper)
        );
    }
}
