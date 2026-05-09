package net.quepierts.thatskyinteractions.infra.util;

import lombok.experimental.UtilityClass;

import java.util.function.Supplier;

@UtilityClass
public class ArrayUtils {

    @SuppressWarnings("unchecked")
    public static <T> T[] create(int i) {
        return (T[]) new Object[i];
    }

    public static <T> T[] create(int i, Supplier<T> supplier) {
        T[] array = create(i);
        init(array, supplier);
        return array;
    }

    public static <T> void init(T[] array, Supplier<T> supplier) {
        for (int i = 0; i < array.length; i++) {
            array[i] = supplier.get();
        }
    }

}
