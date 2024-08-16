package net.quepierts.thatskyinteractions.client.gui.holder;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class IntHolder implements NumberHolder {
    private int value;

    public IntHolder(int value) {
        this.value = value;
    }
    @Override
    public double get() {
        return value;
    }

    @Override
    public void set(double v) {
        this.value = (int) v;
    }

    public int getValue() {
        return value;
    }
}
