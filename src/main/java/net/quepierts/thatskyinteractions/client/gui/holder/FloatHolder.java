package net.quepierts.thatskyinteractions.client.gui.holder;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class FloatHolder implements NumberHolder {
    private float value;

    public FloatHolder(float value) {
        this.value = value;
    }

    @Override
    public double get() {
        return this.value;
    }

    @Override
    public void set(double v) {
        this.value = (float) v;
    }

    public float getValue() {
        return value;
    }
}
