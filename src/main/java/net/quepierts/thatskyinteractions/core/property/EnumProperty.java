package net.quepierts.thatskyinteractions.core.property;

import lombok.AllArgsConstructor;
import org.jetbrains.annotations.Contract;

@AllArgsConstructor
public class EnumProperty<T extends PropertyEnum<T>> {

    private T value;

    @Contract(pure = true)
    public T get() {
        return this.value;
    }

    public void set(T value) {
        this.value = value;
    }

}
