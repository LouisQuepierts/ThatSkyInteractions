package net.quepierts.thatskyinteractions.client.gui.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.StringUtil;
import net.quepierts.thatskyinteractions.client.gui.Palette;
import net.quepierts.thatskyinteractions.client.gui.component.button.SqueezeButton;
import net.quepierts.thatskyinteractions.client.gui.layer.AnimateScreenHolderLayer;
import net.quepierts.thatskyinteractions.client.util.RenderUtils;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public class InputScreen extends AnimatedScreen {
    public static final int BG_COLOR = 0xc0101010;

    private final ResourceLocation icon;
    private final int boxWidth;
    private final int boxHeight;
    private final EditBox editBox;
    private final Consumer<String> consumer;

    public InputScreen(ResourceLocation icon, int boxWidth, int boxHeight, Component message, String def, Consumer<String> consumer) {
        super(message);
        this.icon = icon;
        this.boxWidth = boxWidth;
        this.boxHeight = boxHeight;
        this.consumer = consumer;
        this.editBox = new EditBox(
                Minecraft.getInstance().font,
                boxWidth / -4, 6,
                boxWidth / 2, 16,
                Component.empty()
        );
        this.editBox.setValue(def);
    }

    public InputScreen(ResourceLocation icon, int boxWidth, int boxHeight, Component message) {
        super(message);
        this.icon = icon;
        this.boxWidth = boxWidth;
        this.boxHeight = boxHeight;
        this.consumer = (string) -> {};
        this.editBox = new EditBox(
                Minecraft.getInstance().font,
                boxWidth / -4, 6,
                boxWidth / 2, 16,
                Component.empty()
        );
    }

    @Override
    protected void init() {
        this.addRenderableWidget(this.editBox);

        int bottom = this.boxHeight / 2 - 52;

        this.addRenderableWidget(new SqueezeButton(20, bottom, 32, Component.literal(""), this.animator, ConfirmScreen.ICON_CONFIRM) {
            @Override
            public void onPress() {
                String value = editBox.getValue();
                if (StringUtil.isNullOrEmpty(value)) {
                    return;
                }
                consumer.accept(value);
                AnimateScreenHolderLayer.INSTANCE.pop(InputScreen.this);
            }
        });

        this.addRenderableWidget(new SqueezeButton(-52, bottom, 32, Component.literal(""), this.animator, ConfirmScreen.ICON_CANCEL) {
            @Override
            public void onPress() {
                AnimateScreenHolderLayer.INSTANCE.pop(InputScreen.this);
            }
        });
    }

    @Override
    public final void irender(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        if (this.enter.getValue() < 0.02f)
            return;

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
        RenderUtils.fillRoundRect(guiGraphics, -hWidth, -hHeight, this.boxWidth, this.boxHeight, 0.05f, BG_COLOR);

        RenderSystem.enableBlend();
        Palette.useUnlockedIconColor();
        RenderUtils.blitIcon(guiGraphics, this.icon, -20, 40 - yMid, 40, 40);
        Palette.reset();

        mouseX -= xMid;
        mouseY -= yMid;
        for (Renderable renderable : this.renderables) {
            renderable.render(guiGraphics, mouseX, mouseY, partialTick);
        }

        pose.scale(1.25f, 1.25f, 1.25f);
        guiGraphics.drawCenteredString(this.font, this.title, 0, -20, 0xffffffff);

        Palette.setShaderAlpha(alpha);
        RenderSystem.disableBlend();

        pose.popPose();
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        return super.mouseClicked(mouseX - (this.width / 2.0), mouseY - (this.height / 2.0), button);
    }
}
