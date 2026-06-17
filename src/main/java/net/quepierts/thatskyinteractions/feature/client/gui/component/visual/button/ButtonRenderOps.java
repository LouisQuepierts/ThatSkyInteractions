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
                .round(4.5f)
                .box(
                        0, 0,
                        width - 3,
                        height - 3
                )
                .stroke(0.5f)
                .light(3.2f)
                .draw(graphics.original());
    };

}
