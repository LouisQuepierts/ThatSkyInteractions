package net.quepierts.thatskyinteractions.feature.client.gui.component.attribute;

import org.jspecify.annotations.NonNull;

public interface IAttributeHolder {

    <T> void setAttribute(
            final @NonNull AttributeKey<T>  key,
            final @NonNull T                value
    );

    <T> T getAttribute(
            final @NonNull AttributeKey<T>  key
    );

    <T> boolean hasAttribute(
            final @NonNull AttributeKey<T>  key
    );

}
