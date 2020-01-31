package io.metadew.iesi.metadata.definition.impersonation.key;

import io.metadew.iesi.metadata.definition.key.MetadataKey;

public class ImpersonationKey extends MetadataKey {

    private String name;

    public ImpersonationKey(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
