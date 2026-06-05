package net.quepierts.thatskyinteractions.feature.client.gui.component.friendship;

import com.google.common.collect.ImmutableList;
import dev.anvilcraft.lib.v2.rendering.sdf.SdfGraphics;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.UtilityClass;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.quepierts.thatskyinteractions.ThatSkyInteractions;
import net.quepierts.thatskyinteractions.core.friendship.model.Cost;
import net.quepierts.thatskyinteractions.core.friendship.model.NodeState;
import net.quepierts.thatskyinteractions.core.property.FloatProperty;
import net.quepierts.thatskyinteractions.feature.client.ClientPlayerFriendshipSystem;
import net.quepierts.thatskyinteractions.feature.client.gui.component.control.Button;
import net.quepierts.thatskyinteractions.feature.client.gui.component.control.Control;
import net.quepierts.thatskyinteractions.feature.client.gui.component.visual.HoverNode;
import net.quepierts.thatskyinteractions.feature.client.gui.component.visual.VisualNode;
import net.quepierts.thatskyinteractions.feature.client.gui.component.visual.button.ButtonRenderOps;
import net.quepierts.thatskyinteractions.feature.client.gui.component.visual.button.SpinButtonNode;
import net.quepierts.thatskyinteractions.feature.client.gui.controller.FriendshipScreenController;
import net.quepierts.thatskyinteractions.core.friendship.model.FriendshipTreeNode;
import net.quepierts.thatskyinteractions.feature.friendship.behaviour.FriendshipBehaviour;
import net.quepierts.thatskyinteractions.feature.friendship.behaviour.FriendshipBehaviourFactory;
import net.quepierts.thatskyinteractions.infra.animation.tween.Tween;
import net.quepierts.thatskyinteractions.infra.animation.tween.TweenScope;
import net.quepierts.thatskyinteractions.infra.animation.tween.ease.Eases;
import net.quepierts.thatskyinteractions.infra.animation.tween.interpolate.Interpolators;
import org.joml.Vector2f;
import org.joml.Vector2fc;
import org.jspecify.annotations.NonNull;

import java.util.ArrayList;
import java.util.Collection;

@UtilityClass
public class FriendshipTreeComponentFactory {

    public static final Identifier ICON_LOCKED      = ThatSkyInteractions.location("textures/gui/locked.png");

    public static final int COLOR_LOCKED            = 0xff52677a;
    public static final int COLOR_UNLOCKABLE        = 0xffc8f9fd;
    public static final int COLOR_UNLOCKED          = 0xfffffee0;

    public static final int VERTICAL_GAP_FULL       = 48;
    public static final int VERTICAL_GAP_SIMPLE     = 24;
    public static final int HORIZONTAL_GAP          = 24;
    public static final int NODE_SIZE               = 32;

    private static final Vector2fc[] DIRECTIONS
            = new Vector2fc[] {
                    new Vector2f(-0.8f, -0.8f),
                    new Vector2f(0, -1),
                    new Vector2f(0.8f, -0.8f),
            };

    private static final int[] BRANCH_X
            = new int[] {
                    - NODE_SIZE - HORIZONTAL_GAP,
                    0,
                    + NODE_SIZE + HORIZONTAL_GAP
            };

    private static final int[] STATED_COLORS
            = new int[] {
                    COLOR_LOCKED,
                    COLOR_UNLOCKABLE,
                    COLOR_UNLOCKED
            };

    public static FriendshipTreeComponents create(
            final @NonNull TweenScope                   tween,
            final @NonNull FriendshipScreenController   controller
    ) {

        final var model             = controller.getModel();
        final var structure         = model.getStructure();

        final var lines             = new ArrayList<Control>();
        final var buttons           = new ArrayList<Button>();

        int i                       = 0;
        for (final var node : structure) {

            final var button        = new Button(
                                        tween,
                                        0, 0,
                                        NODE_SIZE, NODE_SIZE,
                                        Component.empty()
                                    );

            final var index         = i;
            final var state         = model.getState(index);

            button                  .setVisualNode(vButton(node, state));
            button                  .setOnClick(() -> controller.onButtonClicked(index));
            buttons                 .add(button);

            if (node.getParent() != -1) {
                final var parent    = structure.get(node.getParent());
                final var previous  = buttons.get(node.getParent());
                final var branch    = node.getBranch();

                final var same      = branch == parent.getBranch();
                final var yOffset   = (same &&
                                    (parent.hasLeft() || parent.hasRight())) ?
                                    NODE_SIZE + VERTICAL_GAP_FULL :
                                    NODE_SIZE + VERTICAL_GAP_SIMPLE;

                final var xOffset   = BRANCH_X[branch.ordinal()];
                button              .setPosition(
                                        xOffset,
                                        previous.getY() - yOffset
                                    );

                final var lineSize  = same ? (yOffset - 40) : 30;
                final var line      = new Control(
                                        tween,
                                        previous.getX() + 16,
                                        previous.getY() + 16,
                                        2,
                                        lineSize,
                                        Component.empty()
                                        );

                final var direction = same ?
                                    DIRECTIONS[1] :
                                    DIRECTIONS[branch.ordinal()];

                line                .setVisualNode(vLine(direction, state));
                lines               .add(line);
            }


            i ++;
        }

        final int height            = calculateHeight(buttons);

        return new FriendshipTreeComponents(
                ImmutableList.copyOf(lines),
                ImmutableList.copyOf(buttons),
                height
        );
    }

