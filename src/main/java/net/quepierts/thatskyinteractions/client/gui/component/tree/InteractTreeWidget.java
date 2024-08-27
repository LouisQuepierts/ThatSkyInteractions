package net.quepierts.thatskyinteractions.client.gui.component.tree;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayFIFOQueue;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.quepierts.simpleanimator.api.IAnimateHandler;
import net.quepierts.thatskyinteractions.ThatSkyInteractions;
import net.quepierts.thatskyinteractions.client.gui.animate.AnimateUtils;
import net.quepierts.thatskyinteractions.client.gui.animate.LerpNumberAnimation;
import net.quepierts.thatskyinteractions.client.gui.animate.ScreenAnimator;
import net.quepierts.thatskyinteractions.client.gui.component.CulledRenderable;
import net.quepierts.thatskyinteractions.client.gui.component.GlowingLine;
import net.quepierts.thatskyinteractions.client.gui.component.Resizable;
import net.quepierts.thatskyinteractions.client.gui.holder.DoubleHolder;
import net.quepierts.thatskyinteractions.client.gui.layer.AnimateScreenHolderLayer;
import net.quepierts.thatskyinteractions.client.gui.layer.CandleInfoLayer;
import net.quepierts.thatskyinteractions.client.gui.screen.AnimatableScreen;
import net.quepierts.thatskyinteractions.client.gui.screen.ConfirmScreen;
import net.quepierts.thatskyinteractions.data.tree.InteractTree;
import net.quepierts.thatskyinteractions.data.tree.InteractTreeInstance;
import net.quepierts.thatskyinteractions.data.tree.NodeState;
import net.quepierts.thatskyinteractions.data.tree.node.InteractTreeNode;
import net.quepierts.thatskyinteractions.proxy.Animations;
import org.apache.commons.compress.utils.Lists;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector4f;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@OnlyIn(Dist.CLIENT)
public class InteractTreeWidget extends AbstractWidget implements Resizable {
    private static final float SQRT2 = 1.414213562373095f;
    private final Minecraft minecraft = Minecraft.getInstance();
    private final AnimatableScreen parent;
    private final InteractTreeInstance treeInstance;
    private final ScreenAnimator animator;
    private final LerpNumberAnimation animation;
    private final List<CulledRenderable> renderables = Lists.newArrayList();
    private final List<TreeNodeButton> widgets = Lists.newArrayList();

    private final DoubleHolder scroll0;
    private double scroll;
    private int max;

    private TreeNodeButton clickUnlockable;
    private int clickUnlockableTimes = 0;
    private float clickUnlockableTimer = 0.0f;

    public InteractTreeWidget(AnimatableScreen parent, int x, int y, int width, int height, InteractTree tree, InteractTreeInstance instance, ScreenAnimator animator) {
        super(x, y, width, height, Component.empty());
        this.parent = parent;
        this.treeInstance = instance;

        this.animator = animator;
        this.scroll0 = new DoubleHolder(0.0);
        this.animation = new LerpNumberAnimation(this.scroll0, AnimateUtils.Lerp::smooth, 0.0f, 0.0f, 0.3f);
        this.reset(tree, instance);
    }

    protected void reset(InteractTree tree, InteractTreeInstance instance) {
        this.widgets.clear();
        this.renderables.clear();

        this.clickUnlockable = null;
        this.clickUnlockableTimes = 0;
        this.clickUnlockableTimer = 0.0f;
        final int middle = this.width / 2;

        int length = 0;

        Object2ObjectMap<String, InteractTreeNode> nodes = tree.getNodes();
        ObjectArrayFIFOQueue<Pair<String, NodeState>> queue = new ObjectArrayFIFOQueue<>(nodes.size());
        queue.enqueue(Pair.of(tree.getRootID(), NodeState.byUnlocked(instance.isUnlocked(tree.getRootID()))));

        for (Map.Entry<String, InteractTreeNode> entry : nodes.entrySet()) {
            final InteractTreeNode node = entry.getValue();
            final TreeNodeButton button = node.asButton(animator, instance.getNodeState(entry.getKey()));

            length = Math.max(length, node.getY());
            button.setY(height - button.getY() - 64);
            button.setX(button.getX() + middle - 16);
            this.renderables.add(button);
            this.widgets.add(button);

            final int lineY = button.getY() + 32;

            if (node.hasMiddle()) {
                final String key = node.getMiddle();
                final InteractTreeNode mid = nodes.get(key);
                this.renderables.add(new GlowingLine(
                        button.getX() + 8, lineY, animator, mid.getY() - node.getY() - 32, instance.getNodeState(key), 180
                ));
            }

            if (node.hasLeft()) {
                final String key = node.getLeft();
                final InteractTreeNode left = nodes.get(key);
                this.renderables.add(new GlowingLine(
                        button.getX() + 16, lineY, animator, (int) ((node.getX() - left.getX() - 24) * SQRT2), instance.getNodeState(key), 135
                ));
            }

            if (node.hasRight()) {
                final String key = node.getRight();
                final InteractTreeNode right = nodes.get(key);
                this.renderables.add(new GlowingLine(
                        button.getX(), lineY, animator, (int) ((right.getX() - node.getX() - 24) * SQRT2), instance.getNodeState(key), 225
                ));
            }
        }

        this.max = Math.max(0, length + 96 - height);
    }

