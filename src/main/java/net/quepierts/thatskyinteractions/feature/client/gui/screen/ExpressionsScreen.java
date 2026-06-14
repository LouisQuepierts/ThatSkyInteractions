package net.quepierts.thatskyinteractions.feature.client.gui.screen;

import dev.anvilcraft.lib.v2.rendering.sdf.SdfGraphics;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.quepierts.thatskyinteractions.core.model.ui.Alignment;
import net.quepierts.thatskyinteractions.feature.client.gui.ColorStack;
import net.quepierts.thatskyinteractions.feature.client.gui.component.control.Button;
import net.quepierts.thatskyinteractions.feature.client.gui.component.control.Control;
import net.quepierts.thatskyinteractions.feature.client.gui.component.layout.GridPane;
import net.quepierts.thatskyinteractions.feature.client.gui.component.layout.VBox;
import net.quepierts.thatskyinteractions.feature.client.gui.component.sky.TsiButton;
import net.quepierts.thatskyinteractions.feature.client.gui.component.sky.expression.ExpressionButton;
import net.quepierts.thatskyinteractions.feature.client.gui.component.visual.GeneralVisualNodes;
import net.quepierts.thatskyinteractions.feature.client.gui.component.visual.HoverNode;
import net.quepierts.thatskyinteractions.feature.client.gui.component.visual.VisualNode;
import net.quepierts.thatskyinteractions.feature.client.gui.component.visual.button.ButtonRenderOps;
import net.quepierts.thatskyinteractions.feature.client.gui.component.visual.button.SpinButtonNode;
import net.quepierts.thatskyinteractions.feature.client.gui.controller.ExpressionScreenController;
import net.quepierts.thatskyinteractions.feature.expression.ExpressionSet;
import net.quepierts.thatskyinteractions.feature.expression.PlayerExpressionManager;
import net.quepierts.thatskyinteractions.infra.animation.tween.TweenScope;
import org.jspecify.annotations.NonNull;

import java.util.function.IntSupplier;

public final class ExpressionsScreen extends SlideScreen<Void, ExpressionScreenController> {

    public ExpressionsScreen() {
        super(Component.translatable("menu.thatskyinteractions.expressions"), null);
    }

    @Override
    protected ExpressionScreenController createController(final Void unused) {
        return new ExpressionScreenController();
    }

    @Override
    protected Control createView() {

        var controller  = this.getController();
        var tween       = this.tween();
        var vBox        = new VBox(
                tween,
                0, 0,
                this.getSliderWide().get(),
                this.height,
                Component.empty()
        );

        vBox.setAlignment(Alignment.TOP_CENTER);
        final var padding = vBox.getPadding();
        padding.top = 16;

        var grid = new GridPane(
                tween,
                0, 0,
                0, 0,
                Component.empty()
        );
        grid.setAlignment(Alignment.TOP_CENTER);
        grid.setHgap(4);
        grid.setVgap(4);

        final var manager       = PlayerExpressionManager.getInstance();
        final var expressions   = manager.ordinal();
        final var maxColumn     = 4;
        var row                 = 0;
        var column              = 0;
        var i                   = 0;

        for (final var identifier : expressions) {

            final var id    = i;
            final var set   = manager.getSet(identifier);

            if (set == null) { // normally, this should never happen
                continue;
            }

            final var button = new ExpressionButton(
                    tween,
                    Component.empty(),
                    set.levels()
            );

            button.getActivationTrigger().set(Button.ActivationTrigger.RELEASED);
            button.setVisualNode(vButton(set));
            button.setOnClick(() -> {
                Minecraft.getInstance()
                        .getSoundManager()
                        .play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0f));
                controller.onButtonClicked(id, button.getSelected());
            });

            // calculate row and column
            if (column == maxColumn) {
                row++;
                column = 0;
            }

            grid.add(
                    button,
                    column,
                    row
            );

            column++;
            i++;
        }

        grid.fit();

        vBox.addChild(grid);
        vBox.layout();

        return vBox;
    }

    private static VisualNode vButton(
            final @NonNull ExpressionSet    set
    ) {
        final var icon          = set.icon();
        final var background    = GeneralVisualNodes.base(0x80000000, 6.0f);
        final var content       = SpinButtonNode.of((graphics, colors, _, _, width, height) -> {
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
                    colors.argb()
            );
        });
        final var select    = set.leveled() ? new VisualNode() {
            @Override
            public void extractRenderState(
                    final @NonNull Control control,
                    final @NonNull GuiGraphicsExtractor graphics,
                    final @NonNull ColorStack colors,
                    final @NonNull TweenScope tween,
                    final int mouseX, final int mouseY, final float delta
            ) {


                final var levels    = control.getAttribute(ExpressionButton.ATTRIBUTE_LEVELS).getAsInt();
                final var selected  = control.getAttribute(ExpressionButton.ATTRIBUTE_SELECTED).getAsInt() - 1;
                final var hovered   = control.getAttribute(Button.ATTRIBUTE_PRESS).isTarget();

                final var width     = levels * 4 - 2;
                final var left      = (control.getWidth() - width) / 2f;
                graphics.pose().translate(control.getX() + left, control.getY() + 4);

                for (var i = 0; i < levels; i++) {
                    graphics.fill(
                             i * 4,
                            0,
                            i * 4 + 2,
                            2,
                            hovered && i == selected
                                    ? colors.argb(0xff, 0xff, 0xfe, 0xe0)
                                    : colors.argb(0xff, 0x80, 0x80, 0x80)
                    );
                }
            }
        } : VisualNode.EMPTY;
        final var hover     = HoverNode.of(ButtonRenderOps.HOVER);
        return VisualNode.combine(background, content, select, hover);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
