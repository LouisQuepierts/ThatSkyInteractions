package net.quepierts.thatskyinteractions.common.block.entity;

public interface IUpdateMark {
    boolean isDirty();

    void setDirty(boolean dirty);
}
