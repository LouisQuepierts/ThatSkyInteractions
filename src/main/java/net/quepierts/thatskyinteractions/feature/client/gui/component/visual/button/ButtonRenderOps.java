package net.quepierts.thatskyinteractions.feature.client.gui.component.visual.button;

import dev.anvilcraft.lib.v2.rendering.sdf.SdfGraphics;
import lombok.NonNull;
import lombok.experimental.UtilityClass;
import net.minecraft.resources.Identifier;
import net.quepierts.thatskyinteractions.feature.client.gui.component.visual.RenderOp;

@UtilityClass
public class ButtonRenderOps {

    public static final RenderOp BASE = (graphics, x, y, width, height, color) -> {
        SdfGraphics.getInstance()
                .reset()
                .center(true)
                .round(6.0f)
                .color(0x80101010)
                .box(
                        x, y,
                        width,
                        height
                )
                .draw(graphics);
    };

    public static final RenderOp HOVER = (graphics, x, y, width, height, color) -> {
        SdfGraphics.getInstance()
                .reset()
                .color(color)
                .center(true)
                .round(5.0f)
                .box(
                        x, y,
                        width - 2,
                        height - 2
                )
                .stroke(1.0f)
                .light(2.5f)
                .draw(graphics);
    };

}
