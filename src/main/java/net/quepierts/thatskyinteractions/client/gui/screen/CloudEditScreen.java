package net.quepierts.thatskyinteractions.client.gui.screen;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FastColor;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.quepierts.simpleanimator.core.SimpleAnimator;
import net.quepierts.thatskyinteractions.block.entity.CloudBlockEntity;
import net.quepierts.thatskyinteractions.block.entity.ColoredCloudBlockEntity;
import net.quepierts.thatskyinteractions.client.gui.Palette;
import net.quepierts.thatskyinteractions.client.gui.component.button.SqueezeButton;
import net.quepierts.thatskyinteractions.client.gui.component.label.ColorInputLabel;
import net.quepierts.thatskyinteractions.client.gui.component.label.Vector3InputLabel;
import net.quepierts.thatskyinteractions.client.gui.layer.AnimateScreenHolderLayer;
import net.quepierts.thatskyinteractions.network.packet.block.UpdateCloudDataPacket;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3i;

@OnlyIn(Dist.CLIENT)
public class CloudEditScreen extends AnimatedScreen {
    private final CloudBlockEntity cloud;
    private Vector3InputLabel offset;
    private Vector3InputLabel size;
    private Vector3InputLabel color;

    public CloudEditScreen(Component title, CloudBlockEntity cloud) {
        super(title);
        this.cloud = cloud;
    }

    @Override
    protected void init() {
        super.init();

        if (this.cloud != null) {
            int middle = this.width / 2;
            boolean editColor = this.cloud instanceof ColoredCloudBlockEntity;
            int sliderWidth = 100;
            int sliderHeight = 160;

            int left = editColor ? middle - 160 : middle - 105;
            int top = this.height / 2 - 100;

            this.offset = new Vector3InputLabel(Component.literal("Offset"), this.getAnimator(), left, top, sliderWidth, sliderHeight, 10, 30, -32 * 16, 32 * 16);
            this.offset.setValue(this.cloud.getOffset());
            this.addRenderableWidget(this.offset);
            this.size = new Vector3InputLabel(Component.literal("Size"), this.getAnimator(), left + 110, top, sliderWidth, sliderHeight, 10, 30, 4, 64 * 16);
            this.size.setValue(this.cloud.getSize());
            this.addRenderableWidget(this.size);

            if (editColor) {
                this.color = new ColorInputLabel(this.getAnimator(), left + 220, top, sliderWidth, sliderHeight, 10, 30);
                int argb = ((ColoredCloudBlockEntity) this.cloud).getColor();
                this.color.setValue(new Vector3i(
                        FastColor.ARGB32.red(argb),
                        FastColor.ARGB32.green(argb),
                        FastColor.ARGB32.blue(argb)
                ));
                this.addRenderableWidget(this.color);
            }


            this.addRenderableWidget(new SqueezeButton(
                    middle - 16, this.height / 2 + 80, 32, CommonComponents.GUI_DONE, this.getAnimator(), ConfirmScreen.ICON_CONFIRM
            ) {
                @Override
                public void onPress() {
                    CloudEditScreen.this.onDone();
                }
            });
        } else {
            AnimateScreenHolderLayer.INSTANCE.pop(this);
        }
    }

    @Override
    public void tick() {
        if (this.minecraft == null || this.minecraft.player == null || this.cloud.isRemoved()) {
            AnimateScreenHolderLayer.INSTANCE.pop(this);
        }
    }

    @Override
    public void irender(GuiGraphics guiGraphics, int mouseX, int mouseY, float delta) {

        float value = this.enter.getValue();
        if (value == 1.0f) {
            this.renderBlurredBackground(delta);
        }
        Palette.setShaderAlpha(value);
        for (Renderable renderable : this.renderables) {
            renderable.render(guiGraphics, mouseX, mouseX, delta);
        }
    }

    @Override
    public void resize(@NotNull Minecraft minecraft, int width, int height) {
        super.resize(minecraft, width, height);

        int middle = this.width / 2;
        boolean editColor = this.cloud instanceof ColoredCloudBlockEntity;
        int sliderWidth = 100;
        int sliderHeight = 160;

        int left = editColor ? middle - 160 : middle - 105;
        int top = this.height / 2 - 80;

        this.offset.onResize(left, top, sliderWidth, sliderHeight);
        this.size.onResize(left + 110, top, sliderWidth, sliderHeight);

        if (editColor) {
            this.color.onResize(left + 220, top, sliderWidth, sliderHeight);
        }
    }

    private void onDone() {
        if (this.cloud == null)
            return;

        Vector3i offset = this.offset.getVector3();
        Vector3i size = this.size.getVector3();

        this.cloud.setOffset(offset.x, offset.y, offset.z);
        this.cloud.setSize(size.x, size.y, size.z);

        if (this.cloud instanceof ColoredCloudBlockEntity colored) {
            if (this.color != null) {
                Vector3i color = this.color.getVector3();
                colored.setColor(FastColor.ARGB32.color(color.x, color.y, color.z));
            }
        }

        this.cloud.markUpdate();
        SimpleAnimator.getNetwork().update(new UpdateCloudDataPacket(this.cloud));
        AnimateScreenHolderLayer.INSTANCE.pop(this);
    }
}
