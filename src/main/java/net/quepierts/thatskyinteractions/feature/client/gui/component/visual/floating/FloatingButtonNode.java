package net.quepierts.thatskyinteractions.feature.client.gui.component.visual.floating;

import dev.anvilcraft.lib.v2.rendering.sdf.SdfGraphics;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.quepierts.thatskyinteractions.ThatSkyInteractions;
import net.quepierts.thatskyinteractions.feature.client.gui.ColorStack;
import net.quepierts.thatskyinteractions.feature.client.gui.component.control.Control;
import net.quepierts.thatskyinteractions.feature.client.gui.component.floating.FloatingButton;
import net.quepierts.thatskyinteractions.feature.client.gui.component.visual.RenderOp;
import net.quepierts.thatskyinteractions.feature.client.gui.component.visual.VisualNode;
import net.quepierts.thatskyinteractions.infra.animation.tween.TweenScope;
import org.jspecify.annotations.NonNull;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class FloatingButtonNode implements VisualNode {

    public static final Identifier ICON                 = ThatSkyInteractions.location("textures/gui/floating.png");

    private final RenderOp renderOp;

    public static FloatingButtonNode texture(
            @NonNull final Identifier identifier
    ) {
        return new FloatingButtonNode(
                (graphics, colors, _, _, _, _) -> {
                    graphics.blit(
                            RenderPipelines.GUI_TEXTURED,
                            identifier,
                            -14, -14,
                            0, 0,
                            28, 28,
                            32, 32,
                            32, 32,
                            colors.argb()
                    );
                }
        );
    }

    public static FloatingButtonNode sprite(
            @NonNull final Identifier identifier
    ) {
        return new FloatingButtonNode(
                (graphics, colors, _, _, _, _) -> {
                    graphics.blitSprite(
                            RenderPipelines.GUI_TEXTURED,
                            identifier,
                            32, 32,
                            -14, -28,
                            0, 0,
                            32, 32,
                            colors.argb()
                    );
                }
        );
    }

    @Override
    public void extractRenderState(
            @NonNull final Control              control,
            @NonNull final GuiGraphicsExtractor graphics,
            @NonNull final ColorStack           colors,
            @NonNull final TweenScope           tween,

            final int                           mouseX,
            final int                           mouseY,
            final float                         delta
    ) {
        
        final var activeTransition  = control.getAttribute(FloatingButton.ATTRIBUTE_ACTIVE_TRANSITION);
        final var active            = activeTransition.getValue();

        if (active == 0.0f) {
            return;
        }

        final var transiting        = active < 1.0f;

        final var pose              = graphics.pose();
        pose                        .translate(control.x(), control.y());

        if (transiting) {
            pose                    .scale(0.8f + active * 0.2f);
        }

        colors                      .push();
        colors                      .mul(active, 1.0f, 1.0f, 1.0f);

        final var focusTransition   = control.getAttribute(FloatingButton.ATTRIBUTE_FOCUS_TRANSITION);
        final var ft                = focusTransition.getValue();
        if (ft > 0.0f) {
            pose                    .pushMatrix();
            pose                    .translate(0, -14);

            final var click         = control.getAttribute(FloatingButton.ATTRIBUTE_CLICK_TRANSITION);
            final var t             = Mth.abs(Mth.cos(2 * click.getValue() * Mth.PI));
            pose                    .scale(t, 1.0f);

            colors                  .push();
            colors                  .mul(ft, 1.0f, 1.0f, 1.0f);

            SdfGraphics             .getInstance()
                                    .reset()
                                    .center(true)

                                    .circle(0, 0, 16)
                                    .color(colors.argb(0x80, 0x00, 0x00, 0x00))
                                    .fill()
                                    .draw(graphics)

                                    .circle(0, 0, 15)
                                    .stroke(1.0f)
                                    .color(colors.argb(0xff, 0xa0, 0xa0, 0x80))
                                    .draw(graphics);

            this.renderOp           .render(
                                            graphics,
                                            colors, 0, 0,
                                            0, 0
                                    );

            colors                  .pop();
            pose                    .popMatrix();

        }

        pose                        .rotate(Mth.HALF_PI * 0.5f);

        graphics                    .blit(
                                            RenderPipelines.GUI_TEXTURED,
                                            ICON,
                                            -4, -4,
                                            0, 0,
                                            8, 8,
                                            8, 8,
                                            8, 8,
                                            colors.argb()
                                    );

        colors                      .pop();
    }

}
