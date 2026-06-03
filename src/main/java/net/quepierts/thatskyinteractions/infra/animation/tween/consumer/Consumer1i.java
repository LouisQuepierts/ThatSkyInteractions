package net.quepierts.thatskyinteractions.infra.animation.tween.consumer;

import java.util.function.Consumer;

public interface Consumer1i extends Consumer<Integer> {

    void accept(int value);

    @Override
    default void accept(Integer value) {
        this.accept(value.intValue());
    }

}
