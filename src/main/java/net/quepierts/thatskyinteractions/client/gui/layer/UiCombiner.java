package net.quepierts.thatskyinteractions.client.gui.layer;

import com.google.common.collect.ImmutableList;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.LayeredDraw;
import net.minecraft.resources.ResourceLocation;
import net.quepierts.thatskyinteractions.ThatSkyInteractions;
import org.jetbrains.annotations.NotNull;

public class UiCombiner {
    public static final ResourceLocation UI = ThatSkyInteractions.getLocation("ui");
    public static final UiCombiner TOP = new UiCombiner(
            PromptMessageLayer.INSTANCE,
            AnimateScreenHolderLayer.INSTANCE,
            CandleInfoLayer.INSTANCE
    );

    private final ImmutableList<LayeredDraw.Layer> layers;

    public UiCombiner(LayeredDraw.Layer... layers) {
        this.layers = ImmutableList.copyOf(layers);
    }

    public void render(@NotNull GuiGraphics guiGraphics, @NotNull DeltaTracker deltaTracker) {
        for (LayeredDraw.Layer layer : this.layers) {
            layer.render(guiGraphics, deltaTracker);
        }
    }
}
