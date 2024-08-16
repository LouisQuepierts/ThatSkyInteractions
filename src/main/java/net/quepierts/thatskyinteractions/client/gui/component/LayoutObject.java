package net.quepierts.thatskyinteractions.client.gui.component;

import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.layouts.LayoutElement;
import net.minecraft.client.renderer.Rect2i;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import java.util.function.Consumer;

@OnlyIn(Dist.CLIENT)
public class LayoutObject extends Rect2i implements LayoutElement {
    public LayoutObject(int xPos, int yPos, int width, int height) {
        super(xPos, yPos, width, height);
    }

    @Override
    public void visitWidgets(Consumer<AbstractWidget> consumer) {
    }

    public boolean shouldRender(int width, int height) {
        int thisRight = this.getX() + this.getWidth();
        int thisBottom = this.getY() + this.getHeight();

        return this.getX() < width && thisRight > 0 &&
                this.getY() < height && thisBottom > 0;
    }
}
