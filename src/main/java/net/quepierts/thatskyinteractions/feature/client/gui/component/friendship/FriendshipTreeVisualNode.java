package net.quepierts.thatskyinteractions.feature.client.gui.component.friendship;

import dev.anvilcraft.lib.v2.rendering.sdf.SdfGraphics;
import lombok.RequiredArgsConstructor;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;
import net.quepierts.thatskyinteractions.feature.client.gui.component.control.Control;
import net.quepierts.thatskyinteractions.feature.client.gui.component.visual.VisualNode;
import org.jspecify.annotations.NonNull;

@RequiredArgsConstructor
public class FriendshipTreeVisualNode implements VisualNode {

    private final Identifier icon;

    @Override
    public void extractRenderState(
            final @NonNull Control control,
            final @NonNull GuiGraphicsExtractor graphics,
            final int mouseX,
            final int mouseY,
            final float delta
    ) {
        final var x = control.getX() + 16;
        final var y = control.getY() + 16;

        SdfGraphics.getInstance()
                .reset()
                .light(20.0f)
                .color(0xbbfffee0)
                .center(true)
                .circle(x, y, 0.01f)
                .draw(graphics);

        if (control.isMouseOver(mouseX, mouseY)) {
            SdfGraphics.getInstance()
                    .reset()
                    .color(0xfffffee0)
                    .center(true)
                    .round(5.0f)
                    .box(
                            x, y,
                            control.getWidth() - 2,
                            control.getHeight() - 2
                    )
                    .stroke(1.0f)
                    .light(2.5f)
                    .draw(graphics);
        }

        graphics.blit(
                RenderPipelines.GUI_TEXTURED,
                icon,
                control.getX(),
                control.getY(),
                0,
                0,
                control.getWidth(),
                control.getHeight(),
                32,
                32,
                0xFFFFFFFF
        );
    }
}
