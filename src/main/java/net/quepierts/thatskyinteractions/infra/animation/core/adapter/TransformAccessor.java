package net.quepierts.thatskyinteractions.infra.animation.core.adapter;

public interface TransformAccessor {

    void setPosition(float x, float y, float z);

    void setEulerAngle(float x, float y, float z);

    void setQuaternion(float x, float y, float z, float w);

    void setScale(float x, float y, float z);

}
