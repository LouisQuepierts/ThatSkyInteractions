package net.quepierts.thatskyinteractions.feature.client.gui.layer;

import lombok.NonNull;
import lombok.experimental.UtilityClass;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;

@UtilityClass
public class GameLayerHook {

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
