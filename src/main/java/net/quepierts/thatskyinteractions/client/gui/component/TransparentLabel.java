package net.quepierts.thatskyinteractions.client.gui.component;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Renderable;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.quepierts.thatskyinteractions.client.gui.holder.DoubleHolder;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector4i;

@OnlyIn(Dist.CLIENT)
public class TransparentLabel extends LayoutObject implements Renderable {
    private final Vector4i rect = new Vector4i();
    private final DoubleHolder alpha = new DoubleHolder(127);

    public TransparentLabel(int xPos, int yPos, int width, int height) {
        super(xPos, yPos, width, height);
    }

    @Override
    public void render(@NotNull GuiGraphics guiGraphics, int i, int i1, float v) {
        guiGraphics.fill(rect.x, rect.y, rect.x + rect.z, rect.y + rect.w, ((int) alpha.get()) << 24);
    }

    public DoubleHolder getAlpha() {
        return alpha;
    }

    public Vector4i getRect() {
        return rect;
    }
}
