package net.quepierts.thatskyinteractions.client.gui.screen;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.entity.Entity;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.quepierts.simpleanimator.api.IAnimateHandler;
import net.quepierts.thatskyinteractions.ThatSkyInteractions;
import net.quepierts.thatskyinteractions.client.gui.Palette;
import net.quepierts.thatskyinteractions.client.gui.component.tree.InteractTreeWidget;
import net.quepierts.thatskyinteractions.client.gui.layer.CandleInfoLayer;
import net.quepierts.thatskyinteractions.data.tree.InteractTree;
import net.quepierts.thatskyinteractions.data.tree.InteractTreeInstance;
import net.quepierts.thatskyinteractions.data.tree.node.InteractTreeNode;
import net.quepierts.thatskyinteractions.proxy.Animations;
import org.jetbrains.annotations.NotNull;

@OnlyIn(Dist.CLIENT)
public class PlayerInteractScreen extends RightPoopScreen {
    private final InteractTree tree;
    private final InteractTreeInstance instance;
    private InteractTreeWidget treeWidget;

    public PlayerInteractScreen(Entity entity, InteractTree tree, InteractTreeInstance instance) {
        super(entity.getDisplayName(), InteractTreeNode.NODE_SIZE * 5 + 40);
        this.tree = tree;
        this.instance = instance;
    }

    @Override
    protected void init() {
        super.init();

        this.treeWidget = new InteractTreeWidget(
                this, 8, 0, InteractTreeNode.NODE_SIZE * 5, this.height, tree, instance, animator
        );
        this.addRenderableWidget(this.treeWidget);
        CandleInfoLayer.INSTANCE.show(true);
    }

    @Override
    public void render(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.render(guiGraphics, mouseX, mouseY, partialTick);

        if (ThatSkyInteractions.getInstance().getClient().getTarget() == null) {
            Minecraft.getInstance().popGuiLayer();
        }
    }

    @Override
    public void onClose() {
        super.onClose();
        ThatSkyInteractions.getInstance().getClient().setTarget(null);
        CandleInfoLayer.INSTANCE.show(false);

        IAnimateHandler handler = ((IAnimateHandler) Minecraft.getInstance().player);
        if (handler.simpleanimator$isRunning() && handler.simpleanimator$getAnimator().getAnimationLocation().equals(Animations.HELD_CANDLE)) {
            handler.simpleanimator$stopAnimate(true);
        }
    }

    @Override
    public void renderOriginal(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        guiGraphics.drawCenteredString(this.font, this.getTitle(), this.width / 2, 20, Palette.NORMAL_TEXT_COLOR);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        return this.treeWidget.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
    }
}
