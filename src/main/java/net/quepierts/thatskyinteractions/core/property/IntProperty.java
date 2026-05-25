package net.quepierts.thatskyinteractions.core.property;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import net.quepierts.thatskyinteractions.infra.animation.core.adapter.Consumer1f;

@NoArgsConstructor
@AllArgsConstructor
public class IntProperty implements Consumer1f {

    private int value;

    public static IntProperty zero() {
        return new IntProperty(0);
    }

    public static IntProperty one() {
        return new IntProperty(1);
    }

    public int get() {
        return this.value;
    }

    public void set(final int value) {
        this.value = value;
    }

    @Override
    public void accept(final float value) {
        this.value = (int) value;
    }
}
