package net.quepierts.thatskyinteractions.feature.client.gui.component.friendship;

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

import java.util.ArrayList;
import java.util.List;

public final class FriendshipTreeLayout extends Pane {

    public static final int VERTICAL_GAP_FULL       = 64;
    public static final int VERTICAL_GAP_SIMPLE     = 32;
    public static final int HORIZONTAL_GAP          = 32;
    public static final int NODE_SIZE               = 32;

    private static final int[] DEGREES = new int[] { -45, 0, 45 };

    private final List<FriendshipTreeLine>          lines;
    private final List<Control>                     buttons;
    private final FriendshipTree                    tree;

    private int                                   contentHeight;


    private final int leftX;
    private final int centerX;
    private final int rightX;

    private final int[] branchX;

    public FriendshipTreeLayout(
            final FriendshipTree tree,
            final int x,
            final int y,
            final int width,
            final float screenHeight
    ) {
        super(
                x, y,
                width, 0,
                Component.translatable("gui.thatskyinteractions.friendship.tree")
        );

        this.lines      = new ArrayList<>();
        this.buttons    = new ArrayList<>();
        this.tree       = tree;

        final var padding = this.getPadding();
        padding.top     = 16;
        padding.bottom  = 16;

        for (final var node : tree) {

            if (node.getParent() != -1) {
                final var branch = node.getBranch();
                final var parent = tree.get(node.getParent());
                final var line = new FriendshipTreeLine(
                        branch == parent.getBranch() ? DEGREES[1] : DEGREES[branch.ordinal()]
                );
                this.lines.add(line);
                super.addChild(line);
            }

            final var button = new Button(
                    0, 0,
                    NODE_SIZE, NODE_SIZE,
                    Component.empty()
            );

            final var icon  = this.extractIcon(node);
            button.setVisualNodes(new FriendshipTreeVisualNode(icon));

            this.buttons.add(button);
        }

        this.getChildren().addAll(this.buttons);

        int center = width / 2 - NODE_SIZE / 2;
        this.leftX = center - NODE_SIZE - HORIZONTAL_GAP;
        this.centerX = center;
        this.rightX = center + NODE_SIZE + HORIZONTAL_GAP;

        this.branchX = new int[] {
                this.leftX,
                this.centerX,
                this.rightX
        };

        this.layout();
    }

    @Override
    public void fit() {

    }

    @Override
    protected void extractWidgetRenderState(final @NonNull GuiGraphicsExtractor graphics, final int mouseX, final int mouseY, final float delta) {
        final var pose = graphics.pose();
//        pose.pushMatrix().translate(this.getX(), this.getY());
        super.extractWidgetRenderState(graphics, mouseX, mouseY, delta);
//        pose.popMatrix();
    }

    @Override
    public void layout() {

        this.calculatePositions();

        this.height = this.updateContentHeight();

    }

    private void calculatePositions() {
        final var root = this.buttons.getFirst();

        root.setPosition(
                this.getColumnX(this.tree.getRoot().getBranch()),
                this.getContentBottom()
        );

        for (int i = 1; i < this.tree.size(); i++) {
            final var node      = this.tree.get(i);
            final var parent    = this.tree.get(node.getParent());

            final var tControl  = this.buttons.get(i);
            final var pControl  = this.buttons.get(node.getParent());
            final var branch    = node.getBranch();

            final var offset    = (branch == FriendshipTreeNode.Branch.MIDDLE &&
                    (parent.hasLeft() || parent.hasRight())) ?
                    NODE_SIZE + VERTICAL_GAP_FULL :
                    NODE_SIZE + VERTICAL_GAP_SIMPLE;

            tControl.setPosition(
                    this.getColumnX(node.getBranch()),
                    pControl.getY() - offset
            );

            final var line      = this.lines.get(i - 1);
            final var lineSize  = offset / 2;

            if (branch == parent.getBranch()) {
                line.setPosition(
                        tControl.getX() + 6,
                        pControl.getY() - lineSize - 18
                );
                line.setHeight(lineSize);
            } else {
                line.setPosition(
                        (tControl.getX() + pControl.getX() + 12) / 2,
                        pControl.getY() - lineSize - 12
                );
                line.setHeight(lineSize);
            }
        }
    }

    private int calculateNodeYOffset(final FriendshipTreeNode node) {

        final int parentLocation    = node.getParent();
        final var parentNode        = this.tree.get(parentLocation);
        final var branch            = node.getBranch();

        if (branch == FriendshipTreeNode.Branch.MIDDLE && (parentNode.hasLeft() || parentNode.hasRight())) {
            return NODE_SIZE - VERTICAL_GAP_FULL;
        } else {
            return NODE_SIZE - VERTICAL_GAP_SIMPLE;
        }
    }

    private int calculateNodeY(final FriendshipTreeNode node) {
        int parentLocation          = node.getParent();
        final var parentNode        = this.tree.get(parentLocation);
        final var parentControl     = this.buttons.get(parentLocation);

        final var branch            = node.getBranch();

        if (branch == FriendshipTreeNode.Branch.MIDDLE && (parentNode.hasLeft() || parentNode.hasRight())) {
            return parentControl.getY() - NODE_SIZE - VERTICAL_GAP_FULL;
        } else {
            return parentControl.getY() - NODE_SIZE - VERTICAL_GAP_SIMPLE;
        }
    }

    public int updateContentHeight() {
        if (this.buttons.isEmpty()) {
            this.contentHeight = 0;
            return 0;
        }

        int minY = Integer.MAX_VALUE;
        int maxY = Integer.MIN_VALUE;

        for (final var control : this.buttons) {
            minY = Math.min(minY, control.getY());
            maxY = Math.max(maxY, control.getY() + control.getHeight());
        }

        final var padding = this.getPadding();
        this.contentHeight = (int) (maxY - minY + padding.getTop() + padding.getBottom());

//        normalizePositions(minY);

        return this.contentHeight;
    }

    @Override
    public void addChild(final Control child) {
        throw new UnsupportedOperationException("Cannot add child to FriendshipTreeLayout");
    }

    private void normalizePositions(int minY) {
        final var padding = this.getPadding();
        if (minY >= padding.getTop()) return;

        int offset = (int) (padding.getTop() - minY);

        for (final var control : this.buttons) {
            control.setY(control.getY() + offset);
        }
    }

    private int getColumnX(final FriendshipTreeNode.Branch branch) {
        return this.branchX[branch.ordinal()];
    }

    private int getContentBottom() {
        return (int) (this.getY() + this.getHeight() - this.getPadding().getBottom() - NODE_SIZE);
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
