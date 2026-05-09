package net.quepierts.thatskyinteractions.infra.util;

import lombok.RequiredArgsConstructor;

import java.util.Iterator;

@RequiredArgsConstructor
public final class ArrayIterator<T> implements Iterator<T> {

    private final T[]   array;
    private int         index;

    @Override
    public boolean hasNext() {
        return this.index < this.array.length;
    }

    @Override
    public T next() {
        return this.array[this.index++];
    }

}
