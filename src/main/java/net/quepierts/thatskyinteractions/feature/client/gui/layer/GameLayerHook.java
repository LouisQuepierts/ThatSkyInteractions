package net.quepierts.thatskyinteractions.feature.client.gui.layer;

import lombok.NonNull;
import lombok.experimental.UtilityClass;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;
import net.quepierts.thatskyinteractions.ThatSkyInteractions;


@UtilityClass
@EventBusSubscriber(value = Dist.CLIENT, modid = ThatSkyInteractions.MODID)
public class GameLayerHook {


    @SubscribeEvent
    public static void onRegisterGuiLayers(final RegisterGuiLayersEvent event) {
        event.registerAboveAll(FloatingControlLayer.IDENTIFIER, FloatingControlLayer.INSTANCE);
    }


    public static void onRenderLayer(
            @NonNull final GuiGraphicsExtractor graphics,
            @NonNull final DeltaTracker         tracker
    ) {

        final var minecraft = Minecraft.getInstance();
        final var mouseX    = (int)(
                            minecraft.mouseHandler.xpos()
                                    * (double)minecraft.getWindow().getGuiScaledWidth()
                                    / (double)minecraft.getWindow().getScreenWidth()
        );
        final var mouseY    = (int)(
                            minecraft.mouseHandler.ypos()
                                    * (double)minecraft.getWindow().getGuiScaledHeight()
                                    / (double)minecraft.getWindow().getScreenHeight()
        );

        AnimatableScreenLayer.INSTANCE.render(graphics, tracker, mouseX, mouseY);
    }

}
