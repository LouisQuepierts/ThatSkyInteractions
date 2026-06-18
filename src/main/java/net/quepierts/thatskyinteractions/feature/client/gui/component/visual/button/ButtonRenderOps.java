package net.quepierts.thatskyinteractions.feature.client.gui.component.visual.button;

import dev.anvilcraft.lib.v2.rendering.sdf.SdfGraphics;
import lombok.experimental.UtilityClass;
import net.quepierts.thatskyinteractions.feature.client.gui.component.visual.RenderOp;

@UtilityClass
public class ButtonRenderOps {

    public static final RenderOp BASE = (graphics, colors, x, y, width, height) -> {
        SdfGraphics.getInstance()
                .reset()
                .color(colors.argb())
                .center(true)
                .draw(graphics.original(), ButtonSdfParameters.PARAM_BASE32, 0, 0);
    };

    public static final RenderOp HOVER = (graphics, colors, x, y, width, height) -> {
        SdfGraphics.getInstance()
                .reset()
                .color(colors.argb())
                .center(true)
                .draw(graphics.original(), ButtonSdfParameters.PARAM_HOVER32, 0, 0);
    };

}
