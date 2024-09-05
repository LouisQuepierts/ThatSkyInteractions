package net.quepierts.thatskyinteractions.client.gui.component.label;

import net.minecraft.network.chat.Component;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.quepierts.thatskyinteractions.client.gui.animate.ScreenAnimator;
import net.quepierts.thatskyinteractions.client.gui.component.slider.IntSlider;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3i;

@OnlyIn(Dist.CLIENT)
public class Vector3InputLabel extends TransparentLabel {
    protected final IntSlider xSlider;
    protected final IntSlider ySlider;
    protected final IntSlider zSlider;

    protected final int inX;
    protected final int inY;

    public Vector3InputLabel(@NotNull Component title, ScreenAnimator animator, int xPos, int yPos, int width, int height, int inX, int inY, int minValue, int maxValue) {
        super(title, xPos, yPos, width, height);

        int left = xPos + inX;
        int top = yPos + inY;
        int innerWidth = width - 2 * inX;
        this.xSlider = new IntSlider(animator, left, top, innerWidth, Component.empty(), 0, minValue, maxValue);
        this.ySlider = new IntSlider(animator, left, top + 20, innerWidth, Component.empty(), 0, minValue, maxValue);
        this.zSlider = new IntSlider(animator, left, top + 40, innerWidth, Component.empty(), 0, minValue, maxValue);

        this.inX = inX;
        this.inY = inY;

        addWidgets(this.xSlider, this.ySlider, this.zSlider);
    }

    public Vector3i getVector3() {
        return new Vector3i(
                this.xSlider.getIntValue(),
                this.ySlider.getIntValue(),
                this.zSlider.getIntValue()
        );
    }

    public void setValue(Vector3i vector3i) {
        this.xSlider.setIntValue(vector3i.x);
        this.ySlider.setIntValue(vector3i.y);
        this.zSlider.setIntValue(vector3i.z);
    }

    public void onResize(int xPos, int yPos, int width, int height) {
        this.setRectangle(width, height, xPos, yPos);
        int left = xPos + inX;
        int top = yPos + inY;
        int innerWidth = width - 2 * inX;

        this.xSlider.setRectangle(innerWidth, 16, left, top);
        this.ySlider.setRectangle(innerWidth, 16, left, top + 20);
        this.zSlider.setRectangle(innerWidth, 16, left, top + 40);
    }
}
