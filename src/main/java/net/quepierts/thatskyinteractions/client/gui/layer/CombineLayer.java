package net.quepierts.thatskyinteractions.client.gui.layer;

import com.google.common.collect.ImmutableList;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.LayeredDraw;
import net.minecraft.resources.ResourceLocation;
import net.quepierts.thatskyinteractions.ThatSkyInteractions;
import org.jetbrains.annotations.NotNull;

public class CombineLayer implements LayeredDraw.Layer {
    public static final ResourceLocation UI = ThatSkyInteractions.getLocation("ui");

    private final ImmutableList<LayeredDraw.Layer> layers;

    public CombineLayer(LayeredDraw.Layer... layers) {
        this.layers = ImmutableList.copyOf(layers);
    }

    @Override
    public void render(@NotNull GuiGraphics guiGraphics, @NotNull DeltaTracker deltaTracker) {
        for (LayeredDraw.Layer layer : this.layers) {
            layer.render(guiGraphics, deltaTracker);
        }
    }
}
