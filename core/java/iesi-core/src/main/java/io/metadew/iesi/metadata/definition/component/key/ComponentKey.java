package io.metadew.iesi.metadata.definition.component.key;

import io.metadew.iesi.metadata.definition.key.MetadataKey;

public class ComponentKey extends MetadataKey {
    private String id;
    private long versionNumber;

    public ComponentKey(String id, long versionNumber) {
        this.id = id;
        this.versionNumber = versionNumber;
    }

    public String getId() {
        return id;
    }

    public long getVersionNumber() {
        return versionNumber;
    }
}
