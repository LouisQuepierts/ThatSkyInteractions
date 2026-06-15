package net.quepierts.thatskyinteractions.feature.client.gui.component.attribute;

import org.jspecify.annotations.NonNull;

import java.util.IdentityHashMap;
import java.util.Map;

public final class AttributeHolder implements IAttributeHolder {

    private final Map<AttributeKey<?>, Object> attributes = new IdentityHashMap<>();

    @Override
    public <T> void setAttribute(
            final @NonNull AttributeKey<T>  key,
            final @NonNull T                value
    ) {
        this.attributes.put(key, value);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T getAttribute(
            final @NonNull AttributeKey<T>  key
    ) {
        return (T) this.attributes.get(key);
    }

    @Override
    public <T> boolean hasAttribute(
            final @NonNull AttributeKey<T>  key
    ) {
        return this.attributes.containsKey(key);
    }

}
