package net.quepierts.thatskyinteractions.core.property;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import net.quepierts.veynir.core.adapter.Consumer1f;
import org.jetbrains.annotations.Contract;

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

    @Contract(pure = true)
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
