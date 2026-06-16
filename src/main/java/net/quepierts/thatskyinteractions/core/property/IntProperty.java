package net.quepierts.thatskyinteractions.core.property;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import net.quepierts.thatskyinteractions.infra.animation.tween.consumer.Consumer1i;
import org.jetbrains.annotations.Contract;

@NoArgsConstructor
@AllArgsConstructor
public class IntProperty implements Consumer1i {

    private int value;

    public static IntProperty zero() {
        return new IntProperty(0);
    }

    public static IntProperty one() {
        return new IntProperty(1);
    }

    @Contract(pure = true)
    public int get() {
        return this.value;
    }

    public void set(final int value) {
        this.value = value;
    }

    @Override
    public void accept(final int value) {
        this.value = value;
    }
}
