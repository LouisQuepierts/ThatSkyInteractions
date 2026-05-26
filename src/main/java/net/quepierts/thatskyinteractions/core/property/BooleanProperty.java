package net.quepierts.thatskyinteractions.core.property;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
public class BooleanProperty {

    private boolean value;

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
