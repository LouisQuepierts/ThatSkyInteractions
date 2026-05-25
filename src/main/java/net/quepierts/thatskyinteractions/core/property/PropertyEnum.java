package net.quepierts.thatskyinteractions.core.property;

public interface PropertyEnum<T extends PropertyEnum<T>> {
    int ordinal();
    
    T value(int ordinal);
}
