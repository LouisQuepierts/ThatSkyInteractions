package net.quepierts.thatskyinteractions.client.gui.holder;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public interface NumberHolder {
    double get();

    void set(double v);
}
