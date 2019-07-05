package io.metadew.iesi.metadata.definition;

import io.metadew.iesi.metadata.definition.key.MetadataKey;

public abstract class Metadata<T extends MetadataKey> {

    private final T metadataKey;

    public Metadata(T metadataKey) {
        this.metadataKey = metadataKey;
    }

    public T getMetadataKey() {
        return metadataKey;
    }
}
