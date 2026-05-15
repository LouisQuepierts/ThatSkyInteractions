package net.quepierts.thatskyinteractions.infra.util;

import lombok.experimental.UtilityClass;

import java.util.function.Supplier;

@UtilityClass
public class ArrayUtils {

    public static <T> void init(T[] array, Supplier<T> supplier) {
        for (int i = 0; i < array.length; i++) {
            array[i] = supplier.get();
        }
    }

}
