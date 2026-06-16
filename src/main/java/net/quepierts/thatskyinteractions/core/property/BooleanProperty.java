package net.quepierts.thatskyinteractions.core.property;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.Contract;

@NoArgsConstructor
@AllArgsConstructor
public class BooleanProperty {

    private boolean value;

    @Contract(pure = true)
    public boolean get() {
        return this.value;
    }

    public void set(boolean value) {
        this.value = value;
    }

    public void toggle() {
        this.value = !this.value;
    }

}
