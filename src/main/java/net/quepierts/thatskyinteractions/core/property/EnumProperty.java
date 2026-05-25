package net.quepierts.thatskyinteractions.core.property;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class EnumProperty<T extends PropertyEnum<T>> {

    private T value;

    public T get() {
        return this.value;
    }

    public void set(T value) {
        this.value = value;
    }

}
