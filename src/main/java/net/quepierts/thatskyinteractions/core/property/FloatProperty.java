package net.quepierts.thatskyinteractions.core.property;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import net.quepierts.animata4j.core.adapter.Consumer1f;

@NoArgsConstructor
@AllArgsConstructor
public class FloatProperty implements Consumer1f {
    private float value;

    public static FloatProperty zero() {
        return new FloatProperty(0);
    }

    public static FloatProperty one() {
        return new FloatProperty(1);
    }

    public float get() {
        return value;
    }

    public void set(float value) {
        this.value = value;
    }

    @Override
    public void accept(float value) {
        this.value = value;
    }
}
