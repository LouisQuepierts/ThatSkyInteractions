package net.quepierts.thatskyinteractions.client.gui.animate;

import net.minecraft.util.Mth;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.quepierts.thatskyinteractions.client.gui.holder.DoubleHolder;

@OnlyIn(Dist.CLIENT)
public class ShakeNumberAnimation extends AbstractScreenAnimation {
    private final DoubleHolder holder;
    private final double middle;
    private final double delta;
    private final float frequency;

    public ShakeNumberAnimation(DoubleHolder holder, double min, double max, float frequency, float length) {
        super(length);
        this.holder = holder;
        this.delta = max - min;
        this.middle = max - this.delta / 2;
        this.frequency = frequency;
    }

    @Override
    protected void run(float time) {
        float local = time / getLength();
        holder.set(this.middle + Mth.sin(Mth.clamp(local, 0, 1) * frequency * Mth.TWO_PI) * (local * local - 2 * local + 1) * this.delta);
    }
}
