package net.quepierts.thatskyinteractions.client.gui.component.astrolabe;

import it.unimi.dsi.fastutil.objects.ObjectList;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.quepierts.thatskyinteractions.client.gui.animate.AnimateUtils;
import net.quepierts.thatskyinteractions.client.gui.animate.LerpNumberAnimation;
import net.quepierts.thatskyinteractions.client.gui.component.CulledRenderable;
import net.quepierts.thatskyinteractions.client.gui.component.Resizable;
import net.quepierts.thatskyinteractions.client.gui.holder.FloatHolder;
import net.quepierts.thatskyinteractions.client.gui.screen.AnimatableScreen;
import net.quepierts.thatskyinteractions.client.gui.screen.AnimatedScreen;
import net.quepierts.thatskyinteractions.data.astrolabe.Astrolabe;
import net.quepierts.thatskyinteractions.data.astrolabe.node.AstrolabeNode;
import org.apache.commons.compress.utils.Lists;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector2f;

import java.util.List;

@OnlyIn(Dist.CLIENT)
public class AstrolabeWidget extends AbstractWidget implements Resizable {
    private final AnimatableScreen parent;
    private final List<CulledRenderable> renderables = Lists.newArrayList();
    private final List<AstrolabeButton> buttons = Lists.newArrayList();

    private final FloatHolder alpha = new FloatHolder(0.0f);
    private final LerpNumberAnimation alphaAnimation = new LerpNumberAnimation(this.alpha, AnimateUtils.Lerp::smooth, 0,1, 1);

    private float rotate = 0f;
    //private float selected = 0;


    public AstrolabeWidget(AnimatedScreen parent, int horizontal, int vertical) {
        super(horizontal, vertical, parent.width, parent.height, Component.empty());
        this.parent = parent;
    }

    public void reset(Astrolabe astrolabe) {
        this.renderables.clear();
        this.buttons.clear();

        ObjectList<AstrolabeNode> nodes = astrolabe.getNodes();
        for (AstrolabeNode node : nodes) {
            AstrolabeButton button = new FriendButton(node.x, node.y, Component.empty(), this.parent.getAnimator(), this.alpha);
            this.renderables.add(button);
            this.buttons.add(button);
        }

        Vector2f yAxis = new Vector2f(0.0f, 1.0f);
        for (Astrolabe.Connection connection : astrolabe.getConnections()) {
            AstrolabeButton a = this.buttons.get(connection.a());
            AstrolabeButton b = this.buttons.get(connection.b());

            Vector2f ab = new Vector2f(b.getX() - a.getX(), b.getY() - a.getY());
            float length = ab.length();
            ab.normalize();
            float angle = (float) Math.acos(yAxis.dot(ab));

            if (ab.x > 0) {
                angle = -angle;
            }

            float hWidth = a.getWidth() / 2f;
            float hHeight = b.getWidth() / 2f;

            ab.mul(hWidth - 1);

            this.renderables.add(new AstrolabeLine(
                    ab.add(a.getX(), a.getY()), length - hWidth - hHeight + 2, angle, this.alpha
            ));
        }

        for (AstrolabeNode node : nodes) {
            this.renderables.add(new StarLight(
                    node.x, node.y, this, this.alpha
            ));
        }
    }

    public void renderAstrolabe(GuiGraphics guiGraphics, int mouseX, int mouseY, float pTick, float rotate) {
        this.rotate = rotate;
        for (CulledRenderable renderable : this.renderables) {
            renderable.render(guiGraphics, mouseX, mouseY, pTick);
        }
    }

    @Override
    protected void renderWidget(GuiGraphics guiGraphics, int i, int i1, float v) {}

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        for (AstrolabeButton btn : this.buttons) {
            if (btn.mouseClicked(mouseX, mouseY, button)) {
                return true;
            }
        }

        return false;
    }

    public void enter() {
        this.alphaAnimation.reset(this.alpha.get(), 1);
        this.parent.getAnimator().play(this.alphaAnimation);
    }

    public void hide() {
        this.alphaAnimation.reset(this.alpha.get(), 0);
        this.parent.getAnimator().play(this.alphaAnimation);
    }

    /*@Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        AstrolabeButton selected = this.widgets.get((int) this.selected);
        switch (keyCode) {
            case GLFW.GLFW_KEY_TAB:
                this.selected += 0.5f;
                this.selected %= this.widgets.size();
                break;
            case GLFW.GLFW_KEY_UP:
                selected.setY(selected.getY() - 1);
                break;
            case GLFW.GLFW_KEY_DOWN:
                selected.setY(selected.getY() + 1);
                break;
            case GLFW.GLFW_KEY_LEFT:
                selected.setX(selected.getX() - 1);
                break;
            case GLFW.GLFW_KEY_RIGHT:
                selected.setX(selected.getX() + 1);
                break;
        }

        return false;
    }*/

    @Override
    protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {

    }

    @Override
    public void resize(@NotNull Minecraft minecraft, int width, int height) {
        this.width = width;
        this.height = height;
    }

    public float getRotate() {
        return this.rotate;
    }
}
