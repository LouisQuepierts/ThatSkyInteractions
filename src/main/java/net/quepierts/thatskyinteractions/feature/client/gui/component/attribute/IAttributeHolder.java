package net.quepierts.thatskyinteractions.feature.client.gui.component.attribute;

import org.jspecify.annotations.NonNull;

public interface IAttributeHolder {

    <T> void setAttribute(
            @NonNull final AttributeKey<T>  key,
            @NonNull final T                value
    );

    <T> T getAttribute(
            @NonNull final AttributeKey<T>  key
    );

    <T> boolean hasAttribute(
            @NonNull final AttributeKey<T>  key
    );

}
