package net.quepierts.thatskyinteractions.client.gui.component;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.apache.commons.lang3.math.NumberUtils;
import org.codehaus.plexus.util.StringUtils;

@OnlyIn(Dist.CLIENT)
public class ScrollableEditBox extends EditBox {
    private final float max;
    private final float min;
    private float floatValue;
    public ScrollableEditBox(Font font, Component message, int x, int y, int width, int height, float defaultValue, float min, float max) {
        super(font, x, y, width, height, message);
        this.min = min;
        this.max = max;
        this.floatValue = defaultValue;
        this.setResponder(this::response);
        this.setValue(String.format("%.2f", floatValue));
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        float last = this.floatValue;
        this.floatValue = Mth.clamp(last + (float) scrollY, min, max);

        if (last != this.floatValue) {
            this.setValue(String.format("%.4f", this.floatValue));
            return true;
        }
        return false;
    }

    private void response(String str) {
        if (!StringUtils.isNumeric(str)) {
            this.setValue(String.format("%.4f", this.floatValue));
        } else {
            this.floatValue = NumberUtils.toFloat(this.getValue());
        }
    }

    public float getFloatValue() {
        return this.floatValue;
    }

    public void setFloatValue(float f) {
        this.setValue(String.valueOf(f));
    }
}
