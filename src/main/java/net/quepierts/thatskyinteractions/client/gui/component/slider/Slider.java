package net.quepierts.thatskyinteractions.client.gui.component.slider;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.InputType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.navigation.CommonInputs;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.quepierts.thatskyinteractions.client.gui.Palette;
import net.quepierts.thatskyinteractions.client.gui.animate.AbstractScreenAnimation;
import net.quepierts.thatskyinteractions.client.gui.animate.AnimateUtils;
import net.quepierts.thatskyinteractions.client.gui.animate.ScreenAnimator;
import net.quepierts.thatskyinteractions.client.util.RenderUtils;
import org.jetbrains.annotations.NotNull;

@OnlyIn(Dist.CLIENT)
public class Slider extends AbstractWidget {
    protected final ScreenAnimator animator;
    protected double display;
    protected double value;

    private final SmoothAnimation animation;
    private boolean canChangeValue;
    public Slider(ScreenAnimator animator, int x, int y, int width, Component message, double value) {
        super(x, y, width, 16, message);
        this.animator = animator;
        this.value = value;
        this.display = value;

        this.animation = new SmoothAnimation();
    }

    @Override
    public void renderWidget(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        RenderSystem.enableBlend();

        float alpha = Palette.getShaderAlpha();

        if (this.isFocused()) {
            RenderUtils.fillRoundRect(guiGraphics, this.getX(), this.getY(), this.getWidth(), 16, 0.5f * 16 / this.getWidth(), Palette.HIGHLIGHT_COLOR ^ 0x40000000);
            RenderUtils.fillRoundRect(guiGraphics, this.getX() + 1, this.getY() + 1, this.getWidth() - 2, 14, 0.5f * 14 / (this.getWidth() - 2), 0x80101010);
        } else {
            RenderUtils.fillRoundRect(guiGraphics, this.getX(), this.getY(), this.getWidth(), 16, 0.5f * 16 / this.getWidth(), 0xa0101010);
        }

        int i = (int) (this.display * (double) (this.width - 16));
        RenderUtils.fillRoundRect(guiGraphics, this.getX() + 2, this.getY() + 2, i + 12, 12, 0.5f * 12 / (i + 12), 0xa0404040);
        RenderUtils.fillRoundRect(guiGraphics, this.getX() + i + 3, this.getY() + 3, 10, 10, 0.5f, Palette.HIGHLIGHT_COLOR ^ (this.isFocused() ? 0x40000000 : 0x80000000));
        Minecraft minecraft = Minecraft.getInstance();
        this.renderScrollingString(guiGraphics, minecraft.font, 2, Palette.NORMAL_TEXT_COLOR);
        Palette.setShaderAlpha(alpha);
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {
        narrationElementOutput.add(NarratedElementType.TITLE, this.getMessage());
    }

    @Override
    public void onClick(double mouseX, double mouseY, int button) {
        if (!this.isFocused()) {
            return;
        }
        this.setValueFromMouse(mouseX);
    }

    @Override
    public void setFocused(boolean focused) {
        super.setFocused(focused);
        if (!focused) {
            this.canChangeValue = false;
        } else {
            InputType inputtype = Minecraft.getInstance().getLastInputType();
            if (inputtype == InputType.MOUSE || inputtype == InputType.KEYBOARD_TAB) {
                this.canChangeValue = true;
            }
        }
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (CommonInputs.selected(keyCode)) {
            this.canChangeValue = !this.canChangeValue;
            return true;
        } else {
            if (this.canChangeValue) {
                boolean flag = keyCode == 263;
                if (flag || keyCode == 262) {
                    float f = flag ? -1.0F : 1.0F;
                    this.setValue(this.value + (double)(f / (float)(this.width - 8)));
                    return true;
                }
            }

            return false;
        }
    }

    @Override
    public void setRectangle(int width, int height, int x, int y) {
        super.setRectangle(width, 16, x, y);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        this.setValue(this.value + scrollY);
        return true;
    }

    private void setValueFromMouse(double mouseX) {
        this.setValue((mouseX - (double)(this.getX() + 4 + 8)) / (double)(this.width - 8 - 16));
    }

    protected void setValue(double value) {
        double d0 = this.value;
        this.value = Mth.clamp(value, 0.0, 1.0);
        if (d0 != this.value) {
            this.animation.reset(this.display, this.value);
            this.animator.play(this.animation);
            this.updateMessage();
        }
    }

    protected void onDrag(double mouseX, double mouseY, double dragX, double dragY) {
        this.setValueFromMouse(mouseX);
    }

    protected void updateMessage() {
        this.setMessage(Component.literal(String.format("%.2f", this.value)));
    }

    public double getValue() {
        return this.value;
    }

    private class SmoothAnimation extends AbstractScreenAnimation {
        public double src;
        public double dest;

        protected SmoothAnimation() {
            super(0.2f);
        }

        protected void reset(double src, double dest) {
            this.src = src;
            this.dest = dest;
        }

        @Override
        protected void run(float time) {
            Slider.this.display = AnimateUtils.Lerp.smooth(this.src, this.dest, Mth.clamp(time / 0.2f, 0, 1));
        }
    }
}