    private static int calculateHeight(
            @NonNull final Collection<Button> buttons
    ) {
        if (buttons.isEmpty()) {
            return 0;
        }

        int minY = Integer.MAX_VALUE;
        int maxY = Integer.MIN_VALUE;

        for (final var control : buttons) {
            minY = Math.min(minY, control.getY());
            maxY = Math.max(maxY, control.getY() + control.getHeight());
        }

        return maxY - minY;
    }

    private static Identifier extractIcon(
            final FriendshipTreeNode       node,
            final NodeState                 state
    ) {

        if (state == NodeState.LOCKED) {
            return ICON_LOCKED;
        }

        final var attachment    = ClientPlayerFriendshipSystem.getLocalFriendshipData();
        final var behaviour     = FriendshipBehaviourFactory.get(node);
        
        return behaviour == null ?
                FriendshipBehaviour.DEFAULT_ICON :
                behaviour.getIcon(
                    attachment,
                    node,
                    state
                );
    }

    public static VisualNode vButton(
            final FriendshipTreeNode    node,
            final NodeState             state
    ) {

        final var icon          = extractIcon(node, state);
        final var mColor        = STATED_COLORS[state.ordinal()];

        final var content       = SpinButtonNode.of((graphics, _, _, width, height, _) -> {
            graphics.blit(
                    RenderPipelines.GUI_TEXTURED,
                    icon,
                    -14,
                    -14,
                    0,
                    0,
                    (int) width - 4,
                    (int) height - 4,
                    32,
                    32,
                    32,
                    32,
                    mColor
            );
        });

        final var hover     = HoverNode.of(ButtonRenderOps.HOVER);
        final var price     = state == NodeState.UNLOCKABLE ?
                            Price.of(node.getCost()) :
                            VisualNode.EMPTY;

        return VisualNode.combine(content, hover, price);
    }

    public static VisualNode vLine(
            final @NonNull Vector2fc        direction,
            final NodeState                 state
    ) {
        final var color = STATED_COLORS[state.ordinal()];
        return new Line(direction, color);
    }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    private static final class Line implements VisualNode {

        private final FloatProperty progress    = new FloatProperty(0.0f);
        private final Vector2fc     direction;
        private final int           color;

        @Override
        public void extractRenderState(
                final @NonNull Control              control,
                final @NonNull GuiGraphicsExtractor graphics,
                final @NonNull TweenScope           tween,

                final int                           mouseX,
                final int                           mouseY,
                final float                         delta
        ) {

            final var pose      = graphics.pose();
            final var py        = control.getY() + pose.m21;

            final var progress  = this.progress.get();
            final var empty     = progress == 0.0f;
            if (empty) {
                if (py > 64.0f) {
                    this.progress.set(0.0f);
                    Tween.to(
                            this.progress,
                            0.0f,
                            1.0f,
                            1.0f,
                            Interpolators.FLOAT,
                            Eases.QUAD_OUT
                    );
                }
                return;
            }

            final var factor    = control.getHeight() * progress;
            final var px0       = control.getX() + direction.x() * 20.0f;
            final var py0       = control.getY() + direction.y() * 20.0f;
            final var px1       = px0 + direction.x() * factor;
            final var py1       = py0 + direction.y() * factor;

            SdfGraphics.getInstance()
                    .reset()

                    .round(0.5f)
                    .color(this.color)

                    .light(5.0f)
                    .segment(
                            px0, py0,
                            px1, py1
                    )
                    .draw(graphics);

        }
    }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    private static final class Price implements VisualNode {

        private static final Identifier WHITE       = Identifier.withDefaultNamespace("textures/item/candle.png");
        private static final Identifier ASCENDED    = Identifier.withDefaultNamespace("textures/item/red_candle.png");

        public static VisualNode of(@NonNull final Cost cost) {
            if (cost.isFree()) {
                return VisualNode.EMPTY;
            }

            final var icon = switch (cost.currency()) {
                case WHITE_CANDLE -> WHITE;
                case ASCENDED_CANDLE -> ASCENDED;
            };

            return new Price(
                    icon,
                    Integer.toString(cost.amount())
            );
        }

        private final Font          font = Minecraft.getInstance().font;
        private final Identifier    icon;
        private final String        price;

        @Override
        public void extractRenderState(
                final @NonNull Control              control,
                final @NonNull GuiGraphicsExtractor graphics,
                final @NonNull TweenScope           tween,

                final int                           mouseX,
                final int                           mouseY,
                final float                         delta
        ) {

            graphics.text(
                    this.font,
                    this.price,
                    control.getX() + 32,
                    control.getY() + 32,
                    0xFFFFFFFF
            );
            graphics.blit(
                    RenderPipelines.GUI_TEXTURED,
                    this.icon,
                    control.getX() + 20,
                    control.getY() + 28,
                    0,
                    0,
                    12,
                    12,
                    14,
                    16,
                    16,
                    16,
                    0xFFFFFFFF
            );

        }

    }

}
