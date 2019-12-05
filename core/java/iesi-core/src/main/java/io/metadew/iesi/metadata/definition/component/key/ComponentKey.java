package io.metadew.iesi.metadata.definition.component.key;

import io.metadew.iesi.metadata.definition.key.MetadataKey;

public class ComponentKey extends MetadataKey {
    private String id;

    public ComponentKey(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }
}
