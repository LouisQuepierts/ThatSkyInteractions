package net.quepierts.thatskyinteractions.client.gui.screen.blockentity;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.quepierts.simpleanimator.core.SimpleAnimator;
import net.quepierts.thatskyinteractions.block.entity.MuralBlockEntity;
import net.quepierts.thatskyinteractions.client.gui.Palette;
import net.quepierts.thatskyinteractions.client.gui.component.button.SqueezeButton;
import net.quepierts.thatskyinteractions.client.gui.component.label.MuralPreviewLabel;
import net.quepierts.thatskyinteractions.client.gui.component.label.Vector2InputLabel;
import net.quepierts.thatskyinteractions.client.gui.component.label.Vector3InputLabel;
import net.quepierts.thatskyinteractions.client.gui.layer.AnimateScreenHolderLayer;
import net.quepierts.thatskyinteractions.client.gui.screen.AnimatedScreen;
import net.quepierts.thatskyinteractions.client.gui.screen.ConfirmScreen;
import net.quepierts.thatskyinteractions.network.packet.block.UpdateBlockEntityDataPacket;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector2i;
import org.joml.Vector3i;

@OnlyIn(Dist.CLIENT)
public class MuralEditScreen extends AnimatedScreen {
    private final MuralBlockEntity mural;
    private Vector3InputLabel offset;
    private Vector3InputLabel rotate;
    private Vector2InputLabel size;
    private MuralPreviewLabel preview;


    public MuralEditScreen(MuralBlockEntity mural) {
        super(Component.translatable("screen.mural.title"));
        this.mural = mural;
    }

    @Override
    protected void init() {
        if (!this.mural.isRemoved()) {
            int middle = this.width / 2;

            int left = middle - 190;
            int top = this.height / 2 - 116;

            this.offset = new Vector3InputLabel(Component.literal("Offset"), this.getAnimator(), left, top, 100, 100, 10, 30, -8 * 16, 8 * 16);
            this.offset.setValue(this.mural.getOffset());
            this.addRenderableWidget(this.offset);

            this.rotate = new Vector3InputLabel(Component.literal("Rotate"), this.getAnimator(), left + 110, top, 100, 100, 10, 30, -180, 180);
            this.rotate.setValue(this.mural.getRotate());
            this.addRenderableWidget(this.rotate);

            this.size = new Vector2InputLabel(Component.literal("Size"), this.getAnimator(), left, top + 110, 210, 80, 10, 30, 4, 16 * 16);
            this.size.setValue(this.mural.getSize());
            this.addRenderableWidget(this.size);

            this.preview = new MuralPreviewLabel(
                    this.getAnimator(),
                    left + 220, top, 160, 190,
                    this.mural,
                    this.offset::getDisplay,
                    this.rotate::getDisplay,
                    this.size::getDisplay
            );
            this.addRenderableWidget(this.preview);

            this.addRenderableWidget(new SqueezeButton(
                    middle - 16, this.height / 2 + 80, 32, CommonComponents.GUI_DONE, this.getAnimator(), ConfirmScreen.ICON_CONFIRM
            ) {
                @Override
                public void onPress() {
                    MuralEditScreen.this.onDone();
                }
            });
        } else {
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

        int left = middle - 190;
        int top = this.height / 2 - 116;

        this.offset.onResize(left, top, 100, 100);
        this.rotate.onResize(left + 110, top, 100, 100);
        this.size.onResize(left, top + 110, 210, 80);
        this.preview.onResize(left + 220, top, 160, 190);
    }

    @Override
    public void tick() {
        if (this.minecraft == null || this.minecraft.player == null || this.mural.isRemoved()) {
            AnimateScreenHolderLayer.INSTANCE.pop(this);
        }
    }

    private void onDone() {
        if (!this.mural.isRemoved()) {
            Vector3i offset = this.offset.getVector3();
            Vector2i size = this.size.getVector2();
            Vector3i rotate = this.rotate.getVector3();
            ResourceLocation texture = this.preview.getTexture();

            this.mural.setOffset(offset.x, offset.y, offset.z);
            this.mural.setSize(size.x, size.y);
            this.mural.setRotate(rotate.x, rotate.y, rotate.z);
            this.mural.setMuralTexture(texture);
            this.mural.updateAABB();

            this.mural.markUpdate();
            SimpleAnimator.getNetwork().update(new UpdateBlockEntityDataPacket(this.mural));
        }
        AnimateScreenHolderLayer.INSTANCE.pop(this);
    }
}
