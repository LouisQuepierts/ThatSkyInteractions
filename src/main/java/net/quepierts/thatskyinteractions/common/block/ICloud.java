package net.quepierts.thatskyinteractions.common.block;

import org.joml.Vector3i;

public interface ICloud {
    boolean isRemoved();

    Vector3i getSize();

    Vector3i getOffset();

    boolean isDirty();
}
