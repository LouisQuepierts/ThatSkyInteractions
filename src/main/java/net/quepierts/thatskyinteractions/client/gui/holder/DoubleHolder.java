package net.quepierts.thatskyinteractions.client.gui.holder;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class DoubleHolder implements NumberHolder {
    private double value;

    public DoubleHolder(double value) {
        this.value = value;
    }

    @Override
    public double get() {
        return this.value;
    }

    @Override
    public void set(double v) {
        this.value = v;
    }

    public double getValue() {
        return value;
    }
}
