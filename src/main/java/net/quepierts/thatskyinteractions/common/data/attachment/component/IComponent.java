package net.quepierts.thatskyinteractions.common.data.attachment.component;

public interface IComponent<T extends IComponent<?>> {
    void setInfo(T other);

    void update();
}
