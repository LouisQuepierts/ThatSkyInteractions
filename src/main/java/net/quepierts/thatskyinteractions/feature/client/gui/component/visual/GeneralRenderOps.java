package net.quepierts.thatskyinteractions.feature.client.gui.component.visual;

import dev.anvilcraft.lib.v2.rendering.sdf.SdfGraphics;
import lombok.experimental.UtilityClass;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;
import org.jspecify.annotations.NonNull;

@UtilityClass
public class GeneralRenderOps {

    public static final RenderOp BASE = (graphics, x, y, width, height, color) -> {
        graphics.fill(
                (int) x,
                (int) y,
                (int) (x + width),
                (int) (y + height),
                color
        );
    };

    public static final RenderOp ROUND_BASE = (graphics, x, y, width, height, color) -> {
        SdfGraphics.getInstance()
                .reset()
                .round(6.0f)
                .color(color)
                .box(
                        x, y,
                        width,
                        height
                )
                .draw(graphics);
    };

    public static RenderOp texture(
            final @NonNull Identifier               texture,
            final          int                      textureWidth,
            final          int                      textureHeight
    ) {
        return (graphics, x, y, width, height, color) -> graphics.blit(
                RenderPipelines.GUI_TEXTURED,
                texture,
                (int) (x - width / 2),
                (int) (y - height / 2),
                0f, 0f,
                (int) width,
                (int) height,
                textureWidth,
                textureHeight,
                textureWidth,
                textureHeight,
                color
        );
    }

}