    @Override
    protected void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float v) {
        if (this.scroll0.get() < 0.0 && this.animation.getDest() < 0.0f) {
            this.animation.reset(this.scroll0.get(), 0.0f);
            this.animator.play(this.animation);
        } else if (this.scroll0.get() > this.max && this.animation.getDest() > this.max) {
            this.animation.reset(this.scroll0.get(), this.max);
            this.animator.play(this.animation);
        }

        if (this.clickUnlockableTimer > 0) {
            this.clickUnlockableTimer -= v;
        } else if (this.clickUnlockableTimes != 0) {
            this.clearClickCounter();
        }

        PoseStack pose = guiGraphics.pose();
        pose.pushPose();

        pose.translate(this.getX(), this.getY() + this.scroll0.get(), 0.0f);
        int localMouseX = mouseX - this.getX();
        int localMouseY = mouseY - this.getY();

        Vector4f rect = new Vector4f(0, (float) -this.scroll0.get(), this.width, this.height);
        for (CulledRenderable renderable : this.renderables) {
            if (renderable.shouldRender(rect))
                renderable.render(guiGraphics, localMouseX, localMouseY, v);
        }
        pose.popPose();
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        double localMouseX = mouseX - this.getX();
        double localMouseY = mouseY - this.getY() - this.scroll0.get();
        for (TreeNodeButton btn : this.widgets) {
            if (!btn.mouseClicked(localMouseX, localMouseY, button)) {
                continue;
            }

            switch (btn.getNodeState()) {
                case LOCKED:
                    btn.onClickLocked();
                    break;
                case UNLOCKABLE:
                    int counted = minecraft.player.getInventory().countItem(btn.currency.item);

                    if (counted < btn.getPrice()) {
                        btn.onClickLocked();
                        break;
                    }

                    IAnimateHandler handler = (IAnimateHandler) minecraft.player;
                    if (!handler.simpleanimator$isRunning() && !handler.simpleanimator$getAnimator().getAnimationLocation().equals(Animations.HELD_CANDLE))
                        handler.simpleanimator$playAnimate(Animations.HELD_CANDLE, true);

                    if (btn == this.clickUnlockable) {
                        this.clickUnlockableTimes ++;
                    } else {
                        this.clickUnlockable = btn;
                        this.clickUnlockableTimes = 1;
                        CandleInfoLayer.INSTANCE.refund(btn.getCurrency());
                    }

                    this.clickUnlockableTimer = 40;
                    if (this.clickUnlockableTimes > 2 || this.clickUnlockableTimes == btn.price) {
                        CandleInfoLayer.INSTANCE.setShrink(btn.currency, btn.price);
                        CandleInfoLayer.INSTANCE.freeze(btn.currency);

                        AnimateScreenHolderLayer.INSTANCE.push(
                            new ConfirmScreen(
                                    Component.empty(),
                                    new UnlockNodeInviteConfirmProvider(parent, btn),
                                    264, 176
                            )
                        );
                        CandleInfoLayer.INSTANCE.shrink(btn.currency, 1);
                    }
                    btn.onClickUnlockable(this.clickUnlockableTimes);
                    break;
                case UNLOCKED:
                    btn.onClickUnlocked();
                    break;
            }

            return true;
        }

        return false;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        if (this.scroll * scrollY < 0) {
            this.scroll = Mth.clamp(this.scroll, 0.0, this.max);
        }

        this.scroll += scrollY * 16;
        this.animation.reset(this.scroll0.get(), this.scroll);
        this.animator.play(this.animation);
        return true;
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {

    }

    @Override
    public void resize(@NotNull Minecraft minecraft, int width, int height) {
        final int yDifferent = height - this.height;

        for (CulledRenderable renderable : this.renderables) {
            int y = renderable.getY();
            renderable.setY(y + yDifferent);
        }
        
        this.height = height;
    }

    private void clearClickCounter() {
        this.clickUnlockableTimes = 0;
        CandleInfoLayer.INSTANCE.refund(this.clickUnlockable.getCurrency());
        this.clickUnlockable = null;
    }

    private class UnlockNodeInviteConfirmProvider extends ButtonConfirmProvider {
        protected UnlockNodeInviteConfirmProvider(AnimatableScreen screen, TreeNodeButton button) {
            super(screen, button);
        }

        @Override
        public void confirm() {
            UUID other = treeInstance.getPair().getOther(minecraft.player.getUUID());
            ThatSkyInteractions.getInstance().getClient().getUnlockRelationshipHandler().invite(
                    other,
                    button.id,
                    this::onAccepted,
                    this::onCanceled
            );
        }

        @Override
        public void cancel() {
            CandleInfoLayer.INSTANCE.unfreeze(button.currency);
            clearClickCounter();
            //screen.enter();
        }

        private void onAccepted() {
            CandleInfoLayer.INSTANCE.unfreeze(button.currency);
            CandleInfoLayer.INSTANCE.refund(button.currency);
            //screen.enter();
            reset(treeInstance.getTree(), treeInstance);
            minecraft.getSoundManager().play(
                    SimpleSoundInstance.forUI(
                            SoundEvents.PLAYER_LEVELUP,
                            1.0f
                    )
            );
        }

        private void onCanceled() {
            CandleInfoLayer.INSTANCE.unfreeze(button.currency);
            CandleInfoLayer.INSTANCE.refund(button.currency);
            //screen.enter();
        }
    }
}
