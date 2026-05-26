package net.quepierts.thatskyinteractions.feature.client.gui.component.friendship;

import it.unimi.dsi.fastutil.ints.IntArrayFIFOQueue;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.quepierts.thatskyinteractions.ThatSkyInteractions;
import net.quepierts.thatskyinteractions.feature.client.gui.component.control.Button;
import net.quepierts.thatskyinteractions.feature.client.gui.component.control.Control;
import net.quepierts.thatskyinteractions.feature.client.gui.component.layout.Pane;
import net.quepierts.thatskyinteractions.feature.data.friendship.FriendshipTree;
import net.quepierts.thatskyinteractions.feature.data.friendship.FriendshipTreeNode;
import org.jspecify.annotations.NonNull;

import java.util.HashMap;
import java.util.Map;

public final class FriendshipTreeLayout extends Pane {

    public static final int NODE_SIZE               = 32;
    public static final int SPACING                 = 10;

    private final Map<FriendshipTreeNode, Control>  buttons;
    private final FriendshipTree                    tree;

    private final float                             screenHeight;

    public FriendshipTreeLayout(
            final FriendshipTree tree,
            final int x,
            final int y,
            final float screenHeight
    ) {
        super(
                x, y,
                NODE_SIZE * 5 + SPACING * 4, 0,
                Component.translatable("gui.thatskyinteractions.friendship.tree")
        );
        this.screenHeight = screenHeight;

        this.buttons    = new HashMap<>();
        this.tree       = tree;

        for (final var node : tree) {
            final var button = new Button(
                    0, 0,
                    NODE_SIZE, NODE_SIZE,
                    Component.empty()
            );

            final var icon  = this.extractIcon(node);
            button.setVisualNodes(new FriendshipTreeVisualNode(icon));

            this.buttons.put(node, button);
            this.addChild(button);
        }

        this.layout();
    }

    @Override
    public void fit() {



    }

    @Override
    protected void extractWidgetRenderState(final @NonNull GuiGraphicsExtractor graphics, final int mouseX, final int mouseY, final float delta) {
        final var pose = graphics.pose();
        pose.pushMatrix().translate(0, this.screenHeight);
        super.extractWidgetRenderState(graphics, mouseX, mouseY, delta);
        pose.popMatrix();
    }

    @Override
    public void layout() {
        final var queue     = new IntArrayFIFOQueue(this.tree.size());
        var height          = -64;
        queue               .enqueue(0);

        final int left      = 80 - 16;
        final int[] xs      = {
                left - 54,
                left,
                left + 54
        };

        while (!queue.isEmpty()) {
            final var location  = queue.dequeueInt();
            final var node      = this.tree.get(location);
            final var branch    = node.getBranch();

            if (node.hasLeft()) {
                queue.enqueue(node.getLeft());
            }

            if (node.hasMiddle()) {
                queue.enqueue(node.getMiddle());
            }

            if (node.hasRight()) {
                queue.enqueue(node.getRight());
            }

            final var x         = xs[branch.ordinal()];

            final var control   = this.buttons.get(node);
            control.setX(x);

            final var parent = node.getParent();
            if (parent == -1) {
                control.setY(-64);
            } else {
                final var parentNode    = this.tree.get(parent);
                final var parentControl = this.buttons.get(parentNode);

                if (branch == parentNode.getBranch() && (parentNode.hasLeft() || parentNode.hasRight())) {
                    control.setY(parentControl.getY() - 64);
                } else {
                    control.setY(parentControl.getY() - 48);
                }
            }

            height          = Math.min(height, control.getY() - NODE_SIZE - 16);
        }

        this.height             = -height;
    }

    private Identifier extractIcon(@NonNull final FriendshipTreeNode node) {
        final var type = node.getType();
        if ("interaction".equals(type)) {
            final var metadata      = node.getMetadata();
            final var interaction   = metadata.get("interaction");

            final var raw           = Identifier.parse(interaction);
            return Identifier.fromNamespaceAndPath(
                    raw.getNamespace(),
                    "textures/icon/interaction/" + raw.getPath() + ".png"
            );
        }
        return ThatSkyInteractions.location("textures/gui/" + type + ".png");
    }
}
