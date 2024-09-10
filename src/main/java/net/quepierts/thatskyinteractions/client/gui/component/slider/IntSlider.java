package net.quepierts.thatskyinteractions.client.gui.component.slider;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.quepierts.simpleanimator.api.animation.keyframe.Interpolation;
import net.quepierts.thatskyinteractions.client.gui.animate.ScreenAnimator;
import org.apache.commons.lang3.math.NumberUtils;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.glfw.GLFW;

@OnlyIn(Dist.CLIENT)
public class IntSlider extends Slider {
    private final int min;
    private final int max;

    private String buffer = "";
    public IntSlider(ScreenAnimator animator, int x, int y, int width, Component message, double value, int min, int max) {
        super(animator, x, y, width, message, value);
        this.min = min;
        this.max = max;
        this.updateMessage();
    }

    @Override
    public void renderWidget(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.renderWidget(guiGraphics, mouseX, mouseY, partialTick);
    }

    public int getIntValue() {
        return (int) Interpolation.linerInterpolation(this.min, this.max, (float) this.value);
    }

    public int getDisplayIntValue() {
        return (int) Interpolation.linerInterpolation(this.min, this.max, (float) this.display);
    }

    public void setIntValue(int value) {
        this.setValue((double) (value - this.min) / (this.max - this.min));
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (!this.buffer.isEmpty() && keyCode == GLFW.GLFW_KEY_BACKSPACE) {
            this.buffer = this.buffer.substring(0, this.buffer.length() - 1);

            if (!this.buffer.isEmpty()) {
                this.setIntValue(NumberUtils.toInt(this.buffer));
            } else {
                this.setMessage(Component.literal(buffer));
            }
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean charTyped(char codePoint, int modifiers) {
        int intValue = this.getIntValue();
        switch (codePoint) {
            case '+': {
                if (intValue < 0) {
                    this.setIntValue(-intValue);
                }
                return true;
            }
            case '-': {
                if (intValue > 0) {
                    this.setIntValue(-intValue);
                }
                return true;
            }
        }
        if (codePoint > 47 && codePoint < 58) {
            this.buffer = this.buffer + codePoint;
            this.setIntValue(NumberUtils.toInt(this.buffer));
            return true;
        }
        return false;
    }

    @Override
    public void setFocused(boolean focused) {
        super.setFocused(focused);

        if (!focused) {
            this.updateMessage();
        }
    }

    @Override
    protected void updateMessage() {
        this.buffer = String.valueOf(this.getIntValue());
        this.setMessage(Component.literal(buffer));
    }
}
