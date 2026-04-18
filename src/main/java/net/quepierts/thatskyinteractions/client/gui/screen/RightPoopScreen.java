package net.quepierts.thatskyinteractions.client.gui.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.network.chat.Component;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.quepierts.thatskyinteractions.client.gui.Palette;
import org.jetbrains.annotations.NotNull;

@OnlyIn(Dist.CLIENT)
public class RightPoopScreen extends AnimatedScreen {
    public static final int BG_COLOR = 0xc0101010;

    protected final int size;

    protected RightPoopScreen(Component title, int size) {
        super(title);
        this.size = size;
    }

    @Override
    public final void irender(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        if (this.enter.getValue() < 0.02f)
            return;

        PoseStack pose = guiGraphics.pose();
        pose.pushPose();
        RenderSystem.enableBlend();

        float alpha = Palette.getShaderAlpha();
        Palette.setShaderAlpha(this.enter.getValue());
        float v = this.width - this.size;

        renderOriginal(guiGraphics, mouseX, mouseY, partialTick);

        pose.translate((1.0f - this.enter.get()) * 40.0f + v, 0.0f, 0.0f);
        guiGraphics.fill(0, 0, this.width, this.height, BG_COLOR);

        renderLabel(guiGraphics, mouseX, mouseY, partialTick);

        Palette.setShaderAlpha(alpha);
        RenderSystem.disableBlend();
        pose.popPose();
    }

    protected void renderOriginal(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {

    }

    protected void renderLabel(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        for (Renderable renderable : this.renderables) {
            renderable.render(guiGraphics, mouseX, mouseY, partialTick);
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        return super.mouseClicked(mouseX - this.width + this.size, mouseY, button);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
