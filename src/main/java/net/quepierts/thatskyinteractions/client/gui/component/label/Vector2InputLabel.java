package net.quepierts.thatskyinteractions.client.gui.component.label;

import net.minecraft.network.chat.Component;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.quepierts.thatskyinteractions.client.gui.animate.ScreenAnimator;
import net.quepierts.thatskyinteractions.client.gui.component.slider.IntSlider;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector2i;

@OnlyIn(Dist.CLIENT)
public class Vector2InputLabel extends TransparentLabel {
    protected final IntSlider xSlider;
    protected final IntSlider ySlider;

    protected final int inX;
    protected final int inY;

    public Vector2InputLabel(@NotNull Component title, ScreenAnimator animator, int xPos, int yPos, int width, int height, int inX, int inY, int minValue, int maxValue) {
        super(title, xPos, yPos, width, height, animator);

        int left = xPos + inX;
        int top = yPos + inY;
        int innerWidth = width - 2 * inX;
        this.xSlider = new IntSlider(animator, left, top, innerWidth, Component.empty(), 0, minValue, maxValue);
        this.ySlider = new IntSlider(animator, left, top + 20, innerWidth, Component.empty(), 0, minValue, maxValue);

        this.inX = inX;
        this.inY = inY;

        addWidgets(this.xSlider, this.ySlider);
    }

    public Vector2i getVector2() {
        return new Vector2i(
                this.xSlider.getIntValue(),
                this.ySlider.getIntValue()
        );
    }

    public void getDisplay(Vector2i out) {
        out.set(
                this.xSlider.getDisplayIntValue(),
                this.ySlider.getDisplayIntValue()
        );
    }

    public void setValue(Vector2i vector2i) {
        this.xSlider.setIntValue(vector2i.x);
        this.ySlider.setIntValue(vector2i.y);
    }

    @Override
    public void onResize(int xPos, int yPos, int width, int height) {
        this.setRectangle(width, height, xPos, yPos);
        int left = xPos + inX;
        int top = yPos + inY;
        int innerWidth = width - 2 * inX;

        this.xSlider.setRectangle(innerWidth, 16, left, top);
        this.ySlider.setRectangle(innerWidth, 16, left, top + 20);
    }
}
