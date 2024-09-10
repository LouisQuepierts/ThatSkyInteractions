package net.quepierts.thatskyinteractions.client.gui.component.label;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.quepierts.thatskyinteractions.client.gui.Palette;
import net.quepierts.thatskyinteractions.client.gui.animate.ScreenAnimator;
import net.quepierts.thatskyinteractions.client.gui.component.WidgetHolder;
import net.quepierts.thatskyinteractions.client.util.RenderUtils;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

@OnlyIn(Dist.CLIENT)
public class TransparentLabel extends AbstractWidget implements Renderable, WidgetHolder {
    protected final ScreenAnimator animator;
    protected final Minecraft minecraft;
    private final List<AbstractWidget> widgets;
    public TransparentLabel(@NotNull Component title, int xPos, int yPos, int width, int height, ScreenAnimator animator) {
        super(xPos, yPos, width, height, title);
        this.animator = animator;
        this.widgets = new ArrayList<>();
        this.minecraft = Minecraft.getInstance();
    }

    public void addToParent(@NotNull Consumer<AbstractWidget> consumer) {
        this.widgets.forEach(consumer);
    }

    protected void addWidgets(AbstractWidget... widgets) {
        Collections.addAll(this.widgets, widgets);
    }

    @Override
    protected void renderWidget(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        float alpha = Palette.getShaderAlpha();
        RenderSystem.enableBlend();
        RenderUtils.fillRoundRect(guiGraphics, this.getX(), this.getY(), this.getWidth(), this.getHeight(), 0.1f * this.getHeight() / this.getWidth(), 0x80000000);
        guiGraphics.drawCenteredString(this.minecraft.font, this.getMessage(), this.getX() + this.getWidth() / 2, this.getY() + 10, Palette.NORMAL_TEXT_COLOR);
        RenderSystem.disableBlend();
        Palette.setShaderAlpha(alpha);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        return false;
    }

    @Override
    protected void updateWidgetNarration(@NotNull NarrationElementOutput narrationElementOutput) {
        narrationElementOutput.add(NarratedElementType.TITLE, this.getMessage());
    }

    public void onResize(int xPos, int yPos, int width, int height) {}
}
