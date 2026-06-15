package net.quepierts.thatskyinteractions.feature.client.gui.component.visual.button;

import dev.anvilcraft.lib.v2.rendering.sdf.SdfGraphics;
import lombok.experimental.UtilityClass;
import net.quepierts.thatskyinteractions.feature.client.gui.component.visual.RenderOp;

@UtilityClass
public class ButtonRenderOps {

    public static final RenderOp HOVER = (graphics, colors, x, y, width, height) -> {
        SdfGraphics.getInstance()
                .reset()
                .color(colors.argb())
                .center(true)
                .round(5.0f)
                .box(
                        x, y,
                        width - 2,
                        height - 2
                )
                .stroke(0.5f)
                .light(2.5f)
                .draw(graphics.original());
    };

}
