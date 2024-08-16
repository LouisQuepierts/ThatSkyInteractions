package net.quepierts.thatskyinteractions.client.gui.screen.confirm;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.quepierts.thatskyinteractions.ThatSkyInteractions;
import net.quepierts.thatskyinteractions.client.gui.Palette;
import net.quepierts.thatskyinteractions.client.gui.animate.AnimateUtils;
import net.quepierts.thatskyinteractions.client.gui.animate.LerpNumberAnimation;
import net.quepierts.thatskyinteractions.client.gui.animate.ScreenAnimator;
import net.quepierts.thatskyinteractions.client.gui.component.button.SqueezeButton;
import net.quepierts.thatskyinteractions.client.gui.holder.FloatHolder;
import net.quepierts.thatskyinteractions.client.gui.layer.AnimateScreenHolderLayer;
import net.quepierts.thatskyinteractions.client.gui.screen.AnimatableScreen;

public class ConfirmScreen extends Screen implements AnimatableScreen {
    public static final ResourceLocation ICON_CONFIRM = ThatSkyInteractions.getLocation("textures/gui/confirm.png");
    public static final ResourceLocation ICON_CANCEL = ThatSkyInteractions.getLocation("textures/gui/cancel.png");
    private final ScreenAnimator animator;
    private final FloatHolder enter;
    private final ConfirmProvider provider;

    private final int boxWidth;
    private final int boxHeight;
    public ConfirmScreen(Component title, ConfirmProvider provider, int boxWidth, int boxHeight) {
        super(title);
        this.provider = provider;
        this.boxWidth = boxWidth;
        this.boxHeight = boxHeight;
        this.animator = new ScreenAnimator();
        enter = new FloatHolder(0.0f);

        AnimateScreenHolderLayer.INSTANCE.open(this);
    }

    @Override
    public void enter() {
        this.animator.play(new LerpNumberAnimation(this.enter, AnimateUtils.Lerp::smooth, 0.0, 1.0, 0.5f));
    }

    @Override
    public void hide() {
        this.animator.play(new LerpNumberAnimation(this.enter, AnimateUtils.Lerp::smooth, 1.0, 0.0, 0.5f, false));
    }

    @Override
    protected void init() {
        int bottom = this.boxHeight / 2 - 52;

        this.addRenderableWidget(new SqueezeButton(20, bottom, 32, Component.literal(""), this.animator, ICON_CONFIRM) {
            @Override
            public void onPress() {
                provider.confirm();
                Minecraft.getInstance().popGuiLayer();
            }
        });

        this.addRenderableWidget(new SqueezeButton(-52, bottom, 32, Component.literal(""), this.animator, ICON_CANCEL) {
            @Override
            public void onPress() {
                provider.cancel();
                Minecraft.getInstance().popGuiLayer();
            }
        });
    }

    @Override
    public ScreenAnimator getAnimator() {
        return this.animator;
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {}

    @Override
    public void irender(GuiGraphics guiGraphics, int mouseX, int mouseY, float delta) {
        int xMid = this.width / 2;
        int yMid = this.height / 2;

        PoseStack pose = guiGraphics.pose();
        pose.pushPose();
        pose.translate(xMid, yMid, 0);

        RenderSystem.enableBlend();
        float alpha = Palette.getShaderAlpha();
        Palette.setShaderAlpha(this.enter.getValue());

        int hWidth = this.boxWidth / 2;
        int hHeight = this.boxHeight / 2;
        guiGraphics.fill(-hWidth, -hHeight, hWidth, hHeight, 0xc0101010);

        mouseX -= xMid;
        mouseY -= yMid;
        for (Renderable renderable : this.renderables) {
            renderable.render(guiGraphics, mouseX, mouseY, delta);
        }

        this.provider.render(guiGraphics, this.boxWidth, this.boxHeight);

        Palette.setShaderAlpha(alpha);
        RenderSystem.disableBlend();

        pose.popPose();
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        return super.mouseClicked(mouseX - (this.width / 2.0), mouseY - (this.height / 2.0), button);
    }

    @Override
    public boolean shouldCloseOnEsc() {
        this.provider.cancel();
        return true;
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public void removed() {
        AnimateScreenHolderLayer.INSTANCE.close(this);
    }

    @Override
    public void resize(Minecraft minecraft, int width, int height) {
        this.width = width;
        this.height = height;
    }
}
