package net.quepierts.thatskyinteractions.feature.client.gui.component.visual.button;

import dev.anvilcraft.lib.v2.rendering.sdf.SdfGraphics;
import dev.anvilcraft.lib.v2.rendering.sdf.SdfParameters;
import lombok.experimental.UtilityClass;

@UtilityClass
public class ButtonSdfParameters {

    public static final SdfParameters PARAM_BASE32
            = SdfGraphics.getInstance()
            .reset()
            .round(6.0f)
            .box(0, 0, 32, 32)
            .fill()
            .share();

    public static final SdfParameters PARAM_HOVER32
            = SdfGraphics.getInstance()
            .reset()
            .round(4.5f)
            .box(0, 0, 29, 29)
            .stroke(0.5f)
            .light(3.2f)
            .share();

}
